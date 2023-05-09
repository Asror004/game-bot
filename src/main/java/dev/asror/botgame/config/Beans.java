package dev.asror.botgame.config;

import dev.asror.botgame.state.State;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
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

}
