package springbootboard.board.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import springbootboard.board.domain.board.Comment;

@SpringBootTest
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @AfterEach
    public void cleanup() {
        commentRepository.deleteAll();
    }

    @Test
    @DisplayName("댓글 저장")
    public void save() throws Exception {
        Comment comment = Comment.builder()
                .content("테스트댓글내용")
                .build();

        commentRepository.save(comment);

        //when
        Comment findComment = commentRepository.findById(comment.getId()).get();

        //then
        Assertions.assertThat(findComment.getContent()).isEqualTo(comment.getContent());


    }

}