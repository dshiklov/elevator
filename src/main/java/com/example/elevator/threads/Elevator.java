package com.example.elevator.threads;

import com.example.elevator.data.ElevatorModel;
import com.example.elevator.data.MotionModel;
import com.example.elevator.services.ElevatorService;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

//import java.util.ArrayList;
//
//import static java.lang.Thread.sleep;

public class Elevator implements Runnable {
//    @Inject
//    Logger log;

    private ElevatorService elevatorService;

    private Integer id;

    public void setId(Integer id) {
        this.id = id;
        System.out.println("Elevator ID set in Elevator thread: " + id);
    }

    public Integer getId() {
        return id;
    }

    public void setElevatorService(ElevatorService elevatorService) {
        this.elevatorService = elevatorService;
        System.out.println("Elevator service assigned");
    }

    @Override
    public void run() {
        ElevatorModel elevatorModel = elevatorService.getElevatorsArray().get(id);
        Thread currentThread = Thread.currentThread();
        System.out.println("Elevator is running on thread: " + currentThread.getId());
        System.out.println("Elevator service in runnable: " + elevatorService.toString());
        System.out.println("Condition !elevatorModel.getStops().isEmpty() =" + !elevatorModel.getStops().isEmpty());
        while ((!elevatorModel.getStops().isEmpty())
                || (elevatorModel.getCurrentFloor() != elevatorModel.getLowestFloor())
                || (elevatorModel.getCurrentFloor() != elevatorModel.getHighestFloor())) {

            System.out.println("In while ... elevatorModel.getCurrentFloor() = " + elevatorModel.getCurrentFloor());
            System.out.println("Stops: " + elevatorModel.getStops());
            int nextStop = elevatorModel.getStops().getFirst();
            //Start at level
            int atLevel = elevatorModel.getCurrentFloor();
            // Start moving simulation
            try {
                currentThread.sleep(1000);
                System.out.println("Elevator is ..... at level: " + atLevel);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Check if elevator reached a stop and simulate a stop (500ms)
            if (nextStop == elevatorModel.getCurrentFloor()) {
                elevatorModel.setCurrentMotion(MotionModel.STATIONARY);
                System.out.println("Elevator is stopping ..... at level: " + elevatorModel.getCurrentFloor());
                try {
                    currentThread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                elevatorModel.removeStop(elevatorModel.getCurrentFloor());
                System.out.println("Removed stop: " + elevatorModel.getCurrentFloor());
                elevatorModel.setCurrentMotion(elevatorModel.getDestinationMotion());
                elevatorModel.setCurrentMotion(MotionModel.STATIONARY);
//                elevatorModel.setDestinationMotion(MotionModel.STATIONARY);
                if (elevatorModel.getStops().isEmpty())
                    break;
            }
            // Change current level
            if (elevatorModel.getCurrentMotion() == MotionModel.GOING_UP ||
                    ( elevatorModel.getCurrentMotion() == MotionModel.STATIONARY &&
                      elevatorModel.getDestinationMotion() == MotionModel.GOING_UP)
            )
                elevatorModel.setCurrentFloor(atLevel + 1);
            else
                elevatorModel.setCurrentFloor(atLevel - 1);
        }
    }

}
