package goorm.bus.record.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Entity
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String departure;

    private String destination;

    private String stopCnt;

    private String time;

    private Boolean alarm;

    @Builder
    public Note(String departure, String destination, String stopCnt, String time, Boolean alarm) {
        this.departure=departure;
        this.destination=destination;
        this.stopCnt=stopCnt;
        this.time=time;
        this.alarm=alarm;
    }


    public Note(){

    }
}
