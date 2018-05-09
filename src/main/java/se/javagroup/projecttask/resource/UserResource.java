package se.javagroup.projecttask.resource;

import org.springframework.stereotype.Component;
import se.javagroup.projecttask.repository.data.Team;
import se.javagroup.projecttask.repository.data.User;
import se.javagroup.projecttask.service.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;


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
        User newUser =
                new User(user.getId(), user.getFirstName(), user.getLastName(),
                        user.getUsername(), user.getUserNumber(), user.isStatus(), user.getTeam());

        //User save = service.saveUser(user);

        //return Response.ok(save).build();
        return Response.created(locationOf(service.saveUser(newUser))).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") String id) {
        service.deleteUser(id);
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    public User getUser(@PathParam("id") String id) {

        return service.getUser(id);
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




