package dev.asror.botgame.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import dev.asror.botgame.config.TelegramBotConfiguration;
import dev.asror.botgame.processors.message.DefaultMessageProcessor;
import dev.asror.botgame.processors.message.RegistrationProcessor;
import dev.asror.botgame.service.UserService;
import dev.asror.botgame.state.DefaultState;
import dev.asror.botgame.state.RegistrationState;
import dev.asror.botgame.state.State;
import dev.asror.botgame.utils.BaseUtils;
import dev.asror.botgame.utils.factory.ReplyKeyboardMarkupFactory;
import dev.asror.botgame.utils.factory.SendMessageFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class MessageHandler implements Handler {
    private final TelegramBot bot = TelegramBotConfiguration.get();
    private final Map<Long, State> userState;
    private final DefaultMessageProcessor defaultMessageProcessor;
    private final ReplyKeyboardMarkupFactory replyKeyboardMarkupFactory;
    private final UserService userService;
    private final RegistrationProcessor registrationProcessor;
    private final SendMessageFactory sendMessageFactory;

    @Override
    public void handle(Update update) {
        Message message = update.message();
        Chat chat = message.chat();
        Long chatID = chat.id();
        State state = userState.get(chatID);
        if ( state == null ) startRegister(message);
        else if ( state instanceof RegistrationState registrationState )
            registrationProcessor.process(update, registrationState);
        else if ( state instanceof DefaultState defaultState )
            defaultMessageProcessor.process(update, defaultState);
    }

    private void startRegister(@NonNull Message message) {

        SendMessage sendMessage;
        Chat chat = message.chat();

        if (message.text().equalsIgnoreCase("/start")) {
            String lastName = chat.lastName();
            String firstName = chat.firstName();

            String fullName = firstName + " " + (Objects.nonNull(lastName) ? lastName : "");
            String messageText = """
                             Assalomu alaykum %s
                             Botdan ro'yxatdan o'tish uchun telefon raqamingizni kiriting! 📞
                             """.formatted(fullName);

            sendMessage = new SendMessage(chat.id(), messageText);
            sendMessage.replyMarkup(replyKeyboardMarkupFactory.
                    phoneNumberAndSkip());

            userState.put(chat.id(), RegistrationState.PHONE_NUMBER);
            userService.save(chat, fullName);
        } else {
            sendMessage = new SendMessage(chat.id(), "Noto'g'ri buyruq kiritildi❌\nBotdan to'liq foydalanish uchun ➡️ /start komandasini kiriting!");
        }

        bot.execute(sendMessage);
    }
}
