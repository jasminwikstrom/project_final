package se.javagroup.projecttask.resource;

import org.springframework.stereotype.Component;
import se.javagroup.projecttask.repository.data.Team;
import se.javagroup.projecttask.service.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.Response.Status.CREATED;

@Path("teams")
@Component
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class TeamResource {
    private final Service service;

    @Context
    private UriInfo uriInfo;

    public TeamResource(Service service) {
        this.service = service;
    }

    @POST
    public Response createTeam(Team team) {
        Team newTeam = new Team(team.getName(), team.isStatus(), team.getTeamNumber());
        service.createTeam(newTeam);
        return Response.status(CREATED).header("Location", "teams/" + newTeam.getId()).build();
    }
    /*
    @PUT
    @Path("{id}")
    public Team updateTeam(@PathParam("id")Long id, String name, boolean status, Long teamNumber) {
        return service.updateTeam(id, name, status, teamNumber);
    }*/
    @GET
    public Response getAll(){
        List<Team> teams = new ArrayList<>();
        for(Team t : service.getAllTeams()) {
            teams.add(t);
        }
        return Response.ok(teams).build();
    }
}
