package de.unileipzig.dbpraktikum.cli_interface.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Publisher model for Books
 */
@Entity
@Table(name = "verlag")
public class Publisher {
    @Id
    @Column(name = "verlag_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "publisher", fetch = FetchType.EAGER)
    private List<Book> books;
}
