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


    // 저장
    public Member save(Member member) {
        em.persist(member);
        return member;
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

    public Optional<Member> findByPhone2(String id) {
        try {
            Member findMember = em.createQuery("select m from Member m where m.memberId = :phone ", Member.class)
                    .setParameter("phone", id)
                    .getSingleResult();
            return Optional.of(findMember);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }



}
