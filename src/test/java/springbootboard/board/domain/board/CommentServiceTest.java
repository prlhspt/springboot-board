package springbootboard.board.domain.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import springbootboard.board.domain.board.dto.CommentSaveRequestDto;
import springbootboard.board.domain.board.dto.PostSaveRequestDto;
import springbootboard.board.domain.board.repository.CommentRepository;
import springbootboard.board.domain.member.Member;
import springbootboard.board.domain.member.MemberRepository;
import springbootboard.board.domain.member.Role;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class CommentServiceTest {

    @Autowired CommentService commentService;
    @Autowired CommentRepository commentRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired PostService postService;

    @Test
    @DisplayName("댓글의 부모와 자식은 서로 반대편의 id를 가지고 있어야 한다.")
    public void registerChildComment() throws Exception {
        //given
        Map<String, Long> idMap = createMemberAndPostAndComment();
        Long commentId1 = commentService.register(
                new CommentSaveRequestDto("테스트 답글1"), idMap.get("postId"),
                idMap.get("memberId"), idMap.get("commentId"));

        Long commentId2 = commentService.register(
                new CommentSaveRequestDto("테스트 답글2"), idMap.get("postId"),
                idMap.get("memberId"), idMap.get("commentId"));

        //when
        Comment comment1 = commentRepository.findById(commentId1).orElseThrow(()
                -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        Comment comment2 = commentRepository.findById(commentId2).orElseThrow(()
                -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        Comment parentComment = commentRepository.findById(idMap.get("commentId")).orElseThrow(()
                -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        //then
        assertThat(parentComment.getChild()).extracting(Comment::getContent).containsExactly("테스트 답글1",
                "테스트 답글2");
        assertThat(comment1.getParent().getContent()).isEqualTo("테스트 댓글1");
        assertThat(comment2.getParent().getContent()).isEqualTo("테스트 댓글1");
    }

    private Map<String, Long> createMemberAndPostAndComment() {

        Map<String, Long> map = new HashMap<>();

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
        Long postId = postService.post(postSaveRequestDto, member.getId());

        Long commentId = commentService.register(new CommentSaveRequestDto("테스트 댓글1"), postId, member.getId());

        map.put("postId", postId);
        map.put("memberId", member.getId());
        map.put("commentId", commentId);

        return map;
    }

}