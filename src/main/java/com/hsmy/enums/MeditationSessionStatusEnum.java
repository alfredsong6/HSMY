package com.hsmy.enums;

/**
 * Meditation session lifecycle.
 */
public enum MeditationSessionStatusEnum {
    STARTED,
    INTERRUPTED,
    COMPLETED;

    public static MeditationSessionStatusEnum fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return MeditationSessionStatusEnum.valueOf(value.trim().toUpperCase());
    }
}
