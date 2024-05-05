package com.example.exam.strategies.pensioner;

import com.example.exam.model.pensioner.QPensioner;
import com.example.exam.strategies.person.PersonSearchStrategy;
import com.example.exam.strategies.SearchStrategy;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component("PENSIONER")
@RequiredArgsConstructor
public class PensionerSearchStrategy implements SearchStrategy {
    private final PersonSearchStrategy personSearchStrategy;

    @Override
    public BooleanBuilder buildPredicate(Map<String, Object> criteria) {
        QPensioner qp = QPensioner.pensioner;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (criteria.containsKey("id")) {
            Long id = Long.valueOf(String.valueOf(criteria.get("id")));
            booleanBuilder.and(qp.id.eq(id));
        }
        if (criteria.containsKey("type")) {
            String type = (String) criteria.get("type");
            booleanBuilder.and(qp.type.containsIgnoreCase(type));
        }
        if (criteria.containsKey("name")) {
            String name = (String) criteria.get("name");
            booleanBuilder.and(qp.name.containsIgnoreCase(name));
        }
        if (criteria.containsKey("surname")) {
            String surname = (String) criteria.get("surname");
            booleanBuilder.and(qp.surname.containsIgnoreCase(surname));
        }
        if (criteria.containsKey("pesel")) {
            String pesel = (String) criteria.get("pesel");
            booleanBuilder.and(qp.pesel.eq(pesel));
        }
        if (criteria.containsKey("minHeight")) {
            double minHeight = Double.parseDouble((String) criteria.get("minHeight"));
            booleanBuilder.and(qp.height.goe(minHeight));
        }
        if (criteria.containsKey("maxHeight")) {
            double maxHeight = Double.parseDouble((String) criteria.get("maxHeight"));
            booleanBuilder.and(qp.height.loe(maxHeight));
        }
        if (criteria.containsKey("minWeight")) {
            double minWeight = Double.parseDouble((String) criteria.get("minWeight"));
            booleanBuilder.and(qp.weight.goe(minWeight));
        }
        if (criteria.containsKey("maxWeight")) {
            double maxWeight = Double.parseDouble((String) criteria.get("maxWeight"));
            booleanBuilder.and(qp.weight.loe(maxWeight));
        }
        if (criteria.containsKey("email")) {
            String email = (String) criteria.get("email");
            booleanBuilder.and(qp.email.eq(email));
        }
        if (criteria.containsKey("minPensionAmount")) {
            BigDecimal minPensionAmount = new BigDecimal((String) criteria.get("minPensionAmount"));
            booleanBuilder.and(qp.pensionAmount.goe(minPensionAmount));
        }
        if (criteria.containsKey("maxPensionAmount")) {
            BigDecimal maxPensionAmount = new BigDecimal((String) criteria.get("maxPensionAmount"));
            booleanBuilder.and(qp.pensionAmount.loe(maxPensionAmount));
        }
        if (criteria.containsKey("minYearsOfWork")) {
            int minYearsOfWork = Integer.parseInt(String.valueOf(criteria.get("minYearsOfWork")));
            booleanBuilder.and(qp.yearsOfWork.goe(minYearsOfWork));
        }
        if (criteria.containsKey("maxYearsOfWork")) {
            int maxYearsOfWork = Integer.parseInt(String.valueOf(criteria.get("maxYearsOfWork")));
            booleanBuilder.and(qp.yearsOfWork.loe(maxYearsOfWork));
        }
        return booleanBuilder;
    }

    @Override
    public JPQLQuery<?> createQuery(JPAQueryFactory queryFactory, BooleanBuilder where) {
        return queryFactory.selectFrom(QPensioner.pensioner).where(where);
    }

    @Override
    public Object convertStringValue(String key, String value) {
        try {
            return switch (key) {
                case "id" -> Long.parseLong(value);
                case "yearsOfWork" -> Integer.parseInt(value);
                case "height", "weight" -> Double.parseDouble(value);
                case "pensionAmount" -> new BigDecimal(value);
                default -> value;
            };
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unable to convert value for key:" + key + " to the appropriate type " + e);
        }
    }

    @Override
    public String getType() {
        return "PENSIONER";
    }


}
