package com.kumaran.tac.agent.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;


@Component
public class ApachePOIExcelRead {

	private static final String FILE_NAME = "D:/copyBook/Book1.xlsx";

	public Map<Integer, HashMap> excelReader() {
		
		List<String> headerValue = new ArrayList<String>();
		int mapItr = -1;
		Map<Integer, HashMap> mapValue = new HashMap<Integer, HashMap>();

		try {
			FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet worksheet = workbook.getSheetAt(0);
			XSSFRow row;
			XSSFCell cell;
			Iterator rows = worksheet.rowIterator();

			while (rows.hasNext()) {
				HashMap<String, String> hm = new HashMap<String, String>();
				int xxy = 0;
				row = (XSSFRow) rows.next();
				Iterator cells = row.cellIterator();
				while (cells.hasNext()) {
					cell = (XSSFCell) cells.next();
					if (cell.getRowIndex() == 0) {
						if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
							headerValue.add(cell.getStringCellValue());
						} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
							headerValue.add(cell.getNumericCellValue()+"");
						}
					}
					if (cell.getRowIndex() != 0) {
						if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
							hm.put(headerValue.get(xxy), cell.getStringCellValue());
						} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
							hm.put(headerValue.get(xxy), (cell.getNumericCellValue()+""));
						}
					}
					xxy++;
				}
				if (mapItr != -1) {
					mapValue.put(mapItr, hm);
				}
				mapItr++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mapValue;
	}
}

