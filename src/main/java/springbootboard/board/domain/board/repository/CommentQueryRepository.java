package springbootboard.board.domain.board.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import springbootboard.board.domain.board.Comment;
import springbootboard.board.domain.member.QMember;

import java.util.List;

import static springbootboard.board.domain.board.QComment.comment;
import static springbootboard.board.domain.member.QMember.*;

@RequiredArgsConstructor
@Repository
public class CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Comment> findCommentByPostId(Long postId) {
        return queryFactory.selectFrom(comment)
                .leftJoin(comment.parent)
                .join(comment.member, member)
                .fetchJoin()
                .where(comment.post.id.eq(postId))
                .orderBy(comment.createdDate.asc())
                .fetch();
    }

    public Comment findCommentOne(Long commentId) {
        return queryFactory.selectFrom(comment)
                .join(comment.member, member)
                .fetchJoin()
                .where(comment.id.eq(commentId))
                .fetchOne();
    }

}
