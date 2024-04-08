package com.example.exam.strategies;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.Map;

public interface SearchStrategy {
    BooleanBuilder buildPredicate(Map<String, Object> criteria);

    JPQLQuery<?> createQuery(JPAQueryFactory queryFactory, BooleanBuilder where);


    String getType();

}
