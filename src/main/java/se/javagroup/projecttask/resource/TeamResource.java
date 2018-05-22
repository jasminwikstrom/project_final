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

import static javax.ws.rs.core.Response.Status.NO_CONTENT;

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
        return Response.created(locationOf(service.createTeam(team))).build();
    }

    @GET
    @Path("{teamId}")
    public Response getTeam(@PathParam("teamId") Long teamId) {
        return service.getTeam(teamId).map(Response::ok).orElse(Response.status(Response.Status.NOT_FOUND)).build();
    }

    @PUT
    @Path("{teamId}")
    public Response updateTeam(@PathParam("teamId") Long teamId, Team team) {
        service.updateTeam(teamId, team);
        return Response.noContent().build();
    }

    @PUT
    @Path("{teamId}/{userNumber}")
    public Response addUserToTeam(@PathParam("teamId") Long teamId, @PathParam("userNumber") Long userNumber) {
        service.addUserToTeam(teamId, userNumber);
        return Response.noContent().build();
    }

    @GET
    public Response getAll() {
        return Response.ok(service.getAllTeams()).build();
    }

    @GET
    @Path("{teamId}/workitems")
    public Response getWorkItemsForTeam(@PathParam("teamId") Long teamId) {
        //List<WorkItem> workItems = service.getAllWorkItemsForTeam(teamId);//onödigt?
        return Response.ok(service.getAllWorkItemsForTeam(teamId)).build();
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

    private URI locationOf(User user) {
        return uriInfo.getBaseUriBuilder().path(uriInfo.getPathSegments().get(0).toString()).segment(user.getId().toString()).build();
    }
}