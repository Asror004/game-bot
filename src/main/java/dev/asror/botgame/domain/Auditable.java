package dev.asror.botgame.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@MappedSuperclass
public class Auditable {

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "timestamp default now()")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "timestamp default now()")
    private LocalDateTime updatedAt;
    @Column(columnDefinition = "boolean default 'f'")
    private boolean deleted;
}
