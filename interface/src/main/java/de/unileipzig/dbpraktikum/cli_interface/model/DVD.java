package de.unileipzig.dbpraktikum.cli_interface.model;

import java.util.Set;

import org.hibernate.annotations.SQLJoinTableRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * DVD base model entity. 
 * Extends Product with specific attributes. 
 */
@Entity
@Table(name = "dvd")
@PrimaryKeyJoinColumn(name = "produkt_nr")
public class DVD extends Product {
    @ManyToMany()
    @JoinTable(
        name = "dvd_format", 
        joinColumns = { @JoinColumn(name = "produkt_nr") }, 
        inverseJoinColumns = { @JoinColumn(name = "format_id") }
    )
    private Set<Format> formats;

    @ManyToMany()
    @JoinTable(
        name = "dvd_beteiligung", 
        joinColumns = { @JoinColumn(name = "produkt_nr") }, 
        inverseJoinColumns = { @JoinColumn(name = "person_id") }
    )
    @SQLJoinTableRestriction("rolle = 'ACTOR'")
    private Set<Person> actors;

    @ManyToMany()
    @JoinTable(
        name = "dvd_beteiligung", 
        joinColumns = { @JoinColumn(name = "produkt_nr") }, 
        inverseJoinColumns = { @JoinColumn(name = "person_id") }
    )
    @SQLJoinTableRestriction("rolle = 'CREATOR'")
    private Set<Person> creators;

    @ManyToMany()
    @JoinTable(
        name = "dvd_beteiligung", 
        joinColumns = { @JoinColumn(name = "produkt_nr") }, 
        inverseJoinColumns = { @JoinColumn(name = "person_id") }
    )
    @SQLJoinTableRestriction("rolle = 'DIRECTOR'")
    private Set<Person> directors;

    @Column(name = "laufzeit_minuten")
    private Integer runtime;

    @Column(name = "region_code")
    private Integer regionCode;
}