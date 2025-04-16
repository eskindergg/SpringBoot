package com.project.api.repository;

import com.project.api.model.Note;
import com.project.api.model.NoteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface AdminNoteRepository extends JpaRepository<Note, NoteId> {
    @Query("select n from Note n ORDER BY n.dateModified DESC")
    List<Note> getNotes();

    @Query("select n from Note n where n.id = ?1")
    Note getNote(UUID noteId, UUID userId);

    @Query("SELECT n.owner, n.userId, count(n.userId) FROM Note as n GROUP BY n.owner, n.userId")
    List<Object> getUsers();

    @Procedure(procedureName = "upsert_note")
    Object[] updateNoteUser(
            String p_note_id,
            String p_user_id,
            String p_header,
            String p_text,
            String p_colour,
            Integer p_height,
            Integer p_width,
            Integer p_top,
            Integer p_left,
            Timestamp p_date_created,
            Timestamp p_date_modified,
            Timestamp p_date_archived,
            Timestamp p_pin_order,
            Boolean p_archived,
            Boolean p_active,
            String p_selection,
            Boolean p_spell_check,
            String p_owner,
            Boolean p_favorite,
            Timestamp p_date_sync,
            Boolean p_pinned
    );
}
