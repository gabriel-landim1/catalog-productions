package com.landim.catalogProductions.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "productions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Production {

    @Id
    private String id;

    private String title;
    private Type type;
    private Integer yearRelease;
    private Status status;
    private Double grade;
    private String review;
}