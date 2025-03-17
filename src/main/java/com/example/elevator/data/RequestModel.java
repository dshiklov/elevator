package com.example.elevator.data;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Data;

@Data
@ApplicationScoped
@Named("request")
public class RequestModel implements Comparable<RequestModel> {
    private Integer originatingLevel;
    private MotionModel direction;

    public RequestModel() {
    }

    @Override
    public int compareTo(RequestModel o) {
        return this.originatingLevel - o.originatingLevel;
    }
}
