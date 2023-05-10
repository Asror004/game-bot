package dev.asror.botgame.utils.factory;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InlineKeyboardFactory {
    private final List<String> codes;

    public InlineKeyboardMarkup rateButtons() {
        return new InlineKeyboardMarkup(
                createButton("⭐", "1"),
                createButton("⭐", "2"),
                createButton("⭐", "3"),
                createButton("⭐", "4"),
                createButton("⭐", "5")
        );
    }

    public InlineKeyboardMarkup startButton(){
        return new InlineKeyboardMarkup(createButton("Boshlash", "start"));
    }
    public InlineKeyboardMarkup send(){
        String uuid = UUID.randomUUID().toString();
        codes.add(uuid);
        InlineKeyboardButton button = new InlineKeyboardButton("Ulashish")
                .switchInlineQuery(uuid);
        return new InlineKeyboardMarkup(button);
    }
    private InlineKeyboardButton createButton(String text, String callback){
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.callbackData(callback);
        return button;
    }
}
