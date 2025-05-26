package io.github.zapolyarnydev.handler.callback.subscribe;

import io.github.zapolyarnydev.action.EditAction;
import io.github.zapolyarnydev.action.TelegramAction;
import io.github.zapolyarnydev.handler.CallbackHandler;
import io.github.zapolyarnydev.handler.callback.frequency.FrequencySelectHandler;
import io.github.zapolyarnydev.service.*;
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
    private NewsFrequencyService newsFrequencyService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private KeyboardService keyboardService;

    public SubscribeInfoHandler() {
        super(List.of("main_mysubscription"));
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

        String cross = messageService.getMessage("cross");
        String check = messageService.getMessage("check");

        boolean sportCategoryEnabled = categoryService.hasSubscribeOnCategory(chatId, "sport");
        boolean economyCategoryEnabled = categoryService.hasSubscribeOnCategory(chatId, "economy");
        boolean itCategoryEnabled = categoryService.hasSubscribeOnCategory(chatId, "it");
        boolean politicsCategoryEnabled = categoryService.hasSubscribeOnCategory(chatId, "politics");

        editMessage.setText(messageService.getMessage("subscribe-info",
                subscribeEnableInfo,
                messageService.getMessage("category-menu.sport"),
                sportCategoryEnabled ? check : cross,
                messageService.getMessage("category-menu.economy"),
                economyCategoryEnabled ? check : cross,
                messageService.getMessage("category-menu.it"),
                itCategoryEnabled ? check : cross,
                messageService.getMessage("category-menu.politics"),
                politicsCategoryEnabled ? check : cross,
                messageService.getMessage("frequency-menu." + newsFrequencyService.getNewsFrequency(chatId).name().toLowerCase())
        ));
        editMessage.setReplyMarkup(keyboardService.getMainKeyboard(chatId, "mysubscription"));

        List<TelegramAction> response = new ArrayList<>();
        if(messageId != null) response.add(new EditAction(editMessage));
        return response;
    }
}
