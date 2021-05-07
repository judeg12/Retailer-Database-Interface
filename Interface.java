/*
 * CSE 241 Project
 * Jude Gerhart
 * 
 * This containts the main func for my interface which calls the manager and customer interface 
 */
import java.sql.*;
import java.lang.Object;
import java.util.Scanner;
import java.lang.Exception; 

public class Interface extends basic{

  public static void main(String [] args){
    String choice;
    String username, password;
    Connection con=null;
    boolean repeat=true;
     
    /*possible instances*/
    manager thisManager; 
    customerInsert thisCustomer;
    basic helperFuncs = new basic(); 
    /*ask user for username and password*/
    Scanner scnr = new Scanner(System.in);
    
    System.out.println("Enter your database username");
	username = null;
    	username = helperFuncs.getString(scnr, 30);
    System.out.println("Enter your database password");
	password = null;
    	password = helperFuncs.getString(scnr, 30);
    /*connect to database*/
    System.out.println("Logging into database...");
    con = createCon(username, password, con);
    System.out.println();

    while(repeat){
        System.out.println("Which interface would you like to access?");
	System.out.println("Enter a letter to pick a option or 'q' to quit");
	System.out.println("A -> Analytics Manger Interface"); //in progress 
	System.out.println("B -> Product Manager Interface");
	System.out.println("C -> Create New Customer Interface"); //finished
	System.out.println("D -> Existing Customer Login Interface");
	System.out.println("Q -> Quitting Program"); //finished
	choice = scnr.nextLine();
	if(choice.equalsIgnoreCase("A")){
	  System.out.println("--Analytics Manger Interface Selected--");
	  thisManager = new manager(con);
	  thisManager.managerI(1);
	}
	else if(choice.equalsIgnoreCase("B")){
	  System.out.println("--Product Manager Interface Selected--");
	  thisManager = new manager(con);
          thisManager.managerI(2);
	}
	else if(choice.equalsIgnoreCase("C")){
	  System.out.println("--New Customer Interface Selected--");
          thisCustomer = new customerInsert(con);
	  thisCustomer.createCust();
	}
	else if(choice.equalsIgnoreCase("D")){
	  System.out.println("--Returing Customer Interface Selected--");
          customerMenue(scnr, con);
	}
	else if(choice.equalsIgnoreCase("Q")){
	  System.out.println("--Close Program--");
	  repeat = false; 
	}
	else{
	  System.out.print("Invalid Input, " );
	}
    }
    /*close connections*/
    closeCon(con);
    scnr.close();
  }
  public static void customerMenue(Scanner scnr, Connection con){
    boolean repeat = true;
    String choice=null; 
    customerInsert thisCustomer; 
    customerView viewMyProfile; 
    int storeID, customerID = 1;

    viewMyProfile = new customerView(); //new instance to get customer info 
    customerID = viewMyProfile.getCustomer(scnr, con); 
    storeID = viewMyProfile.getStore(scnr, con);

    while(repeat){
	System.out.println("\nEnter letter for option or 'q' to return.");
	System.out.println(" A -> Create New Order");
	System.out.println(" B -> View Past Orders");
	System.out.println(" C -> Return Order");
	System.out.println(" Q -> Exit Customer Inferface");
	choice = scnr.nextLine();
	if(choice.equalsIgnoreCase("A")){//new order
	  thisCustomer = new customerInsert(con); //new instance to create order 
          thisCustomer.newOrder(customerID, storeID); //pass in custID, StoreID
	}
        else if(choice.equalsIgnoreCase("B")){	//view past orders
          viewMyProfile = new customerView(); 
	  viewMyProfile.viewOrders(scnr, con, customerID);
	}
	else if(choice.equalsIgnoreCase("C")){	//return order 
	  System.out.println("Feature is coming soon!");
	}
	else if(choice.equalsIgnoreCase("Q")){	//exit customer interface
	  repeat = false; 
	}
        else{
	  System.out.print("Invalid input, ");
	}
    }
    
  }
  /*helper functions*/

  /*connect to DB*/
  public static Connection createCon(String username, String password, Connection con){
    try {
      Class.forName ("oracle.jdbc.driver.OracleDriver");
    } catch(Exception e){System.out.println("Oracle Driver Cannot be added");} 
    try {
         con = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", username, password);
         System.out.println("Login success.");
	 return con;
    }catch (Exception e){
        System.out.println("Connection cannot be established, Please re-open program.");
	System.exit(0);
	return null;
    }
  }

  /*close connection*/ 
  public static void closeCon(Connection myConnection){
    /*try{
	if(myStatement!=null) myStatement.close(); 
	System.out.println("Statement closed");
    }catch(SQLException e){
	System.out.println("Statment cannot be closed");
    }*/
    try{ 
          if(myConnection != null){ 
		myConnection.close();
	 	System.out.println("Logged out. ");
	  }
    }catch(SQLException e){
	System.out.println("Could not close connection");
	System.exit(0);
    }
  }//end closeCon()
}
