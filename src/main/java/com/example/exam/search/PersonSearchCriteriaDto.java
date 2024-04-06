package com.example.exam.search;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PersonSearchCriteriaDto {
    private String type;
    private Map<String, Object> criteria = new HashMap<>();
    private int pageNumber;
    private int pageSize;
    private String sortBy;
    private String sortDirection;
}
