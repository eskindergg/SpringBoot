package com.project.api.controller;

import com.project.api.auth.CurrentAuthContext;
import com.project.api.model.Event;
import com.project.api.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping()
    public List<Event> get() {
        return eventService.getEvents();
    }

    @PostMapping()
    public Event post(@RequestBody Event event) {
        return eventService.save(event);
    }

    @PutMapping()
    public Event put(@RequestBody Event event) {
        return eventService.update(event);
    }

    @PutMapping("/toggle")
    public Event toggle(@RequestBody Event event) {
        event.setComplete(!event.getComplete());
        event.setUserId(CurrentAuthContext.getUserId());
        return eventService.update(event);
    }

    @DeleteMapping("/{id}")
    public Event delete(@PathVariable UUID id) {
        return eventService.delete(id);
    }

    @DeleteMapping()
    public List<Event> delete(@RequestBody List<Event> events) {
        return events;
    }

}
