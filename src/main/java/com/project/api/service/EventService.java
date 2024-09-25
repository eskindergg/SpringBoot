package com.project.api.service;

import com.project.api.auth.CurrentAuthContext;
import com.project.api.model.Event;
import com.project.api.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EventService {
    @Autowired
    EventRepository eventRepository;

    public List<Event> getEvents() {
        return eventRepository.getEvents(CurrentAuthContext.getUserId());
    }

    public Event save(Event event) {
        event.setUserId(CurrentAuthContext.getUserId());
        return this.eventRepository.save(event);
    }

    public Event update(Event event) {
        Event fetchEvent = eventRepository.getEventById(CurrentAuthContext.getUserId(), event.getId());

        if (fetchEvent != null) {
            return eventRepository.saveAndFlush(event);
        }

        return event;
    }

    public Event delete(UUID id) {
        Event fetchEvent = eventRepository.getEventById(CurrentAuthContext.getUserId(), id);

        if (fetchEvent != null)
            eventRepository.delete(fetchEvent);

        return fetchEvent;
    }
}
