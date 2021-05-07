import java.sql.*;

public class managerView extends basic{
  static Connection con;

  /* constructor */
  public managerView(Connection con){
    this.con = con;
  }

  //option 1
  public void viewSales() {
   Statement s = null;

    try {
        s = con.createStatement();
    }catch (Exception e){
        System.out.println("statement cannot be created");
    }
    try {
           String q;
           ResultSet result;
           q = "select TRANS_ID, TRANS_DATE, TOTAL_COST from TRANSACTIONS NATURAL JOIN ONLINE_STORE ORDER BY transactions.trans_date" ;
           result = s.executeQuery(q);
           if (!result.next()) System.out.println ("Empty result.");
           else {
	     int rowCount = 0;
	     //column titles
	     System.out.println();
	     System.out.println("trans id:  trans date:  total cost:");
	     System.out.println("-----------------------------------");
             do {
               System.out.println (padRight(result.getString("trans_id"), 10) + " " + result.getDate("trans_date") + padLeft(" $", 6) + padLeft(result.getString("total_cost"),8));
	       rowCount++;
             } while (result.next()); 
	     System.out.println("Total Row Count: "+ rowCount);
           }
         } catch(Exception e){ System.out.println("View could not be retrieved.");
	 } 
  }

  //option 2
  public void viewInventory() {
    Statement s = createState(con);
    String q;
    ResultSet result = null;

    q = "select TRANS_ID, TRANS_DATE, TOTAL_COST from TRANSACTIONS NATURAL JOIN ONLINE_STORE ORDER BY transactions.trans_date" ;
    result = executeQ(result, s, q);

    
    if (! next(result)) System.out.println ("Empty result.");
    else {
      int rowCount = 0;     
      System.out.println();
      System.out.println("trans id:  trans date:  total cost:");
      System.out.println("-----------------------------------");
      do {
        System.out.println (padRight(getStr(result, "trans_id"), 10) + " " + getDate(result, "trans_date") + padLeft(" $", 6) + padLeft(getStr(result,"total_cost"), 8));
        rowCount++;
      } while (next(result)); 
      System.out.println("Total Row Count: "+ rowCount);
    }     
  }
  
  //option 3
  public void viewBrands() {
    Statement s = createState(con); 
    ResultSet result = null; 
    int supID=1;
    int temp=0;
    result = executeQ(result, s, "select supplier_id, brand from product order by supplier_id, brand");
    System.out.println("Supplier id:\tbrands carried:");

    while(next(result)){
      supID=getInt(result, "supplier_id");
      if(supID!=temp){
        System.out.print("\nsup #"+supID+" |");
        temp = supID; 
      }
      System.out.print(getStr(result, "brand") + ", ");
    }
    System.out.println("\n");
  }
  
  public void viewVendorOrders(){
    //set values
    //execute 
    //print 
  }

  public void viewTopProducts(){
    Statement s = createState(con); 
    ResultSet result = null; 
    result = executeQ(result, s, "select product_id, sum(price_paid) from purchased group by product_id order by sum(price_paid) desc");
    System.out.println("Rank) Product #\t Revenue Earned $");
    for(int i=1; i<11; i++){  
      if(next(result)){
        System.out.println("#"+i+"\t"+getStr(result, "product_id")+"\t\t$"+getDouble(result, "sum(price_paid)"));
      }
    }
     System.out.print("\n");
  }
  public void viewCustomerLocation(){
    Statement s = createState(con); 
    ResultSet result = null; 
    result = executeQ(result, s, "select state_name, count(state_name), RANK() over(order by count(state_name) desc) as ranking from customer group by state_name");
    System.out.println("State name: \t\t Customer count: "); 
    System.out.println("----------- \t\t --------------- ");
    for(int i=1; i<11; i++){
      if(next(result)){
        System.out.println(" #"+i+" "+padRight(getStr(result, "state_name"), 20) + "\t "+padLeft(getInt(result, "count(state_name)"), 3) );
      }
    }
    System.out.print("\n");
  }

}//end of calss 
