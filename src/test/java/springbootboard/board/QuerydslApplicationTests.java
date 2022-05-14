package springbootboard.board;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import springbootboard.board.domain.member.Member;
import springbootboard.board.domain.member.Role;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static springbootboard.board.domain.member.QMember.*;

@SpringBootTest
@Transactional
public class QuerydslApplicationTests {

    @Autowired
    EntityManager em;

    @Test
    void contextLoads() {
        Member testMember = Member.builder()
                .username("username")
                .password("member1234")
                .email("member1@gmail.com")
                .loginType("local")
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
