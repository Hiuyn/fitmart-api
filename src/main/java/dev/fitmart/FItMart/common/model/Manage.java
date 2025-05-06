package dev.fitmart.FItMart.common.model;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class Manage<T> {
    @Valid
    private List<T> created;

    @Valid
    private List<T> updated;

    private List<String> deleted;
}
