package goorm.domain.buslog.domain.repository;

import goorm.domain.buslog.domain.entity.BusLog;
import goorm.domain.member.domain.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BusLogRepository extends JpaRepository<BusLog, Long> {

    List<BusLog> findByMember(Member member);

}
