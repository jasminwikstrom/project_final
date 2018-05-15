package se.javagroup.projecttask.resource.mapper;

import se.javagroup.projecttask.service.exception.TeamNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collections;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Provider
public final class TeamNotFoundMapper implements ExceptionMapper<TeamNotFoundException> {
    @Override
    public Response toResponse(TeamNotFoundException e) {
        return Response.status(NOT_FOUND).entity(Collections.singletonMap("error", e.getMessage())).build();
    }
}
