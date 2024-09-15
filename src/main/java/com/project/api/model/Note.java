package com.project.api.model;

import com.project.api.core.GenerateUpdatedAtTimestamp;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "note_tbl")
@IdClass(NoteId.class)
public class Note {

    @Id
    @Column(name = "note_id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Id
    @Column(name = "user_id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID userId;

    @Column(name = "header")
    private String header;

    @Column(name = "colour")
    private String colour;

    @Column(name = "height")
    private Integer height;

    @Column(name = "width")
    private Integer width;

    @Column(name = "`left`")
    private Integer left;

    @Column(name = "top")
    private Integer top;

    @Column(name = "selection")
    private String selection;

    @Column(name = "archived")
    private boolean archived;

    @Column(name = "pinned")
    private boolean pinned;

    @Column(name = "favorite")
    private boolean favorite;

    @Column(name = "active")
    private boolean active = true;

    @Column(name = "spell_check")
    private boolean spellCheck;

    @GenerateUpdatedAtTimestamp
    @Column(name = "pin_order", nullable = false)
    private Timestamp pinOrder = new Timestamp(new Date().getTime());

    @Column(name = "date_created", insertable = false, nullable = false)
    private Timestamp dateCreated = new Timestamp(new Date().getTime());

    @GenerateUpdatedAtTimestamp
    @Column(name = "date_modified", nullable = false )
    private Timestamp dateModified = new Timestamp(new Date().getTime());

    @GenerateUpdatedAtTimestamp
    @Column(name = "date_archived", nullable = false)
    private Timestamp dateArchived = new Timestamp(new Date().getTime());

    @Column(name = "date_sync")
    private Timestamp dateSync;

    @Column(name = "owner")
    private String owner;

    @Column(name = "text", length = 16777215, columnDefinition = "mediumtext")
    private String text;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getLeft() {
        return left;
    }

    public void setLeft(Integer left) {
        this.left = left;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isSpellCheck() {
        return spellCheck;
    }

    public void setSpellCheck(boolean spellCheck) {
        this.spellCheck = spellCheck;
    }

    public Timestamp getPinOrder() {
        return pinOrder;
    }

    public void setPinOrder(Timestamp pinOrder) {
        this.pinOrder = pinOrder;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Timestamp getDateModified() {
        return dateModified;
    }

    public void setDateModified(Timestamp dateModified) {
        this.dateModified = dateModified;
    }

    public Timestamp getDateArchived() {
        return dateArchived;
    }

    public void setDateArchived(Timestamp dateArchived) {
        this.dateArchived = dateArchived;
    }

    public Timestamp getDateSync() {
        return dateSync;
    }

    public void setDateSync(Timestamp dateSync) {
        this.dateSync = dateSync;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
