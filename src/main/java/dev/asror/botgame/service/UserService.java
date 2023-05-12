package dev.asror.botgame.service;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Contact;
import com.pengrad.telegrambot.model.User;
import dev.asror.botgame.domain.UserDomain;
import dev.asror.botgame.repository.UserRepository;
import dev.asror.botgame.response.Response;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Response<List<UserDomain>> getAllUsers() {
        return Response.<List<UserDomain>>builder().body(userRepository.findAll()).build();
    }

    public String findCodeById(Long chatId){
        return userRepository.findByChatId(chatId).orElseThrow();
    }

    public boolean hasCode(String code){
        return userRepository.hasCode(code);
    }

    @Async
    public void save(Long chatId, String fullName) {
        UserDomain user = UserDomain.childBuilder()
                .chatId(chatId)
                .fullName(fullName)
                .code(UUID.randomUUID().toString())
                .build();

        userRepository.save(user);
    }

    @Async
    public void save(Long chatId, Contact contact) {
        UserDomain user = findById(chatId).orElseThrow(() ->
                new RuntimeException("User not found"));

        if (Objects.nonNull(contact))
            user.setPhoneNumber(contact.phoneNumber());

        userRepository.save(user);
    }

    public Optional<UserDomain> findById(@NonNull Long id) {
        return userRepository.findById(id);
    }


}
