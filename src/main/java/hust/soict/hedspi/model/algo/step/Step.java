package hust.soict.hedspi.model.algo.step;

public class Step {
  private State state;
  private String status;
  private int[] lineNo;

  public Step(State state, String status, int... lineNo) {
    this.state = state;
    this.status = status;
    this.lineNo = lineNo;
  }

  public void setState(State state) {
    this.state = state;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setLineNo(int[] lineNo) {
    this.lineNo = lineNo;
  }

  public State getState() {
    return state;
  }

  public String getStatus() {
    return status;
  }

  public int[] getLineNo() {
    return lineNo;
  }
}
