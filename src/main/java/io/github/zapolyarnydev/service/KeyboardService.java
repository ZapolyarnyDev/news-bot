package io.github.zapolyarnydev.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeyboardService {
    private final MessageService messageService;

    public InlineKeyboardMarkup getMainKeyboard() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        String[] commands = {"subscribe", "unsubscribe", "categories", "schedule", "mysubscription"};
        for(String s : commands){
            rows.add(List.of(initializeButton(messageService.getMessage("help." + s), "main_" + s)));
        }
        return new InlineKeyboardMarkup(rows);
    }

    private InlineKeyboardButton initializeButton(String text, String callback){
        var button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callback);
        return button;
    }
}
