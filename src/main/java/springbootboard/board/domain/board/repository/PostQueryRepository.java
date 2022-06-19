package springbootboard.board.domain.board.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import springbootboard.board.domain.board.Post;
import springbootboard.board.domain.board.dto.PostListResponseDto;
import springbootboard.board.domain.board.dto.PostResponseDto;
import springbootboard.board.domain.board.dto.PostSearchCond;
import springbootboard.board.domain.board.dto.QPostResponseDto;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static springbootboard.board.domain.board.QComment.comment;
import static springbootboard.board.domain.board.QPost.post;
import static springbootboard.board.domain.member.QMember.member;

@RequiredArgsConstructor
@Repository
public class PostQueryRepository {

    private final JPAQueryFactory queryFactory;

    public PostResponseDto findPostDto(Long id) {
        return queryFactory.select(new QPostResponseDto(post.id, post.title, post.content, post.member.nickname,
                        post.createdDate, post.view))
                .from(post)
                .join(post.member, member).on(post.id.eq(id))
                .where(post.deleted.eq(false))
                .fetchOne();
    }

    public Page<PostListResponseDto> findPostListDto(PostSearchCond cond, Pageable pageable) {

        List<Post> entity = queryFactory.selectFrom(post)
                .join(post.member, member)
                .leftJoin(post.comments, comment).fetchJoin()
                .where(titleAndContentContainsAndWriterEq(cond.getTitle(), cond.getContent(), cond.getWriter())
                        .and(post.deleted.eq(false)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.id.desc())
                .fetch();

        List<PostListResponseDto> content = entity.stream()
                .map(p -> new PostListResponseDto(p.getId(),
                        p.getTitle(),
                        p.getMember().getNickname(),
                        p.getCreatedDate(),
                        p.getView(),
                        (long) p.getComments().size()))
                .collect(Collectors.toList());

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .join(post.member, member)
                .where(titleAndContentContainsAndWriterEq(cond.getTitle(), cond.getContent(), cond.getWriter())
                        .and(post.deleted.eq(false)))
                .orderBy(post.id.desc());

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());


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
