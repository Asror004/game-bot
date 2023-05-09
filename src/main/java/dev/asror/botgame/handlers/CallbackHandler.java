package dev.asror.botgame.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import dev.asror.botgame.config.TelegramBotConfiguration;
import dev.asror.botgame.processors.callback.DefaultCallbackProcessor;
import dev.asror.botgame.state.DefaultState;
import dev.asror.botgame.state.State;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class CallbackHandler implements Handler {
    private final TelegramBot bot = TelegramBotConfiguration.get();
    private final Map<Long, State> userState;
    private final DefaultCallbackProcessor defaultCallbackProcessor;


    @Override
    public void handle(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        Long chatID = callbackQuery.message().chat().id();
        State state = userState.get(chatID);
        if ( state instanceof DefaultState defaultState) {
            defaultCallbackProcessor.process(update, defaultState);
        }
    }
}
