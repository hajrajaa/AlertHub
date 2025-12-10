package com.mst.processorservice.client;

import com.mst.processorservice.model.MetricEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = "metric-service", url = "${services.metric.url}")
public interface MetricClient {

    @GetMapping("/metrics/checkIf")
    Boolean evaluateMetricsIds(@RequestParam("metricsIds)") List<Long> metricIds);

    @GetMapping("metrics/{id}")
    MetricEvent getMetricById(@PathVariable("id") Long id);


}
