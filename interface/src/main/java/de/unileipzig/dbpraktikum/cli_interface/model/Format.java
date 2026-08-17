package de.unileipzig.dbpraktikum.cli_interface.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Format model for DVDs
 */
@Entity
@Table(name = "format")
public class Format {
    @Id
    @Column(name = "format_id")
    private Long id;

    @Column(name = "name")
    private String name;
}
