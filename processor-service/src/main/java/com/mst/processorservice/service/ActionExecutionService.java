package com.mst.processorservice.service;

import com.mst.processorservice.client.MetricClient;
import com.mst.processorservice.condition.ConditionEvaluator;
import com.mst.processorservice.model.ActionEvent;
import com.mst.processorservice.model.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ActionExecutionService {

    @Autowired
    ConditionEvaluator conditionEvaluator;

    @Autowired
    KafkaNotificationProducer  notificationProducer;

    @Autowired
    MetricClient metricClient;


    public void executeAction(ActionEvent actionEvent) {

        // check if the metric ids is valid
        List<Integer> metricIds = extractMetricIds(actionEvent.conditions());

        boolean preCheck=metricClient.evaluateMetricsIds(metricIds.stream().map(Long::valueOf).toList());

        if (!preCheck) {
            return;
        }

        boolean passed=conditionEvaluator.evaluateCondition(actionEvent.conditions());

        if(!passed){
            return;
        }
        if (actionEvent.action_type()==ActionType.EMAIL)
        {
            notificationProducer.sendEmailNotification(actionEvent);
        }
        else if (actionEvent.action_type()==ActionType.SMS)
        {
            notificationProducer.sendSmsNotification(actionEvent);
        }
    }

    private List<Integer> extractMetricIds(List<List<Integer>> conditions) {

        return conditions.stream()
                .flatMap(List::stream)
                .distinct()
                .toList();
    }


}
