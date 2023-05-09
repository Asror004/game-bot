package dev.asror.botgame;

import com.pengrad.telegrambot.UpdatesListener;
import dev.asror.botgame.config.InitializerConfiguration;
import dev.asror.botgame.config.TelegramBotConfiguration;
import dev.asror.botgame.handlers.UpdateHandler;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BotGameApplication {

    private final InitializerConfiguration initializerConfiguration;
    private final UpdateHandler updateHandler;

    public BotGameApplication(InitializerConfiguration initializerConfiguration, UpdateHandler updateHandler) {
        this.initializerConfiguration = initializerConfiguration;
        this.updateHandler = updateHandler;
    }

    public static void main(String[] args) {
        SpringApplication.run(BotGameApplication.class, args);
    }

    @Bean(name = "applicationRunner")
    ApplicationRunner runner() {
        return (args) -> {
            initializerConfiguration.init();
            TelegramBotConfiguration.get().setUpdatesListener((updates) -> {
                try {
                    updateHandler.handle(updates);
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                return 1;
            });
        };
    }
}
