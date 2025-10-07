package org.labs.config.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.labs.config.DiningProgrammersConfigProperties;
import org.labs.config.DiningProgrammersConfigReader;
import org.labs.config.ProgrammerTimeConfigProperties;
import org.labs.config.WaiterTimeConfigProperties;

public class JsonDiningProgrammersConfigReaderTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DiningProgrammersConfigReader reader = new JsonDiningProgrammersConfigReader(objectMapper);

    @Test
    void readConfigHappyPath() {
        var actualProperties = reader.read("config.json");

        var expectedProperties = new DiningProgrammersConfigProperties(
                52,
                24,
                1100,
                new ProgrammerTimeConfigProperties(
                        1,
                        100,
                        2,
                        50
                ),
                new WaiterTimeConfigProperties(
                        5,
                        10
                )
        );
        Assertions.assertEquals(expectedProperties, actualProperties);
    }
}
