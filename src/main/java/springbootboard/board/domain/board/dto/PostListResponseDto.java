package springbootboard.board.domain.board.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostListResponseDto {

    private Long id;
    private String title;
    private String writer;
    private LocalDateTime createdDate;
    private Long view;

    @QueryProjection
    public PostListResponseDto(Long id, String title, String writer, LocalDateTime createdDate, Long view) {
        this.id = id;
        this.title = title;
        this.writer = writer;
        this.createdDate = createdDate;
        this.view = view;
    }
}
