package io.github.zapolyarnydev.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Getter
public abstract class CommandHandler {

    private final String command;

    public abstract SendMessage handle(Update update);
}
