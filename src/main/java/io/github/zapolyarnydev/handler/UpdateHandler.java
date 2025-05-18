package io.github.zapolyarnydev.handler;

import io.github.zapolyarnydev.action.TelegramAction;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public abstract class UpdateHandler {

    public abstract List<TelegramAction> handle(Update update);
}
