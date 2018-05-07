package se.javagroup.projecttask.resource;

import org.springframework.stereotype.Component;
import se.javagroup.projecttask.repository.data.WorkItem;
import se.javagroup.projecttask.service.Service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
    public Response createUser(WorkItem workItem) {
        WorkItem workItemNew = service.createWorkItem(workItem);
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(workItemNew.getId())).build();
        return Response.created(location).build();
    }
}
