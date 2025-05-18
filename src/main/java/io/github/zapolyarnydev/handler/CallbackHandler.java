package io.github.zapolyarnydev.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
public abstract class CallbackHandler extends UpdateHandler{

    private final String callbackData;

}
