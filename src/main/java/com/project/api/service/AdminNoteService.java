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
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

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

    @Transactional
    public Note update(Note note) {
//        Note fetchNote = adminNoteRepository.getNote(note.getId(), note.getUserId());

        try {
            Object[] row = (Object[]) adminNoteRepository.updateNoteUser(
                    note.getId().toString(),
                    note.getUserId().toString(),
                    note.getHeader(),
                    note.getText(),
                    note.getColour(),
                    note.getHeight(),
                    note.getWidth(),
                    note.getTop(),
                    note.getLeft(),
                    note.getDateCreated(),
                    note.getDateModified(),
                    note.getDateArchived(),
                    note.getPinOrder(),
                    note.isArchived(),
                    note.isActive(),
                    note.getSelection(),
                    note.isSpellCheck(),
                    note.getOwner(),
                    note.isFavorite(),
                    note.getDateSync(),
                    note.isPinned()
            );

            Object[] result = (Object[]) row[0];

            Note returnedNote = new Note();
            returnedNote.setId(UUID.fromString((String) result[0]));
            returnedNote.setUserId(UUID.fromString((String) result[1]));
            returnedNote.setHeader((String) result[2]);
            returnedNote.setText((String) result[3]);
            returnedNote.setColour((String) result[4]);
            returnedNote.setHeight((Integer) result[5]);
            returnedNote.setWidth((Integer) result[6]);
            returnedNote.setTop((Integer) result[7]);
            returnedNote.setLeft((Integer) result[8]);
            returnedNote.setDateCreated((Timestamp) result[9]);
            returnedNote.setDateModified((Timestamp) result[10]);
            returnedNote.setDateArchived((Timestamp) result[11]);
            returnedNote.setPinOrder((Timestamp) result[12]);
            returnedNote.setArchived(((Boolean) result[13]));
            returnedNote.setActive(((Boolean) result[14]));
            returnedNote.setSelection((String) result[15]);
            returnedNote.setSpellCheck((Boolean) result[16]);
            returnedNote.setOwner((String) result[17]);
            returnedNote.setFavorite((Boolean) result[18]);
            returnedNote.setDateSync((Timestamp) result[19]);
            returnedNote.setPinned((Boolean) result[20]);
            return returnedNote;

        } catch (JpaSystemException ex) {

            SQLException sqlEx = (SQLException) ex.getCause().getCause();
            String SQL_STATE = sqlEx.getSQLState();

            if (SQL_STATE.equals(Constants.SQL_STATE_CONFLICT))
                throw new SyncConflictException("Using old date to update the server", note);

            if (SQL_STATE.equals(Constants.SQL_NOT_FOUND))
                throw new NotFoundException(ex.getMessage(), note);
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

    public List<Object> getUsers() {
        return this.adminNoteRepository.getUsers();
    }
}
