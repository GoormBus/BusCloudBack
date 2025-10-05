package goorm.domain.busalarm.domain.entity;

import goorm.domain.buslog.domain.entity.BusLog;
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
@Table(name="버스 알림")
public class BusAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_log_id")
    private BusLog busLog;


    private boolean isAlarmFlag;

    private boolean isExecuteFlag; // 매일 실행 알림때문에
    private int frequency;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
