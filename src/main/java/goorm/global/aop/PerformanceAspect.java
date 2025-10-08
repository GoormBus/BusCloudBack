package goorm.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class PerformanceAspect {

    @Around("execution(* goorm.domain.buslog.application.service..*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = joinPoint.proceed();  // 실제 메서드 실행

        stopWatch.stop();

        String methodName = joinPoint.getSignature().toShortString();
        log.info("⏱️ [성능측정] {} 실행 시간: {} ms", methodName, stopWatch.getTotalTimeMillis());

        return result;
    }
}
