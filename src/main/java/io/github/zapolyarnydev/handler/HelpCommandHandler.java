package io.github.zapolyarnydev.handler;

import io.github.zapolyarnydev.response.BotResponse;
import io.github.zapolyarnydev.service.MessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class HelpCommandHandler extends CommandHandler{
    private final MessageService messageService;

    public HelpCommandHandler(MessageService service) {
        super("/help");
        this.messageService = service;
    }

    @Override
    public BotResponse handle(Update update) {
        var chat = update.getMessage().getChat();

        String helpText = messageService.getMessage("help");
        String responseText = helpText + Stream.of("subscribe", "unsubscribe", "categories", "schedule", "mysubscription")
                .map(code -> messageService.getMessage("help." + code))
                .collect(Collectors.joining("\n"));

        return new BotResponse(chat.getId(), responseText);
    }
}
