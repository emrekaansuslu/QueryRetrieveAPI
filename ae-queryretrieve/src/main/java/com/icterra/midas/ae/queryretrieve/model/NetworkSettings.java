package com.icterra.midas.ae.queryretrieve.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "networksettings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NetworkSettings {

    @Id
    private String id;

    private String deviceName;
    private String aeTitle;
    private String hostName;
    private Integer port;
    private boolean isServer;
}
