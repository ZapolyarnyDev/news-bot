package io.github.zapolyarnydev.service;

import io.github.zapolyarnydev.entity.SubscriptionEntity;
import io.github.zapolyarnydev.news.NewsFrequency;
import io.github.zapolyarnydev.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NewsFrequencyService {

    @Autowired
    private SubscriptionRepository repository;

    public NewsFrequency getNewsFrequency(Long chatId){
        Optional<SubscriptionEntity> subscriptionEntity = repository.findById(chatId);
        return subscriptionEntity.filter(entity -> (entity.getNewsFrequency() != null))
                .map(SubscriptionEntity::getNewsFrequency)
                .orElse(NewsFrequency.HOURLY);
    }

    public void setNewsFrequency(Long chatId, NewsFrequency frequency){
        Optional<SubscriptionEntity> subscriptionEntity = repository.findById(chatId);
        if(subscriptionEntity.isPresent()){
            subscriptionEntity.get().setNewsFrequency(frequency);
            repository.save(subscriptionEntity.get());
        } else {
            var entity = new SubscriptionEntity(chatId, false);
            entity.setNewsFrequency(frequency);
            repository.save(entity);
        }
    }
}
