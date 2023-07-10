package dev.asror.botgame.dtos;

import com.pengrad.telegrambot.model.CallbackQuery;
import dev.asror.botgame.domain.TicTacToe;

public record FinishGameDtoWithAI(TicTacToe ticTacToe, CallbackQuery callbackQuery, long chatId) {
}
