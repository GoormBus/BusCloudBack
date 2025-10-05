package goorm.domain.buslog.domain.repository;

import goorm.domain.buslog.domain.entity.BusLog;
import goorm.domain.buslog.presentation.dto.request.AlarmReq;
import goorm.domain.buslog.presentation.dto.request.NoteFavoriteRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Repository
@RequiredArgsConstructor
public class NoteRepository {
    private final EntityManager em;

    public BusLog save(BusLog busLog) {
        em.persist(busLog);
        return busLog;
    }

    public List<BusLog> findAll(String memberId) {
        return em.createQuery("select m from BusLog m where m.member.memberId = : memberId", BusLog.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public void updelete(NoteFavoriteRequest req, int num) {
        BusLog busLog = em.find(BusLog.class, req.id());
        if (busLog != null) {
            busLog.setFavorite(true);
           busLog.setFavorite_pre(num);
        }
    }
    public Optional<BusLog> findById(Long id) {
        try {
            BusLog findBusLog = em.createQuery("select m from BusLog m where m.id = :id ", BusLog.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(findBusLog);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    public void delete(NoteFavoriteRequest req) {
        BusLog busLog = em.find(BusLog.class, req.id());
        // 엔티티가 존재하면 삭제합니다.
        if (busLog != null) {
            em.remove(busLog);

        }
    }

    public void deletePre(BusLog busLog2) {
        BusLog busLog = em.find(BusLog.class, busLog2.getId());
        // 엔티티가 존재하면 삭제합니다.
        if (busLog != null) {
            em.remove(busLog);

        }
    }


    public List<BusLog> findByAlarmTrue() {
        return em.createQuery("select n from BusLog n where n.alarm = true", BusLog.class)
                .getResultList();
    }

    public List<BusLog> findByAlarmTrueisExcute() {
        return em.createQuery("select n from BusLog n where n.alarm = true and n.execute=false", BusLog.class)
                .getResultList();
    }

    public void updateAlarm(AlarmReq req) {
        BusLog busLog = em.find(BusLog.class, req.id());
        busLog.setAlarm(req.alarm());
    }

}
