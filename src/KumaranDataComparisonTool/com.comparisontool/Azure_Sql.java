import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class Azure_Sql {
	public static ResultSet getRsltSet(Connection con, String sql) {
		Statement stm = null;
		ResultSet rsPackage = null;
		try {
			stm = con.createStatement();
			rsPackage = stm.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rsPackage;

	}

	public static void main(String[] args) {
		ArrayList<String> listofTable_SQL = new ArrayList<String>();
		ArrayList<String> listofViews_SQL = new ArrayList<String>();
		ArrayList<String> listofSynonymn_SQL = new ArrayList<String>();
		ArrayList<String> listofProcedure_SQL = new ArrayList<String>();
		ArrayList<String> listofsquence_SQL = new ArrayList<String>();

		ArrayList<String> listofTable = new ArrayList<String>();
		ArrayList<String> listofViews = new ArrayList<String>();
		ArrayList<String> listofSynonymn = new ArrayList<String>();
		ArrayList<String> listofProcedure = new ArrayList<String>();
		ArrayList<String> listofsquence = new ArrayList<String>();
		
		try {
			Properties props = new Properties();
			InputStream inst = new FileInputStream("src/connectivity.properties");
			props.load(inst);
			inst.close();
			

			Class.forName(props.getProperty("jdbc.driver.sql"));
			Connection con_SQL = DriverManager.getConnection(props.getProperty("jdbc.url.sql"),
					props.getProperty("jdbc.username.sql"), props.getProperty("jdbc.pwd.sql"));
			DatabaseMetaData metaData_SQL = con_SQL.getMetaData();
			ResultSet sqlRs = metaData_SQL.getTables(null, null, "%", null);
			///ResultSet sqlRs_colmn = metaData_SQL.getColumns(null, null, "%", null);
			ResultSet sqlRs_procedures = metaData_SQL.getProcedures(null, null, "%");
			//ResultSet sqlRs_indexes = metaData_SQL.getIndexInfo(null, null, null, true, true);

			Class.forName(props.getProperty("jdbc.driver"));
			Connection con = DriverManager.getConnection(props.getProperty("jdbc.url"),
					props.getProperty("jdbc.username"), props.getProperty("jdbc.pwd"));
			DatabaseMetaData metaData = con.getMetaData();
			ResultSet rsOracle = metaData.getTables(null, null, "%", null);
			ResultSet oracle_procedures = metaData.getProcedures(null, null, "%");
			String synQuery = "select name from sys.synonyms";
			String synQuery_oracle = "select * from sys.all_synonyms where owner='"
					+ props.getProperty("jdbc.schema.name") + "'";
		
			ResultSet rsPackage_Oracle = getRsltSet(con, synQuery_oracle);
			while (rsPackage_Oracle.next()) {
				listofSynonymn.add(rsPackage_Oracle.getString("synonym_name"));
			}
			
			ResultSet rsPackage = getRsltSet(con_SQL, synQuery);
			while (rsPackage.next()) {
				listofSynonymn_SQL.add(rsPackage.getString("name"));
			}
			

			Collections.sort(listofSynonymn_SQL);
			Collections.sort(listofSynonymn);	
			
			String seq_sql = "select name from sys.sequences";
			ResultSet rsSeq_Sql = getRsltSet(con_SQL, seq_sql);
			while (rsSeq_Sql.next()) {
				listofsquence_SQL.add(rsSeq_Sql.getString("name"));
			}
			Collections.sort(listofsquence_SQL);
			
			String seq_orcle = "select sequence_name from user_sequences";
			ResultSet rsSeq_Oracle = getRsltSet(con, seq_orcle);
			while (rsSeq_Oracle.next()) {
				listofsquence.add(rsSeq_Oracle.getString("sequence_name"));
			}
			Collections.sort(listofsquence);
			/* Result set forTables,Views,Columns in MS sql DB */
			while (sqlRs.next()) {
				
				if (sqlRs.getString(2).equals(props.getProperty("jdbc.schema.name"))) {
					if (sqlRs.getString(4).equalsIgnoreCase(props.getProperty("metadata.types.1"))) {
						listofTable_SQL.add(sqlRs.getString(3));
						Collections.sort(listofTable_SQL);
					}

					if (sqlRs.getString(4).equalsIgnoreCase(props.getProperty("metadata.types.3"))) {
						listofSynonymn_SQL.add(sqlRs.getString(3));
					}

					if (sqlRs.getString(4).equalsIgnoreCase(props.getProperty("metadata.types.2"))) {
						listofViews_SQL.add(sqlRs.getString(3));
					}
				}
			}
			/* Result set forTables,Views,Columns in OracleL DB */
			while (rsOracle.next()) {
				if (rsOracle.getString(2).equals(props.getProperty("jdbc.schema.name"))) {

					if (rsOracle.getString(4).equalsIgnoreCase(props.getProperty("metadata.types.1"))) {
						listofTable.add(rsOracle.getString(3));
						Collections.sort(listofTable);
					}
					/*
					 * if
					 * (rsOracle.getString(4).equalsIgnoreCase(props.getProperty
					 * ("metadata.types.3"))) {
					 * listofSynonymn.add(rsOracle.getString(3)); }
					 */
					if (rsOracle.getString(4).equalsIgnoreCase(props.getProperty("metadata.types.2"))) {
						listofViews.add(rsOracle.getString(3));
					}
				}
			}
			
			while (oracle_procedures.next()) {
				if (props.getProperty("jdbc.schema.name").equals(oracle_procedures.getString("PROCEDURE_SCHEM"))) {

					listofProcedure.add(oracle_procedures.getString("PROCEDURE_NAME"));
					Collections.sort(listofProcedure);
				}
			}
			while (sqlRs_procedures.next()) {
				if (props.getProperty("jdbc.schema.name").equals(sqlRs_procedures.getString("PROCEDURE_SCHEM"))) {
						listofProcedure_SQL.add(sqlRs_procedures.getString("PROCEDURE_NAME").substring(sqlRs_procedures.getString("PROCEDURE_NAME").indexOf("$")+1, sqlRs_procedures.getString("PROCEDURE_NAME").lastIndexOf(";")));
					//listofProcedure_SQL.add(sqlRs_procedures.getString("PROCEDURE_NAME"));
					
					Collections.sort(listofProcedure_SQL);
				}
			}
			System.out.println("ORACLE DATABASE");
			System.out.println("Product Name : "+metaData.getDatabaseProductName());
			System.out.println("Product Version : "+metaData.getDatabaseProductVersion());
			System.out.println("MS SQL DATABASE");
			System.out.println("Product Name : "+metaData_SQL.getDatabaseProductName());
			System.out.println("Product Version : "+metaData_SQL.getDatabaseProductVersion());
			
			
			
			System.out.println("Total no.of tables in Oracle DB(RREB0718):" + listofTable.size());
			System.out.println("Total no.of tables in MS SQL DB(AzureRRE):" + listofTable_SQL.size());
			if ((!listofTable.isEmpty() || !listofTable_SQL.isEmpty())
					&& (listofTable.size() != (listofTable_SQL.size()))) {
				if ((listofTable.size() > listofTable_SQL.size())) {
					listofTable.removeAll(listofTable_SQL);
					System.out.println("Tables that are not present in SQL DB");
					System.out.println(listofTable);

				} else {
					listofTable_SQL.removeAll(listofTable);
					System.out.println("Tables that are not present in Oracle  DB ");
					System.out.println(listofTable_SQL);
				}
			}
			
			
			
			System.out.println("---------------------------------------");
			System.out.println("Total no.of VIEW in Oracle DB(RREB0718):" + listofViews.size());
			System.out.println("Total no.of VIEW in MS SQL DB(AzureRRE):" + listofViews_SQL.size());
			if ((!listofViews.isEmpty() || !listofViews_SQL.isEmpty())
					&& (listofViews.size() != listofViews_SQL.size())) {
				if ((listofViews.size() > listofViews_SQL.size())) {
					listofViews.removeAll(listofViews_SQL);
					System.out.println("View that are not present in SQL DB ");
					System.out.println(listofViews);

				} else {
					listofViews_SQL.removeAll(listofViews);
					System.out.println("View that are not present in Oracle  DB ");
					System.out.println(listofViews_SQL);
				}
			}
			
			
			
			System.out.println("---------------------------------------");
			System.out.println("Total no.of SYNONYM in Oracle DB(RREB0718):" + listofSynonymn.size());
			System.out.println("Total no.of SYNONYM in MS SQL DB(AzureRRE):" + listofSynonymn_SQL.size());
			if ((!listofSynonymn.isEmpty() || !listofSynonymn_SQL.isEmpty())
					&& (listofSynonymn.size() != listofSynonymn_SQL.size())) {
				if ((listofSynonymn.size() > listofSynonymn_SQL.size())) {
					listofSynonymn.removeAll(listofSynonymn_SQL);
					System.out.println("Synonym that are not present in SQL DB ");
					System.out.println(listofSynonymn);

				} else {
					listofSynonymn_SQL.removeAll(listofSynonymn);
					System.out.println("Synonym that are not present in Oracle  DB ");
					System.out.println(listofSynonymn_SQL);
				}
			}
			
			
			
			System.out.println("---------------------------------------");
			System.out.println("Total no.of Procedures in Oracle DB(RREB0718):" + listofProcedure.size());
			System.out.println("Total no.of Procedures in MS SQL DB(AzureRRE):" + listofProcedure_SQL.size());
			if ((!listofProcedure.isEmpty() || !listofProcedure_SQL.isEmpty())
					&& (listofProcedure.size() != listofProcedure_SQL.size())) {
				if ((listofProcedure.size() > listofProcedure_SQL.size())) {
					listofProcedure.removeAll(listofProcedure_SQL);
					System.out.println("Procedures that are not present in SQL DB ");
					System.out.println(listofProcedure);

				} else {
					listofProcedure_SQL.removeAll(listofProcedure);
					System.out.println("Procedures that are not present in Oracle  DB ");
					System.out.println(listofProcedure_SQL);
				}
			}
			
			System.out.println("---------------------------------------");
			System.out.println("Total no.of sequence in Oracle DB(RREB0718):" + listofsquence.size());
			System.out.println("Total no.of sequence in MS SQL DB(AzureRRE):" + listofsquence_SQL.size());
			if ((!listofsquence.isEmpty() || !listofsquence_SQL.isEmpty())
					&& (listofsquence.size() != listofsquence_SQL.size())) {
				if ((listofsquence.size() > listofsquence_SQL.size())) {
					listofsquence.removeAll(listofsquence_SQL);
					System.out.println("Sequence that are not present in SQL DB");
					System.out.println(listofsquence);

				} else {
					listofsquence_SQL.removeAll(listofsquence);
					System.out.println("Sequence that are not present in Oracle  DB");
					System.out.println(listofsquence_SQL);
				}
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
