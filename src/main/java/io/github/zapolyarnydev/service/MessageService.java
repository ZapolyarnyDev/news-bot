package io.github.zapolyarnydev.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageSource messageSource;

    public String getMessage(String id, Object... args){
        return messageSource.getMessage(id, args, Locale.forLanguageTag("ru"));
    }

}
