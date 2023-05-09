package dev.asror.botgame.handlers;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import dev.asror.botgame.processors.message.DefaultMessageProcessor;
import dev.asror.botgame.processors.message.RegistrationProcessor;
import dev.asror.botgame.state.DefaultState;
import dev.asror.botgame.state.RegistrationState;
import dev.asror.botgame.state.State;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class ContactHandler implements Handler {
    private final Map<Long, State> userState;
    private final DefaultMessageProcessor defaultMessageProcessor;
    private final RegistrationProcessor registrationProcessor;

    @Override
    public void handle(Update update) {
        Message message = update.message();
        Chat chat = message.chat();
        Long chatID = chat.id();
        State state = userState.get(chatID);
        if ( state instanceof RegistrationState registrationState )
            registrationProcessor.process(update, registrationState);
        else if ( state instanceof DefaultState defaultState )
            defaultMessageProcessor.process(update, defaultState);
    }
}
