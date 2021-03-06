package springbootboard.board.domain.board.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import springbootboard.board.web.dto.CommentResponseDto;
import springbootboard.board.domain.member.LoginType;
import springbootboard.board.domain.member.Member;
import springbootboard.board.domain.member.MemberRepository;
import springbootboard.board.domain.member.Role;
import springbootboard.board.domain.board.Comment;
import springbootboard.board.domain.board.Post;
import springbootboard.board.web.dto.PostListResponseDto;
import springbootboard.board.web.dto.PostResponseDto;
import springbootboard.board.web.dto.PostSearchCond;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PostQueryRepositoryTest {

    @Autowired
    PostQueryRepository postQueryRepository;

    @Autowired
    CommentQueryRepository commentQueryRepository;

    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired MemberRepository memberRepository;

    @AfterEach
    public void clearAll() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();
    }

    public Post makePosts() {

        Member member = new Member("member1", "1234", "nick1"
                , "abcd@google.com", LoginType.LOCAL, Role.USER);

        memberRepository.save(member);

        Post post1 = new Post("title1", "content1");
        Post post2 = new Post("title2", "content2");

        post1.setMember(member);
        post2.setMember(member);

        postRepository.save(post1);
        postRepository.save(post2);

        Comment comment1 = new Comment("comment1", member, post1);
        Comment comment2 = new Comment("comment2", member, post1);

        Comment comment3 = new Comment("comment3", member, post2);
        Comment comment4 = new Comment("comment4", member, post2);
        Comment comment5 = new Comment("comment5", member, post2);

        commentRepository.save(comment1);
        commentRepository.save(comment2);

        commentRepository.save(comment3);
        commentRepository.save(comment4);
        commentRepository.save(comment5);

        return post1;
    }


    // ?????? ???????????? ???????????? ?????? ???????????? ????????? ???????????? ???????????? ?????? @Transactional ??????
    @Transactional(readOnly = true)
    @Test
    @DisplayName("Post ??? ?????? ?????? ??????")
    public void findPostListDto(){
        // given
        makePosts();

        // when
        Page<PostListResponseDto> result = postQueryRepository.findPostListDto(new PostSearchCond(), Pageable.ofSize(10));

        // then

        assertThat(result).extracting(PostListResponseDto::getTitle).containsExactly("title2", "title1");
    }

    @Test
    @DisplayName("Post ?????? ??????")
    public void findPostDto() {
        // given
        Post post = makePosts();

        // when
        PostResponseDto result = postQueryRepository.findPostResponseDto(post.getId());
        // then
        assertThat(result.getTitle()).isEqualTo(post.getTitle());
    }

    @Test
    @DisplayName("Post id??? ?????? Comment ????????? ??????")
    public void findCommentDto() {
        // given
        Post post = makePosts();

        // when
        Page<CommentResponseDto> result = commentQueryRepository.findCommentByPostId(post.getId(), Pageable.ofSize(10));

        // then
        assertThat(result).extracting(CommentResponseDto::getContent).containsExactly("comment1", "comment2");
    }

}