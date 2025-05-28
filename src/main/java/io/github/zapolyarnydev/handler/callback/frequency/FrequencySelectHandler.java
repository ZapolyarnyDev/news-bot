package io.github.zapolyarnydev.handler.callback.frequency;

import io.github.zapolyarnydev.action.EditAction;
import io.github.zapolyarnydev.action.TelegramAction;
import io.github.zapolyarnydev.handler.CallbackHandler;
import io.github.zapolyarnydev.model.NewsFrequency;
import io.github.zapolyarnydev.service.*;
import io.github.zapolyarnydev.service.news.NewsFrequencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
public class FrequencySelectHandler extends CallbackHandler {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private NewsFrequencyService newsFrequencyService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private KeyboardService keyboardService;

    public FrequencySelectHandler() {
        super(List.of("frequency-select-very_often", "frequency-select-often",
                "frequency-select-hourly", "frequency-select-sometimes",
                "frequency-select-rarely"));
    }

    @Override
    public List<TelegramAction> handle(Update update) {
        String frequency = update.getCallbackQuery().getData().split("-")[2];
        var message = update.getCallbackQuery().getMessage();
        var chatId = message.getChatId();

        Integer messageId = null;

        if(message instanceof Message message1){
            messageId = message1.getMessageId();
        }

        var editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);

        newsFrequencyService.setNewsFrequency(chatId, NewsFrequency.valueOf(frequency.toUpperCase()));

        editMessage.setText(messageService.getMessage("schedule-info", messageService.getMessage("frequency-menu." + frequency)));
        editMessage.setReplyMarkup(keyboardService.getScheduleKeyboard(chatId));

        List<TelegramAction> response = new ArrayList<>();
        if(messageId != null) response.add(new EditAction(editMessage));
        return response;
    }
}
