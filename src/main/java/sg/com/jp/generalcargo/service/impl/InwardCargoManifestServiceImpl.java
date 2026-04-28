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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.util.ZipSecureFile;
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
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import sg.com.jp.generalcargo.dao.InwardCargoManifestRepository;
import sg.com.jp.generalcargo.domain.AccessCompanyValueObject;
import sg.com.jp.generalcargo.domain.AccountValueObject;
import sg.com.jp.generalcargo.domain.AdminFeeWaiverValueObject;
import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.CargoDimensionDeclaration;
import sg.com.jp.generalcargo.domain.CargoDimensionDetails;
import sg.com.jp.generalcargo.domain.CargoManifest;
import sg.com.jp.generalcargo.domain.CargoManifestFileUploadDetails;
import sg.com.jp.generalcargo.domain.CargoSelection;
import sg.com.jp.generalcargo.domain.Comments;
import sg.com.jp.generalcargo.domain.CompanyValueObject;
import sg.com.jp.generalcargo.domain.ConfigMsg;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.HSCode;
import sg.com.jp.generalcargo.domain.HatchBreakDownPageDetail;
import sg.com.jp.generalcargo.domain.HatchDetails;
import sg.com.jp.generalcargo.domain.HatchWisePackageDetail;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.ManifestActionTrailDetails;
import sg.com.jp.generalcargo.domain.ManifestCargoValueObject;
import sg.com.jp.generalcargo.domain.ManifestDetails;
import sg.com.jp.generalcargo.domain.ManifestPkgDimDetails;
import sg.com.jp.generalcargo.domain.ManifestUploadConfig;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.MiscDetail;
import sg.com.jp.generalcargo.domain.OscarJsonAdminWaiverVO;
import sg.com.jp.generalcargo.domain.PackageDimension;
import sg.com.jp.generalcargo.domain.PageDetails;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.Summary;
import sg.com.jp.generalcargo.domain.SystemConfigList;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.Template;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.InwardCargoManifestService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;

@Service("cargoManifestService")
public class InwardCargoManifestServiceImpl implements InwardCargoManifestService {

	@Value("${OSCARADMINWAIVER.URI}")
	private String waiverUrl;

	@Value("${cargomanifest.file.manifestupload.path}")
	String folderPath;

	private static final Log log = LogFactory.getLog(InwardCargoManifestServiceImpl.class);
	@Autowired
	private InwardCargoManifestRepository manifestRepo;

	public List<VesselVoyValueObject> getVesselVoy(String cocode) throws BusinessException {
		return manifestRepo.getVesselVoy(cocode);
	}

	public List<ManifestValueObject> getManifestList(String vvcode, String coCode, Criteria criteria) throws BusinessException {
		return manifestRepo.getManifestList(vvcode, coCode, criteria);
	}

	public boolean isManClose(String vesselCd) throws BusinessException {
		return manifestRepo.isManClose(vesselCd);
	}

	public boolean chkVslStat(String varno) throws BusinessException {
		return manifestRepo.chkVslStat(varno);
	}

	public List<VesselVoyValueObject> getVesselVoyList(String cocode, String vesselName, String voyageNumber, String terminal, String vslName) throws BusinessException {
		return manifestRepo.getVesselVoyList(cocode, vesselName, voyageNumber, terminal, vslName);
	}

	public List<VesselVoyValueObject> getVsNmVoy(String varNbr) throws BusinessException {
		return manifestRepo.getVsNmVoy(varNbr);
	}

	public VesselVoyValueObject getVesselInfo(String vv_cd) throws BusinessException {
		return manifestRepo.getVesselInfo(vv_cd);
	}

	public String getCategoryValue(String ccCd) throws BusinessException {
		return manifestRepo.getCategoryValue(ccCd);
	}

	public boolean checkAddManifest(String varno, String coCd) throws BusinessException {
		return manifestRepo.checkAddManifest(varno, coCd);
	}

	public boolean isShowAllCargoCategoryCode(String companyCode) throws BusinessException {
		return manifestRepo.isShowAllCargoCategoryCode(companyCode);
	}

	public List<Map<String, String>> getCategoryList() throws BusinessException {
		return manifestRepo.getCategoryList();
	}

	public Map<String, String> getCargoCategoryCode_CargoCategoryName() throws BusinessException {
		return manifestRepo.getCargoCategoryCode_CargoCategoryName();
	}

	public List<BookingReferenceValueObject> getBRVOList(String module) throws BusinessException {
		return manifestRepo.getBRVOList(module);
	}

	public String getCompanyCodeAllCargoCategory() throws BusinessException {
		return manifestRepo.getCompanyCodeAllCargoCategory();
	}

	public String getNotShowCargoCategoryCode() throws BusinessException {
		return manifestRepo.getNotShowCargoCategoryCode();
	}

	public String getCarCarrierVesselCode() throws BusinessException {
		return manifestRepo.getCarCarrierVesselCode();
	}

	public String getDefaultCargoCategoryCode() throws BusinessException {
		return manifestRepo.getDefaultCargoCategoryCode();
	}

	public String getCargoTypeNotShow() throws BusinessException {
		return manifestRepo.getCargoTypeNotShow();
	}

	public CompanyValueObject getCompanyInfo(String companyCode) throws BusinessException {
		return manifestRepo.getCompanyInfo(companyCode);
	}

	@Transactional(rollbackFor = BusinessException.class)
	public String MftInsertionForEnhancementHSCode(String distype, String addval, String coCd, String varno,
			String blno, String crgtyp, String hscd, String hsSubCodeFr, String hsSubCodeTo, String crgdesc,
			String mark, String nopkgs, String gwt, String gvol, String crgstat, String dgind, String stgind,
			String dop, String pkgtyp, String coname, String consigneeCoyCode, String poL, String poD, String poFD,
			String cntrtype, String cntrsize, String cntr1, String cntr2, String cntr3, String cntr4, String autParty,
			String adviseBy, String adviseDate, String adviseMode, String amendChargedTo, String waiveCharge,
			String waiveReason, String category, String deliveryToEPC, String userId, String selectedCargo,
			String conAddr, String notifyParty, String notifyPartyAddr, String placeDel, String placeReceipt,
			String shipperNm, String shipperAdd, String customHsCode, List<HsCodeDetails> multiHsCodeList, String blNoRoot, boolean isSplitBl) throws BusinessException {
		return manifestRepo.MftInsertionForEnhancementHSCode(distype, addval, coCd, varno, blno, crgtyp, hscd,
				hsSubCodeFr, hsSubCodeTo, crgdesc, mark, nopkgs, gwt, gvol, crgstat, dgind, stgind, dop, pkgtyp, coname,
				consigneeCoyCode, poL, poD, poFD, cntrtype, cntrsize, cntr1, cntr2, cntr3, cntr4, autParty, adviseBy,
				adviseDate, adviseMode, amendChargedTo, waiveCharge, waiveReason, category, deliveryToEPC, userId,
				selectedCargo, conAddr, notifyParty, notifyPartyAddr, placeDel, placeReceipt, shipperNm, shipperAdd,
				customHsCode, multiHsCodeList, blNoRoot, isSplitBl );
	}

	@Transactional(rollbackFor = BusinessException.class)
	public int captureWaiverAdviceRequest(String waiverRefNo, String userID, String waiverRefType, boolean resendReq,
			String adviceIdStr, String vvCd, String waiveReason) throws BusinessException {
		return manifestRepo.captureWaiverAdviceRequest(waiverRefNo, userID, waiverRefType, resendReq, adviceIdStr,
				vvCd, waiveReason);
	}

	@Override
	public AdminFeeWaiverValueObject invokeOscarWaiverRequest(int adviceId, String waiverRefNo, String userID,
			String waiverRefType) throws BusinessException {

		return manifestRepo.invokeOscarWaiverRequest(adviceId, waiverRefNo, userID, waiverRefType);
	}

	@Transactional(rollbackFor = BusinessException.class)
	public String insertMiscEvtLog(String type, String varno, String blno, String coCd) throws BusinessException {
		return manifestRepo.insertMiscEvtLog(type, varno, blno, coCd);
	}

	@Transactional(rollbackFor = BusinessException.class)
	public String MftInsertion(String distype, String addval, String coCd, String varno, String blno, String crgtyp,
			String hscd, String hsSubCodeFr, String hsSubCodeTo, String crgdesc, String mark, String nopkgs, String gwt,
			String gvol, String crgstat, String dgind, String stgind, String dop, String pkgtyp, String coname,
			String consigneeCoyCode, String poL, String poD, String poFD, String cntrtype, String cntrsize,
			String cntr1, String cntr2, String cntr3, String cntr4, String autParty, String crg_category,
			String deliveryToEPC, String UserID, String selectedCargo,
			String conAddr, String notifyParty, String notifyPartyAddr, String placeDel, String placeReceipt,
			String shipperNm, String shipperAdd, String customHsCode, List<HsCodeDetails> multiHsCodeList,
			String blNoRoot, boolean isSplitBl) throws BusinessException {
		return manifestRepo.MftInsertion(distype, addval, coCd, varno, blno, crgtyp, hscd, hsSubCodeFr, hsSubCodeTo,
				crgdesc, mark, nopkgs, gwt, gvol, crgstat, dgind, stgind, dop, pkgtyp, coname, consigneeCoyCode, poL,
				poD, poFD, cntrtype, cntrsize, cntr1, cntr2, cntr3, cntr4, autParty, crg_category, deliveryToEPC,
				UserID, selectedCargo, conAddr, notifyParty, notifyPartyAddr, placeDel, placeReceipt, shipperNm, shipperAdd,
				customHsCode, multiHsCodeList, blNoRoot, isSplitBl);
	}

	public String getPortName(String portcd) throws BusinessException {
		return manifestRepo.getPortName(portcd);
	}

	public String getPkgName(String pkgtype) throws BusinessException {
		return manifestRepo.getPkgName(pkgtype);
	}

	public boolean chkEdonbrPkgs(String seqno, String varno, String blno) throws BusinessException {
		return manifestRepo.chkEdonbrPkgs(seqno, varno, blno);
	}

	public boolean chkNbrEdopkgs(String seqno, String varno, String blno) throws BusinessException {
		return manifestRepo.chkNbrEdopkgs(seqno, varno, blno);
	}

	public boolean chkDNnbrPkgs(String seqno, String varno, String blno) throws BusinessException {
		return manifestRepo.chkDNnbrPkgs(seqno, varno, blno);
	}

	public boolean chkTDNnbrPkgs(String seqno, String varno, String blno) throws BusinessException {
		return manifestRepo.chkTDNnbrPkgs(seqno, varno, blno);
	}

	public boolean chkTnbrPkgs(String seqno, String varno, String blno) throws BusinessException {
		return manifestRepo.chkTnbrPkgs(seqno, varno, blno);
	}

	public String getClBjInd(String seqnbr) throws BusinessException {
		return manifestRepo.getClBjInd(seqnbr);
	}

	public ManifestValueObject mftRetrieve(String blno, String varno, String seqno) throws BusinessException {
		return manifestRepo.mftRetrieve(blno, varno, seqno);
	}

	public List<ManifestValueObject> getAddcrgList() throws BusinessException {
		return manifestRepo.getAddcrgList();
	}

	public List<ManifestValueObject> getHSCodeList(String status, String query) throws BusinessException {
		return manifestRepo.getHSCodeList(status, query);
	}

	public String getVesselTypeByVslNm(String vslNm) throws BusinessException {
		return manifestRepo.getVesselTypeByVslNm(vslNm);
	}

	public boolean getUserAdminVessel(String coCd, String vesselNameLogin, String voyLogin, String blnum) throws BusinessException {
		return manifestRepo.getUserAdminVessel(coCd, vesselNameLogin, voyLogin, blnum);
	}

	public List<AccessCompanyValueObject> getAutPartyListOfVessel(String vvcode) throws BusinessException {
		return manifestRepo.getAutPartyListOfVessel(vvcode);
	}

	public List<AccountValueObject> getListAmendmentChargedTo(String varno) throws BusinessException {
		return manifestRepo.getListAmendmentChargedTo(varno);
	}

	public String getCreateCustCdOfVessel(String vvcode) throws BusinessException {
		return manifestRepo.getCreateCustCdOfVessel(vvcode);
	}

	public boolean checkDisbaleOverSideFroDPE(String varno) throws BusinessException {
		return manifestRepo.checkDisbaleOverSideFroDPE(varno);
	}

	public int retrieveMaxCargoTon(String vvCd) throws BusinessException {
		return manifestRepo.retrieveMaxCargoTon(vvCd);
	}

	public List<AccessCompanyValueObject> listCompanyStart(String keyword, Integer start, Integer limit) throws BusinessException {
		return manifestRepo.listCompanyStart(keyword, start, limit);
	}

	public List<AccessCompanyValueObject> listCompany(String keyword, Integer start, Integer limit) throws BusinessException {
		return manifestRepo.listCompany(keyword, start, limit);
	}

	public List<HSCode> getHSSubCodeList(String hsCd) throws BusinessException {
		return manifestRepo.getHSSubCodeList(hsCd);
	}

	public List<ManifestValueObject> getPkgList() throws BusinessException {
		return manifestRepo.getPkgList();
	}

	public List<EsnListValueObject> getPkgList(String text) throws BusinessException {
		return manifestRepo.getPkgList(text);
	}

	public List<ManifestValueObject> getPortList() throws BusinessException {
		return manifestRepo.getPortList();
	}

	public List<ManifestValueObject> getPortList(String portCd, String portName) throws BusinessException {
		return manifestRepo.getPortList(portCd, portName);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String MftUpdationWhenClosedDPE(String usrid, String coCd, String seqno, String varno, String blno,
			String crgdesc, String mark, String adviseBy, String adviseDate, String adviseMode, String amendChargedTo,
			String waiveCharge, String waiveReason, String hscd, String hsSubCodeFr, String hsSubCodeTo, String coname,
			String consigneeCoyCode, String selectedCargo,
			String conAddr, String notifyParty, String notifyPartyAddr, String placeDel, String placeReceipt,
			String shipperNm, String shipperAdd, String customHsCode, List<HsCodeDetails> multiHsCodeList) throws BusinessException {

		return manifestRepo.MftUpdationWhenClosedDPE(usrid, coCd, seqno, varno, blno, crgdesc, mark, adviseBy,
				adviseDate, adviseMode, amendChargedTo, waiveCharge, waiveReason, hscd, hsSubCodeFr, hsSubCodeTo,
				coname, consigneeCoyCode, selectedCargo, conAddr, notifyParty, notifyPartyAddr, placeDel, placeReceipt, shipperNm, shipperAdd,
				customHsCode, multiHsCodeList );
	}

	@Transactional(rollbackFor = BusinessException.class)
	public String MftUpdationForEnhancementHSCode(String userID, String coCd, String seqno, String varno, String blno,
			String crgtype, String hscd, String hsSubCodeFr, String hsSubCodeTo, String crgdesc, String mark,
			String nop, String gwt, String gvol, String crgstat, String dgind, String stgind, String dopind,
			String pkgtype, String coname, String consigneeCoyCode, String pol, String pod, String pofd,
			String cntrtype, String cntrsize, String cntr1, String cntr2, String cntr3, String cntr4, String autParty,
			String adviseBy, String adviceDttm, String adviseMode, String amendChargedTo, String waiveCharge,
			String waiveReason, String category, String deliveryToEPC, String selectedCargo,
			String conAddr, String notifyParty, String notifyPartyAddr, String placeDel, String placeReceipt,
			String shipperNm, String shipperAdd, String customHsCode, List<HsCodeDetails> multiHsCodeList) throws BusinessException {
		return manifestRepo.MftUpdationForEnhancementHSCode(userID, coCd, seqno, varno, blno, crgtype, hscd,
				hsSubCodeFr, hsSubCodeTo, crgdesc, mark, nop, gwt, gvol, crgstat, dgind, stgind, dopind, pkgtype,
				coname, consigneeCoyCode, pol, pod, pofd, cntrtype, cntrsize, cntr1, cntr2, cntr3, cntr4, autParty,
				adviseBy, adviceDttm, adviseMode, amendChargedTo, waiveCharge, waiveReason, category, deliveryToEPC,
				selectedCargo, conAddr, notifyParty, notifyPartyAddr, placeDel, placeReceipt, shipperNm, shipperAdd,
				customHsCode, multiHsCodeList );
	}

	public AdminFeeWaiverValueObject updateWaiverAdvice(AdminFeeWaiverValueObject adminFeeWaiverVO, String userID) throws BusinessException {
		return manifestRepo.updateWaiverAdvice(adminFeeWaiverVO, userID);
	}

	public String getScheme(String voy_nbr) throws BusinessException {
		return manifestRepo.getScheme(voy_nbr);
	}

	public String getSchemeInd(String voy_nbr) throws BusinessException {
		return manifestRepo.getSchemeInd(voy_nbr);
	}

	public List<ManifestValueObject> getCargoType() throws BusinessException {
		return manifestRepo.getCargoType();
	}

	public List<String> getSAacctno(String voy_nbr) throws BusinessException {
		return manifestRepo.getSAacctno(voy_nbr);
	}

	public String getBPacctnbr(String voy_nbr, String seqno) throws BusinessException {
		return manifestRepo.getBPacctnbr(voy_nbr, seqno);
	}

	public List<ManifestValueObject> getABacctno(String voy_nbr) throws BusinessException {
		return manifestRepo.getABacctno(voy_nbr);
	}

	public List<ManifestValueObject> getABacctnoForSA(String voy_nbr) throws BusinessException {
		return manifestRepo.getABacctnoForSA(voy_nbr);
	}

	public String getSchemeName(String voy_nbr) throws BusinessException {
		return manifestRepo.getSchemeName(voy_nbr);
	}

	public String getVCactnbr(String voy_nbr) throws BusinessException {
		return manifestRepo.getVCactnbr(voy_nbr);
	}

	public String getABactnbr(String voy_nbr) throws BusinessException {
		return manifestRepo.getABactnbr(voy_nbr);
	}

	@Transactional(rollbackFor = BusinessException.class)
	public void MftAssignBillUpdate(String voy_nbr, String acctnbr, String seqno, String userid) throws Exception {
		manifestRepo.MftAssignBillUpdate(voy_nbr, acctnbr, seqno, userid);
	}

	@Transactional(rollbackFor = BusinessException.class)
	public void MftAssignVslUpdate(String voy_nbr, String status, String userid) throws BusinessException {
		manifestRepo.MftAssignVslUpdate(voy_nbr, status, userid);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void MftAssignCargoCategoryCargoTypeUpdate(String voy_nbr, String cargoCategory, String cargoType,
			String seqno, String userid) throws BusinessException {
		manifestRepo.MftAssignCargoCategoryCargoTypeUpdate(voy_nbr, cargoCategory, cargoType, seqno, userid);

	}

	@Override
	public String MftAssignCrgvalCheck(String voy_nbr, String seqno) throws BusinessException {
		return manifestRepo.MftAssignCrgvalCheck(voy_nbr, seqno);
	}

	@Override
	public List<ManifestCargoValueObject> getMftAssignCargo() throws BusinessException {
		return manifestRepo.getMftAssignCargo();
	}

	@Override
	public List<MiscDetail> getCargoSelectionList(Criteria criteria) throws BusinessException {
		return manifestRepo.getCargoSelectionList(criteria);
	}

	// NEW Features Addition BY NS

	@Override
	public String fileUpload(MultipartFile uploadFile, String vvCd) {
		try {
			log.info("START : fileUpload:" +  " Size:" + uploadFile.getSize() + "vvCd :"+ CommonUtility.deNull(vvCd));
			if (uploadFile.getOriginalFilename().indexOf("/") >= 0
					|| uploadFile.getOriginalFilename().indexOf("\\") >= 0) {
				log.info("File name validation failed!");
				return null;
			}
			String extension = FilenameUtils.getExtension(uploadFile.getOriginalFilename());
			UUID uuid = UUID.randomUUID();
			String fileName = uuid.toString() + "." + extension;
			log.info("assignedFileName:" + fileName);
			if (fileName.indexOf("/") >= 0
								|| fileName.indexOf("\\") >= 0) {
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

	private Summary excelRowsValidate(XSSFWorkbook workbook,XSSFSheet sheet,String vvCd, String userId, String timeStamp,Summary summary, String companyCode, boolean isSplitBL) throws BusinessException {
		//XSSFWorkbook workbook = null;
		List<CargoManifest> manifestRecords = new ArrayList<CargoManifest>();
		List<MiscDetail> miscDetailNbrPkgListAdd = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailNbrPkgListUpdate = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailNbrPkgListDelete= new ArrayList<MiscDetail>();
		List<MiscDetail> miscNbrPkgsMainAdd = new ArrayList<MiscDetail>();
		List<MiscDetail> miscNbrPkgsMainUpdate = new ArrayList<MiscDetail>();
		List<MiscDetail> miscNbrPkgsMainUpdateHS = new ArrayList<MiscDetail>();
		List<MiscDetail> miscNbrPkgsdeleteHS = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailWtListAdd = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailWtListUpdate = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailWtListDelete = new ArrayList<MiscDetail>();
		List<MiscDetail> miscNbrWtMainAdd = new ArrayList<MiscDetail>();
		List<MiscDetail> miscNbrWtMainUpdate = new ArrayList<MiscDetail>();
		List<MiscDetail> miscNbrWtMainUpdateHs = new ArrayList<MiscDetail>();
		List<MiscDetail> miscNbrWtMainDeleteHs = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailVolListAdd = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailVolListUpdate = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailVolListDelete = new ArrayList<MiscDetail>();
		List<MiscDetail> miscNbrVolMainAdd = new ArrayList<MiscDetail>();
		List<MiscDetail> miscNbrVolMainUpdate = new ArrayList<MiscDetail>();
		List<MiscDetail> miscNbrVolMainUpdateHs = new ArrayList<MiscDetail>();
		List<MiscDetail> miscNbrVolDeleteHs = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailHSCodelListAdd = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailCustomHSCodelList = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailSubHsCodeListAdd = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailSubHsCodeListUpdate = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailSubHsCodeListDel = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailSubOldHsCodeListDel = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailSubCustomHSCodeListDel = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailSubCargoDescListDel = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailHSCodelListUpdate = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailsubOldHSCodelListUpdate = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailCrgDescMainAdd = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailCrgDescMainUpdate = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailCrgDescSubUpdate = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailCrgDescSubAdd = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailCustomMainUpdate = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailCustomMainAdd = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailNbrPkgsMainUpdate = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailWtMainUpdate = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailVolMainUpdate = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailCustomSubUpdate = new ArrayList<MiscDetail>();
		List<MiscDetail> miscDetailCustomSubAdd = new ArrayList<MiscDetail>();
		List<String> mainHaveError = new ArrayList<String>();
		List<String> subHaveError = new ArrayList<String>();
		List<String> mainBLNbrListAdd = new ArrayList<String>();
		List<String> mainBLNbrListUpdate = new ArrayList<String>();
		List<String> mainBLNbrListSubUpdate = new ArrayList<String>();
		boolean cannotDeleteSub = false;
		try {
			log.info("START excelRowsValidate :vvCd :" + CommonUtility.deNull(vvCd) + " userId : " + CommonUtility.deNull(userId) 
			+ " timeStamp : " + CommonUtility.deNull(timeStamp) + " summary : " + summary );
			int headerRow1 = ConstantUtil.row_header;

			//workbook = new XSSFWorkbook(OPCPackage.open(file.getInputStream()));
			//XSSFSheet sheet = workbook.getSheetAt(0);
			Row header_row = sheet.getRow(headerRow1);

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
			List<ManifestUploadConfig> header = new ArrayList<ManifestUploadConfig>();
			List<ManifestUploadConfig> hatch_header = new ArrayList<ManifestUploadConfig>();
			
			if(isSplitBL) {
				header = manifestRepo.getSplitBlTemplateHeader();
				hatch_header = manifestRepo.getSplitBlHatchTemplate();
			} else {
				header = manifestRepo.getTemplateHeader();
				hatch_header = manifestRepo.getHatchTemplate();
			}
			

			int noOfHatch = manifestRepo.noOfHatchesOnPageLoad(vvCd);
			log.info(" ExcelProcessing :vvCd :" + CommonUtility.deNull(vvCd) + " , " + " getTemplateHeader :" + header.size()
					+ ", hatch_header :" + hatch_header.size() + ", noOfHatch :" + noOfHatch);

			// Dropdown
			List<String> cargoTypeDropdownList = manifestRepo.getCargoTypeDropDown();
			List<String> hsCodeSubCodeDropdownList = manifestRepo.getHs_code_sub_code();
			List<MiscDetail> cargoSelectionDropdownList = manifestRepo.getCargoSelectionDropdown();
			List<String> packagingTypeDropdownList = manifestRepo.getPackagingTypeDropDown();
			List<String> consigneeDropdownList = manifestRepo.getConsigneee();
			consigneeDropdownList.add(ConstantUtil.dropdown_others);// THIS value not coming from db
			List<String> portListDropdownList = manifestRepo.getPortListForExcelProcessing(false);

			// mandatory validadtin for cargo selection
			List<String> hsSubCodeValues_mandatoryCheck = new ArrayList<String>();
			hsSubCodeValues_mandatoryCheck.add(ConstantUtil.hs_code_731315);
			hsSubCodeValues_mandatoryCheck.add(ConstantUtil.hs_code_722729);
			hsSubCodeValues_mandatoryCheck.add(ConstantUtil.hs_code_720102);
			hsSubCodeValues_mandatoryCheck.add(ConstantUtil.hs_code_720304);

			// Find row count
			int rowCount = CommonUtil.getRowCount(sheet, rowStart, ConstantUtil.manifest_bl_nbr_index,
					ConstantUtil.typeCd_Manifest);
			int recStart = rowStart + 1;
			log.info(" rowCount :" + rowCount);
			CargoManifest cm = new CargoManifest();
			List<Comments> commentsList = new ArrayList<Comments>();
			CargoSelection cargoSelect = null;

			String cellData_na = "";
			String cellData_bill = "";
			boolean deleteflag = false;
			boolean chkDGInd = true;
			// for remarks REMOVAL - starts
			for (Cell cell : header_row) {
				if (cell!=null && cell.getStringCellValue().equalsIgnoreCase(ConstantUtil.remarks)) {
//					Comments comments = new Comments();
//					comments.setKey(ConstantUtil.remarks_key);
//					comments.setMessage(ConstantUtil.remarks_msg);
//					comments.setColumnNm(cell.getAddress().formatAsString());
//					commentsList.add(comments);
					CellReference cr = new CellReference(cell);
					// Remove remarks Column
					// Find row count
					int rowCount1 = CommonUtil.getRowCount(sheet, rowStart, ConstantUtil.manifest_bl_nbr_index,
							ConstantUtil.typeCd_Manifest);
					for (int i = headerRow1; i < rowCount1; i++) {
						Row r = sheet.getRow(i);
						Cell c = r.getCell(cr.getCol());
						if (c != null) {
							r.removeCell(c);
						}
					}
				}
			}
			// for remarks - ends

			// START FTZ CR - Get Total Packages, Weight  and Vol - NS JULY 2024
			for (int i = recStart; i <= rowCount + 1; i++) {
				MiscDetail miscDtl = new MiscDetail();
				
				String cellData_ActionHsCode = CommonUtil.deNull(CommonUtil.getCellData(
						CommonUtil.getColumnIndex(ConstantUtil.action_index) + String.valueOf(i), sheet));
				String cellData_BLNbr = CommonUtil.getCellData(
						CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_billofLanding_index :ConstantUtil.billofLanding_index) + String.valueOf(i), sheet);
				String cellData_NbrPkg = CommonUtil.getCellData(
						CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_pkgNbr_index : ConstantUtil.pkgNbr_index) + String.valueOf(i), sheet);
				String afterDotPkg = CommonUtil.deNull(cellData_NbrPkg).indexOf(".") > 0 ?  CommonUtil.deNull(cellData_NbrPkg).substring(CommonUtil.deNull(cellData_NbrPkg).indexOf(".") + 1) : "0";				
				cellData_NbrPkg = CommonUtil.deNull(cellData_NbrPkg).isEmpty() ? cellData_NbrPkg : afterDotPkg.length()>1 ? cellData_NbrPkg : cellData_NbrPkg.replace(".0", "");
				String cellData_Wt = CommonUtil.getCellData(
						CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_grossWt_index : ConstantUtil.grossWt_index) + String.valueOf(i), sheet);
				String afterDotWt = CommonUtil.deNull(cellData_Wt).indexOf(".") > 0 ?  CommonUtil.deNull(cellData_Wt).substring(CommonUtil.deNull(cellData_Wt).indexOf(".") + 1) : "0";				
				Double double2decWt = !CommonUtil.deNull(cellData_Wt).isEmpty() ? Math.round(Double.parseDouble(cellData_Wt) * 100.0) / 100.0 : 0.00;
				cellData_Wt = CommonUtil.deNull(cellData_Wt).isEmpty() ? cellData_Wt : ((afterDotWt.length()>1 && !afterDotWt.equals("00") ) || Integer.valueOf(afterDotWt) > 0 ) ? String.format("%.2f", double2decWt): cellData_Wt.replace("."+afterDotWt, "");
				String cellData_Vol = CommonUtil.getCellData(
						CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_grossVol_index : ConstantUtil.grossVol_index) + String.valueOf(i), sheet);
				String afterDotVol = CommonUtil.deNull(cellData_Vol).indexOf(".") > 0 ?  CommonUtil.deNull(cellData_Vol).substring(CommonUtil.deNull(cellData_Vol).indexOf(".") + 1) : "0";
				Double double2decVol = !CommonUtil.deNull(cellData_Vol).isEmpty() ? Math.round(Double.parseDouble(cellData_Vol) * 100.0) / 100.0 : 0.00;
				cellData_Vol = CommonUtil.deNull(cellData_Vol).isEmpty() ? cellData_Vol : ((afterDotVol.length()>1 && !afterDotVol.equals("00") ) || Integer.valueOf(afterDotVol) > 0 ) ? String.format("%.2f", double2decVol) : cellData_Vol.replace("."+afterDotVol, "");
				String cellData_HsCode = CommonUtil.getCellData(
						CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_hsCode_index : ConstantUtil.hsCode_index) + String.valueOf(i), sheet);
				String cellData_OldHsCode = CommonUtil.getCellData(
						CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_oldHsCode_index : ConstantUtil.oldHsCode_index) + String.valueOf(i), sheet);
				String cellData_crgDesc = CommonUtil.getCellData(
						CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_crgDesc_index : ConstantUtil.crgDesc_index) + String.valueOf(i), sheet);
				String cellData_custom = CommonUtil.getCellData(
						CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_custom_index : ConstantUtil.custom_index) + String.valueOf(i), sheet);
				cellData_custom = !CommonUtil.deNull(cellData_custom).isEmpty() ? cellData_custom.replace(".0", "") : cellData_custom;
				if(cellData_ActionHsCode.equalsIgnoreCase(ConstantUtil.action_addHS)) {
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(CommonUtil.deNull(cellData_NbrPkg).isEmpty() ? "0" : cellData_NbrPkg);
					miscDetailNbrPkgListAdd.add(miscDtl);	
					
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_Wt);
					miscDetailWtListAdd.add(miscDtl);	

					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_Vol);
					miscDetailVolListAdd.add(miscDtl);	
					
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_HsCode);
					miscDetailHSCodelListAdd.add(miscDtl);	

					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_crgDesc);
					miscDetailCrgDescSubAdd.add(miscDtl);
					
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_custom);
					miscDetailCustomSubAdd.add(miscDtl);	
				} else if(cellData_ActionHsCode.equalsIgnoreCase(ConstantUtil.action_updateHS)) {
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_NbrPkg);
					miscDetailNbrPkgListUpdate.add(miscDtl);	
					
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_Wt);
					miscDetailWtListUpdate.add(miscDtl);	

					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_Vol);
					miscDetailVolListUpdate.add(miscDtl);	

					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_HsCode);
					miscDetailSubHsCodeListUpdate.add(miscDtl);	
					
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_OldHsCode);
					miscDetailsubOldHSCodelListUpdate.add(miscDtl);	

					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_crgDesc);
					miscDetailCrgDescSubUpdate.add(miscDtl);
					
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_custom);
					miscDetailCustomSubUpdate.add(miscDtl);	
				} else if(cellData_ActionHsCode.equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_NbrPkg);
					miscDetailNbrPkgListDelete.add(miscDtl);	
					
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_Wt);
					miscDetailWtListDelete.add(miscDtl);	

					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_Vol);
					miscDetailVolListDelete.add(miscDtl);	

					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_HsCode);
					miscDetailSubHsCodeListDel.add(miscDtl);	
					
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_OldHsCode);
					miscDetailSubOldHsCodeListDel.add(miscDtl);	
					
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_custom);
					miscDetailSubCustomHSCodeListDel.add(miscDtl);	
					
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_crgDesc);
					miscDetailSubCargoDescListDel.add(miscDtl);
				} else if(cellData_ActionHsCode.equalsIgnoreCase(ConstantUtil.action_update)) {
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_HsCode);
					miscDetailHSCodelListUpdate.add(miscDtl);		
					
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_crgDesc);
					miscDetailCrgDescMainUpdate.add(miscDtl);
					
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_custom);
					miscDetailCustomMainUpdate.add(miscDtl);
					
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_NbrPkg);
					miscDetailNbrPkgsMainUpdate.add(miscDtl);
					
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_Wt);
					miscDetailWtMainUpdate.add(miscDtl);

					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_Vol);
					miscDetailVolMainUpdate.add(miscDtl);
				} else if (cellData_ActionHsCode.equalsIgnoreCase(ConstantUtil.action_add)) {
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_crgDesc);
					miscDetailCrgDescMainAdd.add(miscDtl);
					
					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_HsCode);
					miscDetailSubHsCodeListAdd.add(miscDtl);	

					miscDtl = new MiscDetail();
					miscDtl.setTypeCode(cellData_BLNbr);
					miscDtl.setTypeValue(cellData_custom);
					miscDetailCustomMainAdd.add(miscDtl);
				}
			}
			CargoManifest manifestDetail = new CargoManifest();
			boolean manifestmultipleHSDetailExist = false;
			String billOfLadingNo = "";
			List<CargoManifest> manifestHSDetail = new ArrayList<CargoManifest>();
			// START FTZ CR - Get Total Packages, Weight  and Vol - NS JULY 2024

			for (int i = recStart; i <= rowCount + 1; i++) {
				deleteflag = false;

				try {
					// For not applicable
					cellData_na = CommonUtil.deNull(CommonUtil.getCellData(
							CommonUtil.getColumnIndex(ConstantUtil.action_index) + String.valueOf(i), sheet));
					log.info("Current action:" + CommonUtil.deNull(cellData_na));
					if (cellData_na != null && cellData_na.equalsIgnoreCase(ConstantUtil.action_NA)) {
						continue;
					}

					/*
					 * // Delete no need to check on validate bill of landing cellData_bill =
					 * CommonUtil.getCellData(CommonUtil.getColumnIndex(ConstantUtil.
					 * billofLanding_index) + String.valueOf(i), sheet); if(cellData_bill!=null &&
					 * !cellData_bill.isEmpty() &&
					 * cellData_na.equalsIgnoreCase(ConstantUtil.action_delete)) { continue; }
					 */

					cm = new CargoManifest();
					cm.setLast_modify_dttm(timeStamp);
					cm.setHasAddProcess(false);
					cm.setValueChanges(false);
					cm.setMainSub(false);
					cm.setEdo_created(false);
					cm.setCustomChanged(false);
					chkDGInd = true;
					commentsList = new ArrayList<Comments>();
					List<HatchDetails> hatchData = new ArrayList<HatchDetails>();
					log.info("Started Excel Irearion  for  RowNo : " + i);
					
					// Delete no need to check on validate bill of landing
					cellData_bill = CommonUtil.deNull(CommonUtil.getCellDataStringType(
							CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_billofLanding_index : ConstantUtil.billofLanding_index) + String.valueOf(i), sheet));
					if (cellData_bill != null && !cellData_bill.isEmpty()
							&& (cellData_na.equalsIgnoreCase(ConstantUtil.action_delete)
									|| cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS))) {
						String seqNo = manifestRepo.getMftSeqNoForDelete(vvCd, (cellData_bill.trim()).toUpperCase());
						if (seqNo != null) {
							if (manifestRepo.chkVslStat(vvCd)) {
								Comments comments = new Comments();
								comments.setKey(ConstantUtil.bills_of_landing_no);
								comments.setMessage(ConstantUtil.Error_M21605 + "-" + vvCd);
								commentsList.add(comments);
							} else if (manifestRepo.chkEdonbrPkgs(seqNo, vvCd, cellData_bill)) {
								Comments comments = new Comments();
								comments.setKey(ConstantUtil.bills_of_landing_no);
								comments.setMessage(ConstantUtil.Error_M20202 + "-" + vvCd);
								commentsList.add(comments);
							} else if (manifestRepo.chkDNnbrPkgs(seqNo, vvCd, cellData_bill)) {
								Comments comments = new Comments();
								comments.setKey(ConstantUtil.bills_of_landing_no);
								comments.setMessage(ConstantUtil.Error_M20208 + "-" + vvCd);
								commentsList.add(comments);
							} else if (manifestRepo.chkTnbrPkgs(seqNo, vvCd, cellData_bill)) {
								Comments comments = new Comments();
								comments.setKey(ConstantUtil.bills_of_landing_no);
								comments.setMessage(ConstantUtil.Error_M20209 + "-" + vvCd);
								commentsList.add(comments);
							} else if (manifestRepo.chkTDNnbrPkgs(seqNo, vvCd, cellData_bill)) {
								Comments comments = new Comments();
								comments.setKey(ConstantUtil.bills_of_landing_no);
								comments.setMessage(ConstantUtil.Error_M20209 + "-" + vvCd);
								commentsList.add(comments);
							} else {
								cm.setBills_of_landing_no(cellData_bill);
								if(!cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
									deleteflag = true;
								}
							}
						}else {
							Comments comments = new Comments();
							comments.setKey(ConstantUtil.bills_of_landing_no);
							comments.setMessage(ConstantUtil.ErrorMsg_BlNoNotExist + "-" + vvCd);
							commentsList.add(comments);
						}
					}

					for (ManifestUploadConfig manifestUploadConfig : header) {

						if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.action)) {
							// Action Column
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							// error
							if (CommonUtil.deNull(cellData) == "") {
								if (!deleteflag
										&& manifestUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(manifestUploadConfig.getAttr_name() + ConstantUtil.mandatory);
									commentsList.add(comments);
								}
							} else {
								cm.setAction(cellData);
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.bills_of_landing_no)) {
							manifestmultipleHSDetailExist = false;
							manifestHSDetail = new ArrayList<CargoManifest>();
							// Bill of Landing no
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							String afterDot = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase().trim();
								afterDot = upperCellData.indexOf(".") > 0 ? upperCellData.substring(upperCellData.indexOf(".") + 1) : "0";
								
							}
							billOfLadingNo = upperCellData;
							String tempData = CommonUtil.deNull(upperCellData).isEmpty() ? CommonUtil.deNull(upperCellData) : afterDot.length()>1 ? upperCellData : upperCellData.replace(".0", "");
							if (upperCellData != null && upperCellData != "" && upperCellData.contains(".")) {
								BigDecimal cellDataStrRemZero = new BigDecimal(upperCellData);
								upperCellData = cellDataStrRemZero.stripTrailingZeros().toPlainString();
							}
							manifestDetail = manifestRepo.getMainfestDetails(upperCellData, vvCd);
							if (manifestDetail != null) {
								manifestmultipleHSDetailExist = manifestRepo
										.checkMultipleMainfestHSDetails(manifestDetail.getMft_seq_nbr());
								manifestHSDetail = manifestRepo.getMainfestHSDetail(manifestDetail);
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (CommonUtil.deNull(upperCellData) == "") {
								if (!deleteflag
										&& manifestUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_BLNO);
									commentsList.add(comments);
								}
							} else if (cm.getAction() != null
									&& cm.getAction().equalsIgnoreCase(ConstantUtil.action_add)) {
								boolean containSpecialCharacter = CommonUtility.containSpecialCharacter(upperCellData);
								if (containSpecialCharacter) {	// validation for special characters here
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_BlNoNoContainsSpecialChar);
									commentsList.add(comments);
									continue;
								}
								if (!deleteflag && manifestRepo.mainfestDetailIsExist(upperCellData, vvCd) > 0 && !CommonUtil.deNull(cm.getSplit_bl_ind()).equalsIgnoreCase("Yes")) {
									String blno = upperCellData;
									if ((manifestRecords.stream()
											.filter(x -> x.getAction() != null
													&& x.getAction().equalsIgnoreCase(ConstantUtil.action_delete)
													&& x.getBills_of_landing_no().equalsIgnoreCase(blno))
											.findFirst().orElse(null) == null)) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.Error_M20201);
										commentsList.add(comments);				
									} else {
										mainBLNbrListAdd.add(upperCellData);
										cm.setBills_of_landing_no(upperCellData);
									}
								} else if(CommonUtility.deNull(cm.getSplit_bl_ind()).equalsIgnoreCase("Yes")){
									String blno = upperCellData;
									mainBLNbrListAdd.add(upperCellData);
									long isMorethan1 = miscDetailSubHsCodeListAdd.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blno)).count();
									if(isMorethan1 > 1) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Duplicate_TempBlNo);
										commentsList.add(comments);
									} else {
										cm.setBills_of_landing_no(upperCellData);
									}
								} else if(CommonUtility.deNull(cm.getSplit_bl_ind()).equalsIgnoreCase("No")){
									String blno = upperCellData;
									long isMorethan1 = miscDetailSubHsCodeListAdd.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blno)).count();
									if(isMorethan1 > 1) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Duplicate_BlNo);
										commentsList.add(comments);
									} else {
										mainBLNbrListAdd.add(upperCellData);
										cm.setBills_of_landing_no(upperCellData);
									}
								} else if (upperCellData.length() > 20) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_BlNoNoLength);
									commentsList.add(comments);
								} else {
									mainBLNbrListAdd.add(upperCellData);
									cm.setBills_of_landing_no(upperCellData);
								}

							} else if (cm.getAction() != null
									&& cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
								String blno = upperCellData;
								String seqno = manifestRepo.getMftSeqNoForDelete(vvCd, blno);
								boolean edostat = chkEdonbrPkgs(seqno, vvCd, blno);
								if (manifestDetail == null) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_BlNoNotExist);
									commentsList.add(comments);
								} else if (edostat) { // !coCd.equals("JP")
									log.info("Edo created for:" + cm.getAction() + blno);
									cm.setEdo_created(true);
									mainBLNbrListUpdate.add(upperCellData);
									cm.setBills_of_landing_no(upperCellData);
								}  else {
									mainBLNbrListUpdate.add(upperCellData);
									cm.setBills_of_landing_no(upperCellData);
								} 
								long exist = miscDetailHSCodelListAdd.stream().parallel()
										.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blno)).count();
								if(exist > 0) {
									cm.setHasAddProcess(true);
								}
							} else if (cm.getAction() != null
									&& cm.getAction().equalsIgnoreCase(ConstantUtil.action_addHS)) {
								long exist = mainBLNbrListAdd.stream().parallel().filter((s) -> CommonUtil.deNull(s).toUpperCase().trim().equalsIgnoreCase(tempData)).count();
								long existUpdate = mainBLNbrListUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s).toUpperCase().trim().equalsIgnoreCase(tempData)).count();
								long existUpdateHs = miscDetailNbrPkgListUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).toUpperCase().trim().equalsIgnoreCase(tempData)).count();
								long existDeleteHs = miscDetailNbrPkgListDelete.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).toUpperCase().trim().equalsIgnoreCase(tempData)).count();

								String blno = upperCellData;
								String seqno = manifestRepo.getMftSeqNoForDelete(vvCd, blno);
								boolean edostat = chkEdonbrPkgs(seqno, vvCd, blno);
								if (edostat) {
									log.info("Edo created for:" + cm.getAction() + blno);
									cm.setEdo_created(true);
									Comments comments = new Comments();
									comments.setKey(ConstantUtil.bills_of_landing_no);
									comments.setMessage(ConstantUtil.ErrorMsg_AddEdoCreated + "-" + vvCd);
									commentsList.add(comments);
								} else if (exist == 0 && manifestDetail == null) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_MainNotExist_Add);
									commentsList.add(comments);
								} else if (existUpdateHs == 0 && existUpdate == 0 && manifestDetail != null && existDeleteHs == 0) {
									if(CommonUtil.deNull(cm.getSplit_bl_ind()).equalsIgnoreCase("Yes")) {
										if(manifestRepo.isMainandBLExist(CommonUtil.deNull(cm.getSplit_main_bl()),upperCellData)) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_MainNotExist_Update);
											commentsList.add(comments);
										} else {
											cm.setBills_of_landing_no(upperCellData);
										}
									} else {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_MainNotExist_Update);
										commentsList.add(comments);
									}
								} else {
									cm.setBills_of_landing_no(upperCellData);
								}
							} else if (cm.getAction() != null
									&& cm.getAction().equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
								String blno = upperCellData;
								String seqno = manifestRepo.getMftSeqNoForDelete(vvCd, blno);
								boolean edostat = chkEdonbrPkgs(seqno, vvCd, blno);
								String temphs = CommonUtil.getCellData(
										CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_hsCode_index : ConstantUtil.hsCode_index) + String.valueOf(i),
										sheet);
								String tempCustomhs = CommonUtil.getCellData(
										CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_custom_index : ConstantUtil.custom_index) + String.valueOf(i),
										sheet);
								String[] hsCode = cellDataSplit(temphs);
								temphs = hsCode[0].trim() + "-" + hsCode[1].trim() + "-" + hsCode[2].trim();
								if (CommonUtil.deNull(temphs).equalsIgnoreCase(manifestDetail.getHs_code_sub_code()) && CommonUtil.deNull(tempCustomhs).equalsIgnoreCase(manifestDetail.getCustom_hs_code())) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_DeleteHSCode_MainSub);
									commentsList.add(comments);
									cannotDeleteSub = true;
								} else if (edostat) {
									log.info("Edo created for:" + cm.getAction() + blno);
									cm.setEdo_created(true);
									Comments comments = new Comments();
									comments.setKey(ConstantUtil.bills_of_landing_no);
									comments.setMessage(ConstantUtil.ErrorMsg_DeleteEdoCreated + "-" + vvCd);
									commentsList.add(comments);
								} else {
									long exist = mainBLNbrListUpdate.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s).toUpperCase().trim().equalsIgnoreCase(tempData)).count();
									long existHSUpdate = miscDetailNbrPkgListUpdate.stream().parallel().filter(
											(s) -> CommonUtil.deNull(s.getTypeCode()).toUpperCase().trim().equalsIgnoreCase(tempData))
											.count();
									long existHSAdd = miscDetailNbrPkgListAdd.stream().parallel().filter(
											(s) -> CommonUtil.deNull(s.getTypeCode()).toUpperCase().trim().equalsIgnoreCase(tempData))
											.count();
									if (exist == 0 && existHSUpdate == 0 && existHSAdd == 0) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_MainOrSubNotExist);
										commentsList.add(comments);
										cannotDeleteSub = true;
									} else {
										cm.setBills_of_landing_no(upperCellData);
									}
								}
							} else if (cm.getAction() != null
									&& cm.getAction().equalsIgnoreCase(ConstantUtil.action_updateHS)) {
								String blno = upperCellData;
								String seqno = manifestRepo.getMftSeqNoForDelete(vvCd, blno);
								boolean edostat = chkEdonbrPkgs(seqno, vvCd, blno);
								if (manifestDetail == null) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_BlNoNotExist);
									commentsList.add(comments);
								} else if (edostat) {
									log.info("Edo created for:" + cm.getAction() + blno);
									cm.setEdo_created(true);
									cm.setBills_of_landing_no(upperCellData);
									mainBLNbrListSubUpdate.add(upperCellData);
								} else {
									cm.setBills_of_landing_no(upperCellData);
									mainBLNbrListSubUpdate.add(upperCellData);
								}
							} else {
								cm.setBills_of_landing_no(upperCellData);
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.cargo_type)) {
							// CargoType
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData="";
							if(cellData!=null && cellData!="")
							{
								upperCellData=cellData.toUpperCase();
							}
						
							removeColorsAndComment(ref, sheet, no_style);
							if (!cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
								if (!cm.getEdo_created()) {
									if (CommonUtil.deNull(upperCellData) == "") {
										if (!deleteflag && manifestUploadConfig.getMandatory_ind()
												.equalsIgnoreCase(ConstantUtil.yes)) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_CargoType);
											commentsList.add(comments);
										}
									} else if (!deleteflag && !cargoTypeDropdownList.contains(upperCellData)) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_InvalidItemFromDroDown);
										commentsList.add(comments);
									} else {
										String[] cargoType = cellDataSplit(upperCellData);
										cm.setCargoType(cargoType[0].trim());
									}
								} else {
									if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
										cellData = CommonUtil.deNull(cellData).isEmpty() ? "" : cellDataSplit(upperCellData)[0].trim();
										if (!CommonUtil.deNull(cellData)
												.equalsIgnoreCase(manifestDetail.getCargoType())) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.Error_M20204);
											commentsList.add(comments);
										}
									}
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.cargo_description)) {
							// Cargo description
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData="";
							if(cellData!=null && cellData!="")
							{
								upperCellData=cellData.toUpperCase();
							}
							String cellData_CustomHSCode = CommonUtil.getCellData(
									CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_custom_index : ConstantUtil.custom_index) + String.valueOf(i),
									sheet);
							removeColorsAndComment(ref, sheet, no_style);
							if(!cm.getEdo_created()) {
								if (CommonUtil.deNull(upperCellData) == "") {
									if (!deleteflag
											&& manifestUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_CargoDesc);
										commentsList.add(comments);
									}
								} else if (upperCellData.length() > 3000) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Length_CargoDesc_Custom);
									commentsList.add(comments);
								} else {
									cm.setCargo_description(upperCellData);
								}
									String cellData_HSCode = CommonUtil.getCellData(
											CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_hsCode_index : ConstantUtil.hsCode_index) + String.valueOf(i),
											sheet);
									String cellData_OldHSCode = CommonUtil.getCellData(
											CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_oldHsCode_index : ConstantUtil.oldHsCode_index) + String.valueOf(i),
											sheet);
									String temphs = "", oldtemphs = "";
									String tempCustom = cm.isCustomChanged() ? cm.getOldCustom() : cellData_CustomHSCode;
									if(!CommonUtil.deNull(cellData_HSCode).isEmpty() && hsCodeSubCodeDropdownList.contains(cellData_HSCode)) {
										String[] hsCode = cellDataSplit(cellData_HSCode);
										temphs = hsCode[0].trim() + "-" + hsCode[1].trim() + "-" + hsCode[2].trim();
									} 
									if(!CommonUtil.deNull(cellData_OldHSCode).isEmpty() && hsCodeSubCodeDropdownList.contains(cellData_OldHSCode)) {
										String[] oldhsCode = cellDataSplit(cellData_OldHSCode);
										oldtemphs = oldhsCode[0].trim() + "-" + oldhsCode[1].trim() + "-" + oldhsCode[2].trim();
									} 
									String blNbr = cellData_bill;
									long otherSubExistUpdate = miscDetailCrgDescSubUpdate.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									long otherSubExistAdd = miscDetailCrgDescSubAdd.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									long mainExistAdd = miscDetailCrgDescMainAdd.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									long mainExistUpdate = miscDetailCrgDescMainUpdate.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									if (cellData_na.equalsIgnoreCase(ConstantUtil.action_update)) {
										if (manifestmultipleHSDetailExist) {
											if (otherSubExistUpdate > 0) {
												int index = IntStream.range(0, miscDetailSubHsCodeListUpdate.size())
														.filter(k -> CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(k).getTypeValue())
																.equalsIgnoreCase(CommonUtil.deNull(cellData_HSCode)) 
																&& CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(k).getTypeCode()).equalsIgnoreCase(blNbr)
																&& CommonUtil.deNull(miscDetailCustomSubUpdate.get(k).getTypeCode()).equalsIgnoreCase(blNbr)
																&& CommonUtil.deNull(miscDetailCustomSubUpdate.get(k).getTypeValue())
																.equalsIgnoreCase(CommonUtil.deNull(tempCustom)))
														.findFirst().orElse(-1);
												if(index >=0) {
													if (!CommonUtil.deNull(cellData).equalsIgnoreCase(
															CommonUtil.deNull(miscDetailCrgDescSubUpdate.get(index).getTypeValue()))
															&& CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(index).getTypeCode())
																	.equalsIgnoreCase(blNbr)
															&& CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(index).getTypeValue())
																	.equalsIgnoreCase(CommonUtil.deNull(cellData_HSCode))
															&& CommonUtil.deNull(miscDetailCustomSubUpdate.get(index).getTypeCode())
																	.equalsIgnoreCase(blNbr)
															&& CommonUtil.deNull(miscDetailCustomSubUpdate.get(index).getTypeValue())
																	.equalsIgnoreCase(CommonUtil.deNull(tempCustom))) {
														Comments comments = new Comments();
														comments.setKey(manifestUploadConfig.getAttr_name());
														comments.setMessage(ConstantUtil.ErrorMsg_UpdateCrgDesc_MainSub);
														commentsList.add(comments);
													}
												}
											}
										} 
									} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS)) {
										if (manifestmultipleHSDetailExist) {
											long errorDuplicateHSCode = commentsList.stream().parallel().filter((s) -> CommonUtil.deNull(s.getMessage()).equalsIgnoreCase(ConstantUtil.ErrorMsg_UpdateHSCode_Duplicate)).count();
											long errorEmptyOldHscode = commentsList.stream().parallel().filter((s) -> CommonUtil.deNull(s.getMessage()).equalsIgnoreCase(ConstantUtil.ErrorMsg_OldHSCode_Compulsary)).count();
											long errorDuplicateCustomHSCode = commentsList.stream().parallel().filter((s) -> CommonUtil.deNull(s.getMessage()).equalsIgnoreCase(ConstantUtil.ErrorMsg_Same_Custom)).count();
											if (mainExistUpdate > 0) {
												int index = IntStream.range(0, miscDetailCrgDescMainUpdate.size())
														.filter(k -> CommonUtil.deNull(miscDetailCrgDescMainUpdate.get(k).getTypeCode())
																.equalsIgnoreCase(blNbr))
														.findFirst().orElse(-1);
												if (!CommonUtil.deNull(cellData).equalsIgnoreCase(
														CommonUtil.deNull(miscDetailCrgDescMainUpdate.get(index).getTypeValue()))
														&& CommonUtil.deNull(miscDetailHSCodelListUpdate.get(index).getTypeValue())
																.equalsIgnoreCase(CommonUtil.deNull(cellData_HSCode))
																&& (!oldtemphs.isEmpty() ? CommonUtil.deNull(oldtemphs)
																.equalsIgnoreCase(manifestDetail.getHs_code_sub_code()) : true)
																&& CommonUtil.deNull(tempCustom).equalsIgnoreCase(CommonUtil.deNull(
																		miscDetailCustomMainUpdate.get(index).getTypeValue()))
																&& CommonUtil.deNull(cm.getBills_of_landing_no()).equalsIgnoreCase(CommonUtil.deNull(
																		miscDetailCustomMainUpdate.get(index).getTypeCode()))
																&& errorDuplicateHSCode == 0 && errorEmptyOldHscode == 0
																&& errorDuplicateCustomHSCode == 0) {
													Comments comments = new Comments();
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(ConstantUtil.ErrorMsg_UpdateCrgDesc_MainSub);
													commentsList.add(comments);
												} else {
													cm.setCargo_description(upperCellData);
												}
											} else {
												if (cm.getBills_of_landing_no()
														.equalsIgnoreCase(manifestDetail.getBills_of_landing_no())
														&& temphs.equalsIgnoreCase(manifestDetail.getHs_code_sub_code())
													&& CommonUtil.deNull(cm.getCustom_hs_code()).equalsIgnoreCase(
															CommonUtil.deNull(manifestDetail.getCustom_hs_code()))
													&& !upperCellData.equalsIgnoreCase(CommonUtil
															.deNull(manifestDetail.getCargo_description()).toUpperCase()) 
													&& errorDuplicateHSCode == 0 && errorEmptyOldHscode == 0
													&& CommonUtil.deNull(tempCustom).equalsIgnoreCase(CommonUtil.deNull(
															manifestDetail.getCustom_hs_code()))
													&& errorDuplicateCustomHSCode == 0) {
													Comments comments = new Comments();
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(ConstantUtil.ErrorMsg_UpdateCrgDesc_MainSub);
													commentsList.add(comments);
												} else {
													cm.setCargo_description(upperCellData);
												}
											}
										} else {
											if(mainExistUpdate == 0) {
												if(!manifestDetail.getCargo_description().equalsIgnoreCase(upperCellData)) {
													Comments comments = new Comments();
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(ConstantUtil.ErrorMsg_MainNotExist_Update);
													commentsList.add(comments);
												}
											} else {
												int index = IntStream.range(0, miscDetailCrgDescMainUpdate.size())
														.filter(k -> CommonUtil.deNull(miscDetailCrgDescMainUpdate.get(k).getTypeCode())
																.equalsIgnoreCase(blNbr))
														.findFirst().orElse(-1);
												if(!miscDetailCrgDescMainUpdate.get(index).getTypeValue().equalsIgnoreCase(upperCellData)) {
													Comments comments = new Comments();
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(ConstantUtil.ErrorMsg_UpdateCrgDesc_MainSub);
													commentsList.add(comments);
												}
											}
										}
									} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_add)) {
										if (otherSubExistAdd > 0) {
											int index = IntStream.range(0, miscDetailHSCodelListAdd.size())
													.filter(k -> CommonUtil.deNull(miscDetailHSCodelListAdd.get(k).getTypeValue())
															.equalsIgnoreCase(cellData_HSCode) && CommonUtil.deNull(miscDetailHSCodelListAdd.get(k).getTypeCode())
															.equalsIgnoreCase(blNbr) && CommonUtil.deNull(miscDetailCustomSubAdd.get(k).getTypeCode())
															.equalsIgnoreCase(blNbr) && CommonUtil.deNull(miscDetailCustomSubAdd.get(k).getTypeValue())
															.equalsIgnoreCase(tempCustom))
													.findFirst().orElse(-1);
											if(miscDetailCrgDescSubAdd.size() > 0 && index >= 0) {
												if (!cellData
														.equalsIgnoreCase(CommonUtil.deNull(miscDetailCrgDescSubAdd.get(index).getTypeValue()))
														&& CommonUtil.deNull(miscDetailHSCodelListAdd.get(index).getTypeCode())
																.equalsIgnoreCase(blNbr)
														&& CommonUtil.deNull(miscDetailHSCodelListAdd.get(index).getTypeValue())
																.equalsIgnoreCase(CommonUtil.deNull(cellData_HSCode))
														&& CommonUtil.deNull(miscDetailCustomSubAdd.get(index).getTypeCode())
																.equalsIgnoreCase(blNbr)
														&& CommonUtil.deNull(miscDetailCustomSubAdd.get(index).getTypeValue())
																.equalsIgnoreCase(CommonUtil.deNull(tempCustom))) { 
													Comments comments = new Comments();
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(ConstantUtil.ErrorMsg_UpdateCrgDesc_MainSub);
													commentsList.add(comments);
												}
											}
										}
									
									} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS)) {
										long errorDuplicateCustomHSCode = commentsList.stream().parallel().filter((s) -> CommonUtil.deNull(s.getMessage()).equalsIgnoreCase(ConstantUtil.ErrorMsg_Same_Custom)).count();
										if (mainExistAdd > 0 && errorDuplicateCustomHSCode == 0) {
											int index = IntStream.range(0, miscDetailCrgDescMainAdd.size())
													.filter(k -> CommonUtil.deNull(miscDetailCrgDescMainAdd.get(k).getTypeCode())
															.equalsIgnoreCase(blNbr))
													.findFirst().orElse(-1);
											int indexHS = IntStream.range(0, miscDetailSubHsCodeListAdd.size())
													.filter(k -> CommonUtil.deNull(miscDetailSubHsCodeListAdd.get(k).getTypeCode())
															.equalsIgnoreCase(blNbr))
													.findFirst().orElse(-1);
											int indexCustomHS = IntStream.range(0, miscDetailCustomMainAdd.size())
													.filter(k -> CommonUtil.deNull(miscDetailCustomMainAdd.get(k).getTypeCode())
															.equalsIgnoreCase(blNbr))
													.findFirst().orElse(-1);
											if (!cellData
													.equalsIgnoreCase(CommonUtil.deNull(miscDetailCrgDescMainAdd.get(index).getTypeValue()))
													&& CommonUtil.deNull(miscDetailSubHsCodeListAdd.get(indexHS).getTypeValue())
															.equalsIgnoreCase(CommonUtil.deNull(cellData_HSCode))
													&& CommonUtil.deNull(miscDetailCustomMainAdd.get(indexCustomHS).getTypeValue())
													.equalsIgnoreCase(CommonUtil.deNull(tempCustom))) { 
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_UpdateCrgDesc_MainSub);
												commentsList.add(comments);
											}
										}
									}
							} else {
								if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
									if (!CommonUtil.deNull(cellData)
											.equalsIgnoreCase(manifestDetail.getCargo_description())) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.Error_M20204);
										commentsList.add(comments);
									}
								} else if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_updateHS)) {
									int index = -1;
									String prevCargoDesc = "";
									List<CargoManifest> manifestHSDetailTmp = manifestHSDetail;
									String customHsCode = cm.isCustomChanged() ? cm.getOldCustom() : CommonUtil.deNull(cellData_CustomHSCode);
									
									if(CommonUtility.deNull(cm.getOldHSCode()).isEmpty()) {
										for (int j = 0; j < manifestHSDetail.size(); j++) {
											if (CommonUtil.deNull(cm.getHs_code()).equalsIgnoreCase(CommonUtil.deNull(manifestHSDetail.get(j).getHs_code()))
													&& CommonUtil.deNull(cm.getHs_sub_code_fr())
															.equals(CommonUtil.deNull(manifestHSDetail.get(j).getHs_sub_code_fr()))
															&& CommonUtil.deNull(cm.getHs_sub_code_to())
															.equals(CommonUtil.deNull(manifestHSDetail.get(j).getHs_sub_code_to()))
													&& cellData_bill.equalsIgnoreCase(
															CommonUtil.deNull(manifestHSDetail.get(j).getBills_of_landing_no()))
													&& CommonUtil.deNull(manifestHSDetail.get(j).getCustom_hs_code())
															.equalsIgnoreCase(CommonUtil.deNull(customHsCode))) {
												prevCargoDesc = manifestHSDetail.get(j).getCargo_description();
												break;
											}
										}
										if (CommonUtil.deNull(prevCargoDesc).isEmpty()) {
											String nbrPkg = cm.getNumber_of_packages();
											String cstmHs = cm.isCustomChanged() ? CommonUtil.deNull(cm.getOldCustom()) : CommonUtil.deNull(cm.getCustom_hs_code());
											int indexNbrPkgs = IntStream.range(0, manifestHSDetailTmp.size())
													.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getNumber_of_packages())
															.equalsIgnoreCase(CommonUtil.deNull(nbrPkg).trim()))
													.findFirst().orElse(-1);
											int indexCrgCustom = IntStream.range(0, manifestHSDetailTmp.size())
													.filter(n -> CommonUtil.deNull(manifestHSDetailTmp.get(n).getCustom_hs_code())
															.equalsIgnoreCase(CommonUtil.deNull(cstmHs).trim()))
													.findFirst().orElse(-1);
											index = indexNbrPkgs >= 0 ? indexNbrPkgs : indexCrgCustom;
											prevCargoDesc = index >=0 ? manifestHSDetailTmp.get(index).getCargo_description() : "";
										}
									} else {
										for (int j = 0; j < manifestHSDetail.size(); j++) {
											if (CommonUtil.deNull(cm.getOldHSCode()).equalsIgnoreCase(CommonUtil.deNull(manifestHSDetail.get(j).getHs_code()))
													&& CommonUtil.deNull(cm.getOldHSCode_fr())
															.equals(CommonUtil.deNull(manifestHSDetail.get(j).getHs_sub_code_fr()))
													&& CommonUtil.deNull(cm.getOldHSCode_to())
															.equals(CommonUtil.deNull(manifestHSDetail.get(j).getHs_sub_code_to()))
													&& cellData_bill.equalsIgnoreCase(
															manifestHSDetail.get(j).getBills_of_landing_no())
													&& CommonUtil.deNull(manifestHSDetail.get(j).getCustom_hs_code())
													.equalsIgnoreCase(CommonUtil.deNull(customHsCode))) {
												prevCargoDesc = manifestHSDetail.get(j).getCargo_description();
												break;
											}
										}
									}
								
									if (!CommonUtil.deNull(cellData).equalsIgnoreCase(prevCargoDesc)) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.Error_M20204);
										commentsList.add(comments);
									}
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.hs_code)) {
							String cellData_oldHSCode = CommonUtil.getCellData(
									CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_oldHsCode_index : ConstantUtil.oldHsCode_index) + String.valueOf(i), sheet);
							String cellData_CustomHSCode = CommonUtil.getCellData(
									CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_custom_index : ConstantUtil.custom_index) + String.valueOf(i), sheet);
							String cellData_CargoDesc = CommonUtil.getCellData(
									CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_crgDesc_index : ConstantUtil.crgDesc_index) + String.valueOf(i), sheet);
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							if(cellData_na.equalsIgnoreCase(ConstantUtil.action_add)) {
								MiscDetail ms = new MiscDetail();
								ms.setTypeCode(cellData_bill);
								ms.setTypeValue(cellData);
								miscDetailCustomHSCodelList.add(ms);
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (CommonUtil.deNull(cellData) == "") {
								if (!deleteflag
										&& manifestUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
									if (cm.getEdo_created()) {
										if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
											if(manifestDetail != null) {
												cm.setHs_code(manifestDetail.getHs_code());
												cm.setHs_sub_code_fr(manifestDetail.getHs_sub_code_fr());
												cm.setHs_sub_code_to(manifestDetail.getHs_sub_code_to());
											}
										} else {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_HSCode);
											commentsList.add(comments);
										}
									} else {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_HSCode);
										commentsList.add(comments);
									}
								}
							} else if (!deleteflag && !hsCodeSubCodeDropdownList.contains(cellData)) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_InvalidItemFromDroDown);
								commentsList.add(comments);
							} else {
								if (CommonUtil.deNull(cellData) != "") {
									String[] hsCode = cellDataSplit(cellData);
									for (int hs = 0; hs < hsCode.length; hs++) {
										if (hs == 0) {
											cm.setHs_code(hsCode[hs].trim());
										} else if (hs == 1) {
											cm.setHs_sub_code_fr(hsCode[hs].trim());
										} else if (hs == 2) {
											cm.setHs_sub_code_to(hsCode[hs].trim());
										}
									}
								}
							}
							if (cellData_na.equalsIgnoreCase(ConstantUtil.action_add) || cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS)) {
								String blNbr = cellData_bill;
								long exist = miscDetailHSCodelListAdd.stream().parallel()
										.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
								if (exist > 0) {
									int index = IntStream.range(0, miscDetailCustomHSCodelList.size()).filter(
											k -> CommonUtil.deNull(miscDetailCustomHSCodelList.get(k).getTypeCode()).equalsIgnoreCase(blNbr))
											.findFirst().orElse(-1);
									if (index >= 0) {
										String main = CommonUtil.deNull(miscDetailCustomHSCodelList.get(index).getTypeValue());
										exist = miscDetailHSCodelListAdd.stream().parallel()
												.filter((s) -> CommonUtil.deNull(s.getTypeValue()).equalsIgnoreCase(main)).count();
										if (exist == 0 && manifestDetail == null) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_AddHSCode_Compulsary);
											commentsList.add(comments);
										}
									} else if (manifestDetail != null) {
										List<CargoManifest> manifestHSDetailTmp = manifestHSDetail;
										long hsExist = manifestHSDetailTmp.stream().parallel()
												.filter((s) -> CommonUtil.deNull(s.getHs_code_sub_code())
														.equalsIgnoreCase(CommonUtil.deNull(cellData).trim())
														&& CommonUtil.deNull(s.getCustom_hs_code()).equalsIgnoreCase(
																CommonUtil.deNull(cellData_CustomHSCode)))
												.count();
										if (hsExist > 0) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_UpdateHSCode_Duplicate);
											commentsList.add(comments);
										} 
									}
								}
							}
							if (cellData_na.equalsIgnoreCase(ConstantUtil.action_update)) {
								String blNbr = cellData_bill;
								String temphs = "";
								if(!CommonUtil.deNull(cellData).isEmpty() && hsCodeSubCodeDropdownList.contains(cellData)) {
									String[] hsCode = cellDataSplit(cellData);
									temphs = hsCode[0].trim() + "-" + hsCode[1].trim() + "-" + hsCode[2].trim();
								}
								if (!temphs.equalsIgnoreCase(manifestDetail.getHs_code_sub_code())) {
									if(cm.getEdo_created()) {
										long exist = miscDetailSubHsCodeListUpdate.stream().parallel()
												.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr))
												.count();
										if(exist == 0) {
											cm.setSubHSUpdate(false);	
										} else {
											cm.setSubHSUpdate(true);
										}
										
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.Error_M20204);
										commentsList.add(comments);
									} else {
										if (manifestmultipleHSDetailExist) {
											List<CargoManifest> manifestHSDetailTmp = manifestHSDetail;
											long hsExist = manifestHSDetailTmp.stream().parallel()
													.filter((s) -> CommonUtil.deNull(s.getHs_code_sub_code())
															.equalsIgnoreCase(CommonUtil.deNull(cellData).trim())
															&& CommonUtil.deNull(s.getCustom_hs_code()).equalsIgnoreCase(
																	CommonUtil.deNull(cellData_CustomHSCode)))
													.count();
											if (hsExist > 0) {
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_UpdateHSCode_Duplicate);
												commentsList.add(comments);
											} else {
												if (miscDetailSubHsCodeListUpdate.size() == 0) {
													Comments comments = new Comments();
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(ConstantUtil.ErrorMsg_UpdateHSCode_Compulsary);
													commentsList.add(comments);
												} else {
													long exist = miscDetailSubHsCodeListUpdate.stream().parallel()
															.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr))
															.count();
													if (exist > 0) {
														int indexHSCode = IntStream
																.range(0, miscDetailSubHsCodeListUpdate.size())
																.filter(k -> CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(k)
																		.getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(cellData))
																		&& CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(k)
																				.getTypeCode()).equalsIgnoreCase(blNbr))
																.findFirst().orElse(-1);
														int indexCargoDesc = IntStream
																.range(0, miscDetailCrgDescSubUpdate.size())
																.filter(k -> CommonUtil.deNull(miscDetailCrgDescSubUpdate.get(k)
																		.getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(cellData_CargoDesc))
																		&& CommonUtil.deNull(miscDetailCrgDescSubUpdate.get(k)
																				.getTypeCode()).equalsIgnoreCase(blNbr))
																.findFirst().orElse(-1);
														int indexCustomHSCode = IntStream
																.range(0, miscDetailCustomSubUpdate.size())
																.filter(k -> CommonUtil.deNull(miscDetailCustomSubUpdate.get(k)
																		.getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(cellData_CustomHSCode))
																		&& CommonUtil.deNull(miscDetailCustomSubUpdate.get(k)
																				.getTypeCode()).equalsIgnoreCase(blNbr))
																.findFirst().orElse(-1);
														if (indexHSCode < 0 && (indexCargoDesc >= 0 && indexCustomHSCode >= 0)) {
															Comments comments = new Comments();
															comments.setKey(manifestUploadConfig.getAttr_name());
															comments.setMessage(ConstantUtil.ErrorMsg_UpdateHSCode_MainSub);
															commentsList.add(comments);
														} else if (indexHSCode < 0 && indexCargoDesc < 0 && (CommonUtil.deNull(cellData_CustomHSCode).isEmpty() || indexCustomHSCode < 0)) {
															Comments comments = new Comments();
															comments.setKey(manifestUploadConfig.getAttr_name());
															comments.setMessage(ConstantUtil.ErrorMsg_UpdateHSCode_Compulsary);
															commentsList.add(comments);
														} else{
															cm.setSubHSUpdate(true);
														}
													} else {
														Comments comments = new Comments();
														comments.setKey(manifestUploadConfig.getAttr_name());
														comments.setMessage(ConstantUtil.ErrorMsg_UpdateHSCode_Compulsary);
														commentsList.add(comments);
													}
												}
											}
										} else {
											long exist = miscDetailSubHsCodeListUpdate.stream().parallel()
													.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr))
													.count();
											if(exist == 0) {
												cm.setSubHSUpdate(false);	
											} else {
												cm.setSubHSUpdate(true);
											}
										}
									}
								} else {
									long exist = miscDetailSubHsCodeListUpdate.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr))
											.count();
									if(exist == 0) {
										cm.setSubHSUpdate(false);	
									} else {
										cm.setSubHSUpdate(true);
									}
								}
							}
							if (cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS)) {
								String blNbr = cellData_bill;
								long mainExist = miscDetailHSCodelListUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
								if (!CommonUtil.deNull(cm.getOldHSCode()).isEmpty()) {
									if(cm.getEdo_created()) {										
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.Error_M20204);
										commentsList.add(comments);
									} else {
										String[] oldhsCode = cellDataSplit(cellData_oldHSCode);
										String temphs = oldhsCode[0].trim() + "-" + oldhsCode[1].trim() + "-" + oldhsCode[2].trim();
										if (manifestmultipleHSDetailExist) {
											List<CargoManifest> manifestHSDetailTmp = manifestHSDetail;
											long hsExist = manifestHSDetailTmp.stream().parallel()
													.filter((s) -> CommonUtil.deNull(s.getHs_code_sub_code())
															.equalsIgnoreCase(CommonUtil.deNull(cellData).trim())
															&& CommonUtil.deNull(s.getCustom_hs_code()).equalsIgnoreCase(
																	CommonUtil.deNull(cellData_CustomHSCode)))
													.count();
											int multipleHSDeclared = 0;
											for (int j = 0; j < miscDetailSubHsCodeListUpdate.size(); j++) {
												MiscDetail msc = miscDetailSubHsCodeListUpdate.get(j);
												if (CommonUtil.deNull(msc.getTypeCode()).equalsIgnoreCase(blNbr)
														&& CommonUtil.deNull(msc.getTypeValue())
																.equalsIgnoreCase(CommonUtil.deNull(cellData))) {
													if (CommonUtil.deNull(miscDetailCustomSubUpdate.get(j).getTypeCode())
															.equalsIgnoreCase(blNbr)
															&& CommonUtil.deNull(miscDetailCustomSubUpdate.get(j).getTypeValue())
																	.equalsIgnoreCase(cellData_CustomHSCode)) {
														multipleHSDeclared++;
													}
												}
											}
											if (hsExist > 0) {
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_UpdateHSCode_Duplicate);
												commentsList.add(comments);
											} else if (multipleHSDeclared > 1) {
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_UpdateHSCode_Duplicate2);
												commentsList.add(comments);
											} else {
												if(CommonUtil.deNull(cm.getOldHSCode()).equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getHs_code()))
															&& CommonUtil.deNull(cm.getOldHSCode_fr())
																	.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getHs_sub_code_fr()))
															&& CommonUtil.deNull(cm.getOldHSCode_to())
																	.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getHs_sub_code_to()))
															&& CommonUtil.deNull(cm.getCustom_hs_code())
																	.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getCustom_hs_code()))) {
													cm.setMainSub(true);
												}
												if (mainExist == 0) {
													if (CommonUtil.deNull(cm.getOldHSCode()).equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getHs_code()))
															&& CommonUtil.deNull(cm.getOldHSCode_fr())
																	.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getHs_sub_code_fr()))
															&& CommonUtil.deNull(cm.getOldHSCode_to())
																	.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getHs_sub_code_to()))
															&& CommonUtil.deNull(cm.getCustom_hs_code())
																	.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getCustom_hs_code()))) {
														Comments comments = new Comments();
														comments.setKey(manifestUploadConfig.getAttr_name());
														comments.setMessage(ConstantUtil.ErrorMsg_MainNotExist_HSCode);
														commentsList.add(comments);
													} else {
														cm.setSubHSUpdate(false);
													}
												} else {
													long exist = miscDetailHSCodelListUpdate.stream().parallel()
															.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr))
															.count();
													if (exist > 0) {
														int index = IntStream.range(0, miscDetailHSCodelListUpdate.size())
																.filter(l -> CommonUtil.deNull(miscDetailHSCodelListUpdate.get(l).getTypeCode())
																		.equalsIgnoreCase(blNbr))
																.findFirst().orElse(-1);
														int indexCustom = IntStream.range(0, miscDetailCustomMainUpdate.size())
																.filter(l -> CommonUtil.deNull(miscDetailCustomMainUpdate.get(l).getTypeCode())
																		.equalsIgnoreCase(blNbr))
																.findFirst().orElse(-1);
														if (CommonUtil.deNull(temphs).equalsIgnoreCase(
																CommonUtil.deNull(manifestDetail.getHs_code_sub_code()))
																&& !CommonUtil.deNull(cellData).equalsIgnoreCase(CommonUtil.deNull(miscDetailHSCodelListUpdate
																				.get(index).getTypeValue()))
																&& CommonUtil.deNull(cellData_CustomHSCode)
																		.equalsIgnoreCase(CommonUtil.deNull(
																				miscDetailCustomMainUpdate.get(indexCustom)
																						.getTypeValue()))) {
															Comments comments = new Comments();
															comments.setKey(manifestUploadConfig.getAttr_name());
															comments.setMessage(ConstantUtil.ErrorMsg_UpdateHSCode_MainSub);
															commentsList.add(comments);
														} else {
															cm.setSubHSUpdate(false);
														}
													} 
												}
											}
										} else {
											if (mainExist == 0) {
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_MainNotExist_HSCode);
												commentsList.add(comments);
											} else {
												if (CommonUtil.deNull(cm.getOldHSCode()).equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getHs_code()))
														&& CommonUtil.deNull(cm.getOldHSCode_fr())
																.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getHs_sub_code_fr()))
														&& CommonUtil.deNull(cm.getOldHSCode_to())
																.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getHs_sub_code_to()))
														&& CommonUtil.deNull(cm.getCustom_hs_code())
																.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getCustom_hs_code()))) {
													cm.setMainSub(true);
												}
											}
										}
									}
								} else {
									// If old hs code is empty
									List<CargoManifest> manifestHSDetailTmp = manifestHSDetail;
									long hsExist = manifestHSDetailTmp.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s.getHs_code_sub_code()).equalsIgnoreCase(CommonUtil.deNull(cellData).trim())).count();
									long errorOldHSCodeInvalid = commentsList.stream().parallel().filter((s) -> CommonUtil.deNull(s.getMessage()).equalsIgnoreCase(ConstantUtil.ErrorMsg_OldHSCode_Declared)).count();
									long errorOldHSCodeCompulsary = commentsList.stream().parallel().filter((s) -> CommonUtil.deNull(s.getMessage()).equalsIgnoreCase(ConstantUtil.ErrorMsg_OldHSCode_Compulsary)).count();
									if (hsExist == 0 && errorOldHSCodeInvalid == 0 && errorOldHSCodeCompulsary == 0) {
										if(cm.getEdo_created()) {										
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.Error_M20204);
											commentsList.add(comments);
										} else {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_UpdateHSCode_Invalid);
											commentsList.add(comments);
										}
									} else {
										if(mainExist == 0) {
											if (manifestDetail != null && CommonUtil.deNull(cm.getHs_code()).equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getHs_code()))
												&& CommonUtil.deNull(cm.getHs_sub_code_fr())
														.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getHs_sub_code_fr()))
												&& CommonUtil.deNull(cm.getHs_sub_code_to())
														.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getHs_sub_code_to()))
												&& CommonUtil.deNull(cm.getCustom_hs_code())
														.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getCustom_hs_code()))) {
												cm.setMainSub(true);
											}
										} else if (mainExist == 1) {
											int mainSub = IntStream.range(0, miscDetailHSCodelListUpdate.size())
													.filter(n -> CommonUtil.deNull(miscDetailHSCodelListUpdate.get(n).getTypeCode()).equalsIgnoreCase(CommonUtil.deNull(blNbr))
															&& CommonUtil.deNull(miscDetailHSCodelListUpdate.get(n).getTypeValue()).equalsIgnoreCase(cellData)
															&& CommonUtil.deNull(miscDetailCustomMainUpdate.get(n).getTypeCode()).equalsIgnoreCase(blNbr)
															&& CommonUtil.deNull(miscDetailCustomMainUpdate.get(n).getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(cellData_CustomHSCode)))
													.findFirst().orElse(-1);
											if(mainSub > 0) {
												cm.setMainSub(true);
											}
										}
									}
								}
							} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
								List<CargoManifest> manifestHSDetailTmp = manifestHSDetail;
								long hsExist = manifestHSDetailTmp.stream().parallel()
										.filter((s) -> CommonUtil.deNull(s.getHs_code_sub_code()).equalsIgnoreCase(CommonUtil.deNull(cellData).trim())).count();
								if (hsExist == 0) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_UpdateHSCode_Invalid);
									commentsList.add(comments);
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.cargo_selection)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							removeColorsAndComment(ref, sheet, no_style);
							String temphs = CommonUtil.getCellData(
									CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_hsCode_index : ConstantUtil.hsCode_index) + String.valueOf(i), sheet);
							String hsCodeCheck = "";
							String hsCode = "";
							
							if (!cm.getEdo_created()) {
								if (!CommonUtil.deNull(temphs).isEmpty() && hsCodeSubCodeDropdownList.contains(temphs)) {
									String[] hsCodeArr = cellDataSplit(temphs);
									hsCodeCheck = (hsCodeArr[0].trim() + "(" + hsCodeArr[1].trim() + "-"
											+ hsCodeArr[2].trim() + ")").trim();
									hsCode = (hsCodeArr[0].trim() + hsCodeArr[1].trim() + hsCodeArr[2].trim())
											.trim();
								}
								if (CommonUtil.deNull(cellData) == "") {
									if (!deleteflag && hsSubCodeValues_mandatoryCheck.contains(hsCodeCheck)) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_CargoSelection);
										commentsList.add(comments);
									}
								} else if (!deleteflag && cargoSelectionDropdownList.stream()
										.filter(x -> x.getTypeValue() != null
										&& x.getTypeValue().equalsIgnoreCase(cellData))
								.findFirst().orElse(null) == null) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_InvalidItemFromDroDown);
									commentsList.add(comments);
								}else if (!deleteflag && !validCargoDescription(cargoSelectionDropdownList,cellData,hsCode )) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Invalid_CargoSelection);
									commentsList.add(comments);
								} 
								else if (!deleteflag && !hsSubCodeValues_mandatoryCheck.contains(hsCodeCheck)) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Invalid_CargoSelection);
									commentsList.add(comments);
								}
								else {

									cargoSelect = manifestRepo.CargoSelectionData(cellData);
									if (cargoSelect != null) {
										cm.setCargo_selection(cargoSelect.getCargo_selection_cd());
									} else {
										if (!deleteflag) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_InvalidItemFromDroDown);
											commentsList.add(comments);
										}
									}
								}
							} else {
								if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
									cargoSelect = CommonUtil.deNull(cellData) == "" ? manifestRepo.CargoSelectionData(cellData) : null;
									String cellDataTmp = CommonUtil.deNull(cellData).isEmpty() ? "" : (cargoSelect != null ? cargoSelect.getCargo_selection_cd() : "");
									if (!CommonUtil.deNull(cellDataTmp)
											.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getCargo_selection()))) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.Error_M20204);
										commentsList.add(comments);
									}
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.cargo_marking)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData="";
							if(cellData!=null && cellData!="")
							{
								upperCellData=cellData.toUpperCase();
							}
						
							removeColorsAndComment(ref, sheet, no_style);
							if (!cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
								if (!cm.getEdo_created()) {
									if (CommonUtil.deNull(upperCellData) == "") {
										if (!deleteflag && manifestUploadConfig.getMandatory_ind()
												.equalsIgnoreCase(ConstantUtil.yes)) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_CargoMarking);
											commentsList.add(comments);
										}
									} else if (upperCellData.length() > 200) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Length_CargoMarking);
										commentsList.add(comments);
									} else {
										cm.setCargo_marking(upperCellData);
									}
								} else {
									if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
										if (!CommonUtil.deNull(cellData)
												.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getCargo_marking()))) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.Error_M20204);
											commentsList.add(comments);
										}
									}
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.number_of_packages)) {
							String blNbr = CommonUtil.deNull(cellData_bill);
							MiscDetail msc = new MiscDetail();
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String afterDot = CommonUtil.deNull(cellData).indexOf(".") > 0 ? CommonUtil.deNull(cellData).substring(CommonUtil.deNull(cellData).indexOf(".") + 1) : "0";

							cellData = CommonUtil.deNull(cellData).isEmpty() ? CommonUtil.deNull(cellData) : afterDot.length()>1 ? cellData : cellData.replace(".0", "");
							String cellData_CustomHSCode = CommonUtil.getCellData(
									CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_custom_index : ConstantUtil.custom_index) + String.valueOf(i),
									sheet);
							msc.setTypeCode(cellData_bill);
							msc.setTypeValue(cellData);
							List<CargoManifest> manifestHSDetailTmp = manifestHSDetail;
							int index = -1;
							String prevPkg = "";
							boolean haveInitialError = false;
							removeColorsAndComment(ref, sheet, no_style);
							if(cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS) || cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
								String customHsCode = cm.isCustomChanged() ? cm.getOldCustom() : CommonUtil.deNull(cellData_CustomHSCode);
								if(CommonUtility.deNull(cm.getOldHSCode()).isEmpty()) {
									for (int j = 0; j < manifestHSDetail.size(); j++) {
										if (CommonUtil.deNull(cm.getHs_code()).equalsIgnoreCase(CommonUtil.deNull(manifestHSDetail.get(j).getHs_code()))
												&& CommonUtil.deNull(cm.getHs_sub_code_fr())
														.equals(CommonUtil.deNull(manifestHSDetail.get(j).getHs_sub_code_fr()))
														&& CommonUtil.deNull(cm.getHs_sub_code_to())
														.equals(CommonUtil.deNull(manifestHSDetail.get(j).getHs_sub_code_to()))
												&& cellData_bill.equalsIgnoreCase(
														manifestHSDetail.get(j).getBills_of_landing_no())
												&& CommonUtil.deNull(manifestHSDetail.get(j).getCustom_hs_code())
												.equalsIgnoreCase(CommonUtil.deNull(customHsCode))) {
											prevPkg = manifestHSDetail.get(j).getNumber_of_packages();
											index = j;
											break;
										}
									}
									if (CommonUtil.deNull(prevPkg).isEmpty()) {
										String crgDesc = cm.getCargo_description();
										String cstmHs = cm.isCustomChanged() ? CommonUtil.deNull(cm.getOldCustom()) : CommonUtil.deNull(cm.getCustom_hs_code());
										int indexCrgDesc = IntStream.range(0, manifestHSDetailTmp.size())
												.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getCargo_description())
														.equalsIgnoreCase(CommonUtil.deNull(crgDesc).trim()))
												.findFirst().orElse(-1);
										int indexCrgCustom = IntStream.range(0, manifestHSDetailTmp.size())
												.filter(n -> CommonUtil.deNull(manifestHSDetailTmp.get(n).getCustom_hs_code())
														.equalsIgnoreCase(CommonUtil.deNull(cstmHs).trim()))
												.findFirst().orElse(-1);
										index = indexCrgDesc >= 0 ? indexCrgDesc : indexCrgCustom;
										prevPkg = index >=0 ? manifestHSDetailTmp.get(index).getNumber_of_packages() : "0";
									}
								} else {
									for (int j = 0; j < manifestHSDetail.size(); j++) {
										if (CommonUtil.deNull(cm.getOldHSCode()).equalsIgnoreCase(CommonUtil.deNull(manifestHSDetail.get(j).getHs_code()))
												&& CommonUtil.deNull(cm.getOldHSCode_fr())
														.equals(CommonUtil.deNull(manifestHSDetail.get(j).getHs_sub_code_fr()))
														&& CommonUtil.deNull(cm.getOldHSCode_to())
														.equals(CommonUtil.deNull(manifestHSDetail.get(j).getHs_sub_code_to()))
												&& cellData_bill.equalsIgnoreCase(
														manifestHSDetail.get(j).getBills_of_landing_no())
												&& CommonUtil.deNull(manifestHSDetail.get(j).getCustom_hs_code())
												.equalsIgnoreCase(CommonUtil.deNull(customHsCode))) {
											prevPkg = manifestHSDetail.get(j).getNumber_of_packages();
											index = j;
											break;
										}
									}
								}
								if(index >= 0) {
									cm.setMft_hscode_seq_nbr(manifestHSDetailTmp.get(index).getMft_hscode_seq_nbr());
								}
							} 
							if(!cm.getEdo_created()) {
								if (CommonUtil.deNull(cellData) == "") {
									if (!deleteflag && !cm.getAction().equalsIgnoreCase(ConstantUtil.action_deleteHS)
											&& manifestUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_NoOfPkg);
										commentsList.add(comments);
										haveInitialError = true;
									}
								} else if (!deleteflag && !cm.getAction().equalsIgnoreCase(ConstantUtil.action_deleteHS) && !CommonUtil.isInteger(cellData)) {
									log.info(" %%%%%% bErrorMsg_NonInteger :"+ cellData);
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_NonInteger);
									commentsList.add(comments);
									haveInitialError = true;
								} else if (!deleteflag && !cm.getAction().equalsIgnoreCase(ConstantUtil.action_deleteHS) && CommonUtil.isNumeric(cellData)
										&& Double.parseDouble(cellData) <= 0) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Valid_noOfPkg);
									commentsList.add(comments);
									haveInitialError = true;
									// START FTZ Validate Total - NS JULY 2024
								} else if (!deleteflag && !cm.getAction().equalsIgnoreCase(ConstantUtil.action_deleteHS) && cellData.length() > 6) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_NoOfPkgLength);
									commentsList.add(comments);
									haveInitialError = true;
								} else {
									BigDecimal cellDataStrRemZero = new BigDecimal(cellData);
									cellData = cellDataStrRemZero.stripTrailingZeros().toPlainString();
									cm.setNumber_of_packages(cellData);
								} 

								int indexMainAdd = IntStream.range(0, manifestRecords.size())
										.filter(l -> CommonUtil.deNull(manifestRecords.get(l)
												.getBills_of_landing_no()).equalsIgnoreCase(CommonUtil.deNull(blNbr)) && CommonUtil.deNull(manifestRecords.get(l)
														.getAction()).equalsIgnoreCase(CommonUtil.deNull(ConstantUtil.action_add)))
										.findFirst().orElse(-1);
								int indexMainUpdate = IntStream.range(0, manifestRecords.size())
										.filter(l -> CommonUtil.deNull(manifestRecords.get(l)
												.getBills_of_landing_no()).equalsIgnoreCase(CommonUtil.deNull(blNbr)) && CommonUtil.deNull(manifestRecords.get(l)
														.getAction()).equalsIgnoreCase(CommonUtil.deNull(ConstantUtil.action_update)))
										.findFirst().orElse(-1);
								boolean mainAddHaveNbrError = false;
								boolean mainUpdateHaveNbrError = false;
								if (indexMainAdd >= 0) {
									long error = (manifestRecords.get(indexMainAdd).getErrorInfo()).stream().parallel()
											.filter((s) -> s.getMessage()
													.equalsIgnoreCase(ConstantUtil.ErrorMsg_NoOfPkgLength)
													|| s.getMessage().equalsIgnoreCase(ConstantUtil.ErrorMsg_Valid_noOfPkg)
													|| s.getMessage().equalsIgnoreCase(ConstantUtil.ErrorMsg_NonInteger)
													|| s.getMessage()
															.equalsIgnoreCase(ConstantUtil.ErrorMsg_Mandatory_NoOfPkg))
											.count();
									mainAddHaveNbrError = error > 0;
								} else if (indexMainUpdate >= 0) {
									long error = (manifestRecords.get(indexMainUpdate).getErrorInfo()).stream().parallel()
											.filter((s) -> s.getMessage()
													.equalsIgnoreCase(ConstantUtil.ErrorMsg_NoOfPkgLength)
													|| s.getMessage().equalsIgnoreCase(ConstantUtil.ErrorMsg_Valid_noOfPkg)
													|| s.getMessage().equalsIgnoreCase(ConstantUtil.ErrorMsg_NonInteger)
													|| s.getMessage()
															.equalsIgnoreCase(ConstantUtil.ErrorMsg_Mandatory_NoOfPkg))
											.count();
									mainUpdateHaveNbrError = error > 0;
								}
								if (cellData_na.equalsIgnoreCase(ConstantUtil.action_add) && !haveInitialError) {
									long exist = miscDetailNbrPkgListAdd.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									if (exist > 0) {
										int total = 0;
										for (int j = 0; j < miscDetailNbrPkgListAdd.size(); j++) {
											if(CommonUtil.deNull(miscDetailNbrPkgListAdd.get(j).getTypeCode()).equalsIgnoreCase(cellData_bill)) {
												total = total + Integer.parseInt(CommonUtil.deNull(miscDetailNbrPkgListAdd.get(j).getTypeValue()));
											}
										}
										if (total > Integer.parseInt(cellData) || total < Integer.parseInt(cellData)) {
											miscNbrPkgsMainAdd.add(msc);
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_Exceed_noOfPkg + cellData);
											commentsList.add(comments);
										}
									}
								} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS) && !haveInitialError && !mainAddHaveNbrError && !mainUpdateHaveNbrError) {
									int total = 0;
									long exist = miscNbrPkgsMainAdd.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									long existUpdate = mainBLNbrListUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count();
									long existUpdateError = miscNbrPkgsMainUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									long existUpdateHsError = miscNbrPkgsMainUpdateHS.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									long existdeleteHsError = miscNbrPkgsdeleteHS.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									if(exist > 0 || existUpdateError > 0 || existUpdateHsError > 0 || existdeleteHsError > 0) {
										if(exist > 0) {
											for (int j = 0; j < miscNbrPkgsMainAdd.size(); j++) {
												if(CommonUtil.deNull(miscNbrPkgsMainAdd.get(j).getTypeCode()).equalsIgnoreCase(CommonUtil.deNull(cellData_bill))) {
													total = Integer.parseInt(CommonUtil.deNull(miscNbrPkgsMainAdd.get(j).getTypeValue()));
												}
											}
										} else if (existUpdateError > 0) {
											for (int j = 0; j < miscNbrPkgsMainUpdate.size(); j++) {
												if(CommonUtil.deNull(miscNbrPkgsMainUpdate.get(j).getTypeCode()).equalsIgnoreCase(CommonUtil.deNull(cellData_bill))) {
													total = Integer.parseInt(CommonUtil.deNull(miscNbrPkgsMainUpdate.get(j).getTypeValue()));
												}
											}
										} else if (existUpdateHsError > 0 || existdeleteHsError > 0) {
											if (existUpdate > 0) {
												int indexUpdate = IntStream.range(0, miscDetailVolMainUpdate.size())
														.filter(l -> CommonUtil
																.deNull(miscDetailNbrPkgsMainUpdate.get(l).getTypeCode())
																.equalsIgnoreCase(CommonUtil.deNull(blNbr)))
														.findFirst().orElse(-1);
												total = Integer.parseInt(miscDetailNbrPkgsMainUpdate.get(indexUpdate).getTypeValue());
											} else {
												total = Integer.parseInt(manifestDetail.getNumber_of_packages());
											}
										}
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Exceed_noOfPkg + total);
										commentsList.add(comments);
									}
								} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_update) && !cannotDeleteSub && !haveInitialError) {
									if(!cellData.equalsIgnoreCase(manifestDetail.getNumber_of_packages())) {
										cm.setValueChanges(true);
										if(manifestmultipleHSDetailExist) {
											int total = 0;
											long existAdd = miscDetailNbrPkgListAdd.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
											long existUpdate = miscDetailNbrPkgListUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
											long existDelete = miscDetailNbrPkgListDelete.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
											if (existUpdate == 0 && existDelete == 0 && existAdd == 0) {
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_HSCode_Compulsary_noOfPkg);
												commentsList.add(comments);
											} else {
												Map<Integer, Integer> indexNotUpdate = new HashMap<Integer, Integer>();
												// If Add hs code exist, should count as total
												if(existAdd > 0) {
													for (int j = 0; j < miscDetailNbrPkgListAdd.size(); j++) {
														if (CommonUtil.deNull(miscDetailNbrPkgListAdd.get(j).getTypeCode())
																.equalsIgnoreCase(CommonUtil.deNull(cellData_bill))) {
															total = total + Integer.parseInt(
																	CommonUtil.deNull(miscDetailNbrPkgListAdd.get(j).getTypeValue()));
														}
													}
												}
												
												// If update hs code exists
												if(existUpdate > 0) {
													for (int j = 0; j < miscDetailNbrPkgListUpdate.size(); j++) {
														// For number of packages declared in excel
														if (CommonUtil.deNull(miscDetailNbrPkgListUpdate.get(j).getTypeCode())
																.equalsIgnoreCase(cellData_bill)) {
															total = total + Integer.parseInt(
																	CommonUtil.deNull(miscDetailNbrPkgListUpdate.get(j).getTypeValue()));
														}
														
														// For number of packages not declared in excel (Update HS Code)
														String tmp = CommonUtil.deNull(miscDetailsubOldHSCodelListUpdate.get(j).getTypeValue())
																.isEmpty()
																		? CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(j).getTypeValue())
																		: CommonUtil.deNull(miscDetailsubOldHSCodelListUpdate.get(j)
																				.getTypeValue());
														String tmpCustom = CommonUtil.deNull(miscDetailCustomSubUpdate.get(j).getTypeValue());
														String tmpCrgDesc = CommonUtil.deNull(miscDetailCrgDescSubUpdate.get(j).getTypeValue());
														int indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l)
																		.getHs_code_sub_code()).equalsIgnoreCase(tmp)
																		&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																				.getCustom_hs_code()).equalsIgnoreCase(tmpCustom))
																.findFirst().orElse(-1);
														if(indexHs < 0) {
															indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																	.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																			.equalsIgnoreCase(CommonUtil.deNull(tmp))
																			&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																					.getCargo_description()).equalsIgnoreCase(tmpCrgDesc))
																	.findFirst().orElse(-1);
														}
														if (indexHs >= 0) {
															indexNotUpdate.put(indexHs, indexHs);
														}
													}
													
												}
												
												// If delete hs code exists
												if (existDelete > 0) {
													for (int j = 0; j < miscDetailSubHsCodeListDel.size(); j++) {
														// For number of packages not declared in excel (Delete HS Code)
														if (CommonUtil.deNull(miscDetailSubHsCodeListDel.get(j).getTypeCode())
																.equalsIgnoreCase(CommonUtil.deNull(cellData_bill))) {
															String tmp = CommonUtil.deNull(miscDetailSubOldHsCodeListDel.get(j).getTypeValue())
																	.isEmpty()
																			? CommonUtil.deNull(miscDetailSubHsCodeListDel.get(j)
																					.getTypeValue())
																			: CommonUtil.deNull(miscDetailSubOldHsCodeListDel.get(j)
																					.getTypeValue());
															String tmpCustom = CommonUtil.deNull(miscDetailSubCustomHSCodeListDel.get(j).getTypeValue());
															String tmpCrgDesc = CommonUtil.deNull(miscDetailSubCargoDescListDel.get(j).getTypeValue());
															int indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																	.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l)
																			.getHs_code_sub_code()).equalsIgnoreCase(CommonUtil.deNull(tmp))
																			&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																					.getCustom_hs_code()).equalsIgnoreCase(tmpCustom))
																	.findFirst().orElse(-1);
															if(indexHs < 0) {
																indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																		.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																				.equalsIgnoreCase(CommonUtil.deNull(tmp))
																				&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																						.getCargo_description()).equalsIgnoreCase(tmpCrgDesc))
																		.findFirst().orElse(-1);
															}
															if (indexHs >= 0) {
																indexNotUpdate.put(indexHs, indexHs);
															}
														}
													}
												}

												for (int j = 0; j < manifestHSDetailTmp.size(); j++) {
													if (!indexNotUpdate.containsValue(j)) {
														total = total + Integer.parseInt(
																manifestHSDetailTmp.get(j).getNumber_of_packages());
													}
												}
												
											}

											if (total > Integer.parseInt(cellData)
													|| total < Integer.parseInt(cellData)) {
												miscNbrPkgsMainUpdate.add(msc);
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_Exceed_noOfPkg + cellData);
												commentsList.add(comments);
											}
										}
									} 
								} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS) && !cannotDeleteSub && !haveInitialError && !mainUpdateHaveNbrError) {
									int total = 0;
									long errorOldHSCodeCompulsary = commentsList.stream().parallel().filter((s) -> CommonUtil.deNull(s.getMessage()).equalsIgnoreCase(ConstantUtil.ErrorMsg_OldHSCode_Compulsary)).count();
									long errorDuplicateCustomHSCode = commentsList.stream().parallel().filter((s) -> CommonUtil.deNull(s.getMessage()).equalsIgnoreCase(ConstantUtil.ErrorMsg_Same_Custom)).count();
									// Error in Updating Main, automatically will show error in updating sub HS Code
									long errorInUpdateMain = miscNbrPkgsMainUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									if (errorInUpdateMain > 0) {
										for (int j = 0; j < miscNbrPkgsMainUpdate.size(); j++) {
											if(CommonUtil.deNull(miscNbrPkgsMainUpdate.get(j).getTypeCode()).equalsIgnoreCase(CommonUtil.deNull(cellData_bill))) {
												total = Integer.parseInt(CommonUtil.deNull(miscNbrPkgsMainUpdate.get(j).getTypeValue()));
											}
										}
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Exceed_noOfPkg + total);
										commentsList.add(comments);
									} else if (errorInUpdateMain == 0 && errorOldHSCodeCompulsary == 0 && errorDuplicateCustomHSCode == 0) { 
										Map<Integer, Integer> indexNotUpdate = new HashMap<Integer, Integer>();
										// No error in Updating Main
										long existAdd = miscDetailNbrPkgListAdd.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
										long updateMainExist = mainBLNbrListUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count();
										long deletExist = miscDetailNbrPkgListDelete.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
										long updateSubExist = miscDetailNbrPkgListUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
										if(!CommonUtil.deNull(cellData).equalsIgnoreCase(prevPkg)) {
											cm.setValueChanges(true);
										}
										// There is changes in package number
										if (updateMainExist == 0 && deletExist == 0 && existAdd == 0 && updateSubExist == 0 && !CommonUtil.deNull(cellData).equalsIgnoreCase(prevPkg)) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_MainNotExist_Update);
											commentsList.add(comments);
										} else if (deletExist > 0 || updateMainExist > 0 || existAdd > 0 || updateSubExist > 0) {
											String mainPkg = "";
											// Check for total - does delete packages updated to hs detail matches main
											// packages
											for (int j = 0; j < miscDetailNbrPkgListUpdate.size(); j++) {
												// For Number of packages declared in excel
												if (CommonUtil.deNull(miscDetailNbrPkgListUpdate.get(j).getTypeCode())
														.equalsIgnoreCase(cellData_bill)) {
													total = total + Integer
															.parseInt(CommonUtil.deNull(miscDetailNbrPkgListUpdate.get(j).getTypeValue()));
												}

												// For number of packages not declared in excel
												if (CommonUtil.deNull(miscDetailNbrPkgListUpdate.get(j).getTypeCode())
														.equalsIgnoreCase(cellData_bill)) {
													// Check index of hscode declared in excel in db
													String tmp = CommonUtil.deNull(miscDetailsubOldHSCodelListUpdate.get(j).getTypeValue())
															.isEmpty() ? CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(j).getTypeValue())
																	: CommonUtil.deNull(miscDetailsubOldHSCodelListUpdate.get(j)
																			.getTypeValue());
													String tmpCustom = CommonUtil.deNull(miscDetailCustomSubUpdate.get(j).getTypeValue());
													String tmpCrgDesc = CommonUtil.deNull(miscDetailCrgDescSubUpdate.get(j).getTypeValue());
													int indexHs = IntStream.range(0, manifestHSDetailTmp.size())
															.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																	.equalsIgnoreCase(CommonUtil.deNull(tmp))
																	&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																			.getCustom_hs_code()).equalsIgnoreCase(tmpCustom))
															.findFirst().orElse(-1);
													if(indexHs < 0) {
														indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																		.equalsIgnoreCase(CommonUtil.deNull(tmp))
																		&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																				.getCargo_description()).equalsIgnoreCase(tmpCrgDesc))
																.findFirst().orElse(-1);
													}
													// If its there, do not take is as total
													if (indexHs >= 0) {
														indexNotUpdate.put(indexHs, indexHs);
													}
												}
											}

											if (deletExist > 0) {
												// To not add packages that is being deleted as total
												for (int j = 0; j < miscDetailSubHsCodeListDel.size(); j++) {
													if (CommonUtil.deNull(miscDetailSubHsCodeListDel.get(j).getTypeCode())
															.equalsIgnoreCase(cellData_bill)) {
														String tmp = CommonUtil.deNull(miscDetailSubOldHsCodeListDel.get(j).getTypeValue())
																.isEmpty()
																		? CommonUtil.deNull(miscDetailSubHsCodeListDel.get(j).getTypeValue())
																		: CommonUtil.deNull(miscDetailSubOldHsCodeListDel.get(j)
																				.getTypeValue());
														String tmpCustom = CommonUtil.deNull(miscDetailSubCustomHSCodeListDel.get(j).getTypeValue());
														String tmpCrgDesc = CommonUtil.deNull(miscDetailSubCargoDescListDel.get(j).getTypeValue());
														int indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l)
																		.getHs_code_sub_code()).equalsIgnoreCase(CommonUtil.deNull(tmp))
																		&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																				.getCustom_hs_code()).equalsIgnoreCase(tmpCustom))
																.findFirst().orElse(-1);
														if(indexHs < 0) {
															indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																	.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																			.equalsIgnoreCase(CommonUtil.deNull(tmp))
																			&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																					.getCargo_description()).equalsIgnoreCase(tmpCrgDesc))
																	.findFirst().orElse(-1);
														}
														if (indexHs >= 0) {
															indexNotUpdate.put(indexHs, indexHs);
														}
													}
												}
											}

											if (existAdd > 0) {
												for (int j = 0; j < miscDetailNbrPkgListAdd.size(); j++) {
													// For Number of packages declared in excel (Add)
													if (CommonUtil.deNull(miscDetailNbrPkgListAdd.get(j).getTypeCode())
															.equalsIgnoreCase(cellData_bill)) {
														total = total + Integer
																.parseInt(CommonUtil.deNull(miscDetailNbrPkgListAdd.get(j).getTypeValue()));
													}
												}
											}

											for (int j = 0; j < manifestHSDetailTmp.size(); j++) {
												// Here we got total of packages not included in excel
												if (!indexNotUpdate.containsValue(j)) {
													total = total + Integer
															.parseInt(manifestHSDetailTmp.get(j).getNumber_of_packages());
												}
											}

											if (updateMainExist > 0) {
												int mainNbrIndex = IntStream.range(0, miscDetailNbrPkgsMainUpdate.size())
														.filter(l -> CommonUtil.deNull(miscDetailNbrPkgsMainUpdate.get(l).getTypeCode())
																.equalsIgnoreCase(blNbr))
														.findFirst().orElse(-1);
												mainPkg = CommonUtil.deNull(miscDetailNbrPkgsMainUpdate.get(mainNbrIndex).getTypeValue());
											} else {
												mainPkg = manifestDetail.getNumber_of_packages();
											}

											if (total > Integer.parseInt(mainPkg) || total < Integer.parseInt(mainPkg)) {
												miscNbrPkgsMainUpdateHS.add(msc);
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_Exceed_noOfPkg + mainPkg);
												commentsList.add(comments);
											}
										}
									} 
								} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS) && !cannotDeleteSub && !haveInitialError && !mainUpdateHaveNbrError) {
									int total = 0;
									long errorInvalidHSCode = commentsList.stream().parallel().filter((s) -> CommonUtil.deNull(s.getMessage()).equalsIgnoreCase(ConstantUtil.ErrorMsg_UpdateHSCode_Invalid)).count();
									boolean updateComesFirst = mainBLNbrListUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count() > 0;
									boolean updateSubComesFirst = mainBLNbrListSubUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count() > 0;
									if (updateComesFirst || updateSubComesFirst) { // Meaning update row has been checked before delete row
										long exist = miscNbrPkgsMainUpdate.stream().parallel()
												.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count(); // Error in Main Update exist?
										long existHS = miscNbrPkgsMainUpdateHS.stream().parallel()
												.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count(); // Error in Sub Update exist?
										if (exist > 0 || existHS > 0) {
											total = 0;
											// If error in main update exist, total should be the same as what is declared in Update Main
											if (exist > 0) {
												for (int j = 0; j < miscNbrPkgsMainUpdate.size(); j++) {
													if (CommonUtil.deNull(miscNbrPkgsMainUpdate.get(j).getTypeCode())
															.equalsIgnoreCase(CommonUtil.deNull(cellData_bill))) {
														total = Integer
																.parseInt(CommonUtil.deNull(miscNbrPkgsMainUpdate.get(j).getTypeValue()));
													}
												}
											// If error in sub update exist, total should be the same as what is declared in DB (Main HS)
											} else if (existHS > 0) {
												total = Integer.parseInt(manifestDetail.getNumber_of_packages());
											}
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_Exceed_noOfPkg_Delete + total);
											commentsList.add(comments);
										}
									} else {
										// Delete row comes before update row
										Map<Integer, Integer> indexNotUpdate = new HashMap<Integer, Integer>();
										long updateMainExist = mainBLNbrListUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count();
										long existUpdate = miscDetailNbrPkgListUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
										long existHSAdd = miscDetailNbrPkgListAdd.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).toUpperCase().trim().equalsIgnoreCase(blNbr)).count();
										// If neither have, should throw error
										if(updateMainExist == 0 && existUpdate == 0 && existHSAdd == 0) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_MainOrSubNotExist);
											commentsList.add(comments);
										} else {
											String mainPkgs = "";
											// Check if updated value sync
											if(updateMainExist > 0) {											
												// to compare with total, we take value inserted in excel update
												int mainNbrPkgs = IntStream.range(0, miscDetailNbrPkgsMainUpdate.size())
														.filter(l -> CommonUtil.deNull(miscDetailNbrPkgsMainUpdate.get(l).getTypeCode())
																.equalsIgnoreCase(blNbr))
														.findFirst().orElse(-1);
												mainPkgs = CommonUtil.deNull(miscDetailNbrPkgsMainUpdate.get(mainNbrPkgs).getTypeValue());
											}
											
											// If update hs exist
											if(existUpdate > 0) {
												// Check if updated value sync with Main in DB
												for (int j = 0; j < miscDetailNbrPkgListUpdate.size(); j++) {
													// For Number of packages declared in excel
													if (CommonUtil.deNull(miscDetailNbrPkgListUpdate.get(j).getTypeCode())
															.equalsIgnoreCase(blNbr)) {
														total = total + Integer
																.parseInt(CommonUtil.deNull(miscDetailNbrPkgListUpdate.get(j).getTypeValue()));
													}
													
													// For number of packages not declared in excel
													if (CommonUtil.deNull(miscDetailNbrPkgListUpdate.get(j).getTypeCode())
															.equalsIgnoreCase(cellData_bill)) {
														String tmp = CommonUtil.deNull(miscDetailsubOldHSCodelListUpdate.get(j).getTypeValue())
																.isEmpty() ? CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(j).getTypeValue())
																		: CommonUtil.deNull(miscDetailsubOldHSCodelListUpdate.get(j).getTypeValue());
														String tmpCustom = CommonUtil.deNull(miscDetailCustomSubUpdate.get(j).getTypeValue());
														String tmpCrgDesc = CommonUtil.deNull(miscDetailCrgDescSubUpdate.get(j).getTypeValue());
														int indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																		.equalsIgnoreCase(CommonUtil.deNull(tmp))
																		&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																				.getCustom_hs_code()).equalsIgnoreCase(tmpCustom))
																.findFirst().orElse(-1);
														if(indexHs < 0) {
															indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																	.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																			.equalsIgnoreCase(CommonUtil.deNull(tmp))
																			&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																					.getCargo_description()).equalsIgnoreCase(tmpCrgDesc))
																	.findFirst().orElse(-1);
														}
														if (indexHs >= 0) {
															indexNotUpdate.put(indexHs, indexHs);
														}
													}
												
												}
											}
											
											// Check if add hs exists
											if (existHSAdd > 0) {
												for (int j = 0; j < miscDetailNbrPkgListAdd.size(); j++) {
													// For Number of packages declared in excel
													if (CommonUtil.deNull(miscDetailNbrPkgListAdd.get(j).getTypeCode())
															.equalsIgnoreCase(blNbr)) {
														total = total + Integer.parseInt(CommonUtil
																.deNull(miscDetailNbrPkgListAdd.get(j).getTypeValue()));
													}
												}
											}

											if(updateMainExist == 0) {
												mainPkgs = manifestDetail.getNumber_of_packages();
											}
											
											// Add that is being deleted should not be count 
											for (int k = 0; k < miscDetailSubHsCodeListDel.size(); k++) {
												if (CommonUtil.deNull(miscDetailSubHsCodeListDel.get(k).getTypeCode())
														.equalsIgnoreCase(cellData_bill)) {
													String tmp = CommonUtil.deNull(miscDetailSubOldHsCodeListDel.get(k).getTypeValue())
															.isEmpty() ? CommonUtil.deNull(miscDetailSubHsCodeListDel.get(k).getTypeValue())
																	: CommonUtil.deNull(miscDetailSubOldHsCodeListDel.get(k).getTypeValue());
													String tmpCustom = CommonUtil.deNull(miscDetailSubCustomHSCodeListDel.get(k).getTypeValue());
													String tmpCrgDesc = CommonUtil.deNull(miscDetailSubCargoDescListDel.get(k).getTypeValue());
													int indexHs = IntStream.range(0, manifestHSDetailTmp.size())
															.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																	.equalsIgnoreCase(CommonUtil.deNull(tmp))
																	&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																			.getCustom_hs_code()).equalsIgnoreCase(tmpCustom))
															.findFirst().orElse(-1);
													if(indexHs < 0) {
														indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																		.equalsIgnoreCase(CommonUtil.deNull(tmp))
																		&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																				.getCargo_description()).equalsIgnoreCase(tmpCrgDesc))
																.findFirst().orElse(-1);
													}
													if (indexHs >= 0) {
														indexNotUpdate.put(indexHs, indexHs);
													}
												}
											}

											// Add total of value in DB that is not being touch 
											for (int j = 0; j < manifestHSDetailTmp.size(); j++) {
												if (!indexNotUpdate.containsValue(j)) {
													total = total + Integer
															.parseInt(manifestHSDetailTmp.get(j).getNumber_of_packages());
												}
											}
											
											// Compare value declared in excel, in db with main value
											if (total > Integer.parseInt(mainPkgs) || total < Integer.parseInt(mainPkgs) && errorInvalidHSCode == 0) {
												miscNbrPkgsdeleteHS.add(msc);
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_Exceed_noOfPkg_Delete
														+ manifestDetail.getNumber_of_packages());
												commentsList.add(comments);
											}	
										}
									}
								}
							} else {
								if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
									if (!CommonUtil.deNull(cellData)
											.equalsIgnoreCase(manifestDetail.getNumber_of_packages())) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.Error_M20204);
										commentsList.add(comments);
									}
								} else if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_updateHS)) {
									if (!CommonUtil.deNull(cellData).equalsIgnoreCase(prevPkg)) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.Error_M20204);
										commentsList.add(comments);
									}
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.gross_weight)) {
							String blNbr = cellData_bill;
							MiscDetail msc = new MiscDetail();
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String cellData_CustomHSCode = CommonUtil.getCellData(
									CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_custom_index : ConstantUtil.custom_index) + String.valueOf(i),
									sheet);
							String afterDot = CommonUtil.deNull(cellData).indexOf(".") > 0 ?  CommonUtil.deNull(cellData).substring(CommonUtil.deNull(cellData).indexOf(".") + 1) : "0";
							Double double2decWt = !CommonUtil.deNull(cellData).isEmpty() ? Math.round(Double.parseDouble(cellData) * 100.0) / 100.0 : 0.00;				
							cellData = CommonUtil.deNull(cellData).isEmpty() ? CommonUtil.deNull(cellData) : ((afterDot.length()>1 && !afterDot.equals("00") ) || Integer.valueOf(afterDot) > 0 ) ? String.format("%.2f", double2decWt) : cellData.replace("."+afterDot, "");
							msc.setTypeCode(cellData_bill);
							msc.setTypeValue(cellData);
							List<CargoManifest> manifestHSDetailTmp = manifestHSDetail;
							int index = -1;
							String prevWt = "";
							removeColorsAndComment(ref, sheet, no_style);
							if(cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS) || cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS)) {
								String customHsCode = cm.isCustomChanged() ? cm.getOldCustom() : CommonUtil.deNull(cellData_CustomHSCode);
								if(CommonUtility.deNull(cm.getOldHSCode()).isEmpty()) {
									for (int j = 0; j < manifestHSDetail.size(); j++) {
										if (CommonUtil.deNull(cm.getHs_code()).equalsIgnoreCase(CommonUtil.deNull(manifestHSDetail.get(j).getHs_code()))
												&& CommonUtil.deNull(cm.getHs_sub_code_fr())
														.equals(CommonUtil.deNull(manifestHSDetail.get(j).getHs_sub_code_fr()))
														&& CommonUtil.deNull(cm.getHs_sub_code_to())
														.equals(CommonUtil.deNull(manifestHSDetail.get(j).getHs_sub_code_to()))
												&& cellData_bill.equalsIgnoreCase(
														CommonUtil.deNull(manifestHSDetail.get(j).getBills_of_landing_no()))
												&& CommonUtil.deNull(manifestHSDetail.get(j).getCustom_hs_code())
														.equalsIgnoreCase(CommonUtil.deNull(customHsCode))) {
											prevWt = manifestHSDetail.get(j).getGross_weight_kg();
											index = j;
											break;
										}
									}
									if (CommonUtil.deNull(prevWt).isEmpty()) {
										String crgDesc = cm.getCargo_description();
										String cstmHs = cm.isCustomChanged() ? CommonUtil.deNull(cm.getOldCustom()) : CommonUtil.deNull(cm.getCustom_hs_code());
										int indexCrgDesc = IntStream.range(0, manifestHSDetailTmp.size())
												.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getCargo_description())
														.equalsIgnoreCase(CommonUtil.deNull(crgDesc).trim()))
												.findFirst().orElse(-1);
										int indexCrgCustom = IntStream.range(0, manifestHSDetailTmp.size())
												.filter(n -> CommonUtil.deNull(manifestHSDetailTmp.get(n).getCustom_hs_code())
														.equalsIgnoreCase(CommonUtil.deNull(cstmHs).trim()))
												.findFirst().orElse(-1);
										index = indexCrgDesc >= 0 ? indexCrgDesc : indexCrgCustom;
										prevWt = index >=0 ? manifestHSDetailTmp.get(index).getGross_weight_kg() : "0";
									}
								} else {
									for (int j = 0; j < manifestHSDetail.size(); j++) {
										if (CommonUtil.deNull(cm.getOldHSCode()).equalsIgnoreCase(CommonUtil.deNull(manifestHSDetail.get(j).getHs_code()))
												&& CommonUtil.deNull(cm.getOldHSCode_fr())
														.equals(CommonUtil.deNull(manifestHSDetail.get(j).getHs_sub_code_fr()))
												&& CommonUtil.deNull(cm.getOldHSCode_to())
														.equals(CommonUtil.deNull(manifestHSDetail.get(j).getHs_sub_code_to()))
												&& cellData_bill.equalsIgnoreCase(
														manifestHSDetail.get(j).getBills_of_landing_no())
												&& CommonUtil.deNull(manifestHSDetail.get(j).getCustom_hs_code())
												.equalsIgnoreCase(CommonUtil.deNull(customHsCode))) {
											prevWt = manifestHSDetail.get(j).getGross_weight_kg();
											index = j;
											break;
										}
									}
								}
							}
							if(!cm.getEdo_created()) {
								if (CommonUtil.deNull(cellData) == "") {
									if (!deleteflag
											&& manifestUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(manifestUploadConfig.getAttr_name() + ConstantUtil.mandatory);
										commentsList.add(comments);
									}

								} else if (!deleteflag && !CommonUtil.isNumeric(cellData)) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
									commentsList.add(comments);
								} else if (!deleteflag && CommonUtil.isNumeric(cellData)) {
									if (Double.parseDouble(cellData) < 10) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Valid_MinWeight);
										commentsList.add(comments);
									} else if (Double.parseDouble(cellData) > 20000000) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Valid_MaxWeight);
										commentsList.add(comments);
									} else if (cellData.length() > 8) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage("Weight" + ConstantUtil.ErrorMsg_8Length);
										commentsList.add(comments);
									} else {
										CommonUtil.setCellData(ref, sheet, CommonUtil.get2DecFromStr(cellData));
										cm.setGross_weight_kg(CommonUtil.get2DecFromStr(cellData));
									}

								} else {
									CommonUtil.setCellData(ref, sheet, CommonUtil.get2DecFromStr(cellData));
									cm.setGross_weight_kg(CommonUtil.get2DecFromStr(cellData));
								}
								if (cellData_na.equalsIgnoreCase(ConstantUtil.action_add)) {
									long exist = miscDetailWtListAdd.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									if (exist > 0) {
										double total = 0;
										cm.setMultipleHsCode(true);
										for (int j = 0; j < miscDetailWtListAdd.size(); j++) {
											if (CommonUtil.deNull(miscDetailWtListAdd.get(j).getTypeCode()).equalsIgnoreCase(cellData_bill)) {
												total = total + (!CommonUtil.deNull(miscDetailWtListAdd.get(j).getTypeValue()).isEmpty() ? Double.parseDouble(miscDetailWtListAdd.get(j).getTypeValue()) : 0);
											}
										}
										if (!CommonUtil.deNull(cellData).isEmpty()) {
											if (total > Double.parseDouble(cellData) || total < Double.parseDouble(cellData)) {
												miscNbrWtMainAdd.add(msc);
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_Exceed_Weight + cellData);
												commentsList.add(comments);
											}
										}
									} else {
										cm.setMultipleHsCode(false);
									}
								} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS)) {
									double total = 0;
									long existAdd = mainBLNbrListAdd.stream().parallel().filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count();
									long existUpdate = mainBLNbrListUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count();
									long existSubUpdate = mainBLNbrListSubUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count();
									long exist = miscNbrWtMainAdd.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									long existUpdateError = miscNbrWtMainUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									long existUpdateHsError = miscNbrWtMainUpdateHs.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									long existdeleteHsError = miscNbrWtMainDeleteHs.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									long errorAddBLExist = commentsList.stream().parallel().filter((s) -> CommonUtil.deNull(s.getMessage()).equalsIgnoreCase(ConstantUtil.Error_M20201)).count();
									if ((exist > 0 || existUpdateError > 0 || existUpdateHsError > 0 || existdeleteHsError > 0) && errorAddBLExist == 0) {
										if (exist > 0) {
											// Have error in Add Main
											for (int j = 0; j < miscNbrWtMainAdd.size(); j++) {
												if (CommonUtil.deNull(miscNbrWtMainAdd.get(j).getTypeCode()).equalsIgnoreCase(cellData_bill)) {
													total = Double.parseDouble(miscNbrWtMainAdd.get(j).getTypeValue());
												}
											}
											// Have error in Update Main
										} else if (existUpdateError > 0) {
											for (int j = 0; j < miscNbrWtMainUpdate.size(); j++) {
												if (CommonUtil.deNull(miscNbrWtMainUpdate.get(j).getTypeCode())
														.equalsIgnoreCase(cellData_bill)) {
													total = Double.parseDouble(miscNbrWtMainUpdate.get(j).getTypeValue());
												}
											}
										} else if (existUpdateHsError > 0 || existdeleteHsError > 0) {
											if (existUpdate > 0) {
												int indexUpdate = IntStream.range(0, miscDetailWtMainUpdate.size())
														.filter(l -> CommonUtil
																.deNull(miscDetailWtMainUpdate.get(l).getTypeCode())
																.equalsIgnoreCase(CommonUtil.deNull(blNbr)))
														.findFirst().orElse(-1);
												total = Double.parseDouble(miscDetailWtMainUpdate.get(indexUpdate).getTypeValue());
											} else {
												total = Double.parseDouble(manifestDetail.getGross_weight_kg());
											}
										} 

										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Exceed_Weight + total);
										commentsList.add(comments);
									// Check if add/update exist and it doesnt comes first
									} else if (existAdd == 0 && existUpdate == 0 && existSubUpdate == 0 && errorAddBLExist == 0) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Declare_First);
										commentsList.add(comments);
									} 
								} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_update) && !cannotDeleteSub) {
									if (!CommonUtil.deNull(cellData).equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getGross_weight_kg()))) {
										if (manifestmultipleHSDetailExist) {
											double total = 0;
											long existAdd = miscDetailWtListAdd.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
											long existUpdate = miscDetailWtListUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
											long existDelete = miscDetailWtListDelete.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
											if (existUpdate == 0 && existDelete == 0 && existAdd == 0) {
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_HSCode_Compulsary_Weight);
												commentsList.add(comments);
											} else {
												Map<Integer, Integer> indexNotUpdate = new HashMap<Integer, Integer>();
												// If Add hs code exist, should count as total
												if (existAdd > 0) {
													for (int j = 0; j < miscDetailWtListAdd.size(); j++) {
														if (CommonUtil.deNull(miscDetailWtListAdd.get(j).getTypeCode())
																.equalsIgnoreCase(cellData_bill)) {
															total = total + Double
																	.parseDouble(miscDetailWtListAdd.get(j).getTypeValue());
														}
													}
												}

												// If update hs code exists
												if (existUpdate > 0) {
													for (int j = 0; j < miscDetailWtListUpdate.size(); j++) {
														// For weight declared in excel
														if (CommonUtil.deNull(miscDetailWtListUpdate.get(j).getTypeCode())
																.equalsIgnoreCase(cellData_bill)) {
															total = total + Double.parseDouble(
																	miscDetailWtListUpdate.get(j).getTypeValue());
														}

														// For weight not declared in excel (Update HS Code) - Check index
														// that has
														String tmp = CommonUtil.deNull(miscDetailsubOldHSCodelListUpdate.get(j).getTypeValue())
																.isEmpty() ? CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(j).getTypeValue())
																		: CommonUtil.deNull(miscDetailsubOldHSCodelListUpdate.get(j)
																				.getTypeValue());
														String tmpCustom = CommonUtil.deNull(miscDetailCustomSubUpdate.get(j).getTypeValue());
														String tmpCrgDesc = CommonUtil.deNull(miscDetailCrgDescSubUpdate.get(j).getTypeValue());
														int indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l)
																		.getHs_code_sub_code()).equalsIgnoreCase(CommonUtil.deNull(tmp))
																		&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																				.getCustom_hs_code()).equalsIgnoreCase(tmpCustom))
																.findFirst().orElse(-1);
														if(indexHs < 0) {
															indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																	.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																			.equalsIgnoreCase(CommonUtil.deNull(tmp))
																			&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																					.getCargo_description()).equalsIgnoreCase(tmpCrgDesc))
																	.findFirst().orElse(-1);
														}
														if (indexHs >= 0) {
															indexNotUpdate.put(indexHs, indexHs);
														}
													}

												}

												// If delete hs code exists
												if (existDelete > 0) {
													for (int j = 0; j < miscDetailSubHsCodeListDel.size(); j++) {
														// For weight not declared in excel (Delete HS Code) - Check index
														// that has
														if (CommonUtil.deNull(miscDetailSubHsCodeListDel.get(j).getTypeCode())
																.equalsIgnoreCase(cellData_bill)) {
															String tmp = CommonUtil.deNull(miscDetailSubOldHsCodeListDel.get(j).getTypeValue())
																	.isEmpty()
																			? CommonUtil.deNull(miscDetailSubHsCodeListDel.get(j)
																					.getTypeValue())
																			: CommonUtil.deNull(miscDetailSubOldHsCodeListDel.get(j)
																					.getTypeValue());
															String tmpCustom = CommonUtil.deNull(miscDetailSubCustomHSCodeListDel.get(j).getTypeValue());
															String tmpCrgDesc = CommonUtil.deNull(miscDetailSubCargoDescListDel.get(j).getTypeValue());
															int indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																	.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l)
																			.getHs_code_sub_code()).equalsIgnoreCase(CommonUtil.deNull(tmp))
																			&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																					.getCustom_hs_code()).equalsIgnoreCase(tmpCustom))
																	.findFirst().orElse(-1);
															if(indexHs < 0) {
																indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																		.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																				.equalsIgnoreCase(CommonUtil.deNull(tmp))
																				&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																						.getCargo_description()).equalsIgnoreCase(tmpCrgDesc))
																		.findFirst().orElse(-1);
															}
															if (indexHs >= 0) {
																indexNotUpdate.put(indexHs, indexHs);
															}
														}
													}
												}

												for (int j = 0; j < manifestHSDetailTmp.size(); j++) {
													if (!indexNotUpdate.containsValue(j)) {
														total = total + Double.parseDouble(
																manifestHSDetailTmp.get(j).getGross_weight_kg());
													}
												}
											}
											if (total > Double.parseDouble(cellData)
													|| total < Double.parseDouble(cellData)) {
												miscNbrWtMainUpdate.add(msc);
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_Exceed_Weight + cellData);
												commentsList.add(comments);
											}
										}
									}
								} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS) && !cannotDeleteSub) {
									long errorOldHSCodeCompulsary = commentsList.stream().parallel().filter((s) -> CommonUtil.deNull(s.getMessage()).equalsIgnoreCase(ConstantUtil.ErrorMsg_OldHSCode_Compulsary)).count();
									long errorDuplicateCustomHSCode = commentsList.stream().parallel().filter((s) -> CommonUtil.deNull(s.getMessage()).equalsIgnoreCase(ConstantUtil.ErrorMsg_Same_Custom)).count();
									double total = 0;
									// Error in Updating Main, automatically will show error in updating sub HS Code
									long errorInUpdateMain = miscNbrWtMainUpdate.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									if (errorInUpdateMain > 0) {
										for (int j = 0; j < miscNbrWtMainUpdate.size(); j++) {
											if (CommonUtil.deNull(miscNbrWtMainUpdate.get(j).getTypeCode()).equalsIgnoreCase(cellData_bill)) {
												total = Double.parseDouble(CommonUtil.deNull(miscNbrWtMainUpdate.get(j).getTypeValue()));
											}
										}
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Exceed_Weight + total);
										commentsList.add(comments);
									} else if (errorInUpdateMain == 0 && errorOldHSCodeCompulsary == 0 && errorDuplicateCustomHSCode == 0) {
										Map<Integer, Integer> indexNotUpdate = new HashMap<Integer, Integer>();
										// No error in Updating Main
										long existAdd = miscDetailWtListAdd.stream().parallel().filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
										long updateMainExist = mainBLNbrListUpdate.stream().parallel()
												.filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count();
										long deletExist = miscDetailWtListDelete.stream().parallel()
												.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
										long updateSubExist = miscDetailWtListUpdate.stream().parallel()
												.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
										// There is changes in weight number
										if(!CommonUtil.deNull(cellData).equalsIgnoreCase(CommonUtil.deNull(prevWt))) {
											cm.setValueChanges(true);
										}
										if (updateMainExist == 0 && deletExist == 0 && existAdd == 0 && updateSubExist == 0
												&& !CommonUtil.deNull(cellData).equalsIgnoreCase(CommonUtil.deNull(prevWt))) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_MainNotExist_Update);
											commentsList.add(comments);
										} else if (deletExist > 0 || updateMainExist > 0 || existAdd > 0 || updateSubExist > 0) {
											String mainWt = "";
											// Check for total - does delete weight updated to hs detail matches main
											// weight or does updated value in update hs code matches with main update
											// value declared
											for (int j = 0; j < miscDetailWtListUpdate.size(); j++) {
												// For weight declared in excel
												if (CommonUtil.deNull(miscDetailWtListUpdate.get(j).getTypeCode())
														.equalsIgnoreCase(cellData_bill)) {
													total = total + Double
															.parseDouble(CommonUtil.deNull(miscDetailWtListUpdate.get(j).getTypeValue()));
												}

												// For weight not declared in excel
												if (CommonUtil.deNull(miscDetailWtListUpdate.get(j).getTypeCode())
														.equalsIgnoreCase(cellData_bill)) {
													// Check index of hscode declared in excel in db
													String tmp = CommonUtil.deNull(miscDetailsubOldHSCodelListUpdate.get(j).getTypeValue())
															.isEmpty() ? CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(j).getTypeValue())
																	: CommonUtil.deNull(miscDetailsubOldHSCodelListUpdate.get(j)
																			.getTypeValue());
													String tmpCustom = CommonUtil.deNull(miscDetailCustomSubUpdate.get(j).getTypeValue());
													String tmpCrgDesc = CommonUtil.deNull(miscDetailCrgDescSubUpdate.get(j).getTypeValue());
													int indexHs = IntStream.range(0, manifestHSDetailTmp.size())
															.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																	.equalsIgnoreCase(CommonUtil.deNull(tmp))
																	&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																			.getCustom_hs_code()).equalsIgnoreCase(tmpCustom))
															.findFirst().orElse(-1);
													if(indexHs < 0) {
														indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																		.equalsIgnoreCase(CommonUtil.deNull(tmp))
																		&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																				.getCargo_description()).equalsIgnoreCase(tmpCrgDesc))
																.findFirst().orElse(-1);
													}
													// If its there, do not take is as total
													if (indexHs >= 0) {
														indexNotUpdate.put(indexHs, indexHs);
													}
												}
											}

											if (deletExist > 0) {
												// To not add weight that is being deleted as total
												for (int j = 0; j < miscDetailSubHsCodeListDel.size(); j++) {
													if (CommonUtil.deNull(miscDetailSubHsCodeListDel.get(j).getTypeCode())
															.equalsIgnoreCase(cellData_bill)) {
														String tmp = CommonUtil.deNull(miscDetailSubOldHsCodeListDel.get(j).getTypeValue())
																.isEmpty()
																		? CommonUtil.deNull(miscDetailSubHsCodeListDel.get(j).getTypeValue())
																		: CommonUtil.deNull(miscDetailSubOldHsCodeListDel.get(j)
																				.getTypeValue());
														String tmpCustom = CommonUtil.deNull(miscDetailSubCustomHSCodeListDel.get(j).getTypeValue());
														String tmpCrgDesc = CommonUtil.deNull(miscDetailSubCargoDescListDel.get(j).getTypeValue());
														int indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l)
																		.getHs_code_sub_code()).equalsIgnoreCase(CommonUtil.deNull(tmp))
																		&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																				.getCustom_hs_code()).equalsIgnoreCase(tmpCustom))
																.findFirst().orElse(-1);
														if(indexHs < 0) {
															indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																	.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																			.equalsIgnoreCase(CommonUtil.deNull(tmp))
																			&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																					.getCargo_description()).equalsIgnoreCase(tmpCrgDesc))
																	.findFirst().orElse(-1);
														}
														if (indexHs >= 0) {
															indexNotUpdate.put(indexHs, indexHs);
														}
													}
												}
											}

											if (existAdd > 0) {
												for (int j = 0; j < miscDetailWtListAdd.size(); j++) {
													// For Number of packages declared in excel (Add)
													if (CommonUtil.deNull(miscDetailWtListAdd.get(j).getTypeCode())
															.equalsIgnoreCase(cellData_bill)) {
														total = total + Double
																.parseDouble(CommonUtil.deNull(miscDetailWtListAdd.get(j).getTypeValue()));
													}
												}
											}

											for (int j = 0; j < manifestHSDetailTmp.size(); j++) {
												// Here we got total of weight not included in excel
												if (!indexNotUpdate.containsValue(j)) {
													total = total + Double
															.parseDouble(manifestHSDetailTmp.get(j).getGross_weight_kg());
												}
											}

											if (updateMainExist > 0) {
												int mainWtIndex = IntStream.range(0, miscDetailWtMainUpdate.size())
														.filter(l -> CommonUtil.deNull(miscDetailWtMainUpdate.get(l).getTypeCode())
																.equalsIgnoreCase(blNbr))
														.findFirst().orElse(-1);
												mainWt = CommonUtil.deNull(miscDetailWtMainUpdate.get(mainWtIndex).getTypeValue());
											} else {
												mainWt = manifestDetail.getGross_weight_kg();
											}

											if (total > Double.parseDouble(mainWt) || total < Double.parseDouble(mainWt)) {
												miscNbrWtMainUpdateHs.add(msc);
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_Exceed_Weight + mainWt);
												commentsList.add(comments);
											}
										}
									}
								} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS) && !cannotDeleteSub) {
									double total = 0;
									long errorInvalidHSCode = commentsList.stream().parallel().filter((s) -> CommonUtil.deNull(s.getMessage()).equalsIgnoreCase(ConstantUtil.ErrorMsg_UpdateHSCode_Invalid)).count();
									boolean updateComesFirst = mainBLNbrListUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count() > 0;
									boolean updateSubComesFirst = mainBLNbrListSubUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count() > 0;
									if (updateComesFirst || updateSubComesFirst) { // Meaning update row has been checked before delete row
										long exist = miscNbrWtMainUpdate.stream().parallel()
												.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
										long existHS = miscNbrWtMainUpdateHs.stream().parallel()
												.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
										if (exist > 0 || existHS > 0) {
											total = 0;
											// If error in main update exist, total should be the same as what is declared
											// in Update Main
											if (exist > 0) {
												for (int j = 0; j < miscNbrWtMainUpdate.size(); j++) {
													if (CommonUtil.deNull(miscNbrWtMainUpdate.get(j).getTypeCode())
															.equalsIgnoreCase(CommonUtil.deNull(cellData_bill))) {
														total = Double.parseDouble(CommonUtil.deNull(miscNbrWtMainUpdate.get(j).getTypeValue()));
													}
												}
												// If error in sub update exist, total should be the same as what is
												// declared in DB (Main HS)
											} else if (existHS > 0) {
												total = Double.parseDouble(manifestDetail.getGross_weight_kg());
											}
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_Exceed_Weight + total);
											commentsList.add(comments);
										}
									} else {
										// Delete row comes before update row
										Map<Integer, Integer> indexNotUpdate = new HashMap<Integer, Integer>();
										long updateMainExist = mainBLNbrListUpdate.stream().parallel()
												.filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count();
										long existUpdate = miscDetailWtListUpdate.stream().parallel()
												.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
										long existHSAdd = miscDetailWtListAdd.stream().parallel()
												.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
										// If neither have, should throw error
										if (updateMainExist == 0 && existUpdate == 0 && existHSAdd == 0) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_MainOrSubNotExist);
											commentsList.add(comments);
										} else {
											String mainWt = "";
											// Check if updated value sync
											if (updateMainExist > 0) {
												// to compare with total, we take value inserted in excel update
												int mainWtIndx = IntStream.range(0, miscDetailWtMainUpdate.size())
														.filter(l -> CommonUtil.deNull(miscDetailWtMainUpdate.get(l).getTypeCode())
																.equalsIgnoreCase(blNbr))
														.findFirst().orElse(-1);
												mainWt = CommonUtil.deNull(miscDetailWtMainUpdate.get(mainWtIndx).getTypeValue());
											}

											// If update hs exist
											if (existUpdate > 0) {
												// Check if updated value sync with Main in DB
												for (int j = 0; j < miscDetailWtListUpdate.size(); j++) {
													// For weight declared in excel
													if (CommonUtil.deNull(miscDetailWtListUpdate.get(j).getTypeCode())
															.equalsIgnoreCase(blNbr)) {
														total = total + Double.parseDouble(
																CommonUtil.deNull(miscDetailWtListUpdate.get(j).getTypeValue()));
													}

													// For weight not declared in excel
													if (CommonUtil.deNull(miscDetailWtListUpdate.get(j).getTypeCode())
															.equalsIgnoreCase(cellData_bill)) {
														String tmp = CommonUtil.deNull(miscDetailsubOldHSCodelListUpdate.get(j).getTypeValue())
																.isEmpty() ? CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(j).getTypeValue())
																		: CommonUtil.deNull(miscDetailsubOldHSCodelListUpdate.get(j)
																				.getTypeValue());
														String tmpCustom = CommonUtil.deNull(miscDetailCustomSubUpdate.get(j).getTypeValue());
														String tmpCrgDesc = CommonUtil.deNull(miscDetailCrgDescSubUpdate.get(j).getTypeValue());
														int indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l)
																		.getHs_code_sub_code()).equalsIgnoreCase(CommonUtil.deNull(tmp))
																		&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																				.getCustom_hs_code()).equalsIgnoreCase(tmpCustom))
																.findFirst().orElse(-1);
														if(indexHs < 0) {
															indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																	.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																			.equalsIgnoreCase(CommonUtil.deNull(tmp))
																			&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																					.getCargo_description()).equalsIgnoreCase(tmpCrgDesc))
																	.findFirst().orElse(-1);
														}
														if (indexHs >= 0) {
															indexNotUpdate.put(indexHs, indexHs);
														}
													}

												}
											}

											if (updateMainExist == 0) {
												mainWt = manifestDetail.getGross_weight_kg();
											}

											// Add that is being deleted should not be count
											for (int k = 0; k < miscDetailSubHsCodeListDel.size(); k++) {
												if (CommonUtil.deNull(miscDetailSubHsCodeListDel.get(k).getTypeCode())
														.equalsIgnoreCase(cellData_bill)) {
													String tmp = CommonUtil.deNull(miscDetailSubOldHsCodeListDel.get(k).getTypeValue())
															.isEmpty() ? CommonUtil.deNull(miscDetailSubHsCodeListDel.get(k).getTypeValue())
																	: CommonUtil.deNull(miscDetailSubOldHsCodeListDel.get(k).getTypeValue());
													String tmpCustom = CommonUtil.deNull(miscDetailSubCustomHSCodeListDel.get(k).getTypeValue());
													String tmpCrgDesc = CommonUtil.deNull(miscDetailSubCargoDescListDel.get(k).getTypeValue());
													int indexHs = IntStream.range(0, manifestHSDetailTmp.size())
															.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																	.equalsIgnoreCase(CommonUtil.deNull(tmp))
																	&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																			.getCustom_hs_code()).equalsIgnoreCase(tmpCustom))
															.findFirst().orElse(-1);
													if(indexHs < 0) {
														indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																		.equalsIgnoreCase(CommonUtil.deNull(tmp))
																		&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																				.getCargo_description()).equalsIgnoreCase(tmpCrgDesc))
																.findFirst().orElse(-1);
													}
													if (indexHs >= 0) {
														indexNotUpdate.put(indexHs, indexHs);
													}
												}
											}
											
											// Check if add hs exists
											if (existHSAdd > 0) {
												for (int j = 0; j < miscDetailWtListAdd.size(); j++) {
													// For Number of packages declared in excel
													if (CommonUtil.deNull(miscDetailWtListAdd.get(j).getTypeCode())
															.equalsIgnoreCase(blNbr)) {
														total = total + Double.parseDouble(CommonUtil
																.deNull(miscDetailWtListAdd.get(j).getTypeValue()));
													}
												}
											}

											// Add total of value in DB that is not being touch
											for (int j = 0; j < manifestHSDetailTmp.size(); j++) {
												if (!indexNotUpdate.containsValue(j)) {
													total = total + Double
															.parseDouble(manifestHSDetailTmp.get(j).getGross_weight_kg());
												}
											}

											// Compare value declared in excel, in db with main value
											if ((total > Double.parseDouble(mainWt) || total < Double.parseDouble(mainWt)) && errorInvalidHSCode == 0) {
												miscNbrWtMainDeleteHs.add(msc);
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_Exceed_Weight_Delete
														+ mainWt);
												commentsList.add(comments);
											}
										}
									}
								} 
							} else {
								double tmpValue = CommonUtil.deNull(cellData).isEmpty() ? 0.00 : Double.parseDouble(cellData);
								if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
									if (tmpValue != Double.parseDouble(manifestDetail.getGross_weight_kg())) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.Error_M20204);
										commentsList.add(comments);
									}
								} else if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_updateHS)) {
									prevWt = CommonUtil.deNull(prevWt).isEmpty() ? "0.00" : prevWt;
									if (tmpValue != Double.parseDouble(prevWt)) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.Error_M20204);
										commentsList.add(comments);
									}
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.gross_measurement)) {
							String blNbr = CommonUtil.deNull(cellData_bill);
							MiscDetail msc = new MiscDetail();
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String cellData_CustomHSCode = CommonUtil.getCellData(
									CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_custom_index : ConstantUtil.custom_index) + String.valueOf(i),
									sheet);
							String afterDot = CommonUtil.deNull(cellData).indexOf(".") > 0 ?  CommonUtil.deNull(cellData).substring(CommonUtil.deNull(cellData).indexOf(".") + 1) : "0";
							
							Double double2decVol = !CommonUtil.deNull(cellData).isEmpty() ? Math.round(Double.parseDouble(cellData) * 100.0) / 100.0 : 0.00;
							cellData = CommonUtil.deNull(cellData).isEmpty() ? CommonUtil.deNull(cellData) : ((afterDot.length()>1 && !afterDot.equals("00") ) || Integer.valueOf(afterDot) > 0 ) ? String.format("%.2f", double2decVol) : cellData.replace("."+afterDot, "");
							msc.setTypeCode(cellData_bill);
							msc.setTypeValue(cellData);
							List<CargoManifest> manifestHSDetailTmp = manifestHSDetail;
							int index = -1;
							String prevVol = "";
							removeColorsAndComment(ref, sheet, no_style);
							if (cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS)
									|| cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS)) {
								String customHsCode = cm.isCustomChanged() ? cm.getOldCustom() : CommonUtil.deNull(cellData_CustomHSCode);
								if (CommonUtility.deNull(cm.getOldHSCode()).isEmpty()) {
									for (int j = 0; j < manifestHSDetail.size(); j++) {
										if (CommonUtil.deNull(cm.getHs_code()).equalsIgnoreCase(CommonUtil.deNull(manifestHSDetail.get(j).getHs_code()))
												&& CommonUtil.deNull(cm.getHs_sub_code_fr())
														.equals(CommonUtil.deNull(manifestHSDetail.get(j).getHs_sub_code_fr()))
												&& CommonUtil.deNull(cm.getHs_sub_code_to())
														.equals(CommonUtil.deNull(manifestHSDetail.get(j).getHs_sub_code_to()))
												&& cellData_bill.equalsIgnoreCase(
														manifestHSDetail.get(j).getBills_of_landing_no())
												&& CommonUtil.deNull(manifestHSDetail.get(j).getCustom_hs_code())
												.equalsIgnoreCase(CommonUtil.deNull(customHsCode))) {
											prevVol = manifestHSDetail.get(j).getGross_measurement_m3();
											index = j;
											break;
										}
									}
									if (CommonUtil.deNull(prevVol).isEmpty()) {
										String crgDesc = cm.getCargo_description();
										String cstmHs = cm.isCustomChanged() ? CommonUtil.deNull(cm.getOldCustom()) : CommonUtil.deNull(cm.getCustom_hs_code());
										int indexCrgDesc = IntStream
												.range(0, manifestHSDetailTmp.size()).filter(l -> CommonUtil.deNull(manifestHSDetailTmp
														.get(l).getCargo_description()).equalsIgnoreCase(CommonUtil.deNull(crgDesc).trim()))
												.findFirst().orElse(-1);
										int indexCrgCustom = IntStream
												.range(0, manifestHSDetailTmp.size()).filter(n -> CommonUtil.deNull(manifestHSDetailTmp
														.get(n).getCustom_hs_code()).equalsIgnoreCase(CommonUtil.deNull(cstmHs).trim()))
												.findFirst().orElse(-1);
										index = indexCrgDesc >= 0 ? indexCrgDesc : indexCrgCustom;
										prevVol = index >=0 ? manifestHSDetailTmp.get(index).getGross_measurement_m3() : "0";
									}
								} else {
									for (int j = 0; j < manifestHSDetail.size(); j++) {
										if (CommonUtil.deNull(cm.getOldHSCode()).equalsIgnoreCase(CommonUtil.deNull(manifestHSDetail.get(j).getHs_code()))
												&& CommonUtil.deNull(cm.getOldHSCode_fr())
														.equals(CommonUtil.deNull(manifestHSDetail.get(j).getHs_sub_code_fr()))
												&& CommonUtil.deNull(cm.getOldHSCode_to())
														.equals(CommonUtil.deNull(manifestHSDetail.get(j).getHs_sub_code_to()))
												&& cellData_bill.equalsIgnoreCase(
														manifestHSDetail.get(j).getBills_of_landing_no())
												&& CommonUtil.deNull(manifestHSDetail.get(j).getCustom_hs_code())
												.equalsIgnoreCase(CommonUtil.deNull(customHsCode))) {
											prevVol = manifestHSDetail.get(j).getGross_measurement_m3();
											index = j;
											break;
										}
									}
								}
							}
							if(!cm.getEdo_created()) {

								if (CommonUtil.deNull(cellData) == "") {
									if (!deleteflag
											&& manifestUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_GrossM3);
										commentsList.add(comments);
									}
								} else if (!deleteflag && !CommonUtil.isNumeric(cellData)) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
									commentsList.add(comments);
								} else if (!deleteflag && CommonUtil.isNumeric(cellData)) {

									if (Double.parseDouble(cellData) < 0.01) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Valid_MinGrossM3);
										commentsList.add(comments);
									} else if (Double.parseDouble(cellData) > 9999.99) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Valid_MaxGrossM3);
										commentsList.add(comments);
									} else if (cellData.length() > 8) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage("Volume" + ConstantUtil.ErrorMsg_8Length);
										commentsList.add(comments);
									} else {
										CommonUtil.setCellData(ref, sheet, CommonUtil.get2DecFromStr(cellData));
										cm.setGross_measurement_m3(CommonUtil.get2DecFromStr(cellData));
									}
								} else {
									CommonUtil.setCellData(ref, sheet, CommonUtil.get2DecFromStr(cellData));
									cm.setGross_measurement_m3(CommonUtil.get2DecFromStr(cellData));
								}
								if (cellData_na.equalsIgnoreCase(ConstantUtil.action_add)) {
									long exist = miscDetailVolListAdd.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									if (exist > 0) {
										double total = 0;
										for (int j = 0; j < miscDetailVolListAdd.size(); j++) {
											if (CommonUtil.deNull(miscDetailVolListAdd.get(j).getTypeCode()).equalsIgnoreCase(cellData_bill)) {
												total = total + (!CommonUtil.deNull(miscDetailVolListAdd.get(j).getTypeValue()).isEmpty() ? Double.parseDouble(miscDetailVolListAdd.get(j).getTypeValue()) : 0);
											}
										}
										if(!CommonUtil.deNull(cellData).isEmpty()) {
											if (total > Double.parseDouble(cellData) || total < Double.parseDouble(cellData)) {
												miscNbrVolMainAdd.add(msc);
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_Exceed_Vol + cellData);
												commentsList.add(comments);
											}
										}
									}
								} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS)) {
									double total = 0;
									long existAdd = mainBLNbrListAdd.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count();
									long existUpdate = mainBLNbrListUpdate.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count();
									long existSubUpdate = mainBLNbrListSubUpdate.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count();
									long exist = miscNbrVolMainAdd.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									long existUpdateError = miscNbrVolMainUpdate.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									long existUpdateHsError = miscNbrVolMainUpdateHs.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr))
											.count();
									long existdeleteHsError = miscNbrVolDeleteHs.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr))
											.count();
									long errorAddBLExist = commentsList.stream().parallel().filter((s) -> CommonUtil
											.deNull(s.getMessage()).equalsIgnoreCase(ConstantUtil.Error_M20201))
											.count();
									if ((exist > 0 || existUpdateError > 0 || existUpdateHsError > 0 || existdeleteHsError > 0) && errorAddBLExist == 0) {
										if (exist > 0) {
											// Have error in Add Main
											for (int j = 0; j < miscNbrVolMainAdd.size(); j++) {
												if (miscNbrVolMainAdd.get(j).getTypeCode()
														.equalsIgnoreCase(cellData_bill)) {
													total = Double.parseDouble(miscNbrVolMainAdd.get(j).getTypeValue());
												}
											}
											// Have error in Update Main
										} else if (existUpdateError > 0) {
											for (int j = 0; j < miscNbrVolMainUpdate.size(); j++) {
												if (miscNbrVolMainUpdate.get(j).getTypeCode()
														.equalsIgnoreCase(cellData_bill)) {
													total = Double.parseDouble(miscNbrVolMainUpdate.get(j).getTypeValue());
												}
											}
										} else if (existUpdateHsError > 0 || existdeleteHsError > 0) {
											if (existUpdate > 0) {
												int indexUpdate = IntStream.range(0, miscDetailVolMainUpdate.size())
														.filter(l -> CommonUtil
																.deNull(miscDetailVolMainUpdate.get(l).getTypeCode())
																.equalsIgnoreCase(CommonUtil.deNull(blNbr)))
														.findFirst().orElse(-1);
												total = Double
														.parseDouble(miscDetailVolMainUpdate.get(indexUpdate).getTypeValue());
											} else {
												total = Double.parseDouble(manifestDetail.getGross_measurement_m3());
											}
										}

										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Exceed_Vol + total);
										commentsList.add(comments);
										// Check if add/Update exist and it doesnt comes first
									} else if (existAdd == 0 && existUpdate == 0 && existSubUpdate == 0 && errorAddBLExist == 0) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Declare_First);
										commentsList.add(comments);
									} else {
										cm.setGross_measurement_m3(cellData);
									}
								} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_update) && !cannotDeleteSub) {
									if (!CommonUtil.deNull(cellData).equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getGross_measurement_m3()))) {
										if (manifestmultipleHSDetailExist) {
											double total = 0;
											long existAdd = miscDetailVolListAdd.stream().parallel()
													.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
											long existUpdate = miscDetailVolListUpdate.stream().parallel()
													.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
											long existDelete = miscDetailVolListDelete.stream().parallel()
													.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
											if (existUpdate == 0 && existDelete == 0 && existAdd == 0) {
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_HSCode_Compulsary_Volume);
												commentsList.add(comments);
											} else {
												Map<Integer, Integer> indexNotUpdate = new HashMap<Integer, Integer>();
												// If Add hs code exist, should count as total
												if (existAdd > 0) {
													for (int j = 0; j < miscDetailVolListAdd.size(); j++) {
														if (CommonUtil.deNull(miscDetailVolListAdd.get(j).getTypeCode())
																.equalsIgnoreCase(cellData_bill)) {
															total = total + Double
																	.parseDouble(miscDetailVolListAdd.get(j).getTypeValue());
														}
													}
												}

												// If update hs code exists
												if (existUpdate > 0) {
													for (int j = 0; j < miscDetailVolListUpdate.size(); j++) {
														// For volume declared in excel
														if (CommonUtil.deNull(miscDetailVolListUpdate.get(j).getTypeCode())
																.equalsIgnoreCase(cellData_bill)) {
															total = total + Double.parseDouble(
																	miscDetailVolListUpdate.get(j).getTypeValue());
														}

														// For volume not declared in excel (Update HS Code) - Check index
														// that has
														String tmp = CommonUtil.deNull(miscDetailsubOldHSCodelListUpdate.get(j).getTypeValue())
																.isEmpty() ? CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(j).getTypeValue())
																		: CommonUtil.deNull(miscDetailsubOldHSCodelListUpdate.get(j)
																				.getTypeValue());
														String tmpCustom = CommonUtil.deNull(miscDetailCustomSubUpdate.get(j).getTypeValue());
														String tmpCrgDesc = CommonUtil.deNull(miscDetailCrgDescSubUpdate.get(j).getTypeValue());
														int indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l)
																		.getHs_code_sub_code()).equalsIgnoreCase(CommonUtil.deNull(tmp))
																		&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																				.getCustom_hs_code()).equalsIgnoreCase(tmpCustom))
																.findFirst().orElse(-1);
														if(indexHs < 0) {
															indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																	.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																			.equalsIgnoreCase(CommonUtil.deNull(tmp))
																			&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																					.getCargo_description()).equalsIgnoreCase(tmpCrgDesc))
																	.findFirst().orElse(-1);
														}
														if (indexHs >= 0) {
															indexNotUpdate.put(indexHs, indexHs);
														}
													}

												}

												// If delete hs code exists
												if (existDelete > 0) {
													for (int j = 0; j < miscDetailSubHsCodeListDel.size(); j++) {
														// For volume not declared in excel (Delete HS Code) - Check index
														// that has
														if (CommonUtil.deNull(miscDetailSubHsCodeListDel.get(j).getTypeCode())
																.equalsIgnoreCase(cellData_bill)) {
															String tmp = CommonUtil.deNull(miscDetailSubOldHsCodeListDel.get(j).getTypeValue())
																	.isEmpty()
																			? CommonUtil.deNull(miscDetailSubHsCodeListDel.get(j)
																					.getTypeValue())
																			: CommonUtil.deNull(miscDetailSubOldHsCodeListDel.get(j)
																					.getTypeValue());
															String tmpCustom = CommonUtil.deNull(miscDetailSubCustomHSCodeListDel.get(j).getTypeValue());
															String tmpCrgDesc = CommonUtil.deNull(miscDetailSubCargoDescListDel.get(j).getTypeValue());
															int indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																	.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l)
																			.getHs_code_sub_code()).equalsIgnoreCase(tmp)
																			&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																					.getCustom_hs_code()).equalsIgnoreCase(tmpCustom))
																	.findFirst().orElse(-1);
															if(indexHs < 0) {
																indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																		.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																				.equalsIgnoreCase(CommonUtil.deNull(tmp))
																				&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																						.getCargo_description()).equalsIgnoreCase(tmpCrgDesc))
																		.findFirst().orElse(-1);
															}
															if (indexHs >= 0) {
																indexNotUpdate.put(indexHs, indexHs);
															}
														}
													}
												}

												for (int j = 0; j < manifestHSDetailTmp.size(); j++) {
													if (!indexNotUpdate.containsValue(j)) {
														total = total + Double.parseDouble(
																manifestHSDetailTmp.get(j).getGross_measurement_m3());
													}
												}
											}
											if (total > Double.parseDouble(cellData) || total < Double.parseDouble(cellData)) {
												miscNbrVolMainUpdate.add(msc);
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_Exceed_Vol + cellData);
												commentsList.add(comments);
											}
										}
									}
								} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS) && !cannotDeleteSub) {
									long errorOldHSCodeCompulsary = commentsList.stream().parallel().filter((s) -> CommonUtil.deNull(s.getMessage()).equalsIgnoreCase(ConstantUtil.ErrorMsg_OldHSCode_Compulsary)).count();
									long errorDuplicateCustomHSCode = commentsList.stream().parallel().filter((s) -> CommonUtil.deNull(s.getMessage()).equalsIgnoreCase(ConstantUtil.ErrorMsg_Same_Custom)).count();
									double total = 0;
									// Error in Updating Main, automatically will show error in updating sub HS Code
									long errorInUpdateMain = miscNbrVolMainUpdate.stream().parallel()
											.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
									if (errorInUpdateMain > 0) {
										for (int j = 0; j < miscNbrVolMainUpdate.size(); j++) {
											if (CommonUtil.deNull(miscNbrVolMainUpdate.get(j).getTypeCode()).equalsIgnoreCase(cellData_bill)) {
												total = Double.parseDouble(miscNbrVolMainUpdate.get(j).getTypeValue());
											}
										}
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Exceed_Vol + total);
										commentsList.add(comments);
									} else if (errorInUpdateMain == 0 && errorOldHSCodeCompulsary == 0 && errorDuplicateCustomHSCode == 0) {
										Map<Integer, Integer> indexNotUpdate = new HashMap<Integer, Integer>();
										// No error in Updating Main
										long existAdd = miscDetailVolListAdd.stream().parallel()
												.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
										long updateMainExist = mainBLNbrListUpdate.stream().parallel()
												.filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count();
										long deletExist = miscDetailVolListDelete.stream().parallel()
												.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
										long updateSubExist = miscDetailVolListUpdate.stream().parallel()
												.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
										// There is changes in volume number
										if(!CommonUtil.deNull(cellData).equalsIgnoreCase(prevVol)) {
											cm.setValueChanges(true);
										}
										if (updateMainExist == 0 && deletExist == 0 && existAdd == 0 && updateSubExist == 0
												&& !CommonUtil.deNull(cellData).equalsIgnoreCase(prevVol)) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_MainNotExist_Update);
											commentsList.add(comments);
										} else if (deletExist > 0 || updateMainExist > 0 || existAdd > 0 || updateSubExist > 0) {
											String mainVol = "";
											// Check for total - does delete volume updated to hs detail matches main
											// volume or does updated value in update hs code matches with main update
											// value declared
											for (int j = 0; j < miscDetailVolListUpdate.size(); j++) {
												// For volume declared in excel
												if (CommonUtil.deNull(miscDetailVolListUpdate.get(j).getTypeCode())
														.equalsIgnoreCase(cellData_bill)) {
													total = total + Double
															.parseDouble(CommonUtil.deNull(miscDetailVolListUpdate.get(j).getTypeValue()));
												}

												// For volume not declared in excel
												if (CommonUtil.deNull(miscDetailVolListUpdate.get(j).getTypeCode())
														.equalsIgnoreCase(cellData_bill)) {
													// Check index of hscode declared in excel in db
													String tmp = CommonUtil.deNull(miscDetailsubOldHSCodelListUpdate.get(j).getTypeValue())
															.isEmpty() ? CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(j).getTypeValue())
																	: CommonUtil.deNull(miscDetailsubOldHSCodelListUpdate.get(j)
																			.getTypeValue());
													String tmpCustom = CommonUtil.deNull(miscDetailCustomSubUpdate.get(j).getTypeValue());
													String tmpCrgDesc = CommonUtil.deNull(miscDetailCrgDescSubUpdate.get(j).getTypeValue());
													int indexHs = IntStream.range(0, manifestHSDetailTmp.size())
															.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																	.equalsIgnoreCase(tmp) && CommonUtil.deNull(manifestHSDetailTmp.get(l)
																			.getCustom_hs_code()).equalsIgnoreCase(tmpCustom))
															.findFirst().orElse(-1);
													if(indexHs < 0) {
														indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																		.equalsIgnoreCase(CommonUtil.deNull(tmp))
																		&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																				.getCargo_description()).equalsIgnoreCase(tmpCrgDesc))
																.findFirst().orElse(-1);
													}
													// If its there, do not take is as total
													if (indexHs >= 0) {
														indexNotUpdate.put(indexHs, indexHs);
													}
												}
											}

											if (deletExist > 0) {
												// To not add volume that is being deleted as total
												for (int j = 0; j < miscDetailSubHsCodeListDel.size(); j++) {
													if (miscDetailSubHsCodeListDel.get(j).getTypeCode()
															.equalsIgnoreCase(cellData_bill)) {
														String tmp = CommonUtil.deNull(miscDetailSubOldHsCodeListDel.get(j).getTypeValue())
																.isEmpty()
																		? CommonUtil.deNull(miscDetailSubHsCodeListDel.get(j).getTypeValue())
																		: CommonUtil.deNull(miscDetailSubOldHsCodeListDel.get(j)
																				.getTypeValue());
														String tmpCustom = CommonUtil.deNull(miscDetailSubCustomHSCodeListDel.get(j).getTypeValue());
														String tmpCrgDesc = CommonUtil.deNull(miscDetailSubCargoDescListDel.get(j).getTypeValue());
														int indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l)
																		.getHs_code_sub_code()).equalsIgnoreCase(tmp)
																		&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																				.getCustom_hs_code()).equalsIgnoreCase(tmpCustom))
																.findFirst().orElse(-1);
														if(indexHs < 0) {
															indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																	.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																			.equalsIgnoreCase(CommonUtil.deNull(tmp))
																			&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																					.getCargo_description()).equalsIgnoreCase(tmpCrgDesc))
																	.findFirst().orElse(-1);
														}
														if (indexHs >= 0) {
															indexNotUpdate.put(indexHs, indexHs);
														}
													}
												}
											}

											if (existAdd > 0) {
												for (int j = 0; j < miscDetailVolListAdd.size(); j++) {
													// For Number of packages declared in excel (Add)
													if (CommonUtil.deNull(miscDetailVolListAdd.get(j).getTypeCode())
															.equalsIgnoreCase(cellData_bill)) {
														total = total + Double
																.parseDouble(CommonUtil.deNull(miscDetailVolListAdd.get(j).getTypeValue()));
													}
												}
											}

											for (int j = 0; j < manifestHSDetailTmp.size(); j++) {
												// Here we got total of volume not included in excel
												if (!indexNotUpdate.containsValue(j)) {
													total = total + Double.parseDouble(
															CommonUtil.deNull(manifestHSDetailTmp.get(j).getGross_measurement_m3()));
												}
											}

											if (updateMainExist > 0) {
												int mainVolIndex = IntStream.range(0, miscDetailVolMainUpdate.size())
														.filter(l -> CommonUtil.deNull(miscDetailVolMainUpdate.get(l).getTypeCode())
																.equalsIgnoreCase(blNbr))
														.findFirst().orElse(-1);
												mainVol = CommonUtil.deNull(miscDetailVolMainUpdate.get(mainVolIndex).getTypeValue());
											} else {
												mainVol = manifestDetail.getGross_measurement_m3();
											}

											if (total > Double.parseDouble(mainVol) || total < Double.parseDouble(mainVol)) {
												miscNbrVolMainUpdateHs.add(msc);
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_Exceed_Vol + mainVol);
												commentsList.add(comments);
											}
										}
									}
								} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS) && !cannotDeleteSub) {
									double total = 0;
									long errorInvalidHSCode = commentsList.stream().parallel().filter((s) -> CommonUtil.deNull(s.getMessage()).equalsIgnoreCase(ConstantUtil.ErrorMsg_UpdateHSCode_Invalid)).count();
									boolean updateComesFirst = mainBLNbrListUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count() > 0;
									boolean updateSubComesFirst = mainBLNbrListSubUpdate.stream().parallel().filter((s) -> CommonUtil.deNull(s).equalsIgnoreCase(blNbr)).count() > 0;
									if (updateComesFirst || updateSubComesFirst) {
										long exist = miscNbrVolMainUpdate.stream().parallel()
												.filter((s) -> s.getTypeCode().equalsIgnoreCase(blNbr)).count();
										long existHS = miscNbrVolMainUpdateHs.stream().parallel()
												.filter((s) -> s.getTypeCode().equalsIgnoreCase(blNbr)).count();
										if (exist > 0 || existHS > 0) {
											total = 0;
											// If error in main update exist, total should be the same as what is declared
											// in Update Main
											if (exist > 0) {
												for (int j = 0; j < miscNbrVolMainUpdate.size(); j++) {
													if (miscNbrVolMainUpdate.get(j).getTypeCode()
															.equalsIgnoreCase(cellData_bill)) {
														total = Double
																.parseDouble(miscNbrVolMainUpdate.get(j).getTypeValue());
													}
												}
												// If error in sub update exist, total should be the same as what is
												// declared in DB (Main HS)
											} else if (existHS > 0) {
												total = Double.parseDouble(manifestDetail.getGross_measurement_m3());
											}
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_Exceed_Vol + total);
											commentsList.add(comments);
										}
									} else {// Delete row comes before update row
										Map<Integer, Integer> indexNotUpdate = new HashMap<Integer, Integer>();
										long updateMainExist = mainBLNbrListUpdate.stream().parallel()
												.filter((s) -> s.equalsIgnoreCase(blNbr)).count();
										long existUpdate = miscDetailVolListUpdate.stream().parallel()
												.filter((s) -> s.getTypeCode().equalsIgnoreCase(blNbr)).count();
										long existHSAdd = miscDetailVolListAdd.stream().parallel()
												.filter((s) -> s.getTypeCode().equalsIgnoreCase(blNbr)).count();
										// If neither have, should throw error
										if (updateMainExist == 0 && existUpdate == 0 && existHSAdd == 0) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_MainOrSubNotExist);
											commentsList.add(comments);
										} else {
											String mainVol = "";
											// Check if updated value sync
											if (updateMainExist > 0) {
												// to compare with total, we take value inserted in excel update
												int mainVolIndx = IntStream.range(0, miscDetailVolMainUpdate.size())
														.filter(l -> miscDetailVolMainUpdate.get(l).getTypeCode()
																.equalsIgnoreCase(blNbr))
														.findFirst().orElse(-1);
												mainVol = CommonUtil.deNull(miscDetailVolMainUpdate.get(mainVolIndx).getTypeValue());
											}

											// If update hs exist
											if (existUpdate > 0) {
												// Check if updated value sync with Main in DB
												for (int j = 0; j < miscDetailVolListUpdate.size(); j++) {
													// For volume declared in excel
													if (miscDetailVolListUpdate.get(j).getTypeCode()
															.equalsIgnoreCase(blNbr)) {
														total = total + Double
																.parseDouble(miscDetailVolListUpdate.get(j).getTypeValue());
													}

													// For volume not declared in excel
													if (miscDetailVolListUpdate.get(j).getTypeCode()
															.equalsIgnoreCase(cellData_bill)) {
														String tmp = CommonUtil.deNull(miscDetailsubOldHSCodelListUpdate.get(j).getTypeValue())
																.isEmpty() ? CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(j).getTypeValue())
																		: CommonUtil.deNull(miscDetailsubOldHSCodelListUpdate.get(j)
																				.getTypeValue());
														String tmpCustom = CommonUtil.deNull(miscDetailCustomSubUpdate.get(j).getTypeValue());
														String tmpCrgDesc = CommonUtil.deNull(miscDetailCrgDescSubUpdate.get(j).getTypeValue());
														int indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																.filter(l -> manifestHSDetailTmp.get(l)
																		.getHs_code_sub_code().equalsIgnoreCase(tmp)
																		&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																				.getCustom_hs_code()).equalsIgnoreCase(tmpCustom))
																.findFirst().orElse(-1);
														if(indexHs < 0) {
															indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																	.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																			.equalsIgnoreCase(CommonUtil.deNull(tmp))
																			&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																					.getCargo_description()).equalsIgnoreCase(tmpCrgDesc))
																	.findFirst().orElse(-1);
														}
														if (indexHs >= 0) {
															indexNotUpdate.put(indexHs, indexHs);
														}
													}

												}
											}

											if (updateMainExist == 0) {
												mainVol = manifestDetail.getGross_measurement_m3();
											}

											// Add that is being deleted should not be count
											for (int k = 0; k < miscDetailSubHsCodeListDel.size(); k++) {
												if (miscDetailSubHsCodeListDel.get(k).getTypeCode()
														.equalsIgnoreCase(cellData_bill)) {
													String tmp = CommonUtil.deNull(miscDetailSubOldHsCodeListDel.get(k).getTypeValue())
															.isEmpty() ? CommonUtil.deNull(miscDetailSubHsCodeListDel.get(k).getTypeValue())
																	: CommonUtil.deNull(miscDetailSubOldHsCodeListDel.get(k).getTypeValue());
													String tmpCustom = CommonUtil.deNull(miscDetailSubCustomHSCodeListDel.get(k).getTypeValue());
													String tmpCrgDesc = CommonUtil.deNull(miscDetailSubCargoDescListDel.get(k).getTypeValue());
													int indexHs = IntStream.range(0, manifestHSDetailTmp.size())
															.filter(l -> manifestHSDetailTmp.get(l).getHs_code_sub_code()
																	.equalsIgnoreCase(tmp)
																	&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																			.getCustom_hs_code()).equalsIgnoreCase(tmpCustom))
															.findFirst().orElse(-1);
													if(indexHs < 0) {
														indexHs = IntStream.range(0, manifestHSDetailTmp.size())
																.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code_sub_code())
																		.equalsIgnoreCase(CommonUtil.deNull(tmp))
																		&& CommonUtil.deNull(manifestHSDetailTmp.get(l)
																				.getCargo_description()).equalsIgnoreCase(tmpCrgDesc))
																.findFirst().orElse(-1);
													}
													if (indexHs >= 0) {
														indexNotUpdate.put(indexHs, indexHs);
													}
												}
											}
											
											// Check if add hs exists
											if (existHSAdd > 0) {
												for (int j = 0; j < miscDetailVolListAdd.size(); j++) {
													// For Number of packages declared in excel
													if (CommonUtil.deNull(miscDetailVolListAdd.get(j).getTypeCode())
															.equalsIgnoreCase(blNbr)) {
														total = total + Double.parseDouble(CommonUtil
																.deNull(miscDetailVolListAdd.get(j).getTypeValue()));
													}
												}
											}

											// Add total of value in DB that is not being touch
											for (int j = 0; j < manifestHSDetailTmp.size(); j++) {
												if (!indexNotUpdate.containsValue(j)) {
													total = total + Double
															.parseDouble(manifestHSDetailTmp.get(j).getGross_measurement_m3());
												}
											}

											// Compare value declared in excel, in db with main value
											if ((total > Double.parseDouble(mainVol) || total < Double.parseDouble(mainVol)) && errorInvalidHSCode == 0) {
												miscNbrVolDeleteHs.add(msc);
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_Exceed_Volume_Delete
														+ mainVol);
												commentsList.add(comments);
											}
										}
									}
								}
							} else {
								double tmpValue = CommonUtil.deNull(cellData).isEmpty() ? 0.00 : Double.parseDouble(cellData);
								if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
									if (tmpValue != Double.parseDouble(manifestDetail.getGross_measurement_m3())) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.Error_M20204);
										commentsList.add(comments);
									}
								} else if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_updateHS)) {
									prevVol = CommonUtil.deNull(prevVol).isEmpty() ? "0.00" : prevVol;
									if (tmpValue != Double.parseDouble(prevVol)) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.Error_M20204);
										commentsList.add(comments);
									}
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.cargo_status)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							removeColorsAndComment(ref, sheet, no_style);
							if (!cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
								if(!cm.getEdo_created()) {
									if (CommonUtil.deNull(cellData) == "") {
										if (!deleteflag && manifestUploadConfig.getMandatory_ind()
												.equalsIgnoreCase(ConstantUtil.yes)) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_CargoStatus);
											commentsList.add(comments);
										}
									} else {
										if (cellData.equalsIgnoreCase(ConstantUtil.cargo_status_local)) {
											cm.setCargo_status(ConstantUtil.cargo_status_L);
										} else if (cellData.equalsIgnoreCase(ConstantUtil.cargo_status_transhipment)) {
											cm.setCargo_status(ConstantUtil.cargo_status_T);
										} else {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_InvalidItemFromDroDown);
											commentsList.add(comments);
										}
									}
								} else {
									if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
										cellData = CommonUtil.deNull(cellData)
												.equalsIgnoreCase(ConstantUtil.cargo_status_local)
														? ConstantUtil.cargo_status_L
														: CommonUtil.deNull(cellData).equalsIgnoreCase(
																ConstantUtil.cargo_status_transhipment)
																		? ConstantUtil.cargo_status_T
																		: "";
										if (!CommonUtil.deNull(cellData)
												.equalsIgnoreCase(manifestDetail.getCargo_status())) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.Error_M20204);
											commentsList.add(comments);
										}
									}
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.dg_indicator)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							removeColorsAndComment(ref, sheet, no_style);
							if (!cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
								if(!cm.getEdo_created()) {

									if (cm != null && CommonUtil.deNull(cm.getBills_of_landing_no()) != "") {
										if(!CommonUtil.deNull(cellData).isEmpty() && cellData.equalsIgnoreCase("Y")) {
											chkDGInd = manifestRepo.chkDGInd(cm.getBills_of_landing_no(), vvCd);
										}
									}
									if (CommonUtil.deNull(cellData) == "") {
										if (!deleteflag
												&& manifestUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_DGIndic);
											commentsList.add(comments);
										}
									} else if (!chkDGInd) { // check DG indicator
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.Error_M20211);
										commentsList.add(comments);
									} else {
										if(!cellData.equalsIgnoreCase(ConstantUtil.yes) && !cellData.equalsIgnoreCase(ConstantUtil.no)) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_InvalidItemFromDroDown);
											commentsList.add(comments);
										} else {
											cm.setDg_indicator(cellData);
										}
									}
								} else {
									if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
										if (!CommonUtil.deNull(cellData)
												.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getDg_indicator()))) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.Error_M20204);
											commentsList.add(comments);
										}
									}
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.storage_indicator)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							removeColorsAndComment(ref, sheet, no_style);

							if (!cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
								if(!cm.getEdo_created()) {
									if (CommonUtil.deNull(cellData) == "") {
										if (!deleteflag && manifestUploadConfig.getMandatory_ind()
												.equalsIgnoreCase(ConstantUtil.yes)) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_StorageIndicator);
											commentsList.add(comments);
										}
									} else {
										if (cellData.equalsIgnoreCase(ConstantUtil.storage_ind_open)) {
											cm.setStorage_indicator(ConstantUtil.storage_ind_open_status);
										} else if (cellData.equalsIgnoreCase(ConstantUtil.storage_ind_covered)) {
											cm.setStorage_indicator(ConstantUtil.storage_ind_covered_status);
										} else {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_InvalidItemFromDroDown);
											commentsList.add(comments);
										}
									}
								} else {
									if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
										cellData = CommonUtil.deNull(cellData)
												.equalsIgnoreCase(ConstantUtil.storage_ind_open)
														? ConstantUtil.storage_ind_open_status
														: CommonUtil.deNull(cellData)
																.equalsIgnoreCase(ConstantUtil.storage_ind_covered)
																		? ConstantUtil.storage_ind_covered_status
																		: "";
										if (!CommonUtil.deNull(cellData)
												.equalsIgnoreCase(manifestDetail.getStorage_indicator())) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.Error_M20204);
											commentsList.add(comments);
										}
									}
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.packing_type)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							removeColorsAndComment(ref, sheet, no_style);
							if (!cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
								if (!cm.getEdo_created()) {
									if (CommonUtil.deNull(cellData) == "") {
										if (!deleteflag && manifestUploadConfig.getMandatory_ind()
												.equalsIgnoreCase(ConstantUtil.yes)) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_PakcagingType);
											commentsList.add(comments);
										}
									} else if (!deleteflag && !packagingTypeDropdownList.contains(cellData)) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_InvalidItemFromDroDown);
										commentsList.add(comments);
									} else {
										if (CommonUtil.deNull(cellData) != "") {
											String[] pakagingType = cellDataSplit(cellData);
											cm.setPacking_type(pakagingType[0].trim());
										}
									}
								} else  {
									if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
										cellData = CommonUtil.deNull(cellData).isEmpty() ? "" : cellDataSplit(cellData)[0];
										if (!CommonUtil.deNull(cellData)
												.equalsIgnoreCase(manifestDetail.getPacking_type())) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.Error_M20204);
											commentsList.add(comments);
										}
									}
								}
							}
						} else if (manifestUploadConfig.getAttr_name()
								.equals(ConstantUtil.discharge_operation_indicator)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							removeColorsAndComment(ref, sheet, no_style);
							if (!cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
								if (!cm.getEdo_created()) {
									if (CommonUtil.deNull(cellData) == "") {
										if (!deleteflag && manifestUploadConfig.getMandatory_ind()
												.equalsIgnoreCase(ConstantUtil.yes)) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_DischargeOprIndic);
											commentsList.add(comments);
										}
									} else {
										if (cellData.equalsIgnoreCase(ConstantUtil.dis_op_ind_normal)) {
											cm.setDischarge_operation_indicator(ConstantUtil.dis_op_ind_normal_status);
										} else if (cellData.equalsIgnoreCase(ConstantUtil.dis_op_ind_direct)) {
											cm.setDischarge_operation_indicator(ConstantUtil.dis_op_ind_direct_status);
										} else if (cellData.equalsIgnoreCase(ConstantUtil.dis_op_ind_overside)) {
											cm.setDischarge_operation_indicator(
													ConstantUtil.dis_op_ind_overside_status);
										} else {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_InvalidItemFromDroDown);
											commentsList.add(comments);
										}
									}
								} else {
									if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
										cellData = CommonUtil.deNull(cellData)
												.equalsIgnoreCase(ConstantUtil.dis_op_ind_normal)
														? ConstantUtil.dis_op_ind_normal_status
														: CommonUtil.deNull(cellData)
																.equalsIgnoreCase(ConstantUtil.dis_op_ind_direct)
																		? ConstantUtil.dis_op_ind_direct_status
																		: CommonUtil.deNull(cellData).equalsIgnoreCase(
																				ConstantUtil.dis_op_ind_overside)
																						? ConstantUtil.dis_op_ind_overside_status
																						: "";
										if (!CommonUtil.deNull(cellData)
												.equalsIgnoreCase(manifestDetail.getDischarge_operation_indicator())) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.Error_M20204);
											commentsList.add(comments);
										}
									}
								}
							}	
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.consignee)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String consOthersCellData = CommonUtil.getCellData(
									CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_consigneeOthers_index : ConstantUtil.consigneeOthers_index) + String.valueOf(i), sheet);
							removeColorsAndComment(ref, sheet, no_style);
							if (!cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
								if(!cm.getEdo_created()) {
									if (CommonUtil.deNull(cellData) == "") {
										if (!deleteflag && manifestUploadConfig.getMandatory_ind()
												.equalsIgnoreCase(ConstantUtil.yes)) {
											// START CR - IF Consignee Name Others Filled, Auto set Consignee Cd to "OTHERS"
											if(!CommonUtil.deNull(consOthersCellData).isEmpty()) {
												cm.setConsignee((ConstantUtil.others).toUpperCase());
											} else {
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_Consignee);
												commentsList.add(comments);
											}
											// END CR - IF Consignee Name Others Filled, Auto set Consignee Cd to "OTHERS"
										}
									} else if (!consigneeDropdownList.contains(cellData)) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_InvalidItemFromDroDown);
										commentsList.add(comments);
									} else {
										if (cellData.equalsIgnoreCase(ConstantUtil.others)) {
											cm.setConsignee(cellData);
										} else {
											cm.setConsignee(CommonUtil.consigneeSplit(cellData, "co_cd"));
											cm.setConsignee_others(CommonUtil.consigneeSplit(cellData, "co_nm"));
										}
									}
								} else {
									if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
										if (!CommonUtil.deNull(cellData).isEmpty() && consigneeDropdownList.contains(cellData)) {
											cellData = CommonUtil.consigneeSplit(cellData, "co_cd");
										}
										if (!CommonUtil.deNull(cellData)
												.equalsIgnoreCase(manifestDetail.getConsignee())) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.Error_M20204);
											commentsList.add(comments);
										}
									}
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.consignee_others)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							removeColorsAndComment(ref, sheet, no_style);
							if (cellData != null && cellData != "") {
								cellData = cellData.toUpperCase();
							}
							if (!cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
								if(!cm.getEdo_created()) {
									String consinee = cm.getConsignee();
									if (!deleteflag && (CommonUtil.deNull(cellData) == "" && consinee != null
											&& consinee.equalsIgnoreCase(ConstantUtil.others))) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_Consignee);
										commentsList.add(comments);
									} else if (CommonUtil.deNull(consinee) != ""
											&& consinee.equalsIgnoreCase(ConstantUtil.others)) {
										if (cellData.length() > 70) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage("Consignee Name" + ConstantUtil.ErrorMsg_70Length);
											commentsList.add(comments);
										} else {
											cm.setConsignee_others(cellData);
										}
									}
								} else {
									if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
										if (!CommonUtil.deNull(cellData)
												.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getConsignee_others()))) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.Error_M20204);
											commentsList.add(comments);
										}
									}
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.port_of_loading)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData="";
							if(cellData!=null && cellData!="")
							{
								upperCellData=cellData.toUpperCase();
							}
						
							removeColorsAndComment(ref, sheet, no_style);
							if (!cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
								if(!cm.getEdo_created()) {
									if (CommonUtil.deNull(upperCellData) == "") {
										if (!deleteflag && manifestUploadConfig.getMandatory_ind()
												.equalsIgnoreCase(ConstantUtil.yes)) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_PortLoading);
											commentsList.add(comments);
										}
									} else if (!deleteflag && !portListDropdownList.contains(upperCellData)) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.Error_M21601);
										commentsList.add(comments);
									} else {
										// START CR FTZ - Removed array, excel only gives port code - NS JULY 2024
										String ld_port = upperCellData;
										cm.setPort_of_loading(ld_port.trim());
										// END CR FTZ - Removed array, excel only gives port code - NS JULY 2024
									}
								} else {
									if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
										if (!CommonUtil.deNull(upperCellData)
												.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getPort_of_loading()))) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.Error_M20204);
											commentsList.add(comments);
										}
									}
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.port_of_discharge)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData="";
							if (!cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
								if (cellData != null && cellData != "") {
									upperCellData = cellData.toUpperCase();
								}

								removeColorsAndComment(ref, sheet, no_style);
								if(!cm.getEdo_created()) {
									if (!deleteflag && CommonUtil.deNull(upperCellData) == "") {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_PortDischarge);
										commentsList.add(comments);
									} else if (!deleteflag && !portListDropdownList.contains(upperCellData)) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.Error_M21602);
										commentsList.add(comments);
									} else if (!deleteflag && CommonUtil.deNull(cm.getPort_of_loading()) != ""
											&& CommonUtil.deNull(upperCellData) != "") {
										// START CR FTZ - Removed array, excel only gives port code - NS JULY 2024
										String dis_port = upperCellData;
										if (dis_port.trim().equalsIgnoreCase(cm.getPort_of_loading())) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_LaodDischarge_SamePort);
											commentsList.add(comments);
										} else {
											cm.setPort_of_discharge(dis_port.trim());
										}
									}

									else {
										String dis_port = upperCellData;
										cm.setPort_of_discharge(dis_port.trim());
										// END CR FTZ - Removed array, excel only gives port code - NS JULY 2024
									}
								} else {
									if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
										if (!CommonUtil.deNull(upperCellData)
												.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getPort_of_discharge()))) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.Error_M20204);
											commentsList.add(comments);
										}
									}
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.port_of_final_destination)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData="";
							if(cellData!=null && cellData!="")
							{
								upperCellData=cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if(!cm.getEdo_created()) {
								if (!deleteflag && CommonUtil.deNull(upperCellData) == "" && cm.getCargo_status() != null
										&& cm.getCargo_status().equalsIgnoreCase(ConstantUtil.cargo_status_T)) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_PortofFinalDestination);
									commentsList.add(comments);
								} else if (!deleteflag && (CommonUtil.deNull(upperCellData) != ""
										&& !portListDropdownList.contains(upperCellData))) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.Error_M21603);
									commentsList.add(comments);
								} else if (!deleteflag && CommonUtil.deNull(upperCellData) != "" && cm.getCargo_status() != null
										&& cm.getCargo_status().equalsIgnoreCase(ConstantUtil.cargo_status_L)) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.Error_PD_blank);
									commentsList.add(comments);
									// START CR FTZ - Removed array, excel only gives port code - NS JULY 2024
								}else if (!deleteflag && CommonUtil.deNull(upperCellData) != "" && upperCellData.equalsIgnoreCase("SGSIN")) { 
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_FinalDestPort);
									commentsList.add(comments);
								} else {
									if (CommonUtil.deNull(upperCellData) != "") {
										String des_port = upperCellData;
										cm.setPort_of_final_destination(des_port.trim());
										// END CR FTZ - Removed array, excel only gives port code - NS JULY 2024
									}
								}
							} else {
								if (cm.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
									if (!CommonUtil.deNull(upperCellData)
											.equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getPort_of_final_destination()))) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.Error_M20204);
										commentsList.add(comments);
									}
								}
							}
						} 
						// START FTZ CR ADDED NEW COLUMNS - NS JULY 2024
						else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.old_hscode)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String temp = "";
							String temphsCell = CommonUtil.getCellData(
											CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_hsCode_index : ConstantUtil.hsCode_index) + String.valueOf(i), sheet);
							String temphs = temphsCell;
							String tempCustom = CommonUtil.getCellData(
									CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_custom_index : ConstantUtil.custom_index) + String.valueOf(i), sheet);
							String tempCrgDesc= CommonUtil.getCellData(
									CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_crgDesc_index : ConstantUtil.crgDesc_index) + String.valueOf(i), sheet);
							String tempNbrPkg= CommonUtil.deNull(CommonUtil.getCellData(
									CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_pkgNbr_index : ConstantUtil.pkgNbr_index) + String.valueOf(i), sheet));
							String tempWght= CommonUtil.deNull(CommonUtil.getCellData(
									CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_grossWt_index : ConstantUtil.grossWt_index) + String.valueOf(i), sheet));
							String tempVol= CommonUtil.deNull(CommonUtil.getCellData(
									CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_grossVol_index : ConstantUtil.grossVol_index) + String.valueOf(i), sheet));
							removeColorsAndComment(ref, sheet, no_style);
							  
							if(cm.getAction().equalsIgnoreCase(ConstantUtil.action_updateHS)) {
								if (!deleteflag && !CommonUtil.deNull(cellData).isEmpty() && !hsCodeSubCodeDropdownList.contains(cellData) ) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_InvalidItemFromDroDown);
									commentsList.add(comments);
								} else {
									List<CargoManifest> manifestHSDetailTmp = manifestHSDetail;
									for (int j = 0; j < manifestHSDetailTmp.size(); j++) {
										if (manifestHSDetailTmp.get(j).getHs_code_sub_code().equalsIgnoreCase(CommonUtil.deNull(cellData).trim())
												&& manifestHSDetailTmp.get(j).getCargo_description().equalsIgnoreCase(tempCrgDesc)) {
											if(!CommonUtil.deNull(manifestHSDetailTmp.get(j).getCustom_hs_code()).equalsIgnoreCase(CommonUtil.deNull(tempCustom))) {
												// Custom changed
												tempCustom = manifestHSDetailTmp.get(j).getCustom_hs_code();
												break;
											}
										} else if (manifestHSDetailTmp.get(j).getHs_code_sub_code().equalsIgnoreCase(CommonUtil.deNull(cellData).trim())
												&& Integer.parseInt(manifestHSDetailTmp.get(j).getNumber_of_packages()) == Integer.parseInt(tempNbrPkg.isEmpty() ? "0" : tempNbrPkg)
												&& Double.parseDouble(manifestHSDetailTmp.get(j).getGross_weight_kg()) == Double.parseDouble(tempWght.isEmpty() ? "0" : tempWght)
												&& Double.parseDouble(manifestHSDetailTmp.get(j).getGross_measurement_m3()) == Double.parseDouble(tempVol.isEmpty() ? "0" : tempVol)) {
											if(!CommonUtil.deNull(manifestHSDetailTmp.get(j).getCustom_hs_code()).equalsIgnoreCase(CommonUtil.deNull(tempCustom))) {
												// Custom changed
												tempCustom = manifestHSDetailTmp.get(j).getCustom_hs_code();
												break;
											}
										}
									}
									String customhs = tempCustom;
									
									if(!CommonUtil.deNull(temphs).isEmpty()) {
										String[] hsCode = cellDataSplit(temphs);
										temphs = hsCode[0].trim() + "-" + hsCode[1].trim() + "-" + hsCode[2].trim();
									} 
									if(!CommonUtil.deNull(cellData).isEmpty()) {
										String[] hsCodeOld = cellDataSplit(cellData);
										temp = hsCodeOld[0].trim() + "-" + hsCodeOld[1].trim() + "-" + hsCodeOld[2].trim();
									} 
									if (CommonUtil.deNull(cellData) != "") {
										long hsExist = manifestHSDetailTmp.stream().parallel()
												.filter((s) -> s.getHs_code_sub_code().equalsIgnoreCase(cellData.trim())
														&& CommonUtil.deNull(s.getCustom_hs_code())
																.equalsIgnoreCase(CommonUtil.deNull(customhs)))
												.count();
										if (manifestHSDetailTmp.size() == 0) {
											boolean hsDeclared = manifestDetail.getHs_code_sub_code()
													.equalsIgnoreCase(temp)
													&& CommonUtil.deNull(manifestDetail.getCustom_hs_code())
															.equalsIgnoreCase(CommonUtil.deNull(tempCustom));
											if (!hsDeclared) {
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_OldHSCode_Declared);
												commentsList.add(comments);
											} else {
												String[] hsCodeOld = cellDataSplit(cellData);
												for (int hs = 0; hs < hsCodeOld.length; hs++) {
													if (hs == 0) {
														cm.setOldHSCode(hsCodeOld[hs].trim());
													} else if (hs == 1) {
														cm.setOldHSCode_fr(hsCodeOld[hs].trim());
													} else if (hs == 2) {
														cm.setOldHSCode_to(hsCodeOld[hs].trim());
													}
												}
											}
										} else if (hsExist == 0) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_OldHSCode_Declared);
											commentsList.add(comments);
										} else {
											String[] hsCodeOld = cellDataSplit(cellData);
											for (int hs = 0; hs < hsCodeOld.length; hs++) {
												if (hs == 0) {
													cm.setOldHSCode(hsCodeOld[hs].trim());
												} else if (hs == 1) {
													cm.setOldHSCode_fr(hsCodeOld[hs].trim());
												} else if (hs == 2) {
													cm.setOldHSCode_to(hsCodeOld[hs].trim());
												}
											}
										}
									} else {
										List<String> manifestHSCd = manifestRepo
												.getMainfestHSCode(manifestDetail.getMft_seq_nbr());
										if (!manifestHSCd.contains(temphs)) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_OldHSCode_Compulsary);
											commentsList.add(comments);
										} else if (manifestHSCd.contains(temphs)) {
											String tempHSCode = temphs;
											long hsExist = manifestHSDetailTmp.stream().parallel().filter(
													(s) -> s.getHs_code_sub_code().equalsIgnoreCase(tempHSCode)
															&& CommonUtil.deNull(s.getCustom_hs_code())
																	.equalsIgnoreCase(CommonUtil.deNull(customhs))
															&& CommonUtil.deNull(s.getCargo_description())
																	.equalsIgnoreCase(CommonUtil.deNull(tempCrgDesc)))
													.count();
											if (hsExist < 0) {
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_OldHSCode_Compulsary);
												commentsList.add(comments);
											}}
									} 
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.custom_hscode)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String cellData_HSCode = CommonUtil.getCellData(
									CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_hsCode_index : ConstantUtil.hsCode_index) + String.valueOf(i), sheet);
							String cellData_OldHSCode = CommonUtil.getCellData(
									CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_oldHsCode_index : ConstantUtil.oldHsCode_index) + String.valueOf(i), sheet);
							String temphs = "", oldtemphs = "";
							if (!CommonUtil.deNull(cellData_HSCode).isEmpty() && hsCodeSubCodeDropdownList.contains(cellData_HSCode)) {
								String[] hsCode = cellDataSplit(cellData_HSCode);
								temphs = hsCode[0].trim() + "-" + hsCode[1].trim() + "-" + hsCode[2].trim();
							}
							if(!CommonUtil.deNull(cellData_OldHSCode).isEmpty() && hsCodeSubCodeDropdownList.contains(cellData_OldHSCode)) {
								String[] oldhsCode = cellDataSplit(cellData_OldHSCode);
								oldtemphs = oldhsCode[0].trim() + "-" + oldhsCode[1].trim() + "-" + oldhsCode[2].trim();
							}
							String cellData_CargoDesc = CommonUtil.getCellData(
									CommonUtil.getColumnIndex(isSplitBL ? ConstantUtil.splitBL_crgDesc_index : ConstantUtil.crgDesc_index) + String.valueOf(i), sheet);
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							removeColorsAndComment(ref, sheet, no_style);
							cellData = CommonUtil.deNull(cellData).isEmpty() ? CommonUtil.deNull(cellData) : cellData.replace(".0", "");
							List<CargoManifest> manifestHSDetailTmp = manifestHSDetail;
							CargoManifest cmTmp = cm;
							String cellDataTmp = cellData;
							long errorEdoCreated = commentsList.stream().parallel().filter((s) -> CommonUtil.deNull(s.getMessage()).equalsIgnoreCase(ConstantUtil.Error_M20204)).count();
							if(!deleteflag) {
								if (!cellData.isEmpty()) {
									cellData = cellData.toUpperCase();
									if (cellData.length() != 4 && cellData.length() != 6 && cellData.length() != 8) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Length_CustomHSCode);
										commentsList.add(comments);
									} else {
										int hscodeCus = Integer.parseInt(cellData.substring(0, 2));
										int hssubcodeCus = Integer.parseInt(cellData.substring(2, 4));
										if(!CommonUtil.deNull(cm.getHs_code()).isEmpty() && !cellData.isEmpty()) {
											if (hscodeCus != Integer.parseInt(CommonUtil.deNull(cm.getHs_code()))) {
												if(errorEdoCreated == 0) {
													Comments comments = new Comments();
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(ConstantUtil.ErrorMsg_Wrong_CustomHSCode);
													commentsList.add(comments);
												}
											} else if (!(hssubcodeCus == Integer.parseInt(CommonUtil.deNull(cm.getHs_sub_code_fr()))
													|| (hssubcodeCus > Integer.parseInt(CommonUtil.deNull(cm.getHs_sub_code_fr()))
															&& hssubcodeCus < Integer.parseInt(CommonUtil.deNull(cm.getHs_sub_code_to())))
													|| hssubcodeCus == Integer.parseInt(CommonUtil.deNull(cm.getHs_sub_code_to())))) {
												if(errorEdoCreated == 0) {
													Comments comments = new Comments();
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(ConstantUtil.ErrorMsg_Wrong_CustomHSCode);
													commentsList.add(comments);
												}
											} else if (cellData.length() > 10) {
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_CustomLength);
												commentsList.add(comments);
											} else {
												cm.setCustom_hs_code(cellData);
											}
										} else {
											cm.setCustom_hs_code(cellData);
										}
									}
								} 
								// START - Check if main & sub custom is same (if included in excel)
								String blNbr = CommonUtil.deNull(cellData_bill);
								long mainUpdateexist = miscDetailCustomMainUpdate.stream().parallel()
										.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
								long mainAddexist = miscDetailCustomMainAdd.stream().parallel()
										.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
								long subUpdateexist = miscDetailCustomSubUpdate.stream().parallel()
										.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
								long subAddexist = miscDetailHSCodelListAdd.stream().parallel()
										.filter((s) -> CommonUtil.deNull(s.getTypeCode()).equalsIgnoreCase(blNbr)).count();
								if (manifestmultipleHSDetailExist || subAddexist > 0 || subUpdateexist > 0 || mainAddexist > 0) {
									String custom = CommonUtil.deNull(cellData);
									if (cellData_na.equalsIgnoreCase(ConstantUtil.action_update)) {
										if (manifestmultipleHSDetailExist) {
											// Check if same hs code have same custom hs code for update hs and main
											if (subUpdateexist > 0) {
												int indexHSCode = IntStream
														.range(0, miscDetailSubHsCodeListUpdate.size())
														.filter(k -> CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(k)
																.getTypeValue()).equalsIgnoreCase(cellData_HSCode)
																&& CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(k)
																		.getTypeCode()).equalsIgnoreCase(blNbr))
														.findFirst().orElse(-1);
												int indexCargoDesc = IntStream
														.range(0, miscDetailCrgDescSubUpdate.size())
														.filter(k -> CommonUtil.deNull(miscDetailCrgDescSubUpdate.get(k)
																.getTypeValue()).equalsIgnoreCase(cellData_CargoDesc)
																&& CommonUtil.deNull(miscDetailCrgDescSubUpdate.get(k)
																		.getTypeCode()).equalsIgnoreCase(blNbr))
														.findFirst().orElse(-1);
												int indexCustomHSCode = IntStream
														.range(0, miscDetailCustomSubUpdate.size())
														.filter(k -> CommonUtil.deNull(miscDetailCustomSubUpdate.get(k)
																.getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(custom))
																&& CommonUtil.deNull(miscDetailCustomSubUpdate.get(k)
																		.getTypeCode()).equalsIgnoreCase(blNbr))
														.findFirst().orElse(-1);
												if (indexCustomHSCode < 0
														&& (indexHSCode >= 0 && indexCargoDesc >= 0)) {
													Comments comments = new Comments();
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(ConstantUtil.ErrorMsg_UpdateCustom_MainSub);
													commentsList.add(comments);
												}
											}
											// Check if same hs code have same custom hs code for add hs and main
											if (subAddexist > 0) {
												int indexHSCode = IntStream
														.range(0, miscDetailHSCodelListAdd.size())
														.filter(k -> CommonUtil.deNull(miscDetailHSCodelListAdd.get(k)
																.getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(cellData_HSCode))
																&& miscDetailHSCodelListAdd.get(k)
																		.getTypeCode().equalsIgnoreCase(blNbr))
														.findFirst().orElse(-1);
												int indexCargoDesc = IntStream
														.range(0, miscDetailCrgDescSubAdd.size())
														.filter(k -> CommonUtil.deNull(miscDetailCrgDescSubAdd.get(k)
																.getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(cellData_CargoDesc))
																&& miscDetailCrgDescSubAdd.get(k)
																		.getTypeCode().equalsIgnoreCase(blNbr))
														.findFirst().orElse(-1);
												int indexCustomHSCode = IntStream
														.range(0, miscDetailCustomSubAdd.size())
														.filter(k -> CommonUtil.deNull(miscDetailCustomSubAdd.get(k)
																.getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(custom))
																&& CommonUtil.deNull(miscDetailCustomSubAdd.get(k)
																		.getTypeCode()).equalsIgnoreCase(blNbr))
														.findFirst().orElse(-1);
												if (indexCustomHSCode < 0
														&& (indexHSCode >= 0 && indexCargoDesc >= 0)) {
													Comments comments = new Comments();
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(ConstantUtil.ErrorMsg_UpdateCustom_MainSub);
													commentsList.add(comments);
												}
											}
											
											if(!CommonUtil.deNull(cm.getCustom_hs_code()).equalsIgnoreCase(CommonUtil.deNull(manifestDetail.getCustom_hs_code())) && !CommonUtil.deNull(cm.getCustom_hs_code()).isEmpty()) {
												if(subUpdateexist == 0) {
													Comments comments = new Comments();
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(ConstantUtil.ErrorMsg_UpdateCustom_MainSubMissing);
													commentsList.add(comments);
												}
											}
										}
									} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS)) {
										if (manifestmultipleHSDetailExist) {
											String data = cellData;
											if (mainUpdateexist > 0) {
												int indexHSCode = IntStream
														.range(0, miscDetailHSCodelListUpdate.size())
														.filter(k -> CommonUtil.deNull(miscDetailHSCodelListUpdate.get(k)
																.getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(cellData_HSCode))
																&& miscDetailHSCodelListUpdate.get(k)
																		.getTypeCode().equalsIgnoreCase(blNbr))
														.findFirst().orElse(-1);
												int indexCustomHSCode = IntStream
														.range(0, miscDetailCustomMainUpdate.size())
														.filter(k -> CommonUtil.deNull(miscDetailCustomMainUpdate.get(k)
																.getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(data))
																&& miscDetailCustomMainUpdate.get(k)
																		.getTypeCode().equalsIgnoreCase(blNbr))
														.findFirst().orElse(-1);
												int indexCargoDesc = IntStream
														.range(0, miscDetailCrgDescMainUpdate.size())
														.filter(k -> CommonUtil.deNull(miscDetailCrgDescMainUpdate.get(k)
																.getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(cellData_CargoDesc))
																&& miscDetailCrgDescMainUpdate.get(k)
																		.getTypeCode().equalsIgnoreCase(blNbr))
														.findFirst().orElse(-1);
												if (indexCustomHSCode < 0 && (indexHSCode >=0 && indexCargoDesc >=0) && (!oldtemphs.isEmpty() ? CommonUtil.deNull(oldtemphs)
														.equalsIgnoreCase(manifestDetail.getHs_code_sub_code()) : true)) {
													Comments comments = new Comments();
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(ConstantUtil.ErrorMsg_UpdateCustom_MainSub);
													commentsList.add(comments);
												}
											} else {
												if (cm.getBills_of_landing_no()
														.equalsIgnoreCase(manifestDetail.getBills_of_landing_no())
														&& temphs.equalsIgnoreCase(manifestDetail.getHs_code_sub_code())
														&& CommonUtil.deNull(cellData_CargoDesc)
																.equalsIgnoreCase(CommonUtil
																		.deNull(manifestDetail.getCargo_description()))
														&& !cellData.equalsIgnoreCase(
																CommonUtil.deNull(manifestDetail.getCustom_hs_code())
																		.toUpperCase())&& CommonUtil.deNull(oldtemphs)
														.equalsIgnoreCase(manifestDetail.getHs_code_sub_code())) {
														Comments comments = new Comments();
														comments.setKey(manifestUploadConfig.getAttr_name());
														comments.setMessage(ConstantUtil.ErrorMsg_UpdateCustom_MainSub);
														commentsList.add(comments);
												}
											}
											
											// START - To check if custom hs code changed for sub
											// Check if multiple same hs code exist
											String hsCode = CommonUtil.deNull(cm.getOldHSCode()).isEmpty() ? cmTmp.getHs_code() : cmTmp.getOldHSCode();
											String hsCodeFr = CommonUtil.deNull(cm.getOldHSCode()).isEmpty() ? cmTmp.getHs_sub_code_fr() : cmTmp.getOldHSCode_fr();
											String hsCodeTo = CommonUtil.deNull(cm.getOldHSCode()).isEmpty() ? cmTmp.getHs_sub_code_to() : cmTmp.getOldHSCode_to();
											long hsExist = manifestHSDetailTmp.stream().parallel()
													.filter((s) -> CommonUtil.deNull(s.getHs_code()).equalsIgnoreCase(
															CommonUtil.deNull(hsCode).trim())
															&& CommonUtil.deNull(s.getHs_sub_code_fr()).equalsIgnoreCase(
																	CommonUtil.deNull(hsCodeFr).trim())
															&& CommonUtil.deNull(s.getHs_sub_code_to()).equalsIgnoreCase(
																	CommonUtil.deNull(hsCodeTo).trim()))
													.count();
											// If more than 1, need to loop
											if(hsExist > 1) {
												for (int j = 0; j < manifestHSDetailTmp.size(); j++) {
													if (manifestHSDetailTmp.get(j).getHs_code().equalsIgnoreCase(CommonUtil.deNull(hsCode))
															&& manifestHSDetailTmp.get(j).getHs_sub_code_fr().equalsIgnoreCase(CommonUtil.deNull(hsCodeFr))
															&& manifestHSDetailTmp.get(j).getHs_sub_code_to().equalsIgnoreCase(CommonUtil.deNull(hsCodeTo))
															&& manifestHSDetailTmp.get(j).getCargo_description().equalsIgnoreCase(cellData_CargoDesc)) {
														// If current custom and existing custom is not the same, then custom hs change
														if(!CommonUtil.deNull(manifestHSDetailTmp.get(j).getCustom_hs_code()).equalsIgnoreCase(CommonUtil.deNull(cellData))) {
															cm.setCustomChanged(true);
															cm.setOldCustom(manifestHSDetailTmp.get(j).getCustom_hs_code());
														}
													}
												}
											// If equal to one, just check directly
											} else if(hsExist == 1){
												int index = IntStream.range(0, manifestHSDetailTmp.size())
														.filter(l -> CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_code())
																.equalsIgnoreCase(CommonUtil.deNull(hsCode))
																&& CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_sub_code_fr())
																		.equalsIgnoreCase(CommonUtil.deNull(hsCodeFr))
																&& CommonUtil.deNull(manifestHSDetailTmp.get(l).getHs_sub_code_to())
																		.equalsIgnoreCase(CommonUtil.deNull(hsCodeTo)))
														.findFirst().orElse(-1);
												// If current custom and existing custom is not the same, then custom hs change
												if(!CommonUtil.deNull(manifestHSDetailTmp.get(index).getCustom_hs_code()).equalsIgnoreCase(CommonUtil.deNull(cellData))) {
													cm.setCustomChanged(true);
													cm.setOldCustom(manifestHSDetailTmp.get(index).getCustom_hs_code());
												}
											}
											// END - To check if custom hs code changed for sub

											// Check for duplicate hs and custom (empty or not)
											// START - For existing hs code in excel, is there any same hs code exists
											String tmp = temphs;
											int multipleSameHsExistsUpdateExcel = 0;
											for (int j = 0; j < miscDetailCustomSubUpdate.size(); j++) {
												if(CommonUtil.deNull(miscDetailCustomSubUpdate.get(j).getTypeCode()).equalsIgnoreCase(CommonUtil.deNull(cmTmp.getBills_of_landing_no()))
														&& CommonUtil.deNull(miscDetailCustomSubUpdate.get(j).getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(cellDataTmp))
														&& CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(j).getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(cellData_HSCode)) 
														&& CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(j).getTypeCode()).equalsIgnoreCase(CommonUtil.deNull(cmTmp.getBills_of_landing_no()))) {
													multipleSameHsExistsUpdateExcel++;
												}
											}
											long multipleSameHsExistsDb = manifestHSDetailTmp.stream().parallel()
													.filter((s) -> CommonUtil.deNull(s.getCustom_hs_code())
															.equalsIgnoreCase(CommonUtil.deNull(cellDataTmp).trim())
															&& CommonUtil.deNull(s.getHs_code_sub_code())
																	.equalsIgnoreCase(CommonUtil.deNull(tmp)))
													.count();
											int multipleSameHsExistsAddExcel = IntStream
													.range(0, miscDetailCustomSubAdd.size())
													.filter(k -> CommonUtil.deNull(miscDetailCustomSubAdd.get(k)
															.getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(cellDataTmp))
															&& CommonUtil.deNull(miscDetailCustomSubAdd.get(k)
																	.getTypeCode()).equalsIgnoreCase(cmTmp.getBills_of_landing_no())
															&& CommonUtil.deNull(miscDetailHSCodelListAdd.get(k)
																	.getTypeCode()).equalsIgnoreCase(cmTmp.getBills_of_landing_no())
															&& CommonUtil.deNull(miscDetailHSCodelListAdd.get(k)
																	.getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(cellData_HSCode)))
													.findFirst().orElse(-1); 
											if(multipleSameHsExistsUpdateExcel > 1 || multipleSameHsExistsAddExcel >= 0) {
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_Same_Custom);
												commentsList.add(comments);
											} else if (multipleSameHsExistsDb > 0) {
												// For existing hs code in db, is there any same hs code exists
												if(cm.isCustomChanged()) {
													Comments comments = new Comments();
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(ConstantUtil.ErrorMsg_Same_Custom);
													commentsList.add(comments);
												}
											}
											// END - For existing hs code in excel, is there any same hs code exists
											else if (mainUpdateexist == 0 && cm.isCustomChanged()) {
												String hs = CommonUtil.deNull(cm.getOldHSCode()).isEmpty() ? temphs : oldtemphs;
												if (hs.equalsIgnoreCase(manifestDetail.getHs_code_sub_code())
														&& CommonUtil.deNull(cm.getOldCustom())
																.equalsIgnoreCase(CommonUtil
																		.deNull(manifestDetail.getCustom_hs_code()))) {
														Comments comments = new Comments();
														comments.setKey(manifestUploadConfig.getAttr_name());
														comments.setMessage(ConstantUtil.ErrorMsg_MainNotExist_Update);
														commentsList.add(comments);
												}
											}
										} else {
											// If current custom and existing custom is not the same, then custom hs change
											if(!CommonUtil.deNull(manifestHSDetailTmp.get(0).getCustom_hs_code()).equalsIgnoreCase(CommonUtil.deNull(cellData))) {
												cm.setCustomChanged(true);
												cm.setOldCustom(manifestHSDetailTmp.get(0).getCustom_hs_code());
											}
											if (mainUpdateexist == 0 && cm.isCustomChanged()) {
												Comments comments = new Comments();
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_MainNotExist_Update);
												commentsList.add(comments);
											} else if(mainUpdateexist > 0) {
												String data = cellData;
												int indexCustomHSCode = IntStream
														.range(0, miscDetailCustomMainUpdate.size())
														.filter(k -> CommonUtil.deNull(miscDetailCustomMainUpdate.get(k)
																.getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(data))
																&& CommonUtil.deNull(miscDetailCustomMainUpdate.get(k)
																		.getTypeCode()).equalsIgnoreCase(blNbr))
														.findFirst().orElse(-1); 
												if (indexCustomHSCode < 0) {
													Comments comments = new Comments();
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(ConstantUtil.ErrorMsg_UpdateCustom_MainSub);
													commentsList.add(comments);
												}
											} 
											if (subAddexist > 0) {
												int multipleSameHsExistsAddExcel = IntStream
														.range(0, miscDetailCustomSubAdd.size())
														.filter(k -> CommonUtil.deNull(miscDetailCustomSubAdd.get(k)
																.getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(cellDataTmp))
																&& CommonUtil.deNull(miscDetailCustomSubAdd.get(k)
																		.getTypeCode()).equalsIgnoreCase(cmTmp.getBills_of_landing_no())
																&& CommonUtil.deNull(miscDetailHSCodelListAdd.get(k)
																		.getTypeCode()).equalsIgnoreCase(cmTmp.getBills_of_landing_no())
																&& CommonUtil.deNull(miscDetailHSCodelListAdd.get(k)
																		.getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(cellData_HSCode)))
														.findFirst().orElse(-1); 
												if(multipleSameHsExistsAddExcel >= 0) {
													Comments comments = new Comments();
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(ConstantUtil.ErrorMsg_Same_Custom);
													commentsList.add(comments);
												} 
											}
										}
									} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_add)) {
										if (subAddexist > 0) {
											int index = IntStream.range(0, miscDetailHSCodelListAdd.size())
													.filter(k -> CommonUtil.deNull(miscDetailHSCodelListAdd.get(k).getTypeValue())
															.equalsIgnoreCase(CommonUtil.deNull(cellData_HSCode)) 
															&& CommonUtil.deNull(miscDetailHSCodelListAdd.get(k).getTypeCode()).equalsIgnoreCase(blNbr))
													.findFirst().orElse(-1);
											if(miscDetailCustomSubAdd.size() > 0 && index >=0) {
												if (!cellData
														.equalsIgnoreCase(CommonUtil.deNull(miscDetailCustomSubAdd.get(index).getTypeValue()))
														&& CommonUtil.deNull(miscDetailHSCodelListAdd.get(index).getTypeCode())
																.equalsIgnoreCase(blNbr)
														&& CommonUtil.deNull(miscDetailHSCodelListAdd.get(index).getTypeValue())
																.equalsIgnoreCase(CommonUtil.deNull(cellData_HSCode))) {
													Comments comments = new Comments();
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(ConstantUtil.ErrorMsg_UpdateCustom_MainSub);
													commentsList.add(comments);
												}
											}
										}
									} else if (cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS)) {
										if (mainAddexist > 0) {
											int index = IntStream.range(0, miscDetailCustomMainAdd.size())
													.filter(k -> miscDetailCustomMainAdd.get(k).getTypeCode()
															.equalsIgnoreCase(blNbr))
													.findFirst().orElse(-1);
											if (index >= 0) {
												if (!cellData.equalsIgnoreCase(CommonUtil
														.deNull(miscDetailCustomMainAdd.get(index).getTypeValue()))
														&& CommonUtil
																.deNull(miscDetailSubHsCodeListAdd.get(index)
																		.getTypeValue())
																.equalsIgnoreCase(CommonUtil.deNull(cellData_HSCode))
														&& CommonUtil
																.deNull(miscDetailCrgDescMainAdd.get(index)
																		.getTypeValue())
																.equalsIgnoreCase(CommonUtil.deNull(cellData_CargoDesc))) {
													Comments comments = new Comments();
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(ConstantUtil.ErrorMsg_UpdateCustom_MainSub);
													commentsList.add(comments);
												}
											}
										}
										String tmp = temphs;
										// START - For existing hs code in excel, is there any same hs code exists
										int multipleSameHsExistsUpdateExcel = IntStream
												.range(0, miscDetailCustomSubUpdate.size())
												.filter(k -> CommonUtil.deNull(miscDetailCustomSubUpdate.get(k)
														.getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(cellDataTmp))
														&& CommonUtil.deNull(miscDetailCustomSubUpdate.get(k)
																.getTypeCode()).equalsIgnoreCase(cmTmp.getBills_of_landing_no())
														&& CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(k)
																.getTypeCode()).equalsIgnoreCase(cmTmp.getBills_of_landing_no())
														&& CommonUtil.deNull(miscDetailSubHsCodeListUpdate.get(k)
																.getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(cellData_HSCode)))
												.findFirst().orElse(-1); 
										long multipleSameHsExistsDb = manifestHSDetailTmp.stream().parallel()
												.filter((s) -> CommonUtil.deNull(s.getCustom_hs_code()).equalsIgnoreCase(
																CommonUtil.deNull(cellDataTmp).trim())
																&& CommonUtil.deNull(s.getHs_code_sub_code()).equalsIgnoreCase(tmp))
												.count();
										int multipleSameHsExistsAddExcel = 0;
										for (int j = 0; j < miscDetailCustomSubAdd.size(); j++) {
											if(CommonUtil.deNull(miscDetailCustomSubAdd.get(j).getTypeCode()).equalsIgnoreCase(CommonUtil.deNull(cmTmp.getBills_of_landing_no()))
													&& CommonUtil.deNull(miscDetailCustomSubAdd.get(j).getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(cellDataTmp))
													&& CommonUtil.deNull(miscDetailHSCodelListAdd.get(j).getTypeValue()).equalsIgnoreCase(CommonUtil.deNull(cellData_HSCode)) 
													&& CommonUtil.deNull(miscDetailHSCodelListAdd.get(j).getTypeCode()).equalsIgnoreCase(CommonUtil.deNull(cmTmp.getBills_of_landing_no()))) {
												multipleSameHsExistsAddExcel++;
											}
										}
										if(multipleSameHsExistsUpdateExcel >= 0 || multipleSameHsExistsAddExcel > 1 || multipleSameHsExistsDb > 0) {
											Comments comments = new Comments();
											comments.setKey(manifestUploadConfig.getAttr_name());
											comments.setMessage(ConstantUtil.ErrorMsg_Same_Custom);
											commentsList.add(comments);
										}
										// END - For existing hs code in excel, is there any same hs code exists
									} 
								}
							
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.consignee_address)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (upperCellData.length() > 500) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage("Consignee Address" + ConstantUtil.ErrorMsg_500Length);
								commentsList.add(comments);
							} else {
								cm.setConsignee_addr(upperCellData);
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.shipper_name)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);

							if (!cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
								if (!deleteflag && CommonUtil.deNull(upperCellData) == "") {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_Shipper_nm);
									commentsList.add(comments);
								} else if (upperCellData.length() > 70) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage("Shipper Name" + ConstantUtil.ErrorMsg_70Length);
									commentsList.add(comments);
								} else {
									cm.setShipper_nm(upperCellData);
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.shipper_address)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);

							if (!cellData_na.equalsIgnoreCase(ConstantUtil.action_updateHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_addHS)
									&& !cellData_na.equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
								if (!deleteflag && CommonUtil.deNull(upperCellData) == "") {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_Shipper_Address);
									commentsList.add(comments);
								} else if (upperCellData.length() > 500) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage("Shipper Address" + ConstantUtil.ErrorMsg_500Length);
									commentsList.add(comments);
								} else {
									cm.setShipper_addr(upperCellData);
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.notify_party)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (upperCellData.length() > 70) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage("Notify Party Name" + ConstantUtil.ErrorMsg_70Length);
								commentsList.add(comments);
							} else {
								cm.setNotify_party(upperCellData);
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.notify_party_address)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (upperCellData.length() > 500) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage("Notify Party Address" + ConstantUtil.ErrorMsg_500Length);
								commentsList.add(comments);
							} else {
								cm.setNotify_party_addr(upperCellData);
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.place_of_delivery)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (upperCellData.length() > 70) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage("Place of Delivery" + ConstantUtil.ErrorMsg_70Length);
								commentsList.add(comments);
							} else {
								if (!CommonUtil.deNull(cellData).isEmpty()) {
									Pattern p = Pattern.compile("^a-z0-9//s", Pattern.CASE_INSENSITIVE);
									Matcher m = p.matcher(upperCellData);
									boolean found = m.find();
									if (found) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_SpecialChar);
										commentsList.add(comments);
									} else {
										cm.setPlace_of_delivery(upperCellData);
									}
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.place_of_receipt)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if (upperCellData.length() > 70) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage("Place of Receipt" + ConstantUtil.ErrorMsg_70Length);
								commentsList.add(comments);
							} else {
								if (!CommonUtil.deNull(cellData).isEmpty()) {
									Pattern p = Pattern.compile("^a-z0-9//s", Pattern.CASE_INSENSITIVE);
									Matcher m = p.matcher(upperCellData);
									boolean found = m.find();
									if (found) {
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_SpecialChar);
										commentsList.add(comments);
									} else {
										cm.setPlace_of_receipt(upperCellData);
									}
								}
							}
						} // END FTZ CR ADDED NEW COLUMNS - NS JULY 2024
						// START SPLIT BILL - NS Jan 2025
						else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.split_bl_ind)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							cm.setSplit_bl_ind(upperCellData);
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.split_main_bl)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							String upperCellData = "";
							if (cellData != null && cellData != "") {
								upperCellData = cellData.toUpperCase();
							}
							removeColorsAndComment(ref, sheet, no_style);
							if((CommonUtil.deNull(cm.getSplit_bl_ind()).equalsIgnoreCase("No") || CommonUtil.deNull(cm.getSplit_bl_ind()).isEmpty()) && cellData_na.equalsIgnoreCase(ConstantUtil.action_add)) {
								if(!upperCellData.isEmpty()) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_Main_Bill_Split_Only);
									commentsList.add(comments);
								}
							}else if(CommonUtil.deNull(cm.getSplit_bl_ind()).equalsIgnoreCase("Yes") && cellData_na.equalsIgnoreCase(ConstantUtil.action_add)) {
								if(upperCellData.isEmpty()) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_Main_Bill);
									commentsList.add(comments);
								} else {
									boolean containSpecialCharacter = CommonUtility.containSpecialCharacter(upperCellData);
									if (containSpecialCharacter) {	// validation for special characters here
										Comments comments = new Comments();
										comments.setKey(manifestUploadConfig.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_BlNoNoContainsSpecialChar);
										commentsList.add(comments);
										continue;
									} else {
										cm.setSplit_main_bl(upperCellData);
									}
								}
							} 
						}
						// END SPLIT BILL - NS Jan 2025
					}
					int headerSize = header.size() - 5; // remove vsl nm and invoy and 3 hath items (5)
					log.info("*********************** Hatch break down check start :" + headerSize); 
					if (noOfHatch > 0 && !cm.getEdo_created()) {
						for (int hatch = noOfHatch; hatch >=1; hatch--) {
							HatchDetails hbd = new HatchDetails();
							hbd.setHatch_cd("H" + hatch);
							String cellData_pkg = null;
							for (ManifestUploadConfig hatchBreakdown : hatch_header) {
								Row r = sheet.getRow(i - 1);

								if (hatchBreakdown.getAttr_name().equalsIgnoreCase(ConstantUtil.hatch_package)) {
									Cell cell_pkg = r.getCell(headerSize);
									String ref_pkg = cell_pkg.getAddress().formatAsString();
									cellData_pkg = CommonUtil.getCellData(ref_pkg, sheet);
									// cellData_pkg = df.format(Double.valueOf(cellData_pkg));
									removeColorsAndComment(ref_pkg, sheet, no_style);

									if (!deleteflag && cellData_pkg == null) {
										Comments comments = new Comments();
										comments.setKey(hatchBreakdown.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_HatchData);
										comments.setColumnNm(ref_pkg);
										commentsList.add(comments);
									}else if (!deleteflag && !CommonUtil.isInteger(cellData_pkg)) {
										Comments comments = new Comments();
										comments.setKey(hatchBreakdown.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_NonInteger);
										comments.setColumnNm(ref_pkg);
										commentsList.add(comments);
									} 
									else if (!deleteflag && !CommonUtil.isNumeric(cellData_pkg)) {
										Comments comments = new Comments();
										comments.setKey(hatchBreakdown.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
										comments.setColumnNm(ref_pkg);
										commentsList.add(comments);
									}
									
									else if (!deleteflag && (Double.parseDouble(cellData_pkg) < 0.0d)) {
										Comments comments = new Comments();
										comments.setKey(hatchBreakdown.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_HatchData);
										comments.setColumnNm(ref_pkg);
										commentsList.add(comments);
									} else {
										hbd.setNbr_pkgs(cellData_pkg);
									}
								} else if (hatchBreakdown.getAttr_name().equalsIgnoreCase(ConstantUtil.hatch_weight)) {
									Cell cell_wt = r.getCell(headerSize + 1);
									String ref_wt = cell_wt.getAddress().formatAsString();
									String cellData_wt = CommonUtil.getCellData(ref_wt, sheet);
									// cellData_wt = df.format(Double.valueOf(cellData_wt));
									removeColorsAndComment(ref_wt, sheet, no_style);
									if (!deleteflag && cellData_wt == null) {
										Comments comments = new Comments();
										comments.setKey(hatchBreakdown.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_HatchData);
										comments.setColumnNm(ref_wt);
										commentsList.add(comments);
									} else if (!deleteflag && !CommonUtil.isNumeric(cellData_wt)) {
										Comments comments = new Comments();
										comments.setKey(hatchBreakdown.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
										comments.setColumnNm(ref_wt);
										commentsList.add(comments);
									} else if (!deleteflag && (Double.parseDouble(cellData_wt) < 0.0d)
											|| (Double.parseDouble(cellData_pkg) >= 0.0d
													&& Double.parseDouble(cellData_wt) < 0.0d)
											|| (Double.parseDouble(cellData_pkg) <= 0.0d
													&& Double.parseDouble(cellData_wt) > 0.0d)) { // cellData_wt == null
																									// ||
										Comments comments = new Comments();
										comments.setKey(hatchBreakdown.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_HatchData);
										comments.setColumnNm(ref_wt);
										commentsList.add(comments);
									} /*else if (!deleteflag && (Double.parseDouble(cellData_pkg) > 0.0d
											&& Double.parseDouble(cellData_wt) < 10d)) {
										Comments comments = new Comments();
										comments.setKey(hatchBreakdown.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Valid_MinWeight);
										comments.setColumnNm(ref_wt);
										commentsList.add(comments);
									} else if (!deleteflag && (Double.parseDouble(cellData_pkg) > 0.0d
											&& Double.parseDouble(cellData_wt) > 20000000d)) {
										Comments comments = new Comments();
										comments.setKey(hatchBreakdown.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Valid_MaxWeight);
										comments.setColumnNm(ref_wt);
										commentsList.add(comments);
									}*/ else if (!deleteflag && !CommonUtil.decimalPointChk(cellData_wt)) {
										Comments comments = new Comments();
										comments.setKey(hatchBreakdown.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Valid_DecimalPt);
										comments.setColumnNm(ref_wt);
										commentsList.add(comments);
									} else {
										hbd.setGross_wt(cellData_wt);
									}
								} else if (hatchBreakdown.getAttr_name().equalsIgnoreCase(ConstantUtil.hatch_volume)) {
									Cell cell_mt = r.getCell(headerSize + 2);
									String ref_mt = cell_mt.getAddress().formatAsString();
									String cellData_mt = CommonUtil.getCellData(ref_mt, sheet);
									// cellData_mt = df.format(Double.valueOf(cellData_mt));
									removeColorsAndComment(ref_mt, sheet, no_style);

									if (!deleteflag && cellData_mt == null) {
										Comments comments = new Comments();
										comments.setKey(hatchBreakdown.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_HatchData);
										comments.setColumnNm(ref_mt);
										commentsList.add(comments);
									} else if (!deleteflag && !CommonUtil.isNumeric(cellData_mt)) {
										Comments comments = new Comments();
										comments.setKey(hatchBreakdown.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
										comments.setColumnNm(ref_mt);
										commentsList.add(comments);
									} else if (!deleteflag && (Double.parseDouble(cellData_mt) < 0)
											|| (Double.parseDouble(cellData_pkg) > 0
													&& Double.parseDouble(cellData_mt) < 0)
											|| (Double.parseDouble(cellData_pkg) <= 0
													&& Double.parseDouble(cellData_pkg) > 0)) { // cellData_mt == null
																								// ||
										Comments comments = new Comments();
										comments.setKey(hatchBreakdown.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_HatchData);
										comments.setColumnNm(ref_mt);
										commentsList.add(comments);
									} else if (!deleteflag && (Double.parseDouble(cellData_pkg) > 0.0d
											&& Double.parseDouble(cellData_mt) < 0.01d)) {
										Comments comments = new Comments();
										comments.setKey(hatchBreakdown.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Valid_MinGrossM3);
										comments.setColumnNm(ref_mt);
										commentsList.add(comments);
									} else if (!deleteflag && (Double.parseDouble(cellData_pkg) > 0.0d
											&& Double.parseDouble(cellData_mt) > 9999.99d)) {
										Comments comments = new Comments();
										comments.setKey(hatchBreakdown.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Valid_MaxGrossM3);
										comments.setColumnNm(ref_mt);
										commentsList.add(comments);
									} else if (!deleteflag && !CommonUtil.decimalPointChk(cellData_mt)) {
										Comments comments = new Comments();
										comments.setKey(hatchBreakdown.getAttr_name());
										comments.setMessage(ConstantUtil.ErrorMsg_Valid_DecimalPt);
										comments.setColumnNm(ref_mt);
										commentsList.add(comments);
									} else {
										hbd.setGross_vol(cellData_mt);
									}
								}
							}
							headerSize = headerSize + 3;
							hatchData.add(hbd);
							log.info("********************************** HBD Data:"+ hbd.toString());
						}
					}

					log.info(" Hatch data process start :" + headerSize);
					if (hatchData.size() > 0) {
						// no hatch validation

						// commnted now NS
						// headerSize = headerSize - 3;

						log.info(" Hatch data process start headerSize:" + headerSize);
						Row r = sheet.getRow(i - 1);
						Cell cell_noHatchPkg = r.getCell(headerSize);
						String ref_noHatchPkg = "";
						if (cell_noHatchPkg.getAddress() != null) {
							ref_noHatchPkg = cell_noHatchPkg.getAddress().formatAsString();
						}
						String cellData_noHatchPkg = CommonUtil.getCellData(ref_noHatchPkg, sheet);
						removeColorsAndComment(ref_noHatchPkg, sheet, no_style);

						// NO_HATCH_PKG
						if (cellData_noHatchPkg == null) { // cellData_pkg == null ||
							Comments comments = new Comments();
							comments.setKey("NO_HATCH_PKG");
							comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_HatchData);
							comments.setColumnNm(ref_noHatchPkg);
							commentsList.add(comments);
						} else if ((Double.parseDouble(cellData_noHatchPkg) < 0)) { // cellData_pkg == null ||
							Comments comments = new Comments();
							comments.setKey("NO_HATCH_PKG");
							comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_HatchData);
							comments.setColumnNm(ref_noHatchPkg);
							commentsList.add(comments);
						} else if (!CommonUtil.isNumeric(cellData_noHatchPkg)) {
							Comments comments = new Comments();
							comments.setKey("NO_HATCH_PKG");
							comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
							comments.setColumnNm(ref_noHatchPkg);
							commentsList.add(comments);
						}
						// WT
						Cell cell_noHatchWt = r.getCell(headerSize + 1);
						String ref_noHatchWt = cell_noHatchWt.getAddress().formatAsString();
						String cellData_noHatchWt = CommonUtil.getCellData(ref_noHatchWt, sheet);
						boolean coloremoval1 = removeColorsAndComment(ref_noHatchWt, sheet, no_style);
						log.info("Color and Comment Removal: " + coloremoval1);
						if (cellData_noHatchWt == null) {
							Comments comments = new Comments();
							comments.setKey("NO_HATCH_WT");
							comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_HatchData);
							comments.setColumnNm(ref_noHatchWt);
							commentsList.add(comments);
						} else if ((Double.parseDouble(cellData_noHatchWt) < 0)) {
							Comments comments = new Comments();
							comments.setKey("NO_HATCH_WT");
							comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_HatchData);
							comments.setColumnNm(ref_noHatchWt);
							commentsList.add(comments);
						} else if (!CommonUtil.isNumeric(cellData_noHatchPkg)) {
							Comments comments = new Comments();
							comments.setKey("NO_HATCH_WT");
							comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
							comments.setColumnNm(ref_noHatchWt);
							commentsList.add(comments);
						} else if (!CommonUtil.decimalPointChk(cellData_noHatchWt)) {
							Comments comments = new Comments();
							log.info(" cellData_noHatchWt :"+ cellData_noHatchWt);
							comments.setKey("NO_HATCH_WT");
							comments.setMessage(ConstantUtil.ErrorMsg_Valid_DecimalPt);
							comments.setColumnNm(ref_noHatchWt);
							commentsList.add(comments);
						}
						// MT
						Cell cell_noHatchMt = r.getCell(headerSize + 2);
						String ref_noHatchMt = cell_noHatchMt.getAddress().formatAsString();
						String cellData_noHatchMt = CommonUtil.getCellData(ref_noHatchMt, sheet);
						removeColorsAndComment(ref_noHatchMt, sheet, no_style);

						if (cellData_noHatchMt == null) {
							Comments comments = new Comments();
							comments.setKey("NO_HATCH_MT");
							comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_HatchData);
							comments.setColumnNm(ref_noHatchMt);
							commentsList.add(comments);
						} else if ((Double.parseDouble(cellData_noHatchMt) < 0)) {
							Comments comments = new Comments();
							comments.setKey("NO_HATCH_MT");
							comments.setMessage(ConstantUtil.ErrorMsg_Mandatory_HatchData);
							comments.setColumnNm(ref_noHatchMt);
							commentsList.add(comments);
						} else if (!CommonUtil.isNumeric(cellData_noHatchPkg)) {
							Comments comments = new Comments();
							comments.setKey("NO_HATCH_MT");
							comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
							comments.setColumnNm(ref_noHatchMt);
							commentsList.add(comments);
						} else if ((!CommonUtil.decimalPointChk(cellData_noHatchMt))) {
							Comments comments = new Comments();
							comments.setKey("NO_HATCH_MT");
							comments.setMessage(ConstantUtil.ErrorMsg_Valid_DecimalPt);
							comments.setColumnNm(ref_noHatchMt);
							commentsList.add(comments);
						}
					}
					cm.setRownum(i - 1);// -1 is to take the exact row number
					cm.setErrorInfo(commentsList);
					if (hatchData.size() > 0) {
						cm.setHatchList(hatchData);
					}
					if (commentsList.size() > 0) {
						cm.setMessage(ConstantUtil.error);
						if (CommonUtil.deNull(cm.getAction()).equalsIgnoreCase(ConstantUtil.action_add)
								|| CommonUtil.deNull(cm.getAction()).equalsIgnoreCase(ConstantUtil.action_update)
								|| (CommonUtil.deNull(cm.getAction()).isEmpty()
										&& !CommonUtil.deNull(cm.getCargoType()).isEmpty())) {
							mainHaveError.add(billOfLadingNo);
						} else if (CommonUtil.deNull(cm.getAction()).equalsIgnoreCase(ConstantUtil.action_addHS)
								|| CommonUtil.deNull(cm.getAction()).equalsIgnoreCase(ConstantUtil.action_updateHS)
								|| CommonUtil.deNull(cm.getAction()).equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
							subHaveError.add(billOfLadingNo);
						}
					} else {
						cm.setMessage(ConstantUtil.success);
					}
//					log.info(cm.toString());
					log.info("##################################################################### manifest Data :"+ cm.toString());
					manifestRecords.add(cm);
				} catch (BusinessException be) {
					log.info("Exception excelRowsValidate : ", be);
					throw new BusinessException(be.getMessage());
				} catch (Exception e) {
					log.info("Exception excelRowsValidate : ", e);
					if (cm == null) {
						cm = new CargoManifest();
					}
					cm.setRownum(i - 1);// -1 is to take the exact row number
					cm.setMessage(ConstantUtil.ErrorMsg_Common);
					cm.setErrorInfo(commentsList);
					if (CommonUtil.deNull(cm.getAction()).equalsIgnoreCase(ConstantUtil.action_add)
							|| CommonUtil.deNull(cm.getAction()).equalsIgnoreCase(ConstantUtil.action_update)
							|| (CommonUtil.deNull(cm.getAction()).isEmpty()
									&& !CommonUtil.deNull(cm.getCargoType()).isEmpty())) {
						mainHaveError.add(billOfLadingNo);
					} else if (CommonUtil.deNull(cm.getAction()).equalsIgnoreCase(ConstantUtil.action_addHS)
							|| CommonUtil.deNull(cm.getAction()).equalsIgnoreCase(ConstantUtil.action_updateHS)
							|| CommonUtil.deNull(cm.getAction()).equalsIgnoreCase(ConstantUtil.action_deleteHS)) {
						subHaveError.add(billOfLadingNo);
					}
					manifestRecords.add(cm);
					log.info("Exception row processing  row:" + i + " cm :" + cm.toString());
					log.info("Exception processing : ", e);
					continue;
				}

			}

			List<CargoManifest> processResults = manifestRepo.insertManifestData(manifestRecords, vvCd, userId, companyCode, mainHaveError, subHaveError, isSplitBL);
			int success = 0;
			int failure = 0;
			int headerRow = ConstantUtil.row_header;
			Row r_remarks = sheet.getRow(headerRow);
			int lastRemrksColumn = r_remarks.getLastCellNum();
			Cell cell_remarks = r_remarks.createCell(lastRemrksColumn);
			cell_remarks.setCellValue(ConstantUtil.remarks);
			sheet.autoSizeColumn(cell_remarks.getColumnIndex());

			for (CargoManifest cargoManifest : processResults) {
//				log.info("writing to Excel iteration :" + cargoManifest.toString());
				if (cargoManifest.getMessage().equalsIgnoreCase(ConstantUtil.success)) {
					Row r = sheet.getRow(cargoManifest.getRownum());
					Cell cell = r.createCell(lastRemrksColumn);
					cell.setCellValue(cargoManifest.getMessage());
					cell.setCellStyle(style_success);
					
					// update generated splitBL
					Cell cellsplit = r.getCell(3);
					cellsplit.setCellValue(cargoManifest.getBills_of_landing_no());
					success++;
				} else if (cargoManifest.getMessage() != null) {
					Row r1 = sheet.getRow(cargoManifest.getRownum());
					Cell c = r1.createCell(lastRemrksColumn);
					c.setCellValue(cargoManifest.getMessage());
					c.setCellStyle(style_error);
					failure++;
					if (cargoManifest.getErrorInfo().size() > 0) {

						List<Comments> errorInfo = cargoManifest.getErrorInfo();
						for (Comments comment : errorInfo) {
							for (ManifestUploadConfig manifestUploadConfig : header) {
								if (!manifestUploadConfig.getColumn_nm().equalsIgnoreCase("AD") // FTZ CR Change to Follow current sequence - NS JULY 2024
										&& manifestUploadConfig.getAttr_name().equals(comment.getKey())) {
									String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
									String ref = columnIndex + (cargoManifest.getRownum() + 1);
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
//									log.info("### manifestUploadConfig :comment.getKey() :" + comment.getKey()
//											+ ", RowNum:" + r.getRowNum() + " , column :" + cell.getColumnIndex()
//											+ " , Message :" + comment.getMessage());
									cell.setCellStyle(style_error);

								}
							}
							for (ManifestUploadConfig hatchBreakdown : hatch_header) {
								if (hatchBreakdown.getAttr_name().equals(comment.getKey())) {
									String columnIndex = CommonUtil.getColumnIndex(comment.getColumnNm());
									String ref = columnIndex + (cargoManifest.getRownum() + 1);
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
//									log.info("### hatchBreakdown :Key :" + comment.getKey() + ", RowNum:"
//											+ r.getRowNum() + " , column :" + cell.getColumnIndex() + " , Message :"
//											+ comment.getMessage());
									cell.setCellStyle(style_error);
								}

							}
							if (comment.getKey().equalsIgnoreCase(ConstantUtil.no_hatchpkg)) {
								String columnIndex = CommonUtil.getColumnIndex(comment.getColumnNm());
								String ref = columnIndex + (cargoManifest.getRownum() + 1);

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
//								log.info("### no_hatchpkg : RowNum:" + r.getRowNum() + " , column :"
//										+ cell.getColumnIndex() + " , Message :" + comment.getMessage());
								cell.setCellStyle(style_error);
							} else if (comment.getKey().equalsIgnoreCase(ConstantUtil.no_hatchwt)) {
								String columnIndex = CommonUtil.getColumnIndex(comment.getColumnNm());
								String ref = columnIndex + (cargoManifest.getRownum() + 1);

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
//								log.info("### no_hatchwt : RowNum:" + r.getRowNum() + " , column :"
//										+ cell.getColumnIndex() + " , Message :" + comment.getMessage());
								cell.setCellStyle(style_error);
							} else if (comment.getKey().equalsIgnoreCase(ConstantUtil.no_hatchmt)) {
								String columnIndex = CommonUtil.getColumnIndex(comment.getColumnNm());
								String ref = columnIndex + (cargoManifest.getRownum() + 1);

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
//								log.info("### no_hatchmt : RowNum:" + r.getRowNum() + " , column :"
//										+ cell.getColumnIndex() + " , Message :" + comment.getMessage());
								cell.setCellStyle(style_error);
							}
						}

					}
				}
			}

			// Draw Success and error Error excel
			//Summary summary = new Summary();
			summary.setTotalLineItemProcessed(String.valueOf(manifestRecords.size()));
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
	
	
	private boolean validCargoDescription(List<MiscDetail> cargoSelectionDropdownList, String cargoselection, String hsCode) throws BusinessException
	{
		boolean result=false;
		try
		{
			log.info("validCargoDescription : cargo selection: "+ CommonUtility.deNull(cargoselection) +" , hscode : " + CommonUtility.deNull(hsCode) +", cargoselectiondropdownlist : " + cargoSelectionDropdownList.toString());
			MiscDetail misc= cargoSelectionDropdownList.stream().filter(x -> x.getTypeValue() != null && x.getTypeValue().equalsIgnoreCase(cargoselection))
	.findFirst().orElse(null);
			if(misc!=null)
			{
				log.info("misc :"+ misc.toString() +", misc.getTypeCode().split(\".\")[1] :" + misc.getTypeCode().split("\\.").toString());
				if(misc.getTypeCode().split("\\.")[1].equalsIgnoreCase(hsCode))
				{
					result=true;
				}
			
			}
			
		}
		catch (Exception e) {
			log.info("Exception validCargoDescription:"+ e.getStackTrace());
			result=false;
		}
		finally {
			log.info(" validCargoDescription :"+ result );
		}
		
		return result;
	}

	private String[] cellDataSplit(String cellData) {
		String[] token = cellData.split("\\(|-|\\)|�");
		return token;
	}

	/*
	 * XSSFWorkbook prepareManifestDataExcel(MultipartFile file, Map<String,
	 * Integer> errorCellIndex, LinkedHashMap<Integer, String> insertResult) {
	 * XSSFWorkbook workbook = null; try { workbook = new
	 * XSSFWorkbook(OPCPackage.open(file.getInputStream())); XSSFSheet sheet =
	 * workbook.getSheetAt(0); CellReference cr = null; int rowIndex = 0; int
	 * colIndex = 0; int rowIndexLast = 0; CellStyle style =
	 * workbook.createCellStyle();
	 * style.setFillForegroundColor(IndexedColors.RED.index);
	 * style.setFillPattern(FillPatternType.SOLID_FOREGROUND); // fill error cells
	 * for (Entry<String, Integer> errorMapElement : errorCellIndex.entrySet()) { cr
	 * = new CellReference(errorMapElement.getKey()); rowIndex = cr.getRow();
	 * colIndex = cr.getCol();
	 * 
	 * Row r = sheet.getRow(rowIndex); int maxCell = r.getLastCellNum(); Cell celli
	 * = r.getCell(maxCell); if (rowIndexLast != rowIndex) { if (celli == null) {
	 * Cell c = r.createCell(maxCell);// .setCellValue(mapElement.getValue());
	 * c.setCellValue("Error"); c.setCellStyle(style); rowIndexLast = rowIndex; } }
	 * Row row = sheet.getRow(rowIndex); Cell cell = row.getCell(colIndex);
	 * cell.setCellStyle(style); // style.setBorderBottom(BorderStyle.MEDIUM);
	 * style.setBorderLeft(BorderStyle.DOUBLE);
	 * style.setBorderRight(BorderStyle.THICK);
	 * style.setBorderTop(BorderStyle.DASHED);
	 * 
	 * style.setBottomBorderColor(IndexedColors.YELLOW.getIndex());
	 * style.setLeftBorderColor(IndexedColors.YELLOW.getIndex());
	 * style.setRightBorderColor(IndexedColors.YELLOW.getIndex());
	 * style.setTopBorderColor(IndexedColors.YELLOW.getIndex()); }
	 * 
	 * // fill success / error cells for (Entry<Integer, String>
	 * insertResultMapElement : insertResult.entrySet()) { if
	 * (insertResultMapElement.getValue() == "Success") { Row r =
	 * sheet.getRow(insertResultMapElement.getKey()); int maxCell =
	 * r.getLastCellNum(); Cell cell = r.getCell(maxCell); if (cell == null) {
	 * r.createCell(maxCell).setCellValue(insertResultMapElement.getValue()); } }
	 * else { Row r = sheet.getRow(rowIndex); int maxCell = r.getLastCellNum(); Cell
	 * celli = r.getCell(maxCell); if (celli == null) { Cell c =
	 * r.createCell(maxCell); c.setCellValue(insertResultMapElement.getValue());
	 * c.setCellStyle(style); }
	 * 
	 * Row row = sheet.getRow(rowIndex); Cell cell = row.getCell(colIndex);
	 * cell.setCellStyle(style);
	 * 
	 * // style.setBorderBottom(BorderStyle.MEDIUM);
	 * style.setBorderLeft(BorderStyle.DOUBLE);
	 * style.setBorderRight(BorderStyle.THICK);
	 * style.setBorderTop(BorderStyle.DASHED);
	 * 
	 * style.setBottomBorderColor(IndexedColors.YELLOW.getIndex());
	 * style.setLeftBorderColor(IndexedColors.YELLOW.getIndex());
	 * style.setRightBorderColor(IndexedColors.YELLOW.getIndex());
	 * style.setTopBorderColor(IndexedColors.YELLOW.getIndex()); } } } catch
	 * (Exception e) { e.printStackTrace(); } return workbook;
	 * 
	 * }
	 */

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public Summary processManifestDetails(MultipartFile uploadingFile,
			CargoManifestFileUploadDetails cargoManifestFileUploadDetails, String vvCd, String userId, String companyCode, boolean isSplitBL) throws BusinessException {
		XSSFWorkbook workbook = null;
		FileOutputStream fileOut = null;
		Summary summary = new Summary();
		String outputFileName = null;
		XSSFSheet sheet=null;
		try {
			log.info("START: processManifestDetails Start "+" vvCd:"+ CommonUtility.deNull(vvCd) +" userId:"+ CommonUtility.deNull(userId));
			Long seq_id = manifestRepo.insertManifestExcelDetails(cargoManifestFileUploadDetails);
			SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmm");

			if (cargoManifestFileUploadDetails.getTypeCd().equals(ConstantUtil.typeCd_Manifest)) {
				log.info("Process vesselDetailsValidation Start  for vvCd :" + CommonUtility.deNull(vvCd));
			
				workbook = new XSSFWorkbook(OPCPackage.open(uploadingFile.getInputStream()));
				sheet = workbook.getSheetAt(0);
				summary = vesselDetailsValidation(workbook,sheet, vvCd,summary, isSplitBL); // Vessel and inward no validation
				log.info("Process vesselDetailsValidation END  for vvCd :" + CommonUtility.deNull(vvCd) + " summary :" + summary.toString());
				if (summary.isHeaderValid()) {
					log.info("Process excelTemplateValidation START  for vvCd :" + CommonUtility.deNull(vvCd));
					summary = excelTemplateValidation(workbook,sheet,summary, isSplitBL); // header validation
					log.info("Process excelTemplateValidation END  for vvCd :" + CommonUtility.deNull(vvCd) + " summary :"
							+ summary.toString());

					if (summary.isHeaderValid()) {
						log.info("Process excelRowsValidate START  for vvCd :" + CommonUtility.deNull(vvCd));
						// excel validate
						summary = excelRowsValidate(workbook,sheet, vvCd, userId,
								cargoManifestFileUploadDetails.getLast_modified_dttm(),summary, companyCode, isSplitBL); // rows validation
						log.info(
								"Process excelRowsValidate END  for vvCd :" + CommonUtility.deNull(vvCd) + " summary :" + summary.toString());
					}
				}
			}

			// 4)create error excel name

			if (cargoManifestFileUploadDetails.getTypeCd().equals(ConstantUtil.typeCd_Packaging)) {
				outputFileName = ConstantUtil.packaging_filename + f.format(new Date()) + ConstantUtil.file_ext;
			} else if (cargoManifestFileUploadDetails.getTypeCd().equals(ConstantUtil.typeCd_Manifest)) {
				outputFileName = ConstantUtil.manifest_filename + f.format(new Date()) + ConstantUtil.file_ext;
			}

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
				log.info("Exception processManifestDetails : ", e);
				throw new BusinessException("M4201");
			}

			// 6)update output file name
			boolean row = manifestRepo.updateManifestExcelDetails(seq_id, outputFileName);
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

			log.info("END: *** processManifestDetails Result *****" + summary.toString());
		} catch (BusinessException be) {
			log.info("Exception processManifestDetails : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception processManifestDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END processManifestDetails");
			sheet=null;
			workbook=null;
		}
		return summary;
	}

	@Override
	public Resource fileDownload(String seq_id, String type) throws BusinessException {
			String fileName = null;
			Resource res = null;
		try {
			log.info("START fileDownload seq_id:" + CommonUtility.deNull(seq_id) + "type:" + CommonUtility.deNull(type));
			Path rootLocation = null;// Paths.get(folderPath).toAbsolutePath().normalize();
			CargoManifestFileUploadDetails fileDetails = manifestRepo.getCargoManifestFileUploadDetails(seq_id);
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

	@Override
	public PageDetails manifestUploadDetail(String vvCd) throws BusinessException {
		PageDetails vessel_details = null;
		try {
			log.info("START manifestUploadDetail vvCd:" + CommonUtility.deNull(vvCd) );
			vessel_details = manifestRepo.manifestUploadDetail(vvCd);	
			log.info("END: *** manifestUploadDetail Result *****" + vessel_details);
		} catch (BusinessException be) {
			log.info("Exception manifestUploadDetail : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception manifestUploadDetail : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END manifestUploadDetail");
		}
		return vessel_details;
	}

	
	private Summary vesselDetailsValidation(XSSFWorkbook workbook, XSSFSheet sheet, String vvCd,Summary summary, boolean isSplitBL) throws BusinessException {
		VesselVoyValueObject vvObj = new VesselVoyValueObject();
		List<ManifestUploadConfig> template = new ArrayList<ManifestUploadConfig>();
		String dbVersion = "";
		try {
			log.info("START vesselDetailsValidation :vvCd :" + CommonUtility.deNull(vvCd));
			if(isSplitBL) {
				template=manifestRepo.getSplitBlTemplateHeader();
				dbVersion=manifestRepo.getTemplateVersionNo(ConstantUtil.manifest_split_bl_type_cd);
			}else {
				template = manifestRepo.getTemplateHeader();
				dbVersion=manifestRepo.getTemplateVersionNo(ConstantUtil.manifest_type_cd);
			}
			
			String excelVersionNo=workbook.getProperties().getCoreProperties().getRevision();
			
			if(!excelVersionNo.equals(dbVersion)) {
				throw new BusinessException("Please choose correct template for "+(isSplitBL ? "Split BL" : "Manifest"));	
			}
			
			PageDetails vesselCallDetails = manifestRepo.getVesselCallDetails(vvCd);
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
			
			for (ManifestUploadConfig manifestUploadConfig_header : template) {
				if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.vessel_name)) {
					
					CellReference cr = new CellReference(manifestUploadConfig_header.getColumn_nm() + 1);
					Row row_vsl_value = sheet.getRow(cr.getRow());
					if (row_vsl_value == null) {
						Row emptyRow = sheet.createRow(cr.getRow());
						Cell emptyCell = emptyRow.createCell(0);
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(ConstantUtil.ErrorMsg_invalidExcel);
						comments.setColumnNm(emptyCell.getAddress().formatAsString());
						commentsList.add(comments);
						break;
					}
					removeColorsAndComment(cr.formatAsString(), sheet, no_style);
					log.info(" dbVersion :"+ dbVersion +", excelVersionNo :"+ excelVersionNo);
					if(!dbVersion.equalsIgnoreCase(excelVersionNo)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(ConstantUtil.versionMismatch);
						comments.setColumnNm(cr.formatAsString());
						commentsList.add(comments);
					}
					
					String cellvalue = CommonUtil.getCellData(cr.formatAsString(), sheet);
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(cellvalue)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
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
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						comments.setColumnNm(cell_vsl_value.getAddress().formatAsString());
						commentsList.add(comments);
					} else {
						// for vsl existence check
						vvObj.setVslName(vsl_cell_value);
						isVslFlag = true;
						vsl_comments.setKey(manifestUploadConfig_header.getAttr_name());
						vsl_comments.setMessage(ConstantUtil.ErrorMsg_vslNotExist);
						vsl_comments.setColumnNm(cell_vsl_value.getAddress().formatAsString());
					}
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.inward_voyage_no)) {
					CellReference cr = new CellReference(manifestUploadConfig_header.getColumn_nm() + 2);
					removeColorsAndComment(cr.formatAsString(), sheet, no_style);
					String cellvalue = CommonUtil.getCellData(cr.formatAsString(), sheet);
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(cellvalue)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						comments.setColumnNm(cr.formatAsString());
						commentsList.add(comments);
					}
					Row row_invoy_value = sheet.getRow(cr.getRow());
					Cell cell_invoy_value = row_invoy_value.getCell(1);
					if (cell_invoy_value == null) {
						cell_invoy_value = row_invoy_value.createCell(1);
					}
					String inVoy_cell_value = CommonUtil.getCellData(cell_invoy_value.getAddress().formatAsString(),
							sheet);
					removeColorsAndComment(cr.formatAsString(), sheet, no_style);
					if (inVoy_cell_value == null) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						comments.setColumnNm(cell_invoy_value.getAddress().formatAsString());
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
						invoy_comments.setKey(manifestUploadConfig_header.getAttr_name());
						invoy_comments.setMessage(ConstantUtil.ErrorMsg_invoyNotExist);
						invoy_comments.setColumnNm(cell_invoy_value.getAddress().formatAsString());
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
				if (vvObj.getVoyNo() != null
						&& (!vvObj.getVoyNo().equalsIgnoreCase(vesselCallDetails.getInwardVoyNo()))) {
					commentsList.add(invoy_comments);
					log.info(vvObj.getVoyNo() + " :invoyNo Not matched with requested vvCd");
				}
			}
			if (commentsList.size() > 0) {
				for (Comments comment : commentsList) {

					for (ManifestUploadConfig manifestUploadConfig : template) {
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

	
	private Summary excelTemplateValidation(XSSFWorkbook workbook,XSSFSheet sheet,Summary summary, boolean isSplitBL) throws BusinessException {
		CellStyle style_error = null;
		int headerRow = 0;
		List<ManifestUploadConfig> template = null;
		List<Comments> commentsList = new ArrayList<Comments>();
		try {
			headerRow = ConstantUtil.row_header;
			int cellIndex = 0;
			Row header_row = sheet.getRow(headerRow);

			if(isSplitBL) {
				template = manifestRepo.getSplitBlTemplateHeader();
			} else {
				template = manifestRepo.getTemplateHeader();
			}
//			log.info("template :" + template.toString());
			log.info("template :" + template.size());

			style_error = workbook.createCellStyle();
			style_error.setFillForegroundColor(IndexedColors.RED.index);
			style_error.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			CargoManifest cm = new CargoManifest();
			cm.setRownum(headerRow);
			String header_value = null;

			// if (commentsList.size() == 0) {
			for (ManifestUploadConfig manifestUploadConfig_header : template) {
				Cell header_cell = header_row.getCell(cellIndex);
//				log.info("cellIndex :" + cellIndex + ", manifestUploadConfig_header :"
//						+ manifestUploadConfig_header.toString());
				if (header_cell != null) {
					header_value = CommonUtil.getCellData(header_cell.getAddress().formatAsString(), sheet);
				}

				if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.action)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.bills_of_landing_no)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.cargo_type)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.cargo_description)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.hs_code)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.cargo_selection)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.cargo_marking)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.number_of_packages)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.gross_weight)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.gross_measurement)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.cargo_status)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.dg_indicator)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.storage_indicator)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.packing_type)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name()
						.equals(ConstantUtil.discharge_operation_indicator)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);

					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.consignee)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.consignee_others)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.port_of_loading)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.port_of_discharge)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.port_of_final_destination)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				 // START FTZ CR ADDED NEW COLUMNS - NS JULY 2024
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.old_hscode)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.custom_hscode)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.consignee)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.consignee_address)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.shipper_name)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.shipper_address)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.notify_party)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.notify_party_address)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.place_of_delivery)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.place_of_receipt)) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} // END FTZ CR ADDED NEW COLUMNS - NS JULY 2024
				// START SPLIT BL - NS Jan 2025
				else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.split_bl_ind) && isSplitBL) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.split_main_bl) && isSplitBL) {
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						commentsList.add(comments);
					}
					cellIndex++;
				} 
				// END SPLIT BL - NS Jan 2025
			}
			// } //

			if (commentsList.size() > 0) {
				for (Comments comment : commentsList) {

//					if (comment.getKey().equalsIgnoreCase(ConstantUtil.remarks_key)) {
//						CellReference cellRef_remarks = new CellReference(comment.getColumnNm());
//						Row r = sheet.getRow(cellRef_remarks.getRow());
//						Cell cell = r.getCell(cellRef_remarks.getCol());
//						// set comment
//						XSSFDrawing hpt = sheet.createDrawingPatriarch();
//						XSSFComment comment1 = hpt
//								.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
//
//						comment1.setRow(r.getRowNum());
//						comment1.setColumn(cell.getColumnIndex());
//						comment1.setString(new XSSFRichTextString(comment.getMessage()));
//						cell.setCellComment(comment1);
//						cell.setCellStyle(style_error);
//					}

					for (ManifestUploadConfig manifestUploadConfig : template) {
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

//						if (comment.getKey().equalsIgnoreCase(ConstantUtil.remarks_key)) {
//							CellReference cellRef_remarks = new CellReference(comment.getColumnNm());
//							Row r = sheet.getRow(cellRef_remarks.getRow());
//							Cell cell = r.getCell(cellRef_remarks.getCol());
//							// set comment
//							XSSFDrawing hpt = sheet.createDrawingPatriarch();
//							XSSFComment comment1 = hpt
//									.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
//
//							comment1.setRow(r.getRowNum());
//							comment1.setColumn(cell.getColumnIndex());
//							comment1.setString(new XSSFRichTextString(comment.getMessage()));
//							cell.setCellComment(comment1);
//							cell.setCellStyle(style_error);
//						}

						for (ManifestUploadConfig manifestUploadConfig : template) {
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

	// Manifest Detail download
	@Override
	public XSSFWorkbook manifestDetailExcelDownload(String vvCd, boolean isSplitBL) throws BusinessException {
		XSSFWorkbook wb = new XSSFWorkbook();
		try {
			log.info("manifestDetailExcelDownload : vvcd = " + CommonUtility.deNull(vvCd));
			XSSFSheet sheet = wb.createSheet("cargo_manifest");
			String version = "";
			if(isSplitBL) {
				version=manifestRepo.getTemplateVersionNo(ConstantUtil.manifest_split_bl_type_cd);
			}else {
				version=manifestRepo.getTemplateVersionNo(ConstantUtil.manifest_type_cd);
			}
			
			wb.getProperties().getCoreProperties().setRevision(version);
			
			// Hide sheet for dropdown
			Sheet sheet_hidden = wb.createSheet("Reference");
			// wb.setSheetVisibility(1, SheetVisibility.HIDDEN);
			
			String noOfPackagesIndex = null;
			String grossWtIndex = null;
			String grossMtIndex = null;
//			String hatchRow = null;
			int startRow = ConstantUtil.row_start;
			int headerRow = ConstantUtil.row_header;
			// int mhd_i = 0;
			int vesselName_cell = ConstantUtil.vesselName_cell;
			int inwardVoy_cell = ConstantUtil.inwardVoy_cell;
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
			c1_sheet_hidden.setCellValue(ConstantUtil.template_version);
			c2_sheet_hidden.setCellValue(ConstantUtil.template_version_no);
			// c2_sheet_hidden.setCellValue(manifestRepo.getTemplateVersionNo());
			c1_sheet_hidden.setCellStyle(style_header);
			c2_sheet_hidden.setCellStyle(style);

			List<CargoManifest> manifestDetails = manifestRepo.getManifestDetails(vvCd);
			log.info(manifestDetails.size());
			int tempSize = manifestDetails.size();
			//START FTZ CR - Add data from MANIFEST_HSCODE_DETAILS - NS JULY 2024
			for (int i = 0; i < tempSize; i++) {
				CargoManifest mf = manifestDetails.get(i);
				manifestDetails.addAll(manifestRepo.getManifestHSDetails(mf.getMft_seq_nbr())); 
			}
			Collections.sort(manifestDetails, (d1, d2) -> {
				return Integer.parseInt(d2.getMft_seq_nbr()) - Integer.parseInt(d1.getMft_seq_nbr());
			});
			//END FTZ CR - Add data from MANIFEST_HSCODE_DETAILS - NS JULY 2024

			List<HatchDetails> manifestHatchDetails = manifestRepo.getManifestHatchDetails(vvCd);
			List<ManifestUploadConfig> template = new ArrayList<ManifestUploadConfig>();
			if(isSplitBL) {
				template = manifestRepo.getSplitBlTemplateHeader();
			} else {
				template = manifestRepo.getTemplateHeader();
			}
			
			PageDetails vesselCalldetails = manifestRepo.getVesselCallDetails(vvCd);

			for (ManifestUploadConfig manifestUploadConfig_vessel_details : template) {
				if (manifestUploadConfig_vessel_details.getAttr_name().equalsIgnoreCase(ConstantUtil.vessel_name)) {
					CellReference cellRef = new CellReference(
							manifestUploadConfig_vessel_details.getColumn_nm() + vesselName_cell);
					Row r = sheet.createRow(cellRef.getRow());
					Cell cell = r.createCell(cellRef.getCol());
					cell.setCellValue(manifestUploadConfig_vessel_details.getAttr_desc());
					cell.setCellStyle(style_header);
					Cell cell1 = r.createCell(cellRef.getCol() + 1);
					cell1.setCellValue(vesselCalldetails.getVesselName());
					cell1.setCellStyle(style_header);
				} else if (manifestUploadConfig_vessel_details.getAttr_name()
						.equalsIgnoreCase(ConstantUtil.inward_voyage_no)) {
					CellReference cellRef = new CellReference(
							manifestUploadConfig_vessel_details.getColumn_nm() + inwardVoy_cell);
					Row r = sheet.createRow(cellRef.getRow());
					Cell cell = r.createCell(cellRef.getCol());
					cell.setCellValue(manifestUploadConfig_vessel_details.getAttr_desc());
					cell.setCellStyle(style_header);
					Cell cell1 = r.createCell(cellRef.getCol() + 1);
					cell1.setCellValue(vesselCalldetails.getInwardVoyNo());
					cell1.setCellStyle(style_header);
				}
			}

			// To set limit same as data if more than default limit - NS AUG 2024
			int limit = manifestDetails.size() >= ConstantUtil.limit ? (manifestDetails.size() + 10) : ConstantUtil.limit;
			// TO draw rest of the excel lines
			for (int k = manifestDetails.size(); k <= limit; k++) {
				CargoManifest cm = new CargoManifest();
				manifestDetails.add(cm);
			}
			// Starts after row number 4 - NS AUG 2024
			limit = limit + ConstantUtil.row_start;
			Row header_r = sheet.createRow(headerRow);
			Cell cell = null;
			for (ManifestUploadConfig manifestUploadConfig_header : template) {
				if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.action)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);
//					hatchRow = manifestUploadConfig_header.getColumn_nm();
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.bills_of_landing_no)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);
					if(isSplitBL) {
						XSSFDrawing hpt = sheet.createDrawingPatriarch();
						XSSFComment comment1 = hpt.createCellComment(
								new XSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
						comment1.setRow(header_r.getRowNum());
						comment1.setColumn(cell.getColumnIndex());
						comment1.setString(new XSSFRichTextString(ConstantUtil.blNumbersplitbill_tooltip));
						cell.setCellComment(comment1);
					}
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.cargo_type)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.cargo_description)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.hs_code)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.cargo_selection)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.cargo_marking)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.number_of_packages)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.gross_weight)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.gross_measurement)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.cargo_status)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.dg_indicator)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.storage_indicator)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.packing_type)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name()
						.equals(ConstantUtil.discharge_operation_indicator)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.consignee)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.consignee_others)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.port_of_loading)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.port_of_discharge)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.port_of_final_destination)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				}
				// START FTZ CR ADDED NEW COLUMNS - NS JULY 2024
				else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.old_hscode)) {
					// Normal Header
					int rowNum = headerRow;
					Row r_h = sheet.getRow(rowNum);
					
					// Upper Header
					int rowHs = headerRow - 1;
					sheet.createRow(rowHs);
					
					// Create cell for upper header
					cell = sheet.getRow(rowHs).createCell(r_h.getLastCellNum());
					String cell_hMerge = cell.getAddress().toString();
					CellReference crf = new CellReference(cell_hMerge);
					
					cell.setCellValue(ConstantUtil.hs_Code);
					cell.setCellStyle(style_header);
					sheet.autoSizeColumn(cell.getColumnIndex());
					
					Cell cell_h11 = sheet.getRow(rowHs).createCell(crf.getCol() + 1);
					Cell cell_h12 = sheet.getRow(rowHs).createCell(crf.getCol() + 2);
					Cell cell_h13 = sheet.getRow(rowHs).createCell(crf.getCol() + 3);
					Cell cell_h14 = sheet.getRow(rowHs).createCell(crf.getCol() + 4);
					Cell cell_h15 = sheet.getRow(rowHs).createCell(crf.getCol() + 5);
					Cell cell_h16 = sheet.getRow(rowHs).createCell(crf.getCol() + 6);
					cell_h11.setCellStyle(style_header);
					cell_h12.setCellStyle(style_header);
					cell_h13.setCellStyle(style_header);
					cell_h14.setCellStyle(style_header);
					cell_h15.setCellStyle(style_header);
					cell_h16.setCellStyle(style_header);
					sheet.autoSizeColumn(cell_h11.getColumnIndex());
					sheet.autoSizeColumn(cell_h12.getColumnIndex());
					sheet.autoSizeColumn(cell_h13.getColumnIndex());
					sheet.autoSizeColumn(cell_h14.getColumnIndex());
					sheet.autoSizeColumn(cell_h15.getColumnIndex());
					sheet.autoSizeColumn(cell_h16.getColumnIndex());
					if(isSplitBL) {
						sheet.createFreezePane(4, 0);
					}else {
						sheet.createFreezePane(2, 0);
					}
					
					sheet.addMergedRegion(
							new CellRangeAddress(crf.getRow(), crf.getRow(), crf.getCol(), crf.getCol() + 6));
					
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);
				} 
				else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.custom_hscode)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);
				} 
				else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.consignee_address)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.shipper_name)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.shipper_address)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.notify_party)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.notify_party_address)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.place_of_delivery)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.place_of_receipt)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				// Start new cols for split BL - NS Jan 2025
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.split_bl_ind)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.split_main_bl)) {
					cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
							manifestUploadConfig_header.getAttr_desc(), style_header);

				}
				// End new cols for split BL - NS Jan 2025
				// END FTZ CR ADDED NEW COLUMNS - NS JULY 2024
			}
			// ---------------------------------------------------------------------------------------
			int noOfHatch = manifestRepo.noOfHatchesOnPageLoad(vvCd);
			int rowNum = headerRow;
			int H_rowNum = headerRow - 1;
			Row r_h = sheet.getRow(rowNum);
			// This will create a new row for headers H1,H2,H3..etc - without this null
			// pointer exception occur
			// sheet.createRow(H_rowNum); - CR FTZ - Created in old hs code section - NS JULY 2024
			if (noOfHatch > 0) {
				for (int i = noOfHatch; i >=1 ; i--) {
					Cell cell_h = sheet.getRow(H_rowNum).createCell(r_h.getLastCellNum());
					cell_h.setCellValue("H" + i);
					String cell_hMerge = cell_h.getAddress().toString();
					CellReference crf = new CellReference(cell_hMerge);
					Cell cell_h11 = sheet.getRow(H_rowNum).createCell(crf.getCol() + 1);
					Cell cell_h12 = sheet.getRow(H_rowNum).createCell(crf.getCol() + 2);
					cell_h.setCellStyle(style_header);
					cell_h11.setCellStyle(style_header);
					cell_h12.setCellStyle(style_header);
					sheet.addMergedRegion(
							new CellRangeAddress(crf.getRow(), crf.getRow(), crf.getCol(), crf.getCol() + 2));

					Row r_cellPkg = sheet.getRow(headerRow);
					Cell cell_pkg = r_cellPkg.createCell(r_h.getLastCellNum());
					cell_pkg.setCellValue("H" + i + "- PKG");
					cell_pkg.setCellStyle(style_header);
					sheet.autoSizeColumn(cell_pkg.getColumnIndex());

					Row r_cellWt = sheet.getRow(headerRow);
					Cell cell_wt = r_cellWt.createCell(r_h.getLastCellNum());
					cell_wt.setCellValue("H" + i + " - Weight");
					cell_wt.setCellStyle(style_header);
					sheet.autoSizeColumn(cell_wt.getColumnIndex());

					Row r_cellMt = sheet.getRow(headerRow);
					Cell cell_mt = r_cellMt.createCell(r_h.getLastCellNum());
					cell_mt.setCellValue("H" + i + " - Volume");
					cell_mt.setCellStyle(style_header);
					sheet.autoSizeColumn(cell_mt.getColumnIndex());
				}
			}
			// NO hatches
			if (noOfHatch > 0) {
				Row r_noH = sheet.getRow(H_rowNum);
				Cell cell_noH = r_noH.createCell(r_h.getLastCellNum());
				String cell_noHMerge = cell_noH.getAddress().toString();
				CellReference crf = new CellReference(cell_noHMerge);

				cell_noH.setCellValue(ConstantUtil.no_hatch);
				cell_noH.setCellStyle(style_header);
				sheet.autoSizeColumn(cell_noH.getColumnIndex());

				Row r_noH11 = sheet.getRow(H_rowNum);
				Cell cell_noH11 = r_noH11.createCell(crf.getCol() + 1);
				cell_noH11.setCellStyle(style_header);
				sheet.autoSizeColumn(cell_noH11.getColumnIndex());

				Row r_noH12 = sheet.getRow(H_rowNum);
				Cell cell_noH12 = r_noH12.createCell(crf.getCol() + 2);
				cell_noH12.setCellStyle(style_header);
				sheet.autoSizeColumn(cell_noH12.getColumnIndex());

				sheet.addMergedRegion(new CellRangeAddress(crf.getRow(), crf.getRow(), crf.getCol(), crf.getCol() + 2));

				Row r_noH1 = sheet.getRow(headerRow);
				Cell cell_noH1 = r_noH1.createCell(r_h.getLastCellNum());
				cell_noH1.setCellValue(ConstantUtil.no_hatch_pkg);
				cell_noH1.setCellStyle(style_header);
				sheet.autoSizeColumn(cell_noH1.getColumnIndex());

				Row r_noH2 = sheet.getRow(headerRow);
				Cell cell_noH2 = r_noH2.createCell(r_h.getLastCellNum());
				cell_noH2.setCellValue(ConstantUtil.no_hatch_wt);
				cell_noH2.setCellStyle(style_header);
				sheet.autoSizeColumn(cell_noH2.getColumnIndex());

				Row r_noH3 = sheet.getRow(headerRow);
				Cell cell_noH3 = r_noH3.createCell(r_h.getLastCellNum());
				cell_noH3.setCellValue(ConstantUtil.no_hatch_mt);
				cell_noH3.setCellStyle(style_header);
				sheet.autoSizeColumn(cell_noH3.getColumnIndex());
			}
			log.info("manifestDetails count:"+ manifestDetails.size());
			for (CargoManifest cargoManifest : manifestDetails) {
				Row r = sheet.createRow(startRow++);
//				log.info("###row :"+ startRow);
//				log.info(cargoManifest.toString());
				for (ManifestUploadConfig manifestUploadConfig : template) {
					if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.action)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getAction(), style);

					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.bills_of_landing_no)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getBills_of_landing_no(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.cargo_type)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getCargoType(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.cargo_description)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getCargo_description(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.hs_code)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getHs_code_sub_code(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.cargo_selection)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getCargo_selection(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.cargo_marking)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getCargo_marking(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.number_of_packages)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getNumber_of_packages(), style);
						noOfPackagesIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.gross_weight)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getGross_weight_kg(), style);
						grossWtIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.gross_measurement)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getGross_measurement_m3(), style);
						grossMtIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.cargo_status)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getCargo_status(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.dg_indicator)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getDg_indicator(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.storage_indicator)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getStorage_indicator(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.packing_type)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getPacking_type(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.discharge_operation_indicator)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getDischarge_operation_indicator(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.consignee)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getConsignee(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.consignee_others)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getConsignee_others(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.port_of_loading)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getPort_of_loading(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.port_of_discharge)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getPort_of_discharge(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.port_of_final_destination)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(),
								cargoManifest.getPort_of_final_destination(), style);
					}
					// START FTZ CR ADDED NEW COLUMNS - NS JULY 2024
					else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.old_hscode)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(), "", style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.custom_hscode)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(), cargoManifest.getCustom_hs_code(), style);
						DataFormat fmt = wb.createDataFormat();
						CellStyle cs = style;
						cs.setDataFormat(fmt.getFormat("@"));
						cell.setCellStyle(cs);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.consignee_address)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(), cargoManifest.getConsignee_addr(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.shipper_name)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(), cargoManifest.getShipper_nm(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.shipper_address)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(), cargoManifest.getShipper_addr(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.notify_party)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(), cargoManifest.getNotify_party(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.notify_party_address)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(), cargoManifest.getNotify_party_addr(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.place_of_delivery)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(), cargoManifest.getPlace_of_delivery(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.place_of_receipt)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(), cargoManifest.getPlace_of_receipt(), style);
					// start split bl - NS Jan 2025
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.split_bl_ind)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(), cargoManifest.getSplit_bl_ind(), style);
					} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.split_main_bl)) {
						cell = createCellvalues(sheet, r, manifestUploadConfig.getColumn_nm(), cargoManifest.getSplit_main_bl(), style);
					}
					// END FTZ CR ADDED NEW COLUMNS - NS JULY 2024
				}
				String noHatchPkgFormula = "";
				String noHatchGrossWtFormula = "";
				String noHatchGrossMtFormula = "";
				List<String> noHatchFormulaList = new ArrayList<String>();

				// FOR HATCHWISE
				if (noOfHatch > 0) {
					for (int i = noOfHatch; i >= 1; i--) {
						int index = r.getRowNum() + 1;

						int hatchNbr = i;
						String hatchCd = ConstantUtil.hatchCode + Integer.toString(hatchNbr);

						HatchDetails hatchCardDet = manifestHatchDetails.stream()
								.filter(x -> x.getMft_seq_nbr().equalsIgnoreCase(cargoManifest.getMft_seq_nbr())
										&& x.getHatch_cd().equalsIgnoreCase(hatchCd))
								.findFirst().orElse(null);

						// no of package
						if (hatchCardDet != null && hatchCardDet.getNbr_pkgs() != null && cargoManifest.getMft_hscode_seq_nbr() == null) {
							cell = r.createCell(r.getLastCellNum());
							cell.setCellValue(Integer.parseInt(hatchCardDet.getNbr_pkgs()));
							cell.setCellStyle(style);
						} else {
							cell = r.createCell(r.getLastCellNum());
							cell.setCellValue(0);
							cell.setCellStyle(style);
						}
						CellReference cellRef = new CellReference(r.getRowNum(), r.getLastCellNum() - 1);
						String pkgIndex = CommonUtil.getColumnIndex(cellRef.formatAsString());

						// gross wt
						cell = r.createCell(r.getLastCellNum());
						cell.setCellFormula("ROUND(IF(" + noOfPackagesIndex + index + ">0," + pkgIndex + index + "*("
								+ grossWtIndex + index + (isSplitBL ? "/L" : "/J") + index + "),0),2)");

						if (hatchCardDet != null && hatchCardDet.getGross_wt() != null && cargoManifest.getMft_hscode_seq_nbr() == null) {
							cell.setCellValue(Double.valueOf(hatchCardDet.getGross_wt()));
						}
						cell.setCellStyle(style);

						CellReference cellRef1 = new CellReference(r.getRowNum(), r.getLastCellNum() - 1);
						String wtIndex = CommonUtil.getColumnIndex(cellRef1.formatAsString());

						// gross vol
						cell = r.createCell(r.getLastCellNum());
						cell.setCellFormula("ROUND(IF(" + noOfPackagesIndex + index + ">0," + pkgIndex + index + "*("
								+ grossMtIndex + index + (isSplitBL ? "/L" : "/J") + index + "),0),2)");
						if (hatchCardDet != null && hatchCardDet.getGross_vol() != null && cargoManifest.getMft_hscode_seq_nbr() == null) {
							cell.setCellValue(Double.valueOf(hatchCardDet.getGross_vol()));
						}
						cell.setCellStyle(style);
						CellReference cellRef2 = new CellReference(r.getRowNum(), r.getLastCellNum() - 1);
						String mtIndex = CommonUtil.getColumnIndex(cellRef2.formatAsString());

						/*
						 * 
						 * // no of package if (manifestHatchDetails!=null &&
						 * manifestHatchDetails.size() > 0) { if (cargoManifest.getMft_seq_nbr() != null
						 * && cargoManifest.getMft_seq_nbr()
						 * .equals(manifestHatchDetails.get(mhd_i).getMft_seq_nbr())) { cell =
						 * r.createCell(r.getLastCellNum());
						 * cell.setCellValue(Integer.parseInt(manifestHatchDetails.get(mhd_i).
						 * getNbr_pkgs())); cell.setCellStyle(style); } else { cell =
						 * r.createCell(r.getLastCellNum()); cell.setCellValue(0);
						 * cell.setCellStyle(style); } } else { cell = r.createCell(r.getLastCellNum());
						 * cell.setCellValue(0); cell.setCellStyle(style); } CellReference cellRef = new
						 * CellReference(r.getRowNum(), r.getLastCellNum() - 1); String pkgIndex =
						 * CommonUtil.getColumnIndex(cellRef.formatAsString()); // gross wt if
						 * (manifestHatchDetails.size() > 0) { if (cargoManifest.getMft_seq_nbr() !=
						 * null && cargoManifest.getMft_seq_nbr()
						 * .equals(manifestHatchDetails.get(mhd_i).getMft_seq_nbr())) { cell =
						 * r.createCell(r.getLastCellNum()); cell.setCellFormula("IF(" +
						 * noOfPackagesIndex + index + ">0," + pkgIndex + index + "*(" + grossWtIndex +
						 * index + "/H" + index + "),0)");
						 * cell.setCellValue(manifestHatchDetails.get(mhd_i).getGross_wt());
						 * cell.setCellStyle(style); } else { cell = r.createCell(r.getLastCellNum());
						 * cell.setCellFormula("IF(" + noOfPackagesIndex + index + ">0," + pkgIndex +
						 * index + "*(" + grossWtIndex + index + "/H" + index + "),0)");
						 * cell.setCellStyle(style); } } else { cell = r.createCell(r.getLastCellNum());
						 * cell.setCellFormula("IF(" + noOfPackagesIndex + index + ">0," + pkgIndex +
						 * index + "*(" + grossWtIndex + index + "/H" + index + "),0)");
						 * cell.setCellStyle(style);
						 * 
						 * } CellReference cellRef1 = new CellReference(r.getRowNum(),
						 * r.getLastCellNum() - 1); String wtIndex =
						 * CommonUtil.getColumnIndex(cellRef1.formatAsString());
						 * 
						 * // gross vol if (manifestHatchDetails.size() > 0) { if
						 * (cargoManifest.getMft_seq_nbr() != null && cargoManifest.getMft_seq_nbr()
						 * .equals(manifestHatchDetails.get(mhd_i).getMft_seq_nbr())) { cell =
						 * r.createCell(r.getLastCellNum()); cell.setCellFormula("IF(" +
						 * noOfPackagesIndex + index + ">0," + pkgIndex + index + "*(" + grossMtIndex +
						 * index + "/H" + index + "),0)");
						 * cell.setCellValue(manifestHatchDetails.get(mhd_i).getGross_vol());
						 * 
						 * } else { cell = r.createCell(r.getLastCellNum()); cell.setCellFormula("IF(" +
						 * noOfPackagesIndex + index + ">0," + pkgIndex + index + "*(" + grossMtIndex +
						 * index + "/H" + index + "),0)"); cell.setCellStyle(style); } } else { cell =
						 * r.createCell(r.getLastCellNum()); cell.setCellFormula("IF(" +
						 * noOfPackagesIndex + index + ">0," + pkgIndex + index + "*(" + grossMtIndex +
						 * index + "/H" + index + "),0)"); cell.setCellStyle(style); } CellReference
						 * cellRef2 = new CellReference(r.getRowNum(), r.getLastCellNum() - 1); String
						 * mtIndex = CommonUtil.getColumnIndex(cellRef2.formatAsString());
						 */
						noHatchPkgFormula = noHatchPkgFormula + pkgIndex + index + ",";
						noHatchGrossWtFormula = noHatchGrossWtFormula + wtIndex + index + ",";
						noHatchGrossMtFormula = noHatchGrossMtFormula + mtIndex + index + ",";
						/*
						 * if (mhd_i < manifestHatchDetails.size() - 1 && cargoManifest.getMft_seq_nbr()
						 * != null && cargoManifest.getMft_seq_nbr()
						 * .equals(manifestHatchDetails.get(mhd_i).getMft_seq_nbr())) { mhd_i++; }
						 */
					}

					// chop - removes last comma
					noHatchFormulaList.add(StringUtils.chop(noHatchPkgFormula));
					noHatchFormulaList.add(StringUtils.chop(noHatchGrossWtFormula));
					noHatchFormulaList.add(StringUtils.chop(noHatchGrossMtFormula));
				}
				// NO HATCH
				if (noOfHatch > 0) {
					int index = r.getRowNum() + 1;
					Cell cell1 = r.createCell(r.getLastCellNum());
					if (noHatchFormulaList.size() > 0) {
						cell1.setCellFormula("ROUND(" + noOfPackagesIndex + index + "-SUM(" + noHatchFormulaList.get(0)
								+ ")" + ",2)");
					} else {
						cell1.setCellValue(0);
					}
					cell1.setCellStyle(style);
					Cell cell2 = r.createCell(r.getLastCellNum());
					if (noHatchFormulaList.size() > 0) {
						cell2.setCellFormula(
								"ROUND(" + grossWtIndex + index + "-SUM(" + noHatchFormulaList.get(1) + ")" + ",2)");
					} else {
						cell2.setCellValue(0);
					}
					cell2.setCellStyle(style);
					Cell cell3 = r.createCell(r.getLastCellNum());
					if (noHatchFormulaList.size() > 0) {
						cell3.setCellFormula(
								"ROUND(" + grossMtIndex + index + "-SUM(" + noHatchFormulaList.get(2) + ")" + ",2)");
					} else {
						cell3.setCellValue(0);
					}
					cell3.setCellStyle(style);
				}
			}
			List<String> portList = manifestRepo.getPortListForExcelProcessing(false);
			List<String> portListWithName = manifestRepo.getPortListForExcelProcessing(true);
			List<String> hsCodeSubCode = manifestRepo.getHs_code_sub_code();
			// dropdown for action
			for (ManifestUploadConfig manifestUploadConfig : template) {

				if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.action)) {
					String[] actionArr = { ConstantUtil.action_NA, ConstantUtil.action_add, ConstantUtil.action_update,
							ConstantUtil.action_delete, ConstantUtil.action_addHS, ConstantUtil.action_updateHS,
							ConstantUtil.action_deleteHS, }; // ADD ADDITIONAL OPTION FOR ACTION - NS JUNE 2024
					setDropDownForColumns(sheet, manifestUploadConfig.getColumn_nm(), actionArr, limit);
				} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.cargo_type)) {
					List<String> cargoType = manifestRepo.getCargoTypeDropDown();
					String[] cargoTypeArr = new String[cargoType.size()];
					cargoTypeArr = cargoType.toArray(cargoTypeArr);
					setDropDownForColumns(sheet, manifestUploadConfig.getColumn_nm(), cargoTypeArr, limit);

				} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.hs_code)) {

					// write header in hidden sheet
					int col_index = CommonUtil.getColumnNumber(manifestUploadConfig.getColumn_nm());
					Row row_hsHeader = sheet_hidden.getRow(ConstantUtil.sheet_hidden_Header);
					Cell cell_Header = row_hsHeader.createCell(0);
					cell_Header.setCellValue(manifestUploadConfig.getAttr_desc());
					cell_Header.setCellStyle(style_header);
					// sheet_hidden.autoSizeColumn(0);

					// write into the hidden sheet
					int i = 6;
					for (String hsCode : hsCodeSubCode) {
						Row row = null;
						if (sheet_hidden.getRow(i) == null) {
							row = sheet_hidden.createRow(i);
						} else {
							row = sheet_hidden.getRow(i);
						}
						cell = row.createCell(0);
						cell.setCellValue(hsCode);
						i = i + 1;

					}

					Name namedCell = sheet.getWorkbook().createName();
					namedCell.setNameName("Reference");
					int hsCodeSubCodeSize = hsCodeSubCode.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$A$7:$A$" + hsCodeSubCodeSize);
					XSSFDataValidationHelper helper1 = (XSSFDataValidationHelper) sheet.getDataValidationHelper();
					XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) helper1
							.createFormulaListConstraint("Reference");
					XSSFDataValidation validation1 = (XSSFDataValidation) helper1.createValidation(constraint1,
							new CellRangeAddressList(ConstantUtil.row_start, limit, col_index, col_index));
					validation1.setSuppressDropDownArrow(true);
					validation1.setShowErrorBox(true);
					sheet.addValidationData(validation1);
				} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.cargo_selection)) {
					List<MiscDetail> cargoSelectionValues = manifestRepo.getCargoSelectionDropdown();
					CellReference refc = new CellReference(manifestUploadConfig.getColumn_nm());
					Row row_hsHeader = sheet_hidden.createRow(ConstantUtil.sheet_hidden_Header);
					Cell cell_Header = row_hsHeader.createCell(5);
					cell_Header.setCellValue(manifestUploadConfig.getAttr_desc());
					cell_Header.setCellStyle(style_header);
					// sheet_hidden.autoSizeColumn(4);

					int i = 6;
					for (MiscDetail value : cargoSelectionValues) {
						Row row = null;
						if (sheet_hidden.getRow(i) == null) {
							row = sheet_hidden.createRow(i);
						} else {
							row = sheet_hidden.getRow(i);
						}
						cell = row.createCell(5);
						cell.setCellValue(value.getTypeValue());
						i = i + 1;

					}

					Name namedCell = sheet.getWorkbook().createName();
					namedCell.setNameName("Reference6");
					int cargoSelectionValuesSize = cargoSelectionValues.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$F$7:$F$" +cargoSelectionValuesSize);

					XSSFDataValidationHelper helper1 = (XSSFDataValidationHelper) sheet.getDataValidationHelper();
					XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) helper1
							.createFormulaListConstraint("Reference6");
					XSSFDataValidation validation1 = (XSSFDataValidation) helper1.createValidation(constraint1,
							new CellRangeAddressList(ConstantUtil.row_start, limit, refc.getCol(),
									refc.getCol()));
					validation1.setSuppressDropDownArrow(true);
					validation1.setShowErrorBox(true);
					sheet.addValidationData(validation1);
				} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.cargo_status)) {
					String[] cargoStatusvalues = { ConstantUtil.cargo_status_local,
							ConstantUtil.cargo_status_transhipment };
					setDropDownForColumns(sheet, manifestUploadConfig.getColumn_nm(), cargoStatusvalues, limit);
				} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.dg_indicator)) {
					String[] dgivalues = { ConstantUtil.yes, ConstantUtil.no };
					setDropDownForColumns(sheet, manifestUploadConfig.getColumn_nm(), dgivalues, limit);
				} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.storage_indicator)) {
					String[] sivalues = { ConstantUtil.storage_ind_open, ConstantUtil.storage_ind_covered };
					setDropDownForColumns(sheet, manifestUploadConfig.getColumn_nm(), sivalues, limit);
				} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.packing_type)) {
					List<String> packagingType = manifestRepo.getPackagingTypeDropDown();
					CellReference refpk = new CellReference(template.get(isSplitBL? 19 : 17).getColumn_nm());

					Row row_hsHeader = sheet_hidden.getRow(ConstantUtil.sheet_hidden_Header);
					Cell cell_Header = row_hsHeader.createCell(1);
					cell_Header.setCellValue(manifestUploadConfig.getAttr_desc());
					cell_Header.setCellStyle(style_header);
					// sheet_hidden.autoSizeColumn(1);

					int i = 6;
					for (String value : packagingType) {
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
					namedCell.setNameName("Reference1");
					int packagingTypeSize = packagingType.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$B$7:$B$" + packagingTypeSize);

					XSSFDataValidationHelper helper1 = (XSSFDataValidationHelper) sheet.getDataValidationHelper();
					XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) helper1
							.createFormulaListConstraint("Reference1");
					XSSFDataValidation validation1 = (XSSFDataValidation) helper1.createValidation(constraint1,
							new CellRangeAddressList(ConstantUtil.row_start, limit, refpk.getCol(),
									refpk.getCol()));
					validation1.setSuppressDropDownArrow(true);
					validation1.setShowErrorBox(true);
					sheet.addValidationData(validation1);
				} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.discharge_operation_indicator)) {
					List<String> disOpTypes = manifestRepo.getDischargeTypeIndicatorDropdown();
					String[] disTypeArr = new String[disOpTypes.size()];
					disOpTypes.forEach(disOpType -> {
						if (disOpType != null && disOpType.equalsIgnoreCase(ConstantUtil.dis_op_ind_normal_status)) {
							disTypeArr[0] = ConstantUtil.dis_op_ind_normal;
						} else if (disOpType != null
								&& disOpType.equalsIgnoreCase(ConstantUtil.dis_op_ind_direct_status)) {
							disTypeArr[1] = ConstantUtil.dis_op_ind_direct;
						} else if (disOpType != null
								&& disOpType.equalsIgnoreCase(ConstantUtil.dis_op_ind_overside_status)) {
							disTypeArr[2] = ConstantUtil.dis_op_ind_overside;
						}
					});
					setDropDownForColumns(sheet, manifestUploadConfig.getColumn_nm(), disTypeArr, limit);
				} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.consignee)) {
					List<String> consignee = manifestRepo.getConsigneee();
					consignee.add(ConstantUtil.dropdown_others);
					CellReference refc = new CellReference(manifestUploadConfig.getColumn_nm());

					Row row_hsHeader = sheet_hidden.getRow(ConstantUtil.sheet_hidden_Header);
					Cell cell_Header = row_hsHeader.createCell(2);
					cell_Header.setCellValue(manifestUploadConfig.getAttr_desc());
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
					namedCell.setNameName("Reference2");
					int consigneeSize = consignee.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$C$7:$C$" + consigneeSize);

					XSSFDataValidationHelper helper1 = (XSSFDataValidationHelper) sheet.getDataValidationHelper();
					XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) helper1
							.createFormulaListConstraint("Reference2");
					XSSFDataValidation validation1 = (XSSFDataValidation) helper1.createValidation(constraint1,
							new CellRangeAddressList(ConstantUtil.row_start, limit, refc.getCol(),
									refc.getCol()));
					validation1.setEmptyCellAllowed(false);
					validation1.setSuppressDropDownArrow(true);
					validation1.setShowErrorBox(true);
					sheet.addValidationData(validation1);
				} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.port_of_loading)) {
					CellReference refld = new CellReference(manifestUploadConfig.getColumn_nm());

					Row row_hsHeader = sheet_hidden.getRow(ConstantUtil.sheet_hidden_Header);
					Cell cell_Header = row_hsHeader.createCell(3);
					cell_Header.setCellValue(manifestUploadConfig.getAttr_desc());
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
						cell = row.createCell(3);
						cell.setCellValue(value);
						i = i + 1;

					}
					
					row_hsHeader = sheet_hidden.getRow(ConstantUtil.sheet_hidden_Header);
					cell_Header = row_hsHeader.createCell(4);
					cell_Header.setCellValue(manifestUploadConfig.getAttr_desc() + " (Name Ref)");
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
						cell = row.createCell(4);
						cell.setCellValue(value);
						i = i + 1;

					}

					Name namedCell = sheet.getWorkbook().createName();
					namedCell.setNameName("Reference3");
					int portListSize = portList.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$D$7:$D$" + portListSize);

					XSSFDataValidationHelper helper1 = (XSSFDataValidationHelper) sheet.getDataValidationHelper();
					XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) helper1
							.createFormulaListConstraint("Reference3");
					XSSFDataValidation validation1 = (XSSFDataValidation) helper1.createValidation(constraint1,
							new CellRangeAddressList(ConstantUtil.row_start, limit, refld.getCol(),
									refld.getCol()));
					validation1.setSuppressDropDownArrow(true);
					validation1.setShowErrorBox(true);
					sheet.addValidationData(validation1);
				} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.port_of_discharge)) {

					CellReference refdis = new CellReference(manifestUploadConfig.getColumn_nm());
					Name namedCell = sheet.getWorkbook().createName();
					namedCell.setNameName("Reference4");
					int portListSize = portList.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$D$7:$D$" + portListSize);

					XSSFDataValidationHelper helper1 = (XSSFDataValidationHelper) sheet.getDataValidationHelper();
					XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) helper1
							.createFormulaListConstraint("Reference4");
					XSSFDataValidation validation1 = (XSSFDataValidation) helper1.createValidation(constraint1,
							new CellRangeAddressList(ConstantUtil.row_start, limit, refdis.getCol(),
									refdis.getCol()));
					validation1.setSuppressDropDownArrow(true);
					validation1.setShowErrorBox(true);
					sheet.addValidationData(validation1);
				} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.port_of_final_destination)) {
					CellReference refdes = new CellReference(manifestUploadConfig.getColumn_nm());
					Name namedCell = sheet.getWorkbook().createName();
					namedCell.setNameName("Reference5");
					int portListSize = portList.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$D$7:$D$" + portListSize);
					XSSFDataValidationHelper helper1 = (XSSFDataValidationHelper) sheet.getDataValidationHelper();
					XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) helper1
							.createFormulaListConstraint("Reference5");
					XSSFDataValidation validation1 = (XSSFDataValidation) helper1.createValidation(constraint1,
							new CellRangeAddressList(ConstantUtil.row_start, limit, refdes.getCol(),
									refdes.getCol()));
					validation1.setSuppressDropDownArrow(true);
					validation1.setShowErrorBox(true);
					sheet.addValidationData(validation1);
				} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.old_hscode)) {
					CellReference refdes = new CellReference(manifestUploadConfig.getColumn_nm());
					Name namedCell = sheet.getWorkbook().createName();
					namedCell.setNameName("Reference7");
					int hsCodeSubCodeSize = hsCodeSubCode.size() + ConstantUtil.sheet_hidden_rec_start;// header row
					namedCell.setRefersToFormula("Reference!$A$7:$A$" + hsCodeSubCodeSize);
					XSSFDataValidationHelper helper1 = (XSSFDataValidationHelper) sheet.getDataValidationHelper();
					XSSFDataValidationConstraint constraint1 = (XSSFDataValidationConstraint) helper1
							.createFormulaListConstraint("Reference");
					XSSFDataValidation validation1 = (XSSFDataValidation) helper1.createValidation(constraint1,
							new CellRangeAddressList(ConstantUtil.row_start, limit, refdes.getCol(), refdes.getCol()));
					validation1.setSuppressDropDownArrow(true);
					validation1.setShowErrorBox(true);
					sheet.addValidationData(validation1);
				} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.split_bl_ind)) {
					String[] IndArr = { ConstantUtil.STRING_VALUE_NO, ConstantUtil.STRING_VALUE_YES}; 
					setDropDownForColumns(sheet, manifestUploadConfig.getColumn_nm(), IndArr, limit);
				}
			}
			 sheet_hidden.autoSizeColumn(0);
			 sheet_hidden.autoSizeColumn(1);
			 sheet_hidden.autoSizeColumn(2);
			 sheet_hidden.autoSizeColumn(3);
			 sheet_hidden.autoSizeColumn(4);
			 sheet_hidden.autoSizeColumn(5);
			// wb.cloneSheet(1, "Reference");
		} catch (BusinessException be) {
			log.info("Exception manifestDetailExcelDownload : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception manifestDetailExcelDownload : ", e);
			throw new BusinessException("M4201");
		}
		return wb;
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

	private Cell createCellvalues(XSSFSheet sheet, Row header_r, String column_nm, String attr_desc,
			CellStyle style_header) {
		int columnIndex = CommonUtil.getColumnNumber(column_nm);
		Cell cell = header_r.createCell(columnIndex);
		if(attr_desc == null || attr_desc.equals("")) {
			cell.setCellValue(attr_desc);
		} else {
			attr_desc = Jsoup.parse(attr_desc).text();
			cell.setCellValue(attr_desc);
		}
		cell.setCellStyle(style_header);
		sheet.autoSizeColumn(columnIndex);
		return cell;
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public boolean insertActionTrial(String vvCd, String typeCd, Summary summary, String lastTimestamp, String userId) throws BusinessException {
		String summaryDet = ConstantUtil.total_line_processed + summary.getTotalLineItemProcessed()
				+ ConstantUtil.total_success + summary.getTotalSuccess() + ConstantUtil.total_fail
				+ summary.getTotalFail();
		return manifestRepo.insertManifest_action_trial(vvCd, typeCd, summaryDet, lastTimestamp, userId);
	}

	@Override
	public TableResult getManifestActionTrail(Criteria criteria) throws BusinessException {
		TableResult hatch_act_trls = null;
		try {
			log.info("START getManifestActionTrail criteria:" + criteria.toString() );
			hatch_act_trls = manifestRepo.getManifestActionTrial(criteria);
			
			log.info("END: *** getManifestActionTrail Result *****" + hatch_act_trls.getData().getTotal());

		} catch (BusinessException be) {
			log.info("Exception getManifestActionTrail : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception getManifestActionTrail : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getManifestActionTrail");
		}
		return hatch_act_trls;
	}

	@Override
	public String getTimeStamp() throws BusinessException {
		return manifestRepo.getTimeStamp();
	}

	@Override
	public ManifestActionTrailDetails manifestActionTrailDetail(String mft_act_trl_id) throws BusinessException {
		return manifestRepo.getManifestActionTrialDetail(mft_act_trl_id);
	}

	private boolean removeColorsAndComment(String ref, XSSFSheet sheet, CellStyle no_style) {
		try {
			log.info("START removeColorsAndComment" + " ref:"+CommonUtility.deNull(ref) );
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

	public Boolean isManifestSubmissionAllowed(Criteria criteria) throws BusinessException {
		return manifestRepo.isManifestSubmissionAllowed(criteria);
	}

	// Region HatchBD

	@Override
	public HatchBreakDownPageDetail getHatchBreakDownDetails(Criteria criteria) throws BusinessException {
		HatchBreakDownPageDetail hatchBrkDwnResp = null;
		try {
			log.info("START:getHatchBreakDownDetails service criteria:" + criteria.toString());
			List<ManifestDetails> manifestDetails = manifestRepo.getManifestDetails(criteria);

			// Main leavel details
			hatchBrkDwnResp = new HatchBreakDownPageDetail();

			// Hatch Card Details
			List<HatchBreakDownPageDetail.HatchCardDetail> hatchCardInfo = new ArrayList<HatchBreakDownPageDetail.HatchCardDetail>();
			HatchBreakDownPageDetail.HatchCardDetail hatchCardObj = null;
			List<HatchBreakDownPageDetail.CardDetail> cardDetailInfo = null;
			HatchBreakDownPageDetail.CardDetail cardDetailObj = null;

			// hatch details
			List<HatchBreakDownPageDetail.HatchDetail> hatchDetailInfo = new ArrayList<HatchBreakDownPageDetail.HatchDetail>();
			HatchBreakDownPageDetail.HatchDetail hatchDetailObj = null;
			List<HatchBreakDownPageDetail.HatchInfo> hatchInfo = null;
			HatchBreakDownPageDetail.HatchInfo hatchInfoObj = null;

			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			int noOfHatch = manifestRepo.noOfHatchesOnPageLoad(vvCd);

			List<HatchDetails> manifestHatchDetails = manifestRepo.getManifestHatchDetails(vvCd);

			for (ManifestDetails manifestDetObj : manifestDetails) {
				hatchBrkDwnResp.setVvCd(manifestDetObj.getVvCd());
				hatchBrkDwnResp.setRemarks(manifestDetObj.getRemarks());
				hatchBrkDwnResp.setVesselName(manifestDetObj.getVesselName());
				hatchBrkDwnResp.setVoyageNo(manifestDetObj.getVoyageNo());
				hatchBrkDwnResp.setNbrOfHatch(Integer.toString(noOfHatch));
				hatchInfo = new ArrayList<HatchBreakDownPageDetail.HatchInfo>();
				hatchDetailObj = new HatchBreakDownPageDetail.HatchDetail();
				hatchDetailObj.setBillNo(manifestDetObj.getBillNo());
				hatchDetailObj.setMftSeqNbr(manifestDetObj.getMftSeq());
				hatchDetailObj.setHsCode(manifestDetObj.getHsCode());
				hatchDetailObj.setCargoDesc(manifestDetObj.getCargoDes());
				hatchDetailObj.setNumOfPackages(manifestDetObj.getNbrPkgs());
				hatchDetailObj.setVolume(manifestDetObj.getGrossVol());
				hatchDetailObj.setWeight(manifestDetObj.getGrossWt());
				for (int i = noOfHatch; i >=1; i--) {
					int hatchNbr = i;
					String hatchCd = ConstantUtil.hatchCode + Integer.toString(hatchNbr);
					hatchInfoObj = new HatchBreakDownPageDetail.HatchInfo();
					hatchInfoObj.setHatchNo(Integer.toString(hatchNbr));
					for (HatchDetails hatchCardDet : manifestHatchDetails.stream()
							.filter(x -> x.getMft_seq_nbr().equalsIgnoreCase(manifestDetObj.getMftSeq())
									&& x.getHatch_cd().equalsIgnoreCase(hatchCd))
							.collect(Collectors.toList())) {

						if (hatchCardDet.getHatch_cd().equalsIgnoreCase(hatchCd)) {
							hatchInfoObj.setPackages(hatchCardDet.getNbr_pkgs());
							hatchInfoObj.setVolume(hatchCardDet.getGross_vol());
							hatchInfoObj.setWeight(hatchCardDet.getGross_wt());
							hatchInfoObj.setMftHatchSeqNbr(hatchCardDet.getMft_hatch_seq_nbr().toString());

						} else {
							hatchInfoObj = new HatchBreakDownPageDetail.HatchInfo();
							hatchInfoObj.setHatchNo(Integer.toString(hatchNbr));
							hatchInfoObj.setPackages("0");
							hatchInfoObj.setVolume("0");
							hatchInfoObj.setWeight("0");
							hatchInfoObj.setMftHatchSeqNbr("0");
						}
					}
					hatchInfo.add(hatchInfoObj);
				}
				HatchBreakDownPageDetail.HatchInfo noHatchInfo1 = new HatchBreakDownPageDetail.HatchInfo();
				noHatchInfo1.setHatchNo(ConstantUtil.noHatchCode);
				noHatchInfo1.setPackages("0");
				noHatchInfo1.setVolume("0");
				noHatchInfo1.setWeight("0");
				hatchInfo.add(noHatchInfo1);
				hatchDetailObj.setHatchInfo(hatchInfo);
				hatchDetailInfo.add(hatchDetailObj);
			}

			hatchCardInfo = new ArrayList<HatchBreakDownPageDetail.HatchCardDetail>();
			for (int i = noOfHatch; i >= 1; i--) {
				hatchCardObj = new HatchBreakDownPageDetail.HatchCardDetail();
				Integer hatNbr = i;
				String hatchCd = ConstantUtil.hatchCode + Integer.toString(hatNbr);
				hatchCardObj.setHatchNo(hatNbr.toString());
				cardDetailInfo = new ArrayList<HatchBreakDownPageDetail.CardDetail>();
				for (HatchDetails hatchCardDet : manifestHatchDetails.stream()
						.filter(x -> x.getHatch_cd().equalsIgnoreCase(hatchCd)).collect(Collectors.toList())) {
					if (hatchCardDet.getNbr_pkgs() != null && !hatchCardDet.getNbr_pkgs().isEmpty()
							&& Integer.parseInt(hatchCardDet.getNbr_pkgs()) > 0) {
						cardDetailObj = new HatchBreakDownPageDetail.CardDetail();
						cardDetailObj.setBillNo(hatchCardDet.getBillNo());
						cardDetailObj.setCargoDesc(hatchCardDet.getCargoDesc());
						cardDetailObj.setPackages(hatchCardDet.getNbr_pkgs());
						cardDetailObj.setVolume(hatchCardDet.getGross_vol());
						cardDetailObj.setWeight(hatchCardDet.getGross_wt());
						cardDetailInfo.add(cardDetailObj);
					}
				}
				hatchCardObj.setCardDetail(cardDetailInfo);
				hatchCardInfo.add(hatchCardObj);
			}
			hatchBrkDwnResp.setHatchCardInfo(hatchCardInfo);
			hatchBrkDwnResp.setHatchDetail(hatchDetailInfo);

			hatchBrkDwnResp.setIsSubmissionAllowed(isManifestSubmissionAllowed(criteria));

			log.info("hatchbrkOut Output Response" + hatchBrkDwnResp.toString());
		} catch (BusinessException be) {
			log.info("Exception getHatchBreakDownDetails : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception getHatchBreakDownDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getHatchBreakDownDetails service");
		}
		return hatchBrkDwnResp;

	}

	@Transactional(rollbackFor = BusinessException.class)
	public boolean saveManifestHatchDetails(HatchWisePackageDetail saveManifestHatchDet) throws BusinessException {
		return manifestRepo.saveManifestHatchDetails(saveManifestHatchDet);
	}

	@Override
	public HatchBreakDownPageDetail getHatchBreakDownHistoryDetail(Criteria criteria) throws BusinessException {
		HatchBreakDownPageDetail hatchBrkDwnResp = null;
		try {
			log.info("getHatchBreakDownHistoryDetail service criteria:" + criteria.toString());
			List<ManifestDetails> manifestDetails = manifestRepo.getManifestHistoryDetailsForHBD(criteria);

			// Main leavel details
			hatchBrkDwnResp = new HatchBreakDownPageDetail();

			// Hatch Card Details
			List<HatchBreakDownPageDetail.HatchCardDetail> hatchCardInfo = new ArrayList<HatchBreakDownPageDetail.HatchCardDetail>();
			HatchBreakDownPageDetail.HatchCardDetail hatchCardObj = null;
			List<HatchBreakDownPageDetail.CardDetail> cardDetailInfo = null;
			HatchBreakDownPageDetail.CardDetail cardDetailObj = null;

			// hatch details
			List<HatchBreakDownPageDetail.HatchDetail> hatchDetailInfo = new ArrayList<HatchBreakDownPageDetail.HatchDetail>();
			HatchBreakDownPageDetail.HatchDetail hatchDetailObj = null;
			List<HatchBreakDownPageDetail.HatchInfo> hatchInfo = null;
			HatchBreakDownPageDetail.HatchInfo hatchInfoObj = null;

			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			int noOfHatch = manifestRepo.noOfHatchesOnPageLoad(vvCd);

			List<HatchDetails> manifestHatchDetails = manifestRepo.getManifestHatchBDHistoryDetails(criteria);

			for (ManifestDetails manifestDetObj : manifestDetails) {
				hatchBrkDwnResp.setVvCd(vvCd);
				hatchBrkDwnResp.setRemarks(manifestDetObj.getRemarks());
				// hatchBrkDwnResp.setVesselName(manifestDetObj.getVesselName());
				// hatchBrkDwnResp.setVoyageNo(manifestDetObj.getVoyageNo());
				hatchBrkDwnResp.setNbrOfHatch(Integer.toString(noOfHatch));
				hatchInfo = new ArrayList<HatchBreakDownPageDetail.HatchInfo>();
				hatchDetailObj = new HatchBreakDownPageDetail.HatchDetail();
				hatchDetailObj.setBillNo(manifestDetObj.getBillNo());
				hatchDetailObj.setMftSeqNbr(manifestDetObj.getMftSeq());
				hatchDetailObj.setHsCode(manifestDetObj.getHsCode());
				hatchDetailObj.setCargoDesc(manifestDetObj.getCargoDes());
				hatchDetailObj.setNumOfPackages(manifestDetObj.getNbrPkgs());
				hatchDetailObj.setVolume(manifestDetObj.getGrossVol());
				hatchDetailObj.setWeight(manifestDetObj.getGrossWt());
				for (int i = noOfHatch; i >=1; i--) {
					int hatchNbr = i;
					String hatchCd = ConstantUtil.hatchCode + Integer.toString(hatchNbr);
					hatchInfoObj = new HatchBreakDownPageDetail.HatchInfo();
					hatchInfoObj.setHatchNo(Integer.toString(hatchNbr));
					for (HatchDetails hatchCardDet : manifestHatchDetails.stream()
							.filter(x -> x.getMft_seq_nbr().equalsIgnoreCase(manifestDetObj.getMftSeq())
									&& x.getHatch_cd().equalsIgnoreCase(hatchCd))
							.collect(Collectors.toList())) {

						if (hatchCardDet.getHatch_cd().equalsIgnoreCase(hatchCd)) {
							hatchInfoObj.setPackages(hatchCardDet.getNbr_pkgs());
							hatchInfoObj.setVolume(hatchCardDet.getGross_vol());
							hatchInfoObj.setWeight(hatchCardDet.getGross_wt());
							hatchInfoObj.setMftHatchSeqNbr(hatchCardDet.getMft_hatch_seq_nbr().toString());

						} else {
							hatchInfoObj = new HatchBreakDownPageDetail.HatchInfo();
							hatchInfoObj.setHatchNo(Integer.toString(hatchNbr));
							hatchInfoObj.setPackages("N/A");
							hatchInfoObj.setVolume("N/A");
							hatchInfoObj.setWeight("N/A");
							hatchInfoObj.setMftHatchSeqNbr("N/A");
						}
					}
					hatchInfo.add(hatchInfoObj);
				}
				HatchBreakDownPageDetail.HatchInfo noHatchInfo1 = new HatchBreakDownPageDetail.HatchInfo();
				noHatchInfo1.setHatchNo(ConstantUtil.noHatchCode);
				noHatchInfo1.setPackages("0");
				noHatchInfo1.setVolume("0");
				noHatchInfo1.setWeight("0");
				hatchInfo.add(noHatchInfo1);
				hatchDetailObj.setHatchInfo(hatchInfo);
				hatchDetailInfo.add(hatchDetailObj);
			}

			hatchCardInfo = new ArrayList<HatchBreakDownPageDetail.HatchCardDetail>();
			for (int i = noOfHatch; i >= 1; i--) {
				hatchCardObj = new HatchBreakDownPageDetail.HatchCardDetail();
				Integer hatNbr = i;
				String hatchCd = ConstantUtil.hatchCode + Integer.toString(hatNbr);
				hatchCardObj.setHatchNo(hatNbr.toString());
				cardDetailInfo = new ArrayList<HatchBreakDownPageDetail.CardDetail>();
				for (HatchDetails hatchCardDet : manifestHatchDetails.stream()
						.filter(x -> x.getHatch_cd().equalsIgnoreCase(hatchCd)).collect(Collectors.toList())) {
					cardDetailObj = new HatchBreakDownPageDetail.CardDetail();
					cardDetailObj.setBillNo(hatchCardDet.getBillNo());
					cardDetailObj.setCargoDesc(hatchCardDet.getCargoDesc());
					cardDetailObj.setPackages(hatchCardDet.getNbr_pkgs());
					cardDetailObj.setVolume(hatchCardDet.getGross_vol());
					cardDetailObj.setWeight(hatchCardDet.getGross_wt());
					cardDetailInfo.add(cardDetailObj);
				}
				hatchCardObj.setCardDetail(cardDetailInfo);
				hatchCardInfo.add(hatchCardObj);
			}
			hatchBrkDwnResp.setHatchCardInfo(hatchCardInfo);
			hatchBrkDwnResp.setHatchDetail(hatchDetailInfo);
			log.info("hatchbrkOut Output Response" + hatchBrkDwnResp.toString());
		} catch (BusinessException be) {
			log.info("Exception getHatchBreakDownHistoryDetail : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception getHatchBreakDownHistoryDetail : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getHatchBreakDownHistoryDetail service");
		}
		return hatchBrkDwnResp;

	}

	// EndRegion HatchBD

	// Region CargoDimensionDeclaration

	@Override
	public List<CargoDimensionDeclaration> getCargoDimensionDeclaration(Criteria criteria) throws BusinessException {

		List<CargoDimensionDeclaration> cargoDimensionDeclarationList = null;
		try {
			log.info("START getCargoDimensionDeclaration :" + criteria.toString());
			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd")).trim().toUpperCase();
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount")).trim().toUpperCase();

			cargoDimensionDeclarationList = manifestRepo.getCargoDimensionDeclarationInfo(vvCd, userId);

			if (cargoDimensionDeclarationList == null) {
				cargoDimensionDeclarationList = new ArrayList<CargoDimensionDeclaration>();
				List<SystemConfigList> systemConfigList = manifestRepo.getCargoDimensionDeclarationInfo(userId);
				List<CargoDimensionDeclaration> getHsCodeInfo = manifestRepo.getHsCodeInfo();
				for (SystemConfigList dimensionInfo : systemConfigList) {
					for (ConfigMsg configMsg : dimensionInfo.getRemarksDet()) {
						CargoDimensionDeclaration cargoDimensionDeclaraction = new CargoDimensionDeclaration();
						CargoDimensionDeclaration cargoDimensionDeclarationOptional = null;
						cargoDimensionDeclarationOptional = getHsCodeInfo.stream()
								.filter(x -> x.getHsCode().equalsIgnoreCase(configMsg.getHsCode())
										&& x.getHsSubCodeFrom().equalsIgnoreCase(configMsg.getHsSubCodeFrom())
										&& x.getHsSubCodeTo().equalsIgnoreCase(configMsg.getHsSubCodeTo()))
								.findFirst().orElse(null);

						if (cargoDimensionDeclarationOptional != null) {
							cargoDimensionDeclaraction.setHsSubDesc(cargoDimensionDeclarationOptional.getHsSubDesc());
						}
						cargoDimensionDeclaraction.setHsCode(configMsg.getHsCode());
						cargoDimensionDeclaraction.setHsSubCodeFrom(configMsg.getHsSubCodeFrom());
						cargoDimensionDeclaraction.setHsSubCodeTo(configMsg.getHsSubCodeTo());
						cargoDimensionDeclaraction.setQuestion(configMsg.getQuestion());
						cargoDimensionDeclaraction.setOptions(configMsg.getOptions());
						cargoDimensionDeclaraction.setAnswer("");
						cargoDimensionDeclaraction.setInputTime(dimensionInfo.getInputTime());
						cargoDimensionDeclaraction.setUserId(dimensionInfo.getUserId());
						cargoDimensionDeclaraction.setUserName(dimensionInfo.getUserName());
						cargoDimensionDeclaraction.setDimDeclarSeqNum(0);
						cargoDimensionDeclarationList.add(cargoDimensionDeclaraction);

					}
				}
			}
		} catch (BusinessException be) {
			log.info("Exception getCargoDimensionDeclaration : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception getCargoDimensionDeclaration : ", e);
			throw new BusinessException("M4201");
		}
		return cargoDimensionDeclarationList;
	}

	@Override
	public Result saveCargoDimensionDeclaration(List<CargoDimensionDeclaration> obj,String userAcct) throws BusinessException {
		return manifestRepo.saveCargoDimensionDeclaration(obj,userAcct);
	}

	// EndRegion
	@Override
	public boolean sendAdminWaiverRequestToOscar(AdminFeeWaiverValueObject adminFeeWaiverVO) throws BusinessException {
		boolean adminWaiveJsonSent = false;
		String adminWaiverJsonString = new String();
		try {
			log.info("START:sendAdminWaiverRequestToOscar adminFeeWaiverVO:" + adminFeeWaiverVO.toString());
			String svrUrl = waiverUrl;
			log.info("To OSCAR:Micro.Service.OSCARADMINWAIVER.URI=" + svrUrl);
			if (StringUtils.isNotBlank(svrUrl)) {
				adminFeeWaiverVO.setCreateUserId(adminFeeWaiverVO.getCreateUserId());
			} else {
				log.info(
						"requestCredentialWebServiceUrl or adminFeeApprovalWebServiceUrl is not defined in sys.properties file, please check!");
			}
			if (adminFeeWaiverVO != null) {
				StringBuffer sb = new StringBuffer();
				sb.setLength(0);
				sb.append("waiverAdviceNo:" + adminFeeWaiverVO.getWanAdviceNbr());
				sb.append(";waiverCompany:" + adminFeeWaiverVO.getWaiverCompany());
				sb.append(";vslVoy:" + adminFeeWaiverVO.getVesselVoy());
				sb.append(";address:" + adminFeeWaiverVO.getCompanyAddress());
				sb.append(";vvcd:" + adminFeeWaiverVO.getVarCode());
				sb.append(";accountNbr:" + adminFeeWaiverVO.getCompanyAccount());
				sb.append(";atbetbbtr:" + adminFeeWaiverVO.getAtbEtbBtr());
				sb.append(";reqDate:" + adminFeeWaiverVO.getRequestedAt());
				sb.append(";reqBy :" + adminFeeWaiverVO.getCreateUserId());
				sb.append(";tariffDesc:" + adminFeeWaiverVO.getTariffDesc());
				sb.append(";unit:" + adminFeeWaiverVO.getUnitNbr());
				sb.append(";uintRate:" + adminFeeWaiverVO.getUnitRate());
				sb.append(";gst:" + adminFeeWaiverVO.getGst());
				sb.append(";waiveReason:" + adminFeeWaiverVO.getWaiverReasons());
				sb.append("\n");
				log.info("Admin waive Data to Jason=" + sb.toString());
				OscarJsonAdminWaiverVO oscarObj = new OscarJsonAdminWaiverVO(adminFeeWaiverVO);
				adminWaiverJsonString = oscarObj.toString();
				log.info("Final AdminWaiverJsonString=" + adminWaiverJsonString + "=END");
			}
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<String>(adminWaiverJsonString, headers);
			RestTemplate restTemplate = new RestTemplate();
			log.info("INPUT TO REST CALL:svrUrl" + svrUrl + "BODY" + entity.toString());
			ResponseEntity<Object> result = null;
			try {
				result = restTemplate.exchange(svrUrl, HttpMethod.POST, entity, Object.class);
			} catch (HttpClientErrorException e) {
				log.info("Exception sendAdminWaiverRequestToOscar : ", e);
				log.info("******* REST CALL EXCEPTION **********" + e.getMessage());
			}
			log.info("**POST Admin Waive** request Url: " + svrUrl + ";Result=" + result);
			int output = 1;
			if (HttpStatus.OK == result.getStatusCode()) { // 200
				output = 0;
			} else if (HttpStatus.NOT_FOUND == result.getStatusCode()) { // 404
				log.info("Admin Waiver OSCAR Service is unavailable=" + result);
			}
			log.info("Admin Waiver Json output=" + output);
			// Read the response body.
			if (output == 0) {
                //Commented By NS ON 27-01-21
                /*
                 * ObjectMapper objectMapper = new ObjectMapper(); OscarJsonResponseVO
                 * oscarResponse = objectMapper.readValue(result.getBody().toString(),
                 * OscarJsonResponseVO.class); String errorCode = oscarResponse.getError();
                 * String errorMessage = oscarResponse.getErrorMessage(); String id =
                 * oscarResponse.getId(); String url = oscarResponse.getUrl();
                 */
                //End commented
                //Added By NS on  27-01-21
                String errorCode="";
                String value = result.getBody().toString();
                value = value.substring(1, value.length() - 1);
                String[] keyValuePairs = value.split(",");
                try {
                    log.info("START: ***   INSIDE Response MAP");
                    for (String pair : keyValuePairs) {
                        log.info("pair"+pair);
                        String[] entry = pair.split("=");
                        if (entry[0].trim().equalsIgnoreCase("error")) {
                            errorCode = entry[1].trim();
                            break;
                        }
                       
                    }
                    log.info("END:INSIDE Response MAP****");
                } catch (Exception e) {
                	log.info("Exception sendAdminWaiverRequestToOscar : ", e);
                	throw new BusinessException("M4201");
                }
				if (errorCode != null && !errorCode.equals("") && Integer.parseInt(errorCode) != -1) {
					adminWaiveJsonSent = true;
				}
			}
		} catch (BusinessException ex) {
			log.info("Exception sendAdminWaiverRequestToOscar : ", ex);
			throw new BusinessException(ex.getMessage());
		} catch (Exception ex) {
			log.info("Exception sendAdminWaiverRequestToOscar : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: sendAdminWaiverRequestToOscar");
		}
		return adminWaiveJsonSent;
	}

	// EndRegion CDD

	// Region Cargodimension CD
	@Override
	public TableResult getCargoDimensionList(Criteria criteria) throws BusinessException {
		return manifestRepo.getCargoDimensionList(criteria);
	}

	@Override
	public List<ManifestPkgDimDetails> getCargoDimensionDetails(Criteria criteria) throws BusinessException {
		return manifestRepo.getCargoDimensionDetails(criteria);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public boolean saveCargoDimensionDetails(CargoDimensionDetails saveCargoDimensionDetails) throws BusinessException {
		return manifestRepo.saveCargoDimensionDetails(saveCargoDimensionDetails);
	}

	// Region Cargodimension Excel CDE

	@Override
	public XSSFWorkbook packagingDownload(String vvCd) throws BusinessException {
		XSSFWorkbook wb = new XSSFWorkbook();
		try {
			log.info("START packagingDownload :vvCd :" + CommonUtility.deNull(vvCd));
			XSSFSheet sheet = wb.createSheet(ConstantUtil.packaging_sheet_name);

			String version=manifestRepo.getTemplateVersionNo(ConstantUtil.packaging_type_cd);
			wb.getProperties().getCoreProperties().setRevision(version);
			
			Sheet sheet_hidden = wb.createSheet(ConstantUtil.sheet_name_hidden);
			wb.setSheetVisibility(1, SheetVisibility.HIDDEN);

			int headerRow = ConstantUtil.row_header;
			int startRow = ConstantUtil.row_start;

			// cargo template version
			try {
				log.info(" packagingDownload START for vvCd:" + CommonUtility.deNull(vvCd));
				Row r_sheet_hidden = sheet_hidden.createRow(ConstantUtil.sheet_hidden_template_row);
				Cell c1_sheet_hidden = r_sheet_hidden.createCell(0);
				Cell c2_sheet_hidden = r_sheet_hidden.createCell(1);
				c1_sheet_hidden.setCellValue(ConstantUtil.template_version);
				c2_sheet_hidden.setCellValue(ConstantUtil.template_version_no);
				c1_sheet_hidden.setCellStyle(CommonUtil.style_header(wb));
				c2_sheet_hidden.setCellStyle(CommonUtil.style(wb));
			} catch (Exception e) {
				log.info("Exception packagingDownload HiddenSheet creation : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info(" packagingDownload END for vvCd:" + CommonUtility.deNull(vvCd));
			}

			List<ManifestUploadConfig> packagingHeaders = manifestRepo.getPackagingTemplate();
			log.info("Packaging Header Template Values:" + packagingHeaders.size());
			List<PackageDimension> packageDimensionDataList = manifestRepo.getPackageDimensionDetails(vvCd);
			log.info("Packaging Dimension Datas:" + packageDimensionDataList.size());
			PageDetails vesselDetails = manifestRepo.getVesselCallDetails(vvCd);
			log.info("vesselDetails:vvCd:" + vesselDetails.getVvCd() + "VslName:" + vesselDetails.getVesselName()
					+ "InvoyNo:" + vesselDetails.getInwardVoyNo());

			String cell_pkg_dimension_header = null;

			// TO draw rest of the excel lines
			for (int k = packageDimensionDataList.size(); k <= ConstantUtil.limit; k++) {
				PackageDimension pd = new PackageDimension();
				packageDimensionDataList.add(pd);
			}

			// Write packagin template
			Row header_r = sheet.createRow(headerRow);
			Cell cell = null;
			try {
				log.info("Header Creation STARTS for vvCd: " + CommonUtility.deNull(vvCd));
				for (ManifestUploadConfig manifestUploadConfig_header : packagingHeaders) {
					if (manifestUploadConfig_header.getAttr_name().equalsIgnoreCase(ConstantUtil.pkg_vessel_name)) {
						Row vessel_r = sheet.createRow(0);
						cell = createCellvalues(sheet, vessel_r, manifestUploadConfig_header.getColumn_nm(),
								manifestUploadConfig_header.getAttr_desc(), CommonUtil.style_header(wb));
//						log.info(cell);
					} else if (manifestUploadConfig_header.getAttr_name()
							.equalsIgnoreCase(ConstantUtil.pkg_inward_voyage_no)) {
						Row invoy_r = sheet.createRow(1);
						cell = createCellvalues(sheet, invoy_r, manifestUploadConfig_header.getColumn_nm(),
								manifestUploadConfig_header.getAttr_desc(), CommonUtil.style_header(wb));
//						log.info(cell);
					} else if (manifestUploadConfig_header.getAttr_name()
							.equalsIgnoreCase(ConstantUtil.pkg_bills_of_landing_no)) {
						cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
								manifestUploadConfig_header.getAttr_desc(), CommonUtil.style_header(wb));
//						log.info(cell);
					} else if (manifestUploadConfig_header.getAttr_name()
							.equalsIgnoreCase(ConstantUtil.pkg_cargo_description)) {
						cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
								manifestUploadConfig_header.getAttr_desc(), CommonUtil.style_header(wb));
//						log.info(cell);
					} else if (manifestUploadConfig_header.getAttr_name()
							.equalsIgnoreCase(ConstantUtil.pkg_total_packages)) {
						cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
								manifestUploadConfig_header.getAttr_desc(), CommonUtil.style_header(wb));
//						log.info(cell);
					} else if (manifestUploadConfig_header.getAttr_name()
							.equalsIgnoreCase(ConstantUtil.pkg_weight_kg)) {
						cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
								manifestUploadConfig_header.getAttr_desc(), CommonUtil.style_header(wb));
//						log.info(cell);
					} else if (manifestUploadConfig_header.getAttr_name()
							.equalsIgnoreCase(ConstantUtil.pkg_number_of_packages)) {
						cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
								manifestUploadConfig_header.getAttr_desc(), CommonUtil.style_header(wb));
						cell_pkg_dimension_header = cell.getAddress().formatAsString();
					} else if (manifestUploadConfig_header.getAttr_name()
							.equalsIgnoreCase(ConstantUtil.pkg_total_package_weight_kg)) {
						cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
								manifestUploadConfig_header.getAttr_desc(), CommonUtil.style_header(wb));
//						log.info(cell);
					} else if (manifestUploadConfig_header.getAttr_name()
							.equalsIgnoreCase(ConstantUtil.pkg_length_mm)) {
						cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
								manifestUploadConfig_header.getAttr_desc(), CommonUtil.style_header(wb));
//						log.info(cell);
					} else if (manifestUploadConfig_header.getAttr_name()
							.equalsIgnoreCase(ConstantUtil.pkg_breadth_mm)) {
						cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
								manifestUploadConfig_header.getAttr_desc(), CommonUtil.style_header(wb));
//						log.info(cell);
					} else if (manifestUploadConfig_header.getAttr_name()
							.equalsIgnoreCase(ConstantUtil.pkg_height_thickness_diameter_mm)) {
						cell = createCellvalues(sheet, header_r, manifestUploadConfig_header.getColumn_nm(),
								manifestUploadConfig_header.getAttr_desc(), CommonUtil.style_header(wb));
//						log.info(cell);
					}
					
				}
				
			} catch (Exception e) {
				log.info("Exception packagingDownload : ", e);
			} finally {
				log.info("Header Creation  END for vvCd:" + vvCd);
			}

			// Package Dimension Header - merged column
			CellReference cell_pkg_dimension_header_ref = new CellReference(cell_pkg_dimension_header);
			Row header_r_pkg_dimension = sheet.createRow(cell_pkg_dimension_header_ref.getRow() - 1);
			cell = null;
			for (int i = 0; i <= 4; i++) {
				cell = header_r_pkg_dimension.createCell(cell_pkg_dimension_header_ref.getCol() + i);
				cell.setCellValue(ConstantUtil.pkg_packaging_dimension_header);
				cell.setCellStyle(CommonUtil.borderStyle(wb));
			}
			sheet.addMergedRegion(new CellRangeAddress(cell_pkg_dimension_header_ref.getRow() - 1,
					cell_pkg_dimension_header_ref.getRow() - 1, cell_pkg_dimension_header_ref.getCol(),
					cell_pkg_dimension_header_ref.getCol() + 4));

			// write package dimension records in Excel
			try {
				log.info("Write packageDimension records in Excel STARTS for vvCd: " + vvCd);
				
				for (PackageDimension packageDimensionData : packageDimensionDataList.stream()
						.filter(x -> CommonUtil.deNull(x.getNbr_of_pkg())!="" && x.getNbr_of_pkg()!="0").collect(Collectors.toList())) {
					Row r = sheet.createRow(startRow++);
					Cell cell_pkg_dim = null;
					for (ManifestUploadConfig manifestUploadConfig_header : packagingHeaders) {
						if (manifestUploadConfig_header.getAttr_name().equalsIgnoreCase(ConstantUtil.pkg_vessel_name)) {
							Row vessel_r = sheet.getRow(0);
							if (vesselDetails.getVvCd() != null) {
								Cell vessel_c = vessel_r.createCell(1);
								vessel_c.setCellValue(vesselDetails.getVesselName());
								vessel_c.setCellStyle(CommonUtil.style(wb));
							} else {
								Cell vessel_c = vessel_r.createCell(1);
								vessel_c.setCellStyle(CommonUtil.style(wb));
							}

						} else if (manifestUploadConfig_header.getAttr_name()
								.equalsIgnoreCase(ConstantUtil.pkg_inward_voyage_no)) {
							Row invoy_r = sheet.getRow(1);
							if (vesselDetails.getInwardVoyNo() != null) {
								Cell invoy_c = invoy_r.createCell(1);
								invoy_c.setCellValue(vesselDetails.getInwardVoyNo());
								invoy_c.setCellStyle(CommonUtil.style(wb));
							} else {
								Cell invoy_c = invoy_r.createCell(1);
								invoy_c.setCellStyle(CommonUtil.style(wb));

							}
						} else if (manifestUploadConfig_header.getAttr_name()
								.equals(ConstantUtil.pkg_bills_of_landing_no)) {
							cell_pkg_dim = createCellvalues(sheet, r, manifestUploadConfig_header.getColumn_nm(),
									packageDimensionData.getBl_nbr(), CommonUtil.style(wb));
//							log.info(cell_pkg_dim);
						} else if (manifestUploadConfig_header.getAttr_name()
								.equals(ConstantUtil.pkg_cargo_description)) {
							cell_pkg_dim = createCellvalues(sheet, r, manifestUploadConfig_header.getColumn_nm(),
									packageDimensionData.getCargo_desc(), CommonUtil.style(wb));
//							log.info(cell_pkg_dim);
						} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.pkg_total_packages)) {
							cell_pkg_dim = createCellvalues(sheet, r, manifestUploadConfig_header.getColumn_nm(),
									packageDimensionData.getTotal_pkg(), CommonUtil.style(wb));
							String cell_pkg_dim_data = CommonUtil
									.getCellData(cell_pkg_dim.getAddress().formatAsString(), sheet);
							if (cell_pkg_dim_data != null) {
								cell_pkg_dim.setCellValue(Long.valueOf(packageDimensionData.getTotal_pkg()));
							} else {
								cell_pkg_dim.setCellValue(Long.valueOf(0L));
							}

						} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.pkg_weight_kg)) {
							cell_pkg_dim = createCellvalues(sheet, r, manifestUploadConfig_header.getColumn_nm(),
									packageDimensionData.getGross_wt(), CommonUtil.style(wb));
							if (packageDimensionData.getGross_wt() != null) {
								cell_pkg_dim.setCellValue(Long.valueOf(packageDimensionData.getGross_wt()));
							} else {
								cell_pkg_dim.setCellValue(Long.valueOf(0L));
							}
						} else if (manifestUploadConfig_header.getAttr_name()
								.equals(ConstantUtil.pkg_number_of_packages)) {
							cell_pkg_dim = createCellvalues(sheet, r, manifestUploadConfig_header.getColumn_nm(),
									packageDimensionData.getNbr_of_pkg(), CommonUtil.style(wb));
							if (packageDimensionData.getNbr_of_pkg() != null) {
								cell_pkg_dim.setCellValue(Long.valueOf(packageDimensionData.getNbr_of_pkg()));
							} else {
								cell_pkg_dim.setCellValue(Long.valueOf(0L));
							}
						} else if (manifestUploadConfig_header.getAttr_name()
								.equals(ConstantUtil.pkg_total_package_weight_kg)) {
							cell_pkg_dim = createCellvalues(sheet, r, manifestUploadConfig_header.getColumn_nm(),
									packageDimensionData.getTotal_pkg_wt_kg(), CommonUtil.style(wb));
							if (packageDimensionData.getTotal_pkg_wt_kg() != null) {
								cell_pkg_dim.setCellValue(Long.valueOf(packageDimensionData.getTotal_pkg_wt_kg()));
							} else {
								cell_pkg_dim.setCellValue(Long.valueOf(0L));
							}
						} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.pkg_length_mm)) {
							cell_pkg_dim = createCellvalues(sheet, r, manifestUploadConfig_header.getColumn_nm(),
									packageDimensionData.getLength_pkg(), CommonUtil.style(wb));
							if (packageDimensionData.getLength_pkg() != null) {
								cell_pkg_dim.setCellValue(Double.valueOf(packageDimensionData.getLength_pkg()));
							} else {
								cell_pkg_dim.setCellValue(Double.valueOf(0));
							}
						} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.pkg_breadth_mm)) {
							cell_pkg_dim = createCellvalues(sheet, r, manifestUploadConfig_header.getColumn_nm(),
									packageDimensionData.getBreadth(), CommonUtil.style(wb));
							if (packageDimensionData.getBreadth() != null) {
								cell_pkg_dim.setCellValue(Double.valueOf(packageDimensionData.getBreadth()));
							} else {
								cell_pkg_dim.setCellValue(Double.valueOf(0));
							}
						} else if (manifestUploadConfig_header.getAttr_name()
								.equals(ConstantUtil.pkg_height_thickness_diameter_mm)) {
							cell_pkg_dim = createCellvalues(sheet, r, manifestUploadConfig_header.getColumn_nm(),
									packageDimensionData.getHeight(), CommonUtil.style(wb));
							if (packageDimensionData.getHeight() != null) {
								cell_pkg_dim.setCellValue(Double.valueOf(packageDimensionData.getHeight()));
							} else {
								cell_pkg_dim.setCellValue(Double.valueOf(0));
							}
						}
					}
				}
				
				// Woth Zero and Empty records
				for (PackageDimension packageDimensionData : packageDimensionDataList.stream()
												.filter(x -> CommonUtil.deNull(x.getNbr_of_pkg())=="" || x.getNbr_of_pkg()=="0").collect(Collectors.toList())) 
				{
					
					Row r = sheet.createRow(startRow++);
					Cell cell_pkg_dim = null;
					for (ManifestUploadConfig manifestUploadConfig_header : packagingHeaders) {
						if (manifestUploadConfig_header.getAttr_name().equalsIgnoreCase(ConstantUtil.pkg_vessel_name)) {
							Row vessel_r = sheet.getRow(0);
							if (vesselDetails.getVvCd() != null) {
								Cell vessel_c = vessel_r.createCell(1);
								vessel_c.setCellValue(vesselDetails.getVesselName());
								vessel_c.setCellStyle(CommonUtil.style(wb));
							} else {
								Cell vessel_c = vessel_r.createCell(1);
								vessel_c.setCellStyle(CommonUtil.style(wb));
							}

						} else if (manifestUploadConfig_header.getAttr_name()
								.equalsIgnoreCase(ConstantUtil.pkg_inward_voyage_no)) {
							Row invoy_r = sheet.getRow(1);
							if (vesselDetails.getInwardVoyNo() != null) {
								Cell invoy_c = invoy_r.createCell(1);
								invoy_c.setCellValue(vesselDetails.getInwardVoyNo());
								invoy_c.setCellStyle(CommonUtil.style(wb));
							} else {
								Cell invoy_c = invoy_r.createCell(1);
								invoy_c.setCellStyle(CommonUtil.style(wb));

							}
						} else if (manifestUploadConfig_header.getAttr_name()
								.equals(ConstantUtil.pkg_bills_of_landing_no)) {
							cell_pkg_dim = createCellvalues(sheet, r, manifestUploadConfig_header.getColumn_nm(),
									packageDimensionData.getBl_nbr(), CommonUtil.style(wb));
//							log.info(cell_pkg_dim);
						} else if (manifestUploadConfig_header.getAttr_name()
								.equals(ConstantUtil.pkg_cargo_description)) {
							cell_pkg_dim = createCellvalues(sheet, r, manifestUploadConfig_header.getColumn_nm(),
									packageDimensionData.getCargo_desc(), CommonUtil.style(wb));
//							log.info(cell_pkg_dim);
						} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.pkg_total_packages)) {
							cell_pkg_dim = createCellvalues(sheet, r, manifestUploadConfig_header.getColumn_nm(),
									packageDimensionData.getTotal_pkg(), CommonUtil.style(wb));
							String cell_pkg_dim_data = CommonUtil
									.getCellData(cell_pkg_dim.getAddress().formatAsString(), sheet);
							if (cell_pkg_dim_data != null) {
								cell_pkg_dim.setCellValue(Long.valueOf(packageDimensionData.getTotal_pkg()));
							} else {
								cell_pkg_dim.setCellValue(Long.valueOf(0L));
							}

						} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.pkg_weight_kg)) {
							cell_pkg_dim = createCellvalues(sheet, r, manifestUploadConfig_header.getColumn_nm(),
									packageDimensionData.getGross_wt(), CommonUtil.style(wb));
							if (packageDimensionData.getGross_wt() != null) {
								cell_pkg_dim.setCellValue(Long.valueOf(packageDimensionData.getGross_wt()));
							} else {
								cell_pkg_dim.setCellValue(Long.valueOf(0L));
							}
						} else if (manifestUploadConfig_header.getAttr_name()
								.equals(ConstantUtil.pkg_number_of_packages)) {
							cell_pkg_dim = createCellvalues(sheet, r, manifestUploadConfig_header.getColumn_nm(),
									packageDimensionData.getNbr_of_pkg(), CommonUtil.style(wb));
							if (packageDimensionData.getNbr_of_pkg() != null) {
								cell_pkg_dim.setCellValue(Long.valueOf(packageDimensionData.getNbr_of_pkg()));
							} else {
								cell_pkg_dim.setCellValue(Long.valueOf(0L));
							}
						} else if (manifestUploadConfig_header.getAttr_name()
								.equals(ConstantUtil.pkg_total_package_weight_kg)) {
							cell_pkg_dim = createCellvalues(sheet, r, manifestUploadConfig_header.getColumn_nm(),
									packageDimensionData.getTotal_pkg_wt_kg(), CommonUtil.style(wb));
							if (packageDimensionData.getTotal_pkg_wt_kg() != null) {
								cell_pkg_dim.setCellValue(Long.valueOf(packageDimensionData.getTotal_pkg_wt_kg()));
							} else {
								cell_pkg_dim.setCellValue(Long.valueOf(0L));
							}
						} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.pkg_length_mm)) {
							cell_pkg_dim = createCellvalues(sheet, r, manifestUploadConfig_header.getColumn_nm(),
									packageDimensionData.getLength_pkg(), CommonUtil.style(wb));
							if (packageDimensionData.getLength_pkg() != null) {
								cell_pkg_dim.setCellValue(Double.valueOf(packageDimensionData.getLength_pkg()));
							} else {
								cell_pkg_dim.setCellValue(Double.valueOf(0));
							}
						} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.pkg_breadth_mm)) {
							cell_pkg_dim = createCellvalues(sheet, r, manifestUploadConfig_header.getColumn_nm(),
									packageDimensionData.getBreadth(), CommonUtil.style(wb));
							if (packageDimensionData.getBreadth() != null) {
								cell_pkg_dim.setCellValue(Double.valueOf(packageDimensionData.getBreadth()));
							} else {
								cell_pkg_dim.setCellValue(Double.valueOf(0));
							}
						} else if (manifestUploadConfig_header.getAttr_name()
								.equals(ConstantUtil.pkg_height_thickness_diameter_mm)) {
							cell_pkg_dim = createCellvalues(sheet, r, manifestUploadConfig_header.getColumn_nm(),
									packageDimensionData.getHeight(), CommonUtil.style(wb));
							if (packageDimensionData.getHeight() != null) {
								cell_pkg_dim.setCellValue(Double.valueOf(packageDimensionData.getHeight()));
							} else {
								cell_pkg_dim.setCellValue(Double.valueOf(0));
							}
						}
					}
				}
				
				log.info("Write packageDimension records in Excel ENDS for vvCd: " + vvCd);
			} catch (Exception e) {
				log.info("Exception packageDimension records in Excel : ", e);
			} finally {
				log.info("Write packageDimension records in Excel  END");
			}
		} catch (BusinessException be) {
			log.info("Exception packagingDownload : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception packagingDownload : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END packagingDownload");
		}
		return wb;
	}

	@Override
	public Summary processPackagingExcel(MultipartFile uploadingFile,
			CargoManifestFileUploadDetails cargoManifestFileUploadDetails, String vvCd, String userId) throws BusinessException {
		XSSFWorkbook workbook = null;
		XSSFSheet sheet=null;
		Summary summary = new Summary();
		String outputFileName = null;
		FileOutputStream fileOut = null;
		try {
			log.info("START processPackagingExcel: Param:" + "uploadingFile:" + uploadingFile.getOriginalFilename()
					+ "vvCd:" + vvCd + "UserId:" + userId);
			Long seq_id = manifestRepo.insertManifestExcelDetails(cargoManifestFileUploadDetails);
			SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmm");

			if (cargoManifestFileUploadDetails.getTypeCd().equals(ConstantUtil.typeCd_Packaging)) {
				log.info("Packaging Process: VesselDetailsValidation for Start  for vvCd :" + vvCd);
				workbook = new XSSFWorkbook(OPCPackage.open(uploadingFile.getInputStream()));
				sheet = workbook.getSheetAt(0);
				summary = packageVesselDetailValidation(workbook,sheet, vvCd,summary); // Vessel and inward no validation
				log.info("Packaging Process vesselDetailsValidation END  for vvCd :" + vvCd + " summary :"
						+ summary.toString());
				if (summary.isHeaderValid()) {
					log.info("Packaging Process: excelTemplateValidation START  for vvCd :" + vvCd);
					summary = packageTemplateValidation(workbook,sheet,summary); // header validation
					log.info("Packaging Process: excelTemplateValidation END  for vvCd :" + vvCd + " summary :"
							+ summary.toString());
					if (summary.isHeaderValid()) {
						log.info("Packaging Process: excelRowsValidate START  for vvCd :" + vvCd);
						summary = packagingRowsValidate(workbook,sheet, vvCd, userId,summary); // rows validation
						log.info("Packaging Process excelRowsValidate END  for vvCd :" + vvCd + " summary :"
								+ summary.toString());
					}
				}
			}
			// 4)create error excel name

			if (cargoManifestFileUploadDetails.getTypeCd().equals(ConstantUtil.typeCd_Packaging)) {
				outputFileName = ConstantUtil.packaging_filename + f.format(new Date()) + ConstantUtil.file_ext;
			} else if (cargoManifestFileUploadDetails.getTypeCd().equals(ConstantUtil.typeCd_Manifest)) {
				outputFileName = ConstantUtil.manifest_filename + f.format(new Date()) + ConstantUtil.file_ext;
			}

			// 5)need to copy file
			fileOut = new FileOutputStream(folderPath + "/" + vvCd + "/" + outputFileName);
			summary.getWorkbook().write(fileOut);
			summary.getWorkbook().close();
			fileOut.close();

			// 6)update output file name
			boolean row = manifestRepo.updateManifestExcelDetails(seq_id, outputFileName);
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
		} catch (BusinessException be) {
			log.info("Exception processPackagingExcel : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception processPackagingExcel : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END processPackagingExcel");
		}
		return summary;
	}

	private Summary packageTemplateValidation(XSSFWorkbook workbook,XSSFSheet sheet,Summary summary) throws BusinessException {
		List<ManifestUploadConfig> packagingTemplate = null;
		List<Comments> commentsList = new ArrayList<Comments>();
		int cellIndex = 0;
		try {
			//workbook = new XSSFWorkbook(OPCPackage.open(uploadingFile.getInputStream()));
			//sheet = workbook.getSheetAt(0);
			int headerRow = ConstantUtil.row_header;
			Row header_row = sheet.getRow(headerRow);

			packagingTemplate = manifestRepo.getPackagingTemplate();
			log.info("packageTemplateValidation:" + packagingTemplate.size());

			if (commentsList.size() == 0) {
				for (ManifestUploadConfig manifestUploadConfig_header : packagingTemplate) {
					Cell header_cell = header_row.getCell(cellIndex);
					String header_value = CommonUtil.getCellData (header_cell.getAddress().formatAsString(), sheet);
					if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.pkg_bills_of_landing_no)) {
						boolean coloremoval = CommonUtil.removeColorsAndComment(
								header_cell.getAddress().formatAsString(), sheet, CommonUtil.style_header(workbook));
						log.info(coloremoval);
						if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
							Comments comments = new Comments();
							comments.setKey(manifestUploadConfig_header.getAttr_name());
							comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
							comments.setColumnNm(header_cell.getAddress().formatAsString());
							commentsList.add(comments);
						}
						cellIndex++;
					} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.pkg_cargo_description)) {
						boolean coloremoval = CommonUtil.removeColorsAndComment(
								header_cell.getAddress().formatAsString(), sheet, CommonUtil.style_header(workbook));
						log.info(coloremoval);
						if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
							Comments comments = new Comments();
							comments.setKey(manifestUploadConfig_header.getAttr_name());
							comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
							comments.setColumnNm(header_cell.getAddress().formatAsString());
							commentsList.add(comments);
						}
						cellIndex++;
					} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.pkg_total_packages)) {
						boolean coloremoval = CommonUtil.removeColorsAndComment(
								header_cell.getAddress().formatAsString(), sheet, CommonUtil.style_header(workbook));
						log.info(coloremoval);
						if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
							Comments comments = new Comments();
							comments.setKey(manifestUploadConfig_header.getAttr_name());
							comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
							comments.setColumnNm(header_cell.getAddress().formatAsString());
							commentsList.add(comments);
						}
						cellIndex++;
					} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.pkg_weight_kg)) {
						boolean coloremoval = CommonUtil.removeColorsAndComment(
								header_cell.getAddress().formatAsString(), sheet, CommonUtil.style_header(workbook));
						log.info(coloremoval);
						if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
							Comments comments = new Comments();
							comments.setKey(manifestUploadConfig_header.getAttr_name());
							comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
							comments.setColumnNm(header_cell.getAddress().formatAsString());
							commentsList.add(comments);
						}
						cellIndex++;
					} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.pkg_number_of_packages)) {
						boolean coloremoval = CommonUtil.removeColorsAndComment(
								header_cell.getAddress().formatAsString(), sheet, CommonUtil.style_header(workbook));
						log.info(coloremoval);
						if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
							Comments comments = new Comments();
							comments.setKey(manifestUploadConfig_header.getAttr_name());
							comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
							comments.setColumnNm(header_cell.getAddress().formatAsString());
							commentsList.add(comments);
						}
						cellIndex++;
					} else if (manifestUploadConfig_header.getAttr_name()
							.equals(ConstantUtil.pkg_total_package_weight_kg)) {
						boolean coloremoval = CommonUtil.removeColorsAndComment(
								header_cell.getAddress().formatAsString(), sheet, CommonUtil.style_header(workbook));
						log.info(coloremoval);
						if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
							Comments comments = new Comments();
							comments.setKey(manifestUploadConfig_header.getAttr_name());
							comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
							comments.setColumnNm(header_cell.getAddress().formatAsString());
							commentsList.add(comments);

						}
						cellIndex++;
					} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.pkg_length_mm)) {
						boolean coloremoval = CommonUtil.removeColorsAndComment(
								header_cell.getAddress().formatAsString(), sheet, CommonUtil.style_header(workbook));
						log.info(coloremoval);
						if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
							Comments comments = new Comments();
							comments.setKey(manifestUploadConfig_header.getAttr_name());
							comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
							comments.setColumnNm(header_cell.getAddress().formatAsString());
							commentsList.add(comments);
						}
						cellIndex++;
					} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.pkg_breadth_mm)) {
						boolean coloremoval = CommonUtil.removeColorsAndComment(
								header_cell.getAddress().formatAsString(), sheet, CommonUtil.style_header(workbook));
						log.info(coloremoval);
						if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(header_value)) {
							Comments comments = new Comments();
							comments.setKey(manifestUploadConfig_header.getAttr_name());
							comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
							comments.setColumnNm(header_cell.getAddress().formatAsString());
							commentsList.add(comments);
						}
						cellIndex++;
					}
				}
			}

			boolean isCommentCreated = CommonUtil.createCellComment(commentsList, packagingTemplate, sheet, workbook);
			log.info("isCommentCreated : " + isCommentCreated);

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
			return summary;
		} catch (BusinessException be) {
			log.info("Exception packageTemplateValidation : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception packageTemplateValidation : ", e);
			summary.setTotalLineItemProcessed(String.valueOf(0));
			summary.setTotalFail(String.valueOf(0));
			summary.setTotalSuccess(String.valueOf(0));
			summary.setWorkbook(workbook);
			summary.setHeaderValid(false);

			boolean isCommentCreated = CommonUtil.createCellComment(commentsList, packagingTemplate, sheet, workbook);
			log.info("isCommentCreated : " + isCommentCreated);
		}
		return summary;
	}

	private Summary packagingRowsValidate(XSSFWorkbook workbook,XSSFSheet sheet, String vvCd, String userId,Summary summary) throws BusinessException {
		List<PackageDimension> packageDimensionsRecords = new ArrayList<PackageDimension>();

		log.info("START packagingRowsValidate:" + "upload vvCd:" + vvCd + "UserId:"	+ userId);
		try {
//			DecimalFormat df = new DecimalFormat("0.00");

			int headerRow1 = ConstantUtil.row_header;
			//workbook = new XSSFWorkbook(OPCPackage.open(uploadingFile.getInputStream()));
			//XSSFSheet sheet = workbook.getSheetAt(0);
			Row header_row = sheet.getRow(headerRow1);
			int rowStart = ConstantUtil.row_start;

			List<ManifestUploadConfig> header = manifestRepo.getPackagingTemplate();
			log.info("getPackagingTemplate:" + header.size());
			List<CargoManifest> cargoManifestList = manifestRepo.getManifestDetailsForPackage(vvCd);
			log.info("getManifestDetailsForPackage:" + cargoManifestList.size());

			// Find row count
			int rowCount = CommonUtil.getRowCount(sheet, rowStart, ConstantUtil.pakaging_bl_nbr_index,
					ConstantUtil.typeCd_Packaging);
			int recStart = rowStart + 1;
			List<Boolean> successStatus = new ArrayList<Boolean>();
//			String bl_nbr = null;

			log.info("packagingRowsValidation Starts");
			List<Comments> commentsList = null;

			// for remarks REMOVAL - starts
			for (Cell cell : header_row) {
				if (cell.getStringCellValue().equalsIgnoreCase(ConstantUtil.remarks)) {
					CellReference cr = new CellReference(cell);
					// Remove remarks Column
//					int rowStart1 = ConstantUtil.row_start;
					// Find row count
					int rowCount1 = CommonUtil.getRowCount(sheet, rowStart, ConstantUtil.manifest_bl_nbr_index,
							ConstantUtil.typeCd_Manifest);
					for (int i = headerRow1; i <= rowCount1; i++) {
						Row r = sheet.getRow(i);
						Cell c = r.getCell(cr.getCol());
						if (c != null) {
							r.removeCell(c);
						}
					}
				}
			}
			// for remarks - ends

			for (int i = recStart; i <= rowCount + 1; i++) {
				PackageDimension packageDimension = new PackageDimension();
				try {
					commentsList = new ArrayList<Comments>();
					for (ManifestUploadConfig manifestUploadConfig : header) {
						if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.pkg_bills_of_landing_no)) {
							// Bill of Landing no
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellDataStringType(ref, sheet);
							boolean coloremoval = removeColorsAndComment(ref, sheet, CommonUtil.no_style(workbook));
							log.info(coloremoval);
							if (CommonUtil.deNull(cellData) == "") {
								if (manifestUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									// comments.setMessage( manifestUploadConfig.getAttr_name() +
									// ConstantUtil.mandatory);

									commentsList.add(comments);
									successStatus.add(false);
								}
							} else {
								// validate billnumber valid
								if (cargoManifestList.stream()
										.filter(x -> x.getBills_of_landing_no().equalsIgnoreCase(cellData.trim())).findFirst()
										.orElse(null) == null) {
									log.info("ErrorMsg_BlNoNotExist :" + cellData);
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(ConstantUtil.ErrorMsg_BlNoNotExist);
									commentsList.add(comments);
									successStatus.add(false);
								} else {
									packageDimension.setBl_nbr(cellData.trim());
								}
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.pkg_cargo_description)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							boolean coloremoval = removeColorsAndComment(ref, sheet, CommonUtil.no_style(workbook));
							log.info(coloremoval);
							if (CommonUtil.deNull(cellData) == "") {
								if (manifestUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(manifestUploadConfig.getAttr_name() + ConstantUtil.mandatory);
									commentsList.add(comments);
									successStatus.add(false);
								}
							} else {
								packageDimension.setCargo_desc(cellData);
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.pkg_total_packages)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							boolean coloremoval = removeColorsAndComment(ref, sheet, CommonUtil.no_style(workbook));
							log.info(coloremoval);
							if (CommonUtil.deNull(cellData) == "") {
								if (manifestUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(manifestUploadConfig.getAttr_name() + ConstantUtil.mandatory);
									commentsList.add(comments);
									successStatus.add(false);
								}
							} else if (!CommonUtil.isNumeric(cellData)) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
								commentsList.add(comments);
								successStatus.add(false);
							}else if (!CommonUtil.decimalPointChk(cellData)) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_Valid_DecimalPt);
								commentsList.add(comments);
								successStatus.add(false);
							} else {
								packageDimension.setTotal_pkg(cellData);
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.pkg_weight_kg)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							boolean coloremoval = removeColorsAndComment(ref, sheet, CommonUtil.no_style(workbook));
							log.info(coloremoval);
							if (CommonUtil.deNull(cellData) == "") {
								if (manifestUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(manifestUploadConfig.getAttr_name() + ConstantUtil.mandatory);
									commentsList.add(comments);
									successStatus.add(false);
								}
							} else if (!CommonUtil.isNumeric(cellData)) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
								commentsList.add(comments);
								successStatus.add(false);
							} else {
								packageDimension.setGross_wt(cellData);
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.pkg_number_of_packages)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							log.info(" cellData :" + cellData);
							boolean coloremoval = removeColorsAndComment(ref, sheet, CommonUtil.no_style(workbook));
							log.info(coloremoval);
							if (CommonUtil.deNull(cellData) == "") {
								if (manifestUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(manifestUploadConfig.getAttr_name() + ConstantUtil.mandatory);
									commentsList.add(comments);
									successStatus.add(false);
								}
							} else if (!CommonUtil.isInteger(cellData)) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_NonInteger);
								commentsList.add(comments);
								successStatus.add(false);
							} else if (Double.parseDouble(cellData) <= 0) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_Valid_noOfPkg);
								commentsList.add(comments);
								successStatus.add(false);
							} else if (!CommonUtil.decimalPointChk(cellData)) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_Valid_DecimalPt);
								commentsList.add(comments);
								successStatus.add(false);
							}else {
								packageDimension.setNbr_of_pkg(cellData);
							}
						} else if (manifestUploadConfig.getAttr_name()
								.equals(ConstantUtil.pkg_total_package_weight_kg)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							boolean coloremoval = removeColorsAndComment(ref, sheet, CommonUtil.no_style(workbook));
							log.info(coloremoval);
							if (CommonUtil.deNull(cellData) == "") {
								if (manifestUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(manifestUploadConfig.getAttr_name() + ConstantUtil.mandatory);
									commentsList.add(comments);
									successStatus.add(false);
								}
							} else if (!CommonUtil.isNumeric(cellData)) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
								commentsList.add(comments);
								successStatus.add(false);
							} else if (Double.parseDouble(cellData) < 0) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_nonNegNo);
								commentsList.add(comments);
								successStatus.add(false);
							} else if (!CommonUtil.decimalPointChk(cellData)) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_Valid_DecimalPt);
								commentsList.add(comments);
								successStatus.add(false);
							}else {
								packageDimension.setTotal_pkg_wt_kg(cellData);
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.pkg_length_mm)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							boolean coloremoval = removeColorsAndComment(ref, sheet, CommonUtil.no_style(workbook));
							log.info(coloremoval);
							if (CommonUtil.deNull(cellData) == "") {
								if (manifestUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(manifestUploadConfig.getAttr_name() + ConstantUtil.mandatory);
									commentsList.add(comments);
									successStatus.add(false);
								}
							} else if (!CommonUtil.isNumeric(cellData)) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
								commentsList.add(comments);
								successStatus.add(false);
							} else if (Double.parseDouble(cellData) < 0) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_nonNegNo);
								commentsList.add(comments);
								successStatus.add(false);
							} else if (!CommonUtil.decimalPointChk(cellData)) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_Valid_DecimalPt);
								commentsList.add(comments);
								successStatus.add(false);
							}else {
								packageDimension.setLength_pkg(cellData);
							}
						} else if (manifestUploadConfig.getAttr_name().equals(ConstantUtil.pkg_breadth_mm)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							boolean coloremoval = removeColorsAndComment(ref, sheet, CommonUtil.no_style(workbook));
							log.info(coloremoval);
							if (CommonUtil.deNull(cellData) == "") {
								if (manifestUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(manifestUploadConfig.getAttr_name() + ConstantUtil.mandatory);
									commentsList.add(comments);
									successStatus.add(false);
								}
							} else if (!CommonUtil.isNumeric(cellData)) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
								commentsList.add(comments);
								successStatus.add(false);
							} else if (Double.parseDouble(cellData) < 0) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_nonNegNo);
								commentsList.add(comments);
								successStatus.add(false);
							} else if (!CommonUtil.decimalPointChk(cellData)) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_Valid_DecimalPt);
								commentsList.add(comments);
								successStatus.add(false);
							}else {
								packageDimension.setBreadth(cellData);
							}
						} else if (manifestUploadConfig.getAttr_name()
								.equals(ConstantUtil.pkg_height_thickness_diameter_mm)) {
							String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
							String ref = columnIndex + String.valueOf(i);
							String cellData = CommonUtil.getCellData(ref, sheet);
							boolean coloremoval = removeColorsAndComment(ref, sheet, CommonUtil.no_style(workbook));
							log.info(coloremoval);
							if (CommonUtil.deNull(cellData) == "") {
								if (manifestUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
									Comments comments = new Comments();
									comments.setKey(manifestUploadConfig.getAttr_name());
									comments.setMessage(manifestUploadConfig.getAttr_name() + ConstantUtil.mandatory);
									commentsList.add(comments);
									successStatus.add(false);
								}
							} else if (!CommonUtil.isNumeric(cellData)) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_NonNumeric);
								commentsList.add(comments);
								successStatus.add(false);
							} else if (!CommonUtil.decimalPointChk(cellData)) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_Valid_DecimalPt);
								commentsList.add(comments);
								successStatus.add(false);
							}else if (Double.parseDouble(cellData) < 0) {
								Comments comments = new Comments();
								comments.setKey(manifestUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_nonNegNo);
								commentsList.add(comments);
								successStatus.add(false);
							} else {
								packageDimension.setHeight(cellData);
							}
						}
					}
					packageDimension.setRowNum(i - 1);// -1 is to take the exact row number
					packageDimension.setErrorInfo(commentsList);
					if (commentsList.size() > 0) {
						packageDimension.setMessage(ConstantUtil.error);
					} else {
						packageDimension.setMessage(ConstantUtil.success);
					}
					log.info(packageDimension.toString());
					packageDimensionsRecords.add(packageDimension);

				} catch (Exception e) {
					log.info("Exception packagingRowsValidate : ", e);
					packageDimension.setRowNum(i - 1);// -1 is to take the exact row number
					packageDimension.setErrorInfo(commentsList);
					if (commentsList.size() > 0) {
						packageDimension.setMessage(ConstantUtil.error);
					} else {
						packageDimension.setMessage(ConstantUtil.success);
					}
					log.info("Under exception :" + packageDimension.toString());
					packageDimensionsRecords.add(packageDimension);
				}
			}

			log.info("Bef pkg check. packageDimensionsRecords :" + packageDimensionsRecords.size());
			// region max logic for no of pkg

			List<String> bl_nbr_lst = new ArrayList<String>();
			for (int i = 0; i < packageDimensionsRecords.size(); i++) {
				Comments comments = new Comments();
				List<Comments> commentList3 = new ArrayList<Comments>();
				String bl_nbr1 = packageDimensionsRecords.get(i).getBl_nbr();
				if (!bl_nbr_lst.contains(bl_nbr1)) {
					for (ManifestUploadConfig manifestUploadConfig : header) {
						if (manifestUploadConfig.getAttr_name().equalsIgnoreCase(ConstantUtil.pkg_number_of_packages)) {
							CargoManifest cm = getCargoManifestDetail(bl_nbr1, cargoManifestList);
							if (cm != null) {
								Double cargoNoOfPkg = Double.parseDouble(cm.getNumber_of_packages());
								Double noOfPkgs = 0.0;
								for (int j = 0; j < packageDimensionsRecords.size(); j++) {
									if (packageDimensionsRecords.get(j).getErrorInfo().size() == 0 && CommonUtil
											.deNull(packageDimensionsRecords.get(j).getNbr_of_pkg()) != "") {
										String bl_nbr2 = packageDimensionsRecords.get(j).getBl_nbr();
										if (bl_nbr1.equalsIgnoreCase(bl_nbr2)) {
											if (noOfPkgs > cargoNoOfPkg) {
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(ConstantUtil.ErrorMsg_no_pkg_Exceeds);
												commentList3.add(comments);
												successStatus.add(false);
												packageDimensionsRecords.get(j).getErrorInfo().addAll(commentList3);
												if (commentList3.size() > 0) {
													packageDimensionsRecords.get(j).setMessage(ConstantUtil.error);
												} else {
													packageDimensionsRecords.get(j).setMessage(ConstantUtil.success);
												}
												log.info("****if packageDimensionsRecords bl_nbr1:" + bl_nbr1
														+ ",bl_nbr2 :" + bl_nbr2 + ", data :"
														+ packageDimensionsRecords.get(j).toString());
											} else {

												noOfPkgs = noOfPkgs + Double
														.parseDouble(packageDimensionsRecords.get(j).getNbr_of_pkg());
												if (noOfPkgs > cargoNoOfPkg) {
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(ConstantUtil.ErrorMsg_no_pkg_Exceeds);
													commentList3.add(comments);
													successStatus.add(false);

													packageDimensionsRecords.get(j).getErrorInfo().addAll(commentList3);
													if (commentList3.size() > 0) {
														packageDimensionsRecords.get(j).setMessage(ConstantUtil.error);
													} else {
														packageDimensionsRecords.get(j)
																.setMessage(ConstantUtil.success);
													}
													log.info("****else packageDimensionsRecords bl_nbr1:" + bl_nbr1
															+ ",bl_nbr2 :" + bl_nbr2 + ", data :"
															+ packageDimensionsRecords.get(j).toString());
												}
											}
										}
									}
								}
							}
						}
					}

				}
				bl_nbr_lst.add(packageDimensionsRecords.get(i).getBl_nbr());
			}
			// endregion max logic for no of pkg

			log.info("Bef totalWght check. packageDimensionsRecords :" + packageDimensionsRecords.size());
			// region max logic for totalWt
			List<String> bl_nbr_lst1 = new ArrayList<String>();
			for (int i = 0; i < packageDimensionsRecords.size(); i++) {
				List<Comments> commentList2 = new ArrayList<Comments>();
				Comments comments = new Comments();
				String bl_nbr1 = packageDimensionsRecords.get(i).getBl_nbr();
				if (!bl_nbr_lst1.contains(bl_nbr1)) {
					for (ManifestUploadConfig manifestUploadConfig : header) {
						if (manifestUploadConfig.getAttr_name()
								.equalsIgnoreCase(ConstantUtil.pkg_total_package_weight_kg)) {
							CargoManifest cm = getCargoManifestDetail(bl_nbr1, cargoManifestList);
							if (cm != null) {
								Double cargoTotalWt = Double.parseDouble(cm.getGross_weight_kg());
								Double totalWt = 0.0;
								for (int j = 0; j < packageDimensionsRecords.size(); j++) {
									if (packageDimensionsRecords.get(j).getErrorInfo().size() == 0 && CommonUtil
											.deNull(packageDimensionsRecords.get(j).getTotal_pkg_wt_kg()) != "") {
										String bl_nbr2 = packageDimensionsRecords.get(j).getBl_nbr();
										if (bl_nbr1.equalsIgnoreCase(bl_nbr2)) {
											if (totalWt > cargoTotalWt) {
												comments.setKey(manifestUploadConfig.getAttr_name());
												comments.setMessage(manifestUploadConfig.getAttr_name()
														+ ConstantUtil.ErrorMsg_totalWt_Exceeds);
												commentList2.add(comments);
												successStatus.add(false);
												packageDimensionsRecords.get(j).getErrorInfo().addAll(commentList2);
												if (commentList2.size() > 0) {
													packageDimensionsRecords.get(j).setMessage(ConstantUtil.error);
												} else {
													packageDimensionsRecords.get(j).setMessage(ConstantUtil.success);
												}
											} else {
												totalWt = totalWt + Double.parseDouble(
														packageDimensionsRecords.get(j).getTotal_pkg_wt_kg());
												if (totalWt > cargoTotalWt) {
													comments.setKey(manifestUploadConfig.getAttr_name());
													comments.setMessage(manifestUploadConfig.getAttr_name()
															+ ConstantUtil.ErrorMsg_totalWt_Exceeds);
													commentList2.add(comments);
													successStatus.add(false);
													packageDimensionsRecords.get(j).getErrorInfo().addAll(commentList2);
													if (commentList2.size() > 0) {
														packageDimensionsRecords.get(j).setMessage(ConstantUtil.error);
													} else {
														packageDimensionsRecords.get(j)
																.setMessage(ConstantUtil.success);
													}
												}
											}
										}
									}
								}
							}
						}
					}

				}
				bl_nbr_lst.add(packageDimensionsRecords.get(i).getBl_nbr());
			}
			// endregion max logic for totalWt

			List<PackageDimension> processResults = new ArrayList<PackageDimension>();
			if (successStatus.contains(false)) {
				processResults = packageDimensionsRecords;
			} else {
				processResults = manifestRepo.insertPackagingData(packageDimensionsRecords, vvCd, userId);
			}

			// processResults.add(PackageDimension.Stre)
			log.info("before process data packageDimensionsRecords :" + packageDimensionsRecords.size());
			// comented by NS 11/12
			/*
			 * processResults.addAll(packageDimensionsRecords.stream() .filter(x ->
			 * x.getMessage().equalsIgnoreCase(ConstantUtil.error) ||
			 * x.getErrorInfo().size()>0) .collect(Collectors.toList()));
			 * 
			 * processResults.addAll(manifestRepo.insertPackagingData(
			 * packageDimensionsRecords.stream() .filter(x ->
			 * x.getMessage().equalsIgnoreCase(ConstantUtil.success) &&
			 * x.getErrorInfo().size()==0) .collect(Collectors.toList()), vvCd, userId));
			 * log.info("processResults :"+ processResults.toString());
			 */
			// draw success and error
			int success = 0;
			int failure = 0;
			int headerRow = ConstantUtil.row_header;
			Row r_remarks = sheet.getRow(headerRow);
			Cell cell_remarks = r_remarks.createCell(r_remarks.getLastCellNum());
			cell_remarks.setCellValue(ConstantUtil.remarks);
			sheet.autoSizeColumn(cell_remarks.getColumnIndex());
			
			for (PackageDimension packageDimension : processResults) {
				if (packageDimension.getMessage().equalsIgnoreCase(ConstantUtil.success)
						&& packageDimension.getErrorInfo().size() == 0) {
					Row r = sheet.getRow(packageDimension.getRowNum());
					Cell cell = r.createCell(r.getLastCellNum());
					cell.setCellValue(packageDimension.getMessage());
					cell.setCellStyle(CommonUtil.style_success(workbook));
					success++;
				} else if (packageDimension.getMessage().equalsIgnoreCase(ConstantUtil.error)
						|| packageDimension.getMessage().equalsIgnoreCase(ConstantUtil.ErrorMsg_Common)
						|| packageDimension.getErrorInfo().size() > 0) {
					Row r1 = sheet.getRow(packageDimension.getRowNum());
					Cell c = r1.createCell(r1.getLastCellNum());
					c.setCellValue(packageDimension.getMessage());
					c.setCellStyle(CommonUtil.style_error(workbook));
					if (packageDimension.getErrorInfo().size() > 0) {
						failure++;
						List<Comments> errorInfo = packageDimension.getErrorInfo();
						for (Comments comment : errorInfo) {
							for (ManifestUploadConfig manifestUploadConfig : header) {
								if (manifestUploadConfig.getAttr_name().equals(comment.getKey())) {
									String columnIndex = CommonUtil.getColumnIndex(manifestUploadConfig.getColumn_nm());
									String ref = columnIndex + (packageDimension.getRowNum() + 1);
									removeColorsAndComment(ref, sheet, CommonUtil.no_style(workbook));

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
									cell.setCellStyle(CommonUtil.style_error(workbook));
								}
							}
						}
					} else {
						Row r = sheet.getRow(packageDimension.getRowNum());
						Cell cell = r.createCell(r.getLastCellNum());
						cell.setCellValue(packageDimension.getMessage());
						cell.setCellStyle(CommonUtil.style_error(workbook));
						failure++;
					}

				}
			}
			
			// Draw Success and error Error excel
			if (failure == 0) {
				summary.setTotalLineItemProcessed(String.valueOf(packageDimensionsRecords.size()));
				summary.setTotalFail(String.valueOf(failure));
				summary.setTotalSuccess(String.valueOf(success));
				summary.setWorkbook(workbook);
				return summary;
			} else {
				summary.setTotalLineItemProcessed(String.valueOf(packageDimensionsRecords.size()));
				summary.setTotalFail(String.valueOf(failure));
				summary.setTotalSuccess(String.valueOf(success));
				summary.setWorkbook(workbook);
				return summary;
			}
		} catch (BusinessException be) {
			log.info("Exception packagingRowsValidate : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception packagingRowsValidate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END packagingRowsValidate");
		}
	}

	private CargoManifest getCargoManifestDetail(String bl_nbr, List<CargoManifest> cargoManifestList) throws BusinessException {
		CargoManifest manifest = null;
		try {
			log.info("START getCargoManifestDetail bl_nbr:" + bl_nbr + "cargoManifestList:" + cargoManifestList.size());
			manifest = cargoManifestList.stream()
					.filter(x -> x.getBills_of_landing_no().equalsIgnoreCase(bl_nbr)).findFirst().orElse(null);
		} catch (Exception e) {
			log.info("Exception getCargoManifestDetail : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getCargoManifestDetail");
		}
		return manifest;
	}

	private Summary packageVesselDetailValidation(XSSFWorkbook workbook,XSSFSheet sheet, String vvCd,Summary summary ) throws BusinessException {
		VesselVoyValueObject vvObj = new VesselVoyValueObject();
		try {
			log.info("START packageVesselDetailValidation");
			ZipSecureFile.setMinInflateRatio(0);
			List<ManifestUploadConfig> template = manifestRepo.getPackagingTemplate();
			PageDetails vesselCallDetails = manifestRepo.getVesselCallDetails(vvCd);
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
			String excelVersionNo=workbook.getProperties().getCoreProperties().getRevision();
			String dbVersion=manifestRepo.getTemplateVersionNo(ConstantUtil.packaging_type_cd);
			
			for (ManifestUploadConfig manifestUploadConfig_header : template) {
				if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.vessel_name)) {
					CellReference cr = new CellReference(manifestUploadConfig_header.getColumn_nm() + 1);
					Row row_vsl_value = sheet.getRow(cr.getRow());
					if (row_vsl_value == null) {
						Row emptyRow = sheet.createRow(cr.getRow());
						Cell emptyCell = emptyRow.createCell(0);
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(ConstantUtil.ErrorMsg_invalidExcel);
						comments.setColumnNm(emptyCell.getAddress().formatAsString());
						commentsList.add(comments);
						break;
					}
					boolean coloremoval = removeColorsAndComment(cr.formatAsString(), sheet, no_style);
					log.info(coloremoval);
					//Version check
					if(!dbVersion.equalsIgnoreCase(excelVersionNo)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(ConstantUtil.versionMismatch);
						comments.setColumnNm(cr.formatAsString());
						commentsList.add(comments);
					}
					
					String cellvalue = CommonUtil.getCellData(cr.formatAsString(), sheet);
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(cellvalue)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
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
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						comments.setColumnNm(cell_vsl_value.getAddress().formatAsString());
						commentsList.add(comments);
					} else {
						// for vsl existence check
						vvObj.setVslName(vsl_cell_value);
						isVslFlag = true;
						vsl_comments.setKey(manifestUploadConfig_header.getAttr_name());
						vsl_comments.setMessage(ConstantUtil.ErrorMsg_vslNotExist);
						vsl_comments.setColumnNm(cell_vsl_value.getAddress().formatAsString());
					}
				} else if (manifestUploadConfig_header.getAttr_name().equals(ConstantUtil.inward_voyage_no)) {
					CellReference cr = new CellReference(manifestUploadConfig_header.getColumn_nm() + 2);
					boolean coloremoval = removeColorsAndComment(cr.formatAsString(), sheet, no_style);
					log.info(coloremoval);

					String cellvalue = CommonUtil.getCellData(cr.formatAsString(), sheet);
					if (!manifestUploadConfig_header.getAttr_desc().equalsIgnoreCase(cellvalue)) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						comments.setColumnNm(cr.formatAsString());
						commentsList.add(comments);
					}
					Row row_invoy_value = sheet.getRow(cr.getRow());
					Cell cell_invoy_value = row_invoy_value.getCell(1);
					if (cell_invoy_value == null) {
						cell_invoy_value = row_invoy_value.createCell(1);
					}
					String inVoy_cell_value = CommonUtil.getCellData(cell_invoy_value.getAddress().formatAsString(),
							sheet);
					log.info(cell_invoy_value.getAddress().formatAsString());
					log.info(cr.formatAsString());
					boolean coloremoval2 = removeColorsAndComment(cell_invoy_value.getAddress().formatAsString(), sheet,
							no_style);
					log.info(coloremoval2);

					if (inVoy_cell_value == null) {
						Comments comments = new Comments();
						comments.setKey(manifestUploadConfig_header.getAttr_name());
						comments.setMessage(manifestUploadConfig_header.getAttr_name() + ConstantUtil.mandatory);
						comments.setColumnNm(cell_invoy_value.getAddress().formatAsString());
						commentsList.add(comments);
					} else {
						// for voy existence check
						boolean coloremoval3 = removeColorsAndComment(cell_invoy_value.getAddress().formatAsString(),
								sheet, no_style);
						log.info(coloremoval3);

						vvObj.setVoyNo(inVoy_cell_value);
						isInvoyFlag = true;
						invoy_comments.setKey(manifestUploadConfig_header.getAttr_name());
						invoy_comments.setMessage(ConstantUtil.ErrorMsg_invoyNotExist);
						invoy_comments.setColumnNm(cell_invoy_value.getAddress().formatAsString());
					}
				}

			}

			// for vsl existence check
			if (isVslFlag) {
				if (vvObj.getVslName() != null
						&& (!vvObj.getVslName().equalsIgnoreCase(vesselCallDetails.getVesselName()))) {
					commentsList.add(vsl_comments);
					log.info(vvObj.getVslName() + " :Vessel name Not matched with requested vvCd");
				}
			}
			if (isInvoyFlag) {
				if (vvObj.getVoyNo() != null
						&& (!vvObj.getVoyNo().equalsIgnoreCase(vesselCallDetails.getInwardVoyNo()))) {
					commentsList.add(invoy_comments);
					log.info(vvObj.getVoyNo() + " :invoyNo Not matched with requested vvCd");
				}
			}
			if (commentsList.size() > 0) {
				for (Comments comment : commentsList) {

					for (ManifestUploadConfig manifestUploadConfig : template) {
						if (manifestUploadConfig.getAttr_name().equals(comment.getKey())) {
							CellReference cellRef = new CellReference(comment.getColumnNm());
							boolean coloremoval2 = removeColorsAndComment(cellRef.formatAsString(), sheet, no_style);
							log.info(coloremoval2);
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
			log.info("Exception packageVesselDetailValidation : ", be);
			throw new BusinessException(be.getMessage());
		} catch (Exception e) {
			log.info("Exception packageVesselDetailValidation : ", e);
			summary.setTotalLineItemProcessed(String.valueOf(0));
			summary.setTotalFail(String.valueOf(0));
			summary.setTotalSuccess(String.valueOf(0));
			summary.setWorkbook(workbook);
			summary.setHeaderValid(false);
		} finally {
			log.info("END packageVesselDetailValidation");
		}
		return summary;
	}

	// EndRegion CDE

	public Result validateTransferofManifest(Criteria criteria) throws BusinessException {
		return manifestRepo.validateTransferofManifest(criteria);
	}

	public Result removeHatchBreakDownDetails(Criteria criteria) throws BusinessException {
		return manifestRepo.removeHatchBreakDownDetails(criteria);
	}

	public List<ManifestPkgDimDetails> getCargoDimensionAuditDetails(Criteria criteria) throws BusinessException {
		return manifestRepo.getCargoDimensionAuditDetails(criteria);
	}
	
	public String getVesselCreatedCustomerCode(String vvCd) throws BusinessException
	{
		return manifestRepo.getVesselCreatedCustomerCode(vvCd);
	}
	
	public Boolean vesselDeclarantExists(String vvCd) throws BusinessException
	{
		return manifestRepo.vesselDeclarantExists(vvCd);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean mftCancel(String userID, String seqno, String varno, String blno) throws BusinessException {
		return manifestRepo.mftCancel(userID, seqno, varno, blno);
	}
	
	@Override
	public List<VesselVoyValueObject> getVesselVoyTo(String cocode) throws BusinessException {
		return manifestRepo.getVesselVoyTo(cocode);
	}

	// START CR TO DISABLE VOLUME - NS FEB 2024
	@Override
	public boolean isDisabledVolume(Criteria criteria) throws BusinessException {
		return manifestRepo.isDisabledVolume(criteria);
	}
	// END CR TO DISABLE VOLUME - NS FEB 2024

	// START - #39699 : CR TO VALIDATE CLOSE LCT - NS JUNE 2024
	@Override
	public boolean checkCloseLCT(String vvcd) throws BusinessException {
		return manifestRepo.checkCloseLCT(vvcd);
	}
	// END - #39699 : CR TO VALIDATE CLOSE LCT - NS JUNE 2024

	// START CR FTZ HSCODE - NS JULY 2024
	@Override
	public List<MiscDetail> loadHSSubCode(String query, String hsCode) throws BusinessException {
		return manifestRepo.loadHSSubCode(query, hsCode);
	}

	@Override
	public List<HsCodeDetails> getHsCodeDetailList(String seqno) throws BusinessException {
		return manifestRepo.getHsCodeDetailList(seqno);
	}
	// End CR FTZ HSCODE - NS JULY 2024

	// Start Split BL - NS Jan 2025
	@Override
	public String generateSplitBl(String blRoot, String vvcd) throws BusinessException {
		return manifestRepo.generateSplitBl(blRoot, vvcd);
	}
	// End Split BL - NS Jan 2025
}
