package dev.onichimiuk.marcin.warehouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.onichimiuk.marcin.geolocation.GeoLocation;
import dev.onichimiuk.marcin.warehouse.model.Warehouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResponseErrorHandler;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class WarehouseServlet {
    private final Logger logger = LoggerFactory.getLogger(HttpServlet.class);

    private WarehouseService service;

    public WarehouseServlet(WarehouseService service) { this.service = service; }

    @GetMapping("warehouse/{warehouseId}/geolocation")
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

    // Jako zmienne x, y - przyjąłem współrzędne geograficzne miast Polski ze stopniami i minutami bez znaczników
    // np. Bydgoszcz  18°00'E  53°07'N  ma współrzędne x = 1800, y = 5307.
    // Współrzędne zamawiającego i wybór paramterów obsłużyłem tymczasowo w GUI pod localhostem:8777


    @GetMapping("/warehouses/*")
    ResponseEntity<Object> findNearestConfiguration(HttpServletRequest req) {

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
            return ResponseEntity.ok(service.findNearestConfiguration(location, productsMap));
//            resp.setContentType("application/json;charset=UTF-8");
//            logger.info("WarehouseServlet GET response:\n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
        } catch (Exception e) {
//            resp.setContentType("text/html;charset=UTF-8");
//            mapper.writeValue(resp.getOutputStream(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}