package com.piniters.pinit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "daily_question", uniqueConstraints = {
        @UniqueConstraint(name = "UK_daily_question_date", columnNames = {"selected_date"})
})
public class DailyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question; // 해당 날짜의 질문

    @Column(name = "selected_date")
    private LocalDate selectedDate; // 질문배정일시 (중복 불가)
}