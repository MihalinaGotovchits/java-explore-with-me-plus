package ru.practicum.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.model.Category;
import ru.practicum.model.Event;

import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    List<Event> findByCategory(Category category);

    List<Event> findAllByIdIn(List<Long> eventIds);
}
