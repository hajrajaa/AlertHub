package com.mst.evaluationservice.service;

import com.mst.evaluationservice.client.LoaderFeignClient;
import com.mst.evaluationservice.dto.NotificationDTO;
import com.mst.evaluationservice.enums.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EvaluationService {

    @Autowired
    private LoaderFeignClient lfc;
    @Autowired
    KafkaService kafkaService;

    public String getDeveloperWithMostLabelOccurrences(Label labelName, int since){
        Long res;
        res = lfc.getDeveloperWithMostLabelOccurrences(labelName,since);
        String resMsg = "Developer with ID " + res + " has the highest number of tasks for label '" + labelName +
                "' in the last " + since + " days.";
        NotificationDTO msg = new NotificationDTO((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                resMsg);
        kafkaService.publishMsg(msg);
        return resMsg;
    }

    public String developerLabelAggregate(Long developer_id, int since){
        Map<String,Long> res;
        res = lfc.developerLabelAggregate(developer_id,since);
        String resMsg = "Developer with ID " + developer_id.toString() + " has the following task counts by label in the last " +
                since + " days: \n" + res.toString();
        NotificationDTO msg = new NotificationDTO((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                resMsg);
        kafkaService.publishMsg(msg);

        return resMsg;
    }

    public String developerTaskAmount(Long developer_id, int since){
        int res;
        res = lfc.developerTaskAmount(developer_id,since);
        String resMsg = "Developer with ID " + developer_id.toString() + " has been assigned " + res + " tasks in the last " +
                since +" days.";
        NotificationDTO msg = new NotificationDTO((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                resMsg);
        kafkaService.publishMsg(msg);

        return resMsg;
    }
}

