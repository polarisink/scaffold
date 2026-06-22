package com.scaffold.audio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RcsDB {
    private Integer id;
    private Integer polarizationMode;
    private Double frequency;
    private Double startOfTheta;
    private Double endOfTheta;
    private Double startOfPhi;
    private Double endOfPhi;
    private Double thetaStep;
    private Double phiStep;
    private Integer thetaCount;
    private Integer phiCount;
    private Integer rcsSize;
    @JsonIgnore
    private byte[] rcsList;
    private float[][] rcsData;
    private Double normalRcs;
}
