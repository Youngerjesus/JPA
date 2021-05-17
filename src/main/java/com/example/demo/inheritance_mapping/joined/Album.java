package com.example.demo.inheritance_mapping.joined;

import javax.persistence.Entity;

@Entity
public class Album extends Item{

    private String artist;
}
