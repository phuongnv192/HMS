package com.module.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_cleaner")
public class Cleaner {
    @Id
    @GeneratedValue
    private Integer id;
    private String address;
    private String idCard;
    private String status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id")
    public Branch branch;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id")
    public Set<Service> services;

    private String review;
}
