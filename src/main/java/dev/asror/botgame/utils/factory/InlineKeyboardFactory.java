package dev.asror.botgame.utils.factory;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
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

    public InlineKeyboardMarkup ticTacToeButtons(byte[][] board){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row;

        for (int i = 0; i < 3; i++) {
            row = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                row.add(createButton(getChar(board[i][j]),
                        String.valueOf(board[i][j])+"_"+i));
            }

            markup.addRow(row.get(0), row.get(1), row.get(2));
        }

        return markup;
    }

    public InlineKeyboardMarkup ticTacToeStartButtons(){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton[] row;

        for (int i = 0; i < 3; i++) {
            row = new InlineKeyboardButton[3];
            for (int j = 0; j < 3; j++) {
                row[j] = createButton("ㅤㅤㅤㅤ", "0_"+i+"_"+j);
            }

            markup.addRow(row);
        }

        return markup;
    }

    private String getChar(byte n) {
        return switch (n){
            case 1 -> "0";
            case 2 -> "X";
            default -> "ㅤㅤㅤㅤ";
        };
    }

    public InlineKeyboardMarkup startButton(String id){
        return new InlineKeyboardMarkup(createButton("Boshlash", "start|"+id));
    }
    public InlineKeyboardMarkup send(String uuid){
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
