package io.github.zapolyarnydev.handler.callback;

import io.github.zapolyarnydev.action.EditAction;
import io.github.zapolyarnydev.action.TelegramAction;
import io.github.zapolyarnydev.handler.CallbackHandler;
import io.github.zapolyarnydev.service.KeyboardService;
import io.github.zapolyarnydev.service.MessageService;
import io.github.zapolyarnydev.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
public class SubscribeInfoHandler extends CallbackHandler {
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private KeyboardService keyboardService;

    public SubscribeInfoHandler() {
        super("main_mysubscription");
    }

    @Override
    public List<TelegramAction> handle(Update update) {
        var message = update.getCallbackQuery().getMessage();
        var chatId = message.getChatId();

        Integer messageId = null;

        if(message instanceof Message message1){
            messageId = message1.getMessageId();
        }

        var editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);

        String subscribeEnableInfo = subscriptionService.isSubscribed(chatId)
                ? messageService.getMessage("has-subscribe")
                : messageService.getMessage("no-subscribe");

        editMessage.setText(messageService.getMessage("subscribe-info", subscribeEnableInfo));
        editMessage.setReplyMarkup(keyboardService.getMainKeyboard(chatId));

        List<TelegramAction> response = new ArrayList<>();
        if(messageId != null) response.add(new EditAction(editMessage));
        return response;
    }
}
