package io.github.zapolyarnydev.service.message;

import io.github.zapolyarnydev.model.News;
import io.github.zapolyarnydev.model.NewsFrequency;
import io.github.zapolyarnydev.service.news.NewsCategoryService;
import io.github.zapolyarnydev.service.news.NewsFrequencyService;
import io.github.zapolyarnydev.service.news.NewsSubscriptionService;
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

    private final NewsFrequencyService newsFrequencyService;

    private final NewsSubscriptionService subscriptionService;

    private final NewsCategoryService categoryService;

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
        var commands = new ArrayList<>(List.of("sport", "economy", "it"));
        String unsubCategory = "     " + messageService.getMessage("check");
        String subCategory = "     " +  messageService.getMessage("cross");
        for(String s : commands){
            String message = categoryService.hasSubscribeOnCategory(chatId, s)
                    ? messageService.getMessage("category-menu." + s) + unsubCategory
                    : messageService.getMessage("category-menu." + s) + subCategory;
            rows.add(List.of(initializeButton(message, "category-select-" + s)));
        }
        rows.add(List.of(initializeButton(messageService.getMessage("return-back"), "main_open")));
        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup getNewsKeyboard(News news){
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        var urlButton = initializeButton(messageService.getMessage("to-source"), null);
        urlButton.setUrl(news.url());

        rows.add(List.of(urlButton));
        rows.add(List.of(initializeButton(messageService.getMessage("setup-settings"),  "setup_settings")));
        rows.add(List.of(initializeButton(messageService.getMessage("delete-news"),  "delete_news")));
        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup getScheduleKeyboard(Long chatId){
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for(var frequency : NewsFrequency.values()){
            String frequencyId = frequency.name().toLowerCase();

            if(newsFrequencyService.getNewsFrequency(chatId) == frequency) continue;

            String message = messageService.getMessage("frequency-menu." + frequencyId);
            rows.add(List.of(initializeButton(message, "frequency-select-" + frequencyId)));
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
