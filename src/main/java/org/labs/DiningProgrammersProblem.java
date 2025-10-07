package org.labs;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.labs.actor.Programmer;
import org.labs.actor.Waiter;
import org.labs.config.DiningProgrammersConfigProperties;
import org.labs.config.ProgrammerTimeConfigProperties;
import org.labs.resource.Spoon;
import org.labs.result.DiningProgrammersResult;
import org.labs.service.FoodProvider;
import org.labs.service.Restaurant;
import org.labs.utils.TimeWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiningProgrammersProblem {
    private static final Logger log = LoggerFactory.getLogger(DiningProgrammersProblem.class);
    private static final long DEFAULT_PROGRESS_INTERVAL_SECONDS = 1;

    private final ExecutorService programmersPool;
    private final ExecutorService waitersPool;
    private final DiningProgrammersConfigProperties configProperties;
    private final TimeWatch timeWatch;

    public DiningProgrammersProblem(
            ExecutorService programmersPool,
            ExecutorService waitersPool,
            DiningProgrammersConfigProperties configProperties, TimeWatch timeWatch
    ) {
        this.programmersPool = programmersPool;
        this.waitersPool = waitersPool;
        this.configProperties = configProperties;
        this.timeWatch = timeWatch;
    }

    public DiningProgrammersResult solve() {
        var actors = createActors();
        log.info("[DiningProgrammersProblem] Simulation is initialized and ready to start");

        timeWatch.start();
        runActors(actors.programmers, programmersPool);
        runActors(actors.waiters, waitersPool);

        awaitExecution(actors.restaurant);

        return makeResults(actors, timeWatch.tick());
    }

    private DiningProgrammersActorsAndServices createActors() {
        var restaurant = new Restaurant(configProperties.totalSoupPortions(), configProperties.programmersCount());
        var spoons = IntStream.range(0, configProperties.programmersCount())
                .mapToObj(Spoon::new)
                .toList();

        var programmers = IntStream.range(0, configProperties.programmersCount())
                .mapToObj(id ->
                        createProgrammer(id, spoons, configProperties.programmerTimeConfigProperties(), restaurant)
                ).toList();

        var waiters = IntStream.range(0, configProperties.waitersCount())
                .mapToObj(id ->
                        new Waiter(id, configProperties.waiterTimeConfigProperties(), restaurant)
                ).toList();

        return new DiningProgrammersActorsAndServices(programmers, waiters, restaurant);
    }

    private Programmer createProgrammer(
            int id,
            List<Spoon> spoons,
            ProgrammerTimeConfigProperties configProperties,
            FoodProvider foodProvider
    ) {
        var leftSpoon = spoons.get(id);
        var rightSpoon = spoons.get((id + 1) % spoons.size());

        return new Programmer(id, leftSpoon, rightSpoon, configProperties, foodProvider);
    }

    private void runActors(List<? extends Runnable> actors, ExecutorService pool) {
        actors.forEach(pool::submit);
    }

    private void awaitExecution(Restaurant restaurant) {
        programmersPool.shutdown();
        waitersPool.shutdown();

        try {
            while (!programmersPool.awaitTermination(DEFAULT_PROGRESS_INTERVAL_SECONDS, TimeUnit.SECONDS)) {
                var elapsedSeconds = timeWatch.tick().getSeconds();
                log.info(
                        "[DiningProgrammersProblem] Currently running for {} seconds, available soup portions: {}," +
                                " plates to refill: {}",
                        elapsedSeconds,
                        restaurant.getRemainingSoupPortions(),
                        restaurant.getPlatesToRefillCount()
                );
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[DiningProgrammersProblem] Problem thread interrupted, problem is not solved");

            throw new RuntimeException(e);
        }
    }

    private DiningProgrammersResult makeResults(DiningProgrammersActorsAndServices actors, Duration tick) {
        return new DiningProgrammersResult();
    }

    private record DiningProgrammersActorsAndServices(
            List<Programmer> programmers,
            List<Waiter> waiters,
            Restaurant restaurant
    ) {  }
}
