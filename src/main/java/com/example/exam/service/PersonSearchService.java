package com.example.exam.service;

import com.example.exam.model.person.Person;
import com.example.exam.strategies.DefaultSearchStrategy;
import com.example.exam.strategies.SearchStrategy;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PersonSearchService {
    private final JPAQueryFactory queryFactory;
    private Map<String, SearchStrategy> searchStrategies;


    @Autowired
    public PersonSearchService(JPAQueryFactory queryFactory, List<SearchStrategy> strategies) {
        this.queryFactory = queryFactory;
        this.searchStrategies = strategies.stream().collect(Collectors.toMap(SearchStrategy::getType, Function.identity()));
    }

    public Page<Person> searchPeopleWithCriteria(String type, Map<String, String> allParams, Pageable pageable) {
        allParams.remove("type");
        SearchStrategy strategy = searchStrategies.getOrDefault(type, new DefaultSearchStrategy());


        Map<String, Object> criteria = allParams.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> convertStringValue(e.getKey(), e.getValue(), type)));


        BooleanBuilder where = strategy.buildPredicate(criteria);
        JPQLQuery<?> query = strategy.createQuery(queryFactory, where);

        long total = query.fetchCount();
        List<Person> people = (List<Person>) query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(people, pageable, total);
    }

    private Object convertStringValue(String key, String value, String type) {
        SearchStrategy strategy = searchStrategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy available for type: " + type);
        }
        return strategy.convertStringValue(key, value);
    }

}