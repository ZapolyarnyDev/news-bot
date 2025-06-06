package io.github.zapolyarnydev.scheduler;

import io.github.zapolyarnydev.bot.NewsBot;
import io.github.zapolyarnydev.entity.SentNewsEntity;
import io.github.zapolyarnydev.entity.SubscriptionEntity;
import io.github.zapolyarnydev.model.FetchedNewsWrapper;
import io.github.zapolyarnydev.model.News;
import io.github.zapolyarnydev.repository.SentNewsRepository;
import io.github.zapolyarnydev.repository.SubscriptionRepository;
import io.github.zapolyarnydev.service.message.KeyboardService;
import io.github.zapolyarnydev.service.news.NewsFetcherService;
import io.github.zapolyarnydev.service.news.NewsFrequencyService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.HttpURLConnection;
import java.net.URL;
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
    private final NewsBot newsBot;
    private final NewsFrequencyService newsFrequencyService;
    private final NewsFetcherService newsFetcherService;
    private final KeyboardService keyboardService;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void processDeliveries(){
        String[] categories = {"sport", "it", "economy"};
        List<FetchedNewsWrapper> fetchedNewsWrappers = new ArrayList<>();
        for(String s : categories){
            var news = newsFetcherService.fetchByCategory(s);
            fetchedNewsWrappers.add(new FetchedNewsWrapper(news, s));
        }
        subscriptionRepository.findAll().forEach(subscriptionEntity -> {
            if(newsFrequencyService.shouldSendNews(subscriptionEntity.getChatId())){
                for(var wrapper : fetchedNewsWrappers){
                    if(!subscriptionEntity.getSubscribedCategories().contains(wrapper.category())) continue;
                    Long chatId = subscriptionEntity.getChatId();

                    Optional<News> selectedNews = getRandomNews(chatId, wrapper.news());

                    if(selectedNews.isPresent()){
                        sendNews(subscriptionEntity, selectedNews.get());
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

    private void sendNews(SubscriptionEntity entity, News news) {
        entity.setLastNewsSendDateTime(LocalDateTime.now());
        try {
            if (news.imageUrl() != null && isValidImageUrl(news.imageUrl())) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(entity.getChatId());
                sendPhoto.setPhoto(new InputFile(news.imageUrl()));
                sendPhoto.setCaption(formatNewsMessage(news));
                sendPhoto.setParseMode("HTML");
                sendPhoto.setReplyMarkup(keyboardService.getNewsKeyboard(news));
                newsBot.execute(sendPhoto);
            } else {
                newsBot.execute(SendMessage.builder()
                        .chatId(entity.getChatId())
                        .text(formatNewsMessage(news))
                        .parseMode("HTML")
                        .replyMarkup(keyboardService.getNewsKeyboard(news))
                        .build());
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to send news: " + e.getMessage(), e);
        }
    }

    private boolean isValidImageUrl(String url) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                return false;
            }

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            return false;
        }
    }

    private String formatNewsMessage(News news) {
        String description = sanitizeForTelegram(news.description());
        String fullText = String.format("<b>%s</b>\n%s\n", news.title(), description);

        if (fullText.length() > 450) {
            fullText = fullText.substring(0, 450) + "...";
        }
        return fullText;
    }

    private String sanitizeForTelegram(String html) {
        if(html.equals(" ")) return html;
        String allowed = Jsoup.clean(html, "", Safelist.none(), new Document.OutputSettings().prettyPrint(false));
        return "\n" + allowed + "\n";
    }
}
