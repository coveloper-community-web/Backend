package com.covelopment.coveloper.service;

import com.covelopment.coveloper.entity.Member;
import com.covelopment.coveloper.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PrincipalDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public PrincipalDetailService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Member> memberOptional = memberRepository.findByEmail(email);
        if (!memberOptional.isPresent()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        Member member = memberOptional.get();
        return org.springframework.security.core.userdetails.User.builder()
                .username(member.getEmail())
                .password(member.getPassword()) // 비밀번호가 인코딩된 상태여야 합니다.
                .authorities("ROLE_USER") // 기본 권한 설정
                .build();
    }
}
