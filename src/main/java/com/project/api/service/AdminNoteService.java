package com.project.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.api.auth.CurrentAuthContext;
import com.project.api.core.Constants;
import com.project.api.core.NotFoundException;
import com.project.api.core.SyncConflictException;
import com.project.api.core.utils.NoteJsonHelper;
import com.project.api.model.Note;
import com.project.api.repository.AdminNoteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AdminNoteService {

    @Autowired
    private AdminNoteRepository adminNoteRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ObjectMapper objectMapper;

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
                    note.isPinned()
            );

            Object[] result = (Object[]) row[0];

            Note returnedNote = new Note();
            returnedNote.setId(UUID.fromString((String) result[0]));
            returnedNote.setUserId(UUID.fromString((String) result[1]));
            returnedNote.setHeader((String) result[2]);
            returnedNote.setText((String) result[3]);
            returnedNote.setColour((String) result[4]);
            returnedNote.setDateCreated((Timestamp) result[5]);
            returnedNote.setDateModified((Timestamp) result[6]);
            returnedNote.setDateArchived((Timestamp) result[7]);
            returnedNote.setPinOrder((Timestamp) result[8]);
            returnedNote.setArchived(((Boolean) result[9]));
            returnedNote.setActive(((Boolean) result[10]));
            returnedNote.setSelection((String) result[11]);
            returnedNote.setSpellCheck((Boolean) result[12]);
            returnedNote.setOwner((String) result[13]);
            returnedNote.setFavorite((Boolean) result[14]);
            returnedNote.setPinned((Boolean) result[15]);
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

    @SuppressWarnings("unchecked")
    public List<Note> bulkUpdate(List<Note> notes) throws JsonProcessingException {
        String notesJson = NoteJsonHelper.convertNotesToJson(notes);

        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("admin_bulk_update")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .setParameter(1, notesJson);

        List<Object[]> result = query.getResultList();
        String jsonResult = objectMapper.writeValueAsString(result);
        if (result.isEmpty()) {
            return List.of(); // no existing notes
        }

        List<Note> existingNotes = new ArrayList<>();

        for (Object[] row : result) {
            Note note = new Note();
            note.setId(UUID.fromString((String) row[0]));        // note_id
            note.setText((String) row[4]);                       // text
            note.setUserId(UUID.fromString((String) row[7]));     // user_id
            note.setHeader((String) row[8]);                     // header
            note.setOwner((String) row[17]);                      // owner
            note.setColour((String) row[19]);                     // colour
            note.setSelection((String) row[15]);                  // selection
            note.setDateCreated((Timestamp) row[9]);             // date_created
            note.setDateModified((Timestamp) row[10]);           // date_modified
            note.setDateArchived((Timestamp) row[11]);           // date_archived
            note.setPinOrder((Timestamp) row[12]);               // pin_order
            note.setPinned(row[20] != null && (Boolean) row[20]);   // pinned
            note.setArchived(row[19] != null && (Boolean) row[19]); // archived
            note.setSpellCheck(row[16] != null && (Boolean) row[16]); // spell_check
            note.setActive(row[14] != null && (Boolean) row[14]);     // active

            existingNotes.add(note);
        }

        return existingNotes;
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
