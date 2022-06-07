package springbootboard.board.domain.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginType {

    LOCAL("LOCAL", "일반 로그인"),
    NAVER("NAVER", "네이버"),
    GOOGLE("GOOGLE", "구글"),
    FACEBOOK("FACEBOOK", "페이스북");

    private final String key;
    private final String title;

}
