package dev.asror.botgame.utils.factory;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import org.springframework.stereotype.Component;

@Component
public class InlineKeyboardFactory {
    public InlineKeyboardMarkup rateButtons() {
        return new InlineKeyboardMarkup(
                createButton("⭐", "1"),
                createButton("⭐", "2"),
                createButton("⭐", "3"),
                createButton("⭐", "4"),
                createButton("⭐", "5")
        );
    }
    private InlineKeyboardButton createButton(String text, String callback){
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.callbackData(callback);
        return button;
    }
}
