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
    @Path("{userNumber}/workitems")
    public Response getAllWorkItems(@PathParam("userNumber") Long userNumber) {
        Optional<User> user = service.getUserByUserNumber(userNumber);
        return user.map(t -> Response.ok(service.getAllWorkItemsForUser(user))).orElse(Response.status(NOT_FOUND)).build();
    }


    @GET
    @Path("/{userNumber}")
    public Response getUserByUnumber(@PathParam("userNumber") Long userNumber) {
        return service.getUserByUserNumber(userNumber).map(Response::ok).orElse(Response.status(Response.Status.NOT_FOUND)).build();
    }

    @PUT
    @Path("/{userNumber}")
    public Response updateUser(@PathParam("userNumber") Long userNumber, User user) {
        service.updateUser(userNumber, user);
        return Response.noContent().build();
    }

    @GET
    public List<User> getUsers(
            @QueryParam("firstname") String firstName,
            @QueryParam("lastname") String lastName,
            @QueryParam("username") String userName,
            @QueryParam("usernumber") String userNumber,
            @QueryParam("teamname") String teamName) {
        return service.getAllUsers(firstName, lastName, userName, teamName, userNumber);
    }

    @DELETE
    @Path("{userNumber}")
    public Response deleteUser(@PathParam("userNumber") Long userNumber) {
        if (service.deleteUser(userNumber)) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private URI locationOf(User user) {
        return uriInfo.getBaseUriBuilder().path(uriInfo.getPathSegments().get(0).toString()).segment(user.getUserNumber().toString()).build();
    }
}