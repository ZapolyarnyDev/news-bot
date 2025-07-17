package io.github.zapolyarnydev.bot;

import io.github.zapolyarnydev.action.TelegramAction;
import io.github.zapolyarnydev.configuration.BotProperties;
import io.github.zapolyarnydev.handler.callback.subscribe.SubscribeHandler;
import io.github.zapolyarnydev.handler.command.StartCommandHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование обработки действий в боте")
public class NewsBotHandleTest {

    private NewsBot newsBot;

    @Mock
    private StartCommandHandler startCommandHandler;
    @Mock
    private SubscribeHandler subscribeHandler;

    @Mock
    private TelegramAction telegramAction;

    @Test
    @DisplayName("Обработка команды /start")
    public void shouldCallStartCommandHandler_WhenStartCommandReceived() throws TelegramApiException {
        newsBot = buildBot();

        ReflectionTestUtils.setField(newsBot, "commandHandlers", List.of(startCommandHandler));
        ReflectionTestUtils.setField(newsBot, "callbackHandlers", List.of());

        var update = getTestUpdate(null, "/start");

        when(startCommandHandler.getCommand()).thenReturn("/start");

        newsBot.onUpdateReceived(update);

        verify(startCommandHandler).handle(update);
    }

    @Test
    @DisplayName("Обработка подписки на новости")
    public void shouldCallSubscribeHandler_WhenSubscribeCallbackReceived(){
        newsBot = buildBot();

        ReflectionTestUtils.setField(newsBot, "commandHandlers", List.of());
        ReflectionTestUtils.setField(newsBot, "callbackHandlers", List.of(subscribeHandler));

        var update = getTestUpdate("main_subscribe", "testText");

        when(subscribeHandler.getCallbackData()).thenReturn(List.of("main_subscribe"));

        newsBot.onUpdateReceived(update);

        verify(subscribeHandler).handle(update);
    }

    @Test
    @DisplayName("Применение действия Telegram дважды при двух обработчиках")
    public void shouldApplyTelegramActionTwice_WhenTwoHandlersReturnActions() throws TelegramApiException {
        newsBot = buildBot();

        ReflectionTestUtils.setField(newsBot, "commandHandlers", List.of(startCommandHandler));
        ReflectionTestUtils.setField(newsBot, "callbackHandlers", List.of(subscribeHandler));

        var update = getTestUpdate("main_subscribe", "testText");
        var update1 = getTestUpdate(null, "/start");

        when(startCommandHandler.getCommand()).thenReturn("/start");
        when(subscribeHandler.getCallbackData()).thenReturn(List.of("main_subscribe"));

        when(startCommandHandler.handle(update1)).thenReturn(List.of(telegramAction));
        when(subscribeHandler.handle(update)).thenReturn(List.of(telegramAction));

        newsBot.onUpdateReceived(update);
        newsBot.onUpdateReceived(update1);

        verify(telegramAction, times(2)).apply(newsBot);
    }

    public Update getTestUpdate(String callbackData, String messageText){
        Update update = new Update();

        var query = new CallbackQuery();
        query.setData(callbackData);

        Message message = new Message();
        message.setMessageId(1);

        if(messageText != null) message.setText(messageText);

        message.setChat(new Chat(1L, "private"));

        if(callbackData != null){
            query.setMessage(message);
            update.setCallbackQuery(query);
            return update;
        }
        update.setMessage(message);

        return update;
    }

    public NewsBot buildBot(){
        var properties = new BotProperties();
        properties.setToken("fakeToken");
        properties.setUsername("fakeUsername");

        return new NewsBot(properties);
    }

    public NewsBot buildBot(String token, String username){
        var properties = new BotProperties();
        properties.setToken(token);
        properties.setUsername(username);

        return new NewsBot(properties);
    }

}
