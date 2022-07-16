package springbootboard.board.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import springbootboard.board.domain.board.Comment;
import springbootboard.board.domain.member.LoginType;
import springbootboard.board.domain.member.Member;
import springbootboard.board.domain.member.MemberRepository;
import springbootboard.board.domain.member.Role;
import springbootboard.board.web.dto.CommentResponseDto;
import springbootboard.board.web.dto.CommentSaveRequestDto;
import springbootboard.board.web.dto.PostSaveRequestDto;
import springbootboard.board.domain.board.repository.CommentRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class CommentServiceTest {

    @Autowired
    CommentService commentService;
    @Autowired CommentRepository commentRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired
    PostService postService;

    static String POST_ID = "postId";
    static String MEMBER_ID = "memberId";

    @Test
    @DisplayName("계층형 댓글 등록")
    public void registerChildComment() throws IOException {
        // given
        Map<String, Long> idMap = createMemberAndPostAndComment();

        // when
        Page<CommentResponseDto> comment = commentService.findComment(idMap.get(POST_ID), Pageable.ofSize(10));

        System.out.println("CommentServiceTest.registerChildComment");
        // then
        assertThat(comment.getContent().get(2).getParentWriter()).isEqualTo("nickname2");
    }
    
    @Test
    @DisplayName("댓글 삭제")
    public void deleteComment() throws IOException {
        // given
        Map<String, Long> idMap = createMemberAndPostAndComment();
        commentService.delete(idMap.get("nestedCommentId3"));

        // when
        Page<CommentResponseDto> comment = commentService.findComment(idMap.get(POST_ID), Pageable.ofSize(10));

        // then
        assertThat(comment.getContent().get(3).getContent()).isEqualTo("삭제된 댓글입니다.");
    }

    private Map<String, Long> createMemberAndPostAndComment() throws IOException {

        Map<String, Long> map = new HashMap<>();

        Member testMember = Member.builder()
                .username("username")
                .password("member1234")
                .email("member1@gmail.com")
                .loginType(LoginType.LOCAL)
                .nickname("nickname1")
                .role(Role.USER)
                .build();

        Member testMember2 = Member.builder()
                .username("username2")
                .password("member1234")
                .email("member2@gmail.com")
                .loginType(LoginType.LOCAL)
                .nickname("nickname2")
                .role(Role.USER)
                .build();

        Member member = memberRepository.save(testMember);
        Member member2 = memberRepository.save(testMember2);

        MultipartFile mockMultipartFile = new MockMultipartFile("test", new byte[]{});
        List<MultipartFile> mockMultipartFiles = new ArrayList<>();
        mockMultipartFiles.add(mockMultipartFile);

        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .attachFile(mockMultipartFile)
                .imageFiles(mockMultipartFiles)
                .build();

        Long postId = postService.post(postSaveRequestDto, member.getUsername());

        Comment comment = commentService.register(new CommentSaveRequestDto("테스트 댓글1"), postId, member.getUsername());

        Long nestedCommentId1 = commentService.register(
                new CommentSaveRequestDto("테스트 대댓글 1"), postId,
                member2.getUsername(), comment.getId(), comment.getAncestorId());

        Long nestedCommentId2 = commentService.register(new CommentSaveRequestDto("테스트 대댓글2"), postId,
                member.getUsername(), nestedCommentId1, comment.getAncestorId());

        Long nestedCommentId3 = commentService.register(new CommentSaveRequestDto("테스트 대댓글3"), postId,
                member.getUsername(), nestedCommentId1, comment.getAncestorId());

        commentService.register(new CommentSaveRequestDto("테스트 대댓글4"), postId,
                member.getUsername(), nestedCommentId2, comment.getAncestorId());
        
        map.put(POST_ID, postId);
        map.put(MEMBER_ID, member.getId());
        map.put("nestedCommentId3", nestedCommentId3);

        return map;
    }
}