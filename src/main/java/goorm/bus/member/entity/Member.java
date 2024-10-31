package goorm.bus.member.entity;

import goorm.bus.record.entity.Note;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Member {

    @Id
    private String memberId;
    private String phone;
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> note = new ArrayList<>();
    @Builder
    public Member(String memberId, String phone) {
        this.memberId= memberId;
        this.phone = phone;
    }

    public Member() {

    }
}
