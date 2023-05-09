package dev.asror.botgame.processors.message;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import dev.asror.botgame.config.TelegramBotConfiguration;
import dev.asror.botgame.processors.Processor;
import dev.asror.botgame.state.DefaultState;
import dev.asror.botgame.state.RegistrationState;
import dev.asror.botgame.state.State;
import dev.asror.botgame.utils.BaseUtils;
import dev.asror.botgame.utils.factory.ReplyKeyboardMarkupFactory;
import dev.asror.botgame.utils.factory.SendMessageFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DefaultMessageProcessor implements Processor<DefaultState> {
    private final TelegramBot bot = TelegramBotConfiguration.get();
    private final Map<Long, State> userState;
    private final ReplyKeyboardMarkupFactory replyKeyboardMarkupFactory;
    private final SendMessageFactory sendMessageFactory;

    @Override
    public void process(Update update, DefaultState state) {
        Message message = update.message();
        String text = message.text();
        Long chatID = message.chat().id();
        String language = message.from().languageCode();
        if (state.equals(DefaultState.DELETE)) {
            bot.execute(new DeleteMessage(chatID, message.messageId()));
        } else if (state.equals(DefaultState.MAIN_STATE)) {
            if (Objects.nonNull(text)){
                if (text.equals("/start")) {
                    userState.put(chatID, DefaultState.MAIN_STATE);
                    bot.execute(sendMessageFactory.sendMessageWithMainMenu(chatID, BaseUtils.MENU));
                } else if (text.equals("/fullreg")){
                    SendMessage sendMessage = new SendMessage(chatID, "Botdan ro'yxatdan o'tish uchun telefon raqamingizni kiriting! 📞");
                    sendMessage.replyMarkup(replyKeyboardMarkupFactory.phoneNumberAndSkip());
                    bot.execute(sendMessage);

                    userState.put(chatID, RegistrationState.PHONE_NUMBER);
                }
            }

        } else {
            bot.execute(new DeleteMessage(chatID, message.messageId()));
        }
    }
}
