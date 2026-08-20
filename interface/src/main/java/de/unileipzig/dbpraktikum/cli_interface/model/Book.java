package de.unileipzig.dbpraktikum.cli_interface.model;

import java.sql.Date;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * Book base model entity. 
 * Extends Product with specific attributes. 
 */
@Entity
@Table(name = "buch")
@PrimaryKeyJoinColumn(name = "produkt_nr")
public class Book extends Product {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="verlag_id", nullable=false)
    private Publisher publisher;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "buch_autor", 
        joinColumns = { @JoinColumn(name = "produkt_nr") }, 
        inverseJoinColumns = { @JoinColumn(name = "person_id") }
    )
    private Set<Person> authors;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "seitenzahl")
    private Integer pages;

    @Column(name = "erscheinungsdatum")
    private Date publication;

    //Getter
    public Publisher getPublisher() {
        return publisher;
    }

    public Set<Person> getAuthors() {
        return authors;
    }

    public String getIsbn() {
        return isbn;
    }

    public Integer getPages() {
        return pages;
    }

    public Date getPublication() {
        return publication;
    }

    
}