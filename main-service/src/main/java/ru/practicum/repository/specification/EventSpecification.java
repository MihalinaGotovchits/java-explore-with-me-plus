package ru.practicum.repository.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.dto.event.EventParam;
import ru.practicum.model.Event;

import java.util.ArrayList;
import java.util.List;

public class EventSpecification {

    public static Specification<Event> byEventParam(EventParam params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Поиск по тексту
            if (params.getText() != null && !params.getText().isEmpty()) {
                Predicate textPredicate = cb.or(
                        cb.like(cb.lower(root.get("annotation")), "%" + params.getText().toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("description")), "%" + params.getText().toLowerCase() + "%")
                );
                predicates.add(textPredicate);
            }

            // Фильтр по категориям
            if (params.getCategories() != null && !params.getCategories().isEmpty()) {
                predicates.add(root.get("category").get("id").in(params.getCategories()));
            }

            // Фильтр по платности
            if (params.getPaid() != null) {
                predicates.add(cb.equal(root.get("paid"), params.getPaid()));
            }

            // Диапазон дат
            if (params.getRangeStart() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), params.getRangeStart()));
            }
            if (params.getRangeEnd() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), params.getRangeEnd()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
