package goorm.domain.busalarm.domain.entity;

import goorm.domain.buslog.domain.entity.BusLog;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Table(name = "버스 알림")
public class BusAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_log_id")
    private BusLog busLog;

    @Comment("알림 활성화")
    @Column(nullable = false)
    private boolean isAlarmFlag;

    @Comment("얘는 뭐지..")
    @Column(nullable = false)
    private boolean isExecuteFlag; // 매일 실행 알림때문에

    @Comment("알림 잔여 횟수, 초기는 2로 고정")
    @Column(nullable = false)
    private Long alarmRemaining;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public BusAlarm(BusLog busLog) {
        this.busLog = busLog;
        this.isAlarmFlag = true;
        this.isExecuteFlag = true;
        this.alarmRemaining = 2L;
    }

    public void activateIsAlarmFlag() {
        this.isAlarmFlag = true;
    }

    public void deactivateIsAlarmFlag() {
        this.isAlarmFlag = false;
    }

    public void updateAlarmRemaining() {
        this.alarmRemaining -= 1;
    }
}
