package de.unileipzig.dbpraktikum.cli_interface.model;

import java.sql.Date;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * CD base model entity. 
 * Extends Product with specific attributes. 
 */
@Entity
@Table(name = "musik_cd")
@PrimaryKeyJoinColumn(name = "produkt_nr")
public class CD extends Product {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="label_id", nullable=false)
    private Label label;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "musik_cd_kuenstler", 
        joinColumns = { @JoinColumn(name = "produkt_nr") }, 
        inverseJoinColumns = { @JoinColumn(name = "person_id") }
    )
    private Set<Person> artists;

    @OneToMany(mappedBy = "cd", fetch = FetchType.EAGER)
    private List<Track> tracks;

    @Column(name = "erscheinungsdatum")
    private Date publication;

    // Getters
    public Label getLabel() {
        return label;
    }

    public Set<Person> getArtists() {
        return artists;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public Date getPublication() {
        return publication;
    }

    
}