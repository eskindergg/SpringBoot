package com.project.api.repository;

import com.project.api.model.Note;
import com.project.api.model.NoteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MovieRepository extends JpaRepository<Note, NoteId> {
//    @Query("select n from Note n ORDER BY n.dateModified DESC")
//    List<Note> getNotes();
//
//    @Procedure(name = "getAdminNotes")
//    List<Note> getAdminNotes();

//    @Query("select n from Note n where n.id = ?1")
//    Note getNote(UUID noteId, UUID userId);

//    @Query("SELECT n.owner, n.userId, count(n.userId) FROM Note as n GROUP BY n.owner, n.userId")
//    List<Object> getUsers();

    @Procedure(name = "getUserMovies")
    List<Object> getUserMovies(@Param("user_id") String user_id);

    @Procedure(name = "movies_bulk_upsert")
    List<Object> movies_bulk_upsert(@Param("json_movies") String moviesJson);

//    @Procedure(name = "getUsersNotesCount")
//    List<Object> getUsersNotesCount();
}
