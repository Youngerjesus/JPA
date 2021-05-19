package com.example.demo.value_type;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    @Embedded
    private Period period;

    @Embedded
    private Address address;
}
