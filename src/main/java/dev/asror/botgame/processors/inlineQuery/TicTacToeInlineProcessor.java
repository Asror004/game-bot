package dev.asror.botgame.processors.inlineQuery;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineQueryResult;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.model.request.InputTextMessageContent;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import dev.asror.botgame.config.TelegramBotConfiguration;
import dev.asror.botgame.processors.Processor;
import dev.asror.botgame.processors.callback.TicTacToeWithFriendsCallbackProcessor;
import dev.asror.botgame.service.UserService;
import dev.asror.botgame.state.TicTacToeState;
import dev.asror.botgame.utils.BaseUtils;
import dev.asror.botgame.utils.factory.InlineKeyboardFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TicTacToeInlineProcessor implements Processor<TicTacToeState> {
    private final InlineKeyboardFactory inlineKeyboardFactory;
    private final TelegramBot bot = TelegramBotConfiguration.get();
    private final TicTacToeWithFriendsCallbackProcessor ticTacToeWithFriendsCallbackProcessor;
    private final Map<String, Map<Long, TicTacToeState>> ticTacToeState;
    private final UserService userService;

    @Override
    public void process(Update update, TicTacToeState state) {
        if (state.equals(TicTacToeState.SENDING)) {
            InlineQuery inlineQuery = update.inlineQuery();
            String inlineQueryId = inlineQuery.id();

            InputTextMessageContent messageContent = new InputTextMessageContent("Boshlash uchun tugamni bosing \uD83D\uDC47");
            InlineQueryResultArticle article = new InlineQueryResultArticle("1", "Tic Tac Toe", messageContent);

            String id = UUID.randomUUID().toString();
            while (ticTacToeState.get(id) != null){
                id = UUID.randomUUID().toString();
            }

            article.description(BaseUtils.BUTTON_X + BaseUtils.BUTTON_0);
            article.replyMarkup(inlineKeyboardFactory.startButton(id));

            @SuppressWarnings("rawtypes")
            InlineQueryResult results = article;

            AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery(inlineQueryId, results);
            answerInlineQuery.cacheTime(0);

            bot.execute(answerInlineQuery);

            HashMap<Long, TicTacToeState> stateHashMap = new HashMap<>(2){{
                        put(inlineQuery.from().id(), TicTacToeState.SEND);
                    }};
            ticTacToeState.put(id, stateHashMap);

            User chat = update.inlineQuery().from();
            userService.updateFullName(BaseUtils.getFullName(chat.firstName(), chat.lastName()), chat.id());
        }
    }
}
