package goorm.domain.buslog.domain.repository;

import goorm.domain.buslog.domain.entity.BusFavorite;
import goorm.domain.buslog.domain.entity.BusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * ⭐ BusFavoriteRepository
 *
 * <p>버스 즐겨찾기(BusFavorite) 엔티티에 대한 데이터 접근을 담당합니다.</p>
 * <ul>
 *     <li>즐겨찾기 등록/조회/삭제</li>
 *     <li>BusLog 기반으로 즐겨찾기 조회</li>
 * </ul>
 */
@Repository
public interface BusFavoriteRepository extends JpaRepository<BusFavorite, Long> {

    /**
     * 특정 버스 로그에 해당하는 즐겨찾기 정보를 조회합니다.
     *
     * @param busLog 대상 버스 로그
     * @return Optional<BusFavorite> 객체
     */
    Optional<BusFavorite> findByBusLog(BusLog busLog);
}
