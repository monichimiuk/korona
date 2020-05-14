package dev.onichimiuk.marcin.warehouse;

import dev.onichimiuk.marcin.geolocation.GeoLocation;
import dev.onichimiuk.marcin.warehouse.transport.OrderItemDTO;
import dev.onichimiuk.marcin.warehouse.transport.WarehouseOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/warehouse")
public class WarehouseServlet {
    private final Logger logger = LoggerFactory.getLogger(HttpServlet.class);

    private WarehouseService service;

    public WarehouseServlet(WarehouseService service) { this.service = service; }

    @GetMapping("{warehouseId}/geolocation")
    public ResponseEntity getGeoLocation(@Valid @PathVariable("warehouseId") Integer warehouseId){

        var warehouse = service.getWarehouseById(warehouseId);

        if (warehouse.isPresent()) {

            var x = warehouse.get().getX();
            var y = warehouse.get().getY();

            GeoLocation location = new GeoLocation() {
                @Override
                public long getX() { return x; }
                @Override
                public long getY() { return y; }
            };

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(location);
        }
        else{
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("There is no warehouse with id = "+warehouseId);
        }
    }

    @PostMapping("order")
    ResponseEntity<List<OrderItemDTO>> findNearestConfiguration(@Validated @RequestBody WarehouseOrder warehouseOrder) {

        var productsMap = warehouseOrder.getOrderItem().stream()
                .collect(Collectors.toMap(OrderItemDTO::getProductCode,OrderItemDTO::getNumber));

        var x = warehouseOrder.getLocation().getX();
        var y = warehouseOrder.getLocation().getY();

        GeoLocation location = new GeoLocation() {
            @Override
            public long getX() { return x; }
            @Override
            public long getY() { return y; }
        };

        return ResponseEntity.ok(service.findNearestConfiguration(location, productsMap));
    }


    //--------------------------------------------------------------------------------------------------------
    @GetMapping("/test/*")
    ResponseEntity<Object> oldTestMethod(HttpServletRequest req) {

        var requestParameterMap = req.getParameterMap();
        logger.info("WarehouseServlet GET request with parameters: " + requestParameterMap);

        var productsMap = requestParameterMap.entrySet()
                .stream()
                .filter(f -> !f.getKey().equals("x") & !f.getKey().equals("y") & Integer.parseInt(f.getValue()[0])!=0)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Integer.parseInt(e.getValue()[0])));

        var x = Integer.valueOf(req.getParameter("x"));
        var y = Integer.valueOf(req.getParameter("y"));

        GeoLocation location = new GeoLocation() {
            @Override
            public long getX() { return x; }
            @Override
            public long getY() { return y; }
        };

        try {
            return ResponseEntity.ok(service.testMethod(location, productsMap));
//            resp.setContentType("application/json;charset=UTF-8");
//            logger.info("WarehouseServlet GET response:\n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
        } catch (Exception e) {
//            resp.setContentType("text/html;charset=UTF-8");
//            mapper.writeValue(resp.getOutputStream(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    //----------------------------------------------------------------------------------------------------------
}