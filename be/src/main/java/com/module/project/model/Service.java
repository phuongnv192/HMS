package com.module.project.model;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_service")
public class Service {
    @Id
    @GeneratedValue
    public Integer id;
    public String serviceName;
    public String serviceDescription;
    public String serviceStatus;
    public String paymentMethod;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_type_id")
    public ServiceType serviceType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_package_id")
    public ServicePackage servicePackage;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id")
    public Set<Branch> branch;
}
