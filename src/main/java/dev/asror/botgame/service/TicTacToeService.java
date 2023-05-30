package dev.asror.botgame.service;

import dev.asror.botgame.domain.TicTacToe;
import dev.asror.botgame.repository.TicTacToeRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class TicTacToeService {
    private final TicTacToeRepository ticTacToeRepository;

    public TicTacToeService(TicTacToeRepository ticTacToeRepository) {
        this.ticTacToeRepository = ticTacToeRepository;
    }

    @Async
    public void save(TicTacToe ticTacToe) {
        ticTacToe.setBoardDefinition(Arrays.deepToString(ticTacToe.getBoard()));
        ticTacToeRepository.save(ticTacToe);
    }

    public TicTacToe findById(String ticTacToeId) {
        return ticTacToeRepository.findById(ticTacToeId).orElse(null);
    }
}
