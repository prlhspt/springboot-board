package springbootboard.board.domain.board.dto;

import lombok.Builder;
import springbootboard.board.domain.board.Comment;

public class CommentSaveRequestDto {
    private String content;

    @Builder
    public CommentSaveRequestDto(String content) {
        this.content = content;
    }

    public Comment toEntity() {
        return Comment.builder()
                .content(content)
                .build();
    }
}
