package dev.asror.botgame.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import dev.asror.botgame.config.TelegramBotConfiguration;
import dev.asror.botgame.processors.callback.DefaultCallbackProcessor;
import dev.asror.botgame.processors.callback.TicTacToeWithAICallbackProcessor;
import dev.asror.botgame.processors.callback.TicTacToeWithFriendsCallbackProcessor;
import dev.asror.botgame.state.DefaultState;
import dev.asror.botgame.state.State;
import dev.asror.botgame.state.TicTacToeState;
import dev.asror.botgame.state.TicTacToeVsAI;
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
    private final Map<String, Map<Long, TicTacToeState>> ticTacToeState;
    private final Map<String, TicTacToeVsAI> ticTacToeWithAIState;
    private final TicTacToeWithAICallbackProcessor ticTacToeWithAICallbackProcessor;


    @Override
    public void handle(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        Message message = callbackQuery.message();

        long chatId = Objects.nonNull(message) ? message.chat().id() : callbackQuery.from().id();

        String id;
        String[] data = callbackQuery.data().split("\\|");
        try {
            id = data[1];
        } catch (Exception e){
            id = null;
        }

        TicTacToeVsAI ticTacToeVsAI = ticTacToeWithAIState.get(id);
        Map<Long, TicTacToeState> stateMap = ticTacToeState.get(id);
        ticTacToeWithAIState.get(id);
        State state = userState.get(chatId);

        if (Objects.nonNull(stateMap) || Objects.nonNull(ticTacToeVsAI) || data[0].equals("start"))
            if (Objects.nonNull(stateMap))
                ticTacToeProcessor.process(update, stateMap.get(chatId));

            if (Objects.nonNull(ticTacToeVsAI))
                ticTacToeWithAICallbackProcessor.process(update, ticTacToeVsAI);
        else if (Objects.nonNull(id))
            ticTacToeProcessor.sendAlert("Bu o'yin tugagan yoki mavjud emas!", callbackQuery.id());
        else if (state instanceof DefaultState defaultState) {
            defaultCallbackProcessor.process(update, defaultState);
        }
    }
}
