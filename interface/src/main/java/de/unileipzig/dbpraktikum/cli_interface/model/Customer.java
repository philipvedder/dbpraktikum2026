package de.unileipzig.dbpraktikum.cli_interface.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Customer model for reviews
 */
@Entity
@Table(name = "kunde")
public class Customer {
    @Id
    @Column(name = "kunde_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
    private List<Review> reviews;
}
