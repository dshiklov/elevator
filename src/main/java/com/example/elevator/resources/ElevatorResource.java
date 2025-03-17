package com.example.elevator.resources;

import com.example.elevator.data.Configuration;
import com.example.elevator.services.ElevatorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/elevator")
@ApplicationScoped
public class ElevatorResource {
    @Inject ElevatorService elevatorService;

    @Path("initialize/{numberOfCabins}/lowest/{lowestLevel}/highest/{highestLevel}/capacity/{persons}/speed/{speed}")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String postInitialize(@PathParam("numberOfCabins") Integer numberOfCabins,
                                 @PathParam("lowestLevel") Integer lowestLevel,
                                 @PathParam("highestLevel") Integer highestLevel,
                                 @PathParam("persons") Integer personCapacity,
                                 @PathParam("speed") Integer speed) {
        if (elevatorService.isInitialized())
            return "Elevator service is already initialized.";

        Configuration configuration = new Configuration();
        configuration.setNumberOfCabins(numberOfCabins);
        configuration.setLowestLevel(lowestLevel);
        configuration.setHighestLevel(highestLevel);
        configuration.setPersonCapacity(personCapacity);
        configuration.setSpeed(speed);

        return elevatorService.initializeElevator(configuration);
    }

    @Path("location/{id}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getLocation(@PathParam("id") Integer id) {
        return elevatorService.getAllElevatorsDisplay();
    }

    @Path("press/{direction}/button-on-level/{level}")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String postPressButtonOnLevel(@PathParam("level") Integer level, @PathParam("direction") String direction) {
        return elevatorService.buttonPressedOnLevel(level, direction);
    }

    @Path("press-button/{level}/in-cabin/{id}")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String postPressButton(@PathParam("level") Integer level, @PathParam("id") Integer id) {
        return elevatorService.buttonPressed(id, level);
    }

}
