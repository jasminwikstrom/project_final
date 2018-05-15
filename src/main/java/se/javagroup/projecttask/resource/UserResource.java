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

import static javax.ws.rs.core.Response.Status.NOT_FOUND;


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
        return Response.created(locationOf(service.createUser(user))).build();
    }

    @GET
    @Path("{userId}/workitems")
    public Response getAllWorkItems(@PathParam("userId") String userId) {
        Optional<User> user = service.getUser(userId);
        return user.map(t -> Response.ok(service.getAllWorkItemsForUser(user))).orElse(Response.status(NOT_FOUND)).build();
    }

    @GET
    @Path("/{userId}")
    public Response getUser(@PathParam("userId") String userId) {
        return service.getUser(userId).map(Response::ok).orElse(Response.status(Response.Status.NOT_FOUND)).build();
    }


    @PUT
    @Path("{id}")
    public User updateUser(@PathParam("id") String id, User user) {
        return service.updateUser(id,  user);
    }



    @GET
    public List<User> getResult(
            @QueryParam("firstname") String firstName,
            @QueryParam("lastname") String lastName,
            @QueryParam("username") String username,
            @QueryParam("usernumber") String usernumber,
            @QueryParam("teamname") String teamname) {

        return service.getResult(firstName, lastName, username, teamname, usernumber);
    }

    @DELETE
    @Path("{userId}")
    public Response deleteUser(@PathParam("userId") Long userId) {
        if (service.deleteUser(userId)) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private URI locationOf(User user) {
        return uriInfo.getBaseUriBuilder().path(uriInfo.getPathSegments().get(0).toString()).segment(user.getId().toString()).build();
    }
}




