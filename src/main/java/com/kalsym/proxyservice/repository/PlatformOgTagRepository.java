package com.kalsym.proxyservice.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kalsym.proxyservice.model.PlatformConfig;
import com.kalsym.proxyservice.model.PlatformOgTag;


@Repository
public interface PlatformOgTagRepository extends JpaRepository<PlatformOgTag,String> {
    
    // List<PlatformOgTag> findByPlatformTypeAndRegionCountryId(@Param("platformType") String platformType, @Param("regionCountryId") String regionCountryId);
    
    List<PlatformOgTag> findByPlatformId(@Param("platformId") String platformId);

   
}
