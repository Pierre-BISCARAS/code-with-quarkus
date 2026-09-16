package org.acme.models.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_order")
@Getter @Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    public Customer customer;

    @Column(name = "store_id")
    public Long storeId;

    @Column(name = "quantity")
    public Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type")
    public DeliveryType deliveryType;

    @Column(name = "price")
    public BigDecimal price;

    @Column(name = "distance_km")
    public Double distanceKm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    public OrderStatus status;

    @Column(name = "ordered_at")
    public LocalDateTime orderedAt;

    @Column(name = "estimated_arrival_at")
    public LocalDateTime estimatedArrivalAt;
}
