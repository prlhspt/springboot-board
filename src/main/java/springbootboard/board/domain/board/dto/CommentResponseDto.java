package springbootboard.board.domain.board.dto;

import lombok.Data;
import springbootboard.board.domain.board.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentResponseDto {

    private Long id;
    private String username;
    private String content;
    private String writer;
    private String parentWriter;

    private LocalDateTime createdDate;
    private List<CommentResponseDto> child = new ArrayList<>();

    public CommentResponseDto(Long id, String username, String content, String writer, LocalDateTime createdDate) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.writer = writer;
        this.createdDate = createdDate;
    }

    public void setParentWriter(String parentWriter) {
        this.parentWriter = parentWriter;
    }

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        if (comment.isDeleted()) {
            return new CommentResponseDto(comment.getId(), null, "삭제된 댓글입니다.", null, null);
        }
        return new CommentResponseDto(comment.getId(), comment.getMember().getUsername(), comment.getContent(), comment.getMember().getNickname(), comment.getCreatedDate());
    }
}
