package dev.asror.botgame.processors.callback;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import dev.asror.botgame.config.TelegramBotConfiguration;
import dev.asror.botgame.processors.Processor;
import dev.asror.botgame.state.DefaultState;
import org.springframework.stereotype.Component;

@Component
public class DefaultCallbackProcessor implements Processor<DefaultState> {
    private final TelegramBot bot = TelegramBotConfiguration.get();

    @Override
    public void process(Update update, DefaultState state) {
        CallbackQuery callbackQuery = update.callbackQuery();
        Message message = callbackQuery.message();
        int messageId = message.messageId();
        Chat chat = callbackQuery.message().chat();
        Long chatID = chat.id();
        String data = callbackQuery.data();

    }
}
