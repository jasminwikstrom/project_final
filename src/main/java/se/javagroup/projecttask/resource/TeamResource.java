package se.javagroup.projecttask.resource;

import org.springframework.stereotype.Component;
import se.javagroup.projecttask.repository.data.Team;
import se.javagroup.projecttask.service.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
        //service.createTeam(newTeam);
        return Response.created(locationOf(service.createTeam(newTeam))).build();
    }

    @PUT
    @Path("{id}")
    public Team updateTeam(@PathParam("id") String id, Team team) {
        return service.updateTeam(id, team);
    }
    @GET
    public Response getAll(){
        List<Team> teams = new ArrayList<>();
        for(Team t : service.getAllTeams()) {
            teams.add(t);
        }
        return Response.ok(teams).build();
    }
    private URI locationOf(Team team){
        return uriInfo.getBaseUriBuilder().path(uriInfo.getPathSegments().get(0).toString()).segment(team.getId().toString()).build();
    }

}
