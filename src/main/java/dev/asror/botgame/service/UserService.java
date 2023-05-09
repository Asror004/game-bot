package dev.asror.botgame.service;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Contact;
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

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Response<List<UserDomain>> getAllUsers() {
        return Response.<List<UserDomain>>builder().body(userRepository.findAll()).build();
    }

    @Async
    public void save(Chat chat, String fullName) {
//        UserDomain userDomain = findById(chat.id()).orElse(null);

//        if (Objects.isNull(userDomain)) {
        UserDomain user = UserDomain.childBuilder()
                .chatId(chat.id())
                .fullName(fullName)
                .build();

        userRepository.save(user);
//        }
    }

    @Async
    public void save(Long chatId, Contact contact) {
        UserDomain user = findById(chatId).orElseThrow(() ->
                new RuntimeException("User not found"));

        if (Objects.nonNull(contact))
            user.setPhoneNumber(contact.phoneNumber());

        user.setActive(UserDomain.Active.ACTIVE);

        userRepository.save(user);
    }

    public Optional<UserDomain> findById(@NonNull Long id) {
        return userRepository.findById(id);
    }


}
