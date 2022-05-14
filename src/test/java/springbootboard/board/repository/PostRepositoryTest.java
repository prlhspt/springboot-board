package springbootboard.board.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import springbootboard.board.domain.board.Post;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @AfterEach
    public void cleanup() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글 저장")
    public void save() throws Exception {
        //given

        Post post = Post.builder()
                .title("테스트제목")
                .content("테스트내용")
                .build();

        postRepository.save(post);

        //when
        Post findPost = postRepository.findById(post.getId()).get();

        //then
        assertThat(findPost.getId()).isEqualTo(findPost.getId());
        assertThat(findPost.getTitle()).isEqualTo(findPost.getTitle());


    }

}