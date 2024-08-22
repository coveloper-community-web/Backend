package com.covelopment.coveloper.service;

import com.covelopment.coveloper.dto.MemberDTO;
import com.covelopment.coveloper.entity.Member;
import com.covelopment.coveloper.repository.MemberRepository;
import com.covelopment.coveloper.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterMember() {
        MemberDTO memberDTO = new MemberDTO("test33@example.com", "password", "nickname", "name", "track1", "track2");

        // 비밀번호 인코딩
        Member member = new Member();
        member.setEmail("test33@example.com");
        member.setPassword("encodedPassword"); // 비밀번호 인코딩된 형태
        member.setNickname("nickname");
        member.setName("name");
        member.setTrack1("track1");
        member.setTrack2("track2");

        // Mock 설정
        when(memberRepository.findByEmail(memberDTO.getEmail())).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // 테스트 실행
        Member registeredMember = memberService.registerMember(memberDTO);

        // 검증
        assertNotNull(registeredMember);
        assertEquals(memberDTO.getEmail(), registeredMember.getEmail());
        // 비밀번호가 인코딩된 형태인지 확인
        assertEquals("encodedPassword", registeredMember.getPassword());
    }



    @Test
    public void testAuthenticate() {
        Member member = new Member(1L,"test@example.com", "password", "nickname", "name", "track1", "track2");
        String token = "test-jwt-token";

        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(jwtTokenProvider.createToken(member.getEmail())).thenReturn(token);

        // Note: authenticate method now returns a token string
        String resultToken = memberService.authenticate(member.getEmail(), member.getPassword());

        assertNotNull(resultToken);
        assertEquals(token, resultToken);
    }
}
