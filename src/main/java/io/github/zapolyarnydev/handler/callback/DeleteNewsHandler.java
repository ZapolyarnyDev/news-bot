package io.github.zapolyarnydev.handler.callback;

import io.github.zapolyarnydev.action.DeleteAction;
import io.github.zapolyarnydev.action.SendAction;
import io.github.zapolyarnydev.action.TelegramAction;
import io.github.zapolyarnydev.handler.CallbackHandler;
import io.github.zapolyarnydev.service.message.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
public class DeleteNewsHandler extends CallbackHandler {

    @Autowired
    private MessageService messageService;

    public DeleteNewsHandler() {
        super(List.of("delete_news"));
    }

    @Override
    public List<TelegramAction> handle(Update update) {
        var message = update.getCallbackQuery().getMessage();
        var chatId = message.getChatId();

        Integer messageId = null;

        if(message instanceof Message message1){
            messageId = message1.getMessageId();
        }

        var deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);

        List<TelegramAction> response = new ArrayList<>();
        if(messageId != null){
            response.add(new DeleteAction(deleteMessage));
            deleteMessage.setMessageId(messageId);
        }
        return response;
    }
}
