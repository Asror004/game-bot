package dev.asror.botgame.handlers;

import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
@Component
public class UpdateHandler {
    private final ExecutorService executor;
    private final MessageHandler messageHandler;
    private final CallbackHandler callbackHandler;

    public void handle(List<Update> updates) {
        CompletableFuture.runAsync(() -> {
            for ( Update update : updates ) {
                executor.submit(() -> {
                    if ( Objects.nonNull(update.message()) )
                        messageHandler.handle(update);
                    else if ( Objects.nonNull(update.callbackQuery()) )
                        callbackHandler.handle(update);
                });
            }
        });
    }
}
