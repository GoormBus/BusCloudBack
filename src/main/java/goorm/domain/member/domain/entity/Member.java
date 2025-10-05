package goorm.domain.member.domain.entity;

import goorm.domain.record.entity.Note;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> note = new ArrayList<>();

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;


    @Builder
    public Member(String phone, String name) {
        this.phone = phone;
        this.name = name;
        this.role = Role.USER;
    }

}
