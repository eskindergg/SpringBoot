package com.project.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.api.auth.CurrentAuthContext;
import com.project.api.core.Constants;
import com.project.api.core.NotFoundException;
import com.project.api.core.SyncConflictException;
import com.project.api.core.utils.NoteFactory;
import com.project.api.core.utils.NoteJsonHelper;
import com.project.api.model.Note;
import com.project.api.repository.NoteRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Transactional
    public List<Note> getNotes() {
        return this.noteRepository.getUserNotes(CurrentAuthContext.getUserId().toString());
    }

    @Transactional
    public Note save(Note note) {
        note.setOwner(CurrentAuthContext.getName());
        note.setUserId(CurrentAuthContext.getUserId());
        return this.noteRepository.save(note);
    }


    @Transactional
    public Note update(Note note) {
        Note fetchNote = noteRepository.getNote(note.getId(), CurrentAuthContext.getUserId());

        if (fetchNote == null) {
            throw new NotFoundException("Either the note has been moved or deleted", note);
        } else {
            try {
                return noteRepository.saveAndFlush(note);

            } catch (JpaSystemException ex) {

                SQLException sqlEx = (SQLException) ex.getCause().getCause();
                String SQL_STATE = sqlEx.getSQLState();

                if (SQL_STATE.equals(Constants.SQL_STATE_CONFLICT))
                    throw new SyncConflictException("Using old date to update the server", fetchNote);

                if (SQL_STATE.equals(Constants.SQL_NOT_FOUND))
                    throw new NotFoundException(ex.getMessage(), note);
            }
        }
        return note;
    }

    @Transactional
    public List<Note> bulkUpdate(List<Note> notes) {
        try {
            String notesJson = NoteJsonHelper.convertNotesToJson(notes.stream().map(NoteFactory::create).toList());
            return noteRepository.note_bulk_upsert(CurrentAuthContext.getUserId().toString(), CurrentAuthContext.getName(), notesJson);
        } catch (JpaSystemException ex) {
            SQLException sqlEx = (SQLException) ex.getCause().getCause();
            String SQL_STATE = sqlEx.getSQLState();

            if (SQL_STATE.equals(Constants.SQL_STATE_CONFLICT))
                throw new SyncConflictException("Using old date to update the server", notes);

            if (SQL_STATE.equals(Constants.SQL_NOT_FOUND))
                throw new NotFoundException(ex.getMessage(), notes);

            return notes;
        }
    }

    @Transactional
    public List<Note> bulkInsert(List<Note> notes) throws JsonProcessingException {

        String notesJson = NoteJsonHelper.convertNotesToJson(notes);

        return this.noteRepository.note_bulk_insert(notesJson);
    }
}
