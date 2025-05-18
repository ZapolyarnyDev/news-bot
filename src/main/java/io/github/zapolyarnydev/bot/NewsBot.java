package io.github.zapolyarnydev.bot;

import io.github.zapolyarnydev.configuration.BotProperties;
import io.github.zapolyarnydev.handler.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class NewsBot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    @Autowired
    private List<CommandHandler> commandHandlers;

    public NewsBot(BotProperties botProperties) {
        super(botProperties.getToken());
        this.botProperties = botProperties;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();

            commandHandlers.stream()
                    .filter(handler -> handler.getCommand().equals(text))
                    .findFirst()
                    .ifPresent(handler -> {
                        var message = handler.handle(update);
                        sendMessage(message);
                    });
        }
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    public void sendMessage(SendMessage message){
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }
}
