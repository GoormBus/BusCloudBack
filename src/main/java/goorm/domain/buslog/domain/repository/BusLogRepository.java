package goorm.domain.buslog.domain.repository;

import feign.Param;
import goorm.domain.buslog.domain.entity.BusLog;
import goorm.domain.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 🚌 BusLogRepository
 *
 * <p>버스 로그(BusLog) 엔티티에 대한 데이터 접근을 담당하는 JPA Repository입니다.</p>
 * <ul>
 *     <li>회원별 버스 로그 조회</li>
 *     <li>버스 로그 기본 CRUD</li>
 * </ul>
 */
@Repository
public interface BusLogRepository extends JpaRepository<BusLog, Long> {

    /**
     * 특정 회원이 생성한 버스 로그 목록을 조회합니다.
     *
     * @param member 회원 엔티티
     * @return 해당 회원의 BusLog 리스트
     */
    List<BusLog> findByMember(Member member);


    @Query("""
    SELECT DISTINCT b
    FROM BusLog b
    LEFT JOIN FETCH b.busAlarm
    LEFT JOIN FETCH b.busFavorite
    WHERE b.member = :member
""")
    List<BusLog> findAllWithAlarmAndFavorite(@Param("member") Member member);

}
