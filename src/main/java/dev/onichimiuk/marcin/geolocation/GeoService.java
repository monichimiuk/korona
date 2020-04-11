package dev.onichimiuk.marcin.geolocation;

import dev.onichimiuk.marcin.warehouse.model.Warehouse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeoService {

    //Szukanie najbliższego magazynu z podanej listy względem lokacji zamawijącego lub null gdy lista pusta.
    public Warehouse findNearestOfList(List<Warehouse> warehouseList, GeoLocation ourLocation) {
        if (warehouseList.size() > 0) {
            Warehouse nearest = null;
            int minimum = Integer.MAX_VALUE;
            for (Warehouse w : warehouseList) {
                var check = calculateDistance(w, ourLocation);
                if (check < minimum) {
                    minimum = check;
                    nearest = w;
                }
            }
            return nearest;
        } else {
            return null;
        }
    }

    //Obliczanie odległości na płaszczyźnie między dwoma lokacjami.
    public Integer calculateDistance(GeoLocation geoLocation1, GeoLocation geoLocation2) {
        return (int) Math.sqrt((Math.pow(geoLocation2.getX() - geoLocation1.getX(), 2) + Math.pow(geoLocation2.getY() - geoLocation1.getY(), 2)));
    }

}
