package com.module.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_booking")
//@EqualsAndHashCode(exclude= {"userUpdate", "user"})
//@ToString(exclude = {"userUpdate", "user"})
@JsonIgnoreProperties({"userUpdate", "user"})
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String hostName;
    private String hostPhone;
    private String hostAddress;
    private String hostDistance;
    private String houseType;
    private int floorNumber;
    private float floorArea;
    private String status;
    @Column(name = "note", length = 1000)
    private String note;
    @Column(name = "review", length = 1000)
    private String review;
    private String rejectedReason;

    @JsonIgnore
    @Column(name = "rawRequest", length = 5000)
    private String rawRequest;

    @CreationTimestamp
    private Date createDate;

    @UpdateTimestamp
    private Date updateDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "cleaner_id")
    private Set<Cleaner> cleaners;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "customer_id")
    private User user;

    @ManyToOne()
    @JoinColumn(name = "updated_by_id")
    private User userUpdate;

}
