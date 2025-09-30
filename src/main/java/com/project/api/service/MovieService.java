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
import jakarta.persistence.*;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

//    @Transactional
//    public List<Map<String, Object>> movieUpsert(String movie) {
////        String movieJson = JsonHelper.convertToJson(movie);
//        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("movie_upsert");
//        query.registerStoredProcedureParameter("movies_json", String.class, ParameterMode.IN);
//        query.setParameter("movies_json", movie);
//        query.unwrap(org.hibernate.query.NativeQuery.class)
//                .setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
//        query.execute();
////        List<Map<String, Object>> result = query.getResultList();
////        return result;
////        return this.movieRepository.movie_upsert(movie);
//       List<Tuple> tupleResults = query.getResultList();
//
//    // 5. Manually map the Tuple to the required List<Map<String, Object>>
//    return tupleResults.stream()
//        .map(tuple -> {
//            Map<String, Object> rowMap = new java.util.HashMap<>();
//            // The Tuple knows its column structure and aliases from the stored procedure SELECT
//            for (TupleElement<?> element : tuple.getElements()) {
//                String alias = element.getAlias();
//                Object value = tuple.get(alias);
//                rowMap.put(alias, value);
//            }
//            return rowMap;
//        })
//        .collect(Collectors.toList());
//    }
@Transactional
public List<Map<String, Object>> movieUpsert(String movie) {

    // 1. Prepare the raw SQL call string
    // Note the use of '?' for positional parameters in the native query
    String sql = "CALL movie_upsert(?)";

    // 2. Create the Native Query, expecting Tuple results
    // Tuple is the key here to represent a generic row/column structure
    Query query = entityManager.createNativeQuery(sql, Tuple.class);

    // 3. Set the IN parameter (positional: 1)
    // The stored procedure expects the 'movies_json' string as its first parameter.
    query.setParameter(1, movie);

    // 4. Execute and get results as List<Tuple>
    // Since we specified Tuple.class, JPA should map the results to Tuples.
    @SuppressWarnings("unchecked")
    List<Tuple> tupleResults = query.getResultList();

    // 5. Manually map the List<Tuple> to List<Map<String, Object>>
    return tupleResults.stream()
            .map(tuple -> {
                Map<String, Object> rowMap = new java.util.HashMap<>();
                // Use getElements() to iterate through columns and aliases
                for (TupleElement<?> element : tuple.getElements()) {
                    String alias = element.getAlias();
                    Object value = tuple.get(alias);
                    rowMap.put(alias, value);
                }
                return rowMap;
            })
            .collect(Collectors.toList());
}

    @Transactional
    public List<User> getUsers() {
        return this.userRepository.getUsers();
    }
}
