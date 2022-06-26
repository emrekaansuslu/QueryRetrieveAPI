package com.icterra.midas.ae.queryretrieve.repository;

import com.icterra.midas.ae.queryretrieve.model.NetworkSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NetworkSettingsRepository extends MongoRepository<NetworkSettings, String> {

}
