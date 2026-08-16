package de.unileipzig.dbpraktikum.cli_interface.model;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    // @Column(name = "verkaufsrang")
    // private String publisherName;

    // @Column(name = "verkaufsrang")
    // private List<String> authorNames;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "seitenzahl")
    private int pages;

    @Column(name = "erscheinungsdatum")
    private Date publication;
}