package dev.asror.botgame.config;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import dev.asror.botgame.domain.UserDomain;
import dev.asror.botgame.response.Response;
import dev.asror.botgame.service.UserService;
import dev.asror.botgame.state.DefaultState;
import dev.asror.botgame.state.RegistrationState;
import dev.asror.botgame.state.State;
import dev.asror.botgame.utils.BaseUtils;
import dev.asror.botgame.utils.factory.SendMessageFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
public class InitializerConfiguration {
    private final UserService userService;

    private final Map<Long, State> userState;
    private final SendMessageFactory sendMessageFactory;

    public void init() {
        Response<List<UserDomain>> response = userService.getAllUsers();
        if (!response.isSuccess()) {
            System.err.println(response.getDeveloperErrorMessage());
            System.exit(-1);
        } else {
            List<UserDomain> users = response.getBody();
            String restarted = "Bot qayta ishga tushdi";
            users.forEach((user) -> {
                userState.put(user.getChatId(), DefaultState.MAIN_STATE);
                CompletableFuture.runAsync(() -> {
                            TelegramBot bot = TelegramBotConfiguration.get();
                            Long chatId = user.getChatId();
                            bot.execute(sendMessageFactory.sendMessageWithMainMenu(chatId, restarted));
                            if (Objects.isNull(user.getPhoneNumber())) {
                                bot.execute(new SendMessage(chatId, BaseUtils.NO_REGISTER));
                            }
                        }
                );
            });
        }
    }
}
