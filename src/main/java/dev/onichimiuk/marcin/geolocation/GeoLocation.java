package dev.onichimiuk.marcin.geolocation;

import org.springframework.stereotype.Component;

@Component
public interface GeoLocation {
    long getX();
    long getY();
}
