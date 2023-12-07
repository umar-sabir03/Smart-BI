package com.pilog.mdm.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Blob;

@Data
@Entity
@Table(name = "S_PERSNOLISATION")
public class SPersnolisation extends CommonFields {

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
}
