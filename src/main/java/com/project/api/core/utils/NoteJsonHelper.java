package com.project.api.core.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import com.project.api.model.Note;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

public class NoteJsonHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setTimeZone(TimeZone.getDefault())
            .setSerializationInclusion(JsonInclude.Include.ALWAYS)
            .registerModule(new SimpleModule() {{
                setSerializerModifier(new BeanSerializerModifier() {
                    @Override
                    public JsonSerializer<?> modifySerializer(
                            SerializationConfig config,
                            BeanDescription beanDesc,
                            JsonSerializer<?> serializer
                    ) {
                        // Cast to avoid '? capture' error
                        @SuppressWarnings("unchecked")
                        JsonSerializer<Object> casted = (JsonSerializer<Object>) serializer;

                        return new JsonSerializer<Object>() {
                            @Override
                            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers)
                                    throws IOException {
                                if (value == null) {
                                    gen.writeString("");
                                } else {
                                    casted.serialize(value, gen, serializers);
                                }
                            }
                        };
                    }
                });
            }});

    public static String convertNotesToJson(List<Note> notes) {
        try {
            return objectMapper.writeValueAsString(notes);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing notes to JSON", e);
        }
    }

    public static String convertNoteToJson(Note note) {
        try {
            return objectMapper.writeValueAsString(note);
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

    public static Note parseNoteFromJson(String json) {
        try {
            return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<Note>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing note from JSON", e);
        }
    }
}
