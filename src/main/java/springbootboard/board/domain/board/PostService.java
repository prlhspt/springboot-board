package springbootboard.board.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springbootboard.board.domain.member.Member;
import springbootboard.board.domain.member.MemberRepository;
import springbootboard.board.domain.board.dto.*;
import springbootboard.board.domain.board.repository.PostQueryRepository;
import springbootboard.board.domain.board.repository.PostRepository;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostService {

    private final PostQueryRepository postQueryRepository;

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    @Transactional
    public Long post(PostSaveRequestDto postSaveRequestDto, String username) {

        Member member = memberRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("해당 유저가 존재하지 않습니다. username = " + username));

        Post post = postSaveRequestDto.toEntity();
        post.setMember(member);

        return postRepository.save(post).getId();
    }

    @Transactional
    public void delete(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("해당 유저가 존재하지 않습니다. postId = " + postId));
        post.delete();
    }


    @Transactional
    public void addViewCount(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. postId = " + postId));

        post.addViewCount();
    }

    public List<PostListResponseDto> findPostList(PostSearchCond cond) {
        return postQueryRepository.findPostListDto(cond);
    }

    public PostResponseDto findDetailPost(Long postId) {
        PostResponseDto postResponseDto = postQueryRepository.findPostDto(postId);
        if (postResponseDto == null) {
            throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다. postId = " + postId);
        }
        return postResponseDto;
    }

}
