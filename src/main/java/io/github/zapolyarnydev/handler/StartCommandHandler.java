package io.github.zapolyarnydev.handler;

import io.github.zapolyarnydev.dto.StartCommandDTO;
import io.github.zapolyarnydev.response.BotResponse;
import io.github.zapolyarnydev.service.StartCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {
    private final StartCommandService service;

    public BotResponse handle(Update update) {
        var message = update.getMessage();
        var chat = message.getChat();

        var dto = new StartCommandDTO(
                chat.getId(),
                chat.getUserName(),
                chat.getFirstName()
        );

        String responseText = service.processStartCommand(dto);
        return new BotResponse(chat.getId(), responseText);
    }
}
