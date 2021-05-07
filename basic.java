/*
 * super class for all interfaces 
 * provied help functions to force good user input 
 * jdbc helper functions hold try - catch blocks 
 *
 * CSE 241 project java file 
 */
import java.util.Scanner;
import java.sql.*;
import java.math.BigInteger;

public class basic{

  /* helper to create statement */
  public Statement createState(Connection con){
    Statement s = null; 
    try{
      s = con.createStatement();
    }catch(Exception e){
      System.out.println("Could not create Statement.");
    }
    return s; 
  }
  /* fetch date */
  public String getDate(ResultSet result, String thisValue){ 
    String date=""; 
    try{
	date = String.valueOf(result.getDate(thisValue));
    }catch(Exception e){
	System.out.println("Could not fetch date");
    }
    return date; 
  }
  /* moves result cursor */
  public boolean next(ResultSet result){
    boolean next=false;
    try{
      next = result.next();
    }catch(Exception e){ 
      System.out.println("Cannot move to result tuple.");
    }
    return next;
  }

  /* helper to execute statment */
  public ResultSet executeQ(ResultSet result, Statement s, String q){
    try{
	result = s.executeQuery(q); 
    }catch(Exception e){
	System.out.println("Could not execute statment");
e.printStackTrace();
    }
    return result; 
  }
  /* helper to fetch int from result */ 
  public int getInt(ResultSet result, String getThis){
    int value = 0;
    try{
      value = result.getInt(getThis); 
    }catch(Exception e){  
      System.out.println("Could not fetch string"); 
    }
    return value;
  }
  /* helper to fetch string from result */
  public String getStr(ResultSet result, String getThis){
    String value = null;
    try{  
      value = result.getString(getThis);
    }catch(Exception e){
      System.out.println("Could not fetch " + getThis+ " from result");
    }
    return value; 
  } 
  
  /* fetch double */ 
  public double getDouble(ResultSet result, String getThis){
    Double value = 0.0;
    try{
      value = result.getDouble(getThis);
    }catch(Exception e){
      System.out.println("Could not get double.");
    }
    return value;
  }
 
  /* helper to create prepared statment */
  public PreparedStatement createPrepState(Connection con, PreparedStatement preS, String sqlQuery){
    try{
      preS = con.prepareStatement(sqlQuery);
    }catch(Exception e){
      System.out.println("fatal error - could not create prepared statement");
    }
    return preS; 
  }
  /* set auto commit to on*/ 
  public void setAutoCommitTrue(Connection con){
    try{
      con.setAutoCommit(true);
    }catch(Exception e){
      System.out.println("Auto commit cannot be set to on");
    }
  } 

  /* set auto commit to off*/ 
  public void setAutoCommitFalse(Connection con){
    try{
      con.setAutoCommit(false);
    }catch(Exception e){ 
      System.out.println("Connection cannot be set to false.");
    }
  }
  /* commit pending statments on connection */
  public void Commit(Connection con){
    try{
      con.commit();
    }catch(Exception e){
      System.out.println("Could not commit changes to DB");
    }
  }
  /* helper funcs for printing */
  public String padRight(String s, int n) {
     return String.format("%-" + n + "s", s);  
  }
  
  public String padRight(int d, int n) {
     return String.format("%-" + n + "d", d);  
  }

  public String padLeft(String s, int n) {
    return String.format("%" + n + "s", s);  
  } 

  public String padLeft(int d, int n) {
    return String.format("%" + n + "d", d);  
  } 
  public BigInteger getBigInt(Scanner scnr, int maxLen, int minLen){
    BigInteger num = BigInteger.valueOf(1);
    String input = "";
    boolean repeat=true;  
    while(repeat){
      input = scnr.nextLine(); 
      try{
	if(input.length() < maxLen){ 
	  if(input.length() > minLen){
  	     num = new BigInteger(input);
	     repeat = false; 
 	  }
	  else{
	    System.out.println("Input is too small");
	  }
	}
	else{
	  System.out.println("Input is too big");
        }
      }catch(Exception e){
        System.out.println("Input is not a number");
      }
    }
    return num;
  }
  /* method to force user to input numbers only*/
  public int getInt(Scanner scnr, int max, int min){
    boolean repeat = true;
    String input= ""; 
    while(repeat){
      input=scnr.nextLine();
      if(isInteger(input)){ //check type
	if( Integer.valueOf(input)< max){ //check digits
	  if( Integer.valueOf(input)>min-1){ //check if negative
            repeat=false; //success, correct input format
	  }
	  else{
	    System.out.println("Input is too small, try again");
  	  }
        }
        else{  
          System.out.println("Intput is too large, try again");
        }
      }
      else{
        System.out.println("Input is not a int, try again:");
      }
    }
    return Integer.valueOf(input);
  }
  
  /*method to get input string from user */ 
  public String getString(Scanner scnr, int length){
   boolean repeat = true;
   String input = "";
   while(repeat){
      input = scnr.nextLine();
      if(input.length()<length+1){//check string length 
        repeat = false;
      }
      else{
	System.out.println("Input string is too long");
      }
    }
    return input;
  }
  
  /*helper to check if input is an int */
  public boolean isInteger(String toTest){
    try{
	Integer.parseInt(toTest);
	return true;
    }
    catch (Exception e){
 	return false; 
    }
  }
}
