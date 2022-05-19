package springbootboard.board.domain.board.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostListResponseDto {

    private String title;
    private String writer;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Long view;

    @QueryProjection
    public PostListResponseDto(String title, String writer, LocalDateTime createdDate, LocalDateTime modifiedDate, Long view) {
        this.title = title;
        this.writer = writer;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.view = view;
    }
}
