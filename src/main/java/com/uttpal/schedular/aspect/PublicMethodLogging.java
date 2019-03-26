package com.uttpal.schedular.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

@Component
@Aspect
@Profile("!test")
public class PublicMethodLogging {

    private final Logger logger = LogManager.getLogger(PublicMethodLogging.class);

    @Around("execution(* com.uttpal.schedular..*.*(..))"
            + "&& !@annotation(com.uttpal.schedular.aspect.NoLogging)"
            + "&& !@target(com.uttpal.schedular.aspect.NoLogging)")
    public Object logMethodArgsTime(ProceedingJoinPoint joinPoint) throws Throwable {

        StringBuilder logMessage = new StringBuilder();
        logMessage.append(String.format("Enter %s.%s(", joinPoint.getTarget().getClass().getName(), joinPoint.getSignature().getName()));
        Object[] args = joinPoint.getArgs();

        Arrays.stream(args)
                .map(logMessage::append)
                .forEach(argLog -> argLog.append(","));

        if (args.length > 0) {
            logMessage.deleteCharAt(logMessage.length() - 1);
        }
        logger.info("{})", logMessage.toString());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object retVal = joinPoint.proceed();

        stopWatch.stop();

        logger.info(String.format("Exit  %s.%s Returned { %s } execution time: %s ms", joinPoint.getTarget().getClass().getName(), joinPoint.getSignature().getName(), retVal, stopWatch.getTotalTimeMillis()));


        return retVal;
    }

}