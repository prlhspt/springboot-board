package springbootboard.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springbootboard.board.domain.board.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
