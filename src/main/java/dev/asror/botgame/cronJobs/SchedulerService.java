package dev.asror.botgame.cronJobs;

import dev.asror.botgame.domain.TicTacToe;
import dev.asror.botgame.state.TicTacToeState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "scheduler.enable",
        matchIfMissing = true,
        havingValue = "0"
)
@RequiredArgsConstructor
public class SchedulerService {
    private final Map<String, Map<Long, TicTacToeState>> ticTacToeState;
    private final Map<String, TicTacToe> ticTacToes;
    private final Map<String, Boolean> currentPlayer;

    @Scheduled(initialDelay = 60, fixedRate = 40, timeUnit = TimeUnit.MINUTES)
    public void removeTicTacToes() {
        ticTacToes.forEach((key, value) -> {
            if ((Objects.nonNull(value.getFinishTime()) &&
                    value.getFinishTime().plusMinutes(10).isBefore(LocalDateTime.now())) ||
                    (value.getCreatedAt().plusDays(1).isBefore(LocalDateTime.now()))) {
                ticTacToes.remove(key);
                ticTacToeState.remove(key);
                currentPlayer.remove(key);
            }
        });
    }
}
