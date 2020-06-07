package dev.onichimiuk.marcin.warehouse.transport;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class WarehouseOrderDto {

    private Location location;

    @NotEmpty
    private List<OrderItemDTO> orderItem;

    @Data
    public class Location{
        private Integer x;
        private Integer y;
    }
}
