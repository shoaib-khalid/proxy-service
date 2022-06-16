package com.kalsym.proxyservice.service;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.kalsym.proxyservice.model.PlatformConfig;
import com.kalsym.proxyservice.repository.PlatformConfigRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class PlatformConfigService {

    @Autowired
    PlatformConfigRepository platformConfigRepository;
    

    public List<PlatformConfig> getQueryPlatformConfig(String domain){
     
        Collection<PlatformConfig> result = platformConfigRepository.getPlatformConfigQuery(domain);

        List<PlatformConfig> output = new ArrayList<PlatformConfig>(result);

        return output;

    }

    public List<PlatformConfig> getQueryWildcardPlatformConfig(String domain, String platformType){
     
        Collection<PlatformConfig> result = platformConfigRepository.getPlatformConfigQueryWildcard(domain,platformType);

        List<PlatformConfig> output = new ArrayList<PlatformConfig>(result);

        return output;

    }
    
    
     
}