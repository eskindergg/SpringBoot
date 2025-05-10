package com.project.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.api.core.NotFoundException;
import com.project.api.core.SyncConflictException;
import com.project.api.model.Note;
import com.project.api.model.User;
import com.project.api.service.AdminNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/notes")
public class AdminNoteController {

    @Autowired
    private AdminNoteService adminNoteService;

    @GetMapping()
    @PreAuthorize("hasRole('Admin')")
    public List<Note> get() {
        return adminNoteService.getNotes();
    }

    @PreAuthorize("hasRole('Admin')")
    @PostMapping()
    public ResponseEntity<Note> post(@RequestBody Note note) {
        return new ResponseEntity<>(adminNoteService.save(note), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('Admin')")
    @PutMapping("/update")
    public ResponseEntity<List<Note>> Update(@RequestBody List<Note> notes) throws JsonProcessingException {
        try {
            return new ResponseEntity<>(adminNoteService.bulkUpdate(notes), HttpStatus.OK);
        } catch (SyncConflictException ex) {
            return new ResponseEntity<>(ex.getNotes(), HttpStatus.CONFLICT);
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(ex.getNotes(), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('Admin')")
    @PostMapping("/insert")
    public ResponseEntity<List<Note>> insert(@RequestBody List<Note> notes) {
        return new ResponseEntity<>(adminNoteService.bulkInsert(notes), HttpStatus.CREATED);
    }

    @GetMapping("/usersinfo")
    @PreAuthorize("hasRole('Admin')")
    public List<Map<String, Object>> getUsers() {
        return adminNoteService.getUsersNotesCount();
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(adminNoteService.getUsers(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('Admin')")
    @PostMapping("/users")
    public ResponseEntity<List<User>> upsert(@RequestBody List<User> users) {
        return new ResponseEntity<>(adminNoteService.updateUsers(users), HttpStatus.OK);
    }

}
