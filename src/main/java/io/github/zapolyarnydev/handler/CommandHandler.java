package io.github.zapolyarnydev.handler;

import io.github.zapolyarnydev.response.BotResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Getter
public abstract class CommandHandler {

    private final String command;

    public abstract BotResponse handle(Update update);
}
