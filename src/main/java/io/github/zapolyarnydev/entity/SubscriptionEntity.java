package io.github.zapolyarnydev.entity;

import io.github.zapolyarnydev.model.NewsFrequency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class SubscriptionEntity {
    @Id
    private Long chatId;

    private boolean subscribed;

    @JdbcTypeCode(SqlTypes.JSON)
    private final Set<String> subscribedCategories = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private NewsFrequency newsFrequency;

    private LocalDateTime lastNewsSendDateTime;

    public SubscriptionEntity(Long chatId, boolean subscribed) {
        this.chatId = chatId;
        this.subscribed = subscribed;
    }
}