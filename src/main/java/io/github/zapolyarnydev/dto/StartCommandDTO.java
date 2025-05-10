package io.github.zapolyarnydev.dto;

public record StartCommandDTO(Long chatId, String username, String firstName) implements CommandDTO{
}
