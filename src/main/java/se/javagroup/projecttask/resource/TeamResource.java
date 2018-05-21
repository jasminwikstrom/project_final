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
    @Path("{teamId}")
    public Response getTeam(@PathParam("teamId") Long teamId) {
        return Response.ok(service.getTeam(teamId)).build();
    }

    @POST
    public Response createTeam(Team team) {
        return Response.created(locationOf(service.createTeam(team))).build();
    }

    @PUT
    @Path("{teamId}")
    public Team updateTeam(@PathParam("teamId") Long teamId, Team team) {
        return service.updateTeam(teamId, team);
    }

    @PUT
    @Path("{teamId}/{userNumber}")
    public Response addUserToTeam(@PathParam("teamId") Long teamId, @PathParam("userNumber") Long userNumber) {
        User user = service.addUserToTeam(teamId, userNumber);
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
    @Path("{teamId}/workitems")
    public Response getWorkItemsForTeam(@PathParam("teamId") Long teamId) {
        List<WorkItem> workItems = service.getAllWorkItemsForTeam(teamId);
        return Response.ok(workItems).build();
    }

    @DELETE
    @Path("{teamId}")
    public Response deleteTeam(@PathParam("teamId") Long teamId) {
        if (service.deleteTeam(teamId)) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private URI locationOf(Team team) {
        return uriInfo.getBaseUriBuilder().path(uriInfo.getPathSegments().get(0).toString()).segment(team.getId().toString()).build();
    }
}
