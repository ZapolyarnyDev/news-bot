package io.github.zapolyarnydev.handler;

import io.github.zapolyarnydev.response.BotResponse;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public interface CommandHandler {

    BotResponse handle(Update update);
}
