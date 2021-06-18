package edu.ltu.ngacdbsystem;

/**
 *
 */
public class DatabaseAuthorisation {
  private String dbType;
  private String dbPath;
  private String userName;
  private String pwd;
  private String dbName;

  /**
   *
   * @param dbType
   * @param dbPath
   * @param dbName
   * @param userName
   * @param pwd
   */
  public DatabaseAuthorisation(String dbType, String dbPath,String dbName, String userName, String pwd){
    this.dbType = dbType;
    this.dbPath = dbPath;
    this.dbName = dbName;
    this.userName = userName;
    this.pwd = pwd;
  }

  /**
   *
   * @return
   */
  public String getDbType() {
    return dbType;
  }

  /**
   *
   * @return
   */
  public String getDbPath() {
    return dbPath;
  }

  /**
   *
   * @return
   */
  public String getUserName() {
    return userName;
  }

  /**
   *
   * @return
   */
  public String getPwd() {
    return pwd;
  }

  /**
   *
   * @return
   */
  public String getDbName() {
    return dbName;
  }
}
