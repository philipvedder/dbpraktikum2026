package de.unileipzig.dbpraktikum.cli_interface.model;

import de.unileipzig.dbpraktikum.cli_interface.model.enums.ProductType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/**
 * Product base model entity. Extended by Music, Book and DVD. 
 */
@Entity
@Table(name = "produkt")
@Inheritance(strategy = InheritanceType.JOINED)
public class Product {
    @Id
    @Column(name = "produkt_nr")
    private String id;

    @Column(name = "produkttyp")
    @Enumerated(EnumType.STRING)
    private ProductType type; //Type IN (Book, Music, DVD)

    @Column(name = "titel")
    private String title;

    @Column(name = "verkaufsrang")
    private Integer salesrank;

    @Column(name = "bild_url")
    private String imgUrl;

    // private List<String> similarProductIds;
    // private Offer offer; //Price information

    // Getter
    public String getId() {
        return id;
    }

    public ProductType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public Integer getSalesrank() {
        return salesrank;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}