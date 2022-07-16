package springbootboard.board.web.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostResponseDto {

    private Long id;
    private String username;
    private String title;

    private String content;
    private String writer;

    private LocalDateTime createdDate;
    private Long view;

    private Page<CommentResponseDto> comments;

    private List<AttachmentResponseDto> imageFiles = new ArrayList<>();
    private AttachmentResponseDto attachFile;

    @QueryProjection
    public PostResponseDto(Long id, String username, String title, String content, String writer, LocalDateTime createdDate, Long view) {
        this.id = id;
        this.username = username;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.createdDate = createdDate;
        this.view = view;
    }
}
