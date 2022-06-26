package com.icterra.midas.ae.queryretrieve.model;

import com.icterra.midas.ae.queryretrieve.util.ServiceStatusEnum;

public class ServiceStatusResult {

    private ServiceStatusEnum serviceStatusEnum;

    public ServiceStatusEnum getServiceStatusEnum() {
        return serviceStatusEnum;
    }

    public void setServiceStatusEnum(ServiceStatusEnum serviceStatusEnum) {
        this.serviceStatusEnum = serviceStatusEnum;
    }
}
