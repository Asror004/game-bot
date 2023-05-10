package dev.asror.botgame.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
public class UserDomain extends Auditable {

    @Id
    private Long chatId;

    private String phoneNumber;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    private Language language;

    @Enumerated(EnumType.STRING)
    private Active active;

    @Enumerated(EnumType.STRING)
    private Role role;

    public UserDomain() {
    }

    public enum Language {
        ENGLISH, UZBEK
    }

    public enum Active {
        BLOCKED, ACTIVE,
    }

    public enum Role {
        USER, ADMIN
    }

    @Builder(builderMethodName = "childBuilder")
    public UserDomain(LocalDateTime createdAt, LocalDateTime updatedAt, boolean deleted, Long chatId, String phoneNumber, String fullName, Language language, Active active, Role role) {
        super(createdAt, updatedAt, deleted);
        this.chatId = chatId;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.language = Objects.nonNull(language) ? language : Language.UZBEK;
        this.active = Objects.nonNull(active) ? active : Active.ACTIVE;
        this.role = Objects.nonNull(role) ? role : Role.USER;
    }
}
