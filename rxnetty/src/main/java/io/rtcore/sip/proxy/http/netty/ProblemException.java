package io.rtcore.sip.proxy.http.netty;

public class ProblemException extends RuntimeException {

  /**
   * 
   */

  private static final long serialVersionUID = 1L;

  private final ImmutableProblem problem;

  public ProblemException(Problem problem) {
    this.problem = ImmutableProblem.copyOf(problem);
  }

  public String getMessage() {
    return problem.toString();
  }

  public ImmutableProblem problem() {
    return this.problem;
  }

}
