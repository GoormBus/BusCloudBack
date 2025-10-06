package goorm.domain.busalarm.domain.repository;

import goorm.domain.busalarm.domain.entity.BusAlarm;
import goorm.domain.buslog.domain.entity.BusLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusAlarmRepository extends JpaRepository<BusAlarm, Long> {
    Optional<BusAlarm> findByBusLog(BusLog busLog);
}
