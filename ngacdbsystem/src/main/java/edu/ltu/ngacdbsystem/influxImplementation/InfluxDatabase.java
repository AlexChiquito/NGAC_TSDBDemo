package edu.ltu.ngacdbsystem.influxImplementation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.moshi.Json;

import edu.ltu.ngacdbsystem.DatabaseAuthorisation;
import edu.ltu.ngacdbsystem.IDatabase;
import edu.ltu.ngacdbsystem.tools.JsonTools;

import org.checkerframework.checker.units.qual.Length;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;
import org.influxdb.dto.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class InfluxDatabase extends IDatabase {

  private InfluxDB influxDB = null;
  private BatchPoints batchPoints = null;
  private int dataPointCounter = 0;
  private final int BATCH_SIZE = 1;

  // ####################################################################################
  // ### Constructor
  // ####################################################################################
  /**
   *
   * @param auth
   */
  public InfluxDatabase(DatabaseAuthorisation auth){
    dbAuth = auth;
  }
  // ####################################################################################


  // ####################################################################################
  // ### Implemented IDatabase methods
  // ####################################################################################

  /**
   *
   * @return
   */
  @Override
  public boolean connect() {
    try{
      System.out.println("Connecting to " + dbAuth.getDbPath() + "...");
      influxDB = InfluxDBFactory.connect(dbAuth.getDbPath(), dbAuth.getUserName(), dbAuth.getPwd());
      System.out.println("Connected");
      influxDB.setDatabase(dbAuth.getDbName());
      return true;
    }catch(Exception ex){
      System.out.println("Could not connect to database: " + dbAuth.getDbPath());
      return false;
    }
  }

  /**
   *
   * @return
   */
  @Override
  public boolean close() {
    System.out.println("Closing database connection...");
    try{
      influxDB.close();
      influxDB = null;
      System.out.println("Database connection is now closed");
      return true;
    }
    catch (Exception ex){
        System.out.println("Error when closing the database connection! "+ex);
        return false;
    }

  }

  /**
   *
   * @param json
   * @return
   */
  @Override
  public int insertData(JsonObject json) {
    try{
      if(batchPoints == null){
        batchPoints = createNewBatchPoints();
      }
      createNewMeasurementPoint(json);
      if(dataPointCounter >= BATCH_SIZE){
        writeToDB();
      }
    }
    catch(Exception ex){
      System.out.println(ex.getMessage());
    }
    
    return 0;
  }

  /**
   *
   * @param request
   * @return
   */
  @Override
  public JsonArray requestData(JsonObject request) {
    try{
      System.out.println("Creating query to the database..." + request.get("columns").getAsString());
      Query query = new Query(requestQuery(request));
      connect();
      QueryResult queryResult = influxDB.query(query);
      close();
      List<QueryResult.Result>  results = queryResult.getResults();
      List<String> columns = results.get(0).getSeries().get(0).getColumns();
      List<List<Object>> values = results.get(0).getSeries().get(0).getValues();
      JsonArray result = new JsonArray();
      for(int i = 0; i < values.size(); i++){
        List<Object> value = values.get(i);
        JsonObject json = new JsonObject();
        for (int j = 0; j < value.size(); j++){
          Object pointInfo = value.get(j);
          if (columns.get(j).equals("data")){
            json.addProperty(columns.get(j), Double.parseDouble(pointInfo.toString()));
          }else{
            json.addProperty(columns.get(j), JsonTools.removeQuotes(pointInfo.toString()));
          }
        }
        result.add(json);
      }
      System.out.println("Query done!");
      return result;
    }catch (Exception ex){
      System.out.println("Could not query the request");
      ex.printStackTrace();
      return null;
    }

  }

  public String requestQuery(JsonObject request){
    try{
      //System.out.println("Creating query to the database..." + request.get("filterString"));
      JsonArray filters = request.getAsJsonArray("filterString");
      String filterString = "";
      for (int i = 0; i < filters.size(); i++){
        //System.out.println(filters.get(i).toString().trim().replace("\"", "") + " starts with " + filters.get(i).toString().trim().startsWith("F"));
        if (filters.get(i).toString().trim().replace("\"", "").startsWith("F")){
          String filter = filters.get(i).toString().substring(2).replace("\"", "");
          if (!filterString.isEmpty() && filterString != null){
            filterString = filterString + " and " + filter;
          }else{
            filterString = filter;
          }
        }
      }
      Query query;
      if (!filterString.isEmpty() && filterString != null){
        query = new Query("SELECT "+request.get("columns").getAsString()+" FROM "+request.get("columnName") + " WHERE " + filterString + " ORDER BY desc LIMIT "+request.get("nPoints").getAsInt(), dbAuth.getDbName());
      }else{
        query = new Query("SELECT "+request.get("columns").getAsString()+" FROM "+request.get("columnName") + " ORDER BY desc LIMIT "+request.get("nPoints").getAsInt(), dbAuth.getDbName());
      }
      return query.getCommand();
    }catch (Exception ex){
      System.out.println("Could not query the request");
      ex.printStackTrace();
      return null;
    }
  }
  // ####################################################################################


  // ####################################################################################
  // ### Private help methods for influx database
  // ####################################################################################
  private boolean writeToDB(){
    try{
      if (batchPoints != null || batchPoints.getPoints() != null) {
        connect();
        influxDB.write(batchPoints);
        close();
        System.out.println("Successful insert!");
        batchPoints = null;
        dataPointCounter = 0;
        return true;
      }
      else {
        System.out.println("No points to insert! Insert aborted");
        return false;
      }
    }catch (Exception ex){
      System.out.println("Could not insert data!");
      System.out.println(ex);
      return false;
    }
  }

  /**
   *
   * @return
   */
  private BatchPoints createNewBatchPoints(){
    return BatchPoints.database(dbAuth.getDbName()).build();
  }

  /**
   *
   * @param json
   * @return
   */
  private int createNewMeasurementPoint(JsonObject json){
    try{
      Point.Builder builder = Point.measurement(json.get("sensorType").getAsString());//.tag("engineModel", info.get("engineModel").getAsString())
      builder.time(json.get("timeStamp").getAsLong(), TimeUnit.MILLISECONDS);
      builder.addField("data",json.get("data").getAsLong());
      builder.tag("sensorType",json.get("sensorType").getAsString());
      builder.tag("sensorTag", json.get("sensorTag").getAsString());
      batchPoints.point(builder.build());
      dataPointCounter++;
      return 0;
    }
    catch (Exception ex){
      System.out.println("Could not create Measurement Point!");
      System.out.println(ex);
      return -1;
    }
  }
  // ####################################################################################
}
