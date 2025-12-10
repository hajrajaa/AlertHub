package com.mst.evaluationservice.controller;

import com.mst.evaluationservice.enums.Label;
import com.mst.evaluationservice.service.EvaluationService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/evaluation/developer")
public class EvaluationController {

    @Autowired
    private EvaluationService es;

    // url="/evaluation/developer/most-label?label={labelName}&since={day}"
    @GetMapping("/most-label")
    public ResponseEntity<?> getDeveloperWithMostLabelOccurrences(@RequestParam("label") @NotNull Label labelName,
                                                                  @RequestParam("since") int since){
        String res = es.getDeveloperWithMostLabelOccurrences(labelName,since);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // url="/evaluation/developer/{developer_id}/label-aggregate?since={day}"
    @GetMapping("/{developer_id}/label-aggregate")
    public ResponseEntity<?> developerLabelAggregate(@PathVariable Long developer_id,
                                                     @RequestParam int since){
        String res = es.developerLabelAggregate(developer_id,since);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // url="/evaluation/developer/{developer_id}/task-amount?since={day}"
    @GetMapping("/{developer_id}/task-amount")
    public ResponseEntity<?> developerTaskAmount(@PathVariable Long developer_id,
                                                 @RequestParam int since){
        String res = es.developerTaskAmount(developer_id, since);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}