package com.uttpal.schedular.utils;

import com.uttpal.schedular.aspect.NoLogging;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * @author Uttpal
 */
@Component
public class DateTimeUtil {

    @NoLogging
    public long getEpochMillis() {
        return Instant.now().toEpochMilli();
    }
}
