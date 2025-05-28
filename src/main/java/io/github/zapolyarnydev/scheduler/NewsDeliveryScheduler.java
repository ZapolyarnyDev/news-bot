package io.github.zapolyarnydev.scheduler;

import io.github.zapolyarnydev.bot.NewsBot;
import io.github.zapolyarnydev.entity.SentNewsEntity;
import io.github.zapolyarnydev.model.FetchedNewsWrapper;
import io.github.zapolyarnydev.model.News;
import io.github.zapolyarnydev.repository.SentNewsRepository;
import io.github.zapolyarnydev.repository.SubscriptionRepository;
import io.github.zapolyarnydev.service.news.NewsDeliveryService;
import io.github.zapolyarnydev.service.news.NewsFetcherService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class NewsDeliveryScheduler {
    private final SubscriptionRepository subscriptionRepository;
    private final SentNewsRepository sentNewsRepository;
    private final NewsDeliveryService newsDeliveryService;
    private final NewsBot newsBot;
    private final NewsFetcherService newsFetcherService;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void processDeliveries(){
        String[] categories = {"sport", "it", "economy"};
        List<FetchedNewsWrapper> fetchedNewsWrappers = new ArrayList<>();
        for(String s : categories){
            fetchedNewsWrappers.add(new FetchedNewsWrapper(newsFetcherService.fetchByCategory(s), s));
        }
        subscriptionRepository.findAll().forEach(subscriptionEntity -> {
            if(newsDeliveryService.shouldSendNews(subscriptionEntity.getChatId())){
                for(var wrapper : fetchedNewsWrappers){
                    if(!subscriptionEntity.getSubscribedCategories().contains(wrapper.category())) continue;
                    Long chatId = subscriptionEntity.getChatId();

                    Optional<News> selectedNews = getRandomNews(chatId, wrapper.news());

                    if(selectedNews.isPresent()){
                        sendNews(chatId, selectedNews.get());
                        markAsSent(chatId, selectedNews.get());
                    }
                }
            }
        });
    }

    private Optional<News> getRandomNews(Long chatId, List<News> news){
        Random random = new Random();
        List<News> unsetNews = newsFetcherService.getNewUnsetNews(chatId, news);

        if(unsetNews.isEmpty()) return Optional.empty();

        return Optional.of(
                unsetNews.get(random.nextInt(unsetNews.size()))
        );
    }

    private void markAsSent(Long chatId, News news){
        var entity = new SentNewsEntity();
        entity.setChatId(chatId);
        entity.setNewsId(chatId + "_" + DigestUtils.md5Hex(news.url()));
        entity.setSentAt(LocalDateTime.now());
        sentNewsRepository.save(entity);
    }

    private void sendNews(Long chatId, News news){
        try {
            newsBot.execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(formatNewsMessage(news))
                    .parseMode("HTML")
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private String formatNewsMessage(News news) {
        String safeDescription = sanitizeForTelegram(news.description());
        return String.format(
                "<b>%s</b>\n%s\n<a href=\"%s\">Источник</a>",
                news.title(),
                safeDescription,
                news.url()
        );
    }

    private String sanitizeForTelegram(String html) {

        String allowed = Jsoup.clean(html, "", Safelist.none(), new Document.OutputSettings().prettyPrint(false));
        return escapeHtml(allowed);
    }

    private String escapeHtml(String input) {
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
