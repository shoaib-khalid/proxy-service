package com.kalsym.proxyservice.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.kalsym.proxyservice.enums.StoreAssetType;
import com.kalsym.proxyservice.model.Store;
import com.kalsym.proxyservice.repository.StoreRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class StoreService {

    @Autowired
    StoreRepository storeRepository;
    

    public List<Store> getStore(String domain){
     
        Collection<Store> result = storeRepository.getStoreByQuery(domain);

        List<Store> output = new ArrayList<Store>(result);

        return output;

    }
    
     
}