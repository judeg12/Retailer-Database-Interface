/* Class to intert new customer profile
 * Part of CSE241 DB project
 *
 * Prepared statement allows me get data from user at run time
 * and insert into the database.
 */

import java.lang.Object;     //string methods
import java.lang.Exception;  //catch exceptions
import java.util.ArrayList;  //store products in cart
import java.util.Scanner;    //for input
import java.sql.*;           //jdbc
import java.math.BigInteger; //for credit card num
import java.math.BigDecimal; //jdbc does not have setBigInt()
import java.util.Calendar;   //calculating shipping date

public class customerInsert extends basic{

  /*Data field for newCust insertion*/
  static Connection con;
  PreparedStatement preS;


  /*constructor for customerInsert class*/
  public customerInsert(Connection con){
    this.con = con;
    System.out.println("Hello, welcome to RBC. We are glad to have you as a customer.");
  }

  public void createCust(){
    /*data field*/
    int storeID;
    int customerID = 111; //max 6 digit
    String firstN; //max length 15
    String lastN; //max length 15
    String streetName; //max length 15
    int streetNum; //max 6 digit
    int aptNum; //max 6 digit
    String city; //max length 20
    String state; //max length 20;
    int zip; //max 6 digit
    Scanner scnr = new Scanner(System.in); //new scanner

    System.out.println("Please enter customer information for easy checkout process in the future.");
    System.out.println("Enter first name:");
        firstN = getString(scnr, 15);
    System.out.println("Enter last name:");
        lastN = getString(scnr, 15);
    System.out.println("Enter your street number:");
        streetNum = getInt(scnr, 1000000, 0);
    System.out.println("Enter your street name:");
        streetName = getString(scnr, 15);
    System.out.println("Enter your apartment number: (enter -1 if does not apply)");
        aptNum = getInt(scnr, 1000, -1);
    System.out.println("Enter your city:");
        city = getString(scnr, 20);
    System.out.println("Enter your state name:");
        state = getString(scnr, 20);
    System.out.println("Enter your zip code:");
        zip = getInt(scnr, 1000000, 0);

    /*simple select to find max customer_id from database and add 1*/
    Statement s = null;
    try {
        s = con.createStatement();
    }catch (Exception e){
        System.out.println("statement cannot be created");
    }
    try{
      ResultSet customer_ID_result; //create result set
      customer_ID_result = s.executeQuery("select max(customer_id) as id from customer");
      customer_ID_result.next(); //move to first row
      customerID = customer_ID_result.getInt("ID"); //grab int from row
      customerID++; //add 1 to maintain unique customer_id
      s.close();
    }catch(Exception e){
      System.out.println("(Max customer ID could not be retrieved)");
    }

    try{
        String insertCust;
        ResultSet result;
        insertCust="INSERT INTO CUSTOMER VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)" ;

        con.setAutoCommit(false);
        try{
          preS = con.prepareStatement(insertCust);
        }catch(Exception e){
          System.out.println("Statement could not be created");
        }
        preS.setInt(1, customerID);
        preS.setString(2, firstN);
        preS.setString(3, lastN);
        preS.setString(4, streetName);
        preS.setInt(5, streetNum);
        preS.setInt(6, aptNum);
        preS.setString(7, city);
        preS.setString(8, state);
        preS.setInt(9, zip);

        preS.executeUpdate();
        con.commit();
        System.out.println();
        con.setAutoCommit(true);
      } catch (Exception e){
          System.out.println("Could Not insert new profile for: "+ firstN + " " + lastN);
      }
      System.out.println("New profile created, your customer ID is " +customerID+"\n");
  } //end of create new customer



  public void newOrder(int customerID, int storeID){
    Scanner scnr = new Scanner(System.in);
    String choice;
    boolean repeat = true; //for while loops
    ResultSet result = null; //return from sql query
    double shippingCost = 0; //chosen, only for online.
    double transPrice = 0; //calculated

    int shipDays; //depends on customer's choice
    int transID = 0; //retrived using prep statement

    float [][] productInfo = new float[20][2];
    int item = 0;

    while(repeat == true){ //loop for shopping cart
      productInfo[item] = addItem(storeID, scnr, result); //input storeID
      choice = null;
      while(choice == null){
        System.out.println("Would you like to order another item? enter y/n:");
        choice = getString(scnr, 1); //length 1
        if(choice.equalsIgnoreCase("n")){
          repeat = false;
        }
        else if(choice.equalsIgnoreCase("y")){
          System.out.println("Added last item to cart.");
        }
        else{
          choice = null;
        }
      }
      if(item ==20){
        System.out.println("Shop cart max capacity reached. Moving to checkout.");
        repeat=false;
      }
      item++;
    }

    /* began checkout process */
    System.out.println("\tShopping cart moved to checkout contains "+item+" items:");
    for(int i=0; i<item; i++){
      transPrice += productInfo[i][1];
      System.out.println("\t\tproduct id: "+productInfo[i][0] + "\tprice: "+productInfo[i][1]);
    }
    System.out.println();

    /*get max trans_ID from DB*/
    Statement s = null;
    try{
        s = con.createStatement();
    }catch (Exception e){System.out.println("Statment not created.");}
    try{
        ResultSet max_trans_ID;
        max_trans_ID = s.executeQuery("select max(trans_id) from transactions natural join store_order");
        max_trans_ID.next();
        transID = max_trans_ID.getInt("MAX(TRANS_ID)")+1;
        s.close();
    }catch (Exception e){System.out.println("Could not retrieve max trans_id");
    }

    //get todays date
    java.sql.Date todayDate = new java.sql.Date(System.currentTimeMillis());
    java.sql.Date shipDate = todayDate;

    setAutoCommitFalse(con);
    if(storeID == 1){ //online store id
      //ask for payment method
      System.out.println("Enter you card number for payment: (enter numbers only, 6-20 digits allowed.)");
      int maxLen = 20;
      int minLen = 6;
      BigInteger cardNum = getBigInt(scnr, maxLen, minLen);//6-20 digits range allowed

      //options for shipping costs
      System.out.println("Enter prefered shipping choice: (1 or 2) ");
      System.out.println("  1.Free shipping (5 days)\t $0");
      System.out.println("  2.Express shipping (1 days)\t $5.99");
      if(getInt(scnr, 3, 1)==1){
        shippingCost = 0;
        shipDays = 5;
      }
      else{
        shippingCost = 5.99;
        shipDays = 1;
      }
      transPrice += shippingCost;
      shipDate=this.addDays(todayDate, shipDays);
      //ask for new address or existing address - bonus

      //to insert into DB for online order
      insertTrans(con, transID, transPrice, todayDate);
      insertOnlineOrder(con, transID, shipDate, shippingCost, customerID, cardNum);
      for(int i=0; i<item; i++){
        insertProductPurchase(con, transID, (int)productInfo[i][0], productInfo[i][1]); //good
      }
      Commit(con);

      System.out.println("--------------------------------------");
      System.out.println(" > Your order ID is " + transID +", today's date: " + todayDate +".");
      System.out.println(" > Your selected shipping costs is $" + shippingCost);
      System.out.println(" > Your shippment will arrive on "+ shipDate);
      System.out.println(" > Total online order cost is $" +transPrice);
      System.out.println(" > Your online order is complete");
      System.out.println("--------------------------------------");

    }
    else{ //brick branch id
      //get payment type
      System.out.println("Enter you payment type for store purchase: 'cash' 'card' or 'check'");
      String paymentType = getString(scnr, 6); //max string length is 6

      //to insert into DB for store order
      insertTrans(con, transID, transPrice, todayDate);
      insertStoreOrder(con, transID, storeID, customerID, paymentType);
      for(int i=0; i<item; i++){
        insertProductPurchase(con, transID, (int)productInfo[i][0], productInfo[i][1]); //good
      }
      Commit(con);

      System.out.println("--------------------------------------");
      System.out.println(" > Your order ID is " + transID +", today's date: " + todayDate +".");
      System.out.println(" > Total store order cost is $" +transPrice);
      System.out.println(" > Store order is complete");
      System.out.println("--------------------------------------");
    }

    setAutoCommitTrue(con);
  }
  /*-------- helper functions for inserting trans info to DB ------------ */
  public int insertTrans(Connection con, int trans_id, double total_cost, java.sql.Date trans_date){
    String insertTrans = "insert into transactions(trans_id, total_cost, trans_date) values(?, ?, ?)";
    preS = createPrepState(con, preS, insertTrans);
    try{
      preS.setInt(1, trans_id);
      preS.setDouble(2, total_cost);
      preS.setDate(3, trans_date);
      preS.executeUpdate();
    }catch(Exception e){
        System.out.println("Could not insert transaction.");
        return 0;
    }
    return 1;
  }

  public int insertOnlineOrder(Connection con, int trans_id, java.sql.Date ship_date, double ship_cost, int customer_id, BigInteger card_num){
    String insertOnlineOrder = "insert into online_order values(?,?,?,?,?,null,null,null)";
    preS = createPrepState(con, preS, insertOnlineOrder);
    try{
      preS.setInt(1, trans_id);
      preS.setDate(2, ship_date);
      preS.setDouble(3, ship_cost);
      preS.setInt(4, customer_id);
      preS.setBigDecimal(5, new BigDecimal(card_num));
      preS.executeUpdate();
    }catch(Exception e){
        System.out.println("Could not insert online order info");
        return 0;
    }
    return 1;
  }

  public int insertStoreOrder(Connection con, int transID, int storeID, int customerID, String paymentType){
    String insertStoreOrder = "insert into store_order values(?,?,?,null,?)";
    preS = createPrepState(con, preS, insertStoreOrder);
    try{
      preS.setInt(1, transID);
      preS.setInt(2, storeID);
      preS.setInt(3, customerID);
      preS.setString(4, paymentType);
      preS.executeUpdate();
    }catch(Exception e){
      System.out.println("Could not complete store order");
      return 0;
    }
    return 1;
  }

  public int insertProductPurchase(Connection con, int trans_id, int product_id, double price_paid){
    String insertProductPurchase = "insert into purchased values(?, ?, ?)";
    preS = createPrepState(con, preS, insertProductPurchase);
    try{
      preS.setInt(1, trans_id);
      preS.setInt(2, product_id);
      preS.setDouble(3, price_paid);
      preS.executeUpdate();
    }catch(Exception e){
      System.out.println("Could not update product purchased");
      return 0;
    }
    return 1;
  }
  /*-------- cascading helper functions for creating a new order ------- */

  public float[] addItem(int storeID, Scanner scnr, ResultSet result){
    /*data field*/
    int general, dept;
    boolean repeat = true;
    String department;
    float productInfo [] = new float[2];
    /*display menues*/
    catMenue(); //show general menue
    general = getInt(scnr, 6, 1);//show category menue
    department = deptMenue(general, scnr, 1);//show department menue for given cat
    printProduct(productInfo, department, storeID, scnr, result);//show products for given dept
    //System.out.println(productInfo[0] + " " + productInfo[1]);
    /* Insert new product purchase in DB - will have to include in parent method instead to obtain ACID properties*/
    return productInfo;
  }

  public String deptMenue(int generalDept, Scanner scnr, int store_id){
    /*data field*/
    int i = 0; // to print numbers for menue options
    String viewProd ="";
    String printDeptName;
    ArrayList <String> deptNames = new ArrayList <String> (); //stores menue options
    ResultSet result = null;
    /* get dept menue from DB */
    System.out.println("  Pick department:");
    try{
        viewProd += "SELECT distinct dept_name from department where general_dept = ? ";
        preS = con.prepareStatement(viewProd); //caught
        preS.setInt(1, generalDept);
        result = preS.executeQuery(); //caught
        while(result.next()){ //print deptarmts within given category
          i++;
          printDeptName = result.getString(1);
          deptNames.add(printDeptName);
          System.out.println( "     "+ i + ". " + printDeptName);
        }
    }catch(Exception e){
        System.out.println("Could not retrieve dept listings");
    }

    /* data for printing products */
    int pickedDept = getInt(scnr, i+1, 1); //input must be positive and at most i
    return deptNames.get(pickedDept-1);
  }

  public void printProduct(float productInfo [], String deptName, int storeID, Scanner scnr, ResultSet result){
    /*data field*/
    int choice, i =0;
    String query = null;
    PreparedStatement preS = null;
    ArrayList <Integer> productID = new ArrayList <Integer> (); //stores all possible product ID for fast access
    ArrayList <Float> productPrice = new ArrayList <Float> (); //stores respective prices for fast access

    System.out.println("  Shop for products from " + deptName + " department");
    query = "SELECT product_id, product.product_name, ITEM_PRICE as price ";
    query += "from product NATURAL JOIN inventory NATURAL JOIN department where store_id = ? and dept_name = ?";
    preS=createPrepState(con, preS, query);

    try{
      preS.setLong(1, storeID);
      preS.setString(2, deptName);
      result = preS.executeQuery();
      System.out.println("  Product ID:    Name:\t\t  Price:");
      while(result.next()){
        productID.add(result.getInt("product_id"));
        productPrice.add(result.getFloat("price"));
        System.out.println("\t" + (i+1) +". " + padRight(productID.get(i), 6) + padRight(result.getString("product_name"), 17) + "$"+productPrice.get(i));
        i++;
      }
    }catch(Exception e){System.out.println("could not retreive products"); }
    System.out.println("Enter number to add a product to cart: ");
    choice = getInt(scnr, i+1, 0);
    productInfo[0]=(float)productID.get(choice-1);
    productInfo[1]=productPrice.get(choice-1);
  }

  static void catMenue(){
    System.out.println("Shop by category: (enter number to pick)");
    System.out.println("  1. Fashion Products");
    System.out.println("  2. Tech Products");
    System.out.println("  3. Outdoor Products");
    System.out.println("  4. Indoor Products");
    System.out.println("  5. Youth Products");
  }
  /*
  * @param today's date, int days to add
  * @return date type future date
  */
  public Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return new Date(c.getTimeInMillis());
  }


}//end of class
