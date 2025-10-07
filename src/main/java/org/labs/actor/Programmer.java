package org.labs.actor;

import java.util.concurrent.ThreadLocalRandom;

import org.labs.config.ProgrammerTimeConfigProperties;
import org.labs.resource.Plate;
import org.labs.resource.Spoon;
import org.labs.service.FoodProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Programmer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Programmer.class);

    private final int id;
    private final Spoon leftSpoon;
    private final Spoon rightSpoon;
    private final ProgrammerTimeConfigProperties programmerTimeConfigProperties;
    private final FoodProvider foodProvider;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private int consumedSoupPortions = 0;

    public Programmer(
            int id,
            Spoon leftSpoon,
            Spoon rightSpoon,
            ProgrammerTimeConfigProperties programmerTimeConfigProperties,
            FoodProvider foodProvider
    ) {
        this.id = id;

        if (leftSpoon.getId() < rightSpoon.getId()) {
            this.leftSpoon = leftSpoon;
            this.rightSpoon = rightSpoon;
        } else {
            this.leftSpoon = rightSpoon;
            this.rightSpoon = leftSpoon;
        }

        this.programmerTimeConfigProperties = programmerTimeConfigProperties;
        this.foodProvider = foodProvider;
    }

    public int getId() {
        return id;
    }

    public int getConsumedSoupPortions() {
        return consumedSoupPortions;
    }

    @Override
    public void run() {
        try {
            haveDinner();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[Programmer] [id={}] Was interrupted. Reason: {}", id, e.getMessage(), e);
        }
        log.info("[Programmer] [id={}] Finished dinner. Consumed soup portions: {}", id, consumedSoupPortions);
    }

    private void haveDinner() throws InterruptedException {
        while (foodProvider.isServing()) {
            discussTeachers();

            var plate = refillPlate();
            if (plate.isEmpty()) {
                continue;
            }
            takeSpoons();

            try {
                eat();
            } finally {
                releaseSpoons();
            }
        }
    }

    private void discussTeachers() throws InterruptedException {
        var discussTimeMillis = random.nextInt(
                programmerTimeConfigProperties.discussMillisMin(),
                programmerTimeConfigProperties.discussMillisMax()
        );

        log.debug("[Programmer] [id={}] Will discuss teachers for {} ms", id, discussTimeMillis);
        Thread.sleep(discussTimeMillis);
        log.debug("[Programmer] [id={}] Stopped discussing teachers", id);
    }

    private void takeSpoons() {
        leftSpoon.acquire();
        log.debug("[Programmer] [id={}] [spoonId={}] Successfully acquired spoon", id, leftSpoon.getId());
        rightSpoon.acquire();
        log.debug("[Programmer] [id={}] [spoonId={}] Successfully acquired spoon", id, rightSpoon.getId());
    }

    private Plate refillPlate() throws InterruptedException {
        log.debug("[Programmer] [id={}] Will ask to refill his plate", id);

        var plate = new Plate(id, consumedSoupPortions);
        foodProvider.refillPlate(plate);
        while (plate.isEmpty() && foodProvider.isServing()) {
            plate.askToRefill();
        }

        if (!plate.isEmpty()) {
            log.debug("[Programmer] [id={}] Plate was refilled", id);
        }

        return plate;
    }

    private void eat() throws InterruptedException {
        var eatingTimeMillis = random.nextInt(
                programmerTimeConfigProperties.eatingMillisMin(),
                programmerTimeConfigProperties.eatingMillisMax()
        );

        log.debug("[Programmer] [id={}] Will be eating for {} ms", id, eatingTimeMillis);
        Thread.sleep(eatingTimeMillis);
        log.debug("[Programmer] [id={}] Stopped eating", id);

        consumedSoupPortions += 1;
    }

    private void releaseSpoons() {
        rightSpoon.release();
        log.debug("[Programmer] [id={}] [spoonId={}] Successfully released spoon", id, rightSpoon.getId());
        leftSpoon.release();
        log.debug("[Programmer] [id={}] [spoonId={}] Successfully released spoon", id, leftSpoon.getId());
    }
}
