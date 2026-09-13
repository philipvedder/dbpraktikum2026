package de.unileipzig.dbpraktikum.cli_interface.model;

import java.util.Set;
import java.util.stream.Collectors;

import de.unileipzig.dbpraktikum.cli_interface.model.enums.DVDRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * DVD base model entity. 
 * Extends Product with specific attributes. 
 */
@Entity
@Table(name = "dvd")
@PrimaryKeyJoinColumn(name = "produkt_nr")
public class DVD extends Product {
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "dvd_format", 
        joinColumns = { @JoinColumn(name = "produkt_nr") }, 
        inverseJoinColumns = { @JoinColumn(name = "format_id") }
    )
    private Set<Format> formats;

    @OneToMany(mappedBy = "dvd", fetch = FetchType.EAGER)
    private Set<DVDParticipation> participations;

    @Column(name = "laufzeit_minuten")
    private Integer runtime;

    @Column(name = "region_code")
    private Integer regionCode;

    //Getters
    public Set<Format> getFormats() {
        return formats;
    }

    public Set<Person> getActors() {
        return getPeopleByRole(DVDRole.ACTOR);
    }

    public Set<Person> getCreators() {
        return getPeopleByRole(DVDRole.CREATOR);
    }

    public Set<Person> getDirectors() {
        return getPeopleByRole(DVDRole.DIRECTOR);
    }

    public Integer getRuntime() {
        return runtime;
    }

    public Integer getRegionCode() {
        return regionCode;
    }

    private Set<Person> getPeopleByRole(DVDRole role) {
        return participations.stream()
            .filter(participation -> participation.getRole() == role)
            .map(DVDParticipation::getPerson)
            .collect(Collectors.toSet());
    }
}
