package com.project.api.repository;

import com.project.api.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query("select e from Event e where e.userId = ?1")
    List<Event> getEvents(UUID userId);
}
