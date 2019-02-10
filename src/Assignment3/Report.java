package Assignment3;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class Report extends Operands implements ActionListener
{
	private JTable table = new JTable();
	private JTabbedPane tabed = new JTabbedPane();
	private JButton save = new JButton("To Database");
	
	public JTabbedPane getTabed() {
		return tabed;
	}
	public void setTabed(JTabbedPane tabed) {
		this.tabed = tabed;
	}
	public JTabbedPane tabPanels(JPanel panel1)
	{
		tabed.removeAll();
		tabed.add(panel1);
		return tabed;
	}
	public JPanel associate(JPanel panel1, JPanel panel2)
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(panel1, BorderLayout.WEST);
		panel.add(panel2,BorderLayout.CENTER);
		return panel;
	}

	public String grade(double mark)
	{
		String grd = "";
		
		if(mark >= 70 && mark <= 100)
			grd = "A";
		else if(mark >= 60 && mark < 70)
			grd = "B+";
		else if(mark >= 50 && mark < 60)
			grd = "B";
		else if(mark >= 40 && mark < 50)
			grd = "C";
		else if(mark >= 30 && mark < 40)
			grd = "D";
		else if(mark >= 0 && mark < 30)
			grd = "F";
		
		return grd;
	}
	
	public JPanel tableFunction(DefaultTableModel dtm) throws FileNotFoundException
	{
		JTable table = new JTable();
		JScrollPane scroll = new JScrollPane(table);
		table.setModel(dtm);
		setTable(table);
		JPanel panel = new JPanel();
		panel.add(scroll);
		return panel;
	}
	public JPanel manipulateTable()
	{
		JPanel panel = new JPanel();
		JButton sum = new JButton("SUM AVERAGE");
		JButton classAverage = new JButton("CLASS AVERAGE");
		JButton grade = new JButton("GRADE");
		JTextField totaltxt = new JTextField(10);
		JTextField classAvrg = new JTextField(10);
		classAvrg.setEditable(false);
		totaltxt.setEditable(false);
		
		sum.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						JTable actTable = getTable();
						double sum = 0.0;
						for(int i = 0; i < actTable.getRowCount(); i++)
						{
							Object value = actTable.getValueAt(i, 3);
							sum = sum + Double.parseDouble(value.toString());
						}
						totaltxt.setText(Double.toString(sum));
					}
				});
		
		classAverage.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						JTable actTable = getTable();
						double sum = 0.0;	int i = 0;
						for(; i < actTable.getRowCount(); i++)
						{
							Object value = actTable.getValueAt(i, 3);
							sum = sum + Double.parseDouble(value.toString());
						}
						
						Double classAv = sum/i;
						DecimalFormat df = new DecimalFormat("#.00");
						classAvrg.setText(df.format(classAv));
					}
				});
		grade.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						JTable actTable = getTable();
						try {
							actTable.setModel(modifyTable(tableMode()));
							setTable(actTable);
							JPanel pane = associate(manipulateTable(),tableFunction(modifyTable(tableMode())));
							tabPanels(pane);
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
					}
				});
		save.addActionListener(this);
		panel.add(sum);
		panel.add(totaltxt);
		panel.add(classAverage);
		panel.add(classAvrg);
		panel.add(grade);
		panel.add(save);
		return panel;
	}
	
	public DefaultTableModel tableMode() throws FileNotFoundException
	{
		DefaultTableModel dtm = new DefaultTableModel();
		Scanner scanner = new Scanner(new File("DATA.csv"));
		int x = 0; 
		for(int i = 0; scanner.hasNext(); i++) 
		{
            List<String>line = FilterCsv.parseLine(scanner.nextLine());
            
            if(x == 0)
			{
				for(;x < 4;x++)
				{
					dtm.addColumn(line.get(x));
				}
				x = 0;
			}
            else {
            	Object[] newline = line.toArray();
            	dtm.addRow(newline);
            }
            x++;
        }
        scanner.close();
      
		return dtm;
	}
	
	private DefaultTableModel modifyTable(DefaultTableModel dtm)
	{
		dtm.addColumn("GRADE");
		for(int i = 0; i < dtm.getRowCount(); i++)
		{	
			Object v = dtm.getValueAt(i, 3);
			double mk = Double.parseDouble((String) v);
			String c = grade(mk);
			dtm.setValueAt(c, i, 4);
		}
		return dtm;
	}
	
	public void toDatabase()
	{
		String query = "create table " + getTableName() + " ("
				+ "RegistrationNo varchar(15),"
				+ "Stu_Name varchar(45) not null,"
				+ "Co_Assesment int,"
				+ "Uni_Exam int,"
				+ "Uni_Exam_Grade varchar(5)"
				+ "constraint " + getTableName() + "_pk primary key(RegistrationNo))";
		createTable(query);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == save)
		{
			String name = JOptionPane.showInputDialog("Enter name of "
					+ "the table to be created in the database");
			setTableName(name);
			toDatabase();
			JTable tb = getTable();
			TableModel model = tb.getModel();
			String sql = "insert into " + getTableName() + " (";
			if(tb.getColumnCount() == 4)
			{
				sql = sql + "Stu_Name,RegistrationNo,Co,Assesment,Uni_Exam)"
						+ " values('";
				for(int i = 0; i < 4; i++)
				{
					sql = sql + tb.getValueAt(i, 0) + "','"
							+ tb.getValueAt(i, 1) + "',"
							+ tb.getValueAt(i, 2) + ","
							+ tb.getValueAt(i, 3) + ")";
					inputdata(sql,"group2");
				}
			}
			else if(tb.getColumnCount() == 5)
			{
				sql = sql + "Stu_Name,RegistrationNo,Co,Assesment,Uni_Exam,Uni_Exam_Grade)"
						+ " values('";
				for(int i = 0; i < 5; i++)
				{
					sql = sql + tb.getValueAt(i, 0) + "','"
							+ tb.getValueAt(i, 1) + "',"
							+ tb.getValueAt(i, 2) + ","
							+ tb.getValueAt(i, 3) + ",'"
							+ tb.getValueAt(i, 4) + "')";
					inputdata(sql,"group2");
				}
			}
		}
		
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}
	public JButton getSave() {
		return save;
	}
	public void setSave(JButton save) {
		this.save = save;
	}
	
}


