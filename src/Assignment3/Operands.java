package Assignment3;

import java.io.IOException;
import java.sql.*;

import javax.swing.*;

public class Operands
{
	public final static String link = "jdbc:mysql://localhost:3306/";
	private static Report rpt = new Report();
	private String tableName = "";
	
	public static void main(String[] arg) throws IOException
	{
		
		JFrame frame = new JFrame("REPORT");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setSize(600,400);
		
		JPanel mainPane = new JPanel();
		rpt.setTabed(rpt.tabPanels(rpt.associate(rpt.manipulateTable(), rpt.tableFunction(rpt.tableMode()))));
		mainPane.add(rpt.getTabed());
		frame.add(mainPane);
	}
	
	public void createTable(String query)
	{
		
		Connection con;
		try {
			if(istable(getTableName()))
			{
				JOptionPane.showMessageDialog(null, "Table " + tableName + " is already in the database");
			}
			else
			{
				con = DriverManager.getConnection(link + "group2","root","");
				Statement st = con.createStatement();
				st.executeUpdate(query);
			}

		} catch (SQLException e) 
		{

			e.printStackTrace();
		}

	}
	
	public static Boolean istable(String tablename) throws SQLException
	{
		Boolean present = false;
		Connection con = DriverManager.getConnection(link+"group2","root","");
		DatabaseMetaData dbmd = con.getMetaData();
		ResultSet rs = dbmd.getTables(null, null, tablename, null);
		if(rs.next())
		{
			present = true;
		}
		return present;
	}
	public static void inputdata(String query,String basename)
	{
		Connection connect = null;
		PreparedStatement command = null;
		try 
		{
			connect = DriverManager.getConnection(link+basename,"root","");
			command = connect.prepareStatement(query);
			command.execute();
			JOptionPane.showMessageDialog(null, "data is successfully saved into the database and stock is updated");
		}
		catch(SQLException err)
		{
			JOptionPane.showMessageDialog(null, "Error "+err);
		}
	}
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	

}
