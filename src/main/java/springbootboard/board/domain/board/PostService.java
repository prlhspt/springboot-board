package springbootboard.board.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springbootboard.board.domain.board.dto.*;
import springbootboard.board.domain.board.repository.PostQueryRepository;
import springbootboard.board.domain.board.repository.PostRepository;
import springbootboard.board.domain.member.Member;
import springbootboard.board.domain.member.MemberRepository;
import springbootboard.board.util.FileStore;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostService {

    private final PostQueryRepository postQueryRepository;

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;
    private final FileStore fileStore;

    @Transactional
    public Long post(PostSaveRequestDto postSaveRequestDto, String username) throws IOException {


        Member member = memberRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("해당 유저가 존재하지 않습니다. username = " + username));

        Post post = postSaveRequestDto.toEntity();
        post.setMember(member);

        storeAttachments(postSaveRequestDto, post);

        return postRepository.save(post).getId();
    }

    @Transactional
    public void updatePost(Long postId, PostSaveRequestDto postSaveRequestDto) throws IOException {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. postId = " + postId));

        post.update(postSaveRequestDto.toEntity());
        storeAttachments(postSaveRequestDto, post);
    }

    private void storeAttachments(PostSaveRequestDto postSaveRequestDto, Post post) throws IOException {
        AttachmentSaveRequestDto attachFile = fileStore.storeFile(postSaveRequestDto.getAttachFile(), FileType.FILE);
        List<AttachmentSaveRequestDto> imageFiles = fileStore.storeFiles(postSaveRequestDto.getImageFiles(), FileType.IMAGE);

        if (attachFile != null) {
            post.getAttachments().stream()
                            .forEach(p -> {
                                if (p.getFileType() == FileType.FILE) {
                                   p.delete();
                                }
                            });

            post.addAttachment(attachFile.toEntity());
        }

        if (imageFiles != null && imageFiles.size() != 0) {
            post.getAttachments().stream()
                    .forEach(p -> {
                        if (p.getFileType() == FileType.IMAGE) {
                            p.delete();
                        }
                    });

            for (AttachmentSaveRequestDto imageFile : imageFiles) {
                post.addAttachment(imageFile.toEntity());
            }
        }
    }

    @Transactional
    public void delete(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. postId = " + postId));
        post.delete();
    }


    @Transactional
    public void addViewCount(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. postId = " + postId));

        post.addViewCount();
    }

    public Post findOne(Long postId) {
        return postQueryRepository.findPostWithMember(postId);
    }

    public PostSaveRequestDto findOneDto(Long postId) {
        Post post = postQueryRepository.findPost(postId);
        return PostSaveRequestDto.ofEntity(post);
    }

    public Page<PostListResponseDto> findPostList(PostSearchCond cond, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        return postQueryRepository.findPostListDto(cond, pageable);
    }

    public PostResponseDto findDetailPost(Long postId) {
        PostResponseDto postResponseDto = postQueryRepository.findPostResponseDto(postId);
        if (postResponseDto == null) {
            throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다. postId = " + postId);
        }
        return postResponseDto;
    }

}
