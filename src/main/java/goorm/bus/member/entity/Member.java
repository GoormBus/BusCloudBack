package goorm.bus.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Member {

    @Id
    private String memberId;
    private String loginId;
    private String password;
    private String name;

    @Builder
    public Member(String memberId, String loginId, String password, String name) {
        this.memberId= memberId;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
    }

    public Member() {

    }
}
