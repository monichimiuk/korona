create table product_stocks (
    id long,
    productCode varchar(100) not null,
    amount long,
    warehouse_id long not null,
    foreign key (warehouse_id) references warehouses(id)
);