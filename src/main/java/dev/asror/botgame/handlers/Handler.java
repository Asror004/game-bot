package dev.asror.botgame.handlers;

import com.pengrad.telegrambot.model.Update;

public interface Handler {
    void handle(Update update);
}
