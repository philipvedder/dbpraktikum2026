package de.unileipzig.dbpraktikum.loader.model;

import java.sql.Date;
import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

/**
 * Typed Book model class. 
 * Extends Product with specific attributes. 
 */
public class Book extends Product {
    private String publisherName;
    private List<String> authorNames;
    private String isbn;
    private int pages;
    private Date publication;

    public Book(
        String asin, 
        ProductType type, 
        String title,
        Integer salesrank, 
        String imgUrl,
        List<String> similarProductIds,
        Offer offer,
        String publisherName,
        List<String> authorNames,
        String isbn,
        int pages,
        Date publication
    ) {
        super(asin, type, title, salesrank, imgUrl, similarProductIds, offer);
        
        this.authorNames = authorNames;
        this.isbn = isbn;
        this.pages = pages;
        this.publication = publication;
        this.publisherName = publisherName;
    }

    // Constructor for only Book specific variables
    public Book(
        String publisherName,
        List<String> authorNames,
        String isbn,
        int pages,
        Date publication
    ) {
        super();

        this.authorNames = authorNames;
        this.isbn = isbn;
        this.pages = pages;
        this.publication = publication;
        this.publisherName = publisherName;
    }

    //Getters
    public String getPublisherName() {
        return publisherName;
    }

    public List<String> getAuthorNames() {
        return authorNames;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getPages() {
        return pages;
    }

    public Date getPublication() {
        return publication;
    }

    
}
