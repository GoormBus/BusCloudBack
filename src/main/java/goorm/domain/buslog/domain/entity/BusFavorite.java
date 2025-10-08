package goorm.domain.buslog.domain.entity;

import goorm.domain.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Table(
        name = "bus_favorite",
        indexes = {
                @Index(name = "idx_bus_log_id", columnList = "bus_log_id")
        }
)
public class BusFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_log_id")
    private BusLog busLog;

    private boolean isFavoriteFlag;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public BusFavorite(BusLog busLog){
        this.busLog = busLog;
        this.isFavoriteFlag = true; // true로 초기화
    }


    public void activateIsFavoriteFlag() {
        this.isFavoriteFlag = true;
    }

    public void deactivateIsFavoriteFlag() {
        this.isFavoriteFlag = false;
    }


}
