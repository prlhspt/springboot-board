package springbootboard.board.domain.board.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostListResponseDto {

    private Long id;
    private String title;
    private String writer;
    private LocalDateTime createdDate;
    private Long view;
    private Long commentCount;

    public PostListResponseDto(Long id, String title, String writer, LocalDateTime createdDate, Long view, Long commentCount) {
        this.id = id;
        this.title = title;
        this.writer = writer;
        this.createdDate = createdDate;
        this.view = view;
        this.commentCount = commentCount;
    }
}
