package springbootboard.board;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import springbootboard.board.domain.Member;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static springbootboard.board.domain.QMember.member;

@SpringBootTest
@Transactional
public class QuerydslApplicationTests {

    @Autowired
    EntityManager em;

    @Test
    void contextLoads() {
        Member testMember = Member.builder()
                .name("member1")
                .password("member1234")
                .email("member1@gmail.com")
                .build();
        em.persist(testMember);

        JPAQueryFactory query = new JPAQueryFactory(em);

        Member result = query
                .selectFrom(member)
                .fetchOne();
        assertThat(result).isEqualTo(testMember);
        assertThat(result.getId()).isEqualTo(testMember.getId());
    }
}
