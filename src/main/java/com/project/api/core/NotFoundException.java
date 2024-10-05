package com.project.api.core;

import com.project.api.model.Note;

import java.util.List;

public class NotFoundException extends RuntimeException {
    private Note note;
    private List<Note> notes;

    public NotFoundException(String msg, Note note) {
        super(msg);
        this.note = note;
    }

    public NotFoundException(String msg, List<Note> notes) {
        super(msg);
        this.notes = notes;
    }

    public Note getNote() {
        return this.note;
    }

    public List<Note> getNotes() {
        return this.notes;
    }
}
