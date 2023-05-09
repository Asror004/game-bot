package dev.asror.botgame.processors;

import com.pengrad.telegrambot.model.Update;

public interface Processor<S> {
    void process(Update update, S state);
}
