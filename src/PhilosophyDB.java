import java.io.File;
import java.sql.*;

public class PhilosophyDB {
	
	private static String db_name = "philosophy.db";
	
	public static void connect() {
		
		File file = new File(db_name);
		if(!file.exists()) {
			Connection c = null;
			Statement stmt = null;
			try{
				Class.forName("PhilosophyDB").newInstance();
				c = DriverManager.getConnection("jdbc:sqlite:" + db_name);
				stmt = c.createStatement();
				
				String sql = "CREATE TABLE phil " +
							"(TERM CHAR(200) PRIMARY KEY NOT NULL," + 
							"CLICKS INT NOT NULL)";
				stmt.execute(sql);
				stmt.close();
				c.close();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
	
	public static void put(String s, int i) throws Exception {
		Class.forName("PhilosophyDB");
		Connection c = DriverManager.getConnection("jdbc:sqlite:" + db_name);
		String sql = "INSERT into phil (TERM, CLICKS) VALUES ('" + s + "', " + i + ");";
		PreparedStatement stmt = c.prepareStatement(sql);
		stmt.executeUpdate();
		stmt.close();
		c.close();
	}
	
	public static int get(String s) throws Exception {
		Class.forName("PhilosophyDB");
		Connection c = DriverManager.getConnection("jdbc:sqlite:" + db_name);
		String sql = "SELECT CLICKS FROM phil WHERE TERM= '" + s + "';";
		PreparedStatement stmt = c.prepareStatement(sql);
		ResultSet res = stmt.executeQuery();
		int ret = -1;
		if(res.next()) {
			ret = res.getInt("clicks");
		}
		stmt.close();
		c.close();
		res.close();
		return ret;
	}
	
	public static boolean exists(String s) throws Exception {
		Class.forName("PhilosophyDB");
		Connection c = DriverManager.getConnection("jdbc:sqlite:" + db_name);
		Statement stmt = c.createStatement();
		String sql = "SELECT count(*) FROM phil WHERE term = '" + s + "';";
		ResultSet res = stmt.executeQuery(sql);
		boolean ret = false;
		if (res.next()) {
			if(res.getInt(1) == 1) {
				ret = true;
			}
		}
		stmt.close();
		c.close();
		res.close();
		return ret;
	}

	public static int getAll() throws Exception {
		Class.forName("PhilosophyDB");
		Connection c = DriverManager.getConnection("jdbc:sqlite:" + db_name);
		Statement stmt = c.createStatement();
		String sql = "SELECT count(*) FROM phil;";
		ResultSet res = stmt.executeQuery(sql);
		res.next();
		return res.getInt(1);
	}

	public static int getSuccess() throws Exception {
		Class.forName("PhilosophyDB");
		Connection c = DriverManager.getConnection("jdbc:sqlite:" + db_name);
		Statement stmt = c.createStatement();
		String sql = "SELECT count(*) FROM phil WHERE clicks > 0;";
		ResultSet res = stmt.executeQuery(sql);
		res.next();
		return res.getInt(1);
	}

	public static int getFail() throws Exception{
		Class.forName("PhilosophyDB");
		Connection c = DriverManager.getConnection("jdbc:sqlite:" + db_name);
		Statement stmt = c.createStatement();
		String sql = "SELECT count(*) FROM phil WHERE clicks = -1;";
		ResultSet res = stmt.executeQuery(sql);
		res.next();
		return res.getInt(1);
	}

	public static int getSum() throws Exception {
		Class.forName("PhilosophyDB");
		Connection c = DriverManager.getConnection("jdbc:sqlite:" + db_name);
		Statement stmt = c.createStatement();
		String sql = "SELECT sum(clicks) FROM phil WHERE clicks > 0;";
		ResultSet res = stmt.executeQuery(sql);
		res.next();
		return res.getInt(1);
	}

}
