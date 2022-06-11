package springbootboard.board.domain.board.dto;

import lombok.Data;
import springbootboard.board.domain.board.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentResponseDto {

    private Long id;
    private String content;
    private String writer;

    private LocalDateTime createdDate;
    private List<CommentResponseDto> child = new ArrayList<>();

    public CommentResponseDto(Long id, String content, String writer, LocalDateTime createdDate) {
        this.id = id;
        this.content = content;
        this.writer = writer;
        this.createdDate = createdDate;
    }

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        return comment.isDeleted() == true ?
                new CommentResponseDto(comment.getId(), "삭제된 댓글입니다.", null, null) :
                new CommentResponseDto(comment.getId(), comment.getContent(), comment.getMember().getNickname(), comment.getCreatedDate());
    }

}
