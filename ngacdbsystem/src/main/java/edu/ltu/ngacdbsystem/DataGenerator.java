package edu.ltu.ngacdbsystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class DataGenerator {
  long timer;


  public int start(IDatabase db){
    timer = System.currentTimeMillis();
    try{
      System.out.println("Starting Data Generator...");
      int counter = 0;
      long rpmValue = 10;
      while(counter < 500){
        JsonObject json = new JsonObject();
        json.addProperty("sensortype","EngineRPM");
        json.addProperty("timestamp", System.currentTimeMillis());
        json.addProperty("data",rpmValue);
        json.addProperty("id", "23E4Gb");
        db.insertData(json);
        rpmValue += 100;
        counter++;
      }
      System.out.println("Stopping...");
      stop(db);
      return 1;
    }catch (Exception ex){
      System.out.println("Error with the Data Generator");
      System.out.println(ex);
      return -1;
    }
  }

  private int stop(IDatabase db){
    timer = System.currentTimeMillis() - timer;
    System.out.println("Data Generator is shutdown. Time: " + timer/1000 + "sec");
    JsonObject json = new JsonObject();
    json.addProperty("columnName","EngineRPM");
    System.out.println("Do a request...");
    JsonArray jsonObjects = db.requestData(json);
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gson.toJson(jsonObjects));
    return 1;
  }
}
