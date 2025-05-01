package com.project.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.api.core.NotFoundException;
import com.project.api.core.SyncConflictException;
import com.project.api.model.Note;
import com.project.api.service.AdminNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PutMapping()
    public ResponseEntity<List<Note>> put(@RequestBody Note note) {
        try {
            return new ResponseEntity<List<Note>>(adminNoteService.update(note), HttpStatus.OK);
        } catch (SyncConflictException ex) {
            return new ResponseEntity<List<Note>>(ex.getNotes(), HttpStatus.CONFLICT);
        } catch (NotFoundException ex) {
            return new ResponseEntity<List<Note>>(ex.getNotes(), HttpStatus.NOT_FOUND);
        }
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

    @GetMapping("/users")
    @PreAuthorize("hasRole('Admin')")
    public List<Object> getUsers() {
        return adminNoteService.getUsers();
    }

}
