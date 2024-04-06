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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
                .collect(Collectors.toMap(Map.Entry::getKey, e -> convertStringValue(e.getKey(), e.getValue())));
        BooleanBuilder where = strategy.buildPredicate(criteria);

        JPQLQuery<?> query = strategy.createQuery(queryFactory, where);

        long total = query.fetchCount();

        List<Person> people = (List<Person>) query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(people, pageable, total);
    }

    private Object convertStringValue(String key, String value) {
        try {
            switch (key) {
                case "id":
                    return Long.parseLong(value);
                case "yearsOfWork":
                case "yearOfStudy":
                    return Integer.parseInt(value);
                case "height":
                case "weight":
                    return Double.parseDouble(value);
                case "pensionAmount":
                case "scholarshipAmount":
                case "currentSalary":
                    return new BigDecimal(value);
                case "startDateFrom":
                case "startDateTo":
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    return LocalDate.parse(value, formatter);
                default:
                    return value;
            }
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new IllegalArgumentException("Unable to convert value for key:" + key + " to the appropriate type " + e);
        }
    }

}