package io.github.zapolyarnydev.service;

import io.github.zapolyarnydev.entity.SubscriptionEntity;
import io.github.zapolyarnydev.repository.SubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository repository;

    public void subscribe(Long chatId){
        Optional<SubscriptionEntity> entity = repository.findById(chatId);
        if(entity.isPresent()){
            entity.get().setSubscribed(true);
            repository.save(entity.get());
        } else {
            repository.save(new SubscriptionEntity(chatId, true));
        }
    }

    public void unsubscribe(Long chatId){
        Optional<SubscriptionEntity> entity = repository.findById(chatId);
        if(entity.isPresent()){
            entity.get().setSubscribed(false);
            repository.save(entity.get());
        } else {
            repository.save(new SubscriptionEntity(chatId, false));
        }
    }

    public boolean isSubscribed(Long chatId){
        return repository.findById(chatId)
                .map(SubscriptionEntity::isSubscribed)
                .orElse(false);
    }

    public boolean hasSubscribeOnCategory(Long chatId, String categoryId){
        return repository.findById(chatId)
                .map(subscriptionEntity -> subscriptionEntity.getSubscribedCategories().contains(categoryId))
                .orElse(false);
    }

    public boolean chatExists(Long chatId){
        return repository.existsById(chatId);
    }

    @Transactional
    public void addCategory(Long chatId, String categoryId){
        Optional<SubscriptionEntity> subscriptionEntity = repository.findById(chatId);
        if(subscriptionEntity.isPresent()){
            subscriptionEntity.get().getSubscribedCategories().add(categoryId);
            repository.save(subscriptionEntity.get());
        } else {
            var entity = new SubscriptionEntity(chatId, false);
            entity.getSubscribedCategories().add(categoryId);
            repository.save(entity);
        }
    }

    @Transactional
    public void removeCategory(Long chatId, String categoryId){
        repository.findById(chatId).ifPresent(subscriptionEntity ->{
            subscriptionEntity.getSubscribedCategories().remove(categoryId);
            repository.save(subscriptionEntity);
        });

    }
}
