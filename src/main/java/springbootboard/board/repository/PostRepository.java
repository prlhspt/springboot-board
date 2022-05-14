package springbootboard.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springbootboard.board.domain.board.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
