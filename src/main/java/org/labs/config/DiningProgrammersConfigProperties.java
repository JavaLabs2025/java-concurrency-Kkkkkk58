package org.labs.config;

/**
 * Конфигурация симуляции
 *
 * @param programmersCount количество программистов
 * @param waitersCount количество официантов
 * @param totalSoupPortions количество порций супа
 * @param programmerTimeConfigProperties настройки времени действия разработчиков {@link ProgrammerTimeConfigProperties}
 * @param waiterTimeConfigProperties настройки времени работы официанта {@link WaiterTimeConfigProperties}
 */
public record DiningProgrammersConfigProperties(
    int programmersCount,
    int waitersCount,
    int totalSoupPortions,
    ProgrammerTimeConfigProperties programmerTimeConfigProperties,
    WaiterTimeConfigProperties waiterTimeConfigProperties
) {
}
