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
@Table(name = "tb_booking_schedule")
public class BookingSchedule {

    @Id
    @GeneratedValue
    private Integer scheduleId;

//    @ManyToOne(cascade = CascadeType.ALL)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JoinColumn(name = "booking_id")
//    private Booking booking;
    private Date workDate;
    private Date startTime;
    private Date endTime;
//    private String scheduleProcess;
    private String status;
//    private String addonData;
    private String ratingScore;
    private Date updateDate;
    private Integer updateBy;

    private String paymentStatus;
    private String cashbackStatus;

    @ManyToOne(cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "add_on_id")
    private ServiceAddOn addonId;


}
