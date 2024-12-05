package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.model.Category;
import ru.practicum.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor {

    List<Event> findByCategory(Category category);

    List<Event> findAllByIdIn(List<Long> eventIds);
}