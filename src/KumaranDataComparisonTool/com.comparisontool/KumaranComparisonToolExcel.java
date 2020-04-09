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

import edu.nps.moves.excel.jdbc.ExcelDBDriver;

public class KumaranComparisonToolExcel {

	HSSFWorkbook xssBook = new HSSFWorkbook();
	HSSFSheet xssSheet = xssBook.createSheet("Report");
	HSSFRow row;
	int rowId = 1;
	int cellId = 0;
	Cell cell = null;
	int tempRowId = 0;
	int sheetid = 1;
	
	

	// WriteOnExcel excel;
	public static void main(String[] args) {
		KumaranComparisonToolExcel lComparisonTest = new KumaranComparisonToolExcel();
		try {
			lComparisonTest.compare();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	int a = 0;

	public void compare() throws Exception {

		ArrayList<String> lDbListofTable1 = new ArrayList<String>();
		ArrayList<String> lDbListofTable2 = new ArrayList<String>();

		Properties lProps = new Properties();
		InputStream lInst = new FileInputStream("src/connectivity1.properties");
		lProps.load(lInst);
		lInst.close();
		
		
		String lSourceTable = lProps.getProperty("source.table");
		String lTargetTable = lProps.getProperty("target.table");
		
		List<String> lSourceTableList = null;
		List<String> lTargetTableList = null;
		if (lSourceTable != null) {
			lSourceTableList = Arrays.asList(lSourceTable.split(","));
		}
		
		if (lTargetTable != null) {
			lTargetTableList = Arrays.asList(lTargetTable.split(","));
		}
		
		
		
		//source connection details
		String fileName = lProps.getProperty("sourceconn.url");		
		Class.forName(lProps.getProperty("sourceconn.driver"));
		Connection lDbConnection1 = DriverManager.getConnection(ExcelDBDriver.URL_PREFIX+fileName);
		DatabaseMetaData databaseMetaData = lDbConnection1.getMetaData();
		ResultSet lRsDb1 = null;
		
		
		
		//target connection details
		Connection lDbConnection2 = getConnection(lProps.getProperty("connection2.url"),
				lProps.getProperty("connection2.username"), lProps.getProperty("connection2.pwd"),
				lProps.getProperty("connection2.driver"));		
		DatabaseMetaData lDbMetaData2 = lDbConnection2.getMetaData();
		ResultSet lRsDb2 = null;
		
	
		for(String sourceTable :lSourceTableList)
		{
		 lRsDb1 = databaseMetaData.getTables(null, null,sourceTable, null);
		 while (lRsDb1.next()) {

				if (lProps.getProperty("metadata.types.1").equalsIgnoreCase(lRsDb1.getString("TABLE_TYPE"))) {
					lDbListofTable1.add(lRsDb1.getString("TABLE_NAME"));
					Collections.sort(lDbListofTable1);
					
				}
			}
		}
		
	
	
		for(String targetTable :lTargetTableList)
		{
		 lRsDb2 = lDbMetaData2.getTables(null, null, targetTable, null);
		 while (lRsDb2.next()) {

				if (lRsDb2.getString(2).equals(lProps.getProperty("connection.schema.name"))) {
					if (lRsDb2.getString(4).equalsIgnoreCase(lProps.getProperty("metadata.types.1"))) {
						lDbListofTable2.add(lRsDb2.getString(3));
						Collections.sort(lDbListofTable2);
					}
				}
			}

		}
		
		
	
				
		
		
		final Object obj = new Object();

		synchronized (obj) {
			try {
				for (int i = 0; i < lDbListofTable1.size(); i++) {
				
					String lTableName = lDbListofTable1.get(i);
				
					System.out.println(lTableName);
				
					int lCount = 0;
					ResultSet lResultSet = getResulCount("First", lDbConnection1, lTableName, lProps);
					if (lResultSet != null) {
						while (lResultSet.next()) {
							lCount = lResultSet.getInt(1);

						}
					}

					int rCount = 0;
					ResultSet lRightResultSet = getResulCount("Second", lDbConnection2, lTableName, lProps);
					if (lRightResultSet != null) {
						while (lRightResultSet.next()) {
							rCount = lRightResultSet.getInt(1);
						}
					}

					System.out.println(lCount);

					System.out.println("right" + rCount);
					int commonCount = lCount + rCount;

					List<String> primaryColNameList = null;

					primaryColNameList = primaryKeyColumnCheck(lDbConnection1.getMetaData(), lTableName, lProps);
					if (primaryColNameList.size() != 0) {
					
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
			} finally {
				closeAll(lRsDb1);
				closeAll(lRsDb2);
			}
		}
	}

	public class SingleTableCompare implements Runnable {
		int innerCount = 0;
		KumaranComparisonToolExcel parent;
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
				String fileName = lProps.getProperty("sourceconn.url");
				String url = ExcelDBDriver.URL_PREFIX + fileName;
				Connection lDbConnection1 = null;
			
				
				Connection lDbConnection2 = getConnection(lProps.getProperty("connection2.url"),
						lProps.getProperty("connection2.username"), lProps.getProperty("connection2.pwd"),
						lProps.getProperty("connection2.driver"));
				try {
					Class.forName(lProps.getProperty("sourceconn.driver"));
					lDbConnection1 = DriverManager.getConnection(url);

				

				} catch (ClassNotFoundException e2) {
					e2.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				DatabaseMetaData lDbMetaData1 = null;
				DatabaseMetaData lDbMetaData2 = null;

		
			
				try {
					lDbMetaData1 = lDbConnection1.getMetaData();
					lDbMetaData2 = lDbConnection2.getMetaData();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

				DataSetReader2 first = new DataSetReader2("First", this, leftQueue, lDbConnection1, lTableName, lProps,
						leftCount, lDbMetaData1);
				DataSetReader2 second = new DataSetReader2("Second", this, rightQueue, lDbConnection2, lTableName,
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

			
			System.out.println("excel"+pLeftMainData);
			
			parent.leftOrphan.forEach((k, v) -> pLeftMainData.put(k, v));
			parent.rightOrphan.forEach((k, v) -> pRightMainData.put(k, v));

			
			while (lLeftMainIterator.hasNext()) {
				Entry<Long, Map<Long, List<Object>>> leftMainEntry = lLeftMainIterator.next();
				if (pRightMainData.containsKey(leftMainEntry.getKey())) {
					leftSubMapData = new HashMap<Long, List<Object>>();
					rightSubMapData = new HashMap<Long, List<Object>>();
					rightSubMapData = pRightMainData.get(leftMainEntry.getKey());
					leftSubMapData = leftMainEntry.getValue();
					List<Object> misMatchedData = new ArrayList<Object>();
					if (leftSubMapData != null) {
						for (Entry<Long, List<Object>> entry1 : leftSubMapData.entrySet()) {
							if (rightSubMapData.containsKey(entry1.getKey())) {
								Map<Long, List<Object>> innerMap = new HashMap<>();
								innerMap.put(entry1.getKey(), entry1.getValue());
								parent.matchDataList.add(innerMap);

							} else {

								misMatchedData.add(leftSubMapData.get(entry1.getKey()));
								if (rightSubMapData != null) {
									for (Entry<Long, List<Object>> entry : rightSubMapData.entrySet()) {
										if (!leftSubMapData.containsKey(entry.getKey())) {
											misMatchedData.add(rightSubMapData.get(entry.getKey()));
											Map<Long, List<Object>> tempMap = new HashMap<Long, List<Object>>();
											tempMap.put(entry.getKey(), misMatchedData);
											parent.misMatchDataList.add(tempMap);
											System.out.println("mismatched data" + tempMap);

										}

									}

								}
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
				lResultSet = getResultSet(name,connection, this.tableName, pro);
			} catch (SQLException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			Map<Long, List<Object>> OtherColumMap = null;
			List<String> primaryColNameList = null;

			try {

				primaryColNameList = getPrimaryColmnName(name,lDatabaseMetaData, tableName, pro);

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

						increment = 0;
					}
					increment++;
				}
				System.out.println(lStoreData);
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
			int data = 0;
			if (primaryColNamelist.size() != 0) {
				for (String primaryColName : primaryColNamelist) {
					data = pResultSet.getInt(primaryColName);
					rowValues.add(data);
		
				}
			}

			return rowValues.toArray(new Object[rowValues.size()]);
		}

		private List<String> getPrimaryColmnName(String name,DatabaseMetaData lDatabaseMetaData, String pTableName,
				Properties lProps) throws SQLException {

			String schemaName = lProps.getProperty("connection.schema.name");
			ResultSet primarykey = null;
			String pk_colName = null;
			List<String> listPrimaryColName = new ArrayList<String>();

			

			
			if(name.equalsIgnoreCase("First"))
			{		
				String lPrimaryColName = lProps.getProperty("source.primary-key." + pTableName);
			if (lPrimaryColName != null) {
				listPrimaryColName = Arrays.asList(lPrimaryColName.split(","));
			}
			}
			
			if(name.equalsIgnoreCase("Second"))
			{
				String lPrimaryColName = lProps.getProperty("target.primary-key." + pTableName);
				if (lPrimaryColName != null) {
					listPrimaryColName = Arrays.asList(lPrimaryColName.split(","));
				}
			else {
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
			}
			return listPrimaryColName;
		}

		private ResultSet getResultSet(String name,final Connection pConnection, final String pTableName, Properties pProps)
				throws SQLException {
			List<String> primaryColNameList = null;
			primaryColNameList = getPrimaryColmnName(name,this.lDatabaseMetaData, tableName, pProps);
			String lQuery = null;
			StringBuilder primaryColName = new StringBuilder();
			for (String key : primaryColNameList) {
				primaryColName.append(key + ", ");

			}
			if (primaryColName.length() != 0) {
				primaryColName.deleteCharAt(primaryColName.length() - 2);
			}
			
			if(name.equalsIgnoreCase("First"))
			{
			lQuery = "select * from "  + pTableName + " ORDER BY " + primaryColName;
			}
			else
			{
				lQuery = "select * from "  + pProps.getProperty("connection.schema.name")+"."+pTableName + " ORDER BY " + primaryColName;
			}
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
								if (count == 2) {
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

		if(pDatabaseName.equalsIgnoreCase("First"))
		{
			lQuery = "select COUNT(*) AS total from " + pTableName;
		}
		else
			lQuery = "select COUNT(*) AS total from "+ pProps.getProperty("connection.schema.name") + "."  + pTableName;
		
		return executeQuery(pConnection, lQuery);

	}

	private List<String> primaryKeyColumnCheck(DatabaseMetaData lDatabaseMetaData, String pTableName, Properties lProps)
			throws SQLException {

		String schemaName = lProps.getProperty("connection.schema.name");
		ResultSet primarykey = null;
		String pk_colName = null;
		List<String> listPrimaryColName = new ArrayList<String>();

		String lPrimaryColName = lProps.getProperty("source.primary-key." + pTableName);

		if (lPrimaryColName != null) {

			listPrimaryColName = Arrays.asList(lPrimaryColName.split(","));

		}

		else {
			try {
				primarykey = lDatabaseMetaData.getPrimaryKeys(null, schemaName, pTableName);
				while (primarykey.next()) {
					pk_colName = primarykey.getString("COLUMN_NAME");
					if (pk_colName != null) {
						listPrimaryColName.add(pk_colName);
					}

				}

				System.out.println("primarykey list" + listPrimaryColName);
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
