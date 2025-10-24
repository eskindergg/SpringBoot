package com.project.api.service;

import com.project.api.auth.CurrentAuthContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public List<Map<String, Object>> getUserMovies() {
        String sql = "CALL getUserMovies(?)";
        Query query = entityManager.createNativeQuery(sql, Tuple.class);
        query.setParameter(1, CurrentAuthContext.getUserId().toString());
        @SuppressWarnings("unchecked")
        List<Tuple> tupleResults = query.getResultList();
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
    public List<Map<String, Object>> getWatchedUserMovies() {
        String sql = "CALL getUserWatchedMovies(?)";
        Query query = entityManager.createNativeQuery(sql, Tuple.class);
        query.setParameter(1, CurrentAuthContext.getUserId().toString());
        @SuppressWarnings("unchecked")
        List<Tuple> tupleResults = query.getResultList();
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
    public List<Map<String, Object>> movieBulkUpsert(String movies) {

        String sql = "CALL movies_bulk_upsert(?)";
        Query query = entityManager.createNativeQuery(sql, Tuple.class);
        query.setParameter(1, movies);
        @SuppressWarnings("unchecked")
        List<Tuple> tupleResults = query.getResultList();
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
    public List<Map<String, Object>> watchedBulkUpsert(String movies) {

        String sql = "CALL watched_movies_upsert(?)";
        Query query = entityManager.createNativeQuery(sql, Tuple.class);
        query.setParameter(1, movies);
        @SuppressWarnings("unchecked")
        List<Tuple> tupleResults = query.getResultList();
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
    public List<Map<String, Object>> populateMovies(String movies) {

        String sql = "CALL populate_movies(?)";
        Query query = entityManager.createNativeQuery(sql, Tuple.class);
        query.setParameter(1, movies);
        @SuppressWarnings("unchecked")
        List<Tuple> tupleResults = query.getResultList();
        return tupleResults.stream()
                .map(tuple -> {
                    Map<String, Object> rowMap = new java.util.HashMap<>();
                    for (TupleElement<?> element : tuple.getElements()) {
                        String alias = element.getAlias();
                        Object value = tuple.get(alias);
                        rowMap.put(alias, value);
                    }
                    return rowMap;
                })
                .collect(Collectors.toList());
    }
}
