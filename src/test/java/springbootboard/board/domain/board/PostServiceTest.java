package springbootboard.board.domain.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import springbootboard.board.domain.member.LoginType;
import springbootboard.board.domain.member.Member;
import springbootboard.board.domain.member.MemberRepository;
import springbootboard.board.domain.member.Role;
import springbootboard.board.domain.board.dto.*;
import springbootboard.board.domain.board.repository.PostQueryRepository;
import springbootboard.board.domain.board.repository.PostRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;
    @Autowired PostRepository postRepository;
    @Autowired PostQueryRepository postQueryRepository;
    @Autowired MemberRepository memberRepository;

    static String POST_ID = "postId";
    static String MEMBER_ID = "memberId";

    @Test
    @DisplayName("게시글 등록")
    public void post() throws IOException {
        //given
        Map<String, Long> idMap = createMemberAndPost();

        //when
        List<Post> result = postRepository.findAll();

        //then
        assertThat(result.get(0).getMember().getId()).isEqualTo(idMap.get(MEMBER_ID));
        assertThat(result).extracting(Post::getTitle).containsExactly("제목", "테스트 제목2", "테스트 제목");

    }

    @Test
    @DisplayName("조건 없이 전체 게시글 검색하기")
    public void findPostListNotCond() throws IOException {
        //given
        createMemberAndPost();

        //when
        Page<PostListResponseDto> result = postService.findPostList(new PostSearchCond(), Pageable.ofSize(10));

        //then
        assertThat(result).extracting(PostListResponseDto::getTitle).containsExactly("테스트 제목",
                "테스트 제목2", "제목");

    }

    @Test
    @DisplayName("전체 게시글에서 제목만 검색하기")
    public void findPostListTitle() throws IOException {
        //given
        createMemberAndPost();

        //when
        PostSearchCond cond = new PostSearchCond();
        cond.setTitle("테스트 제목");

        Page<PostListResponseDto> result = postService.findPostList(cond, Pageable.ofSize(10));

        //then
        assertThat(result).extracting(PostListResponseDto::getTitle).containsExactly("테스트 제목", "테스트 제목2");

    }

    @Test
    @DisplayName("전체 게시글에서 내용만 검색하기")
    public void findPostListContent() throws IOException {
        //given
        createMemberAndPost();

        //when
        PostSearchCond cond = new PostSearchCond();
        cond.setContent("테스트 내용");

        Page<PostListResponseDto> result = postService.findPostList(cond, Pageable.ofSize(10));

        //then
        assertThat(result).extracting(PostListResponseDto::getTitle).containsExactly("테스트 제목", "제목");
    }

    @Test
    @DisplayName("전체 게시글에서 작성자만 검색하기")
    public void findPostListWriter() throws IOException {
        //given
        createMemberAndPost();

        //when
        PostSearchCond cond = new PostSearchCond();
        cond.setWriter("nickname1");

        Page<PostListResponseDto> result = postService.findPostList(cond, Pageable.ofSize(10));

        //then
        assertThat(result).extracting(PostListResponseDto::getTitle).containsExactly("테스트 제목",
                "테스트 제목2", "제목");
    }

    @Test
    @DisplayName("작성자 틀리게 검색하기")
    public void findPostListNotEqWriter() throws IOException {
        //given
        createMemberAndPost();

        //when
        PostSearchCond cond = new PostSearchCond();
        cond.setWriter("nick");

        Page<PostListResponseDto> result = postService.findPostList(cond, Pageable.ofSize(10));

        //then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("다중 조건 검색하기")
    public void findPostListTitleAndContentAndWriter() throws IOException {
        //given
        createMemberAndPost();

        //when
        PostSearchCond cond = new PostSearchCond();
        cond.setTitle("테스트 제목");
        cond.setContent("내용");
        cond.setWriter("nickname1");

        Page<PostListResponseDto> result = postService.findPostList(cond, Pageable.ofSize(10));

        //then
        assertThat(result).extracting(PostListResponseDto::getTitle).containsExactly("테스트 제목");
    }

    @Test
    @DisplayName("게시글 상세 검색")
    public void findDetailPost() throws IOException {
        //given
        Map<String, Long> idMap = createMemberAndPost();
        //when
        PostResponseDto postResponseDto = postService.findDetailPost(idMap.get(POST_ID));

        //then
        assertThat(postResponseDto.getTitle()).isEqualTo("테스트 제목");
    }

    @Test
    @DisplayName("게시글 삭제")
    public void deletePost() throws IOException {
        //given
        Map<String, Long> idMap = createMemberAndPost();
        postService.delete(idMap.get(POST_ID));

        // when, then
        assertThrows(IllegalArgumentException.class, () ->
                postService.findDetailPost(idMap.get(POST_ID)));
    }

    private Map<String, Long> createMemberAndPost() throws IOException {

        Map<String, Long> map = new HashMap<>();

        Member testMember = Member.builder()
                .username("username")
                .password("member1234")
                .email("member1@gmail.com")
                .loginType(LoginType.LOCAL)
                .nickname("nickname1")
                .role(Role.USER)
                .build();

        Member member = memberRepository.save(testMember);

        MultipartFile multipartFile = new MockMultipartFile("test", new byte[]{});
        ArrayList<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(multipartFile);

        PostSaveRequestDto postSaveRequestDto3 = PostSaveRequestDto
                .builder()
                .title("제목")
                .content("테스트 내용2")
                .attachFile(multipartFile)
                .imageFiles(multipartFiles)
                .build();

        PostSaveRequestDto postSaveRequestDto2 = PostSaveRequestDto
                .builder()
                .title("테스트 제목2")
                .content("테스트")
                .attachFile(multipartFile)
                .imageFiles(multipartFiles)
                .build();

        PostSaveRequestDto postSaveRequestDto1 = PostSaveRequestDto
                .builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .attachFile(multipartFile)
                .imageFiles(multipartFiles)
                .build();

        postService.post(postSaveRequestDto3, member.getUsername());
        postService.post(postSaveRequestDto2, member.getUsername());
        Long postId = postService.post(postSaveRequestDto1, member.getUsername());

        map.put(POST_ID, postId);
        map.put(MEMBER_ID, member.getId());

        return map;
    }

}