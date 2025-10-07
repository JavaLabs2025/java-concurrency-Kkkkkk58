package org.labs.result.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.labs.result.DiningProgrammersResult;
import org.labs.result.DiningProgrammersResultPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingDiningProgrammersResultPrinter implements DiningProgrammersResultPrinter {
    private static final Logger log = LoggerFactory.getLogger(LoggingDiningProgrammersResultPrinter.class);
    private final ObjectMapper objectMapper;

    public LoggingDiningProgrammersResultPrinter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void print(DiningProgrammersResult result) {
        try {
            var serializedResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
            log.info("[RESULT] {}", serializedResult);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
