package com.uttpal.schedular.utils;

import com.uttpal.schedular.aspect.NoLogging;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;

/**
 * @author Uttpal
 */
@Component
public class DateTimeUtil {

    @NoLogging
    public long getEpochMillis() {
        return Instant.now().toEpochMilli();
    }

    public long getExecutedTtl() {return Instant.now().plus(Duration.ofDays(7)).toEpochMilli();}
}
