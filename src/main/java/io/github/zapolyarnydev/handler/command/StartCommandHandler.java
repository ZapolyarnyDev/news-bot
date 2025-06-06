package io.github.zapolyarnydev.handler.command;

import io.github.zapolyarnydev.action.SendAction;
import io.github.zapolyarnydev.action.TelegramAction;
import io.github.zapolyarnydev.handler.CommandHandler;
import io.github.zapolyarnydev.service.message.KeyboardService;
import io.github.zapolyarnydev.service.message.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public final class StartCommandHandler extends CommandHandler {
    @Autowired
    private MessageService messageService;
    @Autowired
    private KeyboardService keyboardService;

    public StartCommandHandler() {
        super("/start");
    }

    public List<TelegramAction> handle(Update update) {
        var message = update.getMessage();
        var chat = message.getChat();
        var sendMessage = new SendMessage();

        String responseText = messageService.getMessage("start", chat.getFirstName());

        sendMessage.setChatId(chat.getId());
        sendMessage.setText(responseText);
        sendMessage.setReplyMarkup(keyboardService.getMainKeyboard(chat.getId()));

        return List.of(new SendAction(sendMessage));
    }


}
