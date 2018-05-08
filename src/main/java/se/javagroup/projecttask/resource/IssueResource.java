package se.javagroup.projecttask.resource;

import org.springframework.stereotype.Component;
import se.javagroup.projecttask.repository.data.Issue;
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

@Path("issues")
@Component
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class IssueResource {

    private final Service service;

    @Context
    private UriInfo uriInfo;

    public IssueResource(Service service) {
        this.service = service;
    }

    @POST
    public Response createIssue(Issue issue) {
        Issue newIssue = service.createIssue(issue);
        return Response.created(locationOf(newIssue)).build();
    }

    private URI locationOf(Issue issue) {
        return uriInfo.getBaseUriBuilder().path(uriInfo.getPathSegments().get(0).toString()).segment(issue.getId().toString()).build();
    }
}
