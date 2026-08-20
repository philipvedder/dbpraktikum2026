package de.unileipzig.dbpraktikum.cli_interface.model;

import java.util.Set;

import org.hibernate.annotations.SQLJoinTableRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "dvd_format", 
        joinColumns = { @JoinColumn(name = "produkt_nr") }, 
        inverseJoinColumns = { @JoinColumn(name = "format_id") }
    )
    private Set<Format> formats;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "dvd_beteiligung", 
        joinColumns = { @JoinColumn(name = "produkt_nr") }, 
        inverseJoinColumns = { @JoinColumn(name = "person_id") }
    )
    @SQLJoinTableRestriction("rolle = 'ACTOR'")
    private Set<Person> actors;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "dvd_beteiligung", 
        joinColumns = { @JoinColumn(name = "produkt_nr") }, 
        inverseJoinColumns = { @JoinColumn(name = "person_id") }
    )
    @SQLJoinTableRestriction("rolle = 'CREATOR'")
    private Set<Person> creators;

    @ManyToMany(fetch = FetchType.EAGER)
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

    //Getters
    public Set<Format> getFormats() {
        return formats;
    }

    public Set<Person> getActors() {
        return actors;
    }

    public Set<Person> getCreators() {
        return creators;
    }

    public Set<Person> getDirectors() {
        return directors;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public Integer getRegionCode() {
        return regionCode;
    }

    
}