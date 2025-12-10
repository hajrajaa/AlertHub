package com.mst.processorservice.service;


import com.mst.processorservice.model.ActionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KafkaNotificationProducer {

    private static final String EMAIL = "email";
    private static final String SMS = "sms";


    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEmailNotification(ActionEvent event) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("to",event.to());
        payload.put("message",event.message());
        kafkaTemplate.send(EMAIL, payload);

    }
    public void sendSmsNotification(ActionEvent event) {


        Map<String, Object> payload = new HashMap<>();
        payload.put("to",event.to());
        payload.put("message",event.message());

        kafkaTemplate.send(SMS, payload);
    }



}
