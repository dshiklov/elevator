package com.example.elevator.data;

import lombok.Data;

@Data
public class Configuration {
    private Integer numberOfCabins;
    private Integer lowestLevel;
    private Integer highestLevel;
    private Integer personCapacity;
    private Integer speed;
}
