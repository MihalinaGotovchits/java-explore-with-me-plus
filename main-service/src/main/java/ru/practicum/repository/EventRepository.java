package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e " +
            "FROM Event e " +
            "LEFT JOIN Request r ON r.event.id = e.id AND r.status = 'confirmed' " +
            "WHERE (e.annotation ILIKE :text OR e.description ILIKE :text) " +
            "AND e.paid = :paid " +
            "AND e.eventDate >= :rangeStart AND e.eventDate <= :rangeEnd " +
            "AND e.category.id IN :categories " +
            "GROUP BY e.id " +
            "HAVING (:onlyAvailable = FALSE OR e.participantLimit - COUNT(r.id) > 0) ")
    List<Event> findAllByTextAndCategoryInRangeOnlyAvailable(String text,
                                                             List<Integer> categories,
                                                             Boolean paid,
                                                             String rangeStart,
                                                             String rangeEnd,
                                                             Boolean onlyAvailable,
                                                             Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "LEFT JOIN Request r ON r.event.id = e.id AND r.status = 'confirmed' " +
            "WHERE (LOWER(e.annotation) LIKE LOWER(:text) OR LOWER(e.description) LIKE LOWER(:text)) " +
            "AND e.paid = :paid " +
            "AND e.eventDate > :rangeStart " +
            "AND e.category.id IN :categories " +
            "GROUP BY e.id " +
            "HAVING (:onlyAvailable = FALSE OR e.participantLimit - COUNT(r.id) > 0) ")
    List<Event> findAllByTextAndCategoryOnlyAvailable(String text,
                                                      List<Integer> categories,
                                                      Boolean paid,
                                                      String rangeStart,
                                                      Boolean onlyAvailable,
                                                      Pageable pageable);
}
