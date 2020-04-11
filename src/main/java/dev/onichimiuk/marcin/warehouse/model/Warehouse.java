package dev.onichimiuk.marcin.warehouse.model;

import dev.onichimiuk.marcin.geolocation.GeoLocation;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "warehouses")
public class Warehouse implements GeoLocation {
    @Id
    @GeneratedValue(generator = "inc")
    @GenericGenerator(name = "inc", strategy = "increment")
    private Long id;
    private String city;
    private long x;
    private long y;
//, fetch= FetchType.EAGER
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "warehouse")
    private List<ProductStock> productStocks;

    @Override
    public long getX() {
        return x;
    }

    @Override
    public long getY() {
        return y;
    }
}
