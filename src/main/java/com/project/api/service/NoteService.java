package com.project.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.project.api.auth.CurrentAuthContext;
import com.project.api.core.Constants;
import com.project.api.core.NotFoundException;
import com.project.api.core.SyncConflictException;
import com.project.api.core.utils.NoteJsonHelper;
import com.project.api.model.Note;
import com.project.api.repository.NoteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ObjectMapper objectMapper;

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

    public List<Note> bulkUpdate(List<Note> notes) {
        try {
            return noteRepository.saveAll(notes);
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
    @SuppressWarnings("unchecked")
    @Transactional
    public List<Note> bulkInsert(List<Note> notes) throws JsonProcessingException {
//        notes.forEach(note -> {
//            note.setOwner(CurrentAuthContext.getName());
//            note.setUserId(CurrentAuthContext.getUserId());
//        });
//        return noteRepository.saveAll(notes);
//        objectMapper.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, false);
//        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));
//        String notesJson = NoteJsonHelper.convertNotesToJson(notes);

//        String notesJson = objectMapper.writeValueAsString(notes);
//        notesJson = notesJson.replace("+00:00", "Z");
//        List<String> resultJson = noteRepository.admin_upsert(notesJson);
//        return NoteJsonHelper.parseNotesFromJson(resultJson);
        String notesJson = NoteJsonHelper.convertNotesToJson(notes);

        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("admin_upsert")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .setParameter(1, notesJson);

        List<Object[]> result = query.getResultList(); // still safe
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
}
