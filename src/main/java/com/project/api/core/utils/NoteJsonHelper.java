package com.project.api.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import com.project.api.model.Note;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NoteJsonHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static String convertNotesToJson(List<Note> notes) {
        try {
            return objectMapper.writeValueAsString(notes);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing notes to JSON", e);
        }
    }

    public static List<Note> parseNotesFromJson(String json) {
        try {
            return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<List<Note>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing notes from JSON", e);
        }
    }
}
