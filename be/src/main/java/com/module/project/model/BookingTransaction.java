package com.module.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_booking_transaction")
public class BookingTransaction {
    @Id
    @GeneratedValue
    private Integer transactionId;
    @ManyToOne(cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "schedule_id")
    private BookingSchedule schedule;

//    @ManyToMany(cascade = CascadeType.ALL)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JoinColumn(name = "cleaner_ids")
//    private Set<Cleaner> cleaners;

    //    @ManyToOne(cascade = CascadeType.ALL)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JoinColumn(name = "user_id")
//    private User user;
    private double totalBookingPrice;
    private int totalBookingCleaner;
    private float totalBookingDate;
    //    private String bookingStatusBefore;
//    private String bookingStatusAfter;
//    private String changeStatusDescription;
    private Date createDate;


    private String status;
}
