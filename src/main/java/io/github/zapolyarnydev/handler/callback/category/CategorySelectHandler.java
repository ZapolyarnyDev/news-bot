package io.github.zapolyarnydev.handler.callback.category;

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
public class CategorySelectHandler extends CallbackHandler {
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private KeyboardService keyboardService;

    public CategorySelectHandler() {
        super(List.of("category-select_sport", "category-select_economy",
                "category-select_it", "category-select_politics"));
    }

    @Override
    public List<TelegramAction> handle(Update update) {
        String category = update.getCallbackQuery().getData().split("_")[1];
        var message = update.getCallbackQuery().getMessage();
        var chatId = message.getChatId();

        Integer messageId = null;

        if(message instanceof Message message1){
            messageId = message1.getMessageId();
        }

        var editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);

        if(subscriptionService.hasSubscribeOnCategory(chatId, category)){
            subscriptionService.removeCategory(chatId, category);
            editMessage.setText(messageService.getMessage("category-disabled", messageService.getMessage("category-menu." + category)));
        } else {
            subscriptionService.addCategory(chatId, category);
            editMessage.setText(messageService.getMessage("category-enabled", messageService.getMessage("category-menu." + category)));
        }
        editMessage.setReplyMarkup(keyboardService.getCategoryKeyboard(chatId));
        List<TelegramAction> response = new ArrayList<>();
        if(messageId != null) response.add(new EditAction(editMessage));
        return response;
    }
}
