package com.mst.processorservice.model;

import java.time.LocalTime;
import java.util.List;

public record ActionEvent(
        long id,
        String ownerId,
        ActionType action_type,
        DayType run_on_day,
        LocalTime run_on_time,
        String message,
        String to,
        List<List<Integer>> conditions
) {

}
