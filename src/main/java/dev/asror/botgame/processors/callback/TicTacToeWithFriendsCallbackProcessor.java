package dev.asror.botgame.processors.callback;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import dev.asror.botgame.config.TelegramBotConfiguration;
import dev.asror.botgame.domain.UserDomain;
import dev.asror.botgame.processors.Processor;
import dev.asror.botgame.repository.UserRepository;
import dev.asror.botgame.service.UserService;
import dev.asror.botgame.state.State;
import dev.asror.botgame.state.TicTacToeState;
import dev.asror.botgame.utils.BaseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TicTacToeWithFriendsCallbackProcessor implements Processor<TicTacToeState> {
    private final TelegramBot bot = TelegramBotConfiguration.get();
    private final UserRepository userRepository;
    private final UserService userService;
    private final Map<Long, State> userState;

    @Override
    public void process(Update update, TicTacToeState state) {
        CallbackQuery callbackQuery = update.callbackQuery();
        User chat = callbackQuery.from();
        Long chatId = chat.id();
        String data = callbackQuery.data();

        if (data.equals("start") && state.equals(TicTacToeState.START)){
            UserDomain user = userRepository.findById(chatId).orElse(null);

            if (Objects.isNull(user)){
                userService.save(chatId, BaseUtils.getFullName(chat.firstName(), chat.lastName()));
            }

            System.out.println(chatId);
            userState.put(chatId, TicTacToeState.PLAY);
        } else if (data.equals("start") && state.equals(TicTacToeState.SEND)) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(callbackQuery.id());
            answerCallbackQuery.text("Sherigingiz start bosishini kuting!");
            bot.execute(answerCallbackQuery);
        }
    }
}
