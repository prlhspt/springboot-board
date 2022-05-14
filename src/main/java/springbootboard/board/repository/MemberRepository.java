package springbootboard.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springbootboard.board.domain.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

}