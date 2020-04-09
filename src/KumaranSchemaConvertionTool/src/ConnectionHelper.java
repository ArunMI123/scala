import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionHelper {
	
	public static void closeConnection(Connection conn)
	{
		 if (conn != null) {
		        try {
		            conn.close();
		        } catch (SQLException e) { e.printStackTrace();}
		    }
		
	}
	public static void closeResultSet(ResultSet rs)
	{
		 if (rs != null) {
		        try {
		        	rs.close();
		        } catch (SQLException e) { e.printStackTrace();}
		    }
	}
	public static void closeStatement(Statement statement)
	{
		 if (statement!=null) {
		        try {
		        	statement.close();
		        } catch (SQLException e) { e.printStackTrace();}
		    }
	}
}
