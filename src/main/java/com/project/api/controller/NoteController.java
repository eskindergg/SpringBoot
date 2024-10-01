package com.project.api.controller;

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
    public Note post(@RequestBody Note note) {
        return noteService.save(note);
    }

    @PreAuthorize("hasRole('Write')")
    @PutMapping()
    public ResponseEntity<Note> put(@RequestBody Note note) {
        try {
            return new ResponseEntity<Note>(noteService.update(note), HttpStatus.OK);
        } catch (SyncConflictException ex) {
            return new ResponseEntity<Note>(ex.getNote(), HttpStatus.CONFLICT);
        }
    }

    @PreAuthorize("hasRole('Write')")
    @PutMapping("/update")
    public List<Note> Update(@RequestBody List<Note> notes) {
        return noteService.bulkUpdate(notes);
    }

    @PreAuthorize("hasRole('Write')")
    @PostMapping("/insert")
    public List<Note> insert(@RequestBody List<Note> notes) {
        return noteService.bulkInsert(notes);
    }
}
