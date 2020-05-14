package dev.onichimiuk.marcin.warehouse;

import dev.onichimiuk.marcin.geolocation.GeoLocation;
import dev.onichimiuk.marcin.geolocation.GeoService;
import dev.onichimiuk.marcin.warehouse.model.Warehouse;
import dev.onichimiuk.marcin.warehouse.transport.OrderItemDTO;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    public WarehouseService(GeoService geoService, WarehouseRepository repository) {
        this.repository = repository;
        this.geoService = geoService;
    }

    WarehouseRepository repository;
    GeoService geoService;

    public Optional<Warehouse> getWarehouseById(Integer warehouseId){
        var warehouse = repository.findById(Long.valueOf(warehouseId));

        return Optional.of(warehouse).orElse(null);
    }

    public List<OrderItemDTO> findNearestConfiguration(GeoLocation location, Map<String, Integer> map) {

        var warehouseList = repository.findAll();
        List<OrderItemDTO> itemList = new ArrayList<>();

        for (Map.Entry<String,Integer> entry : map.entrySet()) {

            List<Warehouse> warehousesWithProduct = warehouseList.stream()
                    .filter(w -> w.getProductStocks()
                            .stream()
                            .filter(productStock -> productStock.getProductCode().equals(entry.getKey()))
                            .anyMatch(productStock -> productStock.getAmount() >= entry.getValue()))
                    .collect(Collectors.toList());

            Warehouse nearestWarehouse = geoService.findNearestOfList(warehousesWithProduct, location);

            if(nearestWarehouse != null) {
                itemList.add(OrderItemDTO.builder()
                        .productCode(entry.getKey())
                        .number(entry.getValue())
                        .warehouseId(String.valueOf(nearestWarehouse.getId()))
                        .build());
            }else{
                itemList.add(OrderItemDTO.builder()
                        .productCode(entry.getKey())
                        .number(entry.getValue())
                        .build());
            }
        }

        return itemList;
    }

    //------------------------------------------------------------------------------------------------------------
    public Set<Warehouse> testMethod(GeoLocation location, Map<String, Integer> map) throws Exception {
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
    //-----------------------------------------------------------------------------------------------------------
}