package se.javagroup.projecttask.resource.mapper;

import se.javagroup.projecttask.service.exception.BadInputException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collections;

@Provider
public final class BadInputMapper implements ExceptionMapper<BadInputException> {

    @Override
    public Response toResponse(BadInputException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity(Collections.singletonMap("error", exception.getMessage())).build();
    }
}
