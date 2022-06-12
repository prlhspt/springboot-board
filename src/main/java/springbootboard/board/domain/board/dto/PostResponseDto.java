package springbootboard.board.domain.board.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostResponseDto {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime createdDate;
    private Long view;
    private List<CommentResponseDto> comment = new ArrayList<>();

    @QueryProjection
    public PostResponseDto(Long id, String title, String content, String writer, LocalDateTime createdDate, Long view) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.createdDate = createdDate;
        this.view = view;
    }
}
