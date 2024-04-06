package com.example.exam.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class SearchCriteria {
    private Map<String, Object> criteria;
    private String type;
    public SearchCriteria(Map<String, Object> criteria) {
        this.criteria = criteria;
    }
}
