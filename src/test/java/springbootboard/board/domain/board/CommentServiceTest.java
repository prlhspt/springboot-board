package springbootboard.board.domain.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import springbootboard.board.domain.member.LoginType;
import springbootboard.board.domain.member.Member;
import springbootboard.board.domain.member.MemberRepository;
import springbootboard.board.domain.member.Role;
import springbootboard.board.domain.board.dto.CommentResponseDto;
import springbootboard.board.domain.board.dto.CommentSaveRequestDto;
import springbootboard.board.domain.board.dto.PostSaveRequestDto;
import springbootboard.board.domain.board.repository.CommentRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class CommentServiceTest {

    @Autowired CommentService commentService;
    @Autowired CommentRepository commentRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired PostService postService;

    static String POST_ID = "postId";
    static String MEMBER_ID = "memberId";

    @Test
    @DisplayName("계층형 댓글 등록")
    public void registerChildComment() {
        // given
        Map<String, Long> idMap = createMemberAndPostAndComment();

        // when
        List<CommentResponseDto> comment = commentService.findComment(idMap.get(POST_ID));

        // then
        assertThat(comment.get(0).getChild().size()).isEqualTo(1);
        assertThat(comment.get(0).getChild().get(0).getChild().size()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("댓글 삭제")
    public void deleteComment() {
        // given
        Map<String, Long> idMap = createMemberAndPostAndComment();
        commentService.delete(idMap.get("nestedCommentId3"));

        // when
        List<CommentResponseDto> comment = commentService.findComment(idMap.get(POST_ID));

        // then
        assertThat(comment.get(0).getChild().get(0).getChild().get(1).getContent()).isEqualTo("삭제된 댓글입니다.");

    }

    private Map<String, Long> createMemberAndPostAndComment() {

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

        PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto("테스트 제목", "테스트 내용");
        Long postId = postService.post(postSaveRequestDto, member.getUsername());

        Long commentId = commentService.register(new CommentSaveRequestDto("테스트 댓글1"), postId, member.getUsername());

        Long nestedCommentId1 = commentService.register(
                new CommentSaveRequestDto("테스트 대댓글 1"), postId,
                member.getUsername(), commentId);

        Long nestedCommentId2 = commentService.register(new CommentSaveRequestDto("테스트 대댓글2"), postId,
                member.getUsername(), nestedCommentId1);

        Long nestedCommentId3 = commentService.register(new CommentSaveRequestDto("테스트 대댓글3"), postId,
                member.getUsername(), nestedCommentId1);

        commentService.register(new CommentSaveRequestDto("테스트 대댓글4"), postId,
                member.getUsername(), nestedCommentId2);
        
        map.put(POST_ID, postId);
        map.put(MEMBER_ID, member.getId());
        map.put("nestedCommentId3", nestedCommentId3);

        return map;
    }
    
}