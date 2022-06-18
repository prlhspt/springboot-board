package springbootboard.board.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springbootboard.board.domain.board.dto.CommentResponseDto;
import springbootboard.board.domain.board.dto.CommentSaveRequestDto;
import springbootboard.board.domain.board.repository.CommentQueryRepository;
import springbootboard.board.domain.board.repository.CommentRepository;
import springbootboard.board.domain.board.repository.PostRepository;
import springbootboard.board.domain.member.Member;
import springbootboard.board.domain.member.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    public Long register(CommentSaveRequestDto commentSaveRequestDto, Long postId, String username, Long parentCommentId) {

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. postId = " + postId));

        Member member = memberRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("해당 유저가 존재하지 않습니다. username = " + username));

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

    public Comment findByCommentId(Long commentId) {
        return commentQueryRepository.findCommentOne(commentId);
    }


    public List<CommentResponseDto> findComment(Long postId) {
        List<Comment> commentDto = commentQueryRepository.findCommentByPostId(postId);
        List<CommentResponseDto> commentResponseDtoList = convertNestedStructure(commentDto);
        return commentResponseDtoList;
    }

    private List<CommentResponseDto> convertNestedStructure(List<Comment> comments) {

        // ConcurrentHashMap 은 순서를 보장하지 않으므로 List 에 담았음
        List<CommentResponseDto> resultList = new ArrayList<>();
        Map<Long, CommentResponseDto> map = new ConcurrentHashMap<>();

        comments.stream().forEach(comment -> {

            CommentResponseDto commentResponseDto = CommentResponseDto.toCommentResponseDto(comment);

            map.put(commentResponseDto.getId(), commentResponseDto);

            if(comment.getParent() == null) {
                resultList.add(commentResponseDto);
            } else {
                if (!comment.isDeleted()) {
                    commentResponseDto.setParentWriter(comment.getParent().getMember().getNickname());
                }

                Comment ancestor = findAncestor(comment);
                map.get(ancestor.getId()).getChild().add(commentResponseDto);
            }
        });

        return resultList;
    }

    private Comment findAncestor(Comment comment) {
        if (comment.getParent() == null) {
            return comment;
        }

        return findAncestor(comment.getParent());
    }

}
