# Elevator Simulator Design

## Requirements

Develop a software that simulates an elevator system. 
- The system will comprise one or more elevator cabins.
- The elevator will travel between lowest and highest level defined at initialization.
- The speed will be a parameter determining the distance in a number of levels an elevator in motion needs to be before a a stop can be added in the direction of travel. This parameter will be provided at the time of initialization. 
- On each level there will be up and down request buttons.
- The elevators will have a capacity expressed in number of people.
- When an up or down button is pressed on any level the nearest elevator will accept the request.
- When an person on the cabin presses a level button which is in the direction of travel and at least X number of stops (defined by the speed parameter), the elevator will accept the stop.
- If a person in the cabin of the elevator preses a level button opposite of the direction of travel or less than X stops (see above) before the level, the request will not be accepted. 
- The elevator should not move if the load is over capacity*.

## Structure of the project

### Packages

The project is organized under the /com/example/elevator folder. There are four packages/folders.
- data
  - The data folder contains the classes that represent the data model.
- resources
  - This folder contains the REST Controller.
- services
  - The ElevatorService class resides here. 
- threads
  - This folder contains a class that extend the Runnable interface.

#### ElevatorResource Class
This class implements the functionality of the REST service and contains its endpoints. The root path is */elevator*.
There are four methods that accept http request and in turn invoke methods on the ElevatorService class. 
- *postInitialize*
- *getLocation*
- *postPressButtonOnLevel*
- *postPressButton*

#### ElevatorService Class
This class is responsible for initializing the elevator system, reacting to requests from inside the elevator cabins and each level.
This class also has a method that provides a display information that can be observed externally (e.g. using watch curl) 

#### Elevator Class 
This class implements the Runnable interface and is provided to a Thread that is started when an action is invoked.

## Assumptions

- The lowest level could be a negative number, but it will not be tested. 
- The complexity of the system increases when the system needs to track the load of passengers, hence it will not be implemented at this time.

# NOTE
The implementation is not complete at this time. There are several issues that need to be addressed.
The system successfully initializes and responds to requests from the levels, but not completely from inside the cabin.
The code contains numerous System.out.println that previously were log.info, but replaced for clarity during debugging.
There are no test classes at this time.