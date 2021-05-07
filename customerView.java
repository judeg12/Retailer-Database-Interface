/*
 *CSE 241 project
 *class to view customer profile information 
 */
import java.util.Scanner;
import java.sql.*;

public class customerView extends basic{

  public void viewOrders(Scanner scnr, Connection con, int customerID){
    ResultSet viewOrders = null;
    PreparedStatement preS = null;
    String storeOrders ="select trans_id, store_id, total_cost, trans_date from transactions natural join store_order where customer_id = ?";
    String onlineOrders ="select trans_id, total_cost, trans_date, ship_date, ship_cost from transactions natural join online_order where customer_id = ?";
    preS = createPrepState(con, preS, storeOrders);
    try{ 
	preS.setInt(1, customerID); 
	viewOrders = preS.executeQuery();
        System.out.println("--past in store order history--");
        while(viewOrders.next()){
          System.out.println("order# "+viewOrders.getInt("trans_id")+"  store#: "+viewOrders.getInt("store_id")+"  order costs: $"+viewOrders.getDouble("total_cost")+"  date of order: "+viewOrders.getString("trans_date"));
	}
    }catch(Exception e){
      System.out.println("No in-store orders found");
    }
    System.out.println();
    preS = createPrepState(con, preS, onlineOrders);
    try{ 
	preS.setInt(1, customerID); 
	viewOrders = preS.executeQuery();
        System.out.println("--online order history--");
        while(viewOrders.next()){
          System.out.println("order# "+viewOrders.getInt("trans_id")+"  order cost:$ "+viewOrders.getDouble("total_cost")+"  order date: "+viewOrders.getString("trans_date")+"  shipped on:"+viewOrders.getString("ship_date")+"  shipping costs: $"+viewOrders.getDouble("ship_cost"));
	}
    }catch(Exception e){
      System.out.println("No online orders found");
    }  
  }

  public int getStore(Scanner scnr, Connection con){
    ResultSet storeInfo = null;
    Statement s = null;
    System.out.println("here are all RBC stores, which one will you shop at? Enter number to pick store");
    System.out.println();
    try{
	s = con.createStatement();
	storeInfo =s.executeQuery("select store_id, street_num, street_name, city, country from store_branch natural join brick_store");
        System.out.println("1. https://RBC.com\tonline store");
        while(storeInfo.next()){
	  System.out.println(storeInfo.getInt("store_id")+". "+padRight(storeInfo.getString("street_num"), 6) +padRight(storeInfo.getString("street_name"), 15) + padRight(storeInfo.getString("city"), 13) + padRight(storeInfo.getString("country"), 15));
 	} 	
    }catch(Exception e){
	System.out.println("Not able to print stores at the moment. Please contact RBC customer service if issue persists.");
    }finally{
      try{
        if(storeInfo != null) storeInfo.close(); 
      }catch(Exception e){
      }
    } 
    int storeID = getInt(scnr, 8, 1); 
    return storeID;
  }
   
  public int getCustomer(Scanner scnr, Connection con){
    ResultSet customerInfo = null;
    Statement s = null;
    int userID = 1;
    int min = 1;
    int max = 1;
    System.out.println("Enter your customer ID: ");
    try{
      s = con.createStatement();
      customerInfo = s.executeQuery("select max(customer_id), min(customer_id) from customer");
      customerInfo.next(); //move to first tuple 
      min = customerInfo.getInt("min(customer_id)");
      max = customerInfo.getInt("max(customer_id)");
      System.out.println("Customer IDs range from "+min+" to "+max);
      if(s != null){
	s.close();
      }
      if(customerInfo != null){
	customerInfo.close();
      } 
    }catch(Exception e){
      System.out.println("Could not retrieve min/max IDs.");
    }
    userID= getInt(scnr, max+1, min); 
    return userID;
  }
  
}//end of class 
