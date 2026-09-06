package de.unileipzig.dbpraktikum.cli_interface.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Purchase order model for customers and products.
 */
@Entity
@Table(name = "bestellung")
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bestellung_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kunde_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produkt_nr", nullable = false)
    private Product product;

    @Column(name = "kaufzeitpunkt", nullable = false)
    private Timestamp purchaseTime;

    @Column(name = "kontonummer", nullable = false)
    private String accountNumber;

    @Column(name = "strasse", nullable = false)
    private String street;

    @Column(name = "hausnummer", nullable = false)
    private String houseNumber;

    @Column(name = "plz", nullable = false)
    private String postalCode;

    @Column(name = "ort", nullable = false)
    private String city;

    @Column(name = "land", nullable = false)
    private String country;

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Product getProduct() {
        return product;
    }

    public Timestamp getPurchaseTime() {
        return purchaseTime;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getStreet() {
        return street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }
}
