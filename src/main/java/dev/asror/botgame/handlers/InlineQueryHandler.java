package dev.asror.botgame.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineQueryResult;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.model.request.InputTextMessageContent;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import dev.asror.botgame.config.TelegramBotConfiguration;
import dev.asror.botgame.state.State;
import dev.asror.botgame.utils.factory.InlineKeyboardFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class InlineQueryHandler implements Handler {
    private final TelegramBot bot = TelegramBotConfiguration.get();
    private final Map<Long, State> userState;
    private final InlineKeyboardFactory inlineKeyboardFactory;

    @Override
    public void handle(Update update) {

        InlineQuery inlineQuery = update.inlineQuery();
        Long chatId = inlineQuery.from().id();
//        State state = userState.get(chatID);

        String query = inlineQuery.query();
        String inlineQueryId = inlineQuery.id();

        InputTextMessageContent messageContent = new InputTextMessageContent("X, 0 o'yini");

        InlineQueryResultArticle article = new InlineQueryResultArticle("1", "Title", messageContent);

        System.out.println(query);
        article.description("X, 0");
        article.replyMarkup(inlineKeyboardFactory.rateButtons());
        @SuppressWarnings("rawtypes")
        InlineQueryResult results = article;

        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery(inlineQueryId, results);
        bot.execute(answerInlineQuery);
    }
}
