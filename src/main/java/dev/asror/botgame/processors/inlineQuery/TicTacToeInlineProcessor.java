package dev.asror.botgame.processors.inlineQuery;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineQueryResult;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.model.request.InputTextMessageContent;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import dev.asror.botgame.config.TelegramBotConfiguration;
import dev.asror.botgame.processors.Processor;
import dev.asror.botgame.state.TicTacToeState;
import dev.asror.botgame.utils.factory.InlineKeyboardFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicTacToeInlineProcessor implements Processor<TicTacToeState> {
    private final InlineKeyboardFactory inlineKeyboardFactory;
    private final TelegramBot bot = TelegramBotConfiguration.get();

    @Override
    public void process(Update update, TicTacToeState state) {
        if (state.equals(TicTacToeState.SEND)){
            InlineQuery inlineQuery = update.inlineQuery();
            String inlineQueryId = inlineQuery.id();

            InputTextMessageContent messageContent = new InputTextMessageContent("Boshlash");
            InlineQueryResultArticle article = new InlineQueryResultArticle("1", "Tic Tac Toe", messageContent);

            article.description("X, 0");
            article.replyMarkup(inlineKeyboardFactory.startButton());

            @SuppressWarnings("rawtypes")
            InlineQueryResult results = article;

            AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery(inlineQueryId, results);
            bot.execute(answerInlineQuery);
        }
    }
}
