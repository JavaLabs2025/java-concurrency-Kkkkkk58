package org.labs;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.labs.config.DiningProgrammersConfigProperties;
import org.labs.config.ProgrammerTimeConfigProperties;
import org.labs.config.WaiterTimeConfigProperties;
import org.labs.result.DiningProgrammersResult;
import org.labs.utils.TimeWatch;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class DiningProgrammersProblemTest {
    @Test
    void diningProgrammers_emptyRestaurant_isError() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> runSimulation(7, 2, 0));
    }

    @Test
    void diningProgrammers_programmersCountEqualPortionsCount_allProgrammersAteOnePortion() {
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            var result = runSimulation(7, 2, 7);

            Assertions.assertEquals(1, result.minPortions());
            Assertions.assertEquals(1, result.maxPortions());

            assertSuccessfulResult(result, 7);
        });
    }

    @Test
    void diningProgrammers_programmersCountLessThanPortionsCount_notAllProgrammersAte() {
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            var result = runSimulation(2, 2, 1);

            Assertions.assertEquals(0, result.minPortions());
            Assertions.assertEquals(1, result.maxPortions());

            assertSuccessfulResult(result, 1);
        });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("fairnessCases")
    void diningProgrammers_checkFairness_deltaPercentLessThanThreshold(
            String displayName,
            int programmers,
            int waiters,
            int totalPortions,
            double deltaPercent
    ) {
        assertTimeoutPreemptively(Duration.ofSeconds(15), () -> {
            var result = runSimulation(programmers, waiters, totalPortions);

            int allowedDelta = computeAllowedDelta(totalPortions, programmers, deltaPercent);
            int actualDelta = result.maxPortions() - result.minPortions();
            Assertions.assertTrue(
                    actualDelta <= allowedDelta,
                    "Fairness violated: actualDelta=%d, allowedDelta=%d, min=%d, max=%d"
                            .formatted(actualDelta, allowedDelta, result.minPortions(), result.maxPortions())
            );

            assertSuccessfulResult(result, totalPortions);
        });
    }

    static Stream<Arguments> fairnessCases() {
        return Stream.of(
                Arguments.of("Many portions, little programmers", 7, 2, 2_000, 0.03),
                Arguments.of("Many programmers", 100, 4, 1_000, 0.03)
        );
    }

    private static DiningProgrammersResult runSimulation(int programmers, int waiters, int totalPortions) {
        DiningProgrammersConfigProperties config = new DiningProgrammersConfigProperties(
                programmers,
                waiters,
                totalPortions,
                new ProgrammerTimeConfigProperties(1, 2, 1, 2),
                new WaiterTimeConfigProperties(1, 2)
        );

        ExecutorService programmersPool = Executors.newVirtualThreadPerTaskExecutor();
        ExecutorService waitersPool = Executors.newVirtualThreadPerTaskExecutor();

        var problem = new DiningProgrammersProblem(
                programmersPool,
                waitersPool,
                config,
                new TimeWatch()
        );
        return problem.solve();
    }

    private static int computeAllowedDelta(int total, int programmers, double deltaPercent) {
        double avg = (double) total / (double) programmers;
        int delta = (int) Math.ceil(avg * deltaPercent);
        return Math.max(1, delta);
    }

    private static void assertSuccessfulResult(DiningProgrammersResult result, int totalPortions) {
        Assertions.assertEquals(totalPortions, result.programmersPortionsEaten());
        Assertions.assertEquals(0, result.restaurantPortionsLeft());

        Assertions.assertTrue(result.totalSimulationMillis() >= 0);
        Assertions.assertTrue(result.minPortions() <= result.maxPortions());
    }
}
