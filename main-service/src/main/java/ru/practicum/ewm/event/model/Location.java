package ru.practicum.ewm.event.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
public class Location {
    @Column(name = "location_latitude")
    private float lat;
    @Column(name = "location_longitude")
    private float lon;
}
