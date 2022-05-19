package springbootboard.board.domain.board.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResponseDto {

    private String title;
    private String content;
    private String writer;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Long view;
    private List<CommentResponseDto> comment;

    @QueryProjection
    public PostResponseDto(String title, String content, String writer, LocalDateTime createdDate, LocalDateTime modifiedDate, Long view) {
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.view = view;
    }
}
