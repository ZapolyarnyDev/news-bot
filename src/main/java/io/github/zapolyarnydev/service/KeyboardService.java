package io.github.zapolyarnydev.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeyboardService {
    private final MessageService messageService;
    private final SubscriptionService subscriptionService;

    public InlineKeyboardMarkup getMainKeyboard(Long chatId, String... excludedCommands) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        var commands = getActualMainCommands(chatId);

        for(String excluded : excludedCommands){
            commands.remove(excluded);
        }

        for(String s : commands){
            rows.add(List.of(initializeButton(messageService.getMessage("main-menu." + s), "main_" + s)));
        }

        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup getCategoryKeyboard(Long chatId){
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        var commands = new ArrayList<>(List.of("sport", "economy", "it", "politics"));
        String unsubCategory = "     " + messageService.getMessage("unsub-category");
        String subCategory = "     " +  messageService.getMessage("sub-category");
        for(String s : commands){
            String message = subscriptionService.hasSubscribeOnCategory(chatId, s)
                    ? messageService.getMessage("category-menu." + s) + unsubCategory
                    : messageService.getMessage("category-menu." + s) + subCategory;
            rows.add(List.of(initializeButton(message, "category-select_" + s)));
        }
        rows.add(List.of(initializeButton(messageService.getMessage("return-back"), "main_open")));
        return new InlineKeyboardMarkup(rows);
    }



    private List<String> getActualMainCommands(Long chatId){
        List<String> actualCommands = new ArrayList<>();
        if(!subscriptionService.chatExists(chatId) || !subscriptionService.isSubscribed(chatId)) actualCommands.add("subscribe");
        else actualCommands.add("unsubscribe");
        actualCommands.addAll(List.of("categories", "schedule", "mysubscription"));
        return actualCommands;
    }


    private InlineKeyboardButton initializeButton(String text, String callback){
        var button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callback);
        return button;
    }
}
