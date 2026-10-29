package com.store.app.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.app.store.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {

	boolean existsByNameAndCity(String name, String city);

}
