package springbootboard.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import springbootboard.board.domain.member.MemberRepository;
import springbootboard.board.web.dto.MemberSaveDto;

@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(MemberSaveDto memberSaveDto, Errors errors) {
        if (memberRepository.existsByUsername(memberSaveDto.getUsername())) {
            errors.reject("DuplicatedMember");
            return;
        }

        memberRepository.save(memberSaveDto.toEntity(passwordEncoder));
    }

}
