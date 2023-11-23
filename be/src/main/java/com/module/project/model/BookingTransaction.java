package com.module.project.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_booking_transaction")
public class BookingTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @OneToOne()
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @OneToMany(mappedBy = "bookingTransaction", cascade = CascadeType.ALL)
    private Set<BookingSchedule> bookingSchedules;

    @ManyToOne()
    @JoinColumn(name = "service_package_id")
    private ServicePackage servicePackage;

    private double totalBookingPrice;
    private int totalBookingCleaner;
    private float totalBookingDate;
    @CreationTimestamp
    private Date createDate;
    @UpdateTimestamp
    private Date updateDate;

    private String status;
}
