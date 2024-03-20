package com.module.project.repository;

import com.module.project.model.FloorInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FloorTypeRepository extends JpaRepository<FloorInfo, String> {

    Optional<FloorInfo> findByFloorKey(String floorKey);
}
