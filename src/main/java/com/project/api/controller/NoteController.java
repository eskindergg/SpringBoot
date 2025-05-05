package com.project.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.api.core.NotFoundException;
import com.project.api.core.SyncConflictException;
import com.project.api.model.Note;
import com.project.api.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping()
    @PreAuthorize("hasRole('Read')")
    public List<Note> get() {
        return noteService.getNotes();
    }

    @PreAuthorize("hasRole('Write')")
    @PostMapping()
    public ResponseEntity<Note> post(@RequestBody Note note) {
        return new ResponseEntity<>(noteService.save(note), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('Write')")
    @PutMapping()
    public ResponseEntity<Note> put(@RequestBody Note note) {
        try {
            return new ResponseEntity<Note>(noteService.update(note), HttpStatus.OK);
        } catch (SyncConflictException ex) {
            return new ResponseEntity<Note>(ex.getNote(), HttpStatus.CONFLICT);
        } catch (NotFoundException ex) {
            return new ResponseEntity<Note>(ex.getNote(), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('Write')")
    @PutMapping("/update")
    public ResponseEntity<List<Note>> Update(@RequestBody List<Note> notes) {
        try {
            return new ResponseEntity<>(noteService.upsert(notes), HttpStatus.OK);
        } catch (SyncConflictException ex) {
            return new ResponseEntity<>(ex.getNotes(), HttpStatus.CONFLICT);
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(ex.getNotes(), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('Write')")
    @PostMapping("/insert")
    public ResponseEntity<List<Note>> insert(@RequestBody List<Note> notes) throws JsonProcessingException {
        return new ResponseEntity<List<Note>>(noteService.bulkInsert(notes), HttpStatus.CREATED);
    }
}
