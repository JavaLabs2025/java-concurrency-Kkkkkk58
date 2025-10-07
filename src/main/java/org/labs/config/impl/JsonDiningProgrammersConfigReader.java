package org.labs.config.impl;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.labs.config.DiningProgrammersConfigProperties;
import org.labs.config.DiningProgrammersConfigReader;

import static java.lang.ClassLoader.getSystemResourceAsStream;

public class JsonDiningProgrammersConfigReader implements DiningProgrammersConfigReader {
    private final ObjectMapper objectMapper;

    public JsonDiningProgrammersConfigReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public DiningProgrammersConfigProperties read(String path) {
        try {
            return objectMapper.readValue(getFileContent(path), DiningProgrammersConfigProperties.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFileContent(String fileName) {
        try (var fileStream = Objects.requireNonNull(getSystemResourceAsStream(fileName))) {
            return new String(fileStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
