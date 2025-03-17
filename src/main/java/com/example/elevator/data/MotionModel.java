package com.example.elevator.data;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Any;

public enum MotionModel {
    GOING_UP,
    GOING_DOWN,
    STATIONARY
}
