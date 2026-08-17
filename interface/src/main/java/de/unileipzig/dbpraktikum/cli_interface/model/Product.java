package de.unileipzig.dbpraktikum.cli_interface.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.SQLJoinTableRestriction;

import de.unileipzig.dbpraktikum.cli_interface.model.enums.ProductType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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

    @Column(name = "rating_quantity")
    private Integer ratingQuantity;

    @Column(name = "avg_rating")
    private BigDecimal avgRating;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Offer> offers;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Review> reviews;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "aehnliches_produkt", 
        joinColumns = { @JoinColumn(name = "produkt_nr_1") }, 
        inverseJoinColumns = { @JoinColumn(name = "produkt_nr_2") }
    )
    private Set<Product> similarProductsLeft;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "aehnliches_produkt", 
        joinColumns = { @JoinColumn(name = "produkt_nr_2") }, 
        inverseJoinColumns = { @JoinColumn(name = "produkt_nr_1") }
    )
    private Set<Product> similarProductsRight;

    @ManyToMany()
    @JoinTable(
        name = "produkt_kategorie", 
        joinColumns = { @JoinColumn(name = "produkt_nr") }, 
        inverseJoinColumns = { @JoinColumn(name = "kategorie_id") }
    )
    private Set<Category> categories;

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

    public Set<Product> getSimilarProducts() {
        Set<Product> result = new HashSet<>();

        result.addAll(similarProductsLeft);
        result.addAll(similarProductsRight);

        return result;
    }

    public Integer getRatingQuantity() {
        return ratingQuantity;
    }

    public BigDecimal getAvgRating() {
        return avgRating;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public List<Review> getReviews() {
        return reviews;
    }
}