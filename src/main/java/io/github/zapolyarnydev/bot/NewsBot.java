package io.github.zapolyarnydev.bot;

import io.github.zapolyarnydev.configuration.BotProperties;
import io.github.zapolyarnydev.handler.CommandHandler;
import io.github.zapolyarnydev.response.BotResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class NewsBot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    @Autowired
    private CommandHandler startCommandHandler;

    public NewsBot(BotProperties botProperties) {
        super(botProperties.getToken());
        this.botProperties = botProperties;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();

            if (text.equals("/start")) {
                var response = startCommandHandler.handle(update);
                sendMessage(response);
            }
        }
    }
    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    public void sendMessage(BotResponse botResponse){
        SendMessage message = new SendMessage();
        message.setChatId(botResponse.chatId());
        message.setText(botResponse.text());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }
}
