package com.mst.processorservice.condition;


import com.mst.processorservice.client.LoaderClient;
import com.mst.processorservice.client.MetricClient;
import com.mst.processorservice.model.MetricEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ConditionEvaluator {

    @Autowired
    LoaderClient loaderClient;

    @Autowired
    MetricClient metricClient;


    public boolean evaluateCondition(List<List<Integer>> conditions) {


        List<List<Integer>>  conditionsToCheck=simplify(conditions);

        for (List<Integer> group : conditionsToCheck) {
             boolean groupPassed=true;

             for(Integer metricId:group)
             {
                 MetricEvent currMetric=metricClient.getMetricById(Long.valueOf(metricId));

                 boolean metricPassed=loaderClient.actionCheckIfThresholdMet
                         (currMetric.label().toString(),currMetric.threshold(),currMetric.TimeFrame());

                 if (!metricPassed){
                     groupPassed=false;
                     break;
                 }
             }
             if (groupPassed){
                 return true;
             }
        }
        return false;
    }


    private List<List<Integer>> simplify(List<List<Integer>> conditions)
    {
        List<Set<Integer>> sets = new ArrayList<>();

        for (List<Integer> group : conditions) {
            sets.add(new HashSet<>(group));
        }
        List<Set<Integer>> simplified = new ArrayList<>();

        for (int i = 0; i < sets.size(); i++)
        {
            Set<Integer> current = sets.get(i);
            boolean redundant = false;

            for (int j = 0; j < sets.size(); j++) {
                if (i == j) continue;

                Set<Integer> other = sets.get(j);

                // If current is a strict superset of another, it's redundant
                if (current.size() > other.size() && current.containsAll(other)) {
                    redundant = true;
                    break;
                }
            }

            if (!redundant) {
                simplified.add(current);
            }
        }

        // Remove duplicates and convert back to sorted lists
        Set<Set<Integer>> unique = new HashSet<>(simplified);

        List<List<Integer>> result = new ArrayList<>();
        for (Set<Integer> s : unique) {
            List<Integer> list = new ArrayList<>(s);
            result.add(list);
        }
        return result;
    }
}

