package se.javagroup.projecttask.resource;

import org.springframework.stereotype.Component;
import se.javagroup.projecttask.repository.data.Team;
import se.javagroup.projecttask.repository.data.User;
import se.javagroup.projecttask.repository.data.WorkItem;
import se.javagroup.projecttask.service.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
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

    @GET
    @Path("{id}")
    public Response getTeam(@PathParam("id") Long id){
        return Response.ok(service.getTeam(id)).build();
    }

    @POST
    public Response createTeam(Team team) {
        return Response.created(locationOf(service.createTeam(team))).build();
    }

    @PUT
    @Path("{id}")
    public Team updateTeam(@PathParam("id") String id, Team team) {
        return service.updateTeam(id, team);
    }

    @PUT
    @Path("{teamID}/{userID}")
    public Response addUserToTeam(@PathParam("teamID") Long teamID, @PathParam("userID") Long userID){
        User user = service.addUserToTeam(teamID, userID);
        return Response.ok(user).build();
    }

    @GET
    public Response getAll() {
        List<Team> teams = new ArrayList<>();
        for (Team t : service.getAllTeams()) {
            teams.add(t);
        }
        return Response.ok(teams).build();
    }

    @GET
    @Path("{id}/workitems")
    public Response getWorkItemsForTeam(@PathParam("id") Long teamID){
        List<WorkItem> workItems = service.getAllWorkItemsForTeam(teamID);
        return Response.ok(workItems).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteTeam(@PathParam("id") Long teamID){
        if(service.deleteTeam(teamID)){
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private URI locationOf(Team team) {
        return uriInfo.getBaseUriBuilder().path(uriInfo.getPathSegments().get(0).toString()).segment(team.getId().toString()).build();
    }

}
