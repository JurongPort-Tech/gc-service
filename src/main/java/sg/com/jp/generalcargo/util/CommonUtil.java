package sg.com.jp.generalcargo.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import sg.com.jp.generalcargo.domain.Comments;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.Email;
import sg.com.jp.generalcargo.domain.EmailResponse;
import sg.com.jp.generalcargo.domain.ManifestUploadConfig;
import sg.com.jp.generalcargo.domain.Sms;

/*
 * Revision History
 * ------------------------------------------------------------------------------------------------------
 * Author			Description												Version			Date
 * ------------------------------------------------------------------------------------------------------
 * MC Consulting	First Version											1.0				27-Mar-2019
 */
@Component
public class CommonUtil {
	public static String DATETIME_FORMAT = "dd/MM/yyyy  HH:mm:ss";
	private static final Log log = LogFactory.getLog(CommonUtil.class);
		
	private static String SMSGATEWAYADDRESS;
	
	@Value("${jp.common.notificationProperties.smsEndpoint}")
    public void setSMSGatewayAddress(String SMSGatewayAddress) {
		SMSGATEWAYADDRESS = SMSGatewayAddress;
    }

	public static String deNull(String value) {
		return value == null ? "" : value.trim();
	}

	public static String nullToZero(String value) {
		return value == null ? "0" : value.trim();
	}

	public static Criteria getCriteria(HttpServletRequest request) {
		Criteria criteria = new Criteria();
		for (Enumeration<String> v = request.getParameterNames(); v.hasMoreElements();) {
			String key = (String) v.nextElement();
			String value = deNull(request.getParameter(key));
			if (!value.isEmpty()) {
				if (key.equals("start")) {
					Integer start = getInteger(value);
					if (start != null) {
						criteria.setStart(start.intValue());
					}
				} else if (key.equals("limit")) {
					Integer limit = getInteger(value);
					if (limit != null) {
						criteria.setLimit(limit.intValue());
					}
				} else if (key.equals("sort")) {
					criteria.setSort(value);
				} else if (key.equals("dir")) {
					criteria.setDir(value);
				} else {
					criteria.addPredicate(key, value);
				}
			}
		}
		return criteria;
	}

	public static LocalDate getDate(String value, String format) {
		try {
			return LocalDate.parse(value, DateTimeFormatter.ofPattern(format));
		} catch (Exception e) {

		}
		return null;
	}

	public static LocalDateTime getDateTime(String value, String format) {
		try {
			return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format));
		} catch (Exception e) {

		}
		return null;
	}

	public static Integer getInteger(String value) {
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {

			}
		}
		return null;
	}

	public static String getPaginatedSql(String sql, int start, int limit) {
		sql = "SELECT * FROM (SELECT ROW_.*, ROWNUM ROWNUM_ FROM (" + sql + ") ROW_ WHERE ROWNUM <= " + (start + limit)
				+ ") WHERE ROWNUM_ > " + start;
		return sql;
	}

	public static String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		log.error("Exception getStackTrace : ", e);
		return sw.toString();
	}

	public static boolean isValidSortDirection(String dir) {
		return dir == null ? false : (dir.equalsIgnoreCase("asc") || dir.equalsIgnoreCase("desc"));
	}

	public static String parseDBDateToStr(Timestamp timestamp) {
		if (timestamp == null)
			return null;
		else {
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIME_FORMAT);
			Date date = new Date(timestamp.getTime());
			return dateFormat.format(date);
		}
	}

	public static Map<String, String> headerCheck(Map<String, String> tableData, XSSFSheet worksheet) {
		Map<String, String> headerCheckResult = new LinkedHashMap<>();
		tableData.forEach((k, v) -> {
			CellReference cr = new CellReference(k);
			Row row = worksheet.getRow(cr.getRow());
			Cell cell = row.getCell(cr.getCol());

			String cellData = null;
			switch (cell.getCellType()) {
			case STRING:
				cellData = cell.getStringCellValue();
				break;
			case NUMERIC:
				DataFormatter fmt = new DataFormatter();
				cellData = fmt.formatCellValue(cell);
				break;
			case BOOLEAN:
				cellData = String.valueOf(cell.getBooleanCellValue());
				break;
			case BLANK:
				cellData = "";
				break;
			default:
				break;
			}
			if (cellData.equals(v)) {
			} else {
				headerCheckResult.put(k, v);
			}
		});
		return headerCheckResult;
	}

	public static int getRowCount1(XSSFSheet sheet, int startRow, int billNoIndex) {
		int rowCount = 0;
		XSSFRow row;
		int j = 0;
		for (int i = startRow; i <= sheet.getPhysicalNumberOfRows(); i++) {
			row = sheet.getRow(i);
			Cell cell = row.getCell(billNoIndex);
			log.info(i + "" + cell.getStringCellValue());
			if (isRowEmpty(row, billNoIndex)) {
				j++;
				if (j == 2) {
					rowCount = i - j;
					break;
				}
			}
		}
		return rowCount;
	}

	public static boolean isRowEmpty(Row row, int primaryKeyColumn) {
		Cell cell = row.getCell(primaryKeyColumn);
		if (cell.getCellType() == CellType.BLANK) {
			return true;
		}
		return false;

	}

	public static int getRowCount(XSSFSheet sheet, int startRow, int bl_nbr_index, String type) {
		int rowCount = 0;
		try {
			int j = 0;
			if (type == ConstantUtil.typeCd_Manifest || type == ConstantUtil.typeCd_BookingRef || type == ConstantUtil.typeCd_CustomDetailsExcel) {
				log.info("sheet.getPhysicalNumberOfRows() :" + sheet.getPhysicalNumberOfRows());
				for (int i = startRow; i <= sheet.getPhysicalNumberOfRows(); i++) {
					Row row = sheet.getRow(i);
					if (row != null) {
						Cell cell = row.getCell(bl_nbr_index);// 1 for manifest, 0 for packaging
						Cell cellAction = row.getCell(0);// 1 for manifest, 0 for packaging
						if (cell.getCellType() == CellType.BLANK && cellAction.getCellType() == CellType.BLANK) {
							j++;
							if (j == 2) {
								rowCount = i - j;
								break;
							}
						} else {
							j = 0;
						}
					} else {
						if (j == 0) {
							rowCount = i - 1;
							break;
						} else {
							// already one empty row exist
							rowCount = i - 2;
							break;
						}
					}
				}
			} else {

				for (int i = startRow; i <= sheet.getPhysicalNumberOfRows(); i++) {
					Row row = sheet.getRow(i);
					if (row != null) {
						Cell cell = row.getCell(bl_nbr_index);// 1 for manifest, 0 for packaging
						if (cell.getCellType() == CellType.BLANK) {
							j++;
							if (j == 2) {
								rowCount = i - j;
								break;
							}
						} else {
							j = 0;
						}
					} else {
						if (j == 0) {
							rowCount = i - 1;
							break;
						} else {
							// already one empty row exist
							rowCount = i - 2;
							break;
						}
					}
				}
			}
			if (rowCount == 0 && sheet.getPhysicalNumberOfRows() > 0) {
				log.info("rowcount is zero so set physical count");
				rowCount = sheet.getPhysicalNumberOfRows();
			}
			if (rowCount < 0)
				rowCount = 0;
		} catch (Exception ex) {

		} finally {
			log.info("row count:" + rowCount);
		}
		return rowCount;
	}

//	public static String getCellValues(String columnIndex, XSSFSheet worksheet) {
//		CellReference cr = new CellReference(columnIndex);
//		Row row = worksheet.getRow(cr.getRow());
//		Cell cell = row.getCell(cr.getCol());
//		String cellData = null;;
//		switch (cell.getCellType()) {
//		case STRING:
//			cellData  = cell.getStringCellValue();
//			break;
//		case NUMERIC:
//			cellData = String.valueOf(cell.getNumericCellValue());
//			break;
//		case BOOLEAN:
//			cellData = String.valueOf(cell.getBooleanCellValue());
//			break;
//		case BLANK:
//			cellData = "";
//			break;
//		default:
//			break;
//		}
//		return cellData;
//	}

	public static int getColumnNumber(String column_name) {
//		String str = column_name;
//		String[] part = str.split("(?<=\\D)(?=\\d)");
//		return CellReference.convertColStringToIndex(part[0]);
		return CellReference.convertColStringToIndex(column_name);
	}

	public static String getColumnIndex(String column_name) {
		String str = column_name;
		String[] part = str.split("(?<=\\D)(?=\\d)");
		return part[0];
	}
	
	public static void setCellData(String ref, XSSFSheet sheet, String cellData) {
		CellReference cellRef = new CellReference(ref);
		Row row = sheet.getRow(cellRef.getRow());
		Cell cell = row.getCell(cellRef.getCol());
		cell.setCellValue(cellData);		
	}

	public static String getCellData(String ref, XSSFSheet sheet) {
		CellReference cellRef = new CellReference(ref);
		Row row = sheet.getRow(cellRef.getRow());
		Cell cell = row.getCell(cellRef.getCol());
		String cellData = null;
		if (cell != null) {
			switch (cell.getCellType()) {
			case STRING:
				cellData = cell.getStringCellValue();
				break;
			case NUMERIC:
				cellData = String.valueOf(cell.getNumericCellValue());
				String afterDot = CommonUtil.deNull(cellData).indexOf(".") > 0 ?  CommonUtil.deNull(cellData).substring(CommonUtil.deNull(cellData).indexOf(".") + 1) : "0";
				Double doublevalue = !CommonUtil.deNull(cellData).isEmpty() ? Math.round(Double.parseDouble(cellData) * 100.0) / 100.0 : 0.00;				
				cellData = ((afterDot.length()>1 && !afterDot.equals("00") ) || Integer.valueOf(afterDot) > 0 ) ? String.format("%.2f", doublevalue): cellData.replace("."+afterDot, "");
				break;
			case BOOLEAN:
				cellData = String.valueOf(cell.getBooleanCellValue());
				break;
			case FORMULA:
				if (cell.getCellType() == CellType.FORMULA) {
					switch (cell.getCachedFormulaResultType()) {
					case NUMERIC:
						cellData = String.valueOf(cell.getNumericCellValue());
						break;
					case STRING:
						cellData = String.valueOf(cell.getRichStringCellValue());
						break;
					default:
						break;
					}
				}
				break;
			case BLANK:
				cellData = null;
				break;
			default:
				break;
			}
		}
		if(cellData != null) {
			cellData = cellData.replaceAll("\u00A0", " "); //#50564 - \u00a0, representing the non-breaking space (NBSP) character, set space to whitespace
			cellData = cellData.trim();
		}
		return cellData;

	}

	public static String getCellDataStringType(String ref, XSSFSheet sheet) {
		CellReference cellRef = new CellReference(ref);
		Row row = sheet.getRow(cellRef.getRow());
		Cell cell = row.getCell(cellRef.getCol());
		String cellData = null;

		if (cell == null) {
			return cellData;
		}
		switch (cell.getCellType()) {
		case STRING:
			cellData = cell.getStringCellValue();
			break;
		case NUMERIC:
			cellData = String.valueOf(cell.getNumericCellValue());
			if (cellData.contains(".")) {
				cellData = cellData.split("\\.")[0];
			}
			break;
		case BOOLEAN:
			cellData = String.valueOf(cell.getBooleanCellValue());
			break;
		case FORMULA:
			if (cell.getCellType() == CellType.FORMULA) {
				switch (cell.getCachedFormulaResultType()) {
				case NUMERIC:
					cellData = String.valueOf(cell.getNumericCellValue());
					break;
				case STRING:
					cellData = String.valueOf(cell.getRichStringCellValue());
					break;
				default:
					break;
				}
			}
			break;
		case BLANK:
			cellData = null;
			break;
		default:
			break;
		}
		if(cellData != null) {
			cellData = cellData.trim();
		}
		return cellData;

	}

	public static String consigneeSplit(String consignee) {
		int last = consignee.lastIndexOf("(");
		return consignee.substring(last + 1, consignee.length() - 1);
	}

	public static CellStyle no_style(XSSFWorkbook workbook) {
		CellStyle no_style = workbook.createCellStyle();
		no_style.setFillPattern(FillPatternType.NO_FILL);
		no_style.setFillPattern(FillPatternType.NO_FILL);
		no_style.setBorderBottom(BorderStyle.THIN);
		no_style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		no_style.setBorderRight(BorderStyle.THIN);
		no_style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		no_style.setBorderTop(BorderStyle.THIN);
		no_style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		return no_style;
	}

	public static CellStyle style_error(XSSFWorkbook workbook) {
		CellStyle style_error = workbook.createCellStyle();
		style_error.setFillForegroundColor(IndexedColors.RED.index);
		style_error.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return style_error;
	}

	public static CellStyle style_success(XSSFWorkbook workbook) {
		CellStyle style_success = workbook.createCellStyle();
		style_success.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
		style_success.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return style_success;
	}

	public static CellStyle style_header(XSSFWorkbook wb) {
		CellStyle style_header = wb.createCellStyle();
		try {
			// create borders and bold font
			XSSFFont font = wb.createFont();
			font.setBold(true);
			style_header.setBorderBottom(BorderStyle.THIN);
			style_header.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			style_header.setBorderRight(BorderStyle.THIN);
			style_header.setRightBorderColor(IndexedColors.BLACK.getIndex());
			style_header.setBorderTop(BorderStyle.THIN);
			style_header.setTopBorderColor(IndexedColors.BLACK.getIndex());
			style_header.setBorderLeft(BorderStyle.THIN);
			style_header.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			style_header.setBorderRight(BorderStyle.THIN);
			style_header.setRightBorderColor(IndexedColors.BLACK.getIndex());
			style_header.setFont(font);
			style_header.setVerticalAlignment(VerticalAlignment.CENTER);
			style_header.setAlignment(HorizontalAlignment.CENTER);
		} catch (Exception e) {
			log.error("Exception style_header : ", e);
		}
		return style_header;
	}

	public static CellStyle style(XSSFWorkbook wb) {
		CellStyle style = wb.createCellStyle();
		try {
			style.setBorderBottom(BorderStyle.THIN);
			style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			style.setBorderRight(BorderStyle.THIN);
			style.setRightBorderColor(IndexedColors.BLACK.getIndex());
			style.setBorderTop(BorderStyle.THIN);
			style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		} catch (Exception e) {
			log.error("Exception style : ", e);
		}
		return style;
	}

	public static CellStyle borderStyle(XSSFWorkbook wb) {
		CellStyle borderStyle = wb.createCellStyle();
		XSSFFont font = wb.createFont();
		font.setBold(true);
		try {
			borderStyle.setBorderBottom(BorderStyle.THIN);
			borderStyle.setBorderLeft(BorderStyle.THIN);
			borderStyle.setBorderRight(BorderStyle.THIN);
			borderStyle.setBorderTop(BorderStyle.THIN);
			borderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			borderStyle.setAlignment(HorizontalAlignment.CENTER);
			borderStyle.setFont(font);
		} catch (Exception e) {
			log.error("Exception borderStyle : ", e);
		}
		return borderStyle;
	}

	public static boolean removeColorsAndComment(String ref, XSSFSheet sheet, CellStyle no_style) {
		try {
			CellReference cref = new CellReference(ref);
			int rowIndex = cref.getRow();
			int cellIndex = cref.getCol();
			XSSFCell cell = sheet.getRow(rowIndex).getCell(cellIndex);
			cell.removeCellComment();
			XSSFCellStyle cellColorStyle = cell.getCellStyle();
			XSSFColor cellColor = cellColorStyle.getFillForegroundXSSFColor();
			if (cellColor != null) {
				String color = cellColor.getARGBHex();
				if (color.equalsIgnoreCase(ConstantUtil.error_color)) {
					cell.setCellStyle(no_style);
				}
			} else {
				cell.setCellStyle(no_style);
			}
			return true;
		} catch (Exception e) {
			log.error("Exception removeColorsAndComment : ", e);
			return false;
		}
	}

	public static boolean createCellComment(List<Comments> commentsList, List<ManifestUploadConfig> template,
			XSSFSheet sheet, XSSFWorkbook workbook) {
		try {
			if (commentsList.size() > 0) {
				for (Comments comment : commentsList) {

					for (ManifestUploadConfig manifestUploadConfig : template) {
						if (manifestUploadConfig.getAttr_name().equals(comment.getKey())) {
							CellReference cellRef = new CellReference(comment.getColumnNm());
							Row r = sheet.getRow(cellRef.getRow());
							Cell cell = r.getCell(cellRef.getCol());
							// set comment
							XSSFDrawing hpt = sheet.createDrawingPatriarch();
							XSSFComment comment1 = hpt
									.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));

							comment1.setRow(r.getRowNum());
							comment1.setColumn(cell.getColumnIndex());
							comment1.setString(new XSSFRichTextString(comment.getMessage()));
							cell.setCellComment(comment1);
							cell.setCellStyle(style_error(workbook));
						}
					}
				}
				return true;
			}
		} catch (Exception e) {
			log.error("Exception createCellComment : ", e);
		}

		return false;
	}

	public static boolean isNumeric(String cellData) {
		// Pattern pattern = Pattern.compile(".*[^0-9].*");
		// Pattern pattern = Pattern.compile("[0-9].+");
		// return !pattern.matcher(cellData).matches();
		try {
			Double.parseDouble(cellData);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isInteger(String cellData) {
		try {
			Double.parseDouble(cellData);
			if (cellData.contains(".")) {
				if (Double.parseDouble(cellData.split("\\.")[1]) > 0)
					return false;
			}

		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean decimalPointChk(String cellData) {
		try {
			Double data = Double.parseDouble(cellData);
			cellData = new DecimalFormat().format(data).toString();
			String[] dStr = String.valueOf(cellData).split("\\.");
			if (dStr != null && dStr.length == 2 && dStr[1].length() > 2) {
				return false;
			}
			return true;
		} catch (Exception e) {
			log.error("Exception decimalPointChk : ", e);
			return false;
		}
	}

	public static String[] getRequiredStringParameters(final ServletRequest request, final String name) {
		return request.getParameterValues(name);
	}

	// jp.src.sg.com.jp.dpe.util--->DpeCommonUtil-->addDayToDate()
	public static Date addDayToDate(Date date, int days) {
		if (date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, days);
		return cal.getTime();
	}

	// jp.src.util.lwms--->CommonUtils-->removeTime()
	public static Date removeTime(Date dateParam) {
		String dateStr = DateParser.dateToString(dateParam);
		Date temp;
		try {
			temp = DateParser.stringToDate(dateStr, "ddMMyyyy");
		} catch (Exception e) {
			return dateParam;
		}

		return temp;
	}

	// jp.src.util.lwms--->CommonUtils-->parserDouble()
	public static double parserDouble(String value) {
		if (value == null || value.length() <= 0)
			return 0;
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException nfex) {
			log.error("Exception parserDouble : ", nfex);
			return 0.00;
		}
	}

	// jp.src.util.lwms--->CommonUtils-->daysBetweenDouble()
	public static double daysBetweenDouble(Date beforeDate, Date afterDate) throws Exception {
		beforeDate = removeTime(beforeDate);
		afterDate = removeTime(afterDate);
		double diffMillis = (beforeDate.getTime() - afterDate.getTime()) / (24 * 60 * 60 * 1000);
		return diffMillis;
	}

	// jp.src.sg.com.jp.dpe.util--->DpeCommonUtil-->formatDateTime()
	public static String formatDateTime(Date dt) {
		if (dt == null) {
			return "";
		}
		try {
			SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			return fm.format(dt);
		} catch (Exception e) {
			return "";
		}
	}

	// jp.src.util.lwms--->CommonUtils-->formatDouble()
	public static double formatDouble(double number, int digit) {
		long pow = (long) Math.pow(10, digit);
		return (((double) Math.round(pow * number)) / pow);
	}

	// jp.src.util.lwms--->CommonUtils-->getInt()
	public static double getInt(double number) {
		Double objDouble = new Double(number);
		return objDouble.intValue();
	}

	public static Date toDate(String date, String sourceFormat) {

		try {
			return new SimpleDateFormat(sourceFormat).parse(date);

		} catch (ParseException e) {
			log.error("Exception toDate : ", e);
			return null;
		}

	}

	public static String trimLastChar(String actualString, String trimChar) {
		try {

			if (deNull(actualString) != "") {

				if (actualString.trim().contains(trimChar)) {
					actualString = actualString.substring(0, actualString.length() - 1);

				}
			}
		} catch (Exception e) {

		}
		return actualString;
	}

	public static String consigneeSplit(String consignee, String key) {
		int last = consignee.lastIndexOf("(");
		if (key.equalsIgnoreCase("co_nm")) {
			return consignee.substring(0, last);
		} else {
			return consignee.substring(last + 1, consignee.length() - 1);
		}
	}

	// Start Email
	public static String readTemplateFromFile(ClassLoader classLoader, String templatePath) {
		InputStream templateStream = null;
		try {
			log.info("readTemplateFromFile CommonUtil : " + templatePath + ", Class loader :" + classLoader);
			templateStream = classLoader.getResourceAsStream(templatePath.trim());
			log.info("readTemplateFromFile CommonUtil " + templatePath + ", templateStream :"
					+ templateStream.toString());
			String content = IOUtils.toString(templateStream);
			log.info("readTemplateFromFile CommonUtil content " + content);
			return content;
		} catch (Exception e) {
			log.error("Exception Error in readTemplateFromFile :" + templatePath + ", exception :" + e);
			log.info(e.toString());
		} finally {
			if (templateStream != null) {
				try {
					templateStream.close();
				} catch (IOException e) {
					log.info("readTemplateFromFile CommonUtil templateStream close exception");
					log.error(e);
				}

			}
		}

		return "";

	}

	public static String replaceVariablesInHtml(String content, Map<String, String> input) {
		String msg = new String(content);

		try {
			Set<Entry<String, String>> entries = input.entrySet();
			for (Map.Entry<String, String> entry : entries) {
				String key = "<" + entry.getKey().trim() + ">";
				String value = entry.getValue();
				if (value == null) {
					value = "";
				}
				msg = msg.replace(key, value.trim());
			}
			return msg;
		} catch (Exception e) {
			log.error("replaceVariablesInHtml exception ", e);
		}
		return msg;
	}

	public static Boolean sendEmail(Email email, String fnName) {

		if (email == null || email.getToList() == null || email.getToList().size() == 0) {
			log.error("Couldn't locate email recipients. Do nothing.");
			return false;
		}

		log.info("sendEmail ->" + email.toString());
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setContentLength(email.toString().length());

			HttpMethod method = HttpMethod.POST;
			URI url = new URI(email.getEmailSvcUrl());
			
			RequestEntity<Email> request = new RequestEntity<Email>(email, headers, method, url);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<EmailResponse> response = restTemplate.exchange(request, EmailResponse.class);

			if (response == null) {
				log.error("Couldn't receive response from email sending service.");
				return false;
			}

			log.info("response :: " + response.toString());
			// TODO
			// What's the statusCode value? true/false?
			// Need to cater to return false case
			return true;

		} catch (HttpClientErrorException e) {
			log.error("Exception sendEmail :", e);
			return false;
		} catch (Exception e) {
			log.error("Exception sendEmail :", e);
			return false;
		}
	}

	private static String getJsonString(Email email) {
		// Create an ObjectMapper instance
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			return objectMapper.writeValueAsString(email);
		} catch(Exception e) {
			return null;
		}
	}

	public static Boolean sendEmail(Email email) {
		
		if (email == null || email.getToList() == null || email.getToList().size() == 0) {
			log.error("Couldn't locate Email recipients. Do nothing.");
			return false;
		}

		try {
			String jsonBody = getJsonString(email);
			log.info("START sendEmail : " + jsonBody);
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setContentLength(email.toString().length());

			HttpMethod method = HttpMethod.POST;
			URI url = new URI(email.getEmailSvcUrl());

			RequestEntity<String> request = new RequestEntity<String>(jsonBody + "\r\n", headers, method, url);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<EmailResponse> response = restTemplate.exchange(request, EmailResponse.class);

			if (response == null) {
				log.error("Couldn't receive response from email sending service.");
				return false;
			}

			return true;

		} catch (HttpClientErrorException e) {
			log.error("Exception sendEmail :", e);
			return false;
		} catch (Exception e) {
			log.error("Exception sendEmail :", e);
			return false;
		} finally {
			log.info("END sendEmail ");
		}
	}
	// End Email

	public static Boolean sendSMS(Sms smsVO) {
		if (smsVO == null || smsVO.getToList()==null || smsVO.getToList().size() == 0) {
			log.error("Couldn't locate SMS recipients. Do nothing.");
			return false;
		}
		try {
			log.info("sendEmail ->" + smsVO.toString()+"SMSGATEWAYADDRESS:"+ SMSGATEWAYADDRESS);
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setContentLength(smsVO.toString().length());
			HttpMethod method = HttpMethod.POST;
			String urls = SMSGATEWAYADDRESS;
			URI url = new URI(urls);
			RequestEntity<Sms> request = new RequestEntity<Sms>(smsVO, headers, method, url);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Object> response=null;
			try {
				 response = restTemplate.exchange(request, Object.class);
			} catch (HttpClientErrorException e) {
				log.error("Couldn't receive response from SMS sending service.:HttpClientErrorException",e);
				log.error(e);
			}
			if (response == null) {
				log.error("Couldn't receive response from SMS sending service.");
				return false;
			}
			log.info("response :: " + response.toString());
			return true;
		} catch (Exception e) {
			log.error("Exception :sendSMS", e);
			return false;
		}
	}

	public static String getErrorMessage(String msg, String[] params) {
		String errorTemplate = msg;
		StringBuffer preMessage = null;
		if (errorTemplate != null) {
			preMessage = new StringBuffer(errorTemplate);
			if (params == null)
				return preMessage.toString();

			int index = 0;
			for (int i = 0; i < params.length; i++) {
				index = preMessage.toString().indexOf('~', index);
				if (index <= -1)
					break;
				preMessage.replace(index, ++index, params[i]);
			}
			return preMessage.toString();
		}
		return null;
	}

	public static Date convertStrToDate(String arg0, String arg1) {
		if (arg0 == null)
			return null;

		if (arg0.equalsIgnoreCase(""))
			return null;

		SimpleDateFormat f = new SimpleDateFormat(arg1);
		Date dt = null;
		try {
			dt = f.parse(arg0);
		} catch (Exception e) {
			return null;
		}

		return dt;
	}

	public static String formatDateToStr(Date arg0, String arg1) {
		if (arg0 == null)
			return new String("");

		SimpleDateFormat formatter = new SimpleDateFormat(arg1);
		String formattedDate = formatter.format(arg0);

		return formattedDate;
	}

	public static Integer chkNull(Integer arg0) {
		if (arg0 == null)
			return new Integer(0);

		return arg0;
	}

	public static BigDecimal chkNull(BigDecimal arg0) {
		if (arg0 == null)
			return new BigDecimal(0);

		return arg0;
	}
	
	public static String get2DecFromStr(String arg0) {
		if(arg0 == null) {
			return arg0;
		}else {
			String afterDot = arg0.substring(arg0.indexOf(".") + 1);
			if(afterDot.length()>1) {
				double roundOff = Math.round(Double.parseDouble(arg0)*100.0)/100.0;
				return String.valueOf(roundOff);
			}
		}
		return arg0;
	}

	public static String replaceHtmlSyntax(String words) throws BusinessException {
		String convertWords = "";

		try {

			log.info("START: replaceHtmlSyntax DAO **** words : " + deNull(words));

			convertWords = deNull(words);
			convertWords = convertWords.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&")
					.replace("&#x12;", "%12");

		} catch (NullPointerException e) {
			log.error("Exception: replaceHtmlSyntax ****: ", e);

		} catch (Exception e) {
			log.error("Exception: replaceHtmlSyntax ****: ", e);

		} finally {
			log.info(" End replaceHtmlSyntax Dao END : *** convertWords: " + deNull(convertWords));
		}

		return convertWords;
	}
	
	 public static String getKeyFromValue(Map<String,String> hm, String value) {
         for (String o : hm.keySet()) {
           if (hm.get(o).equals(value)) {
             return o;
           }
         }
         return null;
       }
}
