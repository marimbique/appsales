package com.store.app.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.app.warehouse.entity.Warehouse;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

}
