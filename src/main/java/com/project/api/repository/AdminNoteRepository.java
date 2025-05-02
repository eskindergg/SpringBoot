package com.project.api.repository;

import com.project.api.model.Note;
import com.project.api.model.NoteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AdminNoteRepository extends JpaRepository<Note, NoteId> {
    @Query("select n from Note n ORDER BY n.dateModified DESC")
    List<Note> getNotes();

    @Query("select n from Note n where n.id = ?1")
    Note getNote(UUID noteId, UUID userId);

    @Query("SELECT n.owner, n.userId, count(n.userId) FROM Note as n GROUP BY n.owner, n.userId")
    List<Object> getUsers();

    @Procedure(name = "admin_bulk_update")
    Note admin_update_note(@Param("json_note") String note_json);

    @Procedure(name = "admin_bulk_update")
    List<Note> admin_bulk_update(@Param("json_notes") String notesJson);
}
