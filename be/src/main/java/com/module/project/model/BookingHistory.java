package com.module.project.model;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
@Table(name = "tb_booking_history")
public class BookingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer historyId;
//
//    private Long bookingId;
//    private String hostName;
//    private String hostPhone;
//    private String hostAddress;
//    private String houseType;
//    private int floorNumber;
//    private float floorArea;
//
//    @ManyToOne()
//    @JoinColumn(name = "customer_id")
//    private User user;
//
//    @ManyToOne(cascade = CascadeType.ALL)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JoinColumn(name = "schedule_id")
//    private BookingSchedule schedule;
//
//    @ManyToOne(cascade = CascadeType.ALL)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JoinColumn(name = "add_on_id")
//    private ServiceAddOn addonId;
//
//    @ManyToMany(cascade = CascadeType.ALL)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JoinColumn(name = "cleaner_ids")
//    private Set<Cleaner> cleaners;
//
//    @ManyToOne(cascade = CascadeType.ALL)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JoinColumn(name = "user_id")
//    private User user;
//    private double totalBookingPrice;
//    private int totalBookingCleaner;
//    private float totalBookingDate;
//    private String bookingStatusBefore;
//    private String bookingStatusAfter;
//    private String changeStatusDescription;
//    private String paymentStatus;
//    private String cashBackStatus;
//    @CreationTimestamp
//    private Date createDate;

}
