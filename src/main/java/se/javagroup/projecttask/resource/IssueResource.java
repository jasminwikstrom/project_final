package se.javagroup.projecttask.resource;

import org.springframework.stereotype.Component;
import se.javagroup.projecttask.repository.data.Issue;
import se.javagroup.projecttask.service.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

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
        Optional<Issue> newIssue = service.createIssue(issue, issue.getWorkItem().getId());
        return Response.created(locationOf(newIssue.get())).build();
    }

    @GET
    @Path("{id}")
    public Response getIssue(@PathParam("id") Long id) {
        return service.getIssue(id).map(Response::ok).orElse(Response.status(NOT_FOUND)).build();
    }

    @PUT
    @Path("{id}")
    public Issue updateIssue(@PathParam("id") Long id, Issue issue) {
        return service.updateIssue(id, issue);
    }

    @DELETE
    @Path("{id}")
    public Response deleteIssue(@PathParam("id") Long id) {
        Optional<Issue> issue = service.getIssue(id);
        issue.ifPresent(i -> service.deleteIssue(i));
        return issue.map(i -> Response.status(NO_CONTENT)).orElse(Response.status(NOT_FOUND)).build();
    }


    private URI locationOf(Issue issue) {
        return uriInfo.getBaseUriBuilder().path(uriInfo.getPathSegments().get(0).toString()).segment(issue.getId().toString()).build();
    }
}