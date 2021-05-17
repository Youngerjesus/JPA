package com.example.demo.inheritance_mapping.joined;

import javax.persistence.Entity;

@Entity
public class Book extends Item {
    private String author;
    private String isbn;
}
