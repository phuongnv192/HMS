package com.module.project.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_booking_schedule")
@JsonIgnoreProperties({"bookingTransaction", "user"})
public class BookingSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;
    private LocalDate workDate;
    private String dayOfTheWeek;
    private Date startTime;
    private Date endTime;
    private String status;
    private String ratingScore;

    @UpdateTimestamp
    private Date updateDate;
    private Long updateBy;

    private double totalSchedulePrice;
    private String paymentStatus;
    private String paymentNote;
    private String cashbackStatus;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "transaction_id")
    private BookingTransaction bookingTransaction;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "add_on_id")
    private Set<ServiceAddOn> serviceAddOns;
}
