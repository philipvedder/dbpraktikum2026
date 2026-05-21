package de.unileipzig.dbpraktikum.loader.model;

public class PriceRaw {
    private String price;
    private String mult;
    private String state;
    private String currency;
    
    public PriceRaw(String price, String mult, String state, String currency) {
        this.price = price;
        this.mult = mult;
        this.state = state;
        this.currency = currency;
    }

    @Override
    public String toString() {
        return price + " " + mult + " " + state + " " + currency;
    }
}
