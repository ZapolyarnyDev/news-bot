package io.github.zapolyarnydev.handler.callback;

import io.github.zapolyarnydev.action.SendAction;
import io.github.zapolyarnydev.action.TelegramAction;
import io.github.zapolyarnydev.handler.CallbackHandler;
import io.github.zapolyarnydev.service.KeyboardService;
import io.github.zapolyarnydev.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
public class SetupSettingsHandler extends CallbackHandler {
    @Autowired
    private MessageService messageService;
    @Autowired
    private KeyboardService keyboardService;

    public SetupSettingsHandler() {
        super(List.of("setup_settings"));
    }

    @Override
    public List<TelegramAction> handle(Update update) {
        var message = update.getCallbackQuery().getMessage();
        var chatId = message.getChatId();


        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        sendMessage.setText(messageService.getMessage("start", update.getCallbackQuery().getFrom().getFirstName()));
        sendMessage.setReplyMarkup(keyboardService.getMainKeyboard(chatId));

        List<TelegramAction> response = new ArrayList<>();
        response.add(new SendAction(sendMessage));
        return response;

    }
}
