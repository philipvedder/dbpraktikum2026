package de.unileipzig.dbpraktikum.cli_interface.model;

import java.io.Serializable;
import java.util.Objects;

import de.unileipzig.dbpraktikum.cli_interface.model.enums.DVDRole;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class DVDParticipationId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "produkt_nr")
    private String productId;

    @Column(name = "person_id")
    private Long personId;

    @Enumerated(EnumType.STRING)
    @Column(name = "rolle")
    private DVDRole role;

    protected DVDParticipationId() {
    }

    public DVDRole getRole() {
        return role;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DVDParticipationId)) {
            return false;
        }
        DVDParticipationId that = (DVDParticipationId) other;
        return Objects.equals(productId, that.productId)
            && Objects.equals(personId, that.personId)
            && role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, personId, role);
    }
}
