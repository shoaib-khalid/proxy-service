package com.kalsym.proxyservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


/**
 * This model us used for authetication and authrization
 *
 * @author Sarosh
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class Auth {

    private Session session;

    private String role;
    private List<String> authorities;
    private String sessionType;
    
    @JsonCreator
    public Auth(@JsonProperty("session") Session session, 
        @JsonProperty("role") String role,  
        @JsonProperty("sessionType") String sessionType,
        @JsonProperty("authorities") List<String> authorities) {
            this.session = session;
            this.role = role;
            this.authorities = authorities;
            this.sessionType = sessionType;
    }

}

