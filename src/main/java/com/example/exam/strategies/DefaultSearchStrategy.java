package com.example.exam.strategies;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DefaultSearchStrategy implements SearchStrategy {

    @Override
    public BooleanBuilder buildPredicate(Map<String, Object> criteria) {
        return new BooleanBuilder();
    }

    @Override
    public JPQLQuery<?> createQuery(JPAQueryFactory queryFactory, BooleanBuilder where) {
        return null;
    }


    @Override
    public Object convertStringValue(String key, String value) {
        return null;
    }

    @Override
    public String getType() {
        return "default";
    }
}
