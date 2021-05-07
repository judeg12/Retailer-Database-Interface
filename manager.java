/**
 * CSE 241 Project
 * Written: November 2020
 * Sub-Interface for the store manager of RBC
 * */

import java.util.Scanner;
import java.lang.Object;
import java.lang.Exception; 
import java.sql.*;
 

public class manager extends basic{

  int storeID;
  Connection con;

  /* constructor for manager class */
  public manager(Connection con){
    this.con = con;
    System.out.println("instance of manager created");
  }

  public void managerI(int choice){
    Scanner scnr = new Scanner(System.in);

    /*data declartion*/
    String input = "empty";
    int testInt = 0;
    boolean repeat=true;


    if(choice == 1){
      menueA(); //product analytics menue 
    }
    else{
      menueB(); //product manager menue
    }
  }
    
  public void menueA(){
    Scanner scnr = new Scanner(System.in);
    boolean repeat = true; 
    String testInput;
    int choice = 0; 

    managerView myView = new managerView(con);

    while(repeat){
	 System.out.println("  1. View top 10 states by customer density");
 	 System.out.println("  2. View top 10 products by total revenue"); 
         System.out.println("  3. List supplier / brands carried");
	 System.out.println("  4. Return to main");

	testInput = scnr.nextLine();
	if(isInteger(testInput)){ //test
	  choice = Integer.valueOf(testInput); //set value
	}
	else choice = 0;

	switch(choice){
	case 3: 
	  //System.out.println("Choice 3 picked");
	  myView.viewBrands();
	  break;
	case 1:
	  myView.viewCustomerLocation();
	  break;
	case 2: 
	  myView.viewTopProducts(); 
	  break; 
        case 4:
 	  System.out.println("choice 5 exit"); 
	  repeat = false; //exit menue
	  break;
	default: System.out.println("Wrong input, please enter a number 1-5");
      }
    }
  }

  public void menueB(){
    Scanner scnr = new Scanner(System.in);
    boolean repeat = true; 
    String testInput;
    int choice = 0; 
    managerInsert thisManager = null;

    /*get store*/
    setStore();
    //System.out.println("store id is now: " + storeID);
    
    while(repeat){
      System.out.println("1. Modify product price");
      System.out.println("2. Order more inventory for product");
      System.out.println("3. Retun to main menue");
      //user input
	testInput = scnr.nextLine();
	if(isInteger(testInput)){ //test
	  choice = Integer.valueOf(testInput); //set value
	}
	else choice = 0;
      switch(choice){
	case 1: 
	  thisManager = new managerInsert(con, scnr); 
          thisManager.updatePrice(storeID);
	  break;
	case 2: 
	  thisManager = new managerInsert(con, scnr);  
          thisManager.orderInventory(storeID); 
	  break;
	case 3: 
	  System.out.println("choice 3 exit");
	  repeat = false; //exit menue
	  break;
	default: System.out.println("Wrong input, please enter '1', '2', or '3'");
      }
    }
  }
  public int setStore(){
    Scanner scnr = new Scanner(System.in);
    
      /*data declartion*/
    String input = "empty";
    int testInt;
    boolean repeat = true;

    //showStores()

    while(repeat){ //keeps asking 
      System.out.println("enter your store id number: ");
      input = scnr.nextLine();
      if(isInteger(input)){ //check for int & catches exception 
	testInt=Integer.valueOf(input);
	if((0<testInt)&&(testInt<7)){ //check bounds
          storeID = testInt;
	  repeat = false;
        }
 	else{
	  System.out.println("input store id is not within 1-7");
	}
      }
      else{
        System.out.println("input is not a valid int");
      }
    }
    return storeID;
  }
  
}
