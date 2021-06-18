package edu.ltu.ngacdbsystem;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

// ####################################################################################
// ## Every database extends this abstract class.
// ## Help methods should be private (only used internally).
// ####################################################################################
/**
 *
 */
public abstract class IDatabase {

  protected DatabaseAuthorisation dbAuth;

  /**
   *
   * @return
   */
  public abstract boolean connect();

  /**
   *
   * @param json
   * @return
   */
  public abstract int insertData(JsonObject json);

  /**
   *
   * @param requestInfo
   * @return
   */

  public abstract String requestQuery(JsonObject request);
  
  /**
   *
   * @return
   */
  public abstract JsonArray requestData(JsonObject requestInfo);

  /**
   *
   * @return
   */
  public abstract boolean close();

}
// ####################################################################################
