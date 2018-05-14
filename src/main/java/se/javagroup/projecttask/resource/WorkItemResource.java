package se.javagroup.projecttask.resource;

import org.springframework.stereotype.Component;
import se.javagroup.projecttask.repository.data.WorkItem;
import se.javagroup.projecttask.resource.dto.WorkItemDto;
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
    public Response createUser(WorkItemDto workItem) {
        WorkItem workItemNew = service.createWorkItem(workItem);
        return Response.created(locationOf(workItemNew)).build();
    }

    @GET
    @Path("{workItemId}")
    public Response getWorkItem(@PathParam("workItemId") Long workItemId) {
        return service.getWorkItem(workItemId).map(Response::ok).orElse(Response.status(NOT_FOUND)).build();
    }

    @GET
    public Response getAllWorkItems(@QueryParam("status") String status,
                                    @QueryParam("issue") @DefaultValue("false") boolean issue,
                                    @QueryParam("text") String text) {
        return Response.ok(service.getAllWorkItems(status, issue, text)).build();
    }

    @PUT
    @Path("{workItemId}")
    public Response updateWorkItem(@PathParam("workItemId") Long workItemId, WorkItem workItem,
                                   @QueryParam("user") @DefaultValue("0") Long userId) {
        return Response.status(NO_CONTENT).header("Location", locationOf(service.updateWorkItem(workItemId, workItem, userId)).toString()).build();
    }

    @DELETE
    @Path("{workItemId}")
    public Response deleteWorkItem(@PathParam("workItemId") Long workItemId) {
        return service.deleteWorkItem(workItemId).map(w -> Response.status(NO_CONTENT)).orElse(Response.status(NOT_FOUND)).build();
    }

    private URI locationOf(WorkItem workItem) {
        return uriInfo.getBaseUriBuilder().path(uriInfo.getPathSegments().get(0).toString()).segment(workItem.getId().toString()).build();
    }
}
