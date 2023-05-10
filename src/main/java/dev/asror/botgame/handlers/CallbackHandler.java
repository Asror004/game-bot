package dev.asror.botgame.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import dev.asror.botgame.config.TelegramBotConfiguration;
import dev.asror.botgame.processors.callback.DefaultCallbackProcessor;
import dev.asror.botgame.processors.callback.TicTacToeWithFriendsCallbackProcessor;
import dev.asror.botgame.state.DefaultState;
import dev.asror.botgame.state.State;
import dev.asror.botgame.state.TicTacToeState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class CallbackHandler implements Handler {
    private final TelegramBot bot = TelegramBotConfiguration.get();
    private final Map<Long, State> userState;
    private final DefaultCallbackProcessor defaultCallbackProcessor;
    private final TicTacToeWithFriendsCallbackProcessor ticTacToeProcessor;


    @Override
    public void handle(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        Message message = callbackQuery.message();
        Long chatID;

        if (Objects.nonNull(message))
            chatID = message.chat().id();
        else
            chatID = callbackQuery.from().id();

        State state = userState.get(chatID);

        String data = callbackQuery.data();
        if (data.equals("start") && !state.equals(TicTacToeState.SEND)){
            ticTacToeProcessor.process(update, TicTacToeState.START);
        } else if (state instanceof TicTacToeState ticTacToeState) {
            ticTacToeProcessor.process(update, ticTacToeState);
        } else if (state instanceof DefaultState defaultState) {
            defaultCallbackProcessor.process(update, defaultState);
        }
    }
}
