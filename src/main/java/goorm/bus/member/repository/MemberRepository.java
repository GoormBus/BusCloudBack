package goorm.bus.member.repository;

import goorm.bus.member.entity.Member;
import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Transactional
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;
    private final AtomicLong memberIdxGenerator = new AtomicLong(0);
    private final Map<Long, Member> memberMap = new HashMap<>();

    // 저장
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    // id로 Member 조회
    public Optional<Member> findById(Long id) {
        Assert.notNull(id, "ID MUST NOT BE NULL");
        return Optional.ofNullable(memberMap.get(id));
    }

    // memberId로 Member 조회
    public Optional<Member> findByPhone(String phone) {
        try {
            Member findMember = em.createQuery("select m from Member m where m.phone = :phone ", Member.class)
                    .setParameter("phone", phone)
                    .getSingleResult();
            return Optional.of(findMember);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    // memberId로 Member가 존재하는지 검사
    public Boolean existByPhone(String phone) {
        try {
            Member findMember = em.createQuery("select m from Member m where m.phone=:phone", Member.class)
                    .setParameter("phone", phone)
                    .getSingleResult();
            return true; // 멤버가 존재하는 경우 true 반환
        } catch (NoResultException e) {
            return false; // 결과가 없을 때 false 반환
        }
    }

}
