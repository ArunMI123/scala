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
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class KumaranComparisonTool {
	HSSFWorkbook xssBook = new HSSFWorkbook();
	HSSFSheet xssSheet = xssBook.createSheet("ExampleSheet");
	HSSFRow row;
	int rowId = 1;				
	int cellId = 0;
	Cell cell = null;
	int tempRowId = 0;
	int sheetid = 1;
	public static HashMap<String, String> lBlobTable;
	public static HashMap<String, String> lMVTable;

	public static void main(String[] args) {
		KumaranComparisonTool lComparisonTest = new KumaranComparisonTool();

		lBlobTable = new HashMap<>();
		lMVTable = new HashMap<>();
		try {

			lBlobTable.put("RRE_JMSWLSTORE", "RRE_JMSWLSTORE");
			lBlobTable.put("WL_JMS_RS__RRE1DM1N1_WLSTORE", "WL_JMS_RS__RRE1DM1N1_WLSTORE");
			lBlobTable.put("WL_JMS_RS__RRE1DM2N1_WLSTORE", "WL_JMS_RS__RRE1DM2N1_WLSTORE");
			lBlobTable.put("WL_JMS_RS__RRE1DM3N1_WLSTORE", "WL_JMS_RS__RRE1DM3N1_WLSTORE");
			lBlobTable.put("WL_JMS_RS__RRE1M1N1_WLSTORE", "WL_JMS_RS__RRE1M1N1_WLSTORE");
			lBlobTable.put("WL_JMS_RS__RRE1M2N1_WLSTORE", "WL_JMS_RS__RRE1M2N1_WLSTORE");
			lBlobTable.put("WL_JMS_RS__RRE1M3N1_WLSTORE", "WL_JMS_RS__RRE1M3N1_WLSTORE");
			lBlobTable.put("WL_JMS_RS__RRE2DM1N1_WLSTORE", "WL_JMS_RS__RRE2DM1N1_WLSTORE");
			lBlobTable.put("WL_JMS_RS__RRE2DM2N1_WLSTORE", "WL_JMS_RS__RRE2DM2N1_WLSTORE");
			lBlobTable.put("WL_JMS_RS__RRE2M1N1_WLSTORE", "WL_JMS_RS__RRE2M1N1_WLSTORE");
			lBlobTable.put("WL_JMS_RS__RRE2M2N1_WLSTORE", "WL_JMS_RS__RRE2M2N1_WLSTORE");
			lBlobTable.put("WL_JMS_RS__RRE2M3N1_WLSTORE", "WL_JMS_RS__RRE2M3N1_WLSTORE");
			lBlobTable.put("WL_JMS_RS__RRE4DM1N1_WLSTORE", "WL_JMS_RS__RRE4DM1N1_WLSTORE");
			lBlobTable.put("WL_JMS_RS__RRE4DM2N1_WLSTORE", "WL_JMS_RS__RRE4DM2N1_WLSTORE");
			lBlobTable.put("WL_JMS_RS__RRE4DM3N1_WLSTORE", "WL_JMS_RS__RRE4DM3N1_WLSTORE");
			lBlobTable.put("WL_JMS_RS__RRE5DM1N1_WLSTORE", "WL_JMS_RS__RRE5DM1N1_WLSTORE");
			lBlobTable.put("WL_JMS_RS__RRE5DM2N1_WLSTORE", "WL_JMS_RS__RRE5DM2N1_WLSTORE");
			lBlobTable.put("WL_JMS_RS__RRE5DM3N1_WLSTORE", "WL_JMS_RS__RRE5DM3N1_WLSTORE");
			lBlobTable.put("CEAS_JMS_DHAN2189WLSTORE", "CEAS_JMS_DHAN2189WLSTORE");
			lBlobTable.put("WL_JMS_RS_N1_WLSTORE", "WL_JMS_RS_N1_WLSTORE");
			lBlobTable.put("WL_JMS_RS_N1A_WLSTORE", "WL_JMS_RS_N1A_WLSTORE");
			lBlobTable.put("WL_JMS_RS_N2_WLSTORE", "WL_JMS_RS_N2_WLSTORE");
			lBlobTable.put("WL_JMS_RS_N2A_WLSTORE", "WL_JMS_RS_N2A_WLSTORE");
			lBlobTable.put("WL_JMS_RS_N3A_WLSTORE", "WL_JMS_RS_N3A_WLSTORE");

			// to add mv table

			lMVTable.put("RRE_MV_AVIP_ACCOUNTS", "RRE_MV_AVIP_ACCOUNTS");
			lMVTable.put("RRE_MV_AVIP_GLOSSARY", "RRE_MV_AVIP_GLOSSARY");
			lComparisonTest.compare();
			//lComparisonTest.writeOnExcelTable();
			System.out.println("8888888888888888888888888888888888888888888888888888888888888888888888888888888888888");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	CountDownLatch latch1;
	CountDownLatch latch2;
	// List<String> leftQueue = new ArrayList<String>();
	// List<String> rightQueue = new ArrayList<String>();

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

	int a = 0;
	
	public void compare() throws Exception {

		Properties lProps = new Properties();
		InputStream lInst = new FileInputStream("src/connectivity.properties");
		lProps.load(lInst);
		lInst.close();
		
		ArrayList<String> lOracleListofTable = new ArrayList<String>();
		ArrayList<String> lSqlServerlistofTable = new ArrayList<String>();
		ResultSet lOracleResultSet = null;
		ResultSet lSqlServerResultSet = null;
		// I am using Oracle here, but you can use any database
		Connection lOracleconnection = getConnection(lProps.getProperty("jdbc.url"),
				lProps.getProperty("jdbc.username"), lProps.getProperty("jdbc.pwd"), lProps.getProperty("jdbc.driver"));

		Connection lSqlServerconnection_sql = getConnection(lProps.getProperty("jdbc.url.sql"),
				lProps.getProperty("jdbc.username.sql"), lProps.getProperty("jdbc.pwd.sql"),
				lProps.getProperty("jdbc.driver.sql"));
		DatabaseMetaData lOracleMetaData = lOracleconnection.getMetaData();
		ResultSet lRsOracle = lOracleMetaData.getTables(null, null, "%", null);

		DatabaseMetaData lSqlServermetaData = lSqlServerconnection_sql.getMetaData();
		ResultSet lSqlServerRs = lSqlServermetaData.getTables(null, null, "%", null);

		while (lRsOracle.next()) {
			if (lRsOracle.getString(2).equals(lProps.getProperty("jdbc.schema.name"))) {

				if (lRsOracle.getString(4).equalsIgnoreCase(lProps.getProperty("metadata.types.1"))) {
					if (!lMVTable.containsKey(lRsOracle.getString(3))) {
						lOracleListofTable.add(lRsOracle.getString(3));
					}
					Collections.sort(lOracleListofTable);
				}
			}
		}

		while (lSqlServerRs.next()) {

			if (lSqlServerRs.getString(2).equals(lProps.getProperty("jdbc.schema.name"))) {
				if (lSqlServerRs.getString(4).equalsIgnoreCase(lProps.getProperty("metadata.types.1"))) {
					lSqlServerlistofTable.add(lSqlServerRs.getString(3));
					Collections.sort(lSqlServerlistofTable);
				}
			}
		}

		final Object obj = new Object();

		synchronized (obj) {
			try {
				for (int i = 0, j = 0; i < lOracleListofTable.size() && j < lSqlServerlistofTable.size(); i++, j++) {

					String lTableName = lOracleListofTable.get(i);
				//if ("LIST_DETAIL".equals(lTableName)) {
						int lCount = 0;
						ResultSet lResultSet = getResulCount(lOracleconnection, lTableName, lProps);
						if (lResultSet != null) {
							while (lResultSet.next()) {
								lCount = lResultSet.getInt(1);
							}
						}

						int rCount = 0;
						ResultSet lRightResultSet = getResulCount(lSqlServerconnection_sql, lTableName, lProps);
						if (lRightResultSet != null) {
							while (lRightResultSet.next()) {
								rCount = lRightResultSet.getInt(1);
							}
						}
						System.out.println(lCount);
						int commonCount = lCount + rCount;

						DataSetReader first = new DataSetReader("Oracle", this, leftQueue, lOracleconnection,
								lTableName, lProps, leftCount, lOracleMetaData, lPrimaryKeyColumnIndicator);
						DataSetReader second = new DataSetReader("SqlServer", this, rightQueue,
								lSqlServerconnection_sql, lTableName, lProps, rightCount, lSqlServermetaData,
								lPrimaryKeyColumnIndicator);

						class MainThread implements Runnable {

							int innerCount = commonCount;
							KumaranComparisonTool parent;
							Object obj;

							public MainThread(KumaranComparisonTool kumaranComparisonTool, Object obj) {
								this.parent = kumaranComparisonTool;
								this.obj = obj;
								latch1 = new CountDownLatch(2);
								latch2 = new CountDownLatch(2);
							}

							@Override
							public void run() {

								synchronized (this.obj) {
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

										compareDataSet(leftQueue, rightQueue, this.parent);
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
													+ this.parent.matchDataList.size() + " mismatched:"
													+ this.parent.misMatchDataList.size() + " leftOrphen:"
													+ this.parent.leftOrphan.size() + " RightOrphen:"
													+ this.parent.rightOrphan.size());
											List allMaps3 = new ArrayList();
											
											Collection<Map<Long, List<Object>>> values = this.parent.leftOrphan.values(); 
										
											List<Map<Long, List<Object>>> leftOrphanList = new ArrayList<Map<Long, List<Object>>>(values);

											Collection<Map<Long, List<Object>>> right = this.parent.rightOrphan.values(); 
											
											List<Map<Long, List<Object>>> rightOrphanList = new ArrayList<Map<Long, List<Object>>>(right);
											
											allMaps3.add(this.parent.matchDataList);
											allMaps3.add(leftOrphanList);
											
										//	System.out.println("LLLLLL"+this.parent.matchDataList);
											
											
											allMaps3.add(rightOrphanList);
											allMaps3.add(this.parent.misMatchDataList);
											
											
											
											System.out.println("LLLLLL"+this.parent.misMatchDataList);
											
											
											try {
												writeOnExcel(allMaps3);
											} catch (FileNotFoundException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
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

							private void writeOnExcel(List allMaps3) throws IOException {
								
								List<Integer> Mismatchrow = new ArrayList();
								row = xssSheet.createRow(rowId++);
								row.createCell(0).setCellValue("Table Name: " + lTableName);

								int count = 0;
								for (Object mp1 : allMaps3) {
									
									List mp2 = (List) mp1;
									
									for (Object mp : mp2) {

										for (Entry<Long, List<Object>> map1 : ((Map<Long, List<Object>>) mp)
												.entrySet()) {
											List<Object> subMap1 = map1.getValue();
											Cell cell = null;
											String Tname = "Table 1";
											cellId = 1;
										
											row = xssSheet.createRow(rowId++);
											for (Object val1 : subMap1) {

												if(rowId>60000)
												{
													
													 rowId = 1;				
													 cellId = 0;
													 cell = null;
													 tempRowId = 0;
													 xssSheet = xssBook.createSheet("ExampleSheet"+sheetid);
													 sheetid++;
												}
												cell = row.createCell(cellId++);
												if (count == 3) {

													int r = xssSheet.getPhysicalNumberOfRows();
													Mismatchrow.add(r);

												}
												if (tempRowId != 0 || rowId == 3) {
													if (count == 0) {
														//	System.out.println(tempRowId);
														row.createCell(0).setCellValue("MATCHED");

														tempRowId = 0;
													} else if (count == 1) {
														row.createCell(0).setCellValue("LEFT ONLY");

														tempRowId = 0;
													} else if (count == 2) {
														row.createCell(0).setCellValue("RIGHT ONLY");
														tempRowId = 0;
													} else if (count == 3) {
														row.createCell(0).setCellValue("MISMATCHED");
														tempRowId = 0;

													}
												}
												if (count == 3) {												
													List v = (List) val1;
													for (Object v1 : v) {														
														if (v1 == null) {
															cell.setCellValue("null");}
															else
															{
														cell.setCellValue(v1.toString());}														
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
											
											/* } */
										
										}
									
										tempRowId = rowId;
									
									}									
									count++;
									for (int i3 = 0; i3 < Mismatchrow.size(); i3 = i3 + 2) {
										compareSheet(xssSheet, Mismatchrow.get(i3), xssBook);
									}
									FileOutputStream out = new FileOutputStream(
											new File("D:\\ToolComparision_Output00.xls"));
									xssBook.write(out);
									out.close();
									// Write on Excel
									// Write on Excel
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

						}
						List<String> primaryColNameList = null; 
						primaryColNameList= primaryKeyColumnCheck(lOracleconnection.getMetaData(),lTableName);
						if(primaryColNameList.size() != 0){
						MainThread r = new MainThread(new KumaranComparisonTool(), obj);

						Thread th = new Thread(r);
						th.start();

						try {
							obj.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				}

			 finally {
				closeAll(lSqlServerResultSet);
				closeAll(lOracleResultSet);
			}
		}

	}

	ResultSet getResulCount(final Connection pConnection, final String pTableName, Properties pProps) {
		String lQuery = null;
		lQuery = "select COUNT(*) AS total from " + pProps.getProperty("jdbc.schema.name") + "." + pTableName
				+ " ORDER BY 1 asc";
		return executeQuery(pConnection, lQuery);
	}
	
	private List<String> primaryKeyColumnCheck( DatabaseMetaData lDatabaseMetaData,
			String pTableName) throws SQLException {
		ResultSet primarykey = null;
		String pk_colName = null;
		List<String> listPrimaryColName = new ArrayList<String>();
		try {
			primarykey = lDatabaseMetaData.getPrimaryKeys(null, "RRE_META", pTableName);
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

	// left is oracle db, right is sql server data.

	static void compareDataSet(Map<Long, Map<Long, List<Object>>> pLeftMainData,
			Map<Long, Map<Long, List<Object>>> pRightMainData, KumaranComparisonTool parent) {

		Iterator<Entry<Long, Map<Long, List<Object>>>> lLeftMainIterator = pLeftMainData.entrySet().iterator();
		Map<Long, List<Object>> leftSubMapData = null;
		Map<Long, List<Object>> rightSubMapData = null;
		Map<Long, List<Object>> lTempaoryData = null;

		parent.leftDataMisMatch.forEach((k, v) -> pLeftMainData.put(k, v));

		parent.rightDataMisMatch.forEach((k, v) -> pRightMainData.put(k, v));

		parent.leftOrphan.forEach((k, v) -> pLeftMainData.put(k, v));

		parent.rightOrphan.forEach((k, v) -> pRightMainData.put(k, v));

		
		System.out.println(pLeftMainData);
		while (lLeftMainIterator.hasNext()) {
			Entry<Long, Map<Long, List<Object>>> leftMainEntry = lLeftMainIterator.next();

			if (pRightMainData.containsKey(leftMainEntry.getKey())) {

				leftSubMapData = new ConcurrentHashMap<Long, List<Object>>();
				rightSubMapData = new ConcurrentHashMap<Long, List<Object>>();
				List<Long> lDeleteLongkey = new ArrayList<Long>();
				rightSubMapData = pRightMainData.get(leftMainEntry.getKey());
				leftSubMapData = leftMainEntry.getValue();

				if (leftSubMapData != null) {

					for (Entry<Long, List<Object>> entry1 : leftSubMapData.entrySet()) {
						if (rightSubMapData.containsKey(entry1.getKey())) {
							parent.matchedData.put(leftMainEntry.getKey(), leftSubMapData);
							parent.matchDataList.add(leftSubMapData);
							// rightSubMapData.remove(entry1.getKey());
							// lDeleteLongkey.add(entry1.getKey());
						} else {
							// add misMatchDataList data before we can get both
							// left and right data for highlight in excel
							lTempaoryData = pRightMainData.get(leftMainEntry.getKey());
							int i = 0;
							List<Object> tempList = new ArrayList<Object>();
							
						//	List<Object> tempList = null;
							tempList.add(leftSubMapData.get(entry1.getKey()));
							for (Map.Entry<Long, List<Object>> entry : lTempaoryData.entrySet()) {
								if (i == 0) {
									tempList.add(entry.getValue());
								}
								++i;
							}

							Map<Long, List<Object>> tempMap = new HashMap<Long, List<Object>>();
							tempMap.put(entry1.getKey(), tempList);
							parent.misMatchDataList.add(tempMap);
							// tempMap.clear();

						}
					}

				}

				// for(Long key:lDeleteLongkey){
				// leftSubMapData.remove(key);
				// }
				// lDeleteLongkey.clear();

				pRightMainData.remove(leftMainEntry.getKey());
				pLeftMainData.remove(leftMainEntry.getKey());

			}

		}

		pLeftMainData.forEach((k, v) -> {
			if (!parent.leftDataMisMatch.containsKey(k)) {
				parent.leftOrphan.put(k, v);
			}
		});

		pRightMainData.forEach((k, v) -> {
			if (!parent.rightDataMisMatch.containsKey(k)) {
				parent.rightOrphan.put(k, v);
			}
		});

	}

	/*
	 * To get connection for database
	 */

	private Connection getConnection(String pUrl, String pUser, String pPassword, String pDriverClass) {
		try {
			Class.forName(pDriverClass);

			/* DriverManager.registerDriver(driverClass); */
			return DriverManager.getConnection(pUrl, pUser, pPassword);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void closeAll(final ResultSet pResultSet) {
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

	public CountDownLatch getLatch1() {
		return this.latch1;
	}

	public CountDownLatch getLatch2() {
		return this.latch2;
	}

}

class DataSetReader extends Thread {

	int limit = 10000;
	List<Integer> count;
	List<Integer> rightCount;
	Map<Long, Map<Long, List<Object>>> lStoreData;
	KumaranComparisonTool lParent;
	Connection connection;
	String tableName;
	Properties pro;
	String name;
	ResultSet lResultSet = null;
	DatabaseMetaData lDatabaseMetaData;
	boolean lPrimaryKeyColumnIndicator;

	public DataSetReader(String pDatabaseName, KumaranComparisonTool pParent,
			Map<Long, Map<Long, List<Object>>> pStoreData, final Connection pConnection, final String pTableName,
			Properties pProps, List<Integer> Prowcount, DatabaseMetaData pMetaData,
			Boolean pPrimaryKeyColumnIndicator) {
		super(pDatabaseName);
		this.lParent = pParent;
		this.lStoreData = pStoreData;
		this.connection = pConnection;
		this.tableName = pTableName;
		this.pro = pProps;
		this.name = pDatabaseName;
		this.count = Prowcount;
		this.lDatabaseMetaData = pMetaData;
		this.lPrimaryKeyColumnIndicator = pPrimaryKeyColumnIndicator;

	}

	@Override
	public void run() {
		int increment = 1;
		lResultSet = getResultSet(connection, tableName, pro);
		Map<Long, List<Object>> OtherColumMap = null;

		List<String> primaryColNameList = null;
		try {
			primaryColNameList = getPrimaryColmnName(lResultSet, lDatabaseMetaData, tableName);
			Collections.sort(primaryColNameList);
		} catch (SQLException e2) {
			e2.printStackTrace();
		}

		// if(primaryColNameList.size() == 0){
		// this.lPrimaryKeyColumnIndicator = true;
		// lParent.getLatch1().countDown();
		//
		// try {
		// lParent.getLatch2().await();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		//

		try {
			while (lResultSet.next()) {
				OtherColumMap = new HashMap<Long, List<Object>>();
				// System.out.println( Thread.currentThread().getName() + "-" +
				// increment);

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
					limit = limit + 10000;
				}
				increment++;
			}
		} catch (SQLException | IOException e1) {

			e1.printStackTrace();
		}

		// System.out.println("End finished");
		lParent.getLatch1().countDown();

		try {
			lParent.getLatch2().await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// System.out.println("before final");
		// lParent.getLatch1().countDown();

	}

	static final Long hash(final Object... objects) {

		StringBuilder builder = new StringBuilder();
		for (Object object : objects) {
			builder.append(object);
		}
		return hash(builder.toString());
	}

	static Long hash(final String pData) {
		// Must be prime of course
		long lSeed = 131; // 31 131 1313 13131 131313 etc..
		long lHash = 0;
		char[] chars = pData.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			lHash = (lHash * lSeed) + chars[i];
		}
		return Long.valueOf(Math.abs(lHash));
	}

	private List<Object> getRowObject(ResultSet pResultSet, ResultSetMetaData pResultSetMetaData) throws SQLException {
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
						data =new String(pResultSet.getBytes(i), Charset.forName("UTF-8"));
					}else{
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
					} /*else if (type == Types.VARCHAR) {
						// data = removeJunkValues(data);
					}*/

					rowValues.add(data);
				} else {
					rowValues.add(pResultSet.getObject(i));
				}

			}
		}

		return rowValues.toArray(new Object[rowValues.size()]);
	}

	private static Object[] getPrimaryColumnValue(final ResultSet pResultSet, List<String> primaryColNamelist,
			ResultSetMetaData resultSetMetaData) throws SQLException, IOException {
		List<Object> rowValues = new ArrayList<Object>();
		String data = null;

		if (primaryColNamelist.size() != 0) {
			for (String primaryColName : primaryColNamelist) {
				data = pResultSet.getString(primaryColName);
				rowValues.add(data);
			}
		} else {
			// data = pResultSet.getString(1);
			// rowValues.add(data);
			for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {

				int type = resultSetMetaData.getColumnType(i);

				if (pResultSet.getObject(i) != null) {

					if (type == Types.VARCHAR) {
						
						data =new String(pResultSet.getBytes(i), Charset.forName("UTF-8"));
					}else{

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
					} else if (type == Types.VARCHAR) {
						// data = removeJunkValues(data);
					}

					rowValues.add(data);
				} else {
					rowValues.add(pResultSet.getObject(i));
				}

				// }
			}
		}

		return rowValues.toArray(new Object[rowValues.size()]);

	}

	private List<String> getPrimaryColmnName(ResultSet lResultSet2, DatabaseMetaData lDatabaseMetaData2,
			String pTableName) throws SQLException {
		ResultSet primarykey = null;
		String pk_colName = null;
		List<String> listPrimaryColName = new ArrayList<String>();
		try {
			primarykey = lDatabaseMetaData.getPrimaryKeys(null, "RRE_META", pTableName);
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
		return listPrimaryColName;
	}

	private ResultSet getResultSet(final Connection pConnection, final String pTableName, Properties pProps) {
		String lQuery = null;
		lQuery = "select * from " + pProps.getProperty("jdbc.schema.name") + "." + pTableName + " ORDER BY 1 asc";
		return executeQuery(pConnection, lQuery);
	}

	private final ResultSet executeQuery(final Connection pConnection, final String pQuery) {
		try {

			return pConnection.createStatement()
					.executeQuery(pQuery);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
