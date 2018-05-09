package se.javagroup.projecttask.resource;

import org.springframework.stereotype.Component;
import se.javagroup.projecttask.repository.data.User;
import se.javagroup.projecttask.service.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Optional;


@Path("/users")
@Component
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class UserResource {
    private final Service service;


    @Context
    private UriInfo uriInfo;

    public UserResource(Service service) {
        this.service = service;
    }

    @POST
    public Response addUser(User user) {
        return Response.created(locationOf(service.saveUser(user))).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") String id) {
        service.deleteUser(id);
        return Response.ok().build();
    }
/*
    @GET
    @Path("{id}/workitems")
    public Response getAllWorkItems(@PathParam("id") String id){
        return Response.ok(service.getAllWorkItemsForUser(id)).build();
    }
*/
    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") String id) {
        return service.getUser(id).map(Response::ok).orElse(Response.status(Response.Status.NOT_FOUND)).build();
    }

    @PUT
    @Path("{id}")
    public User updateUser(@PathParam("id") String id, User user) {
        return service.updateUser(id, user);
    }


    @GET
    public List<User> getResult(
            @QueryParam("firstname") String firstName,
            @QueryParam("lastname") String lastName,
            @QueryParam("username") String username,
            @QueryParam("teamname") String teamname) {

        return service.getResult(firstName, lastName, username, teamname);
    }
    private URI locationOf(User user) {
        return uriInfo.getBaseUriBuilder().path(uriInfo.getPathSegments().get(0).toString()).segment(user.getId().toString()).build();
    }
}




