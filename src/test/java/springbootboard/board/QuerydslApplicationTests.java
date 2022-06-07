package springbootboard.board;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import springbootboard.board.domain.member.LoginType;
import springbootboard.board.domain.member.Member;
import springbootboard.board.domain.member.Role;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static springbootboard.board.domain.member.QMember.member;

@SpringBootTest
@Transactional
public class QuerydslApplicationTests {

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("Querydsl 동작 테스트")
    void contextLoads() {
        Member testMember = Member.builder()
                .username("username")
                .password("member1234")
                .email("member1@gmail.com")
                .loginType(LoginType.LOCAL)
                .nickname("member")
                .role(Role.USER)
                .build();
        em.persist(testMember);

        JPAQueryFactory query = new JPAQueryFactory(em);

        Member result = query
                .selectFrom(member)
                .fetchOne();

        assertThat(result.getId()).isEqualTo(testMember.getId());
    }
}
