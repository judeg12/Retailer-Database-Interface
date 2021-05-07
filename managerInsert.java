/*cse 241 project
 * insert class for product managers
 */

import java.util.Scanner;
import java.lang.Exception;
import java.sql.*;

public class managerInsert extends basic{
  Connection con = null;
  Scanner scnr = null;  
  /*constructor*/ 
  managerInsert(Connection con, Scanner scnr){
    this.con=con;  
    this.scnr=scnr; 
  }
  
  //update product inventory and insert vendor order 
  public void orderInventory(int storeID){ 
    int productID = 0; 
    int quantity = 0;
    PreparedStatement preS = null;
    PreparedStatement preS_2 = null;

    System.out.println("Which product are you restocking for? Enter product id: 500-559");
    productID = getInt(scnr, 560, 500);
    System.out.println("How many items would you like to reorder? Enter quantity 1-200");
    quantity = getInt(scnr, 201, 1);
    
    preS = createPrepState(con, preS, "update inventory set item_count = item_count + ? where product_id = ? and store_id = ?");

    setAutoCommitFalse(con);
    try{
      preS.setInt(1, quantity);
      preS.setInt(2, productID); 
      preS.setInt(3, storeID);
      preS.executeUpdate(); 
    }catch(Exception e){
      System.out.println("Failed to update inventory & vendor orders");
    }
    preS_2 = createPrepState(con, preS_2,"insert into vendor_order(store_id, product_id, quantity) values(?, ?, ?)");
    try{ 
      preS_2.setInt(1, storeID);
      preS_2.setInt(2, productID);
      preS_2.setInt(3, quantity);
      preS_2.executeUpdate();
      con.commit(); //commit 2 inserts
    }catch(Exception e){
       System.out.println("Failed to update inventory & vendor orders");
    }
    setAutoCommitTrue(con);
    System.out.println("Product "+productID+" has been restocked for store #"+storeID);
  } 

  //update product price 
  public void updatePrice(int storeID){
    int productID = 0;
    double price = 0.0; 
    double temp = 0.0;
    PreparedStatement preS = null;
    
    System.out.println("For which product will you be changing the price? current products id #500-559");
    productID = getInt(scnr, 560, 500);
    System.out.println("What is the new price at your store? enter decimal number whithout '$'"); 
    while(price == 0.0){
      temp = scnr.nextDouble();
      if((temp < 10000000)&&(temp > 0.0)){
        price = temp;   
      }
    }
    
    preS = createPrepState(con, preS, "update inventory set item_price=? where product_id = ? and store_id = ?");
    try{
      preS.setDouble(1 , price);
      preS.setInt(2 , productID);
      preS.setInt(3 , storeID);
      preS.executeUpdate();
    }catch(Exception e){
      System.out.println("Could not update product price");
    }
    System.out.println("product #"+productID+" price has been set to "+price+"\n");
  }
  
}
