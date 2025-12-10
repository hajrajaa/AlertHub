package com.mst.processorservice.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MetricEvent(@NotBlank
                          @NotNull
                          String name,
                          MetricLabel label,
                          int threshold,
                          @NotNull
                          @Min(1)
                          @Max(24)
                          int TimeFrame)
{}

