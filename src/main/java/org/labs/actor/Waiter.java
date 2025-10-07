package org.labs.actor;

import java.util.concurrent.ThreadLocalRandom;

import org.labs.config.WaiterTimeConfigProperties;
import org.labs.resource.Plate;
import org.labs.service.CustomerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Waiter implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Waiter.class);

    private final int id;
    private final WaiterTimeConfigProperties waiterTimeConfigProperties;
    private final CustomerProvider customerProvider;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private int platesServed = 0;

    public Waiter(int id, WaiterTimeConfigProperties waiterTimeConfigProperties, CustomerProvider customerProvider) {
        this.id = id;
        this.waiterTimeConfigProperties = waiterTimeConfigProperties;
        this.customerProvider = customerProvider;
    }

    public int getPlatesServed() {
        return platesServed;
    }

    @Override
    public void run() {
        try {
            serveCustomers();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[Waiter] [id={}] Was interrupted. Reason: {}", id, e.getMessage(), e);
        }
        log.info("[Waiter] [id={}] Finished execution. Served plates: {}", id, platesServed);
    }

    private void serveCustomers() throws InterruptedException {
        while (customerProvider.isServing() || customerProvider.hasPlatesToRefill()) {
            var plate = customerProvider.findPlateToRefill();

            serveCustomer(plate);
        }
    }

    private void serveCustomer(Plate plate) throws InterruptedException {
        if (plate == null) {
            return;
        }
        if (!customerProvider.getSoup()) {
            plate.refuseToRefill();
            return;
        }

        var servingTimeMillis = random.nextInt(
                waiterTimeConfigProperties.servingMillisMin(),
                waiterTimeConfigProperties.servingMillisMax()
        );

        log.debug(
                "[Waiter] [id={}] Will be serving customer {} for {} ms",
                id,
                plate.getProgrammerId(),
                servingTimeMillis
        );
        Thread.sleep(servingTimeMillis);
        plate.refill();

        log.debug("[Waiter] [id={}] Stopped serving customer {}", id, plate.getProgrammerId());
        platesServed += 1;
    }
}
