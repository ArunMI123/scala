import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;

public class TestList {

	HSSFWorkbook xssBook = new HSSFWorkbook();
	HSSFSheet xssSheet = xssBook.createSheet("Report");
	HSSFRow row;
	int rowId = 1;
	int cellId = 0;
	Cell cell = null;
	int tempRowId = 0;
	int sheetid = 1;

	public static void main(String[] args) {
		TestList lComparisonTest = new TestList();
		try {
			lComparisonTest.compare();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	int a = 0;

	public void compare() throws Exception {
		Properties lProps = new Properties();
		InputStream lInst = new FileInputStream("src/connectivity1.properties");
		lProps.load(lInst);
		lInst.close();
		ArrayList<String> lDbListofTable1 = new ArrayList<String>();
		ArrayList<String> lDbListofTable2 = new ArrayList<String>();

		// I am using Oracle here, but you can use any database
		Connection lDbConnection1 = getConnection(lProps.getProperty("connection1.url"),
				lProps.getProperty("connection1.username"), lProps.getProperty("connection1.pwd"),
				lProps.getProperty("connection1.driver"));
		Connection lDbConnection2 = getConnection(lProps.getProperty("connection2.url"),
				lProps.getProperty("connection2.username"), lProps.getProperty("connection2.pwd"),
				lProps.getProperty("connection2.driver"));
		DatabaseMetaData lDbMetaData1 = lDbConnection1.getMetaData();
		ResultSet lRsDb1 = lDbMetaData1.getTables(null, null, "%", null);
		DatabaseMetaData lDbMetaData2 = lDbConnection2.getMetaData();
		ResultSet lRsDb2 = lDbMetaData2.getTables(null, null, "%", null);

		while (lRsDb1.next()) {
			if (lRsDb1.getString(2).equals(lProps.getProperty("connection.schema.name"))) {
				if (lRsDb1.getString(4).equalsIgnoreCase(lProps.getProperty("metadata.types.1"))) {
					lDbListofTable1.add(lRsDb1.getString(3));
					Collections.sort(lDbListofTable1);
				}
			}
		}
		while (lRsDb2.next()) {

			if (lRsDb2.getString(2).equals(lProps.getProperty("connection.schema.name"))) {
				if (lRsDb2.getString(4).equalsIgnoreCase(lProps.getProperty("metadata.types.1"))) {
					lDbListofTable2.add(lRsDb2.getString(3));
					Collections.sort(lDbListofTable2);
				}
			}
		}
		final Object obj = new Object();

		synchronized (obj) {
			try {
				for (int i = 0; i < lDbListofTable1.size(); i++) {

					String lTableName = lDbListofTable1.get(i);
					/*
					 * if("ANSWER".equals(lTableName)||"CONDITION_MESSAGE".
					 * equals(lTableName)||"EXTERNAL_RATING_ITEM".equals(
					 * lTableName)
					 * ||"FA_DESCRIPTIVE_ADJ_REASON".equals(lTableName)||
					 * "FA_RISK_DRIVER".equals(lTableName)){
					 */

					// 10000 records
					/*
					 * if("ANSWER".equals(lTableName)
					 * ||"FA_DESCRIPTIVE_ADJ_REASON".equals(lTableName)||
					 * "FA_RISK_DRIVER".equals(lTableName)){
					 */

					 if
					 ("USER_ROLE_STG".equals(lTableName))
					 {
					int lCount = 0;
					ResultSet lResultSet = getResulCount("Database1", lDbConnection1, lTableName, lProps);
					if (lResultSet != null) {
						while (lResultSet.next()) {
							lCount = lResultSet.getInt(1);
						}
					}

					int rCount = 0;
					ResultSet lRightResultSet = getResulCount("Database2", lDbConnection2, lTableName, lProps);
					if (lRightResultSet != null) {
						while (lRightResultSet.next()) {
							rCount = lRightResultSet.getInt(1);
						}
					}

					System.out.println(lCount);
					int commonCount = lCount + rCount;

					List<String> primaryColNameList = null;
					
					
							
					primaryColNameList = primaryKeyColumnCheck(lDbConnection1.getMetaData(), lTableName,lProps);
					if (primaryColNameList.size() != 0) {
					//	System.out.println(primaryColNameList);
						SingleTableCompare r = new SingleTableCompare(obj, commonCount, lTableName);
						Thread th = new Thread(r);
						th.start();
						try {
							obj.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						// excel to wrting in
					}

				}
				 }
			} finally {
				closeAll(lRsDb1);
				closeAll(lRsDb2);
			}
		}
	}

	public class SingleTableCompare implements Runnable {
		int innerCount = 0;
		TestList parent;
		Object obj;
		String lTableName = null;
		SingleTableCompare lParent;
		CountDownLatch latch1;
		CountDownLatch latch2;
		CountDownLatch latch3;
		CountDownLatch latch4;
		Map<Long, Map<Long, List<Object>>> leftQueue = new ConcurrentHashMap<Long, Map<Long, List<Object>>>();
		Map<Long, Map<Long, List<Object>>> rightQueue = new ConcurrentHashMap<Long, Map<Long, List<Object>>>();
		List<Integer> leftCount = new ArrayList<Integer>();
		List<Integer> rightCount = new ArrayList<Integer>();
		Boolean lPrimaryKeyColumnIndicator = false;
		Map<Long, Map<Long, List<Object>>> matchedData = new ConcurrentHashMap<Long, Map<Long, List<Object>>>();
		Map<Long, Map<Long, List<Object>>> misMatchedData = new ConcurrentHashMap<Long, Map<Long, List<Object>>>();
		Map<Long, Map<Long, List<Object>>> leftDataMisMatch = new ConcurrentHashMap<Long, Map<Long, List<Object>>>();
		Map<Long, Map<Long, List<Object>>> rightDataMisMatch = new ConcurrentHashMap<Long, Map<Long, List<Object>>>();

		Map<Long, Map<Long, List<Object>>> leftOrphan = new ConcurrentHashMap<Long, Map<Long, List<Object>>>();
		Map<Long, Map<Long, List<Object>>> rightOrphan = new ConcurrentHashMap<Long, Map<Long, List<Object>>>();

		List<Map<Long, List<Object>>> misMatchDataList = new ArrayList<Map<Long, List<Object>>>();
		List<Map<Long, List<Object>>> matchDataList = new ArrayList<Map<Long, List<Object>>>();

		public SingleTableCompare(Object obj, int commonCount, String lTableName) {

			this.obj = obj;
			latch1 = new CountDownLatch(2);
			latch2 = new CountDownLatch(2);
			this.innerCount = commonCount;
			this.lTableName = lTableName;

		}

		@Override
		public void run() {
			synchronized (this.obj) {

				Properties lProps = new Properties();
				InputStream lInst;
				try {
					lInst = new FileInputStream("src/connectivity1.properties");
					lProps.load(lInst);
					lInst.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				Connection lDbConnection1 = getConnection(lProps.getProperty("connection1.url"),
						lProps.getProperty("connection1.username"), lProps.getProperty("connection1.pwd"),
						lProps.getProperty("connection1.driver"));
				Connection lDbConnection2 = getConnection(lProps.getProperty("connection2.url"),
						lProps.getProperty("connection2.username"), lProps.getProperty("connection2.pwd"),
						lProps.getProperty("connection2.driver"));

				DatabaseMetaData lDbMetaData1 = null;
				DatabaseMetaData lDbMetaData2 = null;
				try {
					lDbMetaData1 = lDbConnection1.getMetaData();
					lDbMetaData2 = lDbConnection1.getMetaData();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

				DataSetReader2 first = new DataSetReader2("Oracle", this, leftQueue, lDbConnection1, lTableName, lProps,
						leftCount, lDbMetaData1);
				DataSetReader2 second = new DataSetReader2("SqlServer", this, rightQueue, lDbConnection2, lTableName,
						lProps, rightCount, lDbMetaData2);

				System.out.println("enter run method...");
				first.start();
				second.start();
				while (true) {
					try {
						latch1.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					int cnt = 0;
					int leftSize = leftQueue.size();
					int rightSize = rightQueue.size();
					compareDataSet(leftQueue, rightQueue, this, lTableName);
					innerCount = innerCount - leftCount.size();
					innerCount = innerCount - rightCount.size();
					leftCount.clear();
					rightCount.clear();
					if (leftSize > 0) {
						cnt++;
						leftQueue.clear();
						latch2.countDown();

					}
					if (rightSize > 0) {
						cnt++;
						rightQueue.clear();
						latch2.countDown();
					}
					if (innerCount == 0) {
						System.out.println(">>>>>>>>>>> Final result " + lTableName + "  matched:"
								+ this.matchDataList.size() + " mismatched:" + this.misMatchDataList.size()
								+ " leftOrphen:" + this.leftOrphan.size() + " RightOrphen:" + this.rightOrphan.size());
						List allMaps3 = new ArrayList();
						Collection<Map<Long, List<Object>>> values = this.leftOrphan.values();
						List<Map<Long, List<Object>>> leftOrphanList = new ArrayList<Map<Long, List<Object>>>(values);
						Collection<Map<Long, List<Object>>> right = this.rightOrphan.values();
						List<Map<Long, List<Object>>> rightOrphanList = new ArrayList<Map<Long, List<Object>>>(right);
						// allMaps3.add(this.matchDataList);
						System.out.println(this.matchDataList);
						allMaps3.add(leftOrphanList);
						allMaps3.add(rightOrphanList);
						allMaps3.add(this.misMatchDataList);
						WriteOnExcel excel;
						try {
							if (!allMaps3.isEmpty()) {
								// writeOnExcel1(allMaps3, lTableName);

								excel = new WriteOnExcel("MisMatched", this, allMaps3, lTableName);
								excel.start();
								latch4 = new CountDownLatch(1);
								try {
									latch4.await();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						allMaps3.clear();
						latch2.countDown();
						latch2.countDown();
						this.obj.notify();
						break;
					}
					latch1 = new CountDownLatch(cnt);
					latch2 = new CountDownLatch(cnt);
				}
			}
		}

		// left is oracle db, right is sql server data.
		void compareDataSet(Map<Long, Map<Long, List<Object>>> pLeftMainData,
				Map<Long, Map<Long, List<Object>>> pRightMainData, SingleTableCompare parent, String lTableName) {
			Iterator<Entry<Long, Map<Long, List<Object>>>> lLeftMainIterator = pLeftMainData.entrySet().iterator();
			Map<Long, List<Object>> leftSubMapData = null;
			Map<Long, List<Object>> rightSubMapData = null;
			List allMaps3 = new ArrayList();
			/*
			 * parent.leftDataMisMatch.forEach((k, v) -> pLeftMainData.put(k,
			 * v)); parent.rightDataMisMatch.forEach((k, v) ->
			 * pRightMainData.put(k, v));
			 */
			parent.leftOrphan.forEach((k, v) -> pLeftMainData.put(k, v));
			parent.rightOrphan.forEach((k, v) -> pRightMainData.put(k, v));

			System.out.println(pLeftMainData);
			while (lLeftMainIterator.hasNext()) {
				Entry<Long, Map<Long, List<Object>>> leftMainEntry = lLeftMainIterator.next();
				if (pRightMainData.containsKey(leftMainEntry.getKey())) {
					leftSubMapData = new HashMap<Long, List<Object>>();
					rightSubMapData = new HashMap<Long, List<Object>>();
					rightSubMapData = pRightMainData.get(leftMainEntry.getKey());
					leftSubMapData = leftMainEntry.getValue();
					if (leftSubMapData != null) {
						for (Entry<Long, List<Object>> entry1 : leftSubMapData.entrySet()) {
							if (rightSubMapData.containsKey(entry1.getKey())) {
								parent.matchDataList.add(leftSubMapData);

							} else {

								List<Object> misMatchedData = new ArrayList<Object>();
								misMatchedData.add(leftSubMapData.get(entry1.getKey()));

								for (Map.Entry<Long, List<Object>> entry : pRightMainData.get(leftMainEntry.getKey())
										.entrySet()) {
									misMatchedData.add(entry.getValue());
								}

								Map<Long, List<Object>> tempMap = new HashMap<Long, List<Object>>();
								tempMap.put(entry1.getKey(), misMatchedData);
								parent.misMatchDataList.add(tempMap);
							}

						}

					}
					pRightMainData.remove(leftMainEntry.getKey());
					pLeftMainData.remove(leftMainEntry.getKey());
				}

			}
			allMaps3.add(this.matchDataList);
			WriteOnExcel excel;
			if (!allMaps3.isEmpty()) {
				excel = new WriteOnExcel("Matched", parent, allMaps3, lTableName);
				excel.start();
				latch3 = new CountDownLatch(1);
				try {
					latch3.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				allMaps3.clear();
			}

			pLeftMainData.forEach((k, v) -> {

				parent.leftOrphan.put(k, v);

			});
			pRightMainData.forEach((k, v) -> {

				parent.rightOrphan.put(k, v);

			});
		}

		public CountDownLatch getLatch1() {
			return this.latch1;
		}

		public CountDownLatch getLatch2() {
			return this.latch2;
		}

		public CountDownLatch getLatch3() {
			return this.latch3;
		}

		public CountDownLatch getLatch4() {
			return this.latch4;
		}

	}

	class DataSetReader2 extends Thread {
		int limit = 10000;
		List<Integer> count;
		List<Integer> rightCount;
		Map<Long, Map<Long, List<Object>>> lStoreData;
		SingleTableCompare lParent;
		Connection connection;
		String tableName;
		Properties pro;
		String name;
		ResultSet lResultSet = null;
		DatabaseMetaData lDatabaseMetaData;
		boolean lPrimaryKeyColumnIndicator;

		public DataSetReader2(String pDatabaseName, SingleTableCompare pParent,
				Map<Long, Map<Long, List<Object>>> pStoreData, final Connection pConnection, final String pTableName,
				Properties pProps, List<Integer> Prowcount, DatabaseMetaData pMetaData) {
			super(pDatabaseName);
			this.lParent = pParent;
			this.lStoreData = pStoreData;
			this.connection = pConnection;
			this.tableName = pTableName;
			this.pro = pProps;
			this.name = pDatabaseName;
			this.count = Prowcount;
			this.lDatabaseMetaData = pMetaData;
		}

		@Override
		public void run() {
			int increment = 1;
			try {
				lResultSet = getResultSet(connection, this.tableName, pro);
			} catch (SQLException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			Map<Long, List<Object>> OtherColumMap = null;
			List<String> primaryColNameList = null;

			 try {
			
			
			 primaryColNameList = getPrimaryColmnName( lDatabaseMetaData, tableName,  pro);
			
			 
			 }
			
	catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			Collections.sort(primaryColNameList);

			try {
				while (lResultSet.next()) {
					OtherColumMap = new HashMap<Long, List<Object>>();
					long lPrimaryColumnHashCode = hash(
							getPrimaryColumnValue(lResultSet, primaryColNameList, lResultSet.getMetaData()));
					long lOtherColumnsRowHashCode = hash(
							getOtherColValues(lResultSet, lResultSet.getMetaData(), primaryColNameList));
					List<Object> object = getRowObject(lResultSet, lResultSet.getMetaData());
					OtherColumMap.put(lOtherColumnsRowHashCode, object);
					count.add(1);
					if (lStoreData.containsKey(lPrimaryColumnHashCode)) {
						Map<Long, List<Object>> lExistingData = lStoreData.get(lPrimaryColumnHashCode);
						Map<Long, List<Object>> lTempData = new HashMap<Long, List<Object>>();

						lExistingData.forEach((k, v) -> lTempData.put(k, v));
						lTempData.put(lOtherColumnsRowHashCode, object);
						lStoreData.put(lPrimaryColumnHashCode, lTempData);
					} else {
						lStoreData.put(lPrimaryColumnHashCode, OtherColumMap);
					}
					if (increment == limit) {
						System.out.println("inside limit " + increment);
						lParent.getLatch1().countDown();
						try {
							lParent.getLatch2().await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						// limit = limit + 10000;
						increment = 0;
					}
					increment++;
				}
			} catch (SQLException | IOException e1) {

				e1.printStackTrace();
			}

			// if table total row count is less 10000

			if (increment < 10000) {
				lParent.getLatch1().countDown();

				try {
					lParent.getLatch2().await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}

		final Long hash(final Object... objects) {
			StringBuilder builder = new StringBuilder();
			for (Object object : objects) {
				builder.append(object);
			}
			return hash(builder.toString());
		}

		Long hash(final String pData) {
			// Must be prime of course
			long lSeed = 131; // 31 131 1313 13131 131313 etc..
			long lHash = 0;
			char[] chars = pData.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				lHash = (lHash * lSeed) + chars[i];
			}
			return Long.valueOf(Math.abs(lHash));
		}

		private List<Object> getRowObject(ResultSet pResultSet, ResultSetMetaData pResultSetMetaData)
				throws SQLException {
			List<Object> rowValues = new ArrayList<Object>();
			for (int i = 1; i <= pResultSetMetaData.getColumnCount(); i++) {
				rowValues.add(pResultSet.getString(i));
			}
			return rowValues;
		}

		private Object[] getOtherColValues(ResultSet pResultSet, ResultSetMetaData pResultSetMetaData,
				List<String> primaryColNamelist) throws SQLException {
			List<Object> rowValues = new ArrayList<Object>();
			for (int i = 1; i <= pResultSetMetaData.getColumnCount(); i++) {
				if (!primaryColNamelist.contains(pResultSetMetaData.getColumnName(i))) {
					String data = null;
					int type = pResultSetMetaData.getColumnType(i);
					if (pResultSet.getObject(i) != null) {
						if (type == Types.VARCHAR) {
							data = new String(pResultSet.getBytes(i), Charset.forName("UTF-8"));
						} else {
							data = pResultSet.getString(i);
						}
						if (type == Types.DATE || type == Types.TIMESTAMP) {
							data = pResultSet.getTimestamp(i).toString();
						} else if (type == Types.NUMERIC || type == Types.DOUBLE) {
							BigDecimal bigDecimal = new BigDecimal(data);
							DecimalFormat df = new DecimalFormat();
							df.setMaximumFractionDigits(9);
							df.setMinimumFractionDigits(0);
							df.setGroupingUsed(false);
							data = df.format(bigDecimal);
						}
						rowValues.add(data);
					} else {
						rowValues.add(pResultSet.getObject(i));
					}
				}
			}
			return rowValues.toArray(new Object[rowValues.size()]);
		}

		private Object[] getPrimaryColumnValue(final ResultSet pResultSet, List<String> primaryColNamelist,
				ResultSetMetaData resultSetMetaData) throws SQLException, IOException {
			List<Object> rowValues = new ArrayList<Object>();
			String data = null;
			if (primaryColNamelist.size() != 0) {
				for (String primaryColName : primaryColNamelist) {
					data = pResultSet.getString(primaryColName);
					rowValues.add(data);
				}
			}

			return rowValues.toArray(new Object[rowValues.size()]);
		}

	/*	public List<String> getPrimaryColmnName(ResultSet lResultSet, DatabaseMetaData lDatabaseMetaData2,
				String pTableName, String pDatabase, Properties pProps) throws SQLException {
			ResultSet primarykey = null;
			String pk_colName = null;
			String lSchemaName = pProps.getProperty("connection.schema.name");
			Statement stmt = null;
			List<String> listPrimaryColName = new ArrayList<String>();

			try {

				primarykey = lDatabaseMetaData.getPrimaryKeys(null, lSchemaName, pTableName);
				while (primarykey.next()) {

					pk_colName = primarykey.getString("COLUMN_NAME");
					if (pk_colName != null) {
						listPrimaryColName.add(pk_colName);
					}
				}

				

			} catch (Exception e) {

				e.printStackTrace();

			} finally {
				if(primarykey!=null)
				primarykey.close();

			}
			return listPrimaryColName;
		}*/

		
		
		private List<String> getPrimaryColmnName(DatabaseMetaData lDatabaseMetaData, String pTableName,Properties lProps)
				throws SQLException {
			
			String schemaName = lProps.getProperty("connection.schema.name");
			ResultSet primarykey = null;
			String pk_colName = null;
			List<String> listPrimaryColName = new ArrayList<String>();
			
			
			String lPrimaryColName = lProps.getProperty("primary-key."+pTableName);
			
			if(lPrimaryColName!=null)
			{
			
			listPrimaryColName = Arrays.asList(lPrimaryColName.split(","));
			
			}
			
			else
			{
			try {
				primarykey = lDatabaseMetaData.getPrimaryKeys(null, schemaName, pTableName);
				while (primarykey.next()) {
					pk_colName = primarykey.getString("COLUMN_NAME");
					if (pk_colName != null) {
						listPrimaryColName.add(pk_colName);
					}

				}
			} catch (Exception e) {
			} finally {
				primarykey.close();
			}
			
			}
			return listPrimaryColName;
		}
		/*public List<String> getPrimaryColAssignedName(ResultSet lResultSet, 
				 Properties pro) throws SQLException {
			List<String> lColName = new ArrayList<String>();
			String lPrimaryColName = pro.getProperty("primary-key."+tableName);
			List<String> lPrimaryColNameList=null;
			if(lPrimaryColName!=null)
			{
			System.out.println("primary col value from property"+lPrimaryColName);
			lPrimaryColNameList = Arrays.asList(lPrimaryColName.split(","));
			return lPrimaryColNameList;
			}
			
			List<String> lPrimaryColNameListTemp = Arrays.asList(lPrimaryColName.split(","));
			ResultSet primarykey = null;
			String lSchemaName = pro.getProperty("connection.schema.name");
			int count = 0;
			try {
					
				primarykey = lDatabaseMetaData.getColumns(null, lSchemaName, tableName, null);
				while (primarykey.next()) {

					lColName.add(primarykey.getString("COLUMN_NAME"));

				}

				for (String temp : lPrimaryColNameListTemp) {

					if (lColName.contains(temp)) {
						// System.out.println(temp);
						count++;

					}

				}

				if (lPrimaryColNameListTemp.size() == count) {
					return lPrimaryColNameListTemp;
					
				} else {
					return null;
				}
			} finally {
				if (primarykey != null)
					primarykey.close();

			}
			return null;
		}*/

		private ResultSet getResultSet(final Connection pConnection, final String pTableName, Properties pProps)
				throws SQLException {
			List<String> primaryColNameList = null;
			
				primaryColNameList = getPrimaryColmnName( this.lDatabaseMetaData, tableName,  pProps);
			
			 //primaryColNameList = getPrimaryColmnName(lResultSet,lDatabaseMetaData, tableName, name,pProps);
			

			String lQuery = null;

			StringBuilder primaryColName = new StringBuilder();
			for (String key : primaryColNameList) {
				primaryColName.append(key + ", ");

			}
			if (primaryColName.length() != 0) {
				primaryColName.deleteCharAt(primaryColName.length() - 2);
			}
			lQuery = "select * from " + pProps.getProperty("connection.schema.name") + "." + pTableName + " ORDER BY "
					+ primaryColName;
			return executeQuery(pConnection, lQuery);
		}

		private final ResultSet executeQuery(final Connection pConnection, final String pQuery) {
			try {
				return pConnection.createStatement().executeQuery(pQuery);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

	}

	public class WriteOnExcel extends Thread {
		SingleTableCompare lParent;
		String lTableName;
		List allMaps3;
		String lValue = null;

		public WriteOnExcel(String lValue, SingleTableCompare lParent, List allMaps3, String lTableName) {
			this.lTableName = lTableName;
			this.allMaps3 = allMaps3;
			this.lParent = lParent;
			this.lValue = lValue;
		}

		public void run() {
			try {
				List<Integer> Mismatchrow = new ArrayList();
				row = xssSheet.createRow(rowId++);
				row.createCell(0).setCellValue("Table Name: " + lTableName);
				int count = 0;
				for (Object mp1 : allMaps3) {
					List mp2 = (List) mp1;
					for (Object mp : mp2) {
						for (Entry<Long, List<Object>> map1 : ((Map<Long, List<Object>>) mp).entrySet()) {
							List<Object> subMap1 = map1.getValue();
							Cell cell = null;
							cellId = 1;
							row = xssSheet.createRow(rowId++);
							for (Object val1 : subMap1) {
								if (rowId > 60000) {
									rowId = 1;
									cellId = 0;
									cell = null;
									tempRowId = 0;
									xssSheet = xssBook.createSheet("Report" + sheetid);
									sheetid++;
								}
								cell = row.createCell(cellId++);
								if (count == 3) {
									int r = xssSheet.getPhysicalNumberOfRows();
									Mismatchrow.add(r);
								}
								if (lValue.equalsIgnoreCase("Matched")) {
									if (tempRowId != 0 || rowId == 3) {
										if (count == 0) {
											row.createCell(0).setCellValue("MATCHED");
											tempRowId = 0;
										}
									}
								} else {
									if (tempRowId != 0 || rowId == 3) {
										System.out.println("mismatched");
										if (count == 0) {
											row.createCell(0).setCellValue("LEFT ONLY");
											tempRowId = 0;
										} else if (count == 1) {
											row.createCell(0).setCellValue("RIGHT ONLY");
											tempRowId = 0;
										} else if (count == 2) {
											row.createCell(0).setCellValue("MISMATCHED");
											tempRowId = 0;
										}
									}
								}

								if (count == 2) {
									List v = (List) val1;
									for (Object v1 : v) {
										if (v1 == null) {
											cell.setCellValue("null");
										} else {
											cell.setCellValue(v1.toString());
										}
										cell = row.createCell(cellId++);
									}
									row = xssSheet.createRow(rowId++);
									cellId = 1;
								} else {
									if (val1 == null) {
										cell.setCellValue("null");
									} else {
										cell.setCellValue(val1.toString());
									}
								}

							}
						}
						tempRowId = rowId;
					}
					if (!lValue.equals("Matched")) {
						count++;
					}

					for (int i3 = 0; i3 < Mismatchrow.size(); i3 = i3 + 2) {
						compareSheet(xssSheet, Mismatchrow.get(i3), xssBook);
					}
					File file = new File("D:\\ToolComparision_Output00.xls");
					FileOutputStream out = new FileOutputStream(file);
					xssBook.write(out);
					out.close();
					// Write on Excel

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (lValue.equals("Matched")) {
				lParent.getLatch3().countDown();
			}
			if (lValue.equals("MisMatched")) {
				this.lParent.getLatch4().countDown();
			}

		}

	}

	private void compareSheet(HSSFSheet xssSheet, int rowIndex, HSSFWorkbook xssBook) {
		CellStyle style = xssBook.createCellStyle();
		Font font = xssBook.createFont();
		font.setColor(IndexedColors.RED.getIndex());
		style.setFont(font);
		HSSFRow row1 = xssSheet.getRow(rowIndex);
		HSSFRow row2 = xssSheet.getRow(rowIndex + 1);
		for (int cellNo = 1; cellNo < row1.getLastCellNum(); cellNo++) {
			HSSFCell cell1 = row1.getCell(cellNo);
			HSSFCell cell2 = row2.getCell(cellNo);
			if (!(cell1.getStringCellValue().equals(cell2.getStringCellValue()))) {
				cell1.setCellStyle(style);
				cell2.setCellStyle(style);
			}
		}
	}

	ResultSet getResulCount(String pDatabaseName, final Connection pConnection, final String pTableName,
			Properties pProps) throws SQLException {

		String lQuery = null;

		lQuery = "select COUNT(*) AS total from " + pProps.getProperty("connection.schema.name") + "." + pTableName;

		return executeQuery(pConnection, lQuery);

	}

	private List<String> primaryKeyColumnCheck(DatabaseMetaData lDatabaseMetaData, String pTableName,Properties lProps)
			throws SQLException {
		
		String schemaName = lProps.getProperty("connection.schema.name");
		ResultSet primarykey = null;
		String pk_colName = null;
		List<String> listPrimaryColName = new ArrayList<String>();
		
		
		String lPrimaryColName = lProps.getProperty("primary-key."+pTableName);
		
		if(lPrimaryColName!=null)
		{
		
		listPrimaryColName = Arrays.asList(lPrimaryColName.split(","));
		
		}
		
		else
		{
		try {
			primarykey = lDatabaseMetaData.getPrimaryKeys(null, schemaName, pTableName);
			while (primarykey.next()) {
				pk_colName = primarykey.getString("COLUMN_NAME");
				if (pk_colName != null) {
					listPrimaryColName.add(pk_colName);
				}

			}
		} catch (Exception e) {
		} finally {
			primarykey.close();
		}
		
		}
		return listPrimaryColName;
	}

	final ResultSet executeQuery(final Connection pConnection, final String pQuery) {
		try {
			return pConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)
					.executeQuery(pQuery);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * To get connection for database
	 */
	protected Connection getConnection(String pUrl, String pUser, String pPassword, String pDriverClass) {
		try {
			Class.forName(pDriverClass);
			return DriverManager.getConnection(pUrl, pUser, pPassword);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void closeAll(final ResultSet pResultSet) {
		Statement statement = null;
		Connection connection = null;
		try {
			if (pResultSet != null) {
				statement = pResultSet.getStatement();
			}
			if (statement != null) {
				connection = statement.getConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		close(pResultSet);
		close(statement);
		close(connection);
	}

	private void close(final Statement pStatement) {
		if (pStatement == null) {
			return;
		}
		try {
			pStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void close(final Connection pConnection) {
		if (pConnection == null) {
			return;
		}
		try {
			pConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void close(final ResultSet pResultSet) {
		if (pResultSet == null) {
			return;
		}
		try {
			pResultSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
