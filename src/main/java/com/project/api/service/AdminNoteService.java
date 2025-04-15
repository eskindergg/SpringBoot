package com.project.api.service;

import com.project.api.auth.CurrentAuthContext;
import com.project.api.core.Constants;
import com.project.api.core.NotFoundException;
import com.project.api.core.SyncConflictException;
import com.project.api.model.Note;
import com.project.api.repository.AdminNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class AdminNoteService {

    @Autowired
    private AdminNoteRepository adminNoteRepository;

    public List<Note> getNotes() {
        return this.adminNoteRepository.getNotes();
    }

    public Note save(Note note) {
//        note.setOwner(CurrentAuthContext.getName());
//        note.setUserId(CurrentAuthContext.getUserId());
        return this.adminNoteRepository.save(note);
    }


    public Note update(Note note) {
        Note fetchNote = adminNoteRepository.getNote(note.getId(), note.getUserId());

        if (fetchNote == null) {
            throw new NotFoundException("Either the note has been moved or deleted", note);
        } else {
            try {
                return adminNoteRepository.saveAndFlush(note);

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

    public List<Note> bulkUpdate(List<Note> notes) {
        try {
            return adminNoteRepository.saveAll(notes);
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

    public List<Note> bulkInsert(List<Note> notes) {
        notes.forEach(note -> {
            note.setOwner(CurrentAuthContext.getName());
            note.setUserId(CurrentAuthContext.getUserId());
        });
        return adminNoteRepository.saveAll(notes);
    }
}
