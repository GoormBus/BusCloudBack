package goorm.domain.buslog.domain.repository;

import goorm.domain.buslog.domain.entity.BusLog;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BusLogRepository extends JpaRepository<BusLog, Long> {


}
