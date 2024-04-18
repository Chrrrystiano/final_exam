package com.example.exam.strategies.student;

import com.example.exam.model.student.QStudent;
import com.example.exam.strategies.person.PersonSearchStrategy;
import com.example.exam.strategies.SearchStrategy;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component("STUDENT")
public class StudentSearchStrategy implements SearchStrategy {
    private final PersonSearchStrategy personSearchStrategy;

    @Autowired
    public StudentSearchStrategy(PersonSearchStrategy personSearchStrategy) {
        this.personSearchStrategy = personSearchStrategy;
    }


    @Override
    public BooleanBuilder buildPredicate(Map<String, Object> criteria) {
        QStudent qs = QStudent.student;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (criteria.containsKey("id")) {
            Long id = Long.valueOf(String.valueOf(criteria.get("id")));
            booleanBuilder.and(qs.id.eq(id));
        }
        if (criteria.containsKey("type")) {
            String type = (String) criteria.get("type");
            booleanBuilder.and(qs.type.containsIgnoreCase(type));
        }
        if (criteria.containsKey("name")) {
            String name = (String) criteria.get("name");
            booleanBuilder.and(qs.name.containsIgnoreCase(name));
        }
        if (criteria.containsKey("surname")) {
            String surname = (String) criteria.get("surname");
            booleanBuilder.and(qs.surname.containsIgnoreCase(surname));
        }
        if (criteria.containsKey("pesel")) {
            String pesel = (String) criteria.get("pesel");
            booleanBuilder.and(qs.pesel.eq(pesel));
        }
        if (criteria.containsKey("minHeight")) {
            double minHeight = Double.parseDouble((String) criteria.get("minHeight"));
            booleanBuilder.and(qs.height.goe(minHeight));
        }
        if (criteria.containsKey("maxHeight")) {
            double maxHeight = Double.parseDouble((String) criteria.get("maxHeight"));
            booleanBuilder.and(qs.height.loe(maxHeight));
        }
        if (criteria.containsKey("minWeight")) {
            double minWeight = Double.parseDouble((String) criteria.get("minWeight"));
            booleanBuilder.and(qs.weight.goe(minWeight));
        }
        if (criteria.containsKey("maxWeight")) {
            double maxWeight = Double.parseDouble((String) criteria.get("maxWeight"));
            booleanBuilder.and(qs.weight.loe(maxWeight));
        }
        if (criteria.containsKey("email")) {
            String email = (String) criteria.get("email");
            booleanBuilder.and(qs.email.eq(email));
        }
        if (criteria.containsKey("universityName")) {
            String universityName = (String) criteria.get("universityName");
            booleanBuilder.and(qs.universityName.containsIgnoreCase(universityName));
        }
        if (criteria.containsKey("minYearOfStudy")) {
            int minYearOfStudy = Integer.parseInt(String.valueOf(criteria.get("minYearOfStudy")));
            booleanBuilder.and(qs.yearOfStudy.goe(minYearOfStudy));
        }
        if (criteria.containsKey("maxYearOfStudy")) {
            int maxYearOfStudy = Integer.parseInt(String.valueOf(criteria.get("maxYearOfStudy")));
            booleanBuilder.and(qs.yearOfStudy.loe(maxYearOfStudy));
        }
        if (criteria.containsKey("fieldOfStudy")) {
            String fieldOfStudy = (String) criteria.get("fieldOfStudy");
            booleanBuilder.and(qs.fieldOfStudy.containsIgnoreCase(fieldOfStudy));
        }
        if (criteria.containsKey("minScholarshipAmount")) {
            BigDecimal minScholarshipAmount = new BigDecimal(String.valueOf(criteria.get("minScholarshipAmount")));
            booleanBuilder.and(qs.scholarshipAmount.goe(minScholarshipAmount));
        }
        if (criteria.containsKey("maxScholarshipAmount")) {
            BigDecimal maxScholarshipAmount = new BigDecimal(String.valueOf(criteria.get("maxScholarshipAmount")));
            booleanBuilder.and(qs.scholarshipAmount.loe(maxScholarshipAmount));
        }
        return booleanBuilder;
    }

    @Override
    public JPQLQuery<?> createQuery(JPAQueryFactory queryFactory, BooleanBuilder where) {
        return queryFactory.selectFrom(QStudent.student).where(where);
    }

    @Override
    public Object convertStringValue(String key, String value) {
        try {
            return switch (key) {
                case "id" -> Long.parseLong(value);
                case "yearOfStudy" -> Integer.parseInt(value);
                case "height", "weight" -> Double.parseDouble(value);
                case "scholarshipAmount" -> new BigDecimal(value);
                default -> value;
            };
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unable to convert value for key:" + key + " to the appropriate type " + e);
        }
    }

    @Override
    public String getType() {
        return "STUDENT";
    }
}
