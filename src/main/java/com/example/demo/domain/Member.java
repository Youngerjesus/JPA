package com.example.demo.domain;

import javax.persistence.*;

@Entity
public class Member {

    @Id
    @GeneratedValue
    Long id;

    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    Team team;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
