package goorm.bus.record.repository;

import goorm.bus.record.entity.Note;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Transactional
@Repository
@RequiredArgsConstructor
public class NoteRepository {
    private final EntityManager em;

    public Note save(Note note){
        em.persist(note);
        return note;
    }

    public List<Note> findAll(){
        return em.createQuery("select m from Note m",Note.class)
                .getResultList();
    }
}
