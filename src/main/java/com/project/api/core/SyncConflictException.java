package com.project.api.core;

import com.project.api.model.Note;

public class SyncConflictException extends RuntimeException {
    private Note note;

    public SyncConflictException(String msg, Note note) {
        super(msg);
        this.note = note;
    }

    public Note getNote() {
        return this.note;
    }
}
