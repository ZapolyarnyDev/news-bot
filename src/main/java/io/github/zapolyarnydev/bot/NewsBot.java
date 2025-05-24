package io.github.zapolyarnydev.bot;

import io.github.zapolyarnydev.action.TelegramAction;
import io.github.zapolyarnydev.action.executor.ActionExecutor;
import io.github.zapolyarnydev.configuration.BotProperties;
import io.github.zapolyarnydev.handler.CallbackHandler;
import io.github.zapolyarnydev.handler.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class NewsBot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    @Autowired
    private List<CommandHandler> commandHandlers;
    @Autowired
    private List<CallbackHandler> callbackHandlers;

    public NewsBot(BotProperties botProperties) {
        super(botProperties.getToken());
        this.botProperties = botProperties;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleCommand(update);
        } else if (update.hasCallbackQuery()){
            handleCallback(update);
        }
    }

    private void handleCommand(Update update){
        String text = update.getMessage().getText();

        commandHandlers.stream()
                .filter(handler -> handler.getCommand().equals(text))
                .findFirst()
                .ifPresent(handler -> {
                    List<TelegramAction> actions = handler.handle(update);
                    executeActions(actions);
                });
    }

    private void handleCallback(Update update){
        String callbackData = update.getCallbackQuery().getData();
        callbackHandlers.stream()
                .filter(handler -> handler.getCallbackData().equals(callbackData))
                .findFirst()
                .ifPresent(handler -> {
                    List<TelegramAction> actions = handler.handle(update);
                    executeActions(actions);
                });
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    public void executeActions(List<TelegramAction> actions){
        actions.forEach(telegramAction -> {
            try {
                telegramAction.apply(this);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
