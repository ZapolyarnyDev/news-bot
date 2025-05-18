package io.github.zapolyarnydev.action;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface TelegramAction {

    void apply(TelegramLongPollingBot bot) throws TelegramApiException;
}
