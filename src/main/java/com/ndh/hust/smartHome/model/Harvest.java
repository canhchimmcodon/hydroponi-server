package com.ndh.hust.smartHome.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="harvest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Harvest {
    @Id
    private String id;

    private String crop;

    private String timeToStart;

    private int pumpCapacity;

    private double fieldArea;

    private String method;
}
