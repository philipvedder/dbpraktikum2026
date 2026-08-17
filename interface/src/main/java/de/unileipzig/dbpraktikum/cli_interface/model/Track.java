package de.unileipzig.dbpraktikum.cli_interface.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Track model for CDs
 */
@Entity
@Table(name = "musik_cd_titel")
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "track_id")
    private Long id;

    @ManyToOne()
    @JoinColumn(name="produkt_nr", nullable=false)
    private CD cd;

    @Column(name = "name")
    private String name;
}
