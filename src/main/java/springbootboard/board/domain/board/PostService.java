package springbootboard.board.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springbootboard.board.domain.board.dto.*;
import springbootboard.board.domain.board.repository.PostQueryRepository;
import springbootboard.board.domain.board.repository.PostRepository;
import springbootboard.board.domain.member.Member;
import springbootboard.board.domain.member.MemberRepository;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostService {

    private final PostQueryRepository queryRepository;
    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    @Transactional
    public Long post(PostSaveRequestDto postSaveRequestDto, Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("해당 유저가 존재하지 않습니다. id = " + memberId));

        Post post = postSaveRequestDto.toEntity();
        post.setMember(member);

        return postRepository.save(post).getId();
    }

    public List<PostListResponseDto> findPostList(PostSearchCond cond) {
        return queryRepository.findPostListDto(cond);
    }

    public PostResponseDto findDetailPost(Long id) {
        PostResponseDto postResponseDto = queryRepository.findPostDto(id);
        List<CommentResponseDto> commentDto = queryRepository.findCommentDto(id);

        postResponseDto.setComment(commentDto);

        return postResponseDto;
    }

}
