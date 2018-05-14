package se.javagroup.projecttask.resource;

import org.springframework.stereotype.Component;
import se.javagroup.projecttask.repository.data.WorkItem;
import se.javagroup.projecttask.resource.dto.DtoWorkItem;
import se.javagroup.projecttask.service.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

@Path("workitems")
@Component
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class WorkItemResource {

    private final Service service;

    @Context
    private UriInfo uriInfo;

    public WorkItemResource(Service service) {
        this.service = service;
    }

    @POST
    public Response createUser(DtoWorkItem workItem) {
        WorkItem workItemNew = service.createWorkItem(workItem);
        return Response.created(locationOf(workItemNew)).build();
    }

    @GET
    @Path("{id}")
    public Response getWorkItem(@PathParam("id") Long id) {
        return service.getWorkItem(id).map(Response::ok).orElse(Response.status(NOT_FOUND)).build();
    }


    @GET
    public Response getAllWorkItems(@QueryParam("status") String status, @QueryParam("issue") @DefaultValue("false") boolean issue, @QueryParam("text") String text){
       return Response.ok(service.getAllWorkItems(status, issue, text)).build();

    }

    @PUT
    @Path("{id}")
    public Response updateWorkItem(@PathParam("id") Long id, WorkItem workItem,
                                   @QueryParam("user") @DefaultValue("0") Long userId){
        return Response.status(NO_CONTENT).header("Location", locationOf(service.updateWorkItem(id, workItem, userId)).toString()).build();

    }

    private URI locationOf(WorkItem workItem) {
        return uriInfo.getBaseUriBuilder().path(uriInfo.getPathSegments().get(0).toString()).segment(workItem.getId().toString()).build();
    }
}
