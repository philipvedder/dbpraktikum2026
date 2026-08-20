package de.unileipzig.dbpraktikum.cli_interface.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Person model for Actors, Artists, ...
 */
@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Long id;

    @Column(name = "name")
    private String name;

    //Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
