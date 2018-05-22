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
    @Path("{issueId}")
    public Response getIssue(@PathParam("issueId") Long issueId) {
        return service.getIssue(issueId).map(Response::ok).orElse(Response.status(NOT_FOUND)).build();
    }

    @PUT
    @Path("{issueId}")
    public Response updateIssue(@PathParam("issueId") Long issueId, Issue issue) {
        service.updateIssue(issueId, issue);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{issueId}")
    public Response deleteIssue(@PathParam("issueId") Long issueId) {
        Optional<Issue> issue = service.getIssue(issueId);
        issue.ifPresent(i -> service.deleteIssue(i));
        return issue.map(i -> Response.status(NO_CONTENT)).orElse(Response.status(NOT_FOUND)).build();
    }

    private URI locationOf(Issue issue) {
        return uriInfo.getBaseUriBuilder().path(uriInfo.getPathSegments().get(0).toString()).segment(issue.getId().toString()).build();
    }
}