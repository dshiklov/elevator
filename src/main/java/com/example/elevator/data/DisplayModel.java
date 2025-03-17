package com.example.elevator.data;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@ApplicationScoped
@Named("display")
public class DisplayModel {

    private Integer id;
    private Integer level;
    private MotionModel status;

    @Inject
    public DisplayModel() {

    }

    public String getStatus() {
        switch (status) {
            case MotionModel.GOING_DOWN:
                return "Going Down";
            case MotionModel.GOING_UP:
                return "Going Up";
        }
        return "";
    }

}
