package springbootboard.board.domain.board.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDto {

    private String content;
    private String writer;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @QueryProjection
    public CommentResponseDto(String content, String writer, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.content = content;
        this.writer = writer;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }
}
