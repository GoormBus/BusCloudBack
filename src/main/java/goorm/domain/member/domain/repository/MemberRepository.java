package goorm.domain.member.domain.repository;

import goorm.domain.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 👤 MemberRepository
 *
 * <p>회원(Member) 엔티티에 대한 데이터 접근을 담당하는 JPA Repository입니다.</p>
 * <ul>
 *     <li>회원 기본 CRUD</li>
 *     <li>전화번호를 통한 회원 조회</li>
 * </ul>
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    /**
     * 📞 전화번호로 회원을 조회합니다.
     *
     * @param phoneNumber 회원 전화번호
     * @return 일치하는 회원이 존재할 경우 Optional<Member>
     */
    Optional<Member> findByPhone(String phoneNumber);
}
