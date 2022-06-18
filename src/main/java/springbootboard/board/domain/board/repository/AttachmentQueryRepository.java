package springbootboard.board.domain.board.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import springbootboard.board.domain.board.dto.AttachmentResponseDto;
import springbootboard.board.domain.board.dto.QAttachmentResponseDto;

import java.util.List;

import static springbootboard.board.domain.board.QAttachment.attachment;
import static springbootboard.board.domain.board.QPost.post;

@RequiredArgsConstructor
@Repository
public class AttachmentQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<AttachmentResponseDto> findAttachmentByPostId(Long postId) {
        return queryFactory.select(new QAttachmentResponseDto(attachment.fileType, attachment.storeFileName
                        , attachment.storeFilePath, attachment.uploadFileName))
                .from(attachment)
                .join(attachment.post, post).on(attachment.post.id.eq(postId))
                .fetch();

    }
}
