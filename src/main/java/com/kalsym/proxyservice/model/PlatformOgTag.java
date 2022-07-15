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
@Table(name = "platform_og_tag")
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)

public class PlatformOgTag {
    
    @Id
    private Integer id;

    private String property;

    private String content;

    private String name;

    private String platformId;

    //will delete below

    // private String title;

    // private String description;

    // private String imageUrl;

    // private String platformType;

    // private String regionCountryId;

}

