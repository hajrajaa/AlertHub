package com.mst.evaluationservice.client;

import com.mst.evaluationservice.enums.Label;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name="loader-service")
public interface LoaderFeignClient {

    @GetMapping("/most-label")
    Long getDeveloperWithMostLabelOccurrences(@RequestParam("label") Label labelName,
                                              @RequestParam("since") int since);

    @GetMapping("/{developer_id}/label-aggregate")
    Map<String, Long> developerLabelAggregate(@PathVariable Long developer_id,
                                                 @RequestParam int since);

    @GetMapping("/{developer_id}/task-amount")
    int developerTaskAmount(@PathVariable Long developer_id,
                            @RequestParam int since);
}

