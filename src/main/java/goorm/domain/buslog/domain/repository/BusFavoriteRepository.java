package goorm.domain.buslog.domain.repository;

import goorm.domain.busalarm.domain.entity.BusAlarm;
import goorm.domain.buslog.domain.entity.BusFavorite;
import goorm.domain.buslog.domain.entity.BusLog;
import goorm.domain.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BusFavoriteRepository extends JpaRepository<BusFavorite, Long> {

    Optional<BusFavorite> findByBusLog(BusLog busLog);
}
