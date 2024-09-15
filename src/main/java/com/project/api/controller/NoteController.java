package com.project.api.controller;

import com.project.api.model.Note;
import com.project.api.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping()
    public List<Note> get() {
        return noteService.getNotes();
    }

    @PostMapping()
    public Note post(@RequestBody Note note) {
        return noteService.save(note);
    }

    @PutMapping()
    public Note put(@RequestBody Note note) {
        return noteService.update(note);
    }

    @PutMapping("/update")
    public List<Note> Update(@RequestBody List<Note> notes) {
        return noteService.bulkUpdate(notes);
    }

    @PostMapping("/insert")
    public List<Note> insert(@RequestBody List<Note> notes) {
        return noteService.bulkInsert(notes);
    }
}
