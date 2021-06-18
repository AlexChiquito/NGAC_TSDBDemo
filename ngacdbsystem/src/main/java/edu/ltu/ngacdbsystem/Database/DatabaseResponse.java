package edu.ltu.ngacdbsystem.Database;

//import org.glassfish.jersey.internal.guava.MoreObjects;
import com.google.common.base.MoreObjects;

public class DatabaseResponse {
  private Long timeStamp;
  private String data;
  private Integer status;

  public DatabaseResponse(){
  }

  public DatabaseResponse(Long timeStamp, String data, Integer status){
    this.timeStamp = timeStamp;
    this.data = data;
    this.status = status;
  }

  public Long getTimeStamp() {
    return timeStamp;
  }

  public String getData() {
    return data;
  }

  public Integer getStatus() {
    return status;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("timeStamp", timeStamp).add("data", data).add("status", status).toString();
  }
}
