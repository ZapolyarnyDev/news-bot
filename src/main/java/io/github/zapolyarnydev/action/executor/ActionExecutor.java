package io.github.zapolyarnydev.action.executor;

import io.github.zapolyarnydev.action.TelegramAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ActionExecutor {

    private final TelegramLongPollingBot bot;

    public void execute(List<TelegramAction> actions){
        actions.forEach(telegramAction -> {
            try {
                telegramAction.apply(bot);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
