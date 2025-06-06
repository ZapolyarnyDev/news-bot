package io.github.zapolyarnydev.service.news;

import io.github.zapolyarnydev.entity.SubscriptionEntity;
import io.github.zapolyarnydev.model.NewsFrequency;
import io.github.zapolyarnydev.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsFrequencyService {

    private final SubscriptionRepository subscriptionRepository;

    private final Map<Long, LocalDateTime> lastCheck = new HashMap<>();

    public NewsFrequency getNewsFrequency(Long chatId){
        Optional<SubscriptionEntity> subscriptionEntity = subscriptionRepository.findById(chatId);
        return subscriptionEntity.filter(entity -> (entity.getNewsFrequency() != null))
                .map(SubscriptionEntity::getNewsFrequency)
                .orElse(NewsFrequency.HOURLY);
    }

    public void setNewsFrequency(Long chatId, NewsFrequency frequency){
        Optional<SubscriptionEntity> subscriptionEntity = subscriptionRepository.findById(chatId);
        if(subscriptionEntity.isPresent()){
            subscriptionEntity.get().setNewsFrequency(frequency);
            subscriptionRepository.save(subscriptionEntity.get());
        } else {
            var entity = new SubscriptionEntity(chatId, false);
            entity.setNewsFrequency(frequency);
            entity.setLastNewsSendDateTime(LocalDateTime.now());
            subscriptionRepository.save(entity);
        }
    }

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
