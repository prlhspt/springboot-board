package springbootboard.board.domain.board.dto;

import lombok.Builder;
import lombok.Data;
import springbootboard.board.domain.board.Comment;

import javax.validation.constraints.NotBlank;

@Data
public class CommentSaveRequestDto {

    private Long id;
    @NotBlank
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
