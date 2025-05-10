package io.github.zapolyarnydev.configuration;

import io.github.zapolyarnydev.bot.NewsBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class NewsBotConfiguration {

    @Bean
    public TelegramBotsApi telegramBotsApi(NewsBot newsBot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(newsBot);
        return api;
    }

}
