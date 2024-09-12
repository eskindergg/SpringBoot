package com.project.api.service;

import com.project.api.auth.CurrentAuthContext;
import com.project.api.model.Note;
import com.project.api.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    public List<Note> getNotes() {
        return this.noteRepository.getNotes(CurrentAuthContext.getUserId());
    }

    public Note save(Note note) {
        note.setOwner(CurrentAuthContext.getName());
        note.setUserId(CurrentAuthContext.getUserId());
        return this.noteRepository.save(note);
    }


    public Note update(Note note) {
        Note fetchNote = noteRepository.getNote(note.getId(), CurrentAuthContext.getUserId());

        if (fetchNote != null) {
            return noteRepository.saveAndFlush(note);
        }

        return null;
    }

    public List<Note> bulkUpdate(List<Note> notes) {
        return noteRepository.saveAll(notes);
    }

    public List<Note> bulkInsert(List<Note> notes) {
        notes.forEach(note -> {
            note.setOwner(CurrentAuthContext.getName());
            note.setUserId(CurrentAuthContext.getUserId());
        });
        return noteRepository.saveAll(notes);
    }
}
