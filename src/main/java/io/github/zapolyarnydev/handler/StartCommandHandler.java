package io.github.zapolyarnydev.handler;

import io.github.zapolyarnydev.response.BotResponse;
import io.github.zapolyarnydev.service.MessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartCommandHandler extends CommandHandler {
    private final MessageService service;
    public StartCommandHandler(MessageService service) {
        super("/start");
        this.service = service;
    }

    public BotResponse handle(Update update) {
        var message = update.getMessage();
        var chat = message.getChat();

        String responseText = service.getMessage("start", chat.getFirstName());
        return new BotResponse(chat.getId(), responseText);
    }
}
