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
    private String phone;

    @Builder
    public Member(String memberId, String phone) {
        this.memberId= memberId;
        this.phone = phone;
    }

    public Member() {

    }
}
