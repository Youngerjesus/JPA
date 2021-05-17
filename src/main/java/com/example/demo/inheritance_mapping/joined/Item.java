package com.example.demo.inheritance_mapping;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Item {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private int price;
}