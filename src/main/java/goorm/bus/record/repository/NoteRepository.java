package goorm.bus.record.repository;

import goorm.bus.member.entity.Member;
import goorm.bus.record.dto.request.AlarmReq;
import goorm.bus.record.dto.request.NoteFavoriteRequest;
import goorm.bus.record.entity.Note;
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

    public Note save(Note note) {
        em.persist(note);
        return note;
    }

    public List<Note> findAll(String memberId) {
        return em.createQuery("select m from Note m where m.member.memberId = : memberId", Note.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public void updelete(NoteFavoriteRequest req, int num) {
        Note note = em.find(Note.class, req.id());
        if (note != null) {
            note.setFavorite(true);
           note.setFavorite_pre(num);
        }
    }
    public Optional<Note> findById(Long id) {
        try {
            Note findNote = em.createQuery("select m from Note m where m.id = :id ", Note.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(findNote);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    public void delete(NoteFavoriteRequest req) {
        Note note = em.find(Note.class, req.id());
        // 엔티티가 존재하면 삭제합니다.
        if (note != null) {
            em.remove(note);

        }
    }

    public void deletePre(Note note2) {
        Note note = em.find(Note.class, note2.getId());
        // 엔티티가 존재하면 삭제합니다.
        if (note != null) {
            em.remove(note);

        }
    }


    public List<Note> findByAlarmTrue() {
        return em.createQuery("select n from Note n where n.alarm = true", Note.class)
                .getResultList();
    }

    public List<Note> findByAlarmTrueisExcute() {
        return em.createQuery("select n from Note n where n.alarm = true and n.execute=false", Note.class)
                .getResultList();
    }

    public void updateAlarm(AlarmReq req) {
        Note note = em.find(Note.class, req.id());
        note.setAlarm(req.alarm());
    }

}
