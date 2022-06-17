package com.kalsym.proxyservice.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kalsym.proxyservice.enums.StoreAssetType;
import com.kalsym.proxyservice.model.Store;


@Repository
public interface StoreRepository extends JpaRepository<Store,String> {
    
    @Query(
        value =
        " SELECT s "
        +"FROM Store s "
        +"WHERE s.domain LIKE CONCAT('%', :domain ,'%')"

    )
    Collection<Store> getStoreByQuery(
        @Param("domain") String domain
    );
   
}

