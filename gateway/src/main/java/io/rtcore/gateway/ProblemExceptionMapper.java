package io.rtcore.gateway;

import org.zalando.problem.ThrowableProblem;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces("application/problem+json")
public class ProblemExceptionMapper implements ExceptionMapper<ThrowableProblem> {

  @Override
  public Response toResponse(final ThrowableProblem problem) {
    return Response.status(problem.getStatus().getStatusCode(), problem.getTitle())
      .entity(problem)
      .header(HttpHeaders.CONTENT_TYPE, "application/problem+json")
      .build();
  }

}
