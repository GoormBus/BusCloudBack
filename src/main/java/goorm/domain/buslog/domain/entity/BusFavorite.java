package goorm.domain.buslog.domain.entity;

import goorm.domain.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Table(name="버스 기록 즐겨찾기")
public class BusFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_log_id")
    private BusLog busLog;

    private boolean isFavoriteFlag;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;


}
