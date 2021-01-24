package com.mps.project.api.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_history")
public class ResourceBookingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "resource_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Resource resource;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @CreationTimestamp
    private LocalDateTime fromBookingTime;

    @NotNull
    private LocalDateTime toBookingTime;

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getFromBookingTime() {
        return fromBookingTime;
    }

    public LocalDateTime getToBookingTime() {
        return toBookingTime;
    }

    public void setToBookingTime(LocalDateTime toBookingTime) {
        this.toBookingTime = toBookingTime;
    }
}
