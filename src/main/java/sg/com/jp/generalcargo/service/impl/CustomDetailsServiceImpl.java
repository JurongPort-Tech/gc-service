package sg.com.jp.generalcargo.service.impl;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import sg.com.jp.generalcargo.dao.CustomDetailsRepository;
import sg.com.jp.generalcargo.dao.InwardCargoManifestRepository;
import sg.com.jp.generalcargo.domain.BookingReferenceFileUploadDetails;
import sg.com.jp.generalcargo.domain.Comments;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.CustomDetails;
import sg.com.jp.generalcargo.domain.CustomDetailsActionTrailDetails;
import sg.com.jp.generalcargo.domain.CustomDetailsFileUploadDetails;
import sg.com.jp.generalcargo.domain.CustomDetailsUploadConfig;
import sg.com.jp.generalcargo.domain.PageDetails;
import sg.com.jp.generalcargo.domain.Summary;
import sg.com.jp.generalcargo.domain.SummaryCuscar;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.Template;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.CustomDetailsService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;

@Service
public class CustomDetailsServiceImpl implements CustomDetailsService {

	private static final Log log = LogFactory.getLog(CustomDetailsServiceImpl.class);

	@Autowired
	private CustomDetailsRepository customDetailsRepo;

	@Autowired
	private InwardCargoManifestRepository inwardCargoMnifestRepo;

	@Value("${customDetails.file.upload.path}")
	String folderPath;

	@Override
	public List<VesselVoyValueObject> getlistVessel(String coCd, String search) throws BusinessException {
		return customDetailsRepo.getlistVessel(coCd, search);
	}

	@Override
	public TableResult getCustomDetailsActionTrail(Criteria criteria) throws BusinessException {
		return customDetailsRepo.getCustomDetailsActionTrail(criteria);
	}

	@Override
	public XSSFWorkbook customDetailsExcelDownload(String vvCd) throws BusinessException {
		XSSFWorkbook wb = new XSSFWorkbook();
		try {
			log.info("customDetailsExcelDownload : vvcd = " + CommonUtility.deNull(vvCd));
			XSSFSheet sheet = wb.createSheet("MACF");
			String version = "";
			version = customDetailsRepo.getTemplateVersionNo();
			wb.getProperties().getCoreProperties().setRevision(version);

			// Hide sheet for dropdown
			Sheet sheet_hidden = wb.createSheet("Reference");

			int startRow = ConstantUtil.row_start;
			int headerRow = ConstantUtil.row_header;

			int vesselName_cell = ConstantUtil.vesselName_cell;
			int Voy_cell = ConstantUtil.Voy_cell;
			// create borders and bold font
			XSSFFont font = wb.createFont();
			font.setBold(true);
			CellStyle style_header = wb.createCellStyle();
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
			
			XSSFFont redFont = wb.createFont();
			redFont.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
			redFont.setBold(true);
			

			CellStyle style_notes = wb.createCellStyle();
			style_notes.setFont(redFont);

			CellStyle style = wb.createCellStyle();
			style.setBorderBottom(BorderStyle.THIN);
			style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			style.setBorderRight(BorderStyle.THIN);
			style.setRightBorderColor(IndexedColors.BLACK.getIndex());
			style.setBorderTop(BorderStyle.THIN);
			style.setTopBorderColor(IndexedColors.BLACK.getIndex());

			// cargo template version
			Row r_sheet_hidden = sheet_hidden.createRow(0);
			Cell c1_sheet_hidden = r_sheet_hidden.createCell(0);
			Cell c2_sheet_hidden = r_sheet_hidden.createCell(1);
			c1_sheet_hidden.setCellValue(ConstantUtil.custom_template_version);
			c2_sheet_hidden.setCellValue(version);
			// c2_sheet_hidden.setCellValue(manifestRepo.getTemplateVersionNo());
			c1_sheet_hidden.setCellStyle(style_header);
			c2_sheet_hidden.setCellStyle(style);

			List<CustomDetails> customDetailsList = customDetailsRepo.getCustomDetails(vvCd); 
			log.info("customDetailsList size :" + customDetailsList.size());

			List<CustomDetailsUploadConfig> template = new ArrayList<CustomDetailsUploadConfig>();
			template = customDetailsRepo.getTemplateHeader();
			PageDetails vesselCalldetails = customDetailsRepo.getVesselCallDetails(vvCd);

			for (CustomDetailsUploadConfig CustomDetailsUploadConfig_vsl_details : template) {
				if (CustomDetailsUploadConfig_vsl_details.getAttr_name().equalsIgnoreCase(ConstantUtil.vessel_name)) {
					CellReference cellRef = new CellReference(
							CustomDetailsUploadConfig_vsl_details.getColumn_nm() + vesselName_cell);
					Row r = sheet.createRow(cellRef.getRow());
					Cell cell = r.createCell(cellRef.getCol());
					cell.setCellValue(CustomDetailsUploadConfig_vsl_details.getAttr_desc());
					cell.setCellStyle(style_header);
					Cell cell1 = r.createCell(cellRef.getCol() + 1);
					cell1.setCellValue(vesselCalldetails.getVesselName());
					cell1.setCellStyle(style_header);
					
					Cell cellNotes = r.createCell(cellRef.getCol() + 3);
					cellNotes.setCellValue(ConstantUtil.Custom_Excel_Notes);
					cellNotes.setCellStyle(style_notes);
					
				} else if (CustomDetailsUploadConfig_vsl_details.getAttr_name()
						.equalsIgnoreCase(ConstantUtil.CUSTOM_VOYAGE_NO)) {
					CellReference cellRef = new CellReference(
							CustomDetailsUploadConfig_vsl_details.getColumn_nm() + Voy_cell);
					Row r = sheet.createRow(cellRef.getRow());
					Cell cell = r.createCell(cellRef.getCol());
					cell.setCellValue(CustomDetailsUploadConfig_vsl_details.getAttr_desc());
					cell.setCellStyle(style_header);
					Cell cell1 = r.createCell(cellRef.getCol() + 1);
					cell1.setCellValue(vesselCalldetails.getVoyageNo());
					cell1.setCellStyle(style_header);
				}
			}

			// To set limit same as data if more than default limit
			int limit = customDetailsList.size() >= ConstantUtil.limit ? (customDetailsList.size() + 10)
					: ConstantUtil.limit;
			// TO draw rest of the excel lines
			for (int k = customDetailsList.size(); k <= limit; k++) {
				CustomDetails cm = new CustomDetails();
				customDetailsList.add(cm);
			}

			limit = limit + ConstantUtil.row_start;
			Row header_r = sheet.createRow(headerRow);
			Cell cell = null;
			for (CustomDetailsUploadConfig customDetailsUploadConfig_header : template) {
				cell = createCellvalues(sheet, header_r, customDetailsUploadConfig_header.getColumn_nm(),
						customDetailsUploadConfig_header.getAttr_desc(), style_header, redFont,
						customDetailsUploadConfig_header.getMandatory_ind());
				if (customDetailsUploadConfig_header.getAttr_name().equals(ConstantUtil.CUSTOM_HSCODE)) {
					XSSFDrawing hpt = sheet.createDrawingPatriarch();
					XSSFComment comment1 = hpt
							.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
					comment1.setRow(header_r.getRowNum());
					comment1.setColumn(cell.getColumnIndex());
					comment1.setString(new XSSFRichTextString(ConstantUtil.CUSTOM_HSCODE_TOOLTIP));
					cell.setCellComment(comment1);
				}
			}

			log.info("customDetailsList count:" + customDetailsList.size());
			for (CustomDetails customDetails : customDetailsList) {
				Row r = sheet.createRow(startRow++);
				for (CustomDetailsUploadConfig customDetailsUploadConfig : template) {
					cell = createCellvalues(sheet, r, customDetailsUploadConfig.getColumn_nm(),
							customDetails.getDynamicCol(customDetailsUploadConfig.getAttr_name()), style);
					if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_HSCODE)) {
						DataFormat fmt = wb.createDataFormat();
						CellStyle cs = style;
						cs.setDataFormat(fmt.getFormat("@"));
						cell.setCellStyle(cs);
					}

				}
			}

			List<String> portList = inwardCargoMnifestRepo.getPortListForExcelProcessing(false);
			List<String> portListWithName = inwardCargoMnifestRepo.getPortListForExcelProcessing(true);
			List<String> hsCodeSubCode = inwardCargoMnifestRepo.getHs_code_sub_code();
			List<String> consignee = inwardCargoMnifestRepo.getConsigneee();
			consignee.add(ConstantUtil.dropdown_others);

			// dropdown for action
			for (CustomDetailsUploadConfig customDetailsUploadConfig : template) {

				if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.action)) {
					String[] actionArr = { ConstantUtil.action_NA, ConstantUtil.action_add, ConstantUtil.action_update,
							ConstantUtil.action_delete };
					setDropDownForColumns(sheet, customDetailsUploadConfig.getColumn_nm(), actionArr, limit);
				} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_VESSEL_DIS_PORT)) {
					CellReference refld = new CellReference(customDetailsUploadConfig.getColumn_nm());

					Row row_hsHeader = sheet_hidden.createRow(ConstantUtil.sheet_hidden_Header);
					Cell cell_Header = row_hsHeader.createCell(0);
					cell_Header.setCellValue(customDetailsUploadConfig.getAttr_desc());
					cell_Header.setCellStyle(style_header);

					int i = ConstantUtil.sheet_hidden_rec_start;
					for (String value : portList) {
						Row row = null;
						if (sheet_hidden.getRow(i) == null) {
							row = sheet_hidden.createRow(i);
						} else {
							row = sheet_hidden.getRow(i);
						}
						cell = row.createCell(0);
						cell.setCellValue(value);
						i = i + 1;

					}

					row_hsHeader = sheet_hidden.getRow(ConstantUtil.sheet_hidden_Header);
					cell_Header = row_hsHeader.createCell(1);
					cell_Header.setCellValue(customDetailsUploadConfig.getAttr_desc() + " (Name Ref)");
					cell_Header.setCellStyle(style_header);

					i = ConstantUtil.sheet_hidden_rec_start;
					for (String value : portListWithName) {
						Row row = null;
						if (sheet_hidden.getRow(i) == null) {
							row = sheet_hidden.createRow(i);
						} else {
							row = sheet_hidden.getRow(i);
						}
						cell = row.createCell(1);
						cell.setCellValue(value);
						i = i + 1;

					}

					Name namedCell = sheet.getWorkbook().createName();
					namedCell.setNameName("Reference");
					int portListSize = portList.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$A$7:$A$" + portListSize);

					XSSFDataValidationHelper helper1 = (XSSFDataValidationHelper) sheet.getDataValidationHelper();
					XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) helper1
							.createFormulaListConstraint("Reference");
					XSSFDataValidation validation1 = (XSSFDataValidation) helper1.createValidation(constraint1,
							new CellRangeAddressList(ConstantUtil.row_start, limit, refld.getCol(), refld.getCol()));
					validation1.setSuppressDropDownArrow(true);
					validation1.setShowErrorBox(true);
					sheet.addValidationData(validation1);

				} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_INSTRUCTION_TYPE)) {
					String[] actionArr = { ConstantUtil.CUSTOM_IMPORT, ConstantUtil.CUSTOM_EXPORT,
							ConstantUtil.CUSTOM_TRANSHIPMENT_INWARD, ConstantUtil.CUSTOM_TRANSHIPMENT_OUTWARD,
							ConstantUtil.CUSTOM_TRANSIT };
					setDropDownForColumns(sheet, customDetailsUploadConfig.getColumn_nm(), actionArr, limit);
				} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_ORI_LOAD_PORT)) {

					CellReference refdis = new CellReference(customDetailsUploadConfig.getColumn_nm());
					Name namedCell = sheet.getWorkbook().createName();
					namedCell.setNameName("Reference1");
					int portListSize = portList.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$A$7:$A$" + portListSize);

					XSSFDataValidationHelper helper1 = (XSSFDataValidationHelper) sheet.getDataValidationHelper();
					XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) helper1
							.createFormulaListConstraint("Reference1");
					XSSFDataValidation validation1 = (XSSFDataValidation) helper1.createValidation(constraint1,
							new CellRangeAddressList(ConstantUtil.row_start, limit, refdis.getCol(), refdis.getCol()));
					validation1.setSuppressDropDownArrow(true);
					validation1.setShowErrorBox(true);
					sheet.addValidationData(validation1);
				} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_LOAD_PORT)) {

					CellReference refdis = new CellReference(customDetailsUploadConfig.getColumn_nm());
					Name namedCell = sheet.getWorkbook().createName();
					namedCell.setNameName("Reference2");
					int portListSize = portList.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$A$7:$A$" + portListSize);

					XSSFDataValidationHelper helper1 = (XSSFDataValidationHelper) sheet.getDataValidationHelper();
					XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) helper1
							.createFormulaListConstraint("Reference2");
					XSSFDataValidation validation1 = (XSSFDataValidation) helper1.createValidation(constraint1,
							new CellRangeAddressList(ConstantUtil.row_start, limit, refdis.getCol(), refdis.getCol()));
					validation1.setSuppressDropDownArrow(true);
					validation1.setShowErrorBox(true);
					sheet.addValidationData(validation1);
				} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_DIS_PORT)) {

					CellReference refdis = new CellReference(customDetailsUploadConfig.getColumn_nm());
					Name namedCell = sheet.getWorkbook().createName();
					namedCell.setNameName("Reference3");
					int portListSize = portList.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$A$7:$A$" + portListSize);

					XSSFDataValidationHelper helper1 = (XSSFDataValidationHelper) sheet.getDataValidationHelper();
					XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) helper1
							.createFormulaListConstraint("Reference3");
					XSSFDataValidation validation1 = (XSSFDataValidation) helper1.createValidation(constraint1,
							new CellRangeAddressList(ConstantUtil.row_start, limit, refdis.getCol(), refdis.getCol()));
					validation1.setSuppressDropDownArrow(true);
					validation1.setShowErrorBox(true);
					sheet.addValidationData(validation1);
				} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_DEST_PORT)) {

					CellReference refdis = new CellReference(customDetailsUploadConfig.getColumn_nm());
					Name namedCell = sheet.getWorkbook().createName();
					namedCell.setNameName("Reference4");
					int portListSize = portList.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$A$7:$A$" + portListSize);

					XSSFDataValidationHelper helper1 = (XSSFDataValidationHelper) sheet.getDataValidationHelper();
					XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) helper1
							.createFormulaListConstraint("Reference4");
					XSSFDataValidation validation1 = (XSSFDataValidation) helper1.createValidation(constraint1,
							new CellRangeAddressList(ConstantUtil.row_start, limit, refdis.getCol(), refdis.getCol()));
					validation1.setSuppressDropDownArrow(true);
					validation1.setShowErrorBox(true);
					sheet.addValidationData(validation1);

				} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_SHIPPER)) {
					CellReference refc = new CellReference(customDetailsUploadConfig.getColumn_nm());

					Row row_hsHeader = sheet_hidden.getRow(ConstantUtil.sheet_hidden_Header);
					Cell cell_Header = row_hsHeader.createCell(2);
					cell_Header.setCellValue(customDetailsUploadConfig.getAttr_desc());
					cell_Header.setCellStyle(style_header);
					// sheet_hidden.autoSizeColumn(2);

					int i = ConstantUtil.sheet_hidden_rec_start;
					for (String value : consignee) {
						Row row = null;
						if (sheet_hidden.getRow(i) == null) {
							row = sheet_hidden.createRow(i);
						} else {
							row = sheet_hidden.getRow(i);
						}
						cell = row.createCell(2);
						cell.setCellValue(value);
						i = i + 1;

					}

					Name namedCell = sheet.getWorkbook().createName();
					namedCell.setNameName("Reference5");
					int consigneeSize = consignee.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$C$7:$C$" + consigneeSize);

				} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_PACKAGE_TYPE)) {
					List<String> packagingType = inwardCargoMnifestRepo.getPackagingTypeDropDown();

					// write header in hidden sheet
					int col_index = CommonUtil.getColumnNumber(customDetailsUploadConfig.getColumn_nm());
					Row row_hsHeader = sheet_hidden.getRow(ConstantUtil.sheet_hidden_Header);
					Cell cell_Header = row_hsHeader.createCell(3);
					cell_Header.setCellValue(customDetailsUploadConfig.getAttr_desc());
					cell_Header.setCellStyle(style_header);

					int i = 6;
					for (String value : packagingType) {
						Row row = null;
						if (sheet_hidden.getRow(i) == null) {
							row = sheet_hidden.createRow(i);
						} else {
							row = sheet_hidden.getRow(i);
						}
						cell = row.createCell(3);
						cell.setCellValue(value);
						i = i + 1;

					}

					Name namedCell = sheet.getWorkbook().createName();
					namedCell.setNameName("Reference6");
					int packagingTypeSize = packagingType.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$D$7:$D$" + packagingTypeSize);
					XSSFDataValidationHelper helper1 = (XSSFDataValidationHelper) sheet.getDataValidationHelper();
					XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) helper1
							.createFormulaListConstraint("Reference6");
					XSSFDataValidation validation1 = (XSSFDataValidation) helper1.createValidation(constraint1,
							new CellRangeAddressList(ConstantUtil.row_start, limit, col_index, col_index));
					validation1.setSuppressDropDownArrow(true);
					validation1.setShowErrorBox(true);
					sheet.addValidationData(validation1);

				} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_HSCODE)) {

					// write header in hidden sheet
					Row row_hsHeader = sheet_hidden.getRow(ConstantUtil.sheet_hidden_Header);
					Cell cell_Header = row_hsHeader.createCell(4);
					cell_Header.setCellValue(customDetailsUploadConfig.getAttr_desc());
					cell_Header.setCellStyle(style_header);

					// write into the hidden sheet
					int i = 6;
					for (String hsCode : hsCodeSubCode) {
						Row row = null;
						if (sheet_hidden.getRow(i) == null) {
							row = sheet_hidden.createRow(i);
						} else {
							row = sheet_hidden.getRow(i);
						}
						cell = row.createCell(4);
						cell.setCellValue(hsCode);
						i = i + 1;

					}

					Name namedCell = sheet.getWorkbook().createName();
					namedCell.setNameName("Reference7");
					int hsCodeSubCodeSize = hsCodeSubCode.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$E$7:$E$" + hsCodeSubCodeSize);

				} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_HANDLING_INSTRUCTION)) {
					String[] arr = { ConstantUtil.CUSTOM_DIRECT_DELIVERY, ConstantUtil.CUSTOM_DISCHARGE_OVERSIDE,
							ConstantUtil.CUSTOM_UC_CARGO };
					setDropDownForColumns(sheet, customDetailsUploadConfig.getColumn_nm(), arr, limit);

				} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_DG_IND)) {
					String[] arr = { ConstantUtil.CUSTOM_NO, ConstantUtil.CUSTOM_YES };
					setDropDownForColumns(sheet, customDetailsUploadConfig.getColumn_nm(), arr, limit);

				} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_CNTR_STATUS)) {
					String[] arr = { ConstantUtil.CUSTOM_EMPTY, ConstantUtil.CUSTOM_FCL };
					setDropDownForColumns(sheet, customDetailsUploadConfig.getColumn_nm(), arr, limit);

				} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_IMO_CLASS)) {

					List<String> imoClass = customDetailsRepo.getIMOClassList();
					CellReference refpk = new CellReference(customDetailsUploadConfig.getColumn_nm());

					Row row_hsHeader = sheet_hidden.getRow(ConstantUtil.sheet_hidden_Header);
					Cell cell_Header = row_hsHeader.createCell(5);
					cell_Header.setCellValue(customDetailsUploadConfig.getAttr_desc());
					cell_Header.setCellStyle(style_header);
					// sheet_hidden.autoSizeColumn(1);

					int i = 6;
					for (String value : imoClass) {
						Row row = null;
						if (sheet_hidden.getRow(i) == null) {
							row = sheet_hidden.createRow(i);
						} else {
							row = sheet_hidden.getRow(i);
						}
						cell = row.createCell(5);
						cell.setCellValue(value);
						i = i + 1;

					}

					Name namedCell = sheet.getWorkbook().createName();
					namedCell.setNameName("Reference8");
					int imoClassSize = imoClass.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$F$7:$F$" + imoClassSize);

					XSSFDataValidationHelper helper1 = (XSSFDataValidationHelper) sheet.getDataValidationHelper();
					XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) helper1
							.createFormulaListConstraint("Reference8");
					XSSFDataValidation validation1 = (XSSFDataValidation) helper1.createValidation(constraint1,
							new CellRangeAddressList(ConstantUtil.row_start, limit, refpk.getCol(), refpk.getCol()));
					validation1.setSuppressDropDownArrow(true);
					validation1.setShowErrorBox(true);
					sheet.addValidationData(validation1);

				}
				
			}
			sheet_hidden.autoSizeColumn(0);
			sheet_hidden.autoSizeColumn(1);
			sheet_hidden.autoSizeColumn(2);
			sheet_hidden.autoSizeColumn(3);
			sheet_hidden.autoSizeColumn(4);
			sheet_hidden.autoSizeColumn(5);
			sheet.setColumnWidth(3,6000); // for notes
			// wb.cloneSheet(1, "Reference");
		} catch (BusinessException be) {
			log.info("Exception customDetailsExcelDownload : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception customDetailsExcelDownload : ", e);
			throw new BusinessException("M4201");
		}
		return wb;
	}

	private Cell createCellvalues(XSSFSheet sheet, Row header_r, String column_nm, String attr_desc,
			CellStyle style_header, XSSFFont redFont, String mandotaryInd) {
		int columnIndex = CommonUtil.getColumnNumber(column_nm);
		Cell cell = header_r.createCell(columnIndex);
		if (attr_desc == null || attr_desc.equals("")) {
			cell.setCellValue(attr_desc);
		} else {
			attr_desc = Jsoup.parse(attr_desc).text();
			attr_desc = Jsoup.parse(attr_desc).text();
			if (mandotaryInd.equalsIgnoreCase("Y")) {
				XSSFRichTextString richString = new XSSFRichTextString(attr_desc + " *");
				richString.applyFont((attr_desc.length() + 2) - 1, (attr_desc.length() + 2), redFont);
				cell.setCellValue(richString);
			} else {
				cell.setCellValue(attr_desc);
			}
		}
		cell.setCellStyle(style_header);
		if(columnIndex != 3) { // for notes
			sheet.autoSizeColumn(columnIndex);
		}
		
		return cell;
	}

	private Cell createCellvalues(XSSFSheet sheet, Row header_r, String column_nm, String attr_desc,
			CellStyle style_header) {
		int columnIndex = CommonUtil.getColumnNumber(column_nm);
		Cell cell = header_r.createCell(columnIndex);
		if (attr_desc == null || attr_desc.equals("")) {
			cell.setCellValue(attr_desc);
		} else {
			attr_desc = Jsoup.parse(attr_desc).text();
			cell.setCellValue(attr_desc);
		}
		cell.setCellStyle(style_header);
		if(columnIndex != 3) { // for notes
			sheet.autoSizeColumn(columnIndex);
		}
		return cell;
	}

	private void setDropDownForColumns(XSSFSheet sheet, String column_nm, String[] actionArr, int limit) {
		int col_index = CommonUtil.getColumnNumber(column_nm);
		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
		CellRangeAddressList addressList = new CellRangeAddressList(ConstantUtil.row_start, limit, col_index,
				col_index);
		XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
				.createExplicitListConstraint(actionArr);
		XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
		validation.setShowErrorBox(true);
		sheet.addValidationData(validation);
	}

	@Override
	public PageDetails customDetailsUploadDetail(String vvCd) throws BusinessException {
		return customDetailsRepo.customDetailsUploadDetail(vvCd);
	}

	@Override
	public CustomDetailsActionTrailDetails customDetailsActionTrailDetail(String custom_act_trl_id, String typeCd)
			throws BusinessException {
		return customDetailsRepo.customDetailsActionTrailDetail(custom_act_trl_id, typeCd);
	}

	@Override
	public String fileUpload(MultipartFile uploadFile, String varNbr) throws BusinessException {
		try {
			log.info("START : fileUpload:" + " Size:" + uploadFile.getSize() + "varNbr :"
					+ CommonUtility.deNull(varNbr));
			if (uploadFile.getOriginalFilename().indexOf("/") >= 0
					|| uploadFile.getOriginalFilename().indexOf("\\") >= 0) {
				log.info("File name validation failed!");
				return null;
			}
			String extension = FilenameUtils.getExtension(uploadFile.getOriginalFilename());
			UUID uuid = UUID.randomUUID();
			String fileName = uuid.toString() + "." + extension;
			log.info("assignedFileName:" + fileName);
			if (fileName.indexOf("/") >= 0 || fileName.indexOf("\\") >= 0) {
				log.info("File name validation failed!");
				return null;
			}
			String folderPathVvcd = folderPath + "/" + varNbr + "/";
			Path rootLocation = Paths.get(folderPathVvcd);
			if (!Files.exists(rootLocation)) {
				Files.createDirectories(rootLocation);
			}
			log.info("uploadFile data :" + uploadFile.getInputStream());
			Path folderLocation = rootLocation;
			if (!Files.exists(folderLocation)) {
				Files.createDirectories(folderLocation);
			}
			Path fileToDeletePath = folderLocation.resolve(fileName);
			Files.deleteIfExists(fileToDeletePath);
			log.info("fileUpload folderLocation :" + folderLocation);
			Files.copy(uploadFile.getInputStream(), folderLocation.resolve(fileName));
			log.info("END: *** fileUpload Result *****" + CommonUtility.deNull(fileName));
			return fileName;
		} catch (Exception e) {
			log.info("Exception fileUpload : ", e);
			return null;
		} finally {
			log.info("End fileUpload ");
		}
	}

	@Override
	public String getTimeStamp() throws BusinessException {
		return inwardCargoMnifestRepo.getTimeStamp();
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public Summary processCustomDetailsExcelFile(MultipartFile uploadingFile,
			CustomDetailsFileUploadDetails customDetailsFileUploadDetails, String varNbr, String userId,
			String companyCode) throws BusinessException {
		XSSFWorkbook workbook = null;
		FileOutputStream fileOut = null;
		Summary summary = new Summary();
		String outputFileName = null;
		XSSFSheet sheet = null;
		try {
			log.info("START: processCustomDetailsExcelFile Start " + " varNbr:" + CommonUtility.deNull(varNbr)
					+ " userId:" + CommonUtility.deNull(userId));
			Long seq_id = customDetailsRepo.insertCustomExcelDetails(customDetailsFileUploadDetails);
			SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmm");

			if (customDetailsFileUploadDetails.getTypeCd().equals(ConstantUtil.typeCd_CustomDetailsExcel)) {
				log.info("Process vesselDetailsValidation Start  for varNbr :" + CommonUtility.deNull(varNbr));

				workbook = new XSSFWorkbook(OPCPackage.open(uploadingFile.getInputStream()));
				sheet = workbook.getSheetAt(0);
				summary = vesselDetailsValidation(workbook, sheet, varNbr, summary); // Vessel and inward no validation
				log.info("Process vesselDetailsValidation END  for varNbr :" + CommonUtility.deNull(varNbr)
						+ " summary :" + summary.toString());
				if (summary.isHeaderValid()) {
					log.info("Process excelTemplateValidation START  for varNbr :" + CommonUtility.deNull(varNbr));
					summary = excelTemplateValidation(workbook, sheet, summary); // header validation
					log.info("Process excelTemplateValidation END  for varNbr :" + CommonUtility.deNull(varNbr)
							+ " summary :" + summary.toString());

					if (summary.isHeaderValid()) {
						log.info("Process excelRowsValidate START  for varNbr :" + CommonUtility.deNull(varNbr));
						// excel validate
						summary = excelRowsValidate(workbook, sheet, varNbr, userId,
								customDetailsFileUploadDetails.getLast_modified_dttm(), summary, companyCode); // rows
																												// validation
						
						String summaryUploaded = getTotalCntrForVessel(varNbr);
						summary.setSummaryUploaded(summaryUploaded);
						log.info("Process excelRowsValidate END  for varNbr :" + CommonUtility.deNull(varNbr)
								+ " summary :" + summary.toString());
					}
				}
			}
			summary.setTypeCd(customDetailsFileUploadDetails.getTypeCd());
			// 4)create error excel name
			outputFileName = ConstantUtil.custom_details_filename + f.format(new Date()) + ConstantUtil.file_ext;

			// 5)need to copy file
			try {
				Path rootLocation = Paths.get(folderPath + "/" + varNbr + "/");
				if (!Files.exists(rootLocation)) {
					Files.createDirectories(rootLocation);
				}
				Path folderLocation = rootLocation;
				if (!Files.exists(folderLocation)) {
					Files.createDirectories(folderLocation);
				}
				Path fileToDeletePath = folderLocation.resolve(outputFileName);
				Files.deleteIfExists(fileToDeletePath);
				String path = folderLocation.resolve(outputFileName).toString();
				log.info("fileUpload folderLocation :" + path);
				fileOut = new FileOutputStream(path);
				summary.getWorkbook().write(fileOut);
				summary.getWorkbook().close();
				fileOut.close();

			} catch (Exception e) {
				log.info("Exception processCustomDetailsExcelFile : ", e);
				throw new BusinessException("M4201");
			}

			// 6)update output file name
			boolean row = customDetailsRepo.updateCustomDetailsExcelDetails(seq_id, outputFileName);
			log.info(row);

			// 7)return value
			Template template = new Template();
			template.setRefId(seq_id.toString());
			template.setRefType("xlsx");
			template.setFileName(outputFileName);
			List<Template> templateList = new ArrayList<Template>();
			templateList.add(template);
			summary.setWorkbook(null);
			summary.setFileDetails(templateList);
			summary.setType(ConstantUtil.type_Output);

			log.info("END: *** processCustomDetailsExcelFile Result *****" + summary.toString());
		} catch (BusinessException be) {
			log.info("Exception processCustomDetailsExcelFile : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception processCustomDetailsExcelFile : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END processCustomDetailsExcelFile");
			sheet = null;
			workbook = null;
		}
		return summary;
	}

	public String getTotalCntrForVessel(String varNbr) throws BusinessException {
		return customDetailsRepo.getTotalCntrForVessel(varNbr);
	}

	private Summary excelRowsValidate(XSSFWorkbook workbook, XSSFSheet sheet, String varNbr, String userId,
			String last_modified_dttm, Summary summary, String companyCode) throws BusinessException {

		List<CustomDetails> customDetailsRecords = new ArrayList<CustomDetails>();
		String[] purCdInward = {"LI","TS","T"};
		String[] purCdOutward = {"LE","TE"};
		try {
			log.info("START excelRowsValidate :vvCd :" + CommonUtility.deNull(varNbr) + " userId : "
					+ CommonUtility.deNull(userId) + " timeStamp : " + CommonUtility.deNull(last_modified_dttm)
					+ " summary : " + summary);

			CellStyle style_success = workbook.createCellStyle();
			style_success.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
			style_success.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			CellStyle style_error = workbook.createCellStyle();
			style_error.setFillForegroundColor(IndexedColors.RED.index);
			style_error.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			CellStyle no_style = workbook.createCellStyle();
			no_style.setFillPattern(FillPatternType.NO_FILL);
			no_style.setBorderBottom(BorderStyle.THIN);
			no_style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			no_style.setBorderRight(BorderStyle.THIN);
			no_style.setRightBorderColor(IndexedColors.BLACK.getIndex());
			no_style.setBorderTop(BorderStyle.THIN);
			no_style.setTopBorderColor(IndexedColors.BLACK.getIndex());

			int rowStart = ConstantUtil.row_start;

			List<CustomDetailsUploadConfig> header = customDetailsRepo.getTemplateHeader();
			log.info(" ExcelProcessing :vvCd :" + CommonUtility.deNull(varNbr) + " , " + " getTemplateHeader :"
					+ header.size());

			// Dropdown
			List<String> portListDropdownList = inwardCargoMnifestRepo.getPortListForExcelProcessing(false);
			List<String> consigneeDropdownList = inwardCargoMnifestRepo.getConsigneee();
			consigneeDropdownList.add(ConstantUtil.dropdown_others);
			List<String> packagingType = inwardCargoMnifestRepo.getPackagingTypeDropDown();
			List<String> imoClass = customDetailsRepo.getIMOClassList();

			// Find row count
			int rowCount = CommonUtil.getRowCount(sheet, rowStart, ConstantUtil.customDetails_bl_nbr_index,
					ConstantUtil.typeCd_CustomDetailsExcel);
			int recStart = rowStart + 1;
			log.info(" rowCount :" + rowCount);
			CustomDetails macf = new CustomDetails();
			List<Comments> commentsList = new ArrayList<Comments>();
			macf.setVv_cd(varNbr);
			String cellData_na = "";
			boolean deleteflag = false;
			boolean cntrExistinJPOM = false;
			String cellData_cntr = "";
			String cellData_instructionType = "";
			Map<String,String> detailsMap = new HashMap<String,String>();

			for (int i = recStart; i <= rowCount + 1; i++) {
				deleteflag = false;
				detailsMap = new HashMap<String,String>();
				try {
					// For not applicable
					cellData_na = CommonUtil.getCellData(
							CommonUtil.getColumnIndex(ConstantUtil.action_index) + String.valueOf(i), sheet);
					if (cellData_na == null || cellData_na.isEmpty() || (cellData_na != null && cellData_na.equalsIgnoreCase(ConstantUtil.action_NA)) ) {
						continue;
					}

					macf = new CustomDetails();
					macf.setLast_modify_dttm(last_modified_dttm);
					commentsList = new ArrayList<Comments>();
					log.info("Started Excel Iteration  for  RowNo : " + i);

					cellData_cntr = CommonUtil.deNull(CommonUtil.getCellDataStringType(ConstantUtil.CUSTOM_CNTRNBR_INDEX + String.valueOf(i), sheet));
					cellData_instructionType = CommonUtil.deNull(CommonUtil.getCellDataStringType(ConstantUtil.CUSTOM_INSTRUCTIONTYPE_INDEX + String.valueOf(i), sheet));
					if (cellData_cntr != null && !cellData_cntr.isEmpty()
							&& (cellData_na.equalsIgnoreCase(ConstantUtil.action_delete))) {
						int exist = customDetailsRepo.customDetailIsExist(cellData_cntr, varNbr,ConstantUtil.CUSTOM_DETAILS_INSTRUCTION_TYPE_MAP.get(cellData_instructionType));
						if (exist == 0) {
							Comments comments = new Comments();
							comments.setKey(ConstantUtil.bills_of_landing_no);
							comments.setMessage(ConstantUtil.ErrorMsg_cntrNbrNotExist + "-" + varNbr);
							commentsList.add(comments);
						} else {
							deleteflag = true;
						}
					}
					for (CustomDetailsUploadConfig customDetailsUploadConfig : header) {
						if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.action)) {
							// Action Column
							String columnIndex = CommonUtil.getColumnIndex(customDetailsUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							// error
							if (CommonUtil.deNull(cellData) == "") {
								if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
										.equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(customDetailsUploadConfig.getAttr_name());
									comments.setMessage(
											customDetailsUploadConfig.getAttr_name() + ConstantUtil.mandatory);
									commentsList.add(comments);
								}
							} else {
								macf.setAction(cellData);

								// get whole details map based on isCntrDetailsMatch
								detailsMap = customDetailsRepo.getCntrdetailsMap(cellData_cntr,ConstantUtil.CUSTOM_DETAILS_INSTRUCTION_TYPE_MAP.get(cellData_instructionType), varNbr);
								cntrExistinJPOM = customDetailsRepo.containerIsExist(cellData_cntr, varNbr) > 0;
						}

						
						} else if (customDetailsUploadConfig.getAttr_name()
								.equals(ConstantUtil.CUSTOM_INSTRUCTION_TYPE)) {
							String columnIndex = CommonUtil.getColumnIndex(customDetailsUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellDataStringType(ref, sheet);
							String upperCellData = "";
							String cntrNbrCellData = CommonUtil.deNull(CommonUtil
									.getCellData(CommonUtil.getColumnIndex(ConstantUtil.CUSTOM_CNTRNBR_INDEX)
											+ String.valueOf(i), sheet)).toUpperCase();
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (CommonUtil.deNull(cellData) == "") {
								if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
										.equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(customDetailsUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG
											+ customDetailsUploadConfig.getAttr_desc());
									commentsList.add(comments);
								}
							} else if (!deleteflag && ConstantUtil.CUSTOM_DETAILS_INSTRUCTION_TYPE_MAP.get(upperCellData) == null) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_InvalidItemFromDroDown);
								commentsList.add(comments);
							
							} else if (!deleteflag && detailsMap.isEmpty()) {
								if (cntrExistinJPOM) {
									Comments comments = new Comments();
									comments.setKey(customDetailsUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_WrongInstructionType);
									commentsList.add(comments);
								}
							} else if (!deleteflag
									&& (!detailsMap.isEmpty() && !detailsMap.get("PURP_CD").equalsIgnoreCase(
											ConstantUtil.CUSTOM_DETAILS_INSTRUCTION_TYPE_MAP.get(upperCellData)))
									&& !CommonUtil.deNull(cntrNbrCellData).isEmpty()) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_WrongInstructionType);
								commentsList.add(comments);
							} else if (!deleteflag && summary.getCheckVoy().equals("I") && !Arrays.asList(purCdInward).contains(ConstantUtil.CUSTOM_DETAILS_INSTRUCTION_TYPE_MAP.get(upperCellData)) ) { // check if instruction type only for LI and TS, other having error
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_WrongInstructionTypeInward);
								commentsList.add(comments);
							} else if (!deleteflag && summary.getCheckVoy().equals("O") && !Arrays.asList(purCdOutward).contains(ConstantUtil.CUSTOM_DETAILS_INSTRUCTION_TYPE_MAP.get(upperCellData)) ) { // check if instruction type only for LE and TE, other having error
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_WrongInstructionTypeOutward);
								commentsList.add(comments);
							} else {
								macf.setInstruction_type(
										ConstantUtil.CUSTOM_DETAILS_INSTRUCTION_TYPE_MAP.get(upperCellData));
							}

						} else if (customDetailsUploadConfig.getAttr_name()
								.equals(ConstantUtil.CUSTOM_HANDLING_INSTRUCTION)) {
							String columnIndex = CommonUtil.getColumnIndex(customDetailsUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellDataStringType(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (CommonUtil.deNull(cellData) == "") {
								if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
										.equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(customDetailsUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG
											+ customDetailsUploadConfig.getAttr_desc());
									commentsList.add(comments);
								}
							} else if (!deleteflag && ConstantUtil.CUSTOM_DETAILS_HANDLING_INSTRUCTION_MAP
									.get(upperCellData) == null) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_InvalidItemFromDroDown);
								commentsList.add(comments);

							} else {
								macf.setHandling_instruction(
										ConstantUtil.CUSTOM_DETAILS_HANDLING_INSTRUCTION_MAP.get(upperCellData));
							}

						} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_CONSIGNEE)) {
							String columnIndex = CommonUtil.getColumnIndex(customDetailsUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (CommonUtil.deNull(cellData) == "") {
								if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
										.equalsIgnoreCase(ConstantUtil.yes)) {									
										Comments comments = new Comments();
										comments.setKey(customDetailsUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_Consignee_nm);
										commentsList.add(comments);
									
								}
							} else {
								macf.setConsignee_name(upperCellData);						
							}

						} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_SHIPPER)) {
							String columnIndex = CommonUtil.getColumnIndex(customDetailsUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (CommonUtil.deNull(cellData) == "") {
								if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
										.equalsIgnoreCase(ConstantUtil.yes)) {									
										Comments comments = new Comments();
										comments.setKey(customDetailsUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_Shipper_nm);
										commentsList.add(comments);
									
								}
					
							} else {
									macf.setShipper_name(upperCellData);
							}

						} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_CNTR_STATUS)) {
							String columnIndex = CommonUtil.getColumnIndex(customDetailsUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellDataStringType(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (CommonUtil.deNull(cellData) == "") {
//								if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
//										.equalsIgnoreCase(ConstantUtil.yes)) {
//									Comments comments = new Comments();
//									comments.setKey(customDetailsUploadConfig.getAttr_name());
//									comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG
//											+ customDetailsUploadConfig.getAttr_desc());
//									commentsList.add(comments);
//								}
							} else if (!deleteflag && ConstantUtil.CUSTOM_DETAILS_CNTR_STATUS_MAP.get(upperCellData) == null) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_InvalidItemFromDroDown);
								commentsList.add(comments);
							
//							} else if (!deleteflag && !customDetailsRepo.isCntrDetailsMatch(
//									ConstantUtil.CUSTOM_DETAILS_CNTR_STATUS_MAP.get(upperCellData),
//									CommonUtil.deNull(macf.getCntr_nbr()), ConstantUtil.CUSTOM_CNTR_STATUS, varNbr)
//									&& !CommonUtil.deNull(macf.getCntr_nbr()).isEmpty()) {
//								Comments comments = new Comments();
//								comments.setKey(customDetailsUploadConfig.getAttr_name());
//								comments.setMessage(ConstantUtil.ErrorMsg_WrongContainerStatus);
//								commentsList.add(comments);
							} else {
								macf.setCntr_status(ConstantUtil.CUSTOM_DETAILS_CNTR_STATUS_MAP.get(upperCellData));
							}

						} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_PACKAGE_TYPE)) {
							String columnIndex = CommonUtil.getColumnIndex(customDetailsUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellDataStringType(ref, sheet);

							removeColorsAndComment(ref, sheet, no_style);
							if (CommonUtil.deNull(cellData) == "") {
								if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
										.equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(customDetailsUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG
											+ customDetailsUploadConfig.getAttr_desc());
									commentsList.add(comments);
								}
							} else if (!deleteflag && !packagingType.contains(cellData)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_InvalidItemFromDroDown);
								commentsList.add(comments);

							} else {
								String package_cd = cellData.substring(0, cellData.indexOf("-"));
								macf.setPackage_type(package_cd);
							}

						} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_VESSEL_DIS_PORT)
								|| customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_ORI_LOAD_PORT)
								|| customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_LOAD_PORT)
								|| customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_DIS_PORT)
								|| customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_DEST_PORT)) {
							String columnIndex = CommonUtil.getColumnIndex(customDetailsUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (CommonUtil.deNull(cellData) == "") {
								if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
										.equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(customDetailsUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_Port);
									commentsList.add(comments);
								}
							} else if (!deleteflag && !portListDropdownList.contains(upperCellData)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_InvalidPort);
								commentsList.add(comments);
							} else {
								String ld_port = upperCellData;
								if (customDetailsUploadConfig.getAttr_name()
										.equals(ConstantUtil.CUSTOM_VESSEL_DIS_PORT)) {
									macf.setVessel_dis_port(ld_port.trim());
								} else if (customDetailsUploadConfig.getAttr_name()
										.equals(ConstantUtil.CUSTOM_ORI_LOAD_PORT)) {
									macf.setOri_load_port(ld_port.trim());
								} else if (customDetailsUploadConfig.getAttr_name()
										.equals(ConstantUtil.CUSTOM_LOAD_PORT)) {
									macf.setLoad_port(ld_port.trim());
								} else if (customDetailsUploadConfig.getAttr_name()
										.equals(ConstantUtil.CUSTOM_DIS_PORT)) {
									macf.setDis_port(ld_port.trim());
								} else if (customDetailsUploadConfig.getAttr_name()
										.equals(ConstantUtil.CUSTOM_DEST_PORT)) {
									macf.setDest_port(ld_port.trim());
								}
							}
						} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_DG_IND)) {
							String columnIndex = CommonUtil.getColumnIndex(customDetailsUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "", storedValue = "";
							String cntrNbrCellData = CommonUtil.deNull(CommonUtil
									.getCellData(CommonUtil.getColumnIndex(ConstantUtil.CUSTOM_CNTRNBR_INDEX)
											+ String.valueOf(i), sheet)).toUpperCase();
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							if (CommonUtil.deNull(upperCellData).equalsIgnoreCase(ConstantUtil.STRING_VALUE_YES)) {
								storedValue = "Y";
							} else {
								storedValue = "N";
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (!deleteflag && !upperCellData.isEmpty() && (!detailsMap.isEmpty()
									&& !detailsMap.get("DG_IND").equalsIgnoreCase(storedValue))
									&& !CommonUtil.deNull(cntrNbrCellData).isEmpty()) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_WrongDangerousIndicator);
								commentsList.add(comments);
							} else {
								macf.setDg_ind(storedValue);
							}
						} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_HSCODE)) {
							String columnIndex = CommonUtil.getColumnIndex(customDetailsUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
								upperCellData = CommonUtil.deNull(upperCellData).indexOf(".") > 0 ?  CommonUtil.deNull(upperCellData).substring(0, CommonUtil.deNull(upperCellData).indexOf(".")) : upperCellData;
							} 
							removeColorsAndComment(ref, sheet, no_style);
							if (CommonUtil.deNull(cellData) == "") {
								if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
										.equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(customDetailsUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_HSCodeCustom);
									commentsList.add(comments);
								}
							} else if (!deleteflag && (!(upperCellData.length() == 0 || upperCellData.length() == 4
									|| upperCellData.length() == 6 || upperCellData.length() == 8))) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_InvalidCustomHSCode);
								commentsList.add(comments);
							} else {
								macf.setHscode(upperCellData);
							}

						} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_IMO_CLASS)) {
							String columnIndex = CommonUtil.getColumnIndex(customDetailsUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (CommonUtil.deNull(cellData) == "") {
								if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
										.equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(customDetailsUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG
											+ customDetailsUploadConfig.getAttr_desc());
									commentsList.add(comments);
								} 
							} else if (!deleteflag && !imoClass.contains(cellData)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_InvalidItemFromDroDown);
								commentsList.add(comments);
							} else {
								String imoClass_cd = upperCellData.substring(0, upperCellData.indexOf("("));
								macf.setImo_class(imoClass_cd.trim());
							}
						} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_BL_NBR)) {
							String columnIndex = CommonUtil.getColumnIndex(customDetailsUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							String cntrNbrCellData = CommonUtil.deNull(CommonUtil
									.getCellData(CommonUtil.getColumnIndex(ConstantUtil.CUSTOM_CNTRNBR_INDEX)
											+ String.valueOf(i), sheet)).toUpperCase();
							if (cellData != null && cellData != "") {
								upperCellData = (cellData.toUpperCase()).trim();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (CommonUtil.deNull(cellData) == "") {
								if (customDetailsUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(customDetailsUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_BlNbr);
									commentsList.add(comments);
								} 
							
							} else if (!deleteflag && detailsMap.isEmpty()) {
								if (cntrExistinJPOM) {
									continue; // instruction type error
								}
							} else if (!deleteflag && (!detailsMap.isEmpty()
									&& !detailsMap.get("BILL_LADING_NBR").isEmpty()
									&& !detailsMap.get("BILL_LADING_NBR").equalsIgnoreCase(upperCellData))
									&& !CommonUtil.deNull(cntrNbrCellData).isEmpty()) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_WrongBLNo);
								commentsList.add(comments);
							} else {
								macf.setBl_nbr(upperCellData);
							} 
						} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_CNTR_NBR)) {
							String columnIndex = CommonUtil.getColumnIndex(customDetailsUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = (cellData.toUpperCase()).trim();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (CommonUtil.deNull(cellData) == "") {
								if (customDetailsUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(customDetailsUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_ContainerNumber);
									commentsList.add(comments);
								}
							} else if (macf.getAction().equalsIgnoreCase(ConstantUtil.action_add)) {
								if (!deleteflag && !cntrExistinJPOM) {
									Comments comments = new Comments();
									comments.setKey(customDetailsUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_cntrNbrNotExistContainer);
									commentsList.add(comments);
								} else if (customDetailsRepo.customDetailIsExist(upperCellData, varNbr, macf.getInstruction_type()) > 0) {
									Comments comments = new Comments();
									comments.setKey(customDetailsUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_cntrNbrAlreadyExist);
									commentsList.add(comments);
								} else if (!deleteflag && (!detailsMap.isEmpty()
										&& !customDetailsRepo.isShipmentStatusValid(detailsMap.get("CNTR_SEQ_NBR"), varNbr))) {
									Comments comments = new Comments();
									comments.setKey(customDetailsUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_cntrNbrShipementStatusNotValid);
									commentsList.add(comments);
								} else {
									macf.setCntr_nbr(upperCellData);
								}
							} else if (macf.getAction().equalsIgnoreCase(ConstantUtil.action_update)
									|| macf.getAction().equalsIgnoreCase(ConstantUtil.action_delete)) {
								if (customDetailsRepo.customDetailIsExist(upperCellData, varNbr, macf.getInstruction_type()) == 0) {
									Comments comments = new Comments();
									comments.setKey(customDetailsUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_cntrNbrNotExist);
									commentsList.add(comments);
								} else {
									macf.setCntr_nbr(upperCellData);
								}
							}
						} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_GROSS_WEIGHT) ||
								customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_WEIGHT)) {
							String columnIndex = CommonUtil.getColumnIndex(customDetailsUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (CommonUtil.deNull(cellData) == "") {
								if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
										.equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(customDetailsUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG
											+ customDetailsUploadConfig.getAttr_desc());
									commentsList.add(comments);
								}
							} else if (!deleteflag && (upperCellData.indexOf(",")>0) ) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.CUSTOM_VALID_WEIGHT);
								commentsList.add(comments);
							} else {
								 if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_GROSS_WEIGHT)) {
									 macf.setGross_wt(upperCellData);
								 } else {
									 macf.setWeight(upperCellData);
								 }								
							}

						} else { // other data
							String columnIndex = CommonUtil.getColumnIndex(customDetailsUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellDataStringType(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (CommonUtil.deNull(cellData) == "") {
								if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
										.equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(customDetailsUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG
											+ customDetailsUploadConfig.getAttr_desc());
									commentsList.add(comments);
								}
							} else if (upperCellData.length() > customDetailsUploadConfig.getMax_length()) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(customDetailsUploadConfig.getAttr_desc()
										+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
												String.valueOf(customDetailsUploadConfig.getMax_length())));
								commentsList.add(comments);
							} else {
								macf.setDynamicCol(customDetailsUploadConfig.getAttr_name(), upperCellData);
							}

						}
					}

					macf.setRownum(i - 1);// -1 is to take the exact row number
					macf.setErrorInfo(commentsList);

					if (commentsList.size() > 0) {
						macf.setMessage(ConstantUtil.error);
					} else {
						macf.setMessage(ConstantUtil.success);
					}
					log.info("#####################################################################  Data :"
							+ macf.toString());
					customDetailsRecords.add(macf);

				} catch (Exception e) {
					log.info("Exception excelRowsValidate : ", e);
					if (macf == null) {
						macf = new CustomDetails();
					}
					macf.setRownum(i - 1);// -1 is to take the exact row number
					macf.setMessage(ConstantUtil.ErrorMsg_Common);
					macf.setErrorInfo(commentsList);
					customDetailsRecords.add(macf);
					log.info("Exception row processing  row:" + i + " macf :" + macf.toString());
					log.info("Exception processing : ", e);
					continue;
				}

			}

			List<CustomDetails> processResults = customDetailsRepo.insertCustomDetailsData(customDetailsRecords, varNbr,
					userId, companyCode);
			int success = 0;
			int failure = 0;
			int headerRow = ConstantUtil.row_header;
			Row r_remarks = sheet.getRow(headerRow);
			int lastRemrksColumn = r_remarks.getLastCellNum();
			Cell cell_remarks = r_remarks.createCell(lastRemrksColumn);
			cell_remarks.setCellValue(ConstantUtil.remarks);
			sheet.autoSizeColumn(cell_remarks.getColumnIndex());

			for (CustomDetails customDetails : processResults) {
//						log.info("writing to Excel iteration :" + customDetails.toString());
				if (customDetails.getMessage().equalsIgnoreCase(ConstantUtil.success)) {
					Row r = sheet.getRow(customDetails.getRownum());
					Cell cell = r.createCell(lastRemrksColumn);
					cell.setCellValue(customDetails.getMessage());
					cell.setCellStyle(style_success);
					success++;
				} else if (customDetails.getMessage() != null) {
					Row r1 = sheet.getRow(customDetails.getRownum());
					Cell c = r1.createCell(lastRemrksColumn);
					c.setCellValue(customDetails.getMessage());
					c.setCellStyle(style_error);
					failure++;
					if (customDetails.getErrorInfo().size() > 0) {

						List<Comments> errorInfo = customDetails.getErrorInfo();
						for (Comments comment : errorInfo) {
							for (CustomDetailsUploadConfig customDetailsUploadConfig : header) {
								if (!customDetailsUploadConfig.getColumn_nm().equalsIgnoreCase("BH")
										&& customDetailsUploadConfig.getAttr_name().equals(comment.getKey())) {
									String columnIndex = CommonUtil
											.getColumnIndex(customDetailsUploadConfig.getColumn_nm());
									String ref = columnIndex + (customDetails.getRownum() + 1);
									removeColorsAndComment(ref, sheet, no_style);
									// should be refracted
									CellReference cellRef = new CellReference(ref);
									Row r = sheet.getRow(cellRef.getRow());
									Cell cell = r.getCell(cellRef.getCol());
									// set comment
									XSSFDrawing hpt = sheet.createDrawingPatriarch();
									XSSFComment comment1 = hpt.createCellComment(
											new XSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));

									comment1.setRow(r.getRowNum());
									comment1.setColumn(cell.getColumnIndex());
									comment1.setString(new XSSFRichTextString(comment.getMessage()));
									cell.setCellComment(comment1);
									cell.setCellStyle(style_error);

								}
							}

						}

					}
				}
			}

			// Draw Success and error Error excel
			// Summary summary = new Summary();
			summary.setTotalLineItemProcessed(String.valueOf(customDetailsRecords.size()));
			summary.setTotalFail(String.valueOf(failure));
			summary.setTotalSuccess(String.valueOf(success));
			summary.setWorkbook(workbook);

			log.info("END: *** excelRowsValidate Result *****" + summary.toString());

		} catch (BusinessException be) {
			log.info("Exception excelRowsValidate : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception excelRowsValidate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END excelRowsValidate");
		}
		return summary;
	}

	private Summary excelTemplateValidation(XSSFWorkbook workbook, XSSFSheet sheet, Summary summary) {
		CellStyle style_error = null;
		int headerRow = 0;
		List<CustomDetailsUploadConfig> template = null;
		List<Comments> commentsList = new ArrayList<Comments>();
		try {
			headerRow = ConstantUtil.row_header;
			int cellIndex = 0;
			Row header_row = sheet.getRow(headerRow);
			template = customDetailsRepo.getTemplateHeader();
			log.info("template :" + template.size());

			style_error = workbook.createCellStyle();
			style_error.setFillForegroundColor(IndexedColors.RED.index);
			style_error.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			CustomDetails cm = new CustomDetails();
			cm.setRownum(headerRow);
			String header_value = null;

			for (CustomDetailsUploadConfig customDetailsUploadConfig_header : template) {
				if (customDetailsUploadConfig_header.getAttr_desc().equalsIgnoreCase(ConstantUtil.CUSTOM_VESSEL_NAME)
						|| customDetailsUploadConfig_header.getAttr_desc()
								.equalsIgnoreCase(ConstantUtil.CUSTOM_VOY)) {
					continue;
				}
				Cell header_cell = header_row.getCell(cellIndex);
				if (header_cell != null) {
					header_value = CommonUtil.getCellData(header_cell.getAddress().formatAsString(), sheet);
				}

				String trimmedHeader = header_value.replace(" *", "");
				if (!customDetailsUploadConfig_header.getAttr_desc().equalsIgnoreCase(trimmedHeader)) {
					Comments comments = new Comments();
					comments.setKey(customDetailsUploadConfig_header.getAttr_name());
					comments.setMessage(customDetailsUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
					commentsList.add(comments);
				}
				
				if(header_row.getSheet().isColumnHidden(cellIndex)) {
					header_row.getSheet().setColumnHidden(cellIndex, false);
				}
				cellIndex++;
			}

			if (commentsList.size() > 0) {
				for (Comments comment : commentsList) {
					for (CustomDetailsUploadConfig manifestUploadConfig : template) {
						if (manifestUploadConfig.getAttr_name().equals(comment.getKey())) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + (headerRow + 1);
							CellReference cellRef = new CellReference(ref);
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
							cell.setCellStyle(style_error);
						}
					}
				}

			}

			// Draw Success and error Error excel
			summary.setTotalLineItemProcessed(String.valueOf(0));
			summary.setTotalFail(String.valueOf(0));
			summary.setTotalSuccess(String.valueOf(0));
			summary.setWorkbook(workbook);
			if (commentsList.size() > 0) {
				summary.setHeaderValid(false);
			} else {
				summary.setHeaderValid(true);
			}

		} catch (Exception e) {
			log.info("Exception excelTemplateValidation : ", e);
			try {
				summary.setTotalLineItemProcessed(String.valueOf(0));
				summary.setTotalFail(String.valueOf(0));
				summary.setTotalSuccess(String.valueOf(0));
				summary.setWorkbook(workbook);
				summary.setHeaderValid(false);
				if (commentsList.size() > 0) {
					for (Comments comment : commentsList) {
						for (CustomDetailsUploadConfig manifestUploadConfig : template) {
							if (manifestUploadConfig.getAttr_name().equals(comment.getKey())) {
								String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
								String ref = columnIndex + (headerRow + 1);
								CellReference cellRef = new CellReference(ref);
								Row r = sheet.getRow(cellRef.getRow());
								Cell cell = r.getCell(cellRef.getCol());
								// set comment
								XSSFDrawing hpt = sheet.createDrawingPatriarch();
								XSSFComment comment1 = hpt.createCellComment(
										new XSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));

								comment1.setRow(r.getRowNum());
								comment1.setColumn(cell.getColumnIndex());
								comment1.setString(new XSSFRichTextString(comment.getMessage()));
								cell.setCellComment(comment1);
								cell.setCellStyle(style_error);
							}
						}
					}

				}
				log.info("Exception excelTemplateValidation : ", e);
			} catch (Exception ex) {
				log.info("Exception excelTemplateValidation : ", e);
			}
		}
		return summary;
	}

	private Summary vesselDetailsValidation(XSSFWorkbook workbook, XSSFSheet sheet, String varNbr, Summary summary)
			throws BusinessException {
		VesselVoyValueObject vvObj = new VesselVoyValueObject();
		List<CustomDetailsUploadConfig> template = new ArrayList<CustomDetailsUploadConfig>();
		String dbVersion = "";
		try {
			log.info("START vesselDetailsValidation :vvCd :" + CommonUtility.deNull(varNbr));
			template = customDetailsRepo.getTemplateHeader();
			dbVersion = customDetailsRepo.getTemplateVersionNo();

			String excelVersionNo = workbook.getProperties().getCoreProperties().getRevision();

			if (CommonUtility.deNull(excelVersionNo).equalsIgnoreCase("")) {
				throw new BusinessException("Excel Revision Number Missing! Please use the correct template.");
			}
			
			if (!excelVersionNo.equals(dbVersion)) {
				throw new BusinessException("Please choose correct template for Customs Details");
			}

			PageDetails vesselCallDetails = customDetailsRepo.getVesselCallDetails(varNbr);
			List<Comments> commentsList = new ArrayList<Comments>();
			Comments vsl_comments = new Comments();
			Comments invoy_comments = new Comments();
			CellStyle style_error = workbook.createCellStyle();
			style_error.setFillForegroundColor(IndexedColors.RED.index);
			style_error.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			CellStyle no_style = workbook.createCellStyle();
			no_style.setFillPattern(FillPatternType.NO_FILL);
			no_style.setBorderBottom(BorderStyle.THIN);
			no_style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			no_style.setBorderRight(BorderStyle.THIN);
			no_style.setRightBorderColor(IndexedColors.BLACK.getIndex());
			no_style.setBorderTop(BorderStyle.THIN);
			no_style.setTopBorderColor(IndexedColors.BLACK.getIndex());
			boolean isVslFlag = false;
			boolean isInvoyFlag = false;
			for (CustomDetailsUploadConfig customDetailsUploadConfig_header : template) {
				if (customDetailsUploadConfig_header.getAttr_name().equals(ConstantUtil.vessel_name)) {

					CellReference cr = new CellReference(customDetailsUploadConfig_header.getColumn_nm() + 1);
					Row row_vsl_value = sheet.getRow(cr.getRow());
					if (row_vsl_value == null) {
						Row emptyRow = sheet.createRow(cr.getRow());
						Cell emptyCell = emptyRow.createCell(0);
						Comments comments = new Comments();
						comments.setKey(customDetailsUploadConfig_header.getAttr_name());
						comments.setMessage(ConstantUtil.ErrorMsg_invalidExcel);
						comments.setColumnNm(emptyCell.getAddress().formatAsString());
						commentsList.add(comments);
						break;
					}
					removeColorsAndComment(cr.formatAsString(), sheet, no_style);
					log.info(" dbVersion :" + dbVersion + ", excelVersionNo :" + excelVersionNo);
					if (!dbVersion.equalsIgnoreCase(excelVersionNo)) {
						Comments comments = new Comments();
						comments.setKey(customDetailsUploadConfig_header.getAttr_name());
						comments.setMessage(ConstantUtil.versionMismatch);
						comments.setColumnNm(cr.formatAsString());
						commentsList.add(comments);
					}

					String cellvalue = CommonUtil.getCellData(cr.formatAsString(), sheet);
					if (!customDetailsUploadConfig_header.getAttr_desc().equalsIgnoreCase(cellvalue)) {
						Comments comments = new Comments();
						comments.setKey(customDetailsUploadConfig_header.getAttr_name());
						comments.setMessage(customDetailsUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						comments.setColumnNm(cr.formatAsString());
						commentsList.add(comments);
					}

					// Row row_vsl_value = sheet.getRow(cr.getRow());
					Cell cell_vsl_value = row_vsl_value.getCell(1);
					if (cell_vsl_value == null) {
						cell_vsl_value = row_vsl_value.createCell(1);
					}
					boolean coloremoval1 = removeColorsAndComment(cell_vsl_value.getAddress().formatAsString(), sheet,
							no_style);
					log.info("Color and Comment Removal: " + coloremoval1);
					String vsl_cell_value = CommonUtil.getCellData(cell_vsl_value.getAddress().formatAsString(), sheet);
					if (vsl_cell_value == null) {
						Comments comments = new Comments();
						comments.setKey(customDetailsUploadConfig_header.getAttr_name());
						comments.setMessage(customDetailsUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						comments.setColumnNm(cell_vsl_value.getAddress().formatAsString());
						commentsList.add(comments);
					} else {
						// for vsl existence check
						vvObj.setVslName(vsl_cell_value);
						isVslFlag = true;
						vsl_comments.setKey(customDetailsUploadConfig_header.getAttr_name());
						vsl_comments.setMessage(ConstantUtil.ErrorMsg_vslNotExist);
						vsl_comments.setColumnNm(cell_vsl_value.getAddress().formatAsString());
					}
				} else if (customDetailsUploadConfig_header.getAttr_name().equals(ConstantUtil.CUSTOM_VOYAGE_NO)) {
					CellReference cr = new CellReference(customDetailsUploadConfig_header.getColumn_nm() + 2);
					removeColorsAndComment(cr.formatAsString(), sheet, no_style);
					String cellvalue = CommonUtil.getCellData(cr.formatAsString(), sheet);
					if (!customDetailsUploadConfig_header.getAttr_desc().equalsIgnoreCase(cellvalue)) {
						Comments comments = new Comments();
						comments.setKey(customDetailsUploadConfig_header.getAttr_name());
						comments.setMessage(customDetailsUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						comments.setColumnNm(cr.formatAsString());
						commentsList.add(comments);
					}
					Row row_invoy_value = sheet.getRow(cr.getRow());
					Cell cell_voy_value = row_invoy_value.getCell(1);
					if (cell_voy_value == null) {
						cell_voy_value = row_invoy_value.createCell(1);
					}
					String inVoy_cell_value = CommonUtil.getCellData(cell_voy_value.getAddress().formatAsString(),
							sheet);
					removeColorsAndComment(cr.formatAsString(), sheet, no_style);
					if (inVoy_cell_value == null) {
						Comments comments = new Comments();
						comments.setKey(customDetailsUploadConfig_header.getAttr_name());
						comments.setMessage(customDetailsUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						comments.setColumnNm(cell_voy_value.getAddress().formatAsString());
						commentsList.add(comments);
					} else {
						// for voy existence check
						if (inVoy_cell_value != null && inVoy_cell_value != "" && inVoy_cell_value.contains(".")) {
							try {
								BigDecimal cellDataStrRemZero = new BigDecimal(inVoy_cell_value);
								inVoy_cell_value = cellDataStrRemZero.stripTrailingZeros().toPlainString();
							} catch (Exception e) {
								log.info("Exception vesselDetailsValidation : ", e);
							}
						}

						vvObj.setVoyNo(inVoy_cell_value);
						isInvoyFlag = true;
						invoy_comments.setKey(customDetailsUploadConfig_header.getAttr_name());
						invoy_comments.setMessage(ConstantUtil.ErrorMsg_Invalid_Voy);
						invoy_comments.setColumnNm(cell_voy_value.getAddress().formatAsString());
					}
				}

			}

			// for vsl existence check
			if (isVslFlag) {
				if (vvObj.getVslName() != null
						&& (!vvObj.getVslName().equalsIgnoreCase(vesselCallDetails.getVesselName()))) {
					commentsList.add(vsl_comments);
					log.info(vvObj.getVslName() + " :Vessel aname Not matched with requested vvCd");
				}
			}
			if (isInvoyFlag) {
				if (vvObj.getVoyNo() != null && (!vvObj.getVoyNo().equalsIgnoreCase(vesselCallDetails.getVoyageNo()))) {
					if(!vvObj.getVoyNo().equalsIgnoreCase(vesselCallDetails.getInwardVoyNo()) && vvObj.getVoyNo().equalsIgnoreCase(vesselCallDetails.getOutVoyNo())) { // only inward not correct or not being filled
						summary.setCheckVoy("O");						 
					} else if(!vvObj.getVoyNo().equalsIgnoreCase(vesselCallDetails.getOutVoyNo()) && vvObj.getVoyNo().equalsIgnoreCase(vesselCallDetails.getInwardVoyNo())) {// only outward not correct or not being filled
						summary.setCheckVoy("I");
					} else if (!(vvObj.getVoyNo().equalsIgnoreCase(vesselCallDetails.getInwardVoyNo()) && vvObj.getVoyNo().equalsIgnoreCase(vesselCallDetails.getOutVoyNo()))){
						commentsList.add(invoy_comments);
						log.info(vvObj.getVoyNo() + " :voyNo Not matched with requested vvCd"); // inward/outward not same or both not being filled.
					} else if(vvObj.getVoyNo().equalsIgnoreCase(vesselCallDetails.getInwardVoyNo()) && vvObj.getVoyNo().equalsIgnoreCase(vesselCallDetails.getOutVoyNo()))	{ // both invoy and outvoy same but fills only 1
						summary.setCheckVoy("B");
					}
					
				}else {
					summary.setCheckVoy("B");
				}
			}
			if (commentsList.size() > 0) {
				for (Comments comment : commentsList) {

					for (CustomDetailsUploadConfig manifestUploadConfig : template) {
						if (manifestUploadConfig.getAttr_name().equals(comment.getKey())) {
							CellReference cellRef = new CellReference(comment.getColumnNm());
							removeColorsAndComment(cellRef.formatAsString(), sheet, no_style);
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
							cell.setCellStyle(style_error);
						}
					}
				}

			}
			// Draw Success and error Error excel
			String summaryUploaded = getTotalCntrForVessel(varNbr);
			summary.setSummaryUploaded(summaryUploaded);			
			summary.setTotalLineItemProcessed(String.valueOf(0));
			summary.setTotalFail(String.valueOf(0));
			summary.setTotalSuccess(String.valueOf(0));
			summary.setWorkbook(workbook);
			if (commentsList.size() > 0) {
				summary.setHeaderValid(false);
			} else {
				summary.setHeaderValid(true);
			}

		} catch (BusinessException be) {
			log.info("Exception vesselDetailsValidation : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			summary.setTotalLineItemProcessed(String.valueOf(0));
			summary.setTotalFail(String.valueOf(0));
			summary.setTotalSuccess(String.valueOf(0));
			summary.setHeaderValid(false);
			summary.setWorkbook(workbook);
			log.info("Exception vesselDetailsValidation : ", e);
		} finally {
			log.info("END vesselDetailsValidation");
		}
		return summary;
	}

	private boolean removeColorsAndComment(String ref, XSSFSheet sheet, CellStyle no_style) {
		try {
			log.info("START removeColorsAndComment" + " ref:" + CommonUtility.deNull(ref));
			CellReference cref = new CellReference(ref);
			int rowIndex = cref.getRow();
			int cellIndex = cref.getCol();
			XSSFCell cell = sheet.getRow(rowIndex).getCell(cellIndex);
			cell.removeCellComment();
			XSSFCellStyle cellColorStyle = cell.getCellStyle();
			XSSFColor cellColor = cellColorStyle.getFillForegroundXSSFColor();
			if (cellColor != null) {
				String color = cellColor.getARGBHex();
				log.info("color->" + color);
				if (color.equalsIgnoreCase(ConstantUtil.error_color)) {
					cell.setCellStyle(no_style);
				}
			} else {
				cell.setCellStyle(no_style);
			}
			return true;
		} catch (Exception e) {
			log.info("Exception removeColorsAndComment : ", e);
			return false;
		} finally {
			log.info("END removeColorsAndComment");
		}
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public boolean insertActionTrial(String varNbr, String typeCd, Summary summary, String lastTimestamp, String userId)
			throws BusinessException {
		String summaryDet = ConstantUtil.total_line_processed + summary.getTotalLineItemProcessed()
				+ ConstantUtil.total_success + summary.getTotalSuccess() + ConstantUtil.total_fail
				+ summary.getTotalFail();
		return customDetailsRepo.insertActionTrial(varNbr, typeCd, summaryDet, lastTimestamp, userId);
	}

	@Override
	public Resource excelProcessDownload(String seq_id, String type) throws BusinessException {
		String fileName = null;
		Resource res = null;
		try {
			log.info("START excelProcessDownload seq_id:" + CommonUtility.deNull(seq_id) + "type:"
					+ CommonUtility.deNull(type));
			Path rootLocation = null;
			BookingReferenceFileUploadDetails fileDetails = customDetailsRepo.getCustomDetailFileUploadDetails(seq_id);
			if (fileDetails != null) {
				rootLocation = Paths.get(folderPath + "/" + fileDetails.getVv_cd() + "/").toAbsolutePath().normalize();
				if (type.equalsIgnoreCase(ConstantUtil.type_Input)) {
					fileName = fileDetails.getAssigned_file_name();
				} else {
					fileName = fileDetails.getOutput_file_name();
				}
			} else {
				throw new FileSystemNotFoundException("File not found " + CommonUtility.deNull(fileName));
			}

			Path filePath = rootLocation.resolve(fileName).normalize();

			log.info("excelProcessDownload :" + filePath.toString());
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				res = resource;
			} else {
				throw new FileSystemNotFoundException("File not found " + CommonUtility.deNull(fileName));
			}

		} catch (BusinessException be) {
			log.info("Exception excelProcessDownload : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception excelProcessDownload : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END excelProcessDownload");
		}
		return res;
	}

	@Override
	public boolean insertActionTrialCuscar(String varNbr, String typeCd, SummaryCuscar summary, String lastTimestamp,
			String userId) throws BusinessException {
		String summaryDet = ConstantUtil.total_rcrd_rcv + summary.getTotalRecordRcv() +" / " + ConstantUtil.total_err_rcd
				+ summary.getTotalError() + " / " + ConstantUtil.total_rcrd_succ + summary.getTotalSuccess()
				+ " / " + ConstantUtil.total_rcrd_crtd + summary.getTotalCreated() + " / " + ConstantUtil.total_rcrd_updt
				+ summary.getTotalUpdated() + " / " + ConstantUtil.total_rcrd_del + summary.getTotalDeleted();
		return customDetailsRepo.insertActionTrial(varNbr, typeCd, summaryDet, lastTimestamp, userId);
	}
	
	@Override
	public String getVvcdFromVesselDetails(String vslName, String inVoyNo, String outVoyNo) throws BusinessException {
		return customDetailsRepo.getVvcdFromVesselDetails(vslName,inVoyNo,outVoyNo);
	}

}
