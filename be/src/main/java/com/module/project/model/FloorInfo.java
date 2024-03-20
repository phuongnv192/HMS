package com.module.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_floor_info")
public class FloorInfo {

    @Id
    private String floorKey;

    private Integer cleanerNumber;
    private Integer duration;
    private Long price;
    private String priceType;
    private String description;

    @UpdateTimestamp
    private Date updatedDate;
}
