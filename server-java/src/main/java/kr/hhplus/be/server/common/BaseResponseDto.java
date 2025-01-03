package kr.hhplus.be.server.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class BaseResponseDto {

    @JsonProperty("result_code")
    private final int resultCode;
    @JsonProperty("message")
    private final String message;
    @JsonProperty("data")
    private final Object data;

}
