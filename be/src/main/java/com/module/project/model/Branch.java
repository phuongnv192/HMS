package com.module.project.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_branch")
@ToString(exclude = {"cleaner", "services"})
@JsonIgnoreProperties({"cleaner", "services"})
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String branchName;
    private String branchAddress;

    @OneToOne()
    @JoinColumn(name = "manager_id")
    private User user;

    private String description;
    private String status;

    @CreationTimestamp
    private Date createDate;
    @UpdateTimestamp
    private Date updateDate;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    private Set<Cleaner> cleaner;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id")
    private Set<Service> services;
}
