package com.module.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

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
    private Date workDate;
    private Date startTime;
    private Date endTime;
    private String status;
    private String ratingScore;

    @UpdateTimestamp
    private Date updateDate;
    private Integer updateBy;

    private String paymentStatus;
    private String cashbackStatus;

    @ManyToOne(cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "add_on_id")
    private ServiceAddOn addonId;

    @ManyToOne(cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "transaction_id")
    private BookingTransaction booking;
}
