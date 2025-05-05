package com.project.api.repository;

import com.project.api.model.Note;
import com.project.api.model.NoteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, NoteId> {
    @Query("select n from Note n where n.userId = ?1 ORDER BY n.dateModified DESC")
    List<Note> getNotes(UUID id);

    @Procedure(name = "getUserNotes")
    List<Note> getUserNotes(@Param("user_id") String user_id);

    @Query("select n from Note n where n.id = ?1 AND n.userId = ?2")
    Note getNote(UUID noteId, UUID userId);

    @Procedure(name = "note_bulk_insert")
    List<Note> note_bulk_insert(@Param("json_notes") String notesJson);

    @Procedure(name = "note_bulk_upsert")
    List<Note> note_bulk_upsert(@Param("user_id") String user_id,@Param("owner") String owner,@Param("json_notes") String notesJson);
}
