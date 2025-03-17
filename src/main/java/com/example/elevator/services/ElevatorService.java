package com.example.elevator.services;

import com.example.elevator.data.*;
import com.example.elevator.threads.Elevator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import lombok.Data;
import lombok.ToString;
import org.jboss.logging.Logger;

import java.util.*;

@Data
@ApplicationScoped
@ToString
public class ElevatorService {
    @Inject
    public ElevatorService() {
    }
    @Inject
    Logger log;

    private boolean initialized = false;
    private ArrayList<ElevatorModel> elevatorsArray = new ArrayList<>();;
    private ArrayList<RequestModel> requestsArray = new ArrayList<>();
    private ArrayList<RequestModel> unacceptedRequestsArray = new ArrayList<>();
    private ArrayList<Elevator> runningElevatorsArray = new ArrayList<>();

    public String initializeElevator(Configuration configuration) {
        boolean added = false;

        for (int i = 0; i <configuration.getNumberOfCabins(); i++) {
            ElevatorModel elevatorCabin = new ElevatorModel(i,
                    configuration.getLowestLevel(),
                    configuration.getHighestLevel(),
                    configuration.getPersonCapacity(),
                    false,
                    configuration.getSpeed());
            elevatorCabin.setCurrentMotion(MotionModel.STATIONARY);
            elevatorCabin.setDestinationMotion(MotionModel.STATIONARY);
            added = elevatorsArray.add(elevatorCabin);
        }

        if (added) {
            log.info("Elevator initialized.");
            initialized = true;
            return "success";
        } else {
            log.info("Elevator initialization failed.");
            return "failed";
        }
    }

    public boolean addRequest(RequestModel requestModel) {
        boolean added = false;
        List<RequestModel> requests = Collections.synchronizedList(requestsArray);

        synchronized(requests) {
            requests.add(requestModel);
            requests.sort((a,b) -> { return ((RequestModel) a).compareTo(((RequestModel) b));} );
            added = true;
        }

        log.info("Added request: " + requestModel);
        return added;
    }

    public boolean removeRequest(RequestModel requestModel) {
        boolean removed = false;
        List<RequestModel> requests = Collections.synchronizedList(requestsArray);

        synchronized(requests) {
            requests.remove(requestModel);
            requests.sort((a,b) -> { return ((RequestModel) a).compareTo(((RequestModel) b));} );
            removed = true;
        }
        log.info("Removed request: " + requestModel);
        return removed;
    }

    public String buttonPressedOnLevel(Integer level, String direction) {
        List<ElevatorModel> elevators = Collections.synchronizedList(elevatorsArray);
        List<RequestModel> unacceptedRequests = Collections.synchronizedList(unacceptedRequestsArray);
        System.out.println("In buttonPressedOnLevel ....");
        try {
            MotionModel requestDirection = MotionModel.valueOf(direction);
            boolean directionAccepted = false;
            ElevatorModel elevatorAcceptingRequest = this.elevatorsArray.getFirst();
            Integer levelDifference = elevatorAcceptingRequest.getHighestFloor() - elevatorAcceptingRequest.getLowestFloor();

            synchronized(elevators) {

                Iterator i = elevators.iterator(); // Must be in synchronized block
                while (i.hasNext()) {
                    ElevatorModel elevator = (ElevatorModel) i.next();
                    if ( requestDirection == elevator.getDestinationMotion() || elevator.getCurrentMotion() == MotionModel.STATIONARY ) {
                        if ((elevator.getCurrentFloor() - level) <= levelDifference) {
                            levelDifference = Math.abs(elevator.getCurrentFloor() - level);
                            elevatorAcceptingRequest = elevator;
                        }
                        directionAccepted = true;
                    }
                }

                if (elevatorAcceptingRequest.getCurrentFloor() < level)
                    elevatorAcceptingRequest.setCurrentMotion(MotionModel.GOING_UP);
                else
                    elevatorAcceptingRequest.setCurrentMotion(MotionModel.GOING_DOWN);

                if (directionAccepted) {
                    elevatorAcceptingRequest.addStop(level);
                    elevatorAcceptingRequest.setDestinationMotion(requestDirection);
                    System.out.println("Added stop: " + level);
                } else {
                    synchronized(unacceptedRequests) {
                        RequestModel unacceptedRequest = new RequestModel();
                        unacceptedRequest.setDirection(requestDirection);
                        unacceptedRequest.setOriginatingLevel(level);
                        unacceptedRequests.add(unacceptedRequest);
                    }
                    System.out.println("Request added to list when an elevator becomes available. On level %s" + level + " and requested direction %s"+ requestDirection);
                }
                System.out.println("Elevator service before being passed to runnable: " + this.toString());
                Elevator elevarotRun = new Elevator();
                elevarotRun.setId(elevatorAcceptingRequest.getId());
                elevarotRun.setElevatorService(this);
                Thread runningElevator = new Thread(elevarotRun);
                runningElevator.setName("Elevator" + elevatorAcceptingRequest.getId());
                runningElevatorsArray.add(elevarotRun);
                runningElevator.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "success";
    }

    public String buttonPressed(Integer id, Integer level) {

        System.out.println("Button pressed in cabin: " + id + " and level " + level);
        Thread runningThread;

        ElevatorModel elevator = elevatorsArray.get(id);
        MotionModel wantedDirection;
        if (elevator.getCurrentFloor() < level) {
            elevator.setCurrentMotion(MotionModel.GOING_UP);
            wantedDirection = MotionModel.GOING_UP;
        } else {
            elevator.setCurrentMotion(MotionModel.GOING_DOWN);
            wantedDirection = MotionModel.GOING_DOWN;
        }
        System.out.println("In elevator " + id + ". Wanted direction: " + wantedDirection);
        if (elevator.getDestinationMotion() == wantedDirection || elevator.getDestinationMotion() == MotionModel.STATIONARY) {
            elevator.addStop(level);
            boolean elevatorHasThread = false;
            Set<Thread> threads = Thread.getAllStackTraces().keySet();
            for (Thread t : threads) {
                String name = t.getName();
                if (name.equalsIgnoreCase("Elevator" + id)) {
                    elevatorHasThread = true;
                    runningThread = t;
                }
            }
            System.out.println("After looping through Threads. Elevator has thread: " + elevatorHasThread);
            if (elevator.getCurrentFloor() < level)
                elevator.setCurrentMotion(MotionModel.GOING_UP);
            else
                elevator.setCurrentMotion(MotionModel.GOING_DOWN);
            if (!elevatorHasThread) {
                Elevator elevarotRun = new Elevator();
                elevarotRun.setId(id);
                elevarotRun.setElevatorService(this);
                Thread runningElevatorThread = new Thread(elevarotRun);
                runningElevatorThread.setName("Elevator" + id);
                runningElevatorsArray.add(elevarotRun);
                System.out.println("Before starting elevator " + id + ". Wanted direction: " + wantedDirection);
                runningElevatorThread.start();
            } else {


            }

        }
        else
            return "Elevator is going in opposite direction.";



        return "success";
    }

    public String getAllElevatorsDisplay(){
        StringBuffer sb = new StringBuffer();
        for (ElevatorModel elevator : elevatorsArray) {
            DisplayModel displayModel = elevator.getDisplayModel();
            sb.append("\nElevator id: ");
            sb.append(elevator.getId());
            sb.append("\nCurrent level: ");
            sb.append(displayModel.getLevel());
            sb.append("\n");
            sb.append(displayModel.getStatus());
            sb.append("\n");
        }
        return sb.toString();
    }


}
