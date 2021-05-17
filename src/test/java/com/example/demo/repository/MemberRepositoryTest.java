package com.example.demo.repository;

import com.example.demo.domain.Member;
import com.example.demo.domain.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    Member createMember(){
        Team team = new Team();
        teamRepository.save(team);

        Member member = new Member();
        member.setName("first");
        member.setTeam(team);
        Member savedMember = memberRepository.save(member);

        return savedMember;
    }


    @Test
    @Transactional
    void getTestMember(){
        Member member = createMember();
        Team team = member.getTeam();
        System.out.println(team);
    }

    @Test
    @Transactional
    void testGetTeam(){
        Team findTeam = teamRepository.findById(1L);
        assertEquals(1, findTeam.getId());
    }

}