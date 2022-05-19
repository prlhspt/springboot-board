package springbootboard.board.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springbootboard.board.domain.board.dto.CommentSaveRequestDto;
import springbootboard.board.domain.board.repository.CommentRepository;
import springbootboard.board.domain.board.repository.PostRepository;
import springbootboard.board.domain.member.Member;
import springbootboard.board.domain.member.MemberRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long register(CommentSaveRequestDto commentSaveRequestDto, Long postId, Long memberId) {

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. postId = " + postId));

        Member member = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("해당 유저가 존재하지 않습니다. memberId = " + memberId));

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
}
