package com.project.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.api.auth.CurrentAuthContext;
import com.project.api.core.Constants;
import com.project.api.core.NotFoundException;
import com.project.api.core.SyncConflictException;
import com.project.api.core.utils.JsonHelper;
import com.project.api.model.Note;
import com.project.api.model.User;
import com.project.api.repository.AdminNoteRepository;
import com.project.api.repository.MovieRepository;
import com.project.api.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class MovieService {

    @Autowired
    private AdminNoteRepository adminNoteRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public List<Note> getNotes() {
        return this.adminNoteRepository.getAdminNotes();
    }

    @Transactional
    public Note save(Note note) {
        return this.adminNoteRepository.save(note);
    }

    @Transactional
    public List<Object> bulkUpdate(List<Object> movies) throws JsonProcessingException {
        try {
            String moviesJson = JsonHelper.convertToJson(movies);
            return this.movieRepository.movies_bulk_upsert(moviesJson);
        } catch (JpaSystemException ex) {

            SQLException sqlEx = (SQLException) ex.getCause().getCause();
            String SQL_STATE = sqlEx.getSQLState();
        }
        return null;
    }

    @Transactional
    public List<Object> getUserMovies() throws JsonProcessingException {
        try {
            return this.movieRepository.getUserMovies(CurrentAuthContext.getUserId().toString());
        } catch (JpaSystemException ex) {

            SQLException sqlEx = (SQLException) ex.getCause().getCause();
            String SQL_STATE = sqlEx.getSQLState();
        }
        return null;
    }

    @Transactional
    public List<Note> bulkInsert(List<Note> notes) {
        notes.forEach(note -> {
            note.setOwner(CurrentAuthContext.getName());
            note.setUserId(CurrentAuthContext.getUserId());
        });
        return adminNoteRepository.saveAll(notes);
    }

    @Transactional
    public List<Map<String, Object>> getUsersNotesCount() {
        Query query = entityManager.createNativeQuery("CALL getUsersNotesCount()");
        query.unwrap(org.hibernate.query.NativeQuery.class)
                .setResultTransformer(org.hibernate.transform.AliasToEntityMapResultTransformer.INSTANCE);

        List<Map<String, Object>> result = query.getResultList();
        return result;
    }

    @Transactional
    public List<User> updateUsers(List<User> users) {
        String usersJson = JsonHelper.convertToJson(users);
        return this.userRepository.users_bulk_upsert(usersJson);
    }

    @Transactional
    public List<User> getUsers() {
        return this.userRepository.getUsers();
    }
}
