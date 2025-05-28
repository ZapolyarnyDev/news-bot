package io.github.zapolyarnydev.service.news;

import io.github.zapolyarnydev.entity.SubscriptionEntity;
import io.github.zapolyarnydev.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NewsDeliveryService {
    private final SubscriptionRepository subscriptionRepository;

    private final Map<Long, LocalDateTime> lastCheck = new HashMap<>();

    public boolean shouldSendNews(Long chatId){
        if(lastCheck.containsKey(chatId)){
            var time = lastCheck.get(chatId);
            if(Duration.between(time, LocalDateTime.now()).toMinutes() < 1) return false;
        }

        Optional<SubscriptionEntity> optionalEntity = subscriptionRepository.findById(chatId);
        if(optionalEntity.isEmpty()) return false;
        var entity = optionalEntity.get();

        var timePassed = Duration.between(entity.getLastNewsSendDateTime(), LocalDateTime.now());
        lastCheck.put(chatId, LocalDateTime.now());
        return entity.isSubscribed() && timePassed.toMinutes() >= entity.getNewsFrequency().getMinutes();
    }
}
