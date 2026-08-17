package de.unileipzig.dbpraktikum.cli_interface.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Offer model for products / shops 
 */
@Entity
@Table(name = "angebot")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "angebot_id")
    private Long id;

    @ManyToOne()
    @JoinColumn(name="produkt_nr", nullable=false)
    private Product product;

    @Column(name = "zustand")
    private String condition;

    @Column(name = "preis")
    private BigDecimal price;

    @ManyToOne()
    @JoinColumn(name="filiale_id", nullable=false)
    private Shop shop;

    @Column(name = "waehrung")
    private String currency;

    //Getters
    
    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public String getCondition() {
        return condition;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Shop getShop() {
        return shop;
    }

    public String getCurrency() {
        return currency;
    }

    
}
