package dev.asror.botgame.processors.callback;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import dev.asror.botgame.config.TelegramBotConfiguration;
import dev.asror.botgame.domain.TicTacToe;
import dev.asror.botgame.domain.UserDomain;
import dev.asror.botgame.processors.Processor;
import dev.asror.botgame.repository.UserRepository;
import dev.asror.botgame.service.TicTacToeService;
import dev.asror.botgame.service.UserService;
import dev.asror.botgame.state.State;
import dev.asror.botgame.state.Status;
import dev.asror.botgame.state.TicTacToeState;
import dev.asror.botgame.utils.BaseUtils;
import dev.asror.botgame.utils.factory.InlineKeyboardFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TicTacToeWithFriendsCallbackProcessor implements Processor<TicTacToeState>{
    private final TelegramBot bot = TelegramBotConfiguration.get();
    private final UserService userService;
    private final Map<Long, State> userState;
    private final InlineKeyboardFactory inlineKeyboardFactory;
    private final Map<String, TicTacToe> ticTacToes;
    private final Map<String, Map<Long, TicTacToeState>> ticTacToeState;

    @Override
    public void process(Update update, TicTacToeState state) {
        CallbackQuery callbackQuery = update.callbackQuery();
        Message message = callbackQuery.message();
        String data = callbackQuery.data();
        String ticTacToeId = data.split("\\|")[1];

        User chat = callbackQuery.from();

        long chatId = Objects.nonNull(message) ? message.chat().id() : chat.id();

        if (data.startsWith("start")) {
            if (Objects.isNull(userState.get(chatId)))
                saveUser(chat, ticTacToeId);

            if (state == null){
                EditMessageReplyMarkup editMessageReplyMarkup =
                        new EditMessageReplyMarkup(callbackQuery.inlineMessageId());
                editMessageReplyMarkup.replyMarkup(inlineKeyboardFactory.ticTacToeStartButtons());

                bot.execute(editMessageReplyMarkup);

                started(chat, ticTacToeId);
            } else if (state.equals(TicTacToeState.SEND)) {
                AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(callbackQuery.id());
                answerCallbackQuery.text("Sherigingiz start bosishini kuting!");
                answerCallbackQuery.showAlert(true);
                bot.execute(answerCallbackQuery);
            }
        }
    }

    private void started(User chat, String ticTacToeId) {
        Long chatId = chat.id();

        long player1 = ticTacToeState.get(ticTacToeId).keySet()
                        .stream().findFirst().orElseThrow();

        Map<Long, TicTacToeState> ticTaToeUserState = ticTacToeState.get(ticTacToeId);
        ticTaToeUserState.put(chatId, TicTacToeState.PLAY);
        ticTaToeUserState.put(player1, TicTacToeState.PLAY);

        ticTacToes.put(ticTacToeId, TicTacToe.childBuilder()
                .id(ticTacToeId)
                .player1(player1)
                .player2(chatId)
                .status(Status.PLAY).build());
    }

    private void saveUser(User chat, String ticTacToeId) {
        Long chatId = chat.id();
        ticTacToeState.get(ticTacToeId).put(chatId, TicTacToeState.START);
        userService.save(chatId, BaseUtils.getFullName(chat.firstName(), chat.lastName()));
    }
}
