package dev.asror.botgame.dtos;

import dev.asror.botgame.domain.TicTacToe;

public record FinishGameDto(TicTacToe ticTacToe, String callBackQueryId, String inlineMessageId, long chatId) {
}
