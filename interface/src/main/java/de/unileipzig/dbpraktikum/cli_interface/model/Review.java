package de.unileipzig.dbpraktikum.cli_interface.model;

import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Review model for products
 */
@Entity
@Table(name = "rezension")
public class Review {
    @Id
    @Column(name = "rezension_id")
    private Long id;

    @ManyToOne()
    @JoinColumn(name="kunde_id", nullable=false)
    private Customer customer;

    @Column(name = "rezensionszeitpunkt")
    private Timestamp date;

    @Column(name = "punkte")
    private Integer points;

    @Column(name = "rezensionstext")
    private String text;
}
