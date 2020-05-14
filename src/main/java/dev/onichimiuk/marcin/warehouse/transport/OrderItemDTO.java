package dev.onichimiuk.marcin.warehouse.transport;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Builder
@Data
public class OrderItemDTO {

    @NotBlank
    private String productCode;

    @Min(1)
    private Integer number;

    private String warehouseId;
}
