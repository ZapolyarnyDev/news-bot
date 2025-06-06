package io.github.zapolyarnydev.handler.callback.subscribe;

import io.github.zapolyarnydev.action.EditAction;
import io.github.zapolyarnydev.action.TelegramAction;
import io.github.zapolyarnydev.handler.CallbackHandler;
import io.github.zapolyarnydev.service.message.KeyboardService;
import io.github.zapolyarnydev.service.message.MessageService;
import io.github.zapolyarnydev.service.news.NewsSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
public final class SubscribeHandler extends CallbackHandler {
    @Autowired
    private NewsSubscriptionService subscriptionService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private KeyboardService keyboardService;

    public SubscribeHandler() {
        super(List.of("main_subscribe"));
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

        if(subscriptionService.isSubscribed(chatId)){
            editMessage.setText(messageService.getMessage("already-subscribed"));
        } else {
            editMessage.setText(messageService.getMessage("use-subscribe"));
            subscriptionService.subscribe(chatId);
        }
        editMessage.setReplyMarkup(keyboardService.getMainKeyboard(chatId));

        List<TelegramAction> response = new ArrayList<>();
        if(messageId != null) response.add(new EditAction(editMessage));
        return response;
    }
}
