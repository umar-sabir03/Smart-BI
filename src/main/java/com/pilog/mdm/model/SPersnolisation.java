package com.pilog.mdm.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Blob;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "S_PERSNOLISATION")
public class SPersnolisation{

     @Column(name = "AUDIT_ID", length = 4000)
    private String auditId;
     @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "persId", column = @Column(name = "PERS_ID", nullable = false))
        , 
        @AttributeOverride(name = "type", column = @Column(name = "TYPE", nullable = false, length = 400))
        , 
        @AttributeOverride(name = "fileName", column = @Column(name = "FILE_NAME", nullable = false, length = 1000))})

    private SPersnolisationId id;
    @Column(name = "CONTENT")
    private Blob content;

    @Column(name = "CREATE_DATE")
    private LocalDate createDate;

    @Column(name = "CREATE_BY", length = 50)
    private String createBy;


    @Column(name = "EDIT_DATE")
    private LocalDate editDate;

    @Column(name = "EDIT_BY", length = 50)
    private String editBy;
}
