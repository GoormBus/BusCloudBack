package goorm.domain.buslog.domain.entity;

import goorm.domain.busalarm.domain.entity.BusAlarm;
import goorm.domain.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Table(name="버스 기록")
public class BusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "busLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BusFavorite> busFavorites = new ArrayList<>();

    @OneToMany(mappedBy = "busLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BusAlarm> busAlarms = new ArrayList<>();

    @Comment("출발지 ex: 제주 공항")
    @Column(nullable = false)
    private String departure;

    @Comment("도착지 ex: 성산일출봉")
    private String destination;


    @Comment("사용자가 지정한 잔여 정류장")
    private Long station;

    @Comment("출발 정류장 ID")
    private String stationId;

    @Comment("노선 ID 즉 버스 번호 ex: 112번")
    private String notionId;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public BusLog(Member member,String departure, String destination, Long station,
                  String stationId, String notionId
                  ) {
        this.member=member;
        this.departure = departure;
        this.destination = destination;
        this.station = station;
        this.stationId=stationId;
        this.notionId=notionId;

    }

}
