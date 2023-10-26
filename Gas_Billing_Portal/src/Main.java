import java.lang.invoke.MethodHandles.Lookup.ClassOption;
import java.sql.*;
import java.util.*;

//db - BillingDB
//table - gasbilling
public class Main {
	public static void main(String[] args) throws Exception{
		//insertNew();
		//deleteCust();
		viewDB();
		updateCust();
		viewDB();

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
			
			CallableStatement cst = con.prepareCall("{call fetchmeterno(?,?)}");
			cst.setInt(1,no);
			cst.registerOutParameter(2,Types.INTEGER);
			cst.execute();
			if(cst.getInt(2)==0) {
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
			System.out.println("rows updated : "+rows);
			con.close();
			break;
		}
		case 2: 
		{	
			
			System.out.println("Enter Meter No : ");
			int no = scan.nextInt();
			
			CallableStatement cst = con.prepareCall("{call fetchmeterno(?,?)}");
			cst.setInt(1,no);
			cst.registerOutParameter(2,Types.INTEGER);
			cst.execute();
			if(cst.getInt(2)==0) {
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
			System.out.println("rows updated : "+rows);
			con.close();
			break;
		}
		case 3: 
		{	
			
			System.out.println("Enter Meter No : ");
			int no = scan.nextInt();
			
			CallableStatement cst = con.prepareCall("{call fetchmeterno(?,?)}");
			cst.setInt(1,no);
			cst.registerOutParameter(2,Types.INTEGER);
			cst.execute();
			if(cst.getInt(2)==0) {
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
			System.out.println("rows updated : "+rows);
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
		System.out.println("Please mention the reason for deletion of Customer Details:");
		String reason = scan1.nextLine();
		String query = "delete from gasbilling where Meter_No="+no+";";
		int rows = st.executeUpdate(query);
		System.out.println("rows effected : "+rows);
		con.close();
		
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
		String query = "insert into gasbilling (Meter_No, Cust_Name, Mobile_No, Conn_Date,Prev_Reading,Present_Reading) values (?,?,?,?,0,0);";
		PreparedStatement pst = con.prepareStatement(query);
		pst.setInt(1,no);
		pst.setString(2,name);
		pst.setLong(3,mob);
		pst.setString(4,date);
		pst.executeUpdate();
		
		con.close();
	}
	
	public static void viewDB() throws Exception{
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/BillingDB","root","Ark@2037");
		Statement st = con.createStatement();
		String query = "select * from gasbilling";
		ResultSet rs = st.executeQuery(query);

		System.out.println("Meter No"+"\tCustomer Name"+"\t\tMobile No"+"\t\tConnection Date"+"\t\tPrevious Reading"+"\tPresent reading"+"\t\tUnits"+"\t\tBill");
		
		while(rs.next()) {
		System.out.println(rs.getInt(1)+"\t\t"+rs.getString(2)+"\t\t"+rs.getLong(3)+"\t\t"+rs.getString(4)+"\t\t"+rs.getInt(5)+"\t\t\t"+rs.getInt(6)+"\t\t\t"+rs.getInt(7)+"\t\t"+rs.getFloat(8));
		}
		
		con.close();
	}
}
