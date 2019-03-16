package com.uttpal.schedular.utils;

import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * @author Uttpal
 */
@Component
public class DateTimeUtil {

    public long getEpochSecs() {
        return Instant.now().toEpochMilli() / 1000;
    }
}
