package io.github.zapolyarnydev.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubscriptionEntity {
    @Id
    private Long chatId;

    private boolean subscribed;

    @JdbcTypeCode(SqlTypes.JSON)
    private final Set<String> subscribedCategories = new HashSet<>();
}
