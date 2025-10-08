package goorm.global.infra.monitoring;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class HibernateStatisticsLogger {

    private final EntityManagerFactory entityManagerFactory;

    /**
     * 버스 관련 API 요청 후 Hibernate 쿼리 통계 출력
     */
    @AfterReturning("execution(* goorm.domain.buslog.presentation..*(..)) || execution(* goorm.domain.busalarm.presentation..*(..))")
    public void logHibernateStatistics() {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics stats = sessionFactory.getStatistics();

        long selectCount = stats.getQueryExecutionCount();   // 실행된 SELECT 문 수
        long insertCount = stats.getEntityInsertCount();     // INSERT 문 수
        long updateCount = stats.getEntityUpdateCount();     // UPDATE 문 수
        long deleteCount = stats.getEntityDeleteCount();     // DELETE 문 수
        long totalCount = selectCount + insertCount + updateCount + deleteCount;

        log.info("🧾 [Hibernate 쿼리 통계 요약]");
        log.info("───────────────────────────────");
        log.info("SELECT 실행 횟수 : {}", selectCount);
        log.info("INSERT 실행 횟수 : {}", insertCount);
        log.info("UPDATE 실행 횟수 : {}", updateCount);
        log.info("DELETE 실행 횟수 : {}", deleteCount);
        log.info("총 쿼리 실행 횟수 : {}", totalCount);
        log.info("───────────────────────────────");

        // 요청별 측정을 위해 통계 초기화
        stats.clear();
    }
}
