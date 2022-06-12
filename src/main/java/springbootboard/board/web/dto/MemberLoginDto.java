package springbootboard.board.web.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MemberLoginDto {
    private String username;
    private String password;

    @Builder
    public MemberLoginDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}