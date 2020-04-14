package dev.onichimiuk.marcin.warehouse;

import dev.onichimiuk.marcin.geolocation.GeoLocation;
import dev.onichimiuk.marcin.geolocation.GeoService;
import dev.onichimiuk.marcin.warehouse.model.Warehouse;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    public WarehouseService(GeoService geoService, WarehouseRepository repository) {
        this.repository = repository;
        this.geoService = geoService;
    }

    WarehouseRepository repository;
    GeoService geoService;

    //Zwracanie listy najbliższych magazynów gdzie można dostać zamówione produkty w podanej ilości. Gdy nie ma jakiegoś produktu
    //zwracany jest błąd biznesowy z komunikatem, że w takiej ilości brakuje produktu w magazynach. Na wejściu przekazuje się
    //lokacje zamawiającego oraz mapę zamówienia produktów np.  key:rice value:78, key:pasta value:14.
    public Set<Warehouse> findNearestConfiguration(GeoLocation location, Map<String, Integer> map) throws Exception {
        var warehouseList = repository.findAll();
        Set<Warehouse> cumulatedWarehouses = new HashSet<>();

        for (Map.Entry<String,Integer> entry : map.entrySet()) {

            List<Warehouse> warehousesWithProduct = warehouseList.stream()
                    .filter(w -> w.getProductStocks()
                            .stream()
                            .filter(productStock -> productStock.getProductCode().equals(entry.getKey()))
                            .anyMatch(productStock -> productStock.getAmount() >= entry.getValue()))
                    .collect(Collectors.toList());

            Warehouse nearestWarehouse = geoService.findNearestOfList(warehousesWithProduct, location);
            if (nearestWarehouse != null) {
                cumulatedWarehouses.add(nearestWarehouse);
            }
            else {
                String errorMessage = "Produkt "+entry.getKey()+" nie występuje w ilości "+entry.getValue()+" w żadnym magazynie. Zmodyfikuj zamówienie.";
                throw new NullPointerException(errorMessage);
            }
        }
        return cumulatedWarehouses;
    }
}