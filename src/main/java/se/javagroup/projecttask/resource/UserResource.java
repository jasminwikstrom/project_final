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

import static javax.ws.rs.core.Response.Status.*;

@Path("users")
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
    @ApiKey
    public Response createUser(User user) {
        User result = service.createUser(user);
        return Response.status(CREATED).header("Location", "customers/" + result.getId()).build();
    }


    @GET
    public Response getAll() {
        List<User> users = service.getAllUsers();


        return Response.ok(users).build();
    }

    @GET
    @Path("{id}")
    public Response getUserr(@PathParam("id") Long id) {
        return service.getUser(id)
                .map(Response::ok)
                .orElse(Response.status(NOT_FOUND))
                .build();
    }

    @ApiKey
    @PUT
    @Path("{id}")
    public void updateUser(@PathParam("id") Long id, User user) {
        // if (id == customer.getId()) ... else BAD_REQUEST
        service.updateUser(user);
    }

    @ApiKey
    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        return service.deleteCustomer(id)
                .map(c -> Response.status(NO_CONTENT))
                .orElse(Response.status(NOT_FOUND))
                .build();
    }

}
