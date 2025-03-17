package com.example.elevator.data;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@ApplicationScoped
@Named("elevatorInstance")
public class ElevatorModel {
    private Integer id;
    private Integer lowestFloor;
    @Getter
    @Setter
    private Integer highestFloor;
    private Integer personCapacity;
    private Integer currentFloor;
    private boolean doorsAreOpen;
    private Integer speed;
    private MotionModel currentMotion;
    private MotionModel destinationMotion;
    private ArrayList<Integer> stopsArray = new ArrayList<>();
    final List<Integer> stops = Collections.synchronizedList(stopsArray);

    public ElevatorModel(Integer id,
                         Integer lowestFloor,
                         Integer highestFloor,
                         Integer personCapacity,
                         Integer currentFloor,
                         boolean doorsAreOpen,
                         Integer speed,
                         MotionModel currentMotion,
                         MotionModel destinationMotion,
                         ArrayList<Integer> stopsArray,
                         DisplayModel displayModel) {
        this.id = id;
        this.lowestFloor = lowestFloor;
        this.highestFloor = highestFloor;
        this.personCapacity = personCapacity;
        this.currentFloor = currentFloor;
        this.doorsAreOpen = doorsAreOpen;
        this.speed = speed;
        this.currentMotion = currentMotion;
        this.destinationMotion = destinationMotion;
        this.stopsArray = stopsArray;
        this.displayModel = displayModel;
    }

    //    @Getter
//    @Setter
    private DisplayModel displayModel;

    public ElevatorModel(Integer id,
                         Integer lowestFloor,
                         Integer highestFloor,
                         Integer personCapacity,
                         boolean doorsAreOpen,
                         Integer speed) {
        this.id = id;
        this.lowestFloor = lowestFloor;
        this.highestFloor = highestFloor;
        this.personCapacity = personCapacity;
        this.currentFloor = 0;
        this.doorsAreOpen = doorsAreOpen;
        this.speed = speed;

        DisplayModel displayModel = new DisplayModel();
        displayModel.setId(id);
        displayModel.setLevel(0);
        displayModel.setStatus(MotionModel.STATIONARY);
        this.displayModel = displayModel;
    }


    public boolean addStop(Integer stop) {
        boolean added = false;
        synchronized(this.stops) {
            if (this.stops.contains(stop)) {
                added = true;
            } else {
                this.stops.add(stop);
                Collections.sort(this.stops);
                added = true;
            }
        }
        return added;
    }

    public boolean removeStop(Integer stop) {
        boolean removed = false;
        synchronized(this.stops) {
            this.stops.remove(stop);
            Collections.sort(this.stops);
            removed = true;
        }
        return removed;
    }

    public boolean areDoorsOpen() {
        return this.doorsAreOpen;
    }

    public boolean setDoorsAreOpen(boolean doorsAreOpen) {
        return this.doorsAreOpen = doorsAreOpen;
    }

    public synchronized int getCurrentFloor() {
        return this.currentFloor;
    }

    public synchronized void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public DisplayModel getDisplayModel() {
        displayModel.setId(id);
        displayModel.setLevel(this.currentFloor);
        displayModel.setStatus(this.currentMotion);
        return displayModel;
    }
}
