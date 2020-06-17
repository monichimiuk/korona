package dev.onichimiuk.marcin.warehouse.transport;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class WarehouseResponseDto {

    @NotEmpty
    private List<OrderItemDTO> orderItem;
}
