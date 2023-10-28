
import java.security.PublicKey;
import java.sql.*;
import java.util.*;

//database - BillingDB
//table - gasbilling

public class Main {
	public static void main(String[] args) throws Exception{
		Scanner scan = new Scanner(System.in);
		System.out.println("******  GAS BILLING PORTAL  ******");
		while(true) {
		System.out.println("\nPress[1] to view active consumers"+"\nPress[2] to Generate Bill"+"\nPress[3] to Pay Bill");
		System.out.println("Press[4] to Check Bill"+"\nPress[5] to Add New Consumer"+"\nPress[6] to Update Consumer Details"+"\nPress[7] to Discontinue Consumer");
		System.out.println("\nPress[0] to EXIT");
		int key = scan.nextInt();
		if(key==0) {
			System.out.println("Thank you, You can close the terminal.\nPlease visit again.");
			break;
		}
		switch (key) {
		
		case 1: {
			viewDB();
			break;
		}
		case 2: {
			generatebill();
			break;
		}
		case 3: {
			paybill();
			break;
		}
		case 4: {
			checkBill();
			break;
		}
		case 5: {
			insertNew();
			break;
		}
		case 6: {
			updateCust();
			break;
		}
		case 7: {
			deleteCust();
			break;
		}
		default:
			System.out.println("Please choose correct option");
		}
	}
	}
	
	public static void viewDB() throws Exception{
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BillingDB","root","Ark@2037");
		Statement st = con.createStatement();
		String query = "select * from gasbilling";
		ResultSet rs = st.executeQuery(query);
		System.out.println("__________________________________________________________________________________________________________________________________________________________________\n");
		System.out.println("Sl No"+"\t| Meter No"+"\tCustomer Name"+"\t\tMobile No"+"\tConnection Date"+"\t\tPrevious Reading"+"\tPresent reading"+"\t  Units"+"\t\tBill"+"\t   Due");
		System.out.println("__________________________________________________________________________________________________________________________________________________________________");
		int i=1;
		while(rs.next()) {
			
		System.out.println(" "+i+".\t| "+rs.getInt(1)+"\t\t"+rs.getString(2)+"\t\t"+rs.getLong(3)+"\t"+rs.getString(4)+"\t\t"+rs.getInt(5)+"\t\t\t"+rs.getInt(6)+"\t\t  "+rs.getInt(7)+"\t\t"+rs.getFloat(8)+"\t   "+rs.getFloat(9));
		i++;
		}
		System.out.println("__________________________________________________________________________________________________________________________________________________________________");
		con.close();
	}
	
	public static void generatebill() throws Exception{
		Scanner scan = new Scanner(System.in);
		Scanner scan1 = new Scanner(System.in);
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BillingDB","root","Ark@2037");
		System.out.println("Enter Meter No");
		int no = scan.nextInt();
		if(checkmeterno(no)==1){
		CallableStatement cst = con.prepareCall("{call fetchCNPR(?,?,?)}");
		cst.setInt(1,no);
		cst.registerOutParameter(2,Types.VARCHAR);
		cst.registerOutParameter(3,Types.BIGINT);
		cst.execute();
		System.out.println("Customer details are ");
		System.out.println("Customer Name : "+cst.getString(2));
		System.out.println("Previous Reading : "+cst.getLong(3)+" units");
		System.out.println("Enter Present reading(in units) :");
		Long present = scan.nextLong();
		
		CallableStatement cst1 = con.prepareCall("{call generatebill(?,?,?,?,?,?,?)}");
		cst1.setInt(1,no);
		cst1.setLong(2,present);
		cst1.registerOutParameter(3,Types.INTEGER);
		cst1.registerOutParameter(4,Types.FLOAT);
		cst1.registerOutParameter(5,Types.VARCHAR);
		cst1.registerOutParameter(6,Types.DATE);
		cst1.registerOutParameter(7,Types.FLOAT);
		cst1.execute();
		System.out.println("Bill generated on "+cst1.getString(5));
		System.out.println("Units consumed : "+cst1.getInt(3));
		System.out.println("This month Bill amount : "+cst1.getFloat(4));
		System.out.println("Total Due amount : "+cst1.getFloat(7));
		System.out.println("Due Date : "+cst1.getString(6));
				con.close();
		}
		else {
			System.out.println(no+" is Invalid input");
		}
	}
	
	public static void paybill() throws Exception{
		Scanner scan = new Scanner(System.in);
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BillingDB","root","Ark@2037");
		System.out.println("Enter Meter No");
		int no = scan.nextInt();
		if(checkmeterno(no)==1){
		CallableStatement cst = con.prepareCall("{call fetchCNPD(?,?,?)}");
		cst.setInt(1,no);
		cst.registerOutParameter(2,Types.VARCHAR);
		cst.registerOutParameter(3,Types.FLOAT);
		cst.execute();
		
		System.out.println("Customer details are ");
		System.out.println("Customer Name : "+cst.getString(2));
		System.out.println("Pending Due is : "+cst.getFloat(3));
		System.out.println("Enter amount to pay Bill : ");
		float amount = scan.nextFloat();
		CallableStatement cst1 = con.prepareCall("{call paybill(?,?,?,?)}");
		cst1.setInt(1,no);
		cst1.setFloat(2,amount);
		cst1.execute();
		System.out.println("Bill paid succesfully on "+cst1.getString(3));
		System.out.println("Amount paid : "+amount);
		System.out.println("Due after bill pay of "+amount+" is : "+cst1.getFloat(4));
		con.close();
		}
		else {
			System.out.println(no+" is Invalid input");
		}
	}
	
	public static void checkBill() throws Exception {
		Scanner scan = new Scanner(System.in);
		Scanner scan1 = new Scanner(System.in);
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BillingDB","root","Ark@2037");
		System.out.println("Press[1] - using Meter No\nPress[2] - using Customer Name\nPress[3] - using Mobile No");
		int key = scan.nextInt();
		
		switch (key) {
		case 1:{
			System.out.println("Enter Meter No : ");
			int no = scan.nextInt();
			
			if(checkmeterno(no)==0) {
				System.out.println("Invalid meter no");
				break;
			}
			
			CallableStatement cst = con.prepareCall("{call checkbillM(?,?)}");
			cst.setInt(1,no);
			cst.registerOutParameter(2,Types.FLOAT);
			cst.execute();
			System.out.println("Bill amount for Meter No "+no+" is : "+cst.getFloat(2)); 
			con.close();
			break;
			
		}
		case 2: {
			System.out.println("Enter Customer Name :");
			String name = scan1.nextLine();
			
			CallableStatement cst1 = con.prepareCall("{call fetchCN(?,?)}");
			cst1.setString(1,name);
			cst1.registerOutParameter(2,Types.INTEGER);
			cst1.execute();
			if(cst1.getInt(2)==0) {
				System.out.println("Invalid Customer Name");
				break;
			}
			
			CallableStatement cst = con.prepareCall("{call checkbillCN(?,?)}");
			cst.setString(1,name);
			cst.registerOutParameter(2,Types.FLOAT);
			cst.execute();
			System.out.println("Bill amount for Customer Name "+name+" is : "+cst.getFloat(2)); 
			con.close();
			break;
		}
		case 3: {
			System.out.println("Enter Mobile No :");
			Long mob = scan.nextLong();
			
			CallableStatement cst1 = con.prepareCall("{call fetchMN(?,?)}");
			cst1.setLong(1,mob);
			cst1.registerOutParameter(2,Types.INTEGER);
			cst1.execute();
			if(cst1.getInt(2)==0) {
				System.out.println("Invalid Mobile No");
				break;
			}
			
			CallableStatement cst = con.prepareCall("{call checkbillMN(?,?)}");
			cst.setLong(1,mob);
			cst.registerOutParameter(2,Types.FLOAT);
			cst.execute();
			System.out.println("Bill amount for Customer with Mobile No "+mob+" is : "+cst.getFloat(2)); 
			con.close();
			break;
		}
		default:
			System.out.println(key + " is Invalid input");
		}
	}
	
	public static void insertNew() throws Exception{
		Scanner scan = new Scanner(System.in);
		Scanner scan1 = new Scanner(System.in);
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BillingDB","root","Ark@2037");
		Statement st = con.createStatement();
		System.out.println("Enter Following Customer Details");
		System.out.println("Enter Meter No : ");
		int no = scan.nextInt();
		System.out.println("Enter Customer Name : ");
		String name = scan1.nextLine();
		System.out.println("Enter Mobile No : ");
		Long mob = scan.nextLong();
		System.out.println("Enter Connection Date : ");
		String date = scan.next();
		
		/*CallableStatement cst = con.prepareCall("{call insertdata(?,?,?,?)}");
		cst.setInt(1,no);
		cst.setString(2,name);
		cst.setLong(3,mob);
		cst.setString(4,date);
		cst.execute();
		*/
		
		String query = "insert into gasbilling (Meter_No, Cust_Name, Mobile_No, Conn_Date,Prev_Reading,Present_Reading) values (?,?,?,?,?,?);";
		PreparedStatement pst = con.prepareStatement(query);
		pst.setInt(1,no);
		pst.setString(2,name);
		pst.setLong(3,mob);
		pst.setString(4,date);
		pst.setLong(5,0);
		pst.setLong(6,0);
		pst.execute();
		System.out.println("Customer details added succesfully ");
		con.close();
		
	}
	
	
	public static void updateCust() throws Exception{
		Scanner scan = new Scanner(System.in);
		Scanner scan1 = new Scanner(System.in);
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BillingDB","root","Ark@2037");
		Statement st = con.createStatement();
		
		System.out.println("Which detail you want to update");
		System.out.println("Press[1] - Customer Name \nPress[2] - Mobile No\nPress[3] - Connection Date");
		
		int key = scan.nextInt();
		
		switch (key) {
		case 1: 
		{	
			System.out.println("Enter Meter No : ");
			int no = scan.nextInt();
			if(checkmeterno(no)==0) {
				System.out.println("Invalid meter no");
				break;
			}
			System.out.println("Enter updated Name : ");
			String name = scan1.nextLine();
			String query = "update gasbilling  set Cust_Name=? where Meter_No=?;";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setString(1,name);
			pst.setInt(2,no);
			int rows = pst.executeUpdate();
			System.out.println("Name updated to : "+name);
			con.close();
			break;
		}
		case 2: 
		{	
			
			System.out.println("Enter Meter No : ");
			int no = scan.nextInt();
			
			if(checkmeterno(no)==0) {
				System.out.println("Invalid meter no");
				break;
			}
			System.out.println("Enter updated Mobile no : ");
			Long mob = scan.nextLong();
			String query = "update gasbilling  set Mobile_No=? where Meter_No=?;";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setLong(1,mob);
			pst.setInt(2,no);
			int rows = pst.executeUpdate();
			System.out.println("Mobile No updated to : "+mob);
			con.close();
			break;
		}
		
		case 3: 
		{	
			System.out.println("Enter Meter No : ");
			int no = scan.nextInt();
			if(checkmeterno(no)==0) {
				System.out.println("Invalid meter no");
				break;
			}
			
			System.out.println("Enter correct Connection Date : ");
			String date= scan.next();
			String query = "update gasbilling  set Conn_Date=? where Meter_No=?;";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setString(1,date);
			pst.setInt(2,no);
			int rows = pst.executeUpdate();
			System.out.println("Connection date updated to "+date);
			con.close();
			break;
		}
		default:
			System.out.println(key+" is invalid input");
			break;
		} 
	}
	
	public static void deleteCust() throws Exception {
		Scanner scan = new Scanner(System.in);
		Scanner scan1 = new Scanner(System.in);
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BillingDB","root","Ark@2037");
		Statement st = con.createStatement();
		System.out.println("Enter Meter No : ");
		int no = scan.nextInt();
		if(checkmeterno(no)==1){
		System.out.println("Please mention the reason for deletion of Customer Details:");
		String reason = scan1.nextLine();
		String query = "delete from gasbilling where Meter_No="+no+";";
		int rows = st.executeUpdate(query);
		System.out.println("No of customers Deleted : "+rows);
		con.close();
		}
		else {
			System.out.println(no+" is Invalid input");
		}
	}
	
	public static int checkmeterno(int no) throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BillingDB","root","Ark@2037");
		CallableStatement cst1 = con.prepareCall("{call fetchmeterno(?,?)}");
		cst1.setInt(1,no);
		cst1.registerOutParameter(2,Types.INTEGER);
		cst1.execute();
		return cst1.getInt(2);
	}

}
