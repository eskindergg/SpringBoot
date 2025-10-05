package com.project.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.api.auth.CurrentAuthContext;
import com.project.api.model.Event;
import com.project.api.service.EventService;
import com.project.api.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    @Autowired
    private EventService eventService;

    @Autowired
    private MovieService movieService;

    @GetMapping()
    public List<Map<String, Object>> get() {
        return movieService.getUserMovies();
    }

    @PostMapping()
    public Event post(@RequestBody Event event) {
        return eventService.save(event);
    }

//    @PutMapping()
//    public List<Object> put(@RequestBody List<Object> movies) throws JsonProcessingException {
//        return movieService.bulkUpdate(movies);
//    }

    @PutMapping("/upsert")
    public List<Map<String, Object>> upsert(@RequestBody String movies) throws JsonProcessingException {
        return movieService.movieBulkUpsert(movies);
    }

    @PostMapping("/populate")
    public List<Map<String, Object>> populate(@RequestBody String movies) throws JsonProcessingException {
        return movieService.populateMovies(movies);
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
