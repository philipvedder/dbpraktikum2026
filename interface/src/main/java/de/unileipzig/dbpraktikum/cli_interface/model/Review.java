package de.unileipzig.dbpraktikum.cli_interface.model;

import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne()
    @JoinColumn(name="produkt_nr", nullable=false)
    private Product product;

    //Getter Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
