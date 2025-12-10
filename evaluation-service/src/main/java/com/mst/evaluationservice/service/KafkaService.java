package com.mst.evaluationservice.service;

import com.mst.evaluationservice.dto.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    @Autowired
    private KafkaTemplate<String,NotificationDTO> kafkaTemplate;

    public void publishMsg(NotificationDTO msg){
        kafkaTemplate.send("email",msg);
    }
}
