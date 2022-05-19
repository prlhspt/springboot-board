package springbootboard.board.domain.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import springbootboard.board.domain.board.dto.*;
import springbootboard.board.domain.board.repository.PostQueryRepository;
import springbootboard.board.domain.board.repository.PostRepository;
import springbootboard.board.domain.member.Member;
import springbootboard.board.domain.member.MemberRepository;
import springbootboard.board.domain.member.Role;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired PostService postService;
    @Autowired CommentService commentService;
    @Autowired PostRepository postRepository;
    @Autowired PostQueryRepository postQueryRepository;
    @Autowired MemberRepository memberRepository;

    @Test
    @DisplayName("게시글 등록")
    public void post() throws Exception {
        //given
        Member member = createMemberAndPostAndComment();

        //when
        List<Post> result = postRepository.findAll();

        //then
        assertThat(result.get(0).getMember().getId()).isEqualTo(member.getId());
        assertThat(result).extracting(Post::getTitle).containsExactly("테스트 제목", "테스트 제목2", "제목");

    }

    @Test
    @DisplayName("조건 없이 전체 게시글 검색하기")
    public void findPostListNotCond() throws Exception {
        //given
        createMemberAndPostAndComment();

        //when
        List<PostListResponseDto> result = postService.findPostList(new PostSearchCond());

        //then
        assertThat(result).extracting(PostListResponseDto::getTitle).containsExactly("테스트 제목",
                "테스트 제목2", "제목");

    }

    @Test
    @DisplayName("전체 게시글에서 제목만 검색하기")
    public void findPostListTitle() throws Exception {
        //given
        createMemberAndPostAndComment();

        //when
        PostSearchCond cond = new PostSearchCond();
        cond.setTitle("테스트 제목");

        List<PostListResponseDto> result = postService.findPostList(cond);

        //then
        assertThat(result).extracting(PostListResponseDto::getTitle).containsExactly("테스트 제목", "테스트 제목2");

    }

    @Test
    @DisplayName("전체 게시글에서 내용만 검색하기")
    public void
findPostListContent() throws Exception {
        //given
        createMemberAndPostAndComment();

        //when
        PostSearchCond cond = new PostSearchCond();
        cond.setContent("테스트 내용");

        List<PostListResponseDto> result = postService.findPostList(cond);

        //then
        assertThat(result).extracting(PostListResponseDto::getTitle).containsExactly("테스트 제목", "제목");
    }

    @Test
    @DisplayName("전체 게시글에서 작성자만 검색하기")
    public void findPostListWriter() throws Exception {
        //given
        createMemberAndPostAndComment();

        //when
        PostSearchCond cond = new PostSearchCond();
        cond.setWriter("nickname1");

        List<PostListResponseDto> result = postService.findPostList(cond);

        //then
        assertThat(result).extracting(PostListResponseDto::getTitle).containsExactly("테스트 제목",
                "테스트 제목2", "제목");
    }

    @Test
    @DisplayName("작성자는 정확하게 일치하지 않을 경우 검색되지 않는다")
    public void findPostListNotEqWriter() throws Exception {
        //given
        createMemberAndPostAndComment();

        //when
        PostSearchCond cond = new PostSearchCond();
        cond.setWriter("nick");

        List<PostListResponseDto> result = postService.findPostList(cond);

        //then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("다중 조건 검색하기")
    public void findPostListTitleAndContentAndWriter() throws Exception {
        //given
        createMemberAndPostAndComment();

        //when
        PostSearchCond cond = new PostSearchCond();
        cond.setTitle("테스트 제목");
        cond.setContent("내용");
        cond.setWriter("nickname1");

        List<PostListResponseDto> result = postService.findPostList(cond);

        //then
        assertThat(result).extracting(PostListResponseDto::getTitle).containsExactly("테스트 제목");
    }

    @Test
    @DisplayName("게시글 상세 검색")
    public void findDetailPost() throws Exception {
        //given
        createMemberAndPostAndComment();
        Post post = postQueryRepository.findTitleOne("테스트 제목");

        System.out.println("PostServiceTest.findDetailPost start");
        //when
        PostResponseDto result = postService.findDetailPost(post.getId());
        List<CommentResponseDto> commentList = result.getComment();
        System.out.println("PostServiceTest.findDetailPost end");

        //then
        assertThat(result.getTitle()).isEqualTo(post.getTitle());

        assertThat(commentList).extracting(CommentResponseDto::getContent).containsExactly("테스트 댓글1",
                "테스트 댓글2");
    }

    private Member createMemberAndPostAndComment() {
        Member testMember = Member.builder()
                .username("username")
                .password("member1234")
                .email("member1@gmail.com")
                .loginType("local")
                .nickname("nickname1")
                .role(Role.USER)
                .build();

        Member member = memberRepository.save(testMember);

        PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto("테스트 제목", "테스트 내용");
        PostSaveRequestDto postSaveRequestDto2 = new PostSaveRequestDto("테스트 제목2", "테스트");
        PostSaveRequestDto postSaveRequestDto3 = new PostSaveRequestDto("제목", "테스트 내용2");
        Long postId = postService.post(postSaveRequestDto, member.getId());
        postService.post(postSaveRequestDto2, member.getId());
        postService.post(postSaveRequestDto3, member.getId());

        commentService.register(new CommentSaveRequestDto("테스트 댓글1"), postId, member.getId());
        commentService.register(new CommentSaveRequestDto("테스트 댓글2"), postId, member.getId());

        return member;
    }

}