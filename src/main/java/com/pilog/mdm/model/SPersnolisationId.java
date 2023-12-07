package com.pilog.mdm.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
public class SPersnolisationId implements java.io.Serializable {

    @Column(name = "PERS_ID", nullable = false, columnDefinition = "raw(16)")
    private String persId;
    @Column(name = "TYPE", nullable = false, length = 400)
    private String type;
    @Column(name = "FILE_NAME", nullable = false, length = 1000)
    private String fileName;

}