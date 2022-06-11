package springbootboard.board.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springbootboard.board.domain.member.Member;
import springbootboard.board.domain.member.MemberRepository;
import springbootboard.board.domain.board.dto.CommentResponseDto;
import springbootboard.board.domain.board.dto.CommentSaveRequestDto;
import springbootboard.board.domain.board.repository.CommentQueryRepository;
import springbootboard.board.domain.board.repository.CommentRepository;
import springbootboard.board.domain.board.repository.PostRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long register(CommentSaveRequestDto commentSaveRequestDto, Long postId, String username) {

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. postId = " + postId));

        Member member = memberRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("해당 유저가 존재하지 않습니다. username = " + username));

        Comment comment = commentSaveRequestDto.toEntity();
        comment.setPost(post);
        comment.setMember(member);

        return commentRepository.save(comment).getId();
    }

    @Transactional
    public Long register(CommentSaveRequestDto commentSaveRequestDto, Long postId, Long memberId, Long parentCommentId) {

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. postId = " + postId));

        Member member = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("해당 유저가 존재하지 않습니다. memberId = " + memberId));

        Comment parentComment = commentRepository.findById(parentCommentId).orElseThrow(() ->
                new IllegalArgumentException("해당 댓글이 존재하지 않습니다. parentCommentId = " + parentCommentId));

        Comment comment = commentSaveRequestDto.toEntity();
        comment.setPost(post);
        comment.setMember(member);
        parentComment.addChildComment(comment);

        return commentRepository.save(comment).getId();
    }

    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new IllegalArgumentException("해당 댓글이 존재하지 않습니다. commentId = " + commentId));
        comment.delete();
    }

    public List<CommentResponseDto> findComment(Long postId) {
        List<Comment> commentDto = commentQueryRepository.findCommentByPostId(postId);
        List<CommentResponseDto> commentResponseDtoList = convertNestedStructure(commentDto);
        return commentResponseDtoList;
    }

    private List<CommentResponseDto> convertNestedStructure(List<Comment> comments) {
        List<CommentResponseDto> result = new ArrayList<>();
        Map<Long, CommentResponseDto> map = new HashMap<>();

        comments.stream().forEach(comment -> {
            CommentResponseDto commentResponseDto = CommentResponseDto.toCommentResponseDto(comment);
            map.put(commentResponseDto.getId(), commentResponseDto);

            if(comment.getParent() != null) {
                Long id = comment.getParent().getId();
                map.get(id).getChild().add(commentResponseDto);

            } else {
                result.add(commentResponseDto);
            }
        });

        return result;
    }

}
