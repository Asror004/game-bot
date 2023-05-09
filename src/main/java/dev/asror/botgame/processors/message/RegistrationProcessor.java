package dev.asror.botgame.processors.message;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Contact;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import dev.asror.botgame.config.TelegramBotConfiguration;
import dev.asror.botgame.processors.Processor;
import dev.asror.botgame.service.UserService;
import dev.asror.botgame.state.DefaultState;
import dev.asror.botgame.state.RegistrationState;
import dev.asror.botgame.state.State;
import dev.asror.botgame.utils.BaseUtils;
import dev.asror.botgame.utils.factory.SendMessageFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RegistrationProcessor implements Processor<RegistrationState> {
    private final Map<Long, State> userState;
    private final UserService userService;
    private final SendMessageFactory sendMessageFactory;
    private final TelegramBot bot = TelegramBotConfiguration.get();

    @Override
    public void process(Update update, RegistrationState state) {
        Message message = update.message();
        Long chatId = message.chat().id();
        if (userState.get(chatId).equals(RegistrationState.PHONE_NUMBER)){
            Contact contact = message.contact();
            if (Objects.nonNull(contact)) {
                bot.execute(new SendMessage(chatId, "Ro'yxatdan muvaffiqaytli o'tdingiz! ☺️"));
                saveUser(chatId, contact);
            } else if (message.text().equals(BaseUtils.SKIP)){
                bot.execute(new SendMessage(chatId, BaseUtils.NO_REGISTER));
                saveUser(chatId, null);
            }
        }
    }

    private void saveUser(Long chatId, Contact contact){
        bot.execute(sendMessageFactory.sendMessageWithMainMenu(chatId, BaseUtils.MENU));
        userState.put(chatId, DefaultState.MAIN_STATE);
        userService.save(chatId, contact);
    }
}
