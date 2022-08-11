package org.bankmanagement.aspect.log;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;

@Slf4j
@Aspect
@Component
public class LogMethodAspect {

    private static final String EXECUTION_TEMPLATE = "{} execution {} with parameters ({})";
    private static final String RETURNED_TEMPLATE = "{} returned {}";

    @SneakyThrows
    @Around("@annotation(logMethod)")
    public Object logMethod(ProceedingJoinPoint joinPoint, LogMethod logMethod) {

        String parameters = getParameters(joinPoint);
        String signature = joinPoint.getSignature().toShortString();
        String started = "started";

        log.info(EXECUTION_TEMPLATE, signature, started, parameters);

        LocalTime startTime = LocalTime.now();
        Object procceedResult = joinPoint.proceed();

        long timeRangeInMillis = startTime.until(LocalTime.now(), ChronoUnit.MILLIS);
        String finished = String.format("finished in %dms", timeRangeInMillis);

        log.info(EXECUTION_TEMPLATE, signature, finished, parameters);
        log.debug(RETURNED_TEMPLATE, signature, procceedResult);

        return procceedResult;
    }

    private String getParameters(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (isEmpty(args)) {
            return "no parameters";
        }

        return Stream.of(args)
                .map(o -> isNull(o) ? "null" : o.toString())
                .collect(Collectors.joining(", "));
    }
}
