package io.github.zapolyarnydev.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class CommandHandler extends UpdateHandler{

    private final String command;

}
