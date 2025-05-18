package io.github.zapolyarnydev.action;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class DeleteAction implements TelegramAction{

    private final DeleteMessage message;

    @Override
    public void apply(TelegramLongPollingBot bot) throws TelegramApiException {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to delete message", e);
        }
    }
}
