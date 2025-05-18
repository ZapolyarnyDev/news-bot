package io.github.zapolyarnydev.service;

import io.github.zapolyarnydev.entity.SubscriptionEntity;
import io.github.zapolyarnydev.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository repository;

    public void subscribe(Long chatId){
        repository.save(new SubscriptionEntity(chatId, true));
    }

    public void unsubscribe(Long chatId){
        repository.save(new SubscriptionEntity(chatId, false));
    }

    public boolean isSubscribed(Long chatId){
        return repository.findById(chatId)
                .map(SubscriptionEntity::isSubscribed)
                .orElse(false);
    }

    public boolean chatExists(Long chatId){
        return repository.existsById(chatId);
    }
}
