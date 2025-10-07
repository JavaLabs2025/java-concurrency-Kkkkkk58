package org.labs.service;

import org.labs.resource.Plate;

public interface FoodProvider extends Service {
    void refillPlate(Plate plate);
}
