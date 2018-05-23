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
        return Response.ok(service.getWorkItem(workItemId)).build();
    }

    @GET
    public Response getAllWorkItems(@QueryParam("status") String status,
                                    @QueryParam("issue") @DefaultValue("false") boolean issue,
                                    @QueryParam("text") String text) {
        return Response.ok(service.getAllWorkItems(status, issue, text)).build();
    }

    @PUT
    @Path("{workItemId}")
    public Response updateWorkItem(@PathParam("workItemId") Long workItemId, WorkItemDto workItem,
                                   @QueryParam("user") @DefaultValue("0") Long userNumber) {
        service.updateWorkItem(workItemId, workItem, userNumber);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{workItemId}")
    public Response deleteWorkItem(@PathParam("workItemId") Long workItemId) {
        service.deleteWorkItem(workItemId);
        return Response.noContent().build();
    }

    private URI locationOf(WorkItem workItem) {
        return uriInfo.getBaseUriBuilder().path(uriInfo.getPathSegments().get(0).toString())
                .segment(workItem.getId().toString()).build();
    }
}
