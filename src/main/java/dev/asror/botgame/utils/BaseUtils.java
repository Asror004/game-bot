package dev.asror.botgame.utils;

import java.util.Objects;

public interface BaseUtils {
    String MENU = "Menu🪟";
    String WRONG_INPUT = "Noto'g'ri qiymat kiritildi ❌";
    String PHONE_NUMBER = "Telefon raqam kiritish 📱";
    String SKIP = "O'tkazib yuborish ⏭";
    String NO_REGISTER = """
                        Ogohlantirish:
                        Siz botdan to'liq ro'yxatdan o'tmagansiz!
                        Hohlagan vaqt /fullreg komondasi orqali ro'yxatdan o'ting ✔        
                        """;
    String TIC_TAC_TOE = "Tic tac toe (X, 0 o'yini)";

    public static String getFullName(String firstName, String lastName) {
        return firstName + " " + (Objects.nonNull(lastName) ? lastName : "");
    }
}
