package org.labs.config;

/**
 * Настройки времени действия разработчиков
 *
 * @param discussMillisMin минимальное время обсуждения (мс)
 * @param discussMillisMax максимальное время обсуждения (мс)
 * @param eatingMillisMin минимальное время приёма пищи (мс)
 * @param eatingMillisMax максимальное время приёма пищи (мс)
 */
public record ProgrammerTimeConfigProperties(
        int discussMillisMin,
        int discussMillisMax,
        int eatingMillisMin,
        int eatingMillisMax
) {
}
