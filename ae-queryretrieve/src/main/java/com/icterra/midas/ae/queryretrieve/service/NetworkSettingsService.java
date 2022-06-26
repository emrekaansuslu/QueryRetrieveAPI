package com.icterra.midas.ae.queryretrieve.service;

import com.icterra.midas.ae.queryretrieve.model.NetworkSettings;
import com.icterra.midas.ae.queryretrieve.repository.NetworkSettingsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NetworkSettingsService {

    @Autowired
    private NetworkSettingsRepository networkSettingsRepository;

    public NetworkSettings saveNetworkSettings(NetworkSettings networkSettings)
    {
        return networkSettingsRepository.save(networkSettings);
    }

    public List<NetworkSettings> findAllNetworkSettings()
    {
        return networkSettingsRepository.findAll();
    }
}
