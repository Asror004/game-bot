package dev.asror.botgame.handlers;

import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Update;
import dev.asror.botgame.processors.inlineQuery.TicTacToeInlineProcessor;
import dev.asror.botgame.state.State;
import dev.asror.botgame.state.TicTacToeState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class InlineQueryHandler implements Handler {
    private final Map<Long, State> userState;
    private final List<String> codes;
    private final TicTacToeInlineProcessor ticTacToeProcessor;

    @Override
    public void handle(Update update) {
        InlineQuery inlineQuery = update.inlineQuery();
        Long chatId = inlineQuery.from().id();
        String query = inlineQuery.query();

        if (codes.contains(query)){
            codes.remove(query);

            if (Objects.nonNull(userState.get(chatId))) {
                ticTacToeProcessor.process(update, TicTacToeState.SEND);
            }
        }
    }
}
