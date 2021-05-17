package com.example.demo.repository;

import com.example.demo.domain.Team;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
@Transactional
public class TeamRepository {

    @PersistenceContext
    EntityManager em;

    public Team save(Team team){
        em.persist(team);
        return team;
    }

    public Team findById(Long id) {
        return em.find(Team.class, id);
    }
}
