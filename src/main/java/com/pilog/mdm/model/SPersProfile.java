package com.pilog.mdm.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "S_PERS_PROFILE")
@Getter
@Setter
@RequiredArgsConstructor
public class SPersProfile extends  CommonFields{

        @Column(name = "AUDIT_ID", length = 100)
        private String auditId;
        @Id
        @Column(name = "PERS_ID")
        private String persId;

        @Column(name = "REGION", length = 50)
        private String region;

        @Column(name = "INSTANCE", length = 30)
        private String instance;

        @Column(name = "PLANT", length = 30)
        private String plant;

        @Column(name = "LOCALE", length = 5)
        private String locale;

        @Column(name = "ROLE_ID", length = 40)
        private String roleId;
        @Column(name = "DEFAULT_IND", nullable = false, length = 4)
        private String defaultInd;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PERS_ID", nullable = false, insertable = false, updatable = false)
        private SPersDetail SPersDetail;

}

