package edu.ltu.ngacdbsystem.tools;

public class JsonTools {

  public static String removeQuotes(String str){
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < str.length(); i++){
      if(str.charAt(i) != '"'){
        builder.append(str.charAt(i));
      }
    }
    return builder.toString();
  }
}
