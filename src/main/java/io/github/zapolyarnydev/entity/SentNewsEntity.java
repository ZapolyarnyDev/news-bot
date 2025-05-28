package io.github.zapolyarnydev.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sent_news")
@Getter
@Setter
public class SentNewsEntity {

    @Id
    private String newsId;
    private Long chatId;
    private LocalDateTime sentAt;
}
