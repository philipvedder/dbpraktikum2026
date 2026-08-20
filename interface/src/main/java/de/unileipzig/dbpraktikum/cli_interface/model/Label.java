package de.unileipzig.dbpraktikum.cli_interface.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Label model for CDs
 */
@Entity
@Table(name = "label")
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "label_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "label", fetch = FetchType.EAGER)
    private List<CD> cds;

    //Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<CD> getCds() {
        return cds;
    }

    
}
