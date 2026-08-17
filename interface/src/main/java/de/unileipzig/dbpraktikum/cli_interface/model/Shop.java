package de.unileipzig.dbpraktikum.cli_interface.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Shop model for products / offers
 */
@Entity
@Table(name = "filiale")
public class Shop {
    @Id
    @Column(name = "filiale_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "strasse")
    private String street;

    @Column(name = "plz")
    private String zip;

    @OneToMany(mappedBy = "shop", fetch = FetchType.EAGER)
    private List<Offer> offers;
}
