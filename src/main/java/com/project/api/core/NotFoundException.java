package com.project.api.core;

import com.project.api.model.Note;

public class NotFoundException extends RuntimeException {
   private final Note note;

   public NotFoundException(String msg, Note note) {
       super(msg);
       this.note = note;
   }

   public Note getNote() {
       return this.note;
   }
}
