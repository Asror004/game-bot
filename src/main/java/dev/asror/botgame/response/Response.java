package dev.asror.botgame.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    private T body;
    private final boolean success = true;
    private String friendlyErrorMessage;
    private String developerErrorMessage;
    private Integer total;
}
