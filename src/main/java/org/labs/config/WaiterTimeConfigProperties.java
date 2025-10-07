package org.labs.config;

/**
 * Настройки времени работы официанта
 *
 * @param servingMillisMin минимальное время подачи блюда (мс)
 * @param servingMillisMax максимальное время подачи блюда (мс)
 */
public record WaiterTimeConfigProperties(
        int servingMillisMin,
        int servingMillisMax
) {
}
