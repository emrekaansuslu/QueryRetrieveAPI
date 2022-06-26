package com.icterra.midas.ae.queryretrieve.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "crawlerStatus")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class CrawlerStatus {

    @Id
    private String id;


    @Field("excel_index")
    private String index;
    private String date;
    private String patientNumber;
    private String status;
}
