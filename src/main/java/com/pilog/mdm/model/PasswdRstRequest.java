package com.pilog.mdm.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "PASSWORD_RESET_REQUEST")
public class PasswdRstRequest {
    @Id
    @GenericGenerator(name = "gen1", strategy ="com.pilog.mdm.model.MyGenerator")
    @GeneratedValue(generator = "gen1")
    @Column(name = "ID")
    private String id;
    private String userName;
    private String otp;
    private Boolean isUsed;
    private Integer emailSentCount;
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date createdAt;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date updatedAt;

}
