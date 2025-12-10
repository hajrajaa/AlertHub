package com.mst.evaluationservice;

import com.mst.evaluationservice.service.KafkaService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableFeignClients
public class EvaluationServiceApplication {

	public static void main(String[] args) {

        ConfigurableApplicationContext ctx = SpringApplication.run(EvaluationServiceApplication.class, args);
//        KafkaService ks = ctx.getBean(KafkaService.class);
//        ks.publishMsg("Hello World!");
	}

}
