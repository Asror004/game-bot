package dev.asror.botgame.config;

import dev.asror.botgame.domain.TicTacToe;
import dev.asror.botgame.state.State;
import dev.asror.botgame.state.TicTacToeState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class Beans {
    @Bean
    public Map<Long, State> userState(){
        return new HashMap<>();
    }

    @Bean
    public ExecutorService executor(){
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
    @Bean
    public Map<String, TicTacToe> ticTacToes() {return new HashMap<>();}
    @Bean
    public Map<String, Map<Long, TicTacToeState>> ticTacToeState(){
        return new HashMap<>();
    }
}
