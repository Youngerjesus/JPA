package com.example.demo.repository;

import com.example.demo.domain.Member;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
@Transactional
public class MemberRepository {

    @PersistenceContext
    EntityManager em;

    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public Member findById(Long id) {
        return em.find(Member.class,id);
    }
}
