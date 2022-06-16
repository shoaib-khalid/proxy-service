package com.kalsym.proxyservice.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kalsym.proxyservice.model.PlatformConfig;


@Repository
public interface PlatformConfigRepository extends JpaRepository<PlatformConfig,String> {
    
    @Query(
        value =
        " SELECT pc "
        +"FROM PlatformConfig pc "
        +"WHERE domain = :domain"
    )
    Collection<PlatformConfig> getPlatformConfigQuery(
        @Param("domain") String domain
    );

    @Query(
        value =
        " SELECT pc "
        +"FROM PlatformConfig pc "
        +"WHERE platformType = :platformType "
        +"AND domain LIKE CONCAT('%', :domain ,'%')"
    )
    Collection<PlatformConfig> getPlatformConfigQueryWildcard(
        @Param("domain") String domain,
        @Param("platformType") String platformType
    );
   
}

