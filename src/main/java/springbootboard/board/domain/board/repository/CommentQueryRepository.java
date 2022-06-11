package springbootboard.board.domain.board.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import springbootboard.board.domain.board.Comment;

import java.util.List;

import static springbootboard.board.domain.board.QComment.comment;

@RequiredArgsConstructor
@Repository
public class CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Comment> findCommentByPostId(Long postId) {
        return queryFactory.selectFrom(comment)
                .leftJoin(comment.parent)
                .fetchJoin()
                .where(comment.post.id.eq(postId))
                .orderBy(comment.parent.id.asc().nullsFirst(),
                        comment.createdDate.asc())
                .fetch();
    }
}
