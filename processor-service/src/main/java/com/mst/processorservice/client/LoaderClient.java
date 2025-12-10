package com.mst.processorservice.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "loader-service", url = "${services.loader.url}")
public interface LoaderClient {

    @GetMapping("/Processor/check-threshold")
    Boolean actionCheckIfThresholdMet(@RequestParam("label") String label,
                                      @RequestParam("threshold") int threshold,
                                      @RequestParam("timeFrameHours") int timeFrameHours);
}
