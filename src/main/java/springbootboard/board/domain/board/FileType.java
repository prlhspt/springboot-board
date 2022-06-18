package springbootboard.board.domain.board;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {

    IMAGE("image", "이미지"),
    FILE("file", "파일");

    private final String key;
    private final String title;

}