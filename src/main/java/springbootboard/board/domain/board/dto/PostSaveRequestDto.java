package springbootboard.board.domain.board.dto;

import lombok.Builder;
import lombok.Data;
import springbootboard.board.domain.board.Post;

import javax.validation.constraints.NotBlank;

@Data
public class PostSaveRequestDto {

    @NotBlank
    private String title;
    @NotBlank
    private String content;

    @Builder
    public PostSaveRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Post toEntity() {
        return Post.builder()
                .title(title)
                .content(content)
                .build();
    }
}
