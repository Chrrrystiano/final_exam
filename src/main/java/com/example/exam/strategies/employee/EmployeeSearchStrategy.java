package com.example.exam.strategies.employee;

import com.example.exam.model.employee.QEmployee;
import com.example.exam.strategies.person.PersonSearchStrategy;
import com.example.exam.strategies.SearchStrategy;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Component("EMPLOYEE")
public class EmployeeSearchStrategy implements SearchStrategy {

    private final PersonSearchStrategy personSearchStrategy;

    @Autowired
    public EmployeeSearchStrategy(PersonSearchStrategy personSearchStrategy) {
        this.personSearchStrategy = personSearchStrategy;
    }

    @Override
    public BooleanBuilder buildPredicate(Map<String, Object> criteria) {
        QEmployee qe = QEmployee.employee;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (criteria.containsKey("id")) {
            Long id = Long.valueOf(String.valueOf(criteria.get("id")));
            booleanBuilder.and(qe.id.eq(id));
        }
        if (criteria.containsKey("type")) {
            String type = (String) criteria.get("type");
            booleanBuilder.and(qe.type.containsIgnoreCase(type));
        }
        if (criteria.containsKey("name")) {
            String name = (String) criteria.get("name");
            booleanBuilder.and(qe.name.containsIgnoreCase(name));
        }
        if (criteria.containsKey("surname")) {
            String surname = (String) criteria.get("surname");
            booleanBuilder.and(qe.surname.containsIgnoreCase(surname));
        }
        if (criteria.containsKey("pesel")) {
            String pesel = (String) criteria.get("pesel");
            booleanBuilder.and(qe.pesel.eq(pesel));
        }
        if (criteria.containsKey("minHeight")) {
            double minHeight = Double.parseDouble((String) criteria.get("minHeight"));
            booleanBuilder.and(qe.height.goe(minHeight));
        }
        if (criteria.containsKey("maxHeight")) {
            double maxHeight = Double.parseDouble((String) criteria.get("maxHeight"));
            booleanBuilder.and(qe.height.loe(maxHeight));
        }
        if (criteria.containsKey("minWeight")) {
            double minWeight = Double.parseDouble((String) criteria.get("minWeight"));
            booleanBuilder.and(qe.weight.goe(minWeight));
        }
        if (criteria.containsKey("maxWeight")) {
            double maxWeight = Double.parseDouble((String) criteria.get("maxWeight"));
            booleanBuilder.and(qe.weight.loe(maxWeight));
        }
        if (criteria.containsKey("email")) {
            String email = (String) criteria.get("email");
            booleanBuilder.and(qe.email.eq(email));
        }
        if (criteria.containsKey("minSalary")) {
            BigDecimal minSalary = new BigDecimal(String.valueOf(criteria.get("minSalary")));
            booleanBuilder.and(qe.currentSalary.goe(minSalary));
        }
        if (criteria.containsKey("maxSalary")) {
            BigDecimal maxSalary = new BigDecimal(String.valueOf(criteria.get("maxSalary")));
            booleanBuilder.and(qe.currentSalary.loe(maxSalary));
        }
        if (criteria.containsKey("currentPosition")) {
            String currentPosition = (String) criteria.get("currentPosition");
            booleanBuilder.and(qe.currentPosition.containsIgnoreCase(currentPosition));
        }
        if (criteria.containsKey("startDateFrom")) {
            LocalDate startDateFrom = LocalDate.parse(String.valueOf(criteria.get("startDateFrom")));
            booleanBuilder.and(qe.currentPositionStartDate.goe(startDateFrom));
        }
        if (criteria.containsKey("startDateTo")) {
            LocalDate startDateTo = LocalDate.parse(String.valueOf(criteria.get("startDateTo")));
            booleanBuilder.and(qe.currentPositionStartDate.loe(startDateTo));
        }
        return booleanBuilder;
    }

    @Override
    public JPQLQuery<?> createQuery(JPAQueryFactory queryFactory, BooleanBuilder where) {
        return queryFactory.selectFrom(QEmployee.employee).where(where);
    }


    @Override
    public String getType() {
        return "EMPLOYEE";
    }
}
