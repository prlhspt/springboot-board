package springbootboard.board.config.auth.dto;

import lombok.Getter;
import springbootboard.board.domain.member.Member;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {

    private String name;

    public SessionUser(Member member) {
        this.name = member.getUsername();
    }
}
