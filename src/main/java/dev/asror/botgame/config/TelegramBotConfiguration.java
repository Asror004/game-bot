package dev.asror.botgame.config;

import com.pengrad.telegrambot.TelegramBot;

public class TelegramBotConfiguration {
    private final static String BOT_TOKEN = "6057370667:AAE8EQxYoxIaTtvWgCoFJ4gI0ORg9p8LEqg";
    private static final ThreadLocal<TelegramBot> telegramBotThreadLocal =
            ThreadLocal.withInitial(() ->
                    new TelegramBot(BOT_TOKEN));
    public static TelegramBot get() {
        return telegramBotThreadLocal.get();
    }
}
