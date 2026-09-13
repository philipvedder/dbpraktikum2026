package de.unileipzig.dbpraktikum.loader.model;

import java.sql.Date;
import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

/**
 * Typed Book model class. 
 * Extends Product with specific attributes. 
 */
public class BookCD extends Product {
    private String publisherName;
    private List<String> authorNames;
    private String isbn;
    private Date publication;
    private List<String> trackNames;

    public BookCD(
        String asin, 
        ProductType type, 
        String title,
        Integer salesrank, 
        String imgUrl,
        List<String> similarProductIds,
        List<Offer> offers,
        String publisherName,
        List<String> authorNames,
        String isbn,
        Date publication,
        List<String> trackNames
    ) {
        super(asin, type, title, salesrank, imgUrl, similarProductIds, offers);
        
        this.authorNames = authorNames;
        this.isbn = isbn;
        this.publication = publication;
        this.publisherName = publisherName;
        this.trackNames = trackNames;
    }

    // Constructor for only Book specific variables
    public BookCD(
        String publisherName,
        List<String> authorNames,
        String isbn,
        Date publication,
        List<String> trackNames
    ) {
        super();

        this.authorNames = authorNames;
        this.isbn = isbn;
        this.publication = publication;
        this.publisherName = publisherName;
        this.trackNames = trackNames;
    }

    public Book asBook() {
        return new Book(
            this.getAsin(), 
            ProductType.BOOK_CD, 
            this.getTitle(), 
            this.getSalesrank(), 
            this.getImgUrl(), 
            this.getSimilarProductIds(), 
            this.getOffers(), 
            this.getPublisherName(), 
            this.getAuthorNames(), 
            this.getIsbn(), 
            0, 
            this.getPublication()
        );
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

    public Date getPublication() {
        return publication;
    }

    public List<String> getTrackNames() {
        return trackNames;
    }
}
