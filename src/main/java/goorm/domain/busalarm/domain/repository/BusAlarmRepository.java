package goorm.domain.busalarm.domain.repository;

import goorm.domain.busalarm.domain.entity.BusAlarm;
import goorm.domain.buslog.domain.entity.BusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 🔔 BusAlarmRepository
 *
 * <p>버스 알림(BusAlarm) 엔티티에 대한 데이터 접근을 담당하는 JPA Repository입니다.</p>
 * <ul>
 *     <li>버스 로그와 연동된 알림 정보 조회</li>
 *     <li>알림 활성/비활성 상태 관리</li>
 * </ul>
 */
@Repository
public interface BusAlarmRepository extends JpaRepository<BusAlarm, Long> {

    /**
     * 특정 버스 로그에 연결된 알림 정보를 조회합니다.
     *
     * @param busLog 대상 버스 로그 엔티티
     * @return 해당 버스 로그에 매핑된 BusAlarm 정보 (Optional)
     */
    Optional<BusAlarm> findByBusLog(BusLog busLog);
}
