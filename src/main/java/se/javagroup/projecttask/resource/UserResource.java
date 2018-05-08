package se.javagroup.projecttask.resource;

import org.springframework.stereotype.Component;
import se.javagroup.projecttask.repository.data.User;
import se.javagroup.projecttask.service.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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
    public Response addUser(User useradd) {
        User user = new User();
        user.setFirstName(useradd.getFirstName());
        user.setLastName(useradd.getLastName());
        user.setTeam(useradd.getTeam());//NYTT från cla

        User save = service.saveUser(user);

        return Response.ok(save).build();
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
            @QueryParam("lastname") String lastName) {
        return service.getResult(firstName, lastName);
    }


}




