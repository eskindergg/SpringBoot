package com.project.api.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class NoteId implements Serializable {
    private UUID id;
    private UUID userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NoteId notePK)) return false;
        return id == notePK.id && Objects.equals(userId, notePK.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId);
    }

}
