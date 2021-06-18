package edu.ltu.ngacdbsystem.tools;

//import database.DatabaseAuthorisation;

import edu.ltu.ngacdbsystem.DatabaseAuthorisation;

import java.io.InputStream;
import java.util.Properties;

/**
 *
 */
public class PropertiesReader {

  /**
   *
   * @param propertiesFileName
   * @return
   */
  public DatabaseAuthorisation getDatabaseAuthorisationProperties(String propertiesFileName){
    try{
      Properties properties = new Properties();
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFileName);

      if(inputStream != null){
        properties.load(inputStream);
      }else{
        System.out.println("Property file '" + propertiesFileName + "' not found in the classpath");
        return null;
      }
      DatabaseAuthorisation auth = new DatabaseAuthorisation(
              properties.getProperty("dbType"),
              properties.getProperty("dbURL"),
              properties.getProperty("dbName"),
              properties.getProperty("userName"),
              properties.getProperty("pwd"));
      inputStream.close();
      return auth;
    }
    catch (Exception ex){
      System.out.println("Error: Could not collect properties! " + ex);
      return null;
    }
  }
}
