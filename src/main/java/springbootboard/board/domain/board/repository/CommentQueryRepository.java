package springbootboard.board.domain.board.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import springbootboard.board.domain.board.Comment;
import springbootboard.board.web.dto.CommentResponseDto;

import java.util.List;
import java.util.stream.Collectors;

import static springbootboard.board.domain.board.QComment.comment;
import static springbootboard.board.domain.member.QMember.member;

@RequiredArgsConstructor
@Repository
public class CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<CommentResponseDto> findCommentByPostId(Long postId, Pageable pageable) {

        List<Comment> entity = queryFactory.selectFrom(comment)
                .leftJoin(comment.parent)
                .join(comment.member, member).fetchJoin()
                .where(comment.post.id.eq(postId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(comment.ancestorId.asc(), comment.parent.id.asc(), comment.id.asc())
                .fetch();

        List<CommentResponseDto> content = entity.stream()
                .map(comment -> CommentResponseDto.toCommentResponseDto(comment))
                .collect(Collectors.toList());


        JPAQuery<Long> countQuery = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.post.id.eq(postId))
                .orderBy(comment.ancestorId.asc(), comment.parent.id.asc(), comment.id.asc());


        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());


    }

    public Comment findCommentOne(Long commentId) {
        return queryFactory.selectFrom(comment)
                .join(comment.member, member)
                .fetchJoin()
                .where(comment.id.eq(commentId))
                .fetchOne();
    }

}
