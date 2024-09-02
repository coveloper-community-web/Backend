// MemberService.java
package com.covelopment.coveloper.service;

import com.covelopment.coveloper.dto.MemberDTO;
import com.covelopment.coveloper.entity.Member;
import com.covelopment.coveloper.exception.InvalidCredentialsException;
import com.covelopment.coveloper.exception.UserAlreadyExistsException;
import com.covelopment.coveloper.exception.UserNotFoundException;
import com.covelopment.coveloper.repository.MemberRepository;
import com.covelopment.coveloper.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Member registerMember(MemberDTO memberDTO) {
        if (memberRepository.findByEmail(memberDTO.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email is already in use");
        }

        Member member = new Member();
        member.setEmail(memberDTO.getEmail());
        member.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        member.setNickname(memberDTO.getNickname());
        member.setName(memberDTO.getName());
        member.setTrack1(memberDTO.getTrack1());
        member.setTrack2(memberDTO.getTrack2());

        return memberRepository.save(member);
    }

    public String login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        return jwtTokenProvider.createToken(email);
    }


    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }


}
