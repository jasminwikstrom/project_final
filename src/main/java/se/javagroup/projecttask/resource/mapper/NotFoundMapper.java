package se.javagroup.projecttask.resource.mapper;

import se.javagroup.projecttask.service.exception.NotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collections;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Provider
public final class NotFoundMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException e) {
        return Response.status(NOT_FOUND).entity(Collections.singletonMap("error", e.getMessage())).build();
    }
}
