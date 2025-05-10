package io.github.zapolyarnydev.service;

import io.github.zapolyarnydev.dto.StartCommandDTO;
import org.jvnet.hk2.annotations.Service;
import org.springframework.stereotype.Component;

@Service
@Component
public class StartCommandService {

    public String processStartCommand(StartCommandDTO dto) {
        String greeting = dto.firstName() != null ?
                "Привет, " + dto.firstName() + "!" :
                "Привет!";
        return greeting + "\n\nЯ бот для подписки на новости. Используй /help для списка команд.";
    }
}
