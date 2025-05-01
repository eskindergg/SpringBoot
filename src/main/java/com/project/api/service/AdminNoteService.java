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
import java.util.List;

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

    @Transactional
    public Note save(Note note) {
        return this.adminNoteRepository.save(note);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<Note> update(Note note) {
        try {
        String noteJson = NoteJsonHelper.convertNoteToJson(note);

        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("admin_update_note")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .setParameter(1, noteJson);

        return query.getResultList();

        } catch (JpaSystemException ex) {

            SQLException sqlEx = (SQLException) ex.getCause().getCause();
            String SQL_STATE = sqlEx.getSQLState();

            if (SQL_STATE.equals(Constants.SQL_STATE_CONFLICT))
                throw new SyncConflictException("Using old date to update the server", note);

            if (SQL_STATE.equals(Constants.SQL_NOT_FOUND))
                throw new NotFoundException(ex.getMessage(), note);
        }
        return null;
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public List<Note> bulkUpdate(List<Note> notes) throws JsonProcessingException {
        String notesJson = NoteJsonHelper.convertNotesToJson(notes);

        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("admin_bulk_update")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .setParameter(1, notesJson);

        return query.getResultList();
    }

    @Transactional
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
