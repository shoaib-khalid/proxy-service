package com.kalsym.proxyservice.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@Setter
@Entity
@Table(name = "platform_config")
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)

public class PlatformConfig {
    
    @Id
    private String platformId;

    private String platformName;

    private String platformType;

    private String domain;

    private String kubernetesSvcUrl;

    private String kubernetesSvcPort;

}

