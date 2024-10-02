package com.project.api.core;

import com.project.api.model.Note;

import java.util.List;

public class SyncConflictException extends RuntimeException {
    private Note note;
    private List<Note> notes;

    public SyncConflictException(String msg, Note note) {
        super(msg);
        this.note = note;
    }

    public SyncConflictException(String msg, List<Note> notes) {
        super(msg);
        this.notes = notes;
    }

    public Note getNote() {
        return this.note;
    }

    public List<Note>getNotes() {
        return this.notes;
    }
}
