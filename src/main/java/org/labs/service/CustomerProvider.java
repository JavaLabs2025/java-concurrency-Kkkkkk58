package org.labs.service;

import org.jetbrains.annotations.Nullable;
import org.labs.resource.Plate;

public interface CustomerProvider extends Service {
    boolean hasPlatesToRefill();
    @Nullable
    Plate findPlateToRefill();
    boolean getSoup();
}
