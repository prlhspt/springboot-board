package springbootboard.board.domain.board.dto;

import lombok.Data;
import springbootboard.board.domain.board.Comment;

import java.time.LocalDateTime;

@Data
public class CommentResponseDto {

    private Long id;
    private String username;
    private String content;
    private String writer;
    private String parentWriter;
    private Long ancestorId;

    private LocalDateTime createdDate;

    public CommentResponseDto(Long id, String username, String content, String writer, String parentWriter, Long ancestorId, LocalDateTime createdDate) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.writer = writer;
        this.parentWriter = parentWriter;
        this.ancestorId = ancestorId;
        this.createdDate = createdDate;
    }

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        if (comment.isDeleted()) {
            return new CommentResponseDto(comment.getId(), null, "삭제된 댓글입니다.", null, null, null, null);
        } else if (comment.getParent() == null) {
            return new CommentResponseDto(comment.getId(), comment.getMember().getUsername(), comment.getContent(), comment.getMember().getNickname(), null, comment.getAncestorId(), comment.getCreatedDate());
        } else {
            return new CommentResponseDto(comment.getId(), comment.getMember().getUsername(), comment.getContent(), comment.getMember().getNickname(), comment.getParent().getMember().getNickname(), comment.getAncestorId(), comment.getCreatedDate());
        }
    }
}
