package com.example.exam.strategies.person;

import com.example.exam.model.person.QPerson;
import com.example.exam.strategies.SearchStrategy;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("PERSON")
public class PersonSearchStrategy implements SearchStrategy {
    @Override
    public BooleanBuilder buildPredicate(Map<String, Object> criteria) {

        QPerson qPerson = QPerson.person;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (criteria.containsKey("id")) {
            Long id = Long.valueOf(String.valueOf(criteria.get("id")));
            booleanBuilder.and(qPerson.id.eq(id));
        }
        if (criteria.containsKey("name")) {
            String name = (String) criteria.get("name");
            booleanBuilder.and(qPerson.name.containsIgnoreCase(name));
        }
        if (criteria.containsKey("surname")) {
            String surname = (String) criteria.get("surname");
            booleanBuilder.and(qPerson.surname.containsIgnoreCase(surname));
        }
        if (criteria.containsKey("pesel")) {
            String pesel = (String) criteria.get("pesel");
            booleanBuilder.and(qPerson.pesel.eq(pesel));
        }
        if (criteria.containsKey("minHeight")) {
            double minHeight = Double.parseDouble(String.valueOf(criteria.get("minHeight")));
            booleanBuilder.and(qPerson.height.goe(minHeight));
        }
        if (criteria.containsKey("maxHeight")) {
            double maxHeight = Double.parseDouble(String.valueOf(criteria.get("maxHeight")));
            booleanBuilder.and(qPerson.height.loe(maxHeight));
        }
        if (criteria.containsKey("minWeight")) {
            double minWeight = Double.parseDouble(String.valueOf(criteria.get("minWeight")));
            booleanBuilder.and(qPerson.weight.goe(minWeight));
        }
        if (criteria.containsKey("maxWeight")) {
            double maxWeight = Double.parseDouble(String.valueOf(criteria.get("maxWeight")));
            booleanBuilder.and(qPerson.weight.loe(maxWeight));
        }
        if (criteria.containsKey("email")) {
            String email = (String) criteria.get("email");
            booleanBuilder.and(qPerson.email.eq(email));
        }
        return booleanBuilder;
    }

    @Override
    public JPQLQuery<?> createQuery(JPAQueryFactory queryFactory, BooleanBuilder where) {
        return queryFactory.selectFrom(QPerson.person).where(where);
    }

    @Override
    public String getType() {
        return "PERSON";
    }
}