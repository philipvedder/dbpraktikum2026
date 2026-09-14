package de.unileipzig.dbpraktikum.cli_interface.model;

import de.unileipzig.dbpraktikum.cli_interface.model.enums.DVDRole;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

/** Maps a person's role in a DVD production. */
@Entity
@Table(name = "dvd_beteiligung")
public class DVDParticipation {
    @EmbeddedId
    private DVDParticipationId id;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produkt_nr", nullable = false)
    private DVD dvd;

    @MapsId("personId")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    public Person getPerson() {
        return person;
    }

    public DVDRole getRole() {
        return id == null ? null : id.getRole();
    }
}
