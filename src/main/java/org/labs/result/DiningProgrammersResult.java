package org.labs.result;

public record DiningProgrammersResult(
        int restaurantPortionsLeft,
        int programmersPortionsEaten,
        int minPortions,
        int maxPortions,
        double averagePortions,
        long totalSimulationMillis
) {
}
