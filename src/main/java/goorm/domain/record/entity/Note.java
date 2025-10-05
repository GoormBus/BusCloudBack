package goorm.domain.record.entity;

import goorm.domain.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String departure;

    private String destination;


    private int station;

    private String time;

    private boolean favorite;
    private int favorite_pre;

    private boolean alarm;

    private boolean execute; // 매일 실행 알림때문에
    private int frequency;



    private String stationId;
    private String notionId;

    @Builder
    public Note(String departure, String destination, int station, String time,
                boolean alarm, boolean favorite, String stationId, String notionId,
                Member member, int favorite_pre) {
        this.departure = departure;
        this.destination = destination;
        this.station = station;
        this.time = time;
        this.alarm = alarm;
        this.favorite = favorite;
        this.stationId=stationId;
        this.notionId=notionId;
        this.member=member;
        this.favorite_pre=favorite_pre;
    }


    public Note() {

    }
}
