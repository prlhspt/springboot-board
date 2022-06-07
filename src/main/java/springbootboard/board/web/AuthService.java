package springbootboard.board.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springbootboard.board.domain.member.MemberRepository;
import springbootboard.board.web.dto.MemberSaveDto;

@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(MemberSaveDto memberSaveDto) {
        if (memberRepository.existsByUsername(memberSaveDto.getUsername())) {
            throw new IllegalArgumentException("이미 가입되어 있는 유저입니다. username = " + memberSaveDto.getUsername());
        }

        memberRepository.save(memberSaveDto.toEntity(passwordEncoder));
    }

}
