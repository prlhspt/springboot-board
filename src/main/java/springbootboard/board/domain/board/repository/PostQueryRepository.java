package springbootboard.board.domain.board.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import springbootboard.board.domain.board.Post;
import springbootboard.board.domain.board.dto.*;

import java.util.List;
import java.util.function.Supplier;

import static springbootboard.board.domain.board.QComment.comment;
import static springbootboard.board.domain.board.QPost.post;
import static springbootboard.board.domain.member.QMember.member;

@RequiredArgsConstructor
@Repository
public class PostQueryRepository {

    private final JPAQueryFactory queryFactory;

    public PostResponseDto findPostDto(Long id) {
        return queryFactory.select(new QPostResponseDto(post.title, post.content, post.member.nickname,
                        post.createdDate, post.modifiedDate, post.view))
                .from(post)
                .join(post.member, member).on(post.id.eq(id))
                .fetchFirst();
    }

    public List<CommentResponseDto> findCommentDto(Long id) {
        return queryFactory.select(new QCommentResponseDto(comment.content, comment.member.nickname,
                comment.createdDate, comment.modifiedDate))
                .from(comment)
                .join(comment.member, member)
                .where(comment.post.id.eq(id))
                .fetch();

    }

    public List<PostListResponseDto> findPostListDto(PostSearchCond cond) {
        return queryFactory.select(new QPostListResponseDto(post.title, post.member.nickname
                , post.createdDate, post.modifiedDate, post.view))
                .from(post)
                .join(post.member, member)
                .where(titleAndContentContainsAndWriterEq(cond.getTitle(), cond.getContent(), cond.getWriter()))
                .fetch();
    }

    public Post findTitleOne(String titleName) {
        return queryFactory.selectFrom(post)
                .where(post.title.eq(titleName))
                .fetchOne();

    }

    private BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (IllegalArgumentException e) {
            return new BooleanBuilder();
        } catch (NullPointerException e) {
            return new BooleanBuilder();
        }
    }

    private BooleanBuilder titleAndContentContainsAndWriterEq(String title, String content, String writer) {
        return titleContains(title).and(contentContains(content)).and(writerEq(writer));
    }

    private BooleanBuilder titleContains(String title) {
        return nullSafeBuilder(() -> post.title.contains(title));
    }

    private BooleanBuilder contentContains(String content) {
        return nullSafeBuilder(() -> post.content.contains(content));
    }

    private BooleanBuilder writerEq(String writer) {
        return nullSafeBuilder(() -> post.member.nickname.eq(writer));
    }

}
