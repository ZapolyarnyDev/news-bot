package io.github.zapolyarnydev.handler;

import io.github.zapolyarnydev.service.KeyboardService;
import io.github.zapolyarnydev.service.MessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartCommandHandler extends CommandHandler {
    private final MessageService messageService;
    private final KeyboardService keyboardService;

    public StartCommandHandler(MessageService messageService, KeyboardService keyboardService) {
        super("/start");
        this.messageService = messageService;
        this.keyboardService = keyboardService;
    }

    public SendMessage handle(Update update) {
        var message = update.getMessage();
        var chat = message.getChat();
        var sendMessage = new SendMessage();

        String responseText = messageService.getMessage("start", chat.getFirstName());

        sendMessage.setChatId(chat.getId());
        sendMessage.setText(responseText);
        sendMessage.setReplyMarkup(keyboardService.getMainKeyboard());

        return sendMessage;
    }


}
