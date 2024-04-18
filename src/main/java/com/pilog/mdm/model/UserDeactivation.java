package com.pilog.mdm.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_deactivation")
@Data
public class UserDeactivation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "userName")
    private String userName;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "deactivation_date")
    private LocalDateTime deactivationDate;

}
