package springbootboard.board.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springbootboard.board.domain.board.dto.CommentResponseDto;
import springbootboard.board.domain.board.dto.CommentSaveRequestDto;
import springbootboard.board.domain.board.repository.CommentQueryRepository;
import springbootboard.board.domain.board.repository.CommentRepository;
import springbootboard.board.domain.board.repository.PostRepository;
import springbootboard.board.domain.member.Member;
import springbootboard.board.domain.member.MemberRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Comment register(CommentSaveRequestDto commentSaveRequestDto, Long postId, String username) {

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. postId = " + postId));

        Member member = memberRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("해당 유저가 존재하지 않습니다. username = " + username));

        Comment comment = commentSaveRequestDto.toEntity();
        comment.setPost(post);
        comment.setMember(member);

        Long id = commentRepository.save(comment).getId();
        comment.setAncestorId(id);

        return comment;
    }

    @Transactional
    public Long register(CommentSaveRequestDto commentSaveRequestDto, Long postId, String username, Long parentCommentId, Long ancestorId) {

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. postId = " + postId));

        Member member = memberRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("해당 유저가 존재하지 않습니다. username = " + username));

        Comment parentComment = commentRepository.findById(parentCommentId).orElseThrow(() ->
                new IllegalArgumentException("해당 댓글이 존재하지 않습니다. parentCommentId = " + parentCommentId));

        Comment comment = commentSaveRequestDto.toEntity();
        comment.setPost(post);
        comment.setMember(member);
        comment.setAncestorId(ancestorId);
        parentComment.addChildComment(comment);

        return commentRepository.save(comment).getId();
    }

    @Transactional
    public void updateComment(Long commentId, CommentSaveRequestDto commentSaveRequestDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new IllegalArgumentException("해당 댓글이 존재하지 않습니다. commentId = " + commentId));

        comment.update(commentSaveRequestDto.toEntity());
    }

    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new IllegalArgumentException("해당 댓글이 존재하지 않습니다. commentId = " + commentId));
        comment.delete();
    }

    public Comment findByCommentId(Long commentId) {
        return commentQueryRepository.findCommentOne(commentId);
    }


    public Page<CommentResponseDto> findComment(Long postId, Pageable pageable) {

        // 페이지 선택이 없을때는 제일 마지막 페이지로 조정
        if (pageable.getPageNumber() == 0) {
            Page<CommentResponseDto> pageDto = commentQueryRepository.findCommentByPostId(postId, pageable);

            int page = (pageDto.getTotalPages() == 0) ? 0 : (pageDto.getTotalPages() - 1);
            pageable = PageRequest.of(page, pageable.getPageSize());

            return commentQueryRepository.findCommentByPostId(postId, pageable);
        }

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        return commentQueryRepository.findCommentByPostId(postId, pageable);
    }
}
