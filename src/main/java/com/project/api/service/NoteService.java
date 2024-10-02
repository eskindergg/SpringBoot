package com.project.api.service;

import com.project.api.auth.CurrentAuthContext;
import com.project.api.core.SyncConflictException;
import com.project.api.model.Note;
import com.project.api.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

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
            try {
                return noteRepository.saveAndFlush(note);

            } catch (JpaSystemException ex) {
                SQLException sqlEx = (SQLException) ex.getCause().getCause();
                if (Objects.equals(sqlEx.getSQLState(), "12121"))
                    throw new SyncConflictException("Using old date to update the server", fetchNote);
            }
        }
        return note;
    }

    public List<Note> bulkUpdate(List<Note> notes) {
        try {
            return noteRepository.saveAll(notes);
        } catch (JpaSystemException ex) {
            SQLException sqlEx = (SQLException) ex.getCause().getCause();
            if (Objects.equals(sqlEx.getSQLState(), "12121"))
                throw new SyncConflictException("Using old date to update the server", notes);
            return notes;
        }
    }

    public List<Note> bulkInsert(List<Note> notes) {
        notes.forEach(note -> {
            note.setOwner(CurrentAuthContext.getName());
            note.setUserId(CurrentAuthContext.getUserId());
        });
        return noteRepository.saveAll(notes);
    }
}
