package sg.com.jp.generalcargo.service.impl;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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

import sg.com.jp.generalcargo.dao.BookingRefRepository;
import sg.com.jp.generalcargo.dao.CompanyRepository;
import sg.com.jp.generalcargo.dao.EdoRepository;
import sg.com.jp.generalcargo.dao.EsnRepository;
import sg.com.jp.generalcargo.dao.InwardCargoManifestRepository;
import sg.com.jp.generalcargo.domain.AccessCompanyValueObject;
import sg.com.jp.generalcargo.domain.BkRefActionTrailDetails;
import sg.com.jp.generalcargo.domain.BkRefUploadConfig;
import sg.com.jp.generalcargo.domain.BookRefvoyageOutwardValueObject;
import sg.com.jp.generalcargo.domain.BookingReference;
import sg.com.jp.generalcargo.domain.BookingReferenceFileUploadDetails;
import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.Comments;
import sg.com.jp.generalcargo.domain.CompanyValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.MiscDetail;
import sg.com.jp.generalcargo.domain.PageDetails;
import sg.com.jp.generalcargo.domain.Summary;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.Template;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.OutwardCargoBookingReferenceService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;

@Service("bookingReferenceService")
public class OutwardCargoBookingReferenceServiceImpl implements OutwardCargoBookingReferenceService {

	private static final Log log = LogFactory.getLog(OutwardCargoBookingReferenceServiceImpl.class);
	@Autowired
	private BookingRefRepository bookingRefRepo;

	@Autowired
	private CompanyRepository companyRepo;

	@Autowired
	private InwardCargoManifestRepository inwardCargoManifestRepo;

	@Autowired
	private EsnRepository esnRepo;

	@Autowired
	private InwardCargoManifestRepository manifestRepo;

	@Autowired
	EdoRepository edoRepo;

	@Value("${bookingref.file.bkrefupload.path}")
	String folderPath;

	@Override
	public List<BookRefvoyageOutwardValueObject> getVoyageName(String s6) throws BusinessException {

		return bookingRefRepo.getVoyageName(s6);
	}

	@Override
	public List<VesselVoyValueObject> getVslDetailsForDPE(String fetchVesselName, String fetchVoyageNbr, String s6)
			throws BusinessException {

		return bookingRefRepo.getVslDetailsForDPE(fetchVesselName, fetchVoyageNbr, s6);
	}

	@Override
	public List<VesselVoyValueObject> getVslDetails(String vvCd, String s6) throws BusinessException {

		return bookingRefRepo.getVslDetails(vvCd, s6);
	}

	@Override
	public List<BookingReferenceValueObject> getBKDetailsList(String s8, String s6, Criteria criteria)
			throws BusinessException {

		return bookingRefRepo.getBKDetailsList(s8, s6, criteria);
	}

	@Override
	public List<List<String>> getCargoType() throws BusinessException {

		return bookingRefRepo.getCargoType();
	}

	@Override
	public Map<String, String> getCargoCategoryCode_CargoCategoryName() throws BusinessException {

		return bookingRefRepo.getCargoCategoryCode_CargoCategoryName();
	}

	@Override
	public List<BookingReferenceValueObject> fetchBKDetails(String brno) throws BusinessException {

		return bookingRefRepo.fetchBKDetails(brno);
	}

	@Override
	public String chkCancelAmend(String brno, String userCoyCode, String string) throws BusinessException {

		return bookingRefRepo.chkCancelAmend(brno, userCoyCode, string);
	}

	@Override
	public boolean getCheckUserBookingReference(String coCd, String brno) throws BusinessException {

		return bookingRefRepo.getCheckUserBookingReference(coCd, brno);
	}

	@Override
	public CompanyValueObject getCompanyInfo(String shipperCoyCode) throws BusinessException {

		return companyRepo.getCompanyInfo(shipperCoyCode);
	}

	@Override
	public List<AccessCompanyValueObject> getAutPartyListOfVessel(String varno) throws BusinessException {

		return inwardCargoManifestRepo.getAutPartyListOfVessel(varno);
	}

	@Override
	public List<BookingReferenceValueObject> getBRVOList(String string) throws BusinessException {

		return bookingRefRepo.getBRVOList(string);
	}

	@Override
	public boolean isShowAllCargoCategoryCode(String userCoyCode) throws BusinessException {

		return bookingRefRepo.isShowAllCargoCategoryCode(userCoyCode);
	}

	@Override
	public String getNotShowCargoCategoryCode() throws BusinessException {

		return bookingRefRepo.getNotShowCargoCategoryCode();
	}

	@Override
	public String getVesselType(String bkRefNbr) throws BusinessException {

		return esnRepo.getVesselType(bkRefNbr);
	}

	@Override
	public String getCarCarrierVesselCode() throws BusinessException {

		return bookingRefRepo.getCarCarrierVesselCode();
	}

	@Override
	public String getCargoTypeNotShow() throws BusinessException {

		return bookingRefRepo.getCargoTypeNotShow();
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String updateBKForDPE(String bkRefNbr, String crgStatus, String varNo, String cntrNo, String cntrType,
			String cntrSize, String vslId, String outVoyNbr, String conrCode, String cargoType, String cargoCategory,
			String shpCrNo, String shpContactNo, String shpAddr, String shpNm, String bkWt, String bkVol,
			String bkNoOfPkg, String varPkgs, String varVol, String varWt, String portDis, String adpCustCd,
			String bkCmpCode, String user, boolean checkAmendConsignee, String conName, String consigneeAddr,
			String notifyParty, String notifyPartyAddr, String placeofDelivery, String placeofReceipt, String blNbr)
			throws BusinessException {
		return bookingRefRepo.updateBKForDPE(bkRefNbr, crgStatus, varNo, cntrNo, cntrType, cntrSize, vslId, outVoyNbr,
				conrCode, cargoType, cargoCategory, shpCrNo, shpContactNo, shpAddr, shpNm, bkWt, bkVol, bkNoOfPkg,
				varPkgs, varVol, varWt, portDis, adpCustCd, bkCmpCode, user, checkAmendConsignee, conName,
				consigneeAddr, notifyParty, notifyPartyAddr, placeofDelivery, placeofReceipt, blNbr, false);
	}

	@Override
	public String chkPortCode(String portDisc) throws BusinessException {

		return bookingRefRepo.chkPortCode(portDisc);
	}

	@Override
	public String chkCrNo(String esnDecl) throws BusinessException {

		return bookingRefRepo.chkCrNo(esnDecl);
	}

	@Override
	public String chkQuantity(String bkWt, String bkVol, String bkNoOfPkg, String varPkgs, String varVol, String varWt,
			String bkRefNbr) throws BusinessException {

		return bookingRefRepo.chkQuantity(bkWt, bkVol, bkNoOfPkg, varPkgs, varVol, varWt, bkRefNbr);
	}

	@Override
	public int retrieveMaxCargoTon(String varno) throws BusinessException {

		return bookingRefRepo.retrieveMaxCargoTon(varno);
	}

	@Override
	public List<AccessCompanyValueObject> listCompanyStart(String keyword, int start, int limit)
			throws BusinessException {

		return inwardCargoManifestRepo.listCompanyStart(keyword, start, limit);
	}

	@Override
	public List<AccessCompanyValueObject> listCompany(String keyword, Integer start, Integer limit)
			throws BusinessException {

		return inwardCargoManifestRepo.listCompany(keyword, start, limit);
	}

	@Override
	public List<BookingReferenceValueObject> getBrSearchDetails(String bkRefNo, String coCode)
			throws BusinessException {

		return bookingRefRepo.getBrSearchDetails(bkRefNo, coCode);
	}

	@Override
	public Hashtable<String, String> getVoyageDetails(String brn) throws BusinessException {

		return bookingRefRepo.getVoyageDetails(brn);
	}

	@Override
	public String cancelBK(String bkRefNbr, String userId) throws BusinessException {

		return bookingRefRepo.cancelBK(bkRefNbr, userId);
	}

	@Override
	public String getVslTypeCdByFullName(String vslName) throws BusinessException {

		return bookingRefRepo.getVslTypeCdByFullName(vslName);
	}

	@Override
	public String getDefaultCargoCategoryCode() throws BusinessException {

		return bookingRefRepo.getDefaultCargoCategoryCode();
	}

	@Override
	public String getCreateCustCdOfVessel(String varno) throws BusinessException {

		return inwardCargoManifestRepo.getCreateCustCdOfVessel(varno);
	}

	@Override
	public String chkBKCode(String bkRefNbr) throws BusinessException {

		return bookingRefRepo.chkBKCode(bkRefNbr);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String insertBK(String bkRefNbr, String crgStatus, String varNo, String cntrNo, String cntrType,
			String cntrSize, String outVoyNbr, String conrCode, String cargoType, String cargoCategory, String shpCrNo,
			String shpContactNo, String shpAddr, String shpNm, String bkWt, String bkVol, String bkNoOfPkg,
			String varPkgs, String varVol, String varWt, String portDis, String esnCustCd, String bkCreateCD,
			String user, String conName, String consigneeAddr, String notifyParty, String notifyPartyAddr,
			String placeofDelivery, String placeofReceipt, String blNbr) throws BusinessException {

		return bookingRefRepo.insertBK(bkRefNbr, crgStatus, varNo, cntrNo, cntrType, cntrSize, outVoyNbr, conrCode,
				cargoType, cargoCategory, shpCrNo, shpContactNo, shpAddr, shpNm, bkWt, bkVol, bkNoOfPkg, varPkgs,
				varVol, varWt, portDis, esnCustCd, bkCreateCD, user, conName, consigneeAddr, notifyParty,
				notifyPartyAddr, placeofDelivery, placeofReceipt, blNbr, false);
	}

	@Override
	public int getBKDetailsListCount(String varNo, String coCode, Criteria criteria) throws BusinessException {
		return bookingRefRepo.getBKDetailsListCount(varNo, coCode, criteria);
	}

	@Override
	public TableResult getPortCode(Criteria criteria) throws BusinessException {
		return bookingRefRepo.getPortCode(criteria);
	}

	@Override
	public List<String> indicationStatus(String vvCd) throws BusinessException {
		return edoRepo.indicationStatus(vvCd);
	}

	// excel
	@Override
	public XSSFWorkbook bkDetailExcelDownload(String vvCd, String coCode, Criteria criteria) throws BusinessException {
		XSSFWorkbook wb = new XSSFWorkbook();
		try {
			log.info("bkDetailExcelDownload : vvcd = " + CommonUtility.deNull(vvCd));
			XSSFSheet sheet = wb.createSheet("booking_reference");
			String version =  bookingRefRepo.getTemplateVersionNo();
			wb.getProperties().getCoreProperties().setRevision(version);

			Sheet sheet_hidden = wb.createSheet("Reference");

			int startRow = ConstantUtil.row_start;
			int headerRow = ConstantUtil.row_header;
			int vesselName_cell = ConstantUtil.vesselName_cell;
			int outwardVoy_cell = 2;

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
			c1_sheet_hidden.setCellValue(ConstantUtil.template_version_bkref);
			c2_sheet_hidden.setCellValue(version);
			c1_sheet_hidden.setCellStyle(style_header);
			c2_sheet_hidden.setCellStyle(style);

			List<BookingReference> bkrefDetails = bookingRefRepo.getBkRefDetails(vvCd);
			List<BkRefUploadConfig> template = bookingRefRepo.getBkTemplateHeader();
			PageDetails vesselCalldetails = manifestRepo.getVesselCallDetails(vvCd);

			for (BkRefUploadConfig bkRefUploadConfig_vessel_details : template) {
				if (bkRefUploadConfig_vessel_details.getAttr_name().equalsIgnoreCase(ConstantUtil.vessel_name)) {
					CellReference cellRef = new CellReference(
							bkRefUploadConfig_vessel_details.getColumn_nm() + vesselName_cell);
					Row r = sheet.createRow(cellRef.getRow());
					Cell cell = r.createCell(cellRef.getCol());
					cell.setCellValue(bkRefUploadConfig_vessel_details.getAttr_desc());
					cell.setCellStyle(style_header);
					Cell cell1 = r.createCell(cellRef.getCol() + 1);
					cell1.setCellValue(vesselCalldetails.getVesselName());
					cell1.setCellStyle(style_header);
				} else if (bkRefUploadConfig_vessel_details.getAttr_name()
						.equalsIgnoreCase(ConstantUtil.outward_voyage_no)) {
					CellReference cellRef = new CellReference(
							bkRefUploadConfig_vessel_details.getColumn_nm() + outwardVoy_cell);
					Row r = sheet.createRow(cellRef.getRow());
					Cell cell = r.createCell(cellRef.getCol());
					cell.setCellValue(bkRefUploadConfig_vessel_details.getAttr_desc());
					cell.setCellStyle(style_header);
					Cell cell1 = r.createCell(cellRef.getCol() + 1);
					cell1.setCellValue(vesselCalldetails.getOutVoyNo());
					cell1.setCellStyle(style_header);
				}
			}
			
			// To set limit same as data if more than default limit - NS AUG 2024
			int limit = bkrefDetails.size() >= ConstantUtil.limit ? (bkrefDetails.size() + 10) : ConstantUtil.limit;
			// TO draw rest of the excel lines
			for (int k = bkrefDetails.size(); k <= limit; k++) {
				BookingReference bkref = new BookingReference();
				bkrefDetails.add(bkref);
			}
			// Starts after row number 4 - NS AUG 2024
			limit = limit + ConstantUtil.row_start;
			Row header_r = sheet.createRow(headerRow);
			Cell cell = null;
			for (BkRefUploadConfig bkrefUploadConfig_header : template) {
				if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.action)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.bk_ref_nbr)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.bl_number)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.shipper_nm)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.shipper_nm_others)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);
					
				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.shipper_addr)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.cargo_type)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.bk_nbr_pkgs)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.variance_pkgs)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.bk_wt)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.variance_wt)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.bk_vol)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.variance_vol)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.port_of_discharge)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.declarant)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.consignee_nm)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.consignee_addr)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.notify_party_nm)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.notify_party_addr)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.place_of_delivery)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.place_of_receipt)) {
					cell = createCellvalues(sheet, header_r, bkrefUploadConfig_header.getColumn_nm(),
							bkrefUploadConfig_header.getAttr_desc(), style_header);

				}
			}
			// ---------------------------------------------------------------------------------------
			log.info("bkrefDetails count:" + bkrefDetails.size());
			for (BookingReference bookingReference : bkrefDetails) {
				Row r = sheet.createRow(startRow++);
				for (BkRefUploadConfig bkrefUploadConfig : template) {
					if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.action)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getAction(), style);

					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.bk_ref_nbr)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getBk_ref_nbr(), style);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.bl_number)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								CommonUtil.deNull(bookingReference.getBl_number()).toUpperCase(), style);
						XSSFDrawing hpt = sheet.createDrawingPatriarch();
						XSSFComment comment1 = hpt.createCellComment(
								new XSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
						comment1.setRow(r.getRowNum());
						comment1.setColumn(cell.getColumnIndex());
						comment1.setString(new XSSFRichTextString(ConstantUtil.blNumber_tooltip));
						cell.setCellComment(comment1);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.shipper_nm)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getShipper_nm(), style);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.shipper_nm_others)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getShipper_nm_others(), style);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.shipper_addr)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getShipper_addr(), style);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.cargo_type)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getCargoTypeDesc(), style);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.bk_nbr_pkgs)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getBk_nbr_pkgs(), style);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.variance_pkgs)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getVariance_pkgs(), style);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.bk_wt)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(), bookingReference.getBk_wt(),
								style);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.variance_wt)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getVariance_wt(), style);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.bk_vol)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getBk_vol(), style);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.variance_vol)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getVariance_vol(), style);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.port_of_discharge)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getPort_of_discharge(), style);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.declarant)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getDeclarant(), style);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.consignee_nm)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getConsignee_nm(), style);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.consignee_addr)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getConsignee_addr(), style);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.notify_party_nm)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getNotify_party_nm(), style);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.notify_party_addr)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getNotify_party_addr(), style);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.place_of_delivery)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getPlace_of_delivery(), style);
					} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.place_of_receipt)) {
						cell = createCellvalues(sheet, r, bkrefUploadConfig.getColumn_nm(),
								bookingReference.getPlace_of_receipt(), style);
					}
				}
			}

			// dropdown for action
			List<String> portList = manifestRepo.getPortListForExcelProcessing(false);
			List<String> portListWithName = manifestRepo.getPortListForExcelProcessing(true);
			for (BkRefUploadConfig bkrefUploadConfig : template) {

				if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.action)) {
					String[] actionArr = { ConstantUtil.action_NA, ConstantUtil.action_add, ConstantUtil.action_update,
							ConstantUtil.action_delete, ConstantUtil.action_custom_info };
					setDropDownForColumns(sheet, bkrefUploadConfig.getColumn_nm(), actionArr, limit);
				} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.cargo_type)) {
					List<String> cargoType = manifestRepo.getCargoTypeDropDown();
					String[] cargoTypeArr = new String[cargoType.size()];
					cargoTypeArr = cargoType.toArray(cargoTypeArr);
					setDropDownForColumns(sheet, bkrefUploadConfig.getColumn_nm(), cargoTypeArr, limit);

				} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.port_of_discharge)) {
					CellReference refld = new CellReference(bkrefUploadConfig.getColumn_nm());

					Row row_hsHeader = sheet_hidden.getRow(ConstantUtil.sheet_hidden_Header);
					Cell cell_Header = row_hsHeader.createCell(0);
					cell_Header.setCellValue(bkrefUploadConfig.getAttr_desc());
					cell_Header.setCellStyle(style_header);
					// sheet_hidden.autoSizeColumn(3);

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
					cell_Header.setCellValue(bkrefUploadConfig.getAttr_desc() + " (Name Ref)");
					cell_Header.setCellStyle(style_header);
					// sheet_hidden.autoSizeColumn(3);

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
					namedCell.setNameName("Reference3");
					int portListSize = portList.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$A$7:$A$" + portListSize);

					XSSFDataValidationHelper helper1 = (XSSFDataValidationHelper) sheet.getDataValidationHelper();
					XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) helper1
							.createFormulaListConstraint("Reference3");
					XSSFDataValidation validation1 = (XSSFDataValidation) helper1.createValidation(constraint1,
							new CellRangeAddressList(ConstantUtil.row_start, limit, refld.getCol(),
									refld.getCol()));
					validation1.setSuppressDropDownArrow(true);
					validation1.setShowErrorBox(true);
					sheet.addValidationData(validation1);
				} else if (bkrefUploadConfig.getAttr_name().equals(ConstantUtil.shipper_nm)) {
					List<String> consignee = manifestRepo.getConsigneee();
					consignee.add(ConstantUtil.dropdown_others);
					CellReference refshipper = new CellReference(bkrefUploadConfig.getColumn_nm());

					Row row_hsHeader = sheet_hidden.createRow(ConstantUtil.sheet_hidden_Header);
					Cell cell_Header = row_hsHeader.createCell(2);
					cell_Header.setCellValue(bkrefUploadConfig.getAttr_desc());
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
					namedCell.setNameName("Reference1");
					int shipperSize = consignee.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$C$7:$C$" + shipperSize);

					XSSFDataValidationHelper helper1 = (XSSFDataValidationHelper) sheet.getDataValidationHelper();
					XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) helper1
							.createFormulaListConstraint("Reference1");
					XSSFDataValidation validation1 = (XSSFDataValidation) helper1.createValidation(constraint1,
							new CellRangeAddressList(ConstantUtil.row_start, limit, refshipper.getCol(),
									refshipper.getCol()));
					validation1.setEmptyCellAllowed(false);
					validation1.setSuppressDropDownArrow(true);
					validation1.setShowErrorBox(true);
					sheet.addValidationData(validation1);

				} 
			}
			sheet_hidden.autoSizeColumn(0);
			sheet_hidden.autoSizeColumn(1);
			sheet_hidden.autoSizeColumn(2);
		} catch (BusinessException be) {
			log.info("Exception bkDetailExcelDownload : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception bkDetailExcelDownload : ", e);
			throw new BusinessException("M4201");
		}
		return wb;
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
		sheet.autoSizeColumn(columnIndex);
		return cell;
	}

	private void setDropDownForColumns(XSSFSheet sheet, String column_nm, String[] actionArr, int limit) {
		int col_index = CommonUtil.getColumnNumber(column_nm);
		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
		CellRangeAddressList addressList = new CellRangeAddressList(ConstantUtil.row_start, limit,
				col_index, col_index);
		XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
				.createExplicitListConstraint(actionArr);
		XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
		validation.setShowErrorBox(true);
		sheet.addValidationData(validation);
	}

	@Override
	public PageDetails getBkRefDocumentDetail(String vvCd) throws BusinessException {
		return bookingRefRepo.getBkRefDocumentDetail(vvCd);
	}
	
	public Boolean isBkSubmissionAllowed(Criteria criteria) throws BusinessException {
		return bookingRefRepo.isBkSubmissionAllowed(criteria);
	}

	@Override
	public String fileUpload(MultipartFile uploadFile, String vvCd) throws BusinessException {
		try {
			log.info("START : fileUpload:" + " Size:" + uploadFile.getSize() + "vvCd :" + CommonUtility.deNull(vvCd));
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
			String folderPathVvcd = folderPath + "/" + vvCd + "/";
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
		return manifestRepo.getTimeStamp();
	}

	// START CR FTZ - NS JUNE 2024
	@Override
	public Summary processBkrefDetails(MultipartFile uploadingFile,
			BookingReferenceFileUploadDetails bookingReferenceFileUploadDetails, String vvCd, String userId,
			String companyCode) throws BusinessException {
		XSSFWorkbook workbook = null;
		FileOutputStream fileOut = null;
		Summary summary = new Summary();
		String outputFileName = null;
		XSSFSheet sheet = null;
		try {
			log.info("START: processBkrefDetails Start " + " vvCd:" + CommonUtility.deNull(vvCd) + " userId:"
					+ CommonUtility.deNull(userId));
			Long seq_id = bookingRefRepo.insertBkrefExcelDetails(bookingReferenceFileUploadDetails);
			SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmm");

			log.info("Process vesselDetailsValidation Start  for vvCd :" + CommonUtility.deNull(vvCd));

			workbook = new XSSFWorkbook(OPCPackage.open(uploadingFile.getInputStream()));
			sheet = workbook.getSheetAt(0);
			summary = vesselDetailsValidation(workbook, sheet, vvCd, summary); // Vessel and outward no validation
			log.info("Process vesselDetailsValidation END  for vvCd :" + CommonUtility.deNull(vvCd) + " summary :"
					+ summary.toString());
			if (summary.isHeaderValid()) {
				log.info("Process excelTemplateValidation START  for vvCd :" + CommonUtility.deNull(vvCd));
				summary = excelTemplateValidation(workbook, sheet, summary); // header validation
				log.info("Process excelTemplateValidation END  for vvCd :" + CommonUtility.deNull(vvCd) + " summary :"
						+ summary.toString());

				if (summary.isHeaderValid()) {
					log.info("Process excelRowsValidate START  for vvCd :" + CommonUtility.deNull(vvCd));
					// excel validate
					summary = excelRowsValidate(workbook, sheet, vvCd, userId,
							bookingReferenceFileUploadDetails.getLast_modified_dttm(), summary, companyCode); // rows
																												// validation
					log.info("Process excelRowsValidate END  for vvCd :" + CommonUtility.deNull(vvCd) + " summary :"
							+ summary.toString());
				}
			}

			// 4)create error excel name
			outputFileName = ConstantUtil.bkref_filename + f.format(new Date()) + ConstantUtil.file_ext;

			// 5)need to copy file
			try {
				Path rootLocation = Paths.get(folderPath + "/" + vvCd + "/");
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
				log.info("Exception processBkrefDetails : ", e);
				throw new BusinessException("M4201");
			}

			// 6)update output file name
			boolean row = bookingRefRepo.updateBkExcelDetails(seq_id, outputFileName);
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

			log.info("END: *** processBkrefDetails Result *****" + summary.toString());
		} catch (BusinessException be) {
			log.info("Exception processBkrefDetails : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception processBkrefDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END processBkrefDetails");
			sheet = null;
			workbook = null;
		}
		return summary;
	}

	private Summary excelRowsValidate(XSSFWorkbook workbook, XSSFSheet sheet, String vvCd, String userId,
			String timeStamp, Summary summary, String companyCode) throws BusinessException {
		// XSSFWorkbook workbook = null;
		List<BookingReference> bkrefRecords = new ArrayList<BookingReference>();
		try {
			log.info("START excelRowsValidate :vvCd :" + CommonUtility.deNull(vvCd) + " userId : "
					+ CommonUtility.deNull(userId) + " timeStamp : " + CommonUtility.deNull(timeStamp) + " summary : "
					+ summary);

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

			List<BkRefUploadConfig> header = bookingRefRepo.getBkTemplateHeader();
			log.info(" ExcelProcessing :vvCd :" + CommonUtility.deNull(vvCd) + " , " + " getTemplateHeader :"
					+ header.size());

			// Dropdown
			List<String> cargoTypeDropdownList = manifestRepo.getCargoTypeDropDown();
			List<String> consigneeDropdownList = manifestRepo.getConsigneee();
			consigneeDropdownList.add(ConstantUtil.dropdown_others);// THIS value not coming from db
			List<String> portListDropdownList = manifestRepo.getPortListForExcelProcessing(false);

			// Find row count
			int rowCount = CommonUtil.getRowCount(sheet, rowStart, ConstantUtil.bk_ref_nbr_index, ConstantUtil.typeCd_BookingRef);
			int recStart = rowStart + 1;
			log.info(" rowCount :" + rowCount);
			BookingReference bkref = new BookingReference();
			List<Comments> commentsList = new ArrayList<Comments>();

			String cellData_na = "";
			boolean deleteflag = false;

			for (int i = recStart; i <= rowCount + 1; i++) {
				deleteflag = false;

				try {
					// For not applicable
					cellData_na = CommonUtil.getCellData(
							CommonUtil.getColumnIndex(ConstantUtil.action_index) + String.valueOf(i), sheet);
					if (cellData_na != null && cellData_na.equalsIgnoreCase(ConstantUtil.action_NA)) {
						continue;
					}
					boolean notCustomInfo = !cellData_na.equalsIgnoreCase(ConstantUtil.action_custom_info);

					bkref = new BookingReference();
					bkref.setLast_modify_dttm(timeStamp);
					commentsList = new ArrayList<Comments>();
					log.info("Started Excel Irearion  for  RowNo : " + i);

					// Delete

					for (BkRefUploadConfig bkUploadConfig : header) {
						if (bkUploadConfig.getAttr_name().equals(ConstantUtil.action)) {
							// Action Column
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							// error
							if (CommonUtil.deNull(cellData) == "") {
								if (!deleteflag
										&& bkUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(bkUploadConfig.getAttr_name());
									comments.setMessage(bkUploadConfig.getAttr_name() + ConstantUtil.mandatory);
									commentsList.add(comments);
								}
							} else {
								bkref.setAction(cellData);
							}
							
						} else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.bk_ref_nbr)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							removeColorsAndComment(ref, sheet, no_style);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase().trim();
							}
							if (CommonUtil.deNull(cellData) == "") {
								if (!deleteflag
										&& bkUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(bkUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_BkRef);
									commentsList.add(comments);
								}
							} else if (bkref.getAction() != null
									&& bkref.getAction().equalsIgnoreCase(ConstantUtil.action_add)) {
								if (!deleteflag && bookingRefRepo.bookingRefExist(upperCellData, vvCd) > 0) {
									String blno = upperCellData;
									if (bkrefRecords.stream()
											.filter(x -> x.getAction() != null
													&& x.getAction().equalsIgnoreCase(ConstantUtil.action_delete)
													&& x.getBk_ref_nbr().equalsIgnoreCase(blno))
											.findFirst().orElse(null) == null) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_BkNoDuplicate);
										commentsList.add(comments);
									} else {
										bkref.setBk_ref_nbr(upperCellData);
									}
								} else if (upperCellData.length() > 16) {
									Comments comments = new Comments();
									comments.setKey(bkUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_BkNoNoLength);
									commentsList.add(comments);
								} else {
									bkref.setBk_ref_nbr(upperCellData);
								}
							} else if (bkref.getAction() != null
									&& (bkref.getAction().equalsIgnoreCase(ConstantUtil.action_update)
											|| bkref.getAction().equalsIgnoreCase(ConstantUtil.action_custom_info))) {
								if (bookingRefRepo.bookingRefExist(upperCellData, vvCd) == 0) {
									Comments comments = new Comments();
									comments.setKey(bkUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_BkNbr_NotExist);
									commentsList.add(comments);
								} else {
									bkref.setBk_ref_nbr(upperCellData);
								}
							} else {
								bkref.setBk_ref_nbr(upperCellData);
							}
								
							
						} else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.cargo_type)) {
							// CargoType
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}

							removeColorsAndComment(ref, sheet, no_style);
							if (notCustomInfo) {
								if (CommonUtil.deNull(upperCellData) == "") {
									if (!deleteflag
											&& bkUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_CargoType);
										commentsList.add(comments);
									}
								} else if (!deleteflag && !cargoTypeDropdownList.contains(upperCellData)) {
									Comments comments = new Comments();
									comments.setKey(bkUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_InvalidItemFromDroDown);
									commentsList.add(comments);
								} else {
									String[] cargoType = cellDataSplit(upperCellData);
									bkref.setCargoTypeDesc(cargoType[0].trim());
								}
							}
						}  else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.bk_nbr_pkgs)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellDataStringType(ref, sheet);
							removeColorsAndComment(ref, sheet, no_style);
							if (notCustomInfo) {
								if (CommonUtil.deNull(cellData) == "") {
									if (!deleteflag
											&& bkUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_NoOfPkg);
										commentsList.add(comments);
									}
								} else if (!deleteflag && !CommonUtil.isInteger(cellData)) {
									log.info(" %%%%%% bErrorMsg_NonInteger :"+ cellData);
									Comments comments = new Comments();
									comments.setKey(bkUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_NonInteger);
									commentsList.add(comments);
								} else if (!deleteflag && CommonUtil.isNumeric(cellData)
										&& Double.parseDouble(cellData) <= 0) {
									Comments comments = new Comments();
									comments.setKey(bkUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Valid_noOfPkg);
									commentsList.add(comments);
									// START FTZ Validate Total - NS JULY 2024
								} if (!deleteflag && cellData.length() > 6) {
									Comments comments = new Comments();
									comments.setKey(bkUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_NoOfPkgLength);
									commentsList.add(comments);
								} else {
									BigDecimal cellDataStrRemZero = new BigDecimal(cellData);
									cellData = cellDataStrRemZero.stripTrailingZeros().toPlainString();
									bkref.setBk_nbr_pkgs(cellData);
								}
							}
						}else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.bk_wt)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							removeColorsAndComment(ref, sheet, no_style);
							if (notCustomInfo) {
								if (CommonUtil.deNull(cellData) == "") {
									if (!deleteflag
											&& bkUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(bkUploadConfig.getAttr_name() + ConstantUtil.mandatory);
										commentsList.add(comments);
									}

								} else if (!deleteflag && !CommonUtil.isNumeric(cellData)) {
									Comments comments = new Comments();
									comments.setKey(bkUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
									commentsList.add(comments);
								} else if (!deleteflag && CommonUtil.isNumeric(cellData)) {
									if (Double.parseDouble(cellData) < 10) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Valid_MinWeight);
										commentsList.add(comments);
									} else if (Double.parseDouble(cellData) > 20000000) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Valid_MaxWeight);
										commentsList.add(comments);
									} else if (cellData.length() > 8) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage("Weight" + ConstantUtil.ErrorMsg_8Length);
										commentsList.add(comments);
									} else {
										CommonUtil.setCellData(ref, sheet, CommonUtil.get2DecFromStr(cellData));
										bkref.setBk_wt(CommonUtil.get2DecFromStr(cellData));
									}

								} 
							}
						} else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.bk_vol)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							removeColorsAndComment(ref, sheet, no_style);
							if (notCustomInfo) {
								if (CommonUtil.deNull(cellData) == "") {
									if (!deleteflag
											&& bkUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_GrossM3);
										commentsList.add(comments);
									}
								} else if (!deleteflag && !CommonUtil.isNumeric(cellData)) {
									Comments comments = new Comments();
									comments.setKey(bkUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
									commentsList.add(comments);
								} else if (!deleteflag && CommonUtil.isNumeric(cellData)) {

									if (Double.parseDouble(cellData) < 0.01) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Valid_MinGrossM3);
										commentsList.add(comments);
									} else if (Double.parseDouble(cellData) > 9999.99) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Valid_MaxGrossM3);
										commentsList.add(comments);
									} else if (cellData.length() > 8) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage("Volume" + ConstantUtil.ErrorMsg_8Length);
										commentsList.add(comments);
									} else {
										CommonUtil.setCellData(ref, sheet, CommonUtil.get2DecFromStr(cellData));
										bkref.setBk_vol(CommonUtil.get2DecFromStr(cellData));
									}
								} 
							}
						} else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.variance_pkgs)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							removeColorsAndComment(ref, sheet, no_style);
							if (notCustomInfo) {
								if (CommonUtil.deNull(cellData) == "") {
									if (!deleteflag
											&& bkUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_Variance_pkg);
										commentsList.add(comments);
									}
								} else {
									if (cellData.length() > 5) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_VarianceLength);
										commentsList.add(comments);
									} else if (!CommonUtil.isNumeric(cellData)) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
										commentsList.add(comments);
									} else {
										BigDecimal cellDataStrRemZero = new BigDecimal(cellData);
										cellData = cellDataStrRemZero.stripTrailingZeros().toPlainString();
										bkref.setVariance_pkgs(cellData);
									}
								}
							}
						}else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.variance_wt)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							removeColorsAndComment(ref, sheet, no_style);
							if (notCustomInfo) {
								if (CommonUtil.deNull(cellData) == "") {
									if (!deleteflag
											&& bkUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_Variance_wt);
										commentsList.add(comments);
									}
								} else {
									if (cellData.length() > 5) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_VarianceLength);
										commentsList.add(comments);
									} else if (!CommonUtil.isNumeric(cellData)) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
										commentsList.add(comments);
									} else {
										BigDecimal cellDataStrRemZero = new BigDecimal(cellData);
										cellData = cellDataStrRemZero.stripTrailingZeros().toPlainString();
										bkref.setVariance_wt(cellData);
									}
								}
							}
						} else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.variance_vol)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							removeColorsAndComment(ref, sheet, no_style);
							if (notCustomInfo) {
								if (CommonUtil.deNull(cellData) == "") {
									if (!deleteflag
											&& bkUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_Variance_vol);
										commentsList.add(comments);
									}
								} else {
									if (cellData.length() > 5) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_VarianceLength);
										commentsList.add(comments);
									} else if (!CommonUtil.isNumeric(cellData)) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
										commentsList.add(comments);
									} else {
										BigDecimal cellDataStrRemZero = new BigDecimal(cellData);
										cellData = cellDataStrRemZero.stripTrailingZeros().toPlainString();
										bkref.setVariance_vol(cellData);
									}
								} 
							}
						} else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.port_of_discharge)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}

							removeColorsAndComment(ref, sheet, no_style);

							if(notCustomInfo) {
								if (!deleteflag && CommonUtil.deNull(upperCellData) == "") {
									Comments comments = new Comments();
									comments.setKey(bkUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_PortDischarge);
									commentsList.add(comments);
								} else if (!deleteflag && !portListDropdownList.contains(upperCellData)) {
									Comments comments = new Comments();
									comments.setKey(bkUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.Error_M21602);
									commentsList.add(comments);
								} else {
									if (CommonUtil.deNull(upperCellData) != "") {
										if (upperCellData.length() > 5) {
											Comments comments = new Comments();
											comments.setKey(bkUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_podLength);
											commentsList.add(comments);
										} else {
											String des_port = upperCellData;
											bkref.setPort_of_discharge(des_port.trim());
										}
									}
								}
							}
						} else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.declarant)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellDataStringType(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);

							if (CommonUtil.deNull(cellData) == "") {
								if (!deleteflag
										&& bkUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)
										&& notCustomInfo) {
									Comments comments = new Comments();
									comments.setKey(bkUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_Declarant);
									commentsList.add(comments);
								}

							} else {
								if (upperCellData.length() > 12) {
									Comments comments = new Comments();
									comments.setKey(bkUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_declarantLength);
									commentsList.add(comments);
								} else {
									bkref.setDeclarant(upperCellData);
								}
							}
						} else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.shipper_nm)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							removeColorsAndComment(ref, sheet, no_style);
							String shipperOthersCellData = CommonUtil.getCellData(
									CommonUtil.getColumnIndex(ConstantUtil.shipperOthers_index) + String.valueOf(i), sheet);
							if (CommonUtil.deNull(cellData) == "") {								
								if (!deleteflag
										&& bkUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
									
									if(!CommonUtil.deNull(shipperOthersCellData).isEmpty()) {
										bkref.setShipper_nm(ConstantUtil.others.toUpperCase());
									} else {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_Shipper_nm);
										commentsList.add(comments);
									}
								}

							} else if (!consigneeDropdownList.contains(cellData)) {
								Comments comments = new Comments();
								comments.setKey(bkUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_InvalidItemFromDroDown);
								commentsList.add(comments);
							}else {
								if (cellData.equalsIgnoreCase(ConstantUtil.others.toUpperCase())) {
									bkref.setShipper_nm(cellData);
								}  else {
									bkref.setShipper_nm(CommonUtil.consigneeSplit(cellData, "co_nm"));
									bkref.setShipper_cd(CommonUtil.consigneeSplit(cellData, "co_cd"));
								}
								
							}
								
							
						}  else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.shipper_nm_others)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellDataStringType(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
					
								String shipper = bkref.getShipper_nm();
								if (!deleteflag && (CommonUtil.deNull(upperCellData) == "" && shipper != null
										&& shipper.equalsIgnoreCase(ConstantUtil.others.toUpperCase()))) {
									Comments comments = new Comments();
									comments.setKey(bkUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_Shipper_nm);
									commentsList.add(comments);
								} else if (CommonUtil.deNull(shipper) != ""
										&& shipper.equalsIgnoreCase(ConstantUtil.others)) {
									if (upperCellData.length() > 70) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_ShipperOthers70Length);
										commentsList.add(comments);
									} else {
										bkref.setShipper_nm(upperCellData);
										bkref.setShipper_cd("OTHERS");
									}
								} 
						} else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.shipper_addr)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellDataStringType(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (!deleteflag && (CommonUtil.deNull(upperCellData) == "")) {
								Comments comments = new Comments();
								comments.setKey(bkUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_Shipper_Address);
								commentsList.add(comments);
							} else if (upperCellData.length() > 500) {
								Comments comments = new Comments();
								comments.setKey(bkUploadConfig.getAttr_name());
								comments.setMessage("Shipper Address" + ConstantUtil.ErrorMsg_500Length);
								commentsList.add(comments);
							} else {
								bkref.setShipper_addr(upperCellData);
							}
						} else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.consignee_nm)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellDataStringType(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (upperCellData.length() > 70) {
								Comments comments = new Comments();
								comments.setKey(bkUploadConfig.getAttr_name());
								comments.setMessage("Consignee Name" + ConstantUtil.ErrorMsg_70Length);
								commentsList.add(comments);
							} else {
								bkref.setConsignee_nm(upperCellData);
							}
						}else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.consignee_addr)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellDataStringType(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (upperCellData.length() > 500) {
								Comments comments = new Comments();
								comments.setKey(bkUploadConfig.getAttr_name());
								comments.setMessage("Consignee Address" + ConstantUtil.ErrorMsg_500Length);
								commentsList.add(comments);
							} else {
								bkref.setConsignee_addr(upperCellData);
							}
						} else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.notify_party_nm)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellDataStringType(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (upperCellData.length() > 70) {
								Comments comments = new Comments();
								comments.setKey(bkUploadConfig.getAttr_name());
								comments.setMessage("Notify Party Name" + ConstantUtil.ErrorMsg_70Length);
								commentsList.add(comments);
							} else {
								bkref.setNotify_party_nm(upperCellData);
							}
						} else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.notify_party_addr)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellDataStringType(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (upperCellData.length() > 500) {
								Comments comments = new Comments();
								comments.setKey(bkUploadConfig.getAttr_name());
								comments.setMessage("Notify Party Address" + ConstantUtil.ErrorMsg_500Length);
								commentsList.add(comments);
							} else {
								bkref.setNotify_party_addr(upperCellData);
							}
						} else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.place_of_delivery)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellDataStringType(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (upperCellData.length() > 70) {
								Comments comments = new Comments();
								comments.setKey(bkUploadConfig.getAttr_name());
								comments.setMessage("Place of Delivery" + ConstantUtil.ErrorMsg_70Length);
								commentsList.add(comments);
							} else {
								if (!(CommonUtil.deNull(cellData)).isEmpty()) {
									Pattern p = Pattern.compile("^a-z0-9//s", Pattern.CASE_INSENSITIVE);
									Matcher m = p.matcher(upperCellData);
									boolean found = m.find();
									if (found) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_SpecialChar);
										commentsList.add(comments);
									} else {
										bkref.setPlace_of_delivery(upperCellData);
									}
								}
							}
						} else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.place_of_receipt)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellDataStringType(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (upperCellData.length() > 70) {
								Comments comments = new Comments();
								comments.setKey(bkUploadConfig.getAttr_name());
								comments.setMessage("Place of Receipt" + ConstantUtil.ErrorMsg_70Length);
								commentsList.add(comments);
							} else {
								if (!CommonUtil.deNull(cellData).isEmpty()) {
									Pattern p = Pattern.compile("^a-z0-9//s", Pattern.CASE_INSENSITIVE);
									Matcher m = p.matcher(upperCellData);
									boolean found = m.find();
									if (found) {
										Comments comments = new Comments();
										comments.setKey(bkUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_SpecialChar);
										commentsList.add(comments);
									} else {
										bkref.setPlace_of_receipt(upperCellData);
									}
								}
							}
						} else if (bkUploadConfig.getAttr_name().equals(ConstantUtil.bl_number)) {
							String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellDataStringType(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (CommonUtil.deNull(cellData) == "") {
								if (!notCustomInfo && bookingRefRepo.checkATUDttm(vvCd)) {
									Comments comments = new Comments();
									comments.setKey(bkUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Missing_BLNbr);
									commentsList.add(comments);
								} else {
									bkref.setBl_number(upperCellData);
								}
							} if (upperCellData.length() > 100) {
								Comments comments = new Comments();
								comments.setKey(bkUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_BLNumberLength_Custom);
								commentsList.add(comments);
							} else {
								bkref.setBl_number(upperCellData);
							}
						}
					}
					
					bkref.setBk_create_cd(companyCode);
					bkref.setRownum(i - 1);// -1 is to take the exact row number
					bkref.setErrorInfo(commentsList);
					
					if (commentsList.size() > 0) {
						bkref.setMessage(ConstantUtil.error);
					} else {
						bkref.setMessage(ConstantUtil.success);
					}
					log.info("##################################################################### BKref Data :"
							+ bkref.toString());
					bkrefRecords.add(bkref);
				
				} catch (Exception e) {
					log.info("Exception excelRowsValidate : ", e);
					if (bkref == null) {
						bkref = new BookingReference();
					}
					bkref.setRownum(i - 1);// -1 is to take the exact row number
					bkref.setMessage(ConstantUtil.ErrorMsg_Common);
					bkref.setErrorInfo(commentsList);
					bkrefRecords.add(bkref);
					log.info("Exception row processing  row:" + i + " bkref :" + bkref.toString());
					log.info("Exception processing : ", e);
					continue;
				}

			}

			List<BookingReference> processResults = bookingRefRepo.insertBkRefData(bkrefRecords, vvCd, userId,
					companyCode);
			int success = 0;
			int failure = 0;
			int headerRow = ConstantUtil.row_header;
			Row r_remarks = sheet.getRow(headerRow);
			int lastRemrksColumn = r_remarks.getLastCellNum();
			Cell cell_remarks = r_remarks.createCell(lastRemrksColumn);
			cell_remarks.setCellValue(ConstantUtil.remarks);
			sheet.autoSizeColumn(cell_remarks.getColumnIndex());

			for (BookingReference bookingReference : processResults) {
//						log.info("writing to Excel iteration :" + bookingReference.toString());
				if (bookingReference.getMessage().equalsIgnoreCase(ConstantUtil.success)) {
					Row r = sheet.getRow(bookingReference.getRownum());
					Cell cell = r.createCell(lastRemrksColumn);
					cell.setCellValue(bookingReference.getMessage());
					cell.setCellStyle(style_success);
					success++;
				} else if (bookingReference.getMessage() != null) {
					Row r1 = sheet.getRow(bookingReference.getRownum());
					Cell c = r1.createCell(lastRemrksColumn);
					c.setCellValue(bookingReference.getMessage());
					c.setCellStyle(style_error);
					failure++;
					if (bookingReference.getErrorInfo().size() > 0) {

						List<Comments> errorInfo = bookingReference.getErrorInfo();
						for (Comments comment : errorInfo) {
							for (BkRefUploadConfig bkUploadConfig : header) {
								if (!bkUploadConfig.getColumn_nm().equalsIgnoreCase("V")
										&& bkUploadConfig.getAttr_name().equals(comment.getKey())) {
									String columnIndex = CommonUtil.getColumnIndex(bkUploadConfig.getColumn_nm());
									String ref = columnIndex + (bookingReference.getRownum() + 1);
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
//											log.info("### bkUploadConfig :comment.getKey() :" + comment.getKey()
//													+ ", RowNum:" + r.getRowNum() + " , column :" + cell.getColumnIndex()
//													+ " , Message :" + comment.getMessage());
									cell.setCellStyle(style_error);

								}
							}

						}

					}
				}
			}

			// Draw Success and error Error excel
			// Summary summary = new Summary();
			summary.setTotalLineItemProcessed(String.valueOf(bkrefRecords.size()));
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

	private Summary vesselDetailsValidation(XSSFWorkbook workbook, XSSFSheet sheet, String vvCd, Summary summary)
			throws BusinessException {
		VesselVoyValueObject vvObj = new VesselVoyValueObject();
		try {
			log.info("START vesselDetailsValidation :vvCd :" + CommonUtility.deNull(vvCd));
			List<BkRefUploadConfig> template = bookingRefRepo.getBkTemplateHeader();
			PageDetails vesselCallDetails = manifestRepo.getVesselCallDetails(vvCd);
			List<Comments> commentsList = new ArrayList<Comments>();
			Comments vsl_comments = new Comments();
			Comments outvoy_comments = new Comments();
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
			boolean isOutvoyFlag = false;
			String excelVersionNo = workbook.getProperties().getCoreProperties().getRevision();
			String dbVersion = manifestRepo.getTemplateVersionNo(ConstantUtil.bk_type_cd);
			for (BkRefUploadConfig bkrefUploadConfig_header : template) {
				if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.vessel_name)) {

					CellReference cr = new CellReference(bkrefUploadConfig_header.getColumn_nm() + 1);
					Row row_vsl_value = sheet.getRow(cr.getRow());
					if (row_vsl_value == null) {
						Row emptyRow = sheet.createRow(cr.getRow());
						Cell emptyCell = emptyRow.createCell(0);
						Comments comments = new Comments();
						comments.setKey(bkrefUploadConfig_header.getAttr_name());
						comments.setMessage(ConstantUtil.ErrorMsg_invalidExcel);
						comments.setColumnNm(emptyCell.getAddress().formatAsString());
						commentsList.add(comments);
						break;
					}
					removeColorsAndComment(cr.formatAsString(), sheet, no_style);
					log.info(" dbVersion :" + dbVersion + ", excelVersionNo :" + excelVersionNo);
					if (!dbVersion.equalsIgnoreCase(excelVersionNo)) {
						Comments comments = new Comments();
						comments.setKey(bkrefUploadConfig_header.getAttr_name());
						comments.setMessage(ConstantUtil.versionMismatch);
						comments.setColumnNm(cr.formatAsString());
						commentsList.add(comments);
					}

					String cellvalue = CommonUtil.getCellData(cr.formatAsString(), sheet);
					if (!bkrefUploadConfig_header.getAttr_desc().equalsIgnoreCase(cellvalue)) {
						Comments comments = new Comments();
						comments.setKey(bkrefUploadConfig_header.getAttr_name());
						comments.setMessage(bkrefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
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
						comments.setKey(bkrefUploadConfig_header.getAttr_name());
						comments.setMessage(bkrefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						comments.setColumnNm(cell_vsl_value.getAddress().formatAsString());
						commentsList.add(comments);
					} else {
						// for vsl existence check
						vvObj.setVslName(vsl_cell_value);
						isVslFlag = true;
						vsl_comments.setKey(bkrefUploadConfig_header.getAttr_name());
						vsl_comments.setMessage(ConstantUtil.ErrorMsg_vslNotExist);
						vsl_comments.setColumnNm(cell_vsl_value.getAddress().formatAsString());
					}
				} else if (bkrefUploadConfig_header.getAttr_name().equals(ConstantUtil.outward_voyage_no)) {
					CellReference cr = new CellReference(bkrefUploadConfig_header.getColumn_nm() + 2);
					removeColorsAndComment(cr.formatAsString(), sheet, no_style);
					String cellvalue = CommonUtil.getCellData(cr.formatAsString(), sheet);
					if (!bkrefUploadConfig_header.getAttr_desc().equalsIgnoreCase(cellvalue)) {
						Comments comments = new Comments();
						comments.setKey(bkrefUploadConfig_header.getAttr_name());
						comments.setMessage(bkrefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						comments.setColumnNm(cr.formatAsString());
						commentsList.add(comments);
					}
					Row row_outvoy_value = sheet.getRow(cr.getRow());
					Cell cell_outvoy_value = row_outvoy_value.getCell(1);
					if (cell_outvoy_value == null) {
						cell_outvoy_value = row_outvoy_value.createCell(1);
					}
					String outVoy_cell_value = CommonUtil.getCellData(cell_outvoy_value.getAddress().formatAsString(),
							sheet);
					removeColorsAndComment(cr.formatAsString(), sheet, no_style);
					if (outVoy_cell_value == null) {
						Comments comments = new Comments();
						comments.setKey(bkrefUploadConfig_header.getAttr_name());
						comments.setMessage(bkrefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						comments.setColumnNm(cell_outvoy_value.getAddress().formatAsString());
						commentsList.add(comments);
					} else {
						// for voy existence check
						if (outVoy_cell_value != null && outVoy_cell_value != "" && outVoy_cell_value.contains(".")) {
							try {
								BigDecimal cellDataStrRemZero = new BigDecimal(outVoy_cell_value);
								outVoy_cell_value = cellDataStrRemZero.stripTrailingZeros().toPlainString();
							} catch (Exception e) {
								log.info("Exception vesselDetailsValidation : ", e);
							}
						}

						vvObj.setVoyNo(outVoy_cell_value);
						isOutvoyFlag = true;
						outvoy_comments.setKey(bkrefUploadConfig_header.getAttr_name());
						outvoy_comments.setMessage(ConstantUtil.ErrorMsg_outvoyNotExist);
						outvoy_comments.setColumnNm(cell_outvoy_value.getAddress().formatAsString());
					}
				}

			}

			// for vsl existence check
			if (isVslFlag) {
				if (vvObj.getVslName() != null
						&& (!vvObj.getVslName().equalsIgnoreCase(vesselCallDetails.getVesselName()))) {
					vsl_comments.setMessage(vvObj.getVslName() + " :Vessel aname Not matched with requested vvCd");
					commentsList.add(vsl_comments);
					log.info(vvObj.getVslName() + " :Vessel aname Not matched with requested vvCd");
				}
			}
			if (isOutvoyFlag) {
				if (vvObj.getVoyNo() != null
						&& (!vvObj.getVoyNo().equalsIgnoreCase(vesselCallDetails.getOutVoyNo()))) {
					outvoy_comments.setMessage(vvObj.getVoyNo() + " :outvoyNo Not matched with requested vvCd");
					commentsList.add(outvoy_comments);
					log.info(vvObj.getVoyNo() + " :outvoyNo Not matched with requested vvCd");
				}
			}
			if (commentsList.size() > 0) {
				for (Comments comment : commentsList) {

					for (BkRefUploadConfig bkRefUploadConfig : template) {
						if (bkRefUploadConfig.getAttr_name().equals(comment.getKey())) {
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

	private Summary excelTemplateValidation(XSSFWorkbook workbook, XSSFSheet sheet, Summary summary)
			throws BusinessException {
		CellStyle style_error = null;
		int headerRow = 0;
		List<BkRefUploadConfig> template = null;
		List<Comments> commentsList = new ArrayList<Comments>();
		try {
			headerRow = ConstantUtil.row_header;
			int cellIndex = 0;
			Row header_row = sheet.getRow(headerRow);

			template = bookingRefRepo.getBkTemplateHeader();
			log.info("template :" + template.size());

			style_error = workbook.createCellStyle();
			style_error.setFillForegroundColor(IndexedColors.RED.index);
			style_error.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			BookingReference bkref = new BookingReference();
			bkref.setRownum(headerRow);
			String header_value = null;

			for (BkRefUploadConfig bkRefUploadConfig_header : template) {
				Cell header_cell = header_row.getCell(cellIndex);
				if (header_cell != null) {
					header_value = CommonUtil.getCellData(header_cell.getAddress().formatAsString(), sheet);
				}

				if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.action)) {
					if (!bkRefUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.bk_ref_nbr)) {
					if (!bkRefUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.shipper_nm)) {
					if (!bkRefUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.shipper_nm_others)) {
					if (!bkRefUploadConfig_header.getAttr_desc().trim().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.shipper_addr)) {
					if (!bkRefUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.cargo_type)) {
					if (!bkRefUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.bk_nbr_pkgs)) {
					if (!bkRefUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.variance_pkgs)) {
					if (!bkRefUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.bk_wt)) {
					if (!bkRefUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.variance_wt)) {
					if (!bkRefUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.bk_vol)) {
					if (!bkRefUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.variance_vol)) {
					if (!bkRefUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.port_of_discharge)) {
					if (!bkRefUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.declarant)) {
					if (!bkRefUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.consignee_nm)) {
					if (!bkRefUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.consignee_addr)) {
					if (!bkRefUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.notify_party_nm)) {
					if (!bkRefUploadConfig_header.getAttr_desc().trim().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.notify_party_addr)) {
					if (!bkRefUploadConfig_header.getAttr_desc().trim().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.place_of_delivery)) {
					if (!bkRefUploadConfig_header.getAttr_desc().trim().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.place_of_receipt)) {
					if (!bkRefUploadConfig_header.getAttr_desc().trim().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (bkRefUploadConfig_header.getAttr_name().equals(ConstantUtil.bl_number)) {
					if (!bkRefUploadConfig_header.getAttr_desc().trim().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(bkRefUploadConfig_header.getAttr_name());
						comments.setMessage(bkRefUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				}
			}

			if (commentsList.size() > 0) {
				for (Comments comment : commentsList) {
					for (BkRefUploadConfig bkRefUploadConfig : template) {
						if (bkRefUploadConfig.getAttr_name().equals(comment.getKey())) {
							String columnIndex = CommonUtil.getColumnIndex(bkRefUploadConfig.getColumn_nm());
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

		} catch (BusinessException be) {
			log.info("Exception excelTemplateValidation : ", be);
			throw new BusinessException(be.getMessage());
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
						for (BkRefUploadConfig bkRefUploadConfig : template) {
							if (bkRefUploadConfig.getAttr_name().equals(comment.getKey())) {
								String columnIndex = CommonUtil.getColumnIndex(bkRefUploadConfig.getColumn_nm());
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

	private String[] cellDataSplit(String cellData) {
		String[] token = cellData.split("\\(|-|\\)|�");
		return token;
	}
	
	@Override
	public Resource fileDownload(String seq_id, String type) throws BusinessException {
			String fileName = null;
			Resource res = null;
		try {
			log.info("START fileDownload seq_id:" + CommonUtility.deNull(seq_id) + "type:" + CommonUtility.deNull(type));
			Path rootLocation = null;// Paths.get(folderPath).toAbsolutePath().normalize();
			BookingReferenceFileUploadDetails fileDetails = bookingRefRepo.getCargoBkFileUploadDetails(seq_id);
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

			log.info("fileDownload :" + filePath.toString());
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				res = resource;
			} else {
				throw new FileSystemNotFoundException("File not found " + CommonUtility.deNull(fileName));
			}

		} catch (BusinessException be) {
			log.info("Exception fileDownload : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception fileDownload : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END fileDownload");
		}
		return res;
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public TableResult getBkActionTrail(Criteria criteria) throws BusinessException {
		return bookingRefRepo.getBkActionTrail(criteria);
	}
	
	@Override
	public BkRefActionTrailDetails bkActionTrailDetail(String bk_act_trl_id) throws BusinessException {
		return bookingRefRepo.getBkActionTrailDetail(bk_act_trl_id);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean insertActionTrail(String vvCd, Summary summary, String lastTimestamp, String userId)
			throws BusinessException {
		String summaryDet = ConstantUtil.total_line_processed + summary.getTotalLineItemProcessed()
		+ ConstantUtil.total_success + summary.getTotalSuccess() + ConstantUtil.total_fail
		+ summary.getTotalFail();
		return bookingRefRepo.insertActionTrail(vvCd, summaryDet, lastTimestamp, userId);
	}

	
	@Override
	public void updateBlNbr(Criteria criteria) throws BusinessException {
		bookingRefRepo.updateBlNbr(criteria);
	}
	
	@Override
	public String getVarcode(Criteria criteria) throws BusinessException {
		return bookingRefRepo.getVarcode(criteria);
	}
	// END CR FTZ - NS JUNE 2024



}
