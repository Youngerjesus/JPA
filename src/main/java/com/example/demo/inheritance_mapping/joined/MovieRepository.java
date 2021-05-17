package com.example.demo.inheritance_mapping.joined;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MovieRepository {

    @PersistenceContext
    EntityManager em;

    public void save(Movie movie) {
        em.persist(movie);
    }

    public Movie find(Long id){
        return em.find(Movie.class, id);
    }
}
