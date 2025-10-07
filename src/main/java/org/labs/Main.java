package org.labs;

import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.labs.config.impl.JsonDiningProgrammersConfigReader;
import org.labs.result.impl.LoggingDiningProgrammersResultPrinter;
import org.labs.utils.TimeWatch;

public class Main {
    private static final String DEFAULT_CONFIG_FILE = "config.json";

    public static void main(String[] args) {
        var configPath = args.length > 0 ? args[0] : DEFAULT_CONFIG_FILE;
        var objectMapper = new ObjectMapper();
        var configReader = new JsonDiningProgrammersConfigReader(objectMapper);
        var configProperties = configReader.read(configPath);

        try (var programmersPool = Executors.newVirtualThreadPerTaskExecutor();
                var waitersPool = Executors.newVirtualThreadPerTaskExecutor()
        ) {
            var problem = new DiningProgrammersProblem(
                    programmersPool,
                    waitersPool,
                    configProperties,
                    new TimeWatch()
            );
            var result = problem.solve();
            var resultPrinter = new LoggingDiningProgrammersResultPrinter(objectMapper);
            resultPrinter.print(result);
        }
    }
}
