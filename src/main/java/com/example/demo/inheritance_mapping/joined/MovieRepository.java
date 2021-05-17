package com.example.demo.inheritance_mapping;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MovieRepository {

    @PersistenceContext
    EntityManager em;
}
