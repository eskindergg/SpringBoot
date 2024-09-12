package com.project.api.service;

import com.project.api.auth.CurrentAuthContext;
import com.project.api.model.Event;
import com.project.api.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    @Autowired
    EventRepository eventRepository;

    public List<Event> getEvents() {
        return eventRepository.getEvents(CurrentAuthContext.getUserId());
    }
}
