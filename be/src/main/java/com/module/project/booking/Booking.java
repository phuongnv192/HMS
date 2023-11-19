package com.module.project.booking;

import java.util.Date;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.module.project.cleaner.Cleaner;
import com.module.project.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "tb_booking")
public class Booking {
    @Id
    @Column(unique = true)
    private String confirmId;
    private String hostName;
    private String hostPhone;
    private String hostAddress;
    private String hostDistance;
    private String houseType;
    private int floorNumber;
    private float floorArea;
    private String confirmStatus;
    private String txnStatus;
    private float totalDate;
    private String totalPrice;
    private int totalCleaner;
    private Date createDate;
    private Date updateDate;

    // @ManyToMany(cascade = CascadeType.ALL)
    // @OnDelete(action = OnDeleteAction.CASCADE)
    // @JoinColumn(name = "addon_id")
    // private AddOn addOn;

    // @ManyToOne(cascade = CascadeType.ALL)
    // @OnDelete(action = OnDeleteAction.CASCADE)
    // @JoinColumn(name = "schedule_ids")
    // private Set<Schedule> schedule;

    @ManyToMany(cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "cleaner_ids")
    private Set<Cleaner> cleaner;

    @ManyToOne(cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "customer_id")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "updated_by_id")
    private User userUpdate;

}
