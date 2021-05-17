package com.example.demo.inheritance_mapping;

import com.example.demo.inheritance_mapping.joined.Movie;
import com.example.demo.inheritance_mapping.joined.MovieRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class MovieRepositoryTest {

    @Autowired
    MovieRepository movieRepository;

    @Test
    @Transactional
    void testMovie(){
        //given
        Movie movie = new Movie();
        movie.setDirector("aaa");
        movie.setActor("nbb");
        movie.setName("바람과함께사라지다");
        movie.setPrice(10000);
        movieRepository.save(movie);
        //when
        
        //then
    }
}