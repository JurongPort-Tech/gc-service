package sg.com.jp.generalcargo.service.impl;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
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
import sg.com.jp.generalcargo.domain.CustomDetailsFileUploadDetails;
import sg.com.jp.generalcargo.domain.CustomDetailsUploadConfig;
import sg.com.jp.generalcargo.domain.FTZBGMVO;
import sg.com.jp.generalcargo.domain.FTZDTMVO;
import sg.com.jp.generalcargo.domain.FTZG4LOCVO;
import sg.com.jp.generalcargo.domain.FTZG4TDTVO;
import sg.com.jp.generalcargo.domain.FTZG5EQD;
import sg.com.jp.generalcargo.domain.FTZG5SELVO;
import sg.com.jp.generalcargo.domain.FTZG7CNIVO;
import sg.com.jp.generalcargo.domain.FTZGroup11NADVO;
import sg.com.jp.generalcargo.domain.FTZGroup11VO;
import sg.com.jp.generalcargo.domain.FTZGroup14CSTVO;
import sg.com.jp.generalcargo.domain.FTZGroup14DGSVO;
import sg.com.jp.generalcargo.domain.FTZGroup14FTXVO;
import sg.com.jp.generalcargo.domain.FTZGroup14GIDVO;
import sg.com.jp.generalcargo.domain.FTZGroup14HANVO;
import sg.com.jp.generalcargo.domain.FTZGroup14MEAVO;
import sg.com.jp.generalcargo.domain.FTZGroup14PCIVO;
import sg.com.jp.generalcargo.domain.FTZGroup14SGPVO;
import sg.com.jp.generalcargo.domain.FTZGroup14VO;
import sg.com.jp.generalcargo.domain.FTZGroup4VO;
import sg.com.jp.generalcargo.domain.FTZGroup5VO;
import sg.com.jp.generalcargo.domain.FTZGroup7VO;
import sg.com.jp.generalcargo.domain.FTZGroup8GEIVO;
import sg.com.jp.generalcargo.domain.FTZGroup8LOCVO;
import sg.com.jp.generalcargo.domain.FTZGroup8RFF;
import sg.com.jp.generalcargo.domain.FTZGroup8VO;
import sg.com.jp.generalcargo.domain.FTZInterchangeVO;
import sg.com.jp.generalcargo.domain.FTZUNBVO;
import sg.com.jp.generalcargo.domain.FTZUNHVO;
import sg.com.jp.generalcargo.domain.FTZUNTVO;
import sg.com.jp.generalcargo.domain.FTZUNZVO;
import sg.com.jp.generalcargo.domain.PageDetails;
import sg.com.jp.generalcargo.domain.SummaryCuscar;
import sg.com.jp.generalcargo.domain.Template;
import sg.com.jp.generalcargo.service.CustomDetailsCuscarService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;

@Service
public class CustomDetailsCuscarServiceImpl implements CustomDetailsCuscarService {

	private final static Log log = LogFactory.getLog(CustomDetailsCuscarServiceImpl.class);

	@Value("${customDetails.file.upload.path}")
	String folderPath;

	@Autowired
	CustomDetailsRepository customDetailsRepo;

	@Autowired
	InwardCargoManifestRepository manifestRepo;

	// Default separators
	private static final char SEGMENT_TERMINATOR = '\'';
	private static final String DATA_ELEMENT_SEPARATOR = "(?<!\\?)\\+";
	private static final char COMPONENT_SEPARATOR = ':';

	@Override
	public Path uploadFile(Criteria criteria, MultipartFile uploadFile) throws BusinessException {
		String fileName = null;
		Path uploadPath = null;
		try {
			if (uploadFile.getOriginalFilename().indexOf("/") >= 0
					|| uploadFile.getOriginalFilename().indexOf("\\") >= 0) {
				log.info("File name validation failed!");
				throw new BusinessException("File name validation failed!");
			}
			String extension = FilenameUtils.getExtension(uploadFile.getOriginalFilename());
			UUID uuid = UUID.randomUUID();
			fileName = uuid.toString() + "." + extension;
			log.info("assignedFileName:" + fileName);
			if (fileName.indexOf("/") >= 0 || fileName.indexOf("\\") >= 0) {
				log.info("File name validation failed!");
				throw new BusinessException("File name validation failed!");
			}

			String varNbr = (CommonUtility.deNull(criteria.getPredicates().get("varNbr"))).trim();
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
			uploadPath = folderLocation.resolve(fileName);
		} catch (BusinessException e) {
			log.error("Exception uploadFile: ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.error("Exception uploadFile: ", e);
			throw new BusinessException("M4201");
		}
		return uploadPath;
	}

	@Override
	public List<FTZInterchangeVO> parseData(Path path, String varNbr) throws BusinessException {
		StringBuilder rawContent = new StringBuilder();
		String line;
		FTZInterchangeVO intVO = new FTZInterchangeVO();
		List<FTZInterchangeVO> intVoList = new ArrayList<>();
		try {
			log.info("START parseData. File path: " + path);
			BufferedReader reader = Files.newBufferedReader(path);
			// Read file into a single string
			while ((line = reader.readLine()) != null) {
				rawContent.append(line);
			}

			// Split by segments
			String[] segments = rawContent.toString().split(String.valueOf(SEGMENT_TERMINATOR));

			String prevField = "";
			String prevGroup = "";

			List<FTZDTMVO> listDtmVO = new ArrayList<>();
			// Group 4
			List<FTZGroup4VO> listGroup4 = new ArrayList<>();
			FTZGroup4VO g4VO = new FTZGroup4VO();
			List<FTZG4LOCVO> listG4LOC = new ArrayList<>();
			// Group 5
			List<FTZGroup5VO> listGroup5 = new ArrayList<>();
			FTZGroup5VO g5VO = new FTZGroup5VO();
			List<FTZG5SELVO> listG5SEL = new ArrayList<>();
			// Group 7
			List<FTZGroup7VO> listGroup7 = new ArrayList<>();
			FTZGroup7VO g7VO = new FTZGroup7VO();
			// Group 8
			List<FTZGroup8VO> listGroup8 = new ArrayList<>();
			FTZGroup8VO g8VO = new FTZGroup8VO();
			List<FTZGroup8LOCVO> listG8LOC = new ArrayList<>();
			List<FTZGroup8GEIVO> listG8GEI = new ArrayList<>();
			// Group 11
			Map<String, FTZGroup11VO> nadMap = new LinkedHashMap<>();
			List<FTZGroup11VO> listGroup11 = new ArrayList<>();
			FTZGroup11VO g11VO = new FTZGroup11VO();
			// Group 14
			List<FTZGroup14VO> listGroup14 = new ArrayList<>();
			FTZGroup14VO g14VO = new FTZGroup14VO();
			List<FTZGroup14HANVO> listG14HAN = new ArrayList<>();
			List<FTZGroup14FTXVO> listG14FTX = new ArrayList<>();
			List<FTZGroup14MEAVO> listG14MEA = new ArrayList<>();
			List<FTZGroup14SGPVO> listG14SGP = new ArrayList<>();
			List<FTZGroup14DGSVO> listG14DGS = new ArrayList<>();
			List<FTZGroup14PCIVO> listG14PCI = new ArrayList<>();

			String currBl = "";
			String currPtyQual = "";
//			Map<String, Object> blGroup = new LinkedHashMap<>();

			// Process each segment
			for (String segment : segments) {
				if (segment.trim().isEmpty()) {
					continue;
				}

				// Split by data element separator
				String[] elements = segment.split(DATA_ELEMENT_SEPARATOR);

				Map<String, List<String[]>> elementMap = new LinkedHashMap<>();

				// Output the parsed data
				log.info("Segment: " + Arrays.toString(elements));
//				List<String> elementList = new ArrayList<>();
				List<String[]> elementList1 = new ArrayList<>();
				String field = "";
				String group = "";

				for (int i = 0; i < elements.length; i++) {
					String[] components = elements[i].split(String.valueOf(COMPONENT_SEPARATOR));
					log.info("  Element " + i + ": " + Arrays.toString(components));

					if (i == 0) {
						field = elements[0];
					} else {
						elementList1.add(components);
					}

					if (i == elements.length - 1) {
						elementMap.put(elements[0], elementList1);
					}
				}
				log.info("Element map: " + elementMap);

				List<String[]> dataList = elementMap.get(field);
				boolean nonGroup = false;
				// check group
				// non-Group
				if (CommonUtility.deNull(field).length() > 3 || CommonUtility.deNull(field).length() < 3) {
					log.error("File is non-EDIFACT/EDI type");
					return null;
				}
				if (field.equalsIgnoreCase("UNB") || field.equalsIgnoreCase("UNH") || field.equalsIgnoreCase("BGM")
						|| (field.equalsIgnoreCase("DTM") && !prevGroup.equalsIgnoreCase("4"))
						|| field.equalsIgnoreCase("UNT") || field.equalsIgnoreCase("UNZ")) {
					group = "";
					int segmentSz = dataList.size();
					int elementSz = 0;
					switch (field) {
					case "UNB":
						FTZUNBVO unbVO = new FTZUNBVO();
						// check segment length
						if (!checkFieldLength(field, "main", segmentSz)) {
							log.error("File is non-EDIFACT/EDI type. Data insufficient. Segment: " + segment);
							throw new BusinessException(ConstantUtil.INVALID_DATA_FORMAT + field + ". Segment: " + segment);
						}

						// syntax identifier
						String[] synId = dataList.get(0);
						elementSz = synId.length;
						if (!checkFieldLength(ConstantUtil.UNB_SYTX_ID, "segment", elementSz)) {
							log.error("File data issue: no adequate value for: S001 SYNTAX IDENTIFIER. Segment: " + segment);
							throw new BusinessException(ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + ConstantUtil.UNB_SYTX_ID + ". Segment: " + segment);
						}

						unbVO.setSyntax_id(synId[0]);
						unbVO.setSyntax_ver_nbr(synId[1]);

						// interchange sender
						String[] intSend = dataList.get(1);
						elementSz = intSend.length;
						if (!checkFieldLength(ConstantUtil.UNB_INT_SEND, "segment", elementSz)) {
							log.error("File data issue: no adequate value for: S002 INTERCHANGE SENDER. Segment: " + segment);
							throw new BusinessException(
									ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + ConstantUtil.UNB_INT_SEND + ". Segment: " + segment);
						}
						unbVO.setSender_id(intSend[0]);

						// interchange recipients
						String[] intRec = dataList.get(2);
						elementSz = intRec.length;
						if (!checkFieldLength(ConstantUtil.UNB_INT_RCV, "segment", elementSz)) {
							log.error("File data issue: no adequate value for: S003 INTERCHANGE RECIPIENT. Segment: " + segment);
							throw new BusinessException(ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + ConstantUtil.UNB_INT_RCV + ". Segment: " + segment);
						}
						unbVO.setRecipient_id(intRec[0]);

						// date and time of preparation
						String[] dtp = dataList.get(3);
						elementSz = dtp.length;
						if (!checkFieldLength(ConstantUtil.UNB_DTP, "segment", elementSz)) {
							log.error("File data issue: no adequate value for: S004 DATE AND TIME OF PREPARATION. Segment: " + segment);
							throw new BusinessException(ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + ConstantUtil.UNB_DTP + ". Segment: " + segment);
						}
						String date = dtp[0];
						if (isNumeric(date)) {
							unbVO.setDate(Integer.parseInt(date));
						}
						String time = dtp[1];
						if (isNumeric(time)) {
							unbVO.setTime(Integer.parseInt(time));
						}

						// interchange control reference
						String[] icr = dataList.get(4);
						elementSz = icr.length;
						if (!checkFieldLength(ConstantUtil.UNB_ICR, "segment", elementSz)) {
							log.error("File data issue: no adequate value for: 0020 Interchange Control Reference. Segment: " + segment);
							throw new BusinessException(ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + ConstantUtil.UNB_ICR + ". Segment: " + segment);
						}
						unbVO.setInterchange_ctrl_ref(icr[0]);
						intVO.setUnb(unbVO);
						nonGroup = true;
						break;

					case "UNH":
						FTZUNHVO unhVO = new FTZUNHVO();
						if (prevField.equalsIgnoreCase("UNT")) {
							prevGroup = "";
							intVoList.add(intVO);
							listGroup4 = new ArrayList<>();
							listGroup5 = new ArrayList<>();
							listGroup7 = new ArrayList<>();
							listGroup8 = new ArrayList<>();
							listGroup11 = new ArrayList<>();
							nadMap = new LinkedHashMap<>();
							listGroup14 = new ArrayList<>();
							intVO = new FTZInterchangeVO();
						}
						// check segment length
						if (!checkFieldLength(field, "main", segmentSz)) {
							log.error("File is non-EDIFACT/EDI type. Data insufficient. Segment: " + segment);
							throw new BusinessException(ConstantUtil.INVALID_DATA_FORMAT + field + ". Segment: " + segment);
						}
						// message reference number
						String[] msgRefNbr = dataList.get(0);
						elementSz = msgRefNbr.length;
						if (!checkFieldLength(ConstantUtil.UNH_MSG_REF_NBR, "segment", elementSz)) {
							log.error("File data issue: no adequate value for: 0062 MESSAGE REFERENCE NUMBER. Segment: " + segment);
							throw new BusinessException(
									ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + ConstantUtil.UNH_MSG_REF_NBR + ". Segment: " + segment);
						}
						unhVO.setMsg_ref_nbr(msgRefNbr[0]);

						// message identifier
						String[] msgId = dataList.get(1);
						elementSz = msgId.length;
						if (!checkFieldLength(ConstantUtil.UNH_MSG_ID, "segment", elementSz)) {
							log.error("File data issue: no adequate value for: S009 MESSAGE IDENTIFIER. Segment: " + segment);
							throw new BusinessException(ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + ConstantUtil.UNH_MSG_ID + ". Segment: " + segment);
						}
						unhVO.setMsg_type(msgId[0]);
						unhVO.setMsg_ver_nbr(msgId[1]);
						unhVO.setMsg_release_nbr(msgId[2]);
						unhVO.setCtrl_agency(msgId[3]);
						if (elementSz > 4) { // Mandatory but example always doesn't include, so considered as optional
							unhVO.setAssoc_assign_cd(msgId[4]);
						}

						// Optional field
						if (segmentSz > 2) {
							// common access reference
							String[] commonAccRef = dataList.get(2);
							elementSz = commonAccRef.length;
							if (elementSz == 0) {
								log.info("No data for COMMON ACCESS REFERENCE (Optional value)");
							} else {
								unhVO.setCommon_access_ref(commonAccRef[0]);
							}

							// status of the transfer
							String[] seqTransf = dataList.get(3);
							elementSz = seqTransf.length;
							if (elementSz > 0) {
								if (elementSz > 1) {
									unhVO.setFirst_and_last_transfer(seqTransf[1]);
								}
								if (isNumeric(seqTransf[0])) {
									unhVO.setSeq_of_transfer(Integer.parseInt(seqTransf[0]));
								}
							} else {
								log.info("No data for STATUS OF THE TRANSFER (Optional value)");
							}
						}
						if (!unhVO.getMsg_type().equalsIgnoreCase("CUSCAR") 
								|| (!unhVO.getMsg_release_nbr().equalsIgnoreCase("11A")) && !unhVO.getMsg_release_nbr().equalsIgnoreCase("11B")) {
							String err = ConstantUtil.NON_CUSCAR_ERR + unhVO.getMsg_type() + ConstantUtil.NON_CUSCAR_ERR_1 + unhVO.getMsg_release_nbr();
							log.error(err);
							throw new BusinessException(err);
						}
						intVO.setUnh(unhVO);
						nonGroup = true;
						break;

					case "BGM":
						FTZBGMVO bgmVO = new FTZBGMVO();
						// check segment length
						if (!checkFieldLength(field, "main", segmentSz)) {
							log.error("File is non-EDIFACT/EDI type. Data insufficient. Segment: " + segment);
//							throw new BusinessException(ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + "Segment: " + segment);
						}
						// document/message name
						String[] docMsgNm = dataList.get(0);
						elementSz = docMsgNm.length;
						if (!checkFieldLength(ConstantUtil.BGM_DOC_MSG_NM, "segment", elementSz)) {
							log.error("File data issue: no adequate value for: C002 DOCUMENT/MESSAGE NAME. Segment: " + segment);
							throw new BusinessException(
									ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + ConstantUtil.BGM_DOC_MSG_NM + ". Segment: " + segment);
						}
						bgmVO.setDoc_nm_cd(docMsgNm[0]);
						if (elementSz > 1) {
							if (elementSz > 3) {
								bgmVO.setDoc_nm(docMsgNm[3]);
							} else {
								log.info("No data for Document name code (Optional value)");
							}
							if (elementSz > 2) {
								bgmVO.setCd_list_resp_agency_cd(docMsgNm[2]);
							} else {
								log.info("No data for Code list responsible agency code (Optional value)");
							}
							bgmVO.setCd_list_id_cd(docMsgNm[1]);
						} else {
							log.info("No data for Code list identification code (Optional value)");
						}
						// document/message identification
						String[] docMsgId = dataList.get(1);
						elementSz = docMsgId.length;
						if (!checkFieldLength(ConstantUtil.BGM_DOC_MSG_ID, "segment", elementSz)) {
							log.error("File data issue: no adequate value for: C002 DOCUMENT/MESSAGE NAME. Segment: " + segment);
							throw new BusinessException(
									ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + ConstantUtil.BGM_DOC_MSG_ID + ". Segment: " + segment);
						}
						bgmVO.setDoc_id(docMsgId[0]);
						if (elementSz > 1) {
							if (elementSz > 2) {
								bgmVO.setRev_id(docMsgId[2]);
							} else {
								log.info("No data for Revision identifier (Optional value)");
							}
							bgmVO.setVer_id(docMsgId[1]);
						} else {
							log.info("No data for Version identifier (Optional value)");
						}
						// message function code
						if (dataList.size() > 2) {
							String[] msgFuncCd = dataList.get(2);
							elementSz = msgFuncCd.length;
							if (!checkFieldLength(ConstantUtil.BGM_MSG_FUNC_CD, "segment", elementSz)) {
								log.error("File data issue: no adequate value for: 1225 MESSAGE FUNCTION CODE. Segment: " + segment);
								throw new BusinessException(
										ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + ConstantUtil.BGM_MSG_FUNC_CD + ". Segment: " + segment);
							}
							bgmVO.setMsg_func_cd(msgFuncCd[0]);
							// response type code (optional)
							if (segmentSz > 3) {
								String[] respTypeCd = dataList.get(3);
								elementSz = respTypeCd.length;
								if (elementSz > 0) {
									bgmVO.setResp_type_cd(respTypeCd[0]);
								} else {
									log.info("No data for RESPONSE TYPE CODE (Optional value)");
								}
							} else {
								log.info("No data for RESPONSE TYPE CODE (Optional value)");
							}
						}
						intVO.setBgm(bgmVO);
						nonGroup = true;
						break;

					case "DTM":
						FTZDTMVO dtmVO = new FTZDTMVO();
						// check segment length
						if (!checkFieldLength(field, "main", segmentSz)) {
							log.error("File is non-EDIFACT/EDI type. Data insufficient. Segment: " + segment);
							throw new BusinessException(ConstantUtil.INVALID_DATA_FORMAT + field + ". Segment: " + segment);
						}
						String[] dtp1 = dataList.get(0);
						elementSz = dtp1.length;
						if (!checkFieldLength(ConstantUtil.DTM_DTP, "segment", elementSz)) {
							log.error("File data issue: no adequate value for: C507 DATE/TIME/PERIOD. Segment: " + segment);
							throw new BusinessException(ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + ConstantUtil.DTM_DTP + ". Segment: " + segment);
						}
						dtmVO.setDtp_func_cd_qual(dtp1[0]);
						dtmVO.setDtp_value(dtp1[1]);
						dtmVO.setDtp_fmt_cd(dtp1[2]);
						listDtmVO.add(dtmVO);
						nonGroup = true;
						break;

					case "UNT":
						FTZUNTVO untVO = new FTZUNTVO();
						// check if previous group is not 14
						if (!prevGroup.equalsIgnoreCase("14")) {
							log.error("Group 14 missing!");
							throw new BusinessException(ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + " Group 14");
						}
						// check if previous group is 14
						if (prevGroup.equalsIgnoreCase("14")) {
							g14VO.setHan(listG14HAN);
							g14VO.setFtx(listG14FTX);
							g14VO.setMea(listG14MEA);
							g14VO.setSgp(listG14SGP);
							g14VO.setDgs(listG14DGS);
							g14VO.setPci(listG14PCI);
							listGroup14.add(g14VO);
							intVO.setGroup14(listGroup14);
						}
						// check segment length
						if (!checkFieldLength(field, "main", segmentSz)) {
							log.error("File is non-EDIFACT/EDI type. Data insufficient. Segment: " + segment);
							throw new BusinessException(ConstantUtil.INVALID_DATA_FORMAT + field + ". Segment: " + segment);
						}
						String[] noSgmntMsg = dataList.get(0);
						elementSz = noSgmntMsg.length;
						if (!checkFieldLength(ConstantUtil.UNT_NBR_SGMNT_IN_MSG, "segment", elementSz)) {
							log.error("File data issue: no adequate value for: 0074 NUMBER OF SEGMENTS IN THE MESSAGE. Segment: " + segment);
							throw new BusinessException(
									ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + ConstantUtil.UNT_NBR_SGMNT_IN_MSG + ". Segment: " + segment);
						}
						if (isNumeric(noSgmntMsg[0])) {
							untVO.setNbr_of_segments_in_msg(Integer.parseInt(noSgmntMsg[0]));
						}
						String[] msgRefNbrUnt = dataList.get(1);
						elementSz = msgRefNbrUnt.length;
						if (!checkFieldLength(ConstantUtil.UNT_MSG_REF_NBR, "segment", elementSz)) {
							log.error("File data issue: no adequate value for: 0062 MESSAGE REFERENCE NUMBER. Segment: " + segment);
							throw new BusinessException(
									ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + ConstantUtil.UNT_MSG_REF_NBR + ". Segment: " + segment);
						}
						untVO.setMsg_ref_nbr(msgRefNbrUnt[0]);
						intVO.setUnt(untVO);
						nonGroup = true;
						prevField = "UNT";
						break;
					case "UNZ":
						FTZUNZVO unzVO = new FTZUNZVO();
						// check segment length
						if (!checkFieldLength(field, "main", segmentSz)) {
							log.error("File is non-EDIFACT/EDI type. Data insufficient. Segment: " + segment);
							throw new BusinessException(ConstantUtil.INVALID_DATA_FORMAT + field + ". Segment: " + segment);
						}
						String[] icc = dataList.get(0);
						elementSz = icc.length;
						if (!checkFieldLength(ConstantUtil.UNZ_INTCHG_CTRL_CNT, "segment", elementSz)) {
							log.error("File data issue: no adequate value for: 0036 INTERCHANGE CONTROL COUNT. Segment: " + segment);
							throw new BusinessException(
									ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + ConstantUtil.UNZ_INTCHG_CTRL_CNT + ". Segment: " + segment);
						}
						if (isNumeric(icc[0])) {
							unzVO.setInterchange_ctrl_cnt(Integer.parseInt(icc[0]));
						}
						String[] icrUnz = dataList.get(1);
						elementSz = icrUnz.length;
						if (!checkFieldLength(ConstantUtil.UNZ_INTCHG_CTRL_REF, "segment", elementSz)) {
							log.error("File data issue: no adequate value for: 0020 INTERCHANGE CONTROL REFERENCE. Segment: " + segment);
							throw new BusinessException(
									ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + ConstantUtil.UNZ_INTCHG_CTRL_REF + ". Segment: " + segment);
						}
						unzVO.setInterchange_ctrl_ref(icrUnz[0]);
						intVO.setUnz(unzVO);
						nonGroup = true;
						intVoList.add(intVO);
						intVO = new FTZInterchangeVO();
						break;
					default:
						break;
					}
				}

				boolean processed = false;
				if (!nonGroup) {
					// Group 4
					if ((field.equalsIgnoreCase("TDT") && (prevGroup.equalsIgnoreCase("")))
							|| (field.equalsIgnoreCase("TDT") && (prevField.equalsIgnoreCase("TDT") || prevField.equalsIgnoreCase("LOC")))
							|| (field.equalsIgnoreCase("LOC") && (prevField.equalsIgnoreCase("TDT") || prevField.equalsIgnoreCase("LOC")) && prevGroup.equalsIgnoreCase("4"))) {
						if (prevField.equalsIgnoreCase("DTM")) {
							intVO.setDtm(listDtmVO);
						}
						group = "4";
						if (prevGroup.equalsIgnoreCase("4")) {
							if (field.equalsIgnoreCase("TDT")) {
								if (listG4LOC.size() > 0) {
									g4VO.setLoc(listG4LOC);
								}
								listGroup4.add(g4VO);
								g4VO = new FTZGroup4VO(); // reset
								listG4LOC = new ArrayList<>();
							}
						} else {
							g4VO = new FTZGroup4VO(); // reset
							listG4LOC = new ArrayList<>();
						}
						processed = true;
					}
					// special case for DTM after G4 TDT or LOC
					if ((prevGroup.equalsIgnoreCase("4")
							&& (prevField.equalsIgnoreCase("TDT") || prevField.equalsIgnoreCase("LOC")))
							&& field.equalsIgnoreCase("DTM")) {
						group = "4";
					}

					// Group 5
					if ((prevGroup.equalsIgnoreCase("4") && field.equalsIgnoreCase("EQD"))
							|| (prevField.equalsIgnoreCase("SEL") || prevField.equalsIgnoreCase("EQD"))
									&& (field.equalsIgnoreCase("EQD") || field.equalsIgnoreCase("SEL"))) {
						group = "5";
						// check if previous group is not 4
						if (!prevGroup.equalsIgnoreCase("4") && !prevGroup.equalsIgnoreCase("5")) {
							log.error("Group 4 missing!");
							throw new BusinessException(ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + " Group 4");
						}
						// check if previous group is 4
						if (prevGroup.equalsIgnoreCase("4")) {
							if (listG4LOC.size() > 0) {
								g4VO.setLoc(listG4LOC);
							}
							listGroup4.add(g4VO);
							intVO.setGroup4(listGroup4);
							g5VO = new FTZGroup5VO(); // reset
							listG5SEL = new ArrayList<>();
						}
						if (prevGroup.equalsIgnoreCase("5")) {
							if (field.equalsIgnoreCase("EQD")) {
								if (listG5SEL.size() > 0) {
									g5VO.setSel(listG5SEL);
								}
								listGroup5.add(g5VO);
								g5VO = new FTZGroup5VO(); // reset
								listG5SEL = new ArrayList<>();
							}
						}
						processed = true;
					}

					// Group 7
					if ((prevGroup.equalsIgnoreCase("5") && field.equalsIgnoreCase("CNI"))
							|| (prevField.equalsIgnoreCase("CNI") && field.equalsIgnoreCase("CNI"))
							|| (prevGroup.equalsIgnoreCase("14") && field.equalsIgnoreCase("CNI"))) { // add previous
																										// group Group
																										// 14 if
																										// multiple BL
						group = "7";
						// check if previous group is not 5
						if (!prevGroup.equalsIgnoreCase("5") && !prevGroup.equalsIgnoreCase("7")
								&& !prevGroup.equalsIgnoreCase("14")) {
							log.error("Group 5 missing!");
							throw new BusinessException(ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + " Group 5");
						}
						// check if previous group is 5
						if (prevGroup.equalsIgnoreCase("5")) {
							if (listG5SEL.size() > 0) {
								g5VO.setSel(listG5SEL);
							}
							listGroup5.add(g5VO);
							intVO.setGroup5(listGroup5);
							g7VO = new FTZGroup7VO(); // reset
						} else if (prevGroup.equalsIgnoreCase("7")) {
							if (field.equalsIgnoreCase("CNI")) {
								listGroup7.add(g7VO);
								g7VO = new FTZGroup7VO(); // reset
							}
						}
						// check if previous group is 14
						else if (prevGroup.equalsIgnoreCase("14")) { // if multiple BL
							g14VO.setHan(listG14HAN);
							g14VO.setFtx(listG14FTX);
							g14VO.setMea(listG14MEA);
							g14VO.setSgp(listG14SGP);
							g14VO.setDgs(listG14DGS);
							g14VO.setPci(listG14PCI);
							listGroup14.add(g14VO);
//							intVO.setGroup14(listGroup14);
//							intVoList.add(intVO);
//							listGroup7 = new ArrayList<>();
//							listGroup8 = new ArrayList<>();
//							listGroup11 = new ArrayList<>();
//							listGroup14 = new ArrayList<>();
//							listG5SEL = new ArrayList<>();
//							listGroup7 = new ArrayList<>();
							g7VO = new FTZGroup7VO(); // reset
						}
						processed = true;
					}

					// Group 8
					if ((prevGroup.equalsIgnoreCase("7") && field.equalsIgnoreCase("RFF"))
							|| (prevGroup.equalsIgnoreCase("8") && prevField.equalsIgnoreCase("RFF")
									&& field.equalsIgnoreCase("LOC"))
							|| (prevGroup.equalsIgnoreCase("8") && prevField.equalsIgnoreCase("LOC")
									&& field.equalsIgnoreCase("LOC"))
							|| (prevGroup.equalsIgnoreCase("8") && prevField.equalsIgnoreCase("RFF")
									&& field.equalsIgnoreCase("GEI"))
							|| (prevGroup.equalsIgnoreCase("8") && prevField.equalsIgnoreCase("LOC")
									&& field.equalsIgnoreCase("GEI"))
							|| (prevGroup.equalsIgnoreCase("8") && prevField.equalsIgnoreCase("GEI")
									&& field.equalsIgnoreCase("GEI"))
							|| (prevGroup.equalsIgnoreCase("8") && prevField.equalsIgnoreCase("GEI")
									&& field.equalsIgnoreCase("RFF"))) {
						group = "8";
						// check if previous group is not 7
						if (!prevGroup.equalsIgnoreCase("7") && !prevGroup.equalsIgnoreCase("8")) {
							log.error("Group 7 missing!");
							throw new BusinessException(ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + " Group 7");
						}
						// check if previous group is 7
						if (prevGroup.equalsIgnoreCase("7")) {
							listGroup7.add(g7VO);
							intVO.setGroup7(listGroup7);
							g8VO = new FTZGroup8VO(); // reset
							listG8LOC = new ArrayList<>();
							listG8GEI = new ArrayList<>();
						}
						if (prevGroup.equalsIgnoreCase("8")) {
							if (field.equalsIgnoreCase("RFF")) {
								g8VO.setLoc(listG8LOC);
								g8VO.setGei(listG8GEI);
								listGroup8.add(g8VO);
								g8VO = new FTZGroup8VO(); // reset
								listG8LOC = new ArrayList<>();
								listG8GEI = new ArrayList<>();
							}
						}
						processed = true;
					}

					// Group 11
					if ((prevGroup.equalsIgnoreCase("8") && field.equalsIgnoreCase("NAD"))
							|| prevField.equalsIgnoreCase("NAD") && field.equalsIgnoreCase("NAD")) {
						group = "11";
						// check if previous group is not 8
						log.info("prevGroup: " + prevGroup);
						if (!prevGroup.equalsIgnoreCase("8") && !prevGroup.equalsIgnoreCase("11")) {
							log.error("Group 8 missing!");
							throw new BusinessException(ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + " Group 8");
						}
						// check if previous group is 8
						if (prevGroup.equalsIgnoreCase("8")) {
							g8VO.setLoc(listG8LOC);
							g8VO.setGei(listG8GEI);
							listGroup8.add(g8VO);
							intVO.setGroup8(listGroup8);
							g11VO = new FTZGroup11VO();
						}
						if (prevGroup.equalsIgnoreCase("11")) {
							listGroup11.add(g11VO);
							nadMap.put(currBl + "_" + currPtyQual, g11VO);
							g11VO = new FTZGroup11VO();
						}
						processed = true;
					}

					// Group 14
					if ((prevGroup.equalsIgnoreCase("11") && field.equalsIgnoreCase("GID"))
							|| ((prevField.equalsIgnoreCase("CST") || prevField.equalsIgnoreCase("PCI")
									|| prevField.equalsIgnoreCase("DGS") || prevField.equalsIgnoreCase("SGP"))
									&& field.equalsIgnoreCase("GID"))
							|| ((prevField.equalsIgnoreCase("GID") || prevField.equalsIgnoreCase("HAN"))
									&& field.equalsIgnoreCase("HAN"))
							|| ((prevField.equalsIgnoreCase("GID") || prevField.equalsIgnoreCase("HAN")
									|| prevField.equalsIgnoreCase("FTX")) && field.equalsIgnoreCase("FTX"))
							|| ((prevField.equalsIgnoreCase("FTX") || prevField.equalsIgnoreCase("MEA"))
									&& field.equalsIgnoreCase("MEA"))
							|| ((prevField.equalsIgnoreCase("MEA") || prevField.equalsIgnoreCase("SGP"))
									&& field.equalsIgnoreCase("SGP"))
							|| ((prevField.equalsIgnoreCase("SGP") || prevField.equalsIgnoreCase("DGS"))
									&& field.equalsIgnoreCase("DGS"))
							|| ((prevField.equalsIgnoreCase("SGP") || prevField.equalsIgnoreCase("DGS")
									|| prevField.equalsIgnoreCase("PCI")) && field.equalsIgnoreCase("PCI"))
							|| ((prevField.equalsIgnoreCase("SGP") || prevField.equalsIgnoreCase("DGS")
									|| prevField.equalsIgnoreCase("PCI")) && field.equalsIgnoreCase("CST"))) {
						group = "14";
						// check if previous group is not 11
						if (!prevGroup.equalsIgnoreCase("11") && !prevGroup.equalsIgnoreCase("14")) {
							log.error("Group 11 missing!");
							throw new BusinessException(ConstantUtil.CUSCAR_DATA_NOT_ENOUGH + " Group 11");
						}
						// check if previous group is 11
						if (prevGroup.equalsIgnoreCase("11")) {
							listGroup11.add(g11VO);
							nadMap.put(currBl + "_" + currPtyQual, g11VO);
							intVO.setGroup11(listGroup11);
							intVO.setGroup11Map(nadMap);
							g14VO = new FTZGroup14VO(); // reset
							listG14HAN = new ArrayList<>();
							listG14FTX = new ArrayList<>();
							listG14MEA = new ArrayList<>();
							listG14SGP = new ArrayList<>();
							listG14DGS = new ArrayList<>();
							listG14PCI = new ArrayList<>();
						}
						if (prevGroup.equalsIgnoreCase("14")) {
							if (field.equalsIgnoreCase("GID")) {
								g14VO.setHan(listG14HAN);
								g14VO.setFtx(listG14FTX);
								g14VO.setMea(listG14MEA);
								g14VO.setSgp(listG14SGP);
								g14VO.setDgs(listG14DGS);
								g14VO.setPci(listG14PCI);
								listGroup14.add(g14VO);
								g14VO = new FTZGroup14VO(); // reset
								listG14HAN = new ArrayList<>();
								listG14FTX = new ArrayList<>();
								listG14MEA = new ArrayList<>();
								listG14SGP = new ArrayList<>();
								listG14DGS = new ArrayList<>();
								listG14PCI = new ArrayList<>();
							}
						}
						processed = true;
					}

					int segmentSz = 0;
					switch (group) {
					case "4":
						if ("TDT".equalsIgnoreCase(field)) {
							// TDT - DETAILS OF TRANSPORT
							FTZG4TDTVO g4TdtVo = new FTZG4TDTVO();
							segmentSz = dataList.size();
							
							log.info("dataList value: " + dataList);
							// Transport Stage Code Qualifier
							String[] tptStgCdQual = dataList.size()>0 ? dataList.get(0) : null;
							int elementSz = tptStgCdQual == null ? 0 : tptStgCdQual.length;	
							if(elementSz > 0) {
								g4TdtVo.setTpt_stage_cd_qual(tptStgCdQual[0]);
							}
														
							// Conveyance Reference Number
							String[] convRefNbr = dataList.size()>1 ?  dataList.get(1) : null;
							elementSz = convRefNbr == null ? 0 : convRefNbr.length;
							
							if(elementSz > 0) {
								g4TdtVo.setConv_ref_nbr(convRefNbr[0]);
							}
							// Mode of Transport
							String[] mot = dataList.size()>2 ? dataList.get(2) : null;
							elementSz = mot == null ? 0 :mot.length;
							if (elementSz > 0) {
								g4TdtVo.setTpt_mode_nm_cd(mot[0]);
							}
							if (elementSz > 1) { // optional
								g4TdtVo.setTpt_mode_nm(mot[1]);
							}
							// Transport Means - optional
							String[] tptMeans = dataList.size()>3 ? dataList.get(3) : null;
							elementSz = tptMeans == null ? 0 : tptMeans.length;
							if (elementSz > 0) {
								g4TdtVo.setTpt_means_desc_cd(tptMeans[0]);
								if (elementSz > 1) {
									g4TdtVo.setTpt_means_desc(tptMeans[1]);
								}
							}
							// Carrier - optional
							String[] carrier = dataList.size()>4 ? dataList.get(4) : null;
							elementSz = carrier == null ? 0 : carrier.length;
							if (elementSz > 0) {
								g4TdtVo.setCarrier_id(carrier[0]);
								if (elementSz > 1) {
									g4TdtVo.setCd_list_id_cd(carrier[1]);
								}
								if (elementSz > 2) {
									g4TdtVo.setCd_list_resp_agency_cd(carrier[2]);
								}
								if (elementSz > 3) {
									g4TdtVo.setCarrier_nm(carrier[3]);
								}
							}
							// Transit Direction Indicator Code - optional
							String[] transIndCd = dataList.size()>5 ? dataList.get(5) : null;
							elementSz = transIndCd == null ? 0 : transIndCd.length;
							if (elementSz > 0) {
								g4TdtVo.setTransit_dir_ind_cd(transIndCd[0]);
							}
							// Excess Transportation Information - optional
							String[] xcessTptInfo = dataList.size()>6 ? dataList.get(6) : null;
							elementSz = xcessTptInfo == null ? 0 : xcessTptInfo.length;
							if (elementSz > 0) {
								g4TdtVo.setExcess_tpt_reason_cdd(xcessTptInfo[0]);
								if (elementSz > 1) {
									g4TdtVo.setExcess_tpt_resp_cdd(xcessTptInfo[1]);
								}
								if (elementSz > 2) {
									g4TdtVo.setCust_auth_nbr(xcessTptInfo[2]);
								}
							}
							// Transport Identification
							String[] tptId = dataList.size()>7 ? dataList.get(7) : null;
							elementSz = tptId == null ? 0 : tptId.length;
							if (elementSz > 0) {
								g4TdtVo.setTpt_means_id_nm_id(tptId[0]); // optional
							}
							if (elementSz > 1) {
								g4TdtVo.setTpt_id_cd_list_id_cd(tptId[1]); // optional
							}
							if (elementSz > 2) {
								g4TdtVo.setTpt_id_cd_list_resp_agency_cd(tptId[2]); // optional
							}
							if (elementSz > 3) {
								g4TdtVo.setTpt_means_id_nm(tptId[3]); // mandatory
							}
							if (elementSz > 4) { // optional
								g4TdtVo.setNat_means_tpt_cdd(tptId[4]);
							}
							// Transport Ownership, coded - optional
							if (segmentSz > 8) {
								String[] tptOwnCdd = dataList.get(8);
								elementSz = tptOwnCdd == null ? 0 : tptOwnCdd.length;
								if (elementSz > 0) {
									g4TdtVo.setTpt_ownership_cdd(tptOwnCdd[0]);
								}
							}

							g4VO.setTdt(g4TdtVo);
						} else if ("DTM".equalsIgnoreCase(field)) {
							// skip
							field = "LOC"; // set field to LOC & do nothing
						} else {
							// LOC - PLACE/LOCATION IDENTIFICATION
							FTZG4LOCVO g4LocVo = new FTZG4LOCVO();
							segmentSz = dataList.size();
							
							// Location Function Code Qualifier
							String[] locFuncCdQual = dataList.size()>0 ? dataList.get(0) : null;
							int elementSz = locFuncCdQual == null ? 0 : locFuncCdQual.length;
							
							if(elementSz > 0) {
								g4LocVo.setLoc_func_cd_qual(locFuncCdQual[0]);
							}
							// Location Identification
							String[] locId = dataList.size()>1 ?  dataList.get(1) : null;
							elementSz = locId == null ? 0 : locId.length;
							if(elementSz > 0) {
								g4LocVo.setLoc_nm_cd(locId[0]);
							}
							if (elementSz > 1) { // optional
								g4LocVo.setCd_list_id_cd(locId[1]);
							}
							if (elementSz > 2) { // optional
								g4LocVo.setCd_list_resp_agency_cd(locId[2]);
							}
							if (elementSz > 3) { // optional
								g4LocVo.setLoc_nm(locId[3]);
							}
							if (segmentSz > 2) {
								// Related Location One Identification - optional
								String[] relLocOneId = dataList.size()>2 ? dataList.get(2) : null;
								elementSz = relLocOneId == null ? 0 : relLocOneId.length;
								if (elementSz > 0) {
									g4LocVo.setRel_plc_loc_one(relLocOneId[0]);
								}
								if (elementSz > 1) {
									g4LocVo.setRl_one_cd_list_id_cd(relLocOneId[1]);
								}
								if (elementSz > 2) {
									g4LocVo.setRl_one_cd_list_resp_agency_cd(relLocOneId[2]);
								}
								if (elementSz > 3) {
									g4LocVo.setRel_plc_loc_one(relLocOneId[3]);
								}
							}
							if (segmentSz > 3) {
								// Related Location Two Identification - optional
								String[] relLocTwoId = dataList.size()>3 ? dataList.get(3) : null;
								elementSz = relLocTwoId == null ? 0 : relLocTwoId.length;
								if (elementSz > 0) {
									g4LocVo.setRel_plc_loc_two(relLocTwoId[0]);
								}
								if (elementSz > 1) {
									g4LocVo.setRl_two_cd_list_id_cd(relLocTwoId[1]);
								}
								if (elementSz > 2) {
									g4LocVo.setRl_two_cd_list_resp_agency_cd(relLocTwoId[2]);
								}
								if (elementSz > 3) {
									g4LocVo.setRel_plc_loc_two(relLocTwoId[3]);
								}
							}
							if (segmentSz > 4) {
								// Relation, Coded - optional
								String[] relCdd = dataList.size()>4 ? dataList.get(4) : null;
								elementSz = relCdd == null ? 0 : relCdd.length;
								if (elementSz > 0) {
									g4LocVo.setRel_cdd(relCdd[0]);
								}
							}
							listG4LOC.add(g4LocVo);
						}
//					groupFields.put("G4" + field, value);
						break;
					case "5":
						if ("EQD".equalsIgnoreCase(field)) {
							// EQD - Equipment Details
							FTZG5EQD g5EqdVO = new FTZG5EQD();
							segmentSz = dataList.size();
							
							// Equipment Qualifier
							String[] eqpQual = dataList.size()>0 ?  dataList.get(0) : null;
							int elementSz = eqpQual == null ? 0 :  eqpQual.length;
							if(elementSz > 0) {
								g5EqdVO.setEqp_qual(eqpQual[0]);
							}
							// Equipment Identification
							String[] eqpId = dataList.size()>1 ?  dataList.get(1) : null;
							elementSz = eqpId == null ? 0 : eqpId.length;
							if (eqpId.length > 0) {
								g5EqdVO.setEqp_id_nbr(eqpId[0]);
							}
							if (eqpId.length > 1) { // optional
								g5EqdVO.setCd_list_qual(eqpId[1]);
							}
							if (eqpId.length > 2) { // optional
								g5EqdVO.setCd_list_resp_agency_cdd(eqpId[2]);
							}
							if (eqpId.length > 3) { // optional
								g5EqdVO.setCntry_cdd(eqpId[3]);
							}
							// Equipment Size and Type
							String[] eqpSzTyp = dataList.size()>2 ? dataList.get(2) : null;
							elementSz = eqpSzTyp == null ? 0 : eqpSzTyp.length;
							if (eqpSzTyp.length > 0) {
								g5EqdVO.setEqp_sz_type_id(eqpSzTyp[0]);
							}
							if (eqpSzTyp.length > 1) { // optional
								g5EqdVO.setSz_type_cd_list_qual(eqpSzTyp[1]);
							}
							if (eqpSzTyp.length > 2) { // optional
								g5EqdVO.setSz_type_cd_list_resp_agency_cdd(eqpSzTyp[2]);
							}
							if (eqpSzTyp.length > 3) { // optional
								g5EqdVO.setEqp_sz_type(eqpSzTyp[3]);
							}
							// Equipment Supplier, Coded - optional
							String[] eqpSupp = dataList.size()>3 ? dataList.get(3) : null;
							if (eqpSupp.length > 0) {
								g5EqdVO.setEqp_suppl_cdd(eqpSupp[0]);
							}
							// Equipment Status, Coded - optional
							String[] eqpStatus = dataList.size()>4 ? dataList.get(4) : null;
							if (eqpStatus.length > 0) {
								g5EqdVO.setEqp_status_cdd(eqpStatus[0]);
							}
							// Full/Empty Indicator, Coded
							String[] fiIndCdd = dataList.size()>5 ? dataList.get(5) : null;
							elementSz = fiIndCdd == null ? 0 :  fiIndCdd.length;
							if(elementSz > 0) {
								g5EqdVO.setFull_empty_ind_cdd(fiIndCdd[0]);
							}
							g5VO.setEqd(g5EqdVO);
						} else {
							// SEL Sel Number - optional
							FTZG5SELVO g5SelVo = new FTZG5SELVO();
							segmentSz = dataList.size();
							
							// Seal Number
							String[] sealNbr = dataList.size()>0 ? dataList.get(0) : null;
							int elementSz = sealNbr == null ? 0 : sealNbr.length;
							if(elementSz > 0) {
								g5SelVo.setSeal_nbr(sealNbr[0]);
							}
							// Seal Issuer - optional
							if (segmentSz > 1) {
								String[] sealIssuer = dataList.size()>1 ?  dataList.get(1) : null;
								elementSz = sealIssuer == null ? 0 : sealIssuer.length;
								if (elementSz > 0) {
									g5SelVo.setSealing_party_cdd(sealIssuer[0]);
								}
								if (elementSz > 1) {
									g5SelVo.setCd_list_qual(sealIssuer[1]);
								}
								if (elementSz > 2) {
									g5SelVo.setCd_list_resp_agency_cdd(sealIssuer[2]);
								}
								if (elementSz > 3) {
									g5SelVo.setSealing_party(sealIssuer[3]);
								}
							}
							if (segmentSz > 2) {
								// Seal Condition, Coded - optional
								String[] sealCond = dataList.size()>2 ? dataList.get(2) : null;
								elementSz = sealCond == null ? 0 : sealCond.length;
								if (elementSz > 0) {
									g5SelVo.setSeal_cond_cdd(sealCond[0]);
								}
							}
							if (segmentSz > 3) {
								// Identity Number Range - optional
								String[] idNbrRange = dataList.size()>1 ?  dataList.get(1) : null;
								elementSz = idNbrRange == null ? 0 : idNbrRange.length;
								if (elementSz > 0) {
									g5SelVo.setObj_id_1(idNbrRange[0]);
								}
								if (elementSz > 1) {
									g5SelVo.setObj_id_2(idNbrRange[1]);
								}
							}
							listG5SEL.add(g5SelVo);
						}
						break;
					case "7":
						// CNI Consignment Information
						FTZG7CNIVO g7CniVO = new FTZG7CNIVO();
						segmentSz = dataList.size();
						
						// Consolidation Item Number
						String[] consItemNbr = dataList.size()>0 ? dataList.get(0) : null;
						int elementSz = consItemNbr == null ? 0 : consItemNbr.length;
						if(elementSz > 0) {
							g7CniVO.setCons_item_nbr(consItemNbr[0]);
						}
//						blGroup.put("count", consItemNbr[0]);
						// Document / Message Details
						String[] docMsgDetails = dataList.size() >1 ?  dataList.get(1) : null;						
						g7CniVO.setDoc_id(docMsgDetails != null ? docMsgDetails[0] : null);
//						blGroup.put("blNumber", docMsgDetails[0]);
						currBl = g7CniVO.getDoc_id(); // get current BL nbr
						g7VO.setCni(g7CniVO);
						break;
					case "8":
						if ("RFF".equalsIgnoreCase(field)) {
							// RFF REFERENCE
							FTZGroup8RFF g8RffVO = new FTZGroup8RFF();
							segmentSz = dataList.size();
							
							// REFERENCE
							String[] ref = dataList.size()>0 ? dataList.get(0) : null;
							elementSz = ref == null ? 0 : ref.length;
							if (elementSz > 0) {
								g8RffVO.setRef_func_cd_qual(ref[0]);
							}
							if (elementSz > 1) {
								g8RffVO.setRef_id(ref[1]);
							}
							if (elementSz > 2) {
								g8RffVO.setLine_nbr(ref[2]);
							}
							if (elementSz > 3) {
								g8RffVO.setVer_id(ref[3]);
							}
							if (elementSz > 4) {
								g8RffVO.setRev_id(ref[4]);
							}
							g8VO.setRff(g8RffVO);
//							blGroup.put("rff", g8RffVO);
						} else if ("LOC".equalsIgnoreCase(field)) {
							// LOC - PLACE/LOCATION IDENTIFICATION - OPTIONAL
							FTZGroup8LOCVO g8LocVo = new FTZGroup8LOCVO();
							segmentSz = dataList.size();
							
							// Location Function Code Qualifier
							String[] locFuncCdQual = dataList.size()>0 ? dataList.get(0) : null;
							elementSz = locFuncCdQual == null ? 0 : locFuncCdQual.length;
							if(elementSz > 0) {
								g8LocVo.setLoc_func_cd_qual(locFuncCdQual[0]);
							}
							
							// Location Identification
							String[] locId = dataList.size()>1 ?  dataList.get(1) : null;
							elementSz = locId == null ? 0 : locId.length;
							
							if (elementSz > 0) {
								g8LocVo.setLoc_nm_cd(locId[0]);
							}
							if (elementSz > 1) { // optional
								g8LocVo.setCd_list_id_cd(locId[1]);
							}
							if (elementSz > 2) { // optional
								g8LocVo.setCd_list_resp_agency_cd(locId[2]);
							}
							if (elementSz > 3) { // optional
								g8LocVo.setLoc_nm(locId[3]);
							}
							if (segmentSz > 2) {
								// Related Location One Identification - optional
								String[] relLocOneId = dataList.size()>2 ? dataList.get(2) : null;
								elementSz = relLocOneId == null ? 0 : relLocOneId.length;
								if (elementSz > 0) {
									g8LocVo.setRel_plc_loc_one(relLocOneId[0]);
								}
								if (elementSz > 1) {
									g8LocVo.setRl_one_cd_list_id_cd(relLocOneId[1]);
								}
								if (elementSz > 2) {
									g8LocVo.setRl_one_cd_list_resp_agency_cd(relLocOneId[2]);
								}
								if (elementSz > 3) {
									g8LocVo.setRel_plc_loc_one(relLocOneId[3]);
								}
							}
							if (segmentSz > 3) {
								// Related Location Two Identification - optional
								String[] relLocTwoId = dataList.size()>3 ? dataList.get(3) : null;
								elementSz = relLocTwoId == null ? 0 : relLocTwoId.length;
								if (elementSz > 0) {
									g8LocVo.setRel_plc_loc_two(relLocTwoId[0]);
								}
								if (elementSz > 1) {
									g8LocVo.setRl_two_cd_list_id_cd(relLocTwoId[1]);
								}
								if (elementSz > 2) {
									g8LocVo.setRl_two_cd_list_resp_agency_cd(relLocTwoId[2]);
								}
								if (elementSz > 3) {
									g8LocVo.setRel_plc_loc_two(relLocTwoId[3]);
								}
							}
							if (segmentSz > 4) {
								// Relation, Coded - optional
								String[] relCdd = dataList.size()>4 ? dataList.get(4) : null;
								elementSz = relCdd == null ? 0 : relCdd.length;
								if (elementSz > 0) {
									g8LocVo.setRel_cdd(relCdd[0]);
								}
							}
							listG8LOC.add(g8LocVo);
//							blGroup.put("loc", g8LocVo);
						} else {
							// GEI - PROCESSING INFORMATION
							FTZGroup8GEIVO g8GeiVO = new FTZGroup8GEIVO();
							segmentSz = dataList.size();
							
							// Processing Information Code Qualifier
							String[] procInfoCdQual = dataList.size()>0 ? dataList.get(0) : null;
							elementSz = procInfoCdQual == null ? 0 : procInfoCdQual.length;
							if(elementSz > 0) {
								g8GeiVO.setProc_info_cd_qual(procInfoCdQual[0]);
							}
							// Processing indicator
							String[] procId = dataList.size()>1 ?  dataList.get(1) : null;
							elementSz = procId == null ? 0 : procId.length;
							
							g8GeiVO.setProc_ind_desc_cd(elementSz > 0 ? procId[0] : null);
							if (CommonUtil.deNull(g8GeiVO.getProc_ind_desc_cd()).equalsIgnoreCase("28")) {
								if (elementSz > 3) {
									g8GeiVO.setProc_ind_desc(procId[3]);
								}
							}
							listG8GEI.add(g8GeiVO);
						}
						break;
					case "11":
						// NAD - NAME AND ADDRESS
						FTZGroup11NADVO g11NadVO = new FTZGroup11NADVO();
						segmentSz = dataList.size();
						
						// Party Qualifier
						String[] ptyQual = dataList.size()>0 ? dataList.get(0) : null;
						elementSz = ptyQual == null ? 0 : ptyQual.length;
						if(elementSz > 0) {
							currPtyQual = ptyQual[0];
							g11NadVO.setParty_qual(currPtyQual);
						}
						// Party Identification Details - optional
						String[] ptyIdDet = dataList.size()>1 ?  dataList.get(1) : null;
						elementSz = ptyIdDet == null ? 0 : ptyIdDet.length;
						if (elementSz > 0) {
							g11NadVO.setParty_id(ptyIdDet[0]);
						}
						if (elementSz > 1) {
							g11NadVO.setCd_list_id_cd(ptyIdDet[1]);
						}
						if (elementSz > 2) {
							g11NadVO.setCd_list_resp_agency_cdd(ptyIdDet[2]);
						}
						// Name and Address
						String[] nmAddr = dataList.size()>2 ? dataList.get(2) : null;
						elementSz = nmAddr == null ? 0 : nmAddr.length;
						if(elementSz > 0) {
							g11NadVO.setNm_addr_desc_1(nmAddr[0]);
						}
						if (elementSz > 1) {
							g11NadVO.setNm_addr_desc_2(nmAddr[1]);
						}
						if (elementSz > 2) {
							g11NadVO.setNm_addr_desc_3(nmAddr[2]);
						}
						if (elementSz > 3) {
							g11NadVO.setNm_addr_desc_4(nmAddr[3]);
						}
						if (elementSz > 4) {
							g11NadVO.setNm_addr_desc_5(nmAddr[4]);
						}
						if (segmentSz > 3) {
							// Party Name - mandatory for CN,CZ,NI & FW
							String[] ptyNm = dataList.size()>3 ? dataList.get(3) : null;
							elementSz = ptyNm == null ? 0 : ptyNm.length;
							String partyQual = g11NadVO.getParty_qual();
							if (partyQual.equalsIgnoreCase("CN") || partyQual.equalsIgnoreCase("CZ")
									|| partyQual.equalsIgnoreCase("NI") || partyQual.equalsIgnoreCase("FW")) {
								if (!checkFieldLength(ConstantUtil.NAD_PARTY_NM, "segment", elementSz)) {
									String partyId = g11NadVO.getParty_id();
									String partyName = customDetailsRepo.getPartyName(partyId); 
									
									g11NadVO.setParty_name_1(partyName);
								} else {
									if(elementSz > 0) {
										g11NadVO.setParty_name_1(ptyNm[0]);
									}
									if (elementSz > 1) {
										g11NadVO.setParty_name_2(ptyNm[1]);
									}
									if (elementSz > 2) {
										g11NadVO.setParty_name_3(ptyNm[2]);
									}
									if (elementSz > 3) {
										g11NadVO.setParty_name_4(ptyNm[3]);
									}
									if (elementSz > 4) {
										g11NadVO.setParty_name_5(ptyNm[4]);
									}
									if (elementSz > 5) {
										g11NadVO.setParty_name_fmt_cdd(ptyNm[5]);
									}
								}
							} else {
								if (elementSz > 0) {
									g11NadVO.setParty_name_1(ptyNm[0]);
								}
								if (elementSz > 1) {
									g11NadVO.setParty_name_2(ptyNm[1]);
								}
								if (elementSz > 2) {
									g11NadVO.setParty_name_3(ptyNm[2]);
								}
								if (elementSz > 3) {
									g11NadVO.setParty_name_4(ptyNm[3]);
								}
								if (elementSz > 4) {
									g11NadVO.setParty_name_5(ptyNm[4]);
								}
								if (elementSz > 5) {
									g11NadVO.setParty_name_fmt_cdd(ptyNm[5]);
								}
							}
						}
						if (segmentSz > 4) {
							// Street - optional
							String[] street = dataList.size()>4 ? dataList.get(4) : null;
							elementSz = street == null ? 0 : street.length;
							if (elementSz > 0) {
								g11NadVO.setStreet_nbr_pbox_1(street[0]);
							}
							if (elementSz > 1) {
								g11NadVO.setStreet_nbr_pbox_2(street[1]);
							}
							if (elementSz > 2) {
								g11NadVO.setStreet_nbr_pbox_3(street[2]);
							}
						}
						if (segmentSz > 5) {
							// City Name - optional
							String[] cityNm = dataList.size()>5 ? dataList.get(5) : null;
							elementSz = cityNm == null ? 0 : cityNm.length;
							if (elementSz > 0) {
								g11NadVO.setCity_nm(cityNm[0]);
							}
						}
						if (segmentSz > 6) {
							// Country Sub-Entity Details - optional
							String[] ctrySubEntDet = dataList.size()>5 ? dataList.get(5) : null;
							elementSz = ctrySubEntDet == null ? 0 : ctrySubEntDet.length;
							if (elementSz > 0) {
								g11NadVO.setCtry_sub_entity_nm_cd(ctrySubEntDet[0]);
							}
							if (elementSz > 1) {
								g11NadVO.setCd_list_id_cd(ctrySubEntDet[1]);
							}
							if (elementSz > 2) {
								g11NadVO.setCd_list_resp_agency_cd(ctrySubEntDet[2]);
							}
							if (elementSz > 3) {
								g11NadVO.setCtry_sub_entity_nm(ctrySubEntDet[3]);
							}
						}
						if (segmentSz > 7) {
							// Postal Identification - optional
							String[] postId = dataList.size()>6 ? dataList.get(6) : null;
							elementSz = postId == null ? 0 : postId.length;
							if (elementSz > 0) {
								g11NadVO.setPostal_id(postId[0]);
							}
						}
						if (segmentSz > 8) {
							// Country, Coded - optional
							String[] ctryCdd = dataList.get(7);
							elementSz = ctryCdd == null ? 0 : ctryCdd.length;
							if (elementSz > 0) {
								g11NadVO.setCtry_cdd(ctryCdd[0]);
							}
						}
						g11VO.setNad(g11NadVO);
						break;
					case "14":
						switch (field) {
						case "GID":
							// GID - GOODS ITEM DETAILS
							FTZGroup14GIDVO g14GidVO = new FTZGroup14GIDVO();
							segmentSz = dataList.size();
							
							// Goods Item Number
							String[] gdItemNbr = dataList.size()>0 ? dataList.get(0) : null;
							elementSz = (gdItemNbr == null || gdItemNbr.length == 0 || 
						             (gdItemNbr.length == 1 && CommonUtil.deNull(gdItemNbr[0]).isEmpty())) 
						            ? 0 : gdItemNbr.length;
							if(elementSz > 0) {
								Double gdsItemNbr = convertNumeric(gdItemNbr[0]);
								g14GidVO.setGoods_item_nbr(gdsItemNbr.intValue());
							}
							// Number & Types of Packages
							String[] nbrTypPkg = dataList.size()>1 ?  dataList.get(1) : null;
							elementSz = nbrTypPkg == null ? 0 : nbrTypPkg.length;
							if (elementSz > 0) {
								double qty = convertNumeric(nbrTypPkg[0]);
								g14GidVO.setPkg_qty(qty);
							}
							if (elementSz > 1) {
								g14GidVO.setPkg_type_desc_cd(nbrTypPkg[1]);
							}
							if (elementSz > 2) {
								g14GidVO.setCd_list_id_cd(nbrTypPkg[2]);
							}
							if (elementSz > 3) {
								g14GidVO.setCd_list_resp_agency_cd(nbrTypPkg[3]);
							}
							if (elementSz > 4) {
								g14GidVO.setType_pkg(nbrTypPkg[4]);
							}
							if (elementSz > 5) {
								g14GidVO.setPkg_related_desc_cd(nbrTypPkg[5]);
							}
							g14VO.setGid(g14GidVO);
							break;
						case "HAN": // optional
							// HAN - Handling Instructions
							FTZGroup14HANVO g14HanVO = new FTZGroup14HANVO();
							// Handling Instruction
							String[] hdlgInstr = dataList.size()>0 ? dataList.get(0) : null;
							elementSz = hdlgInstr == null ? 0 : hdlgInstr.length;
							if (elementSz > 0) {
								g14HanVO.setHandl_instr_cdd(hdlgInstr[0]);
							}
							if (elementSz > 1) {
								g14HanVO.setCd_list_qual(hdlgInstr[1]);
							}
							if (elementSz > 2) {
								g14HanVO.setCd_list_resp_agency_cdd(hdlgInstr[2]);
							}
							if (elementSz > 3) {
								g14HanVO.setHandl_instr(hdlgInstr[3]);
							}
							if (segmentSz > 1) {
								// Hazardous Material - optional
								String[] hazard = dataList.size()>1 ?  dataList.get(1) : null;
								elementSz = hazard == null ? 0 : hazard.length;
								if (elementSz > 0) {
									g14HanVO.setHzrd_mtrl_class_cd_id(hazard[0]);
								}
								if (elementSz > 1) {
									g14HanVO.setHzrd_cd_list_qual(hazard[1]);
								}
								if (elementSz > 2) {
									g14HanVO.setHzrd_cd_list_resp_agency_cdd(hazard[2]);
								}
								if (elementSz > 3) {
									g14HanVO.setHzrd_mtrl_cat_nm(hazard[3]);
								}
							}
							listG14HAN.add(g14HanVO);
							break;
						case "FTX":
							// FTX - FREE TEXT
							FTZGroup14FTXVO g14FtxVO = new FTZGroup14FTXVO();
							segmentSz = dataList.size();
							
							// Text Subject Code Qualifier
							String[] txtSubjQual = dataList.size()>0 ? dataList.get(0) : null;
							elementSz = txtSubjQual == null ? 0 : txtSubjQual.length;
							if(elementSz > 0) {
								g14FtxVO.setTxt_subj_cd_qual(txtSubjQual[0]);
							}
							// Text Function, Coded - optional
							String[] txtFuncQual = dataList.size()>1 ?  dataList.get(1) : null;
							elementSz = txtFuncQual == null ? 0 : txtFuncQual.length;
							if (elementSz > 0) {
								g14FtxVO.setTxt_func_cdd(txtFuncQual[0]);
							}
							// Text Reference - optional
							String[] txtRef = dataList.size()>2 ? dataList.get(2) : null;
							elementSz = txtRef == null ? 0 : txtRef.length;
							if (elementSz > 0) {
								g14FtxVO.setFree_txt_fmt_cd(txtRef[0]);
							}
							if (elementSz > 1) {
								g14FtxVO.setCd_list_id_cd(txtRef[1]);
							}
							if (elementSz > 2) {
								g14FtxVO.setCd_list_resp_agency_cd(txtRef[2]);
							}
							// Text Literal
							String[] txtLit = dataList.size()>3 ? dataList.get(3) : null;
							elementSz = txtLit  == null ? 0 : txtLit.length;
							
							if (elementSz > 0) {
								g14FtxVO.setFree_txt_val(txtLit[0]);
							}
							if (segmentSz > 4) {
								// Language Name Code - optional
								String[] langNmCd = dataList.size()>4 ? dataList.get(4) : null;
								elementSz =  langNmCd == null ? 0 : langNmCd.length;
								if (elementSz > 0) {
									g14FtxVO.setLang_nm_cd(langNmCd[0]);
								}
							}
							if (segmentSz > 5) {
								// Free Text Format Code - optional
								String[] ftxFmt = dataList.size()>5 ? dataList.get(5) : null;
								elementSz = ftxFmt == null ? 0 : ftxFmt.length;
								if (elementSz > 0) {
									g14FtxVO.setFree_txt_fmt_cd(ftxFmt[0]);
								}
							}
							listG14FTX.add(g14FtxVO);
							break;
						case "MEA":
							// MEA - Measurement
							FTZGroup14MEAVO g14MeaVO = new FTZGroup14MEAVO();
							segmentSz = dataList.size();
							
							// Measurement Application Qualifier
							String[] meaApplQual = dataList.size()>0 ? dataList.get(0) : null;
							elementSz = meaApplQual == null ? 0 : meaApplQual.length;
							if(elementSz > 0) {
								g14MeaVO.setMeasure_appl_qual(meaApplQual[0]);
							}
							// Measurement Details
							String[] meaDtl = dataList.size()>1 ?  dataList.get(1) : null;
							elementSz = meaDtl == null ? 0 : meaDtl.length;
							if (elementSz > 0) {
								g14MeaVO.setMeasure_dim_cdd(meaDtl[0]);
							}
							if (elementSz > 1) {
								g14MeaVO.setMeasure_sign_cdd(meaDtl[1]);
							}
							if (elementSz > 2) {
								g14MeaVO.setMeasure_attr_cdd(meaDtl[2]);
							}
							if (elementSz > 3) {
								g14MeaVO.setMeasure_attr(meaDtl[3]);
							}
							// Value/Range
							String[] valRange = dataList.size()>2 ? dataList.get(2) : null;
							elementSz = valRange == null ? 0 : valRange.length;
							if(elementSz > 0) {
								g14MeaVO.setMeasure_unit_qual(valRange[0]);
								g14MeaVO.setMeasure_val(-1); // set to negative, will be replaced if measure value exist
							}
							if(elementSz > 1) {
								double measureVal = Double.valueOf(valRange[1]);
								g14MeaVO.setMeasure_val(measureVal);
							}
							if (elementSz > 2) {
								double min = convertNumeric(valRange[2]);
								g14MeaVO.setRange_min(min);
							}
							if (elementSz > 3) {
								double max = convertNumeric(valRange[3]);
								g14MeaVO.setRange_max(max);
							}
							if (elementSz > 4) {
								Double dig = convertNumeric(valRange[4]);
								g14MeaVO.setSign_digit(dig.intValue());
							}
							if (segmentSz > 3) {
								// Surface/Layer Indicator, Coded - optional
								String[] surfLyrId = dataList.size()>3 ? dataList.get(3) : null;
								elementSz = surfLyrId == null ? 0 : surfLyrId.length;
								if (elementSz > 0) {
									g14MeaVO.setSurface_layer_ind_cdd(surfLyrId[0]);
								}
							}
							listG14MEA.add(g14MeaVO);
							break;
						case "SGP":
							// SGP - SPLIT GOODS PLACEMENT
							FTZGroup14SGPVO g14SgpVO = new FTZGroup14SGPVO();
							if (CommonUtility.deNull(g14SgpVO.getBlNbr()).equalsIgnoreCase("")) {
								g14SgpVO.setBlNbr(currBl);
							}
							segmentSz = dataList.size();
							
							// Equipment Identification
							String[] eqpId = dataList.size()>0 ? dataList.get(0) : null;
							elementSz = eqpId == null ? 0 : eqpId.length;
							if(elementSz > 0) {
								g14SgpVO.setEqp_idf(eqpId[0]);
							}
							if (elementSz > 1) {
								g14SgpVO.setCd_list_id_cd(eqpId[1]);
							}
							if (elementSz > 2) {
								g14SgpVO.setCd_list_resp_agency_cd(eqpId[2]);
							}
							if (elementSz > 3) {
								g14SgpVO.setCtry_id(eqpId[3]);
							}
							if (segmentSz > 1) {
								// Packing Quantity - optional
								String[] pckQty = dataList.size()>1 ?  dataList.get(1) : null;
								elementSz = pckQty == null ? 0 : pckQty.length;
								if (elementSz > 0) {
									Double pack = convertNumeric(pckQty[0]);
									g14SgpVO.setPkg_qty(pack.intValue());
								}
							}
							listG14SGP.add(g14SgpVO);
							break;
						case "DGS":
							// DGS - Dangerous Goods
							FTZGroup14DGSVO g14DgsVO = new FTZGroup14DGSVO();
							segmentSz = dataList.size();
							
							// Equipment Identification
							String[] dgsReg = dataList.size()>0 ? dataList.get(0) : null;
							elementSz = dgsReg == null ? 0 : dgsReg.length;
							if(elementSz > 0) {
								g14DgsVO.setDgs_reg_cdd(dgsReg[0]);
							}
							// Hazard Code
							String[] hzdCd = dataList.size()>1 ?  dataList.get(1) : null;
							elementSz = hzdCd == null ? 0 : hzdCd.length;
							if (elementSz > 0) {
								g14DgsVO.setHzrd_cd_id(hzdCd[0]);
							}
							if (elementSz > 1) {
								g14DgsVO.setHzrd_subs_item_pg_nbr(hzdCd[1]);
							}
							if (elementSz > 2) {
								g14DgsVO.setHzrd_cd_ver_nbr(hzdCd[2]);
							}
							// UNDG Information
							String[] undg = dataList.size()>2 ? dataList.get(2) : null;
							elementSz = undg == null ? 0 : undg.length;
							if(elementSz > 0) {
								Double undgNbr = convertNumeric(undg[0]);
								g14DgsVO.setUndg_nbr(undgNbr.intValue());
							}
							if (elementSz > 1) {
								g14DgsVO.setDgs_flash(undg[1]);
							}
							if (segmentSz > 3) {
								// Dangerous Goods Shipment Flashpoint - optional
								String[] dgsf = dataList.size()>3 ? dataList.get(3) : null;
								elementSz = dgsf == null ? 0 : dgsf.length;
								if (elementSz > 0) {
									Double shipFlash = convertNumeric(dgsf[0]);
									g14DgsVO.setShipment_flash(shipFlash);
								}
								if (elementSz > 1) {
									g14DgsVO.setMeasure_unit_qual(dgsf[1]);
								}
							}
							if (segmentSz > 4) {
								// Packing Group, Coded - optional
								String[] pkgGrp = dataList.size()>4 ? dataList.get(4) : null;
								elementSz = pkgGrp == null ? 0 : pkgGrp.length;
								if (elementSz > 0) {
									g14DgsVO.setPkg_group_cdd(pkgGrp[0]);
								}
							}
							if (segmentSz > 5) {
								// EMS Number - optional
								String[] ems = dataList.size()>5 ? dataList.get(5) : null;
								elementSz = ems == null ? 0 : ems.length;
								if (elementSz > 0) {
									Double emsNbr = convertNumeric(ems[0]);
									g14DgsVO.setEms_nbr(emsNbr.intValue());
								}
							}
							if (segmentSz > 6) {
								// MFAG - optional
								String[] mfag = dataList.size()>5 ? dataList.get(5) : null;
								elementSz = mfag == null ? 0 : mfag.length;
								if (elementSz > 0) {
									g14DgsVO.setMfag(mfag[0]);
								}
							}
							if (segmentSz > 7) {
								// Trem Card Number - optional
								String[] tcn = dataList.size()>5 ? dataList.get(5) : null;
								elementSz = tcn == null ? 0 : tcn.length;
								if (elementSz > 0) {
									g14DgsVO.setTrem_card_nbr(tcn[0]);
								}
							}
							if (segmentSz > 8) {
								// Hazard Identification - optional
								String[] hzdId = dataList.size()>6 ? dataList.get(6) : null;
								elementSz = hzdId == null ? 0 : hzdId.length;
								if (elementSz > 0) {
									g14DgsVO.setHzrd_id_nbr_upper(hzdId[0]);
								}
								if (elementSz > 1) {
									g14DgsVO.setSubs_id_nbr_lower(hzdId[1]);
								}
							}
							if (segmentSz > 9) {
								// Dangerous Goods Label - optional
								String[] dgsLbl = dataList.size()>7 ? dataList.get(7) : null;
								elementSz = dgsLbl == null ? 0 : dgsLbl.length;
								if (elementSz > 0) {
									g14DgsVO.setDgs_label(dgsLbl[0]);
								}
								if (elementSz > 1) {
									g14DgsVO.setDgs_label_mrk_1(dgsLbl[1]);
								}
								if (elementSz > 2) {
									g14DgsVO.setDgs_label_mrk_2(dgsLbl[2]);
								}
								if (elementSz > 3) {
									g14DgsVO.setDgs_label_mrk_3(dgsLbl[3]);
								}
							}
							if (segmentSz > 10) {
								// Packaging Instruction, Coded - optional
								String[] pkgInstr = dataList.size()>8 ? dataList.get(8) : null;
								elementSz = pkgInstr == null ? 0 : pkgInstr.length;
								if (elementSz > 0) {
									g14DgsVO.setPkg_instr_cdd(pkgInstr[0]);
								}
							}
							if (segmentSz > 11) {
								// Category of Means of Transport, Coded
								String[] catMot = dataList.size()>9 ? dataList.get(9) : null;
								elementSz = catMot == null ? 0 : catMot.length;
								if (elementSz > 0) {
									g14DgsVO.setCat_mot_cdd(catMot[0]);
								}
							}
							if (segmentSz > 12) {
								// Permission for Transport, Coded
								String[] pmsTpt = dataList.size()>10 ? dataList.get(10) : null;
								elementSz = pmsTpt == null ? 0 : pmsTpt.length;
								if (elementSz > 0) {
									g14DgsVO.setPermission_tpt_cdd(pmsTpt[0]);
								}
							}
							listG14DGS.add(g14DgsVO);
							break;
						case "PCI":
							// PCI - PACKAGE IDENTIFICATION
							FTZGroup14PCIVO g14PciVO = new FTZGroup14PCIVO();
							segmentSz = dataList.size();
							
							// Equipment Identification
							String[] markInstr = dataList.size()>0 ? dataList.get(0) : null;
							elementSz = markInstr == null ? 0 : markInstr.length;
							if(elementSz > 0) {
								g14PciVO.setMrk_instr_cd(markInstr[0]);
							}
							// Marks & Labels
							String[] markLbl = dataList.size()>1 ?  dataList.get(1) : null;
							elementSz = markLbl == null ? 0 : markLbl.length;
							if (elementSz > 0) {
								g14PciVO.setShpg_mrks_desc_1(markLbl[0]);
							}
							if (elementSz > 1) {
								g14PciVO.setShpg_mrks_desc_2(markLbl[1]);
							}
							if (elementSz > 2) {
								g14PciVO.setShpg_mrks_desc_3(markLbl[2]);
							}
							if (elementSz > 3) {
								g14PciVO.setShpg_mrks_desc_4(markLbl[3]);
							}
							if (elementSz > 4) {
								g14PciVO.setShpg_mrks_desc_5(markLbl[4]);
							}
							if (elementSz > 5) {
								g14PciVO.setShpg_mrks_desc_6(markLbl[5]);
							}
							if (elementSz > 6) {
								g14PciVO.setShpg_mrks_desc_7(markLbl[6]);
							}
							if (elementSz > 7) {
								g14PciVO.setShpg_mrks_desc_8(markLbl[7]);
							}
							if (elementSz > 8) {
								g14PciVO.setShpg_mrks_desc_9(markLbl[8]);
							}
							if (elementSz > 9) {
								g14PciVO.setShpg_mrks_desc_10(markLbl[9]);
							}
							if (segmentSz > 2) {
								// Container/Package Contents Indicator Code
								String[] ctrPkg = dataList.size()>2 ? dataList.get(2) : null;
								elementSz = ctrPkg == null ? 0 : ctrPkg.length;
								if (elementSz > 0) {
									g14PciVO.setCntr_pkg_content_ind_cd(ctrPkg[0]);
								}
							}
							if (segmentSz > 3) {
								// Type of Marking
								String[] typMrk = dataList.size()>3 ? dataList.get(3) : null;
								elementSz = typMrk == null ? 0 : typMrk.length;
								if (elementSz > 0) {
									g14PciVO.setType_of_marking(typMrk[0]);
								}
							}
							listG14PCI.add(g14PciVO);
							break;
						case "CST": // optional
							// CST - Customs Status of Goods
							FTZGroup14CSTVO g14CstVO = new FTZGroup14CSTVO();
							segmentSz = dataList.size();
							
							// Goods Item Number
							String[] gdsItmNbr = dataList.size()>0 ? dataList.get(0) : null;
							elementSz = gdsItmNbr  == null ? 0 : gdsItmNbr.length;
							if(elementSz > 0) {
								Double gdsItemNo = convertNumeric(gdsItmNbr[0]);
								g14CstVO.setGoods_item_nbr(gdsItemNo.intValue());
							}
							
							// Custom Identity Codes 1
							String[] cstId = dataList.size()>1 ?  dataList.get(1) : null;
							elementSz = cstId == null ? 0 : cstId.length;
							if (elementSz > 0) {
								g14CstVO.setCustoms_goods_id1(cstId[0]);
							}
							if (elementSz > 1) {
								g14CstVO.setCd_list_id_cd1(cstId[1]);
							}
							if (elementSz > 2) {
								g14CstVO.setCd_list_resp_agency_cd1(cstId[2]);
							}
							if (segmentSz > 2) {
								// Custom Identity Codes 2
								String[] cstId2 = dataList.size()>2 ? dataList.get(2) : null;
								elementSz = cstId2 == null ? 0 : cstId2.length;
								if (elementSz > 0) {
									g14CstVO.setCustoms_goods_id2(cstId2[0]);
								}
								if (elementSz > 1) {
									g14CstVO.setCd_list_id_cd2(cstId2[1]);
								}
								if (elementSz > 2) {
									g14CstVO.setCd_list_resp_agency_cd2(cstId2[2]);
								}
							}
							if (segmentSz > 3) {
								// Custom Identity Codes 3
								String[] cstId3 = dataList.size()>3 ? dataList.get(3) : null;
								elementSz = cstId3 == null ? 0 : cstId3.length;
								if(elementSz > 0) {
									g14CstVO.setCustoms_goods_id3(cstId3[0]);
								}
								if (elementSz > 1) {
									g14CstVO.setCd_list_id_cd3(cstId3[1]);
								}
								if (elementSz > 2) {
									g14CstVO.setCd_list_resp_agency_cd3(cstId3[2]);
								}
							}
							if (segmentSz > 4) {
								// Custom Identity Codes 4
								String[] cstId4 = dataList.size()>4 ? dataList.get(4) : null;
								elementSz = cstId4 == null ? 0 : cstId4.length;
								if(elementSz > 0) {
									g14CstVO.setCustoms_goods_id4(cstId4[0]);
								}
								if (elementSz > 1) {
									g14CstVO.setCd_list_id_cd4(cstId4[1]);
								}
								if (elementSz > 2) {
									g14CstVO.setCd_list_resp_agency_cd4(cstId4[2]);
								}
							}
							if (segmentSz > 5) {
								// Custom Identity Codes 5
								String[] cstId5 = dataList.size()>5 ? dataList.get(5) : null;
								elementSz = cstId5 == null ? 0 : cstId5.length;
								if(elementSz > 0) {
									g14CstVO.setCustoms_goods_id5(cstId5[0]);
								}
								if (elementSz > 1) {
									g14CstVO.setCd_list_id_cd5(cstId5[1]);
								}
								if (elementSz > 2) {
									g14CstVO.setCd_list_resp_agency_cd5(cstId5[2]);
								}
							}
							g14VO.setCst(g14CstVO);
							break;
						default:
							break;
						}
						break;
					default:
						break;
					}
				}

				log.info("processed: " + processed);
				if (processed) {
					prevField = field;
					prevGroup = group;
					nonGroup = false;
				}
			}
		} catch (BusinessException e) {
			log.error("Exception uploadFile: ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.error("Exception uploadFile: ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getParse. intVO: " + intVoList.toString());
		}
		return intVoList;
	}

	private boolean checkFieldLength(String field, String type, int size) throws BusinessException {
		try {
			if (type.equalsIgnoreCase("main")) {
				switch (field) {
				case ConstantUtil.UNB_MAIN:
					if (size < 5) {
						return false;
					}
					break;
				case ConstantUtil.UNH_MAIN:
				case ConstantUtil.CNI_MAIN:
				case ConstantUtil.GEI_MAIN:
				case ConstantUtil.GID_MAIN:
				case ConstantUtil.PCI_MAIN:
				case ConstantUtil.UNT_MAIN:
				case ConstantUtil.UNZ_MAIN:
					if (size < 2) {
						return false;
					}
					break;
				case ConstantUtil.BGM_MAIN:
				case ConstantUtil.NAD_MAIN:
				case ConstantUtil.MEA_MAIN:
				case ConstantUtil.DGS_MAIN:
					if (size < 3) {
						return false;
					}
					break;
				case ConstantUtil.TDT_MAIN:
					if (size < 8) {
						return false;
					}
					break;
				case ConstantUtil.EQD_MAIN:
					if (size < 6) {
						return false;
					}
					break;
				case ConstantUtil.RFF_MAIN:
				case ConstantUtil.HAN_MAIN:
				case ConstantUtil.SGP_MAIN:
					if (size < 1) {
						return false;
					}
					break;
				case ConstantUtil.FTX_MAIN:
					if (size < 4) {
						return false;
					}
					break;
				case ConstantUtil.DTM_MAIN:
				case ConstantUtil.LOC_MAIN:
				case ConstantUtil.SEL_MAIN:
				case ConstantUtil.CST_MAIN:
				default:
					break;
				}
			} else {
				switch (field) {
				case ConstantUtil.UNB_SYTX_ID:
				case ConstantUtil.UNB_DTP:
//				case "S010":
				case ConstantUtil.RFF_REF:
				case ConstantUtil.GID_NBR_TYPE_PKG:
				case ConstantUtil.MEA_VAL_RNG:
				case ConstantUtil.DGS_DGS_SHPMT_FPT:
					if (size < 2) {
						return false;
					}
					break;
				case ConstantUtil.UNB_INT_SEND:
				case ConstantUtil.UNB_INT_RCV:
				case ConstantUtil.UNB_ICR:
				case ConstantUtil.UNH_MSG_REF_NBR:
//				case "0068":
				case ConstantUtil.BGM_DOC_MSG_NM:
				case ConstantUtil.BGM_DOC_MSG_ID:
				case ConstantUtil.BGM_MSG_FUNC_CD:
//				case "4343":
				case ConstantUtil.TDT_TPT_STG_CD_QUAL:
				case ConstantUtil.TDT_CONVY_REF_NBR:
				case ConstantUtil.TDT_MOD_OF_TPT:
				case ConstantUtil.LOC_LOC_FUNC_CD_QUAL:
				case ConstantUtil.LOC_LOC_ID:
				case ConstantUtil.EQD_EQP_QUAL:
				case ConstantUtil.EQD_EQP_ID:
				case ConstantUtil.EQD_EQP_SZ_TYP:
				case ConstantUtil.EQD_FULL_EMPTY_IND_CDD:
				case ConstantUtil.SEL_SEAL_NBR:
				case ConstantUtil.CNI_CONS_ITEM_NBR:
				case ConstantUtil.CNI_DOC_MSG_DTL:
				case ConstantUtil.GEI_PROC_INFO_CD_QUAL:
				case ConstantUtil.GEI_PROC_IND:
				case ConstantUtil.NAD_PARTY_QUAL:
				case ConstantUtil.NAD_NM_ADDR:
				case ConstantUtil.NAD_PARTY_NM:
				case ConstantUtil.GID_GDS_ITEM_NBR:
				case ConstantUtil.FTX_TXT_SUBJ_CD_QUAL:
				case ConstantUtil.FTX_TXT_REF:
				case ConstantUtil.FTX_TXT_LIT:
				case ConstantUtil.MEA_MSR_APPL_QUAL:
				case ConstantUtil.MEA_MSR_DTL:
				case ConstantUtil.DGS_DGS_REG:
				case ConstantUtil.DGS_HZD_CD:
				case ConstantUtil.DGS_UNDG_INFO:
				case ConstantUtil.PCI_MRK_INSTR_CD:
				case ConstantUtil.PCI_MRK_LBL:
				case ConstantUtil.CST_CST_ID_CD:
				case ConstantUtil.UNT_NBR_SGMNT_IN_MSG:
				case ConstantUtil.UNZ_INTCHG_CTRL_CNT:
					if (size < 1) {
						return false;
					}
					break;
				case ConstantUtil.UNH_MSG_ID:
				case ConstantUtil.TDT_TPT_ID:
				case ConstantUtil.HAN_HDLG_INSTR:
				case ConstantUtil.HAN_HZRD_MTRL:
					if (size < 4) {
						return false;
					}
					break;
				case ConstantUtil.DTM_DTP:
					if (size < 3) {
						return false;
					}
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			log.error("Exception checkFieldLength: ", e);
			throw new BusinessException("M4201");
		}
		return true;
	}

	private boolean isNumeric(String data) throws BusinessException {
		boolean isNumeric = false;
		try {
			if (CommonUtility.deNull(data).trim().equalsIgnoreCase("")) {
				isNumeric = false;
			} else {
				isNumeric = data.matches("-?\\d+(\\.\\d+)?");
			}
		} catch (Exception e) {
			log.error("Exception isNumeric: ", e);
			throw new BusinessException("M4201");
		}
		return isNumeric;
	}

	private double convertNumeric(String value) throws BusinessException {
		double result = 0;
		try {
			boolean isNumeric = isNumeric(value);
			if (isNumeric) {
				result = Double.valueOf(value);
			} else {
				log.error("non-numeric value");
			}
		} catch (Exception e) {
			log.error("Exception checkNumeric: ", e);
			throw new BusinessException("M4201");
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public SummaryCuscar processData(List<FTZInterchangeVO> voList, CustomDetailsFileUploadDetails fileUploadDetails,
			String vvCd, String userId, String companyCode) throws BusinessException {
		List<CustomDetails> customDetailsList = new ArrayList<CustomDetails>();
		SummaryCuscar SummaryCuscarObj = new SummaryCuscar();
		SummaryCuscar summ = new SummaryCuscar();
		try {
			log.info("START processData");
			// Record in DB first for the uploaded file
			Long seq_id = customDetailsRepo.insertCustomCUSCAR(fileUploadDetails);
			SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmm");
			// check message function
			for (FTZInterchangeVO vo : voList) {
				List<String> blList = new ArrayList<>();
				List<Map<String, Object>> cntrList = new ArrayList<>();
				List<Map<String, Object>> valG8List = new ArrayList<>(); // g8
				List<Map<String, String>> partyInfoList = new ArrayList<>(); // g11
				List<Map<String, Object>> cntrInfo = new ArrayList<>(); // g14

				List<FTZGroup4VO> g4List = new ArrayList<>();
				List<FTZGroup5VO> g5List = new ArrayList<>();
				List<FTZGroup7VO> g7List = new ArrayList<>();
				List<FTZGroup8VO> g8List = new ArrayList<>();
//				List<FTZGroup11VO> g11List = new ArrayList<>();
				Map<String, FTZGroup11VO> nadMap = new LinkedHashMap<>();
				List<FTZGroup14VO> g14List = new ArrayList<>();
				CustomDetails customDetailsVO = new CustomDetails();
				FTZBGMVO bgm = vo.getBgm();
				String function = "";
				if (bgm == null) {
					function = "0"; // set as 0 (invalid)
				} else {
					function = CommonUtility.deNull(bgm.getMsg_func_cd());
				}
				customDetailsVO.setVv_cd(vvCd);
				switch (function) {
				case "3": // delete record
					customDetailsVO.setAction(ConstantUtil.action_delete);
					break;
				case "4": // update record
					customDetailsVO.setAction(ConstantUtil.action_update);
					break;
				case "9": // create record
					customDetailsVO.setAction(ConstantUtil.action_add);
					break;
				default:
					break;
				}

				// G4: vessel nm, voyage nbr, load port, disc port
				// supposedly to only have one record only
				g4List = vo.getGroup4();
				for (FTZGroup4VO g4 : g4List) {
					FTZG4TDTVO tdt = g4.getTdt();
					String vesselNm = tdt.getTpt_means_id_nm();
					String voyNbr = tdt.getConv_ref_nbr();
					customDetailsVO.setVessel_name(vesselNm);
					customDetailsVO.setVoyage_no(voyNbr);
					List<FTZG4LOCVO> loc = g4.getLoc();
					if (loc.size() > 0) {
						loc.forEach(x -> {
							String locType = x.getLoc_func_cd_qual();
							String locNm = x.getLoc_nm_cd();
							if (CommonUtility.deNull(locType).equalsIgnoreCase("11")) {
								customDetailsVO.setDis_port(locNm);
							} else if (CommonUtility.deNull(locType).equalsIgnoreCase("9")) {
								customDetailsVO.setOri_load_port(locNm);
							}
						});
					}
				}
				// G5: get cntr nbr, cntr status, iso, seal nbr (can have multiple per bl) // TO
				// BE PROCESSED LAST
				g5List = vo.getGroup5();
				int count = 0;
				for (FTZGroup5VO g5 : g5List) {
					List<String> sealNbrList = new ArrayList<>();
					Map<String, Object> map = new LinkedHashMap<>();
					FTZG5EQD eqd = g5.getEqd();
					String cntrNbr = eqd.getEqp_id_nbr();
					String cntrStatus = eqd.getFull_empty_ind_cdd();
					String iso = eqd.getEqp_sz_type_id();
					map.put("cntrNbr" + count, cntrNbr);
					map.put("cntrStatus" + count, cntrStatus);
					map.put("iso" + count, iso);
					List<FTZG5SELVO> sel = g5.getSel();
					if (sel.size() > 0) {
						sel.forEach(x -> {
							sealNbrList.add(x.getSeal_nbr());
						});
						map.put("sealNbr" + count, sealNbrList);
					}
					cntrList.add(map);
					count++;
				}
				// G7: get bl nbr (can have multiple per vessel)
				g7List = vo.getGroup7();
				for (FTZGroup7VO g7 : g7List) { // every item is a separate BL from this onwards
					FTZG7CNIVO cni = g7.getCni();
					blList.add(cni.getDoc_id());

				}
				// G8: master bl, instruction type, dest port, place of receipt, place of
				// delivery
				// (based on G7 count)
				g8List = vo.getGroup8();
				count = 0;
				for (FTZGroup8VO g8 : g8List) {
					Map<String, Object> map = new LinkedHashMap<>();
					// master bl
					FTZGroup8RFF rff = g8.getRff();
					String refFuncCd = rff.getRef_func_cd_qual();
					if ("MB".equalsIgnoreCase(refFuncCd)) {
						String masterBl = rff.getRef_id();
						map.put("masterBl" + count, masterBl);
					}
					List<FTZGroup8LOCVO> loc = g8.getLoc();
					if (loc.size() > 0) {
						for (int i = 0; i < loc.size(); i++) {
							String locFunCdQual = loc.get(i).getLoc_func_cd_qual();
							String locNm = loc.get(i).getLoc_nm_cd();
							switch (locFunCdQual) {
							case "7":
								// place of delivery
								map.put("plcDelivery" + count + ":" + i, locNm);
								break;
							case "9":
								// port of loading
								map.put("pLoad" + count + ":" + i, locNm);
								break;
							case "11":
								// port of discharge
								map.put("pDisc" + count + ":" + i, locNm);
								break;
							case "20":
								// destination
								map.put("pDest" + count + ":" + i, locNm);
								break;
							case "76":
								// oriLoadPort
								map.put("oriLoadPort" + count + ":" + i, locNm);
								break;
							case "88":
								// place of receipt
								map.put("plcReceipt" + count + ":" + i, locNm);
								break;
							default:
								break;
							}
						}
					}
					List<FTZGroup8GEIVO> gei = g8.getGei();
					if (gei.size() > 0) {
						for (int i = 0; i < gei.size(); i++) {
							String procInd = gei.get(i).getProc_ind_desc_cd();
							switch (CommonUtil.deNull(procInd)) {
							case "22":
								map.put("instructType" + count + ":" + i, "LE");
								break;
							case "23":
								map.put("instructType" + count + ":" + i, "LI");
								break;
							case "24":
								map.put("instructType" + count + ":" + i, "T");
								break;
							case "28":
								if (CommonUtility.deNull(gei.get(i).getProc_ind_desc()).trim().equalsIgnoreCase("TE")) {
									map.put("instructType" + count + ":" + i, "TE");
								} else {
									map.put("instructType" + count + ":" + i, "TS");
								}
								break;
							default:
								break;
							}
						}
					}
					valG8List.add(map);
					
					count++;
				}
				count = 0;
				// G11: consignee, shipper, notify party, freight forwarder, stevedore, cargo
				// agent
//				g11List = vo.getGroup11();
				// Store maps for each BL persistently across entries
				Map<String, Map<String, String>> blToMap = new LinkedHashMap<>();

				nadMap = vo.getGroup11Map();
				Iterator<Map.Entry<String, FTZGroup11VO>> iterator = nadMap.entrySet().iterator();

				while (iterator.hasNext()) {
				    Map.Entry<String, FTZGroup11VO> entry = iterator.next();
				    String key = entry.getKey();

				    for (String bl : blList) {
				        if (key.startsWith(CommonUtil.deNull(bl))) {
				            // Retrieve or create a map for this BL
				            Map<String, String> map = blToMap.computeIfAbsent(bl, k -> new LinkedHashMap<>());

				            FTZGroup11VO g11 = entry.getValue();
				            FTZGroup11NADVO nad = g11.getNad();

				            String partyQual = CommonUtility.deNull(nad.getParty_qual());
				            String uen = CommonUtility.deNull(nad.getParty_id());
				            String partyNm = CommonUtility.deNull(nad.getParty_name_1());

				            StringBuilder sb = new StringBuilder();
				            sb.append(CommonUtility.deNull(nad.getNm_addr_desc_1()).trim());
				            sb.append(CommonUtility.deNull(nad.getNm_addr_desc_2()).trim());
				            sb.append(CommonUtility.deNull(nad.getNm_addr_desc_3()).trim());
				            sb.append(CommonUtility.deNull(nad.getNm_addr_desc_4()).trim());
				            sb.append(CommonUtility.deNull(nad.getNm_addr_desc_5()).trim());
				            String nmAddr = sb.toString();

				            sb = new StringBuilder();
				            sb.append(CommonUtility.deNull(nad.getStreet_nbr_pbox_1()).trim());
				            sb.append(CommonUtility.deNull(nad.getStreet_nbr_pbox_2()).trim());
				            sb.append(CommonUtility.deNull(nad.getStreet_nbr_pbox_3()).trim());
				            String street = sb.toString();

				            String cityNm = nad.getCity_nm();
				            String postCd = nad.getPostal_id();
				            String subEnt = nad.getCtry_sub_entity_nm_cd();
				            String ctry = nad.getCtry_cdd();

				            sb = new StringBuilder();
				            sb.append(CommonUtility.deNull(nmAddr)).append(" ");
				            sb.append(CommonUtility.deNull(street)).append(" ");
				            sb.append(CommonUtility.deNull(cityNm)).append(" ");
				            sb.append(CommonUtility.deNull(postCd)).append(" ");
				            sb.append(CommonUtility.deNull(subEnt)).append(" ");
				            sb.append(CommonUtility.deNull(ctry));
				            String completeAddr = sb.toString();

				            // Ensure all party types for the same BL are added to the same map
				            switch (partyQual) {
				                case "AG": // Cargo agent/owner
				                    map.put("crgAgentNm" + count + "_" + bl, partyNm.trim());
				                    map.put("crgAgentUen" + count + "_" + bl, uen.trim());
				                    map.put("crgAgentAddr" + count + "_" + bl, completeAddr.trim());
				                    break;
				                case "CN": // Consignee
				                    map.put("consigneeNm" + count + "_" + bl, partyNm.trim());
				                    map.put("consigneeUen" + count + "_" + bl, uen.trim());
				                    map.put("consigneeAddr" + count + "_" + bl, completeAddr.trim());
				                    break;
				                case "CZ": // Shipper
				                    map.put("shipperNm" + count + "_" + bl, partyNm.trim());
				                    map.put("shipperUen" + count + "_" + bl, uen.trim());
				                    map.put("shipperAddr" + count + "_" + bl, completeAddr.trim());
				                    break;
				                case "NI": // Notify party
				                    map.put("notifyPtyNm" + count + "_" + bl, partyNm.trim());
				                    map.put("notifyPtyUen" + count + "_" + bl, uen.trim());
				                    map.put("notifyPtyAddr" + count + "_" + bl, completeAddr.trim());
				                    break;
				                case "FW": // Freight forwarder
				                    map.put("freightFwdNm" + count + "_" + bl, partyNm.trim());
				                    map.put("freightFwdUen" + count + "_" + bl, uen.trim());
				                    map.put("freightFwdAddr" + count + "_" + bl, completeAddr.trim());
				                    break;
				                case "HB": // Stevedore
				                    map.put("stevedoreNm" + count + "_" + bl, partyNm.trim());
				                    map.put("stevedoreUen" + count + "_" + bl, uen.trim());
				                    map.put("stevedoreAddr" + count + "_" + bl, completeAddr.trim());
				                    break;
				                default:
				                    break;
				            }
				        }
				    }
				}

				// Add all the collected maps to partyInfoList at the end, ensuring one map per BL
				partyInfoList.addAll(blToMap.values());


				// G14: type, hscode, quantity, weight, measurement, handling instruction, cargo
				// description, mark & no
				// dgind, imo class, undg nbr, flashpoint, package group, gross weight
				count = 0;
				g14List = vo.getGroup14();
				for (FTZGroup14VO g14 : g14List) {
					Map<String, Object> map = new LinkedHashMap<>();
					FTZGroup14GIDVO gid = g14.getGid();
					map.put("itemNbr", gid.getGoods_item_nbr());
					map.put("pkgType", gid.getPkg_type_desc_cd());
					FTZGroup14CSTVO cst = g14.getCst();
					map.put("hscode1" + count, cst.getCustoms_goods_id1());
					map.put("hscode2" + count, cst.getCustoms_goods_id2());
					map.put("hscode3" + count, cst.getCustoms_goods_id3());
					map.put("hscode4" + count, cst.getCustoms_goods_id4());
					map.put("hscode5" + count, cst.getCustoms_goods_id5());

					List<FTZGroup14SGPVO> sgpList = g14.getSgp();
					if (sgpList.size() > 0) {
						for (int i = 0; i < sgpList.size(); i++) {
							FTZGroup14SGPVO sgp = sgpList.get(i);
							map.put("cntrNbrr" + count + ":" + i, sgp.getEqp_idf()); // check in CNI & EQD
							map.put("pkgQty" + count + ":" + i, sgp.getPkg_qty());
							map.put("blNbr" + count + ":" + i, sgp.getBlNbr());
						}
					}
					List<FTZGroup14MEAVO> meaList = g14.getMea();
					if (meaList.size() > 0) {
						for (int i = 0; i < meaList.size(); i++) {
							FTZGroup14MEAVO mea = meaList.get(i);
							if (CommonUtility.deNull(mea.getMeasure_dim_cdd()).equalsIgnoreCase("AAB")) {
								// item gross weight
								map.put("grossWt" + count + ":" + i, mea.getMeasure_val());
							}
						}
					}
					List<FTZGroup14HANVO> hanList = g14.getHan();
					if (hanList.size() > 0) {
						for (int i = 0; i < hanList.size(); i++) {
							FTZGroup14HANVO han = hanList.get(i);
							map.put("handlingInstr" + count + ":" + i, han.getHandl_instr());
						}
					}
					List<FTZGroup14FTXVO> ftxList = g14.getFtx();
					if (ftxList.size() > 0) {
						for (int i = 0; i < ftxList.size(); i++) {
							FTZGroup14FTXVO ftx = ftxList.get(i);
							String subjectCd = CommonUtility.deNull(ftx.getTxt_subj_cd_qual());
							if (subjectCd.equalsIgnoreCase("AAA")) {
								map.put("cargoDesc" + count + ":" + i, ftx.getFree_txt_val());
							} else if (subjectCd.equalsIgnoreCase("AAS")) {
								map.put("blRemarks" + count + ":" + i, ftx.getFree_txt_val());
							}
						}
					}
					List<FTZGroup14PCIVO> pciList = g14.getPci();
					if (pciList.size() > 0) {
						for (int i = 0; i < pciList.size(); i++) {
							map.put("markNbr" + count + ":" + i, pciList.get(i).getShpg_mrks_desc_1());
						}
					}
					List<FTZGroup14DGSVO> dgsList = g14.getDgs();
					if (dgsList.size() > 0) {
						for (int i = 0; i < dgsList.size(); i++) {
							FTZGroup14DGSVO dgs = dgsList.get(i);
							map.put("dgsInd" + count + ":" + i, "Y");
							map.put("imoCls" + count + ":" + i, dgs.getHzrd_cd_id());
							map.put("undg" + count + ":" + i, dgs.getUndg_nbr());
							map.put("flashPts" + count + ":" + i, dgs.getShipment_flash());
							map.put("pkgGrp" + count + ":" + i, dgs.getPkg_group_cdd());
						}
					}
					cntrInfo.add(map);
					count++;
				}
				
				log.info("cntrInfo: " + cntrInfo);
				
				Map<String, Map<String, String>> mappedValuePartyLst = new LinkedHashMap<>();
				
				for (Map<String, String> partyInfo : partyInfoList) {
				    for (String key : partyInfo.keySet()) {
				        if (key.contains("_")) {
				            String blKey = key.substring(key.lastIndexOf("_") + 1);
				            if(blKey.equalsIgnoreCase("null")) {
				            	blKey = null;
				            }
				            mappedValuePartyLst.put(blKey, partyInfo);
				            break;
				        }
				    }
				}
				
				List<CustomDetails> temporary = new ArrayList<>();
				// assign based on bl nbr G7
				int blCount = blList.size();
				for (int j = 0; j < blCount; j++) {
					String blNbr = blList.get(j);
					String key = "";
					Object temp = "";
					CustomDetails customDetailsVO1 = new CustomDetails();
					BeanUtils.copyProperties(customDetailsVO, customDetailsVO1);
					customDetailsVO1.setBl_nbr(blNbr);
					// valG8List
					Map<String, ?> map = valG8List.get(j);
					for (Map.Entry<String, ?> entry : map.entrySet()) {
						key = entry.getKey();
						temp = entry.getValue();
						if (temp instanceof String) {
							if (key.startsWith("masterBl")) {
								customDetailsVO1.setMaster_bl_nbr((String) temp);
							} else if (key.startsWith("plcDelivery" + j)) {
								customDetailsVO1.setPlace_of_delivery_name((String) temp);
							} else if (key.startsWith("pLoad" + j)) {
								customDetailsVO1.setLoad_port((String) temp);
							} else if (key.startsWith("pDisc" + j)) {
								customDetailsVO1.setDis_port((String) temp);
							} else if (key.startsWith("pDest" + j)) {
								customDetailsVO1.setDest_port((String) temp);
							} else if (key.startsWith("oriLoadPort" + j)) {
								customDetailsVO1.setOri_load_port((String) temp);
							} else if (key.startsWith("plcReceipt" + j)) {
								customDetailsVO1.setPlace_of_receipt_name((String) temp);
							} else if (key.startsWith("instructType")) {
								customDetailsVO1.setInstruction_type((String) temp);
							}
						}
					}
					// partyInfoList
//						map = partyInfoList.get(j);
						map = mappedValuePartyLst.get(blNbr);
						for (Map.Entry<String, ?> entry : map.entrySet()) {
							key = entry.getKey();
							temp = entry.getValue();
							if (temp instanceof String) {
								if (key.startsWith("crgAgentNm")) {
									customDetailsVO1.setCargo_agent_name((String) temp);
								} else if (key.startsWith("crgAgentUen")) {
									customDetailsVO1.setCargo_agent_uen((String) temp);
								} else if (key.startsWith("crgAgentAddr")) {
									customDetailsVO1.setCargo_agent_address((String) temp);
								} else if (key.startsWith("consigneeNm")) {
									customDetailsVO1.setConsignee_name((String) temp);
								} else if (key.startsWith("consigneeUen")) {
									customDetailsVO1.setConsignee_uen((String) temp);
								} else if (key.startsWith("consigneeAddr")) {
									customDetailsVO1.setConsignee_address((String) temp);
								} else if (key.startsWith("shipperNm")) {
									customDetailsVO1.setShipper_name((String) temp);
								} else if (key.startsWith("shipperUen")) {
									customDetailsVO1.setShipper_uen((String) temp);
								} else if (key.startsWith("shipperAddr")) {
									customDetailsVO1.setShipper_address((String) temp);
								} else if (key.startsWith("notifyPtyNm")) {
									customDetailsVO1.setNotify_party_name((String) temp);
								} else if (key.startsWith("notifyPtyUen")) {
									customDetailsVO1.setNotify_party_uen((String) temp);
								} else if (key.startsWith("notifyPtyAddr")) {
									customDetailsVO1.setNotify_party_address((String) temp);
								} else if (key.startsWith("freightFwdNm")) {
									customDetailsVO1.setFreight_fowarder_name((String) temp);
								} else if (key.startsWith("freightFwdUen")) {
									customDetailsVO1.setFreight_fowarder_uen((String) temp);
								} else if (key.startsWith("freightFwdAddr")) {
									customDetailsVO1.setFreight_fowarder_address((String) temp);
								} else if (key.startsWith("stevedoreNm")) {
									customDetailsVO1.setStevedore_name((String) temp);
								} else if (key.startsWith("stevedoreUen")) {
									customDetailsVO1.setStevedore_uen((String) temp);
								} else if (key.startsWith("stevedoreAddr")) {
									customDetailsVO1.setStevedore_address((String) temp);
								}
							}
						}
						
					temporary.add(customDetailsVO1);
				}
				log.info("customDetailsList before: " + temporary);
				// cntrInfo
				for (int k = 0; k < cntrInfo.size(); k++) {
					log.info("cntr info size: " + cntrInfo.size());
					log.info("cntr list size: " + cntrList.size());
					Map<String, Object> map = cntrInfo.get(k);
					if (k < cntrList.size()) {
						Map<String, Object> cntrInf = cntrList.get(k);
						String blNbrValue = null;
					    for (String key : map.keySet()) {
					        if (key.startsWith("blNbr") && map.get(key) instanceof String) {
					            blNbrValue = (String) map.get(key);
					            break;
					        }
					    }
					    CustomDetails tempVO = new CustomDetails();
					    long cntRpt = 0;
				        final String finalBlNbrValue = CommonUtil.deNull(blNbrValue);
				        cntRpt = temporary.stream()  
				        	.filter(x -> finalBlNbrValue.equalsIgnoreCase( CommonUtil.deNull(x.getBl_nbr())))
					        .count(); // count instance of bl within
				        
				        tempVO = temporary.stream()
				            .filter(x -> finalBlNbrValue.equalsIgnoreCase( CommonUtil.deNull(x.getBl_nbr())))
				            .findFirst()
				            .orElse(null);
					    
					    
					    if (tempVO == null ) {
					    	log.info("Record based on BL number not found");
					        continue;
					    }
					    CustomDetails tempVO1 = new CustomDetails();
					    BeanUtils.copyProperties(tempVO, tempVO1);
					    
					    for (Map.Entry<String, ?> entry : cntrInf.entrySet()) {
					    	String key = entry.getKey();
							Object temp = entry.getValue();
							if (temp instanceof String) {
								if (key.startsWith("cntrStatus")) {
									if (((String) temp).equalsIgnoreCase("4")) {
										tempVO1.setCntr_status("E");
									} else {
										tempVO1.setCntr_status("F");
									}
								} else if (key.startsWith("iso")) {
									tempVO1.setIso((String) temp);
								}
							}
					    }
						
						for (Map.Entry<String, ?> entry : map.entrySet()) {
							String key = entry.getKey();
							Object temp = entry.getValue();
							if (temp instanceof String) {
								if (key.startsWith("itemNbr")) {
									tempVO1.setItem_no((String) temp);
								} else if (key.startsWith("pkgType")) {
									tempVO1.setPackage_type((String) temp);
								} else if (key.startsWith("hscode1")) {
									tempVO1.setHscode((String) temp);
								} else if (key.startsWith("cntrNbrr")) {
									tempVO1.setCntr_nbr((String) temp);
								} else if (key.startsWith("pkgQty")) {
									tempVO1.setPackage_quantity((String) temp);
								} else if (key.startsWith("grossWt")) {
									tempVO1.setGross_wt((String) temp);
									tempVO1.setWeight((String) temp);
								} else if (key.startsWith("handlingInstr")) {
									tempVO1.setHandling_instruction((String) temp);
								} else if (key.startsWith("cargoDesc")) {
									tempVO1.setCargo_description((String) temp);
								} else if (key.startsWith("blRemarks")) {
									tempVO1.setBl_nbr_remarks((String) temp);
								} else if (key.startsWith("markNbr")) {
									tempVO1.setMark_and_no((String) temp);
								} else if (key.startsWith("dgInd")) {
									if (CommonUtility.deNull((String) temp).trim().equalsIgnoreCase("") 
											|| CommonUtility.deNull((String) temp).trim().equalsIgnoreCase("null")) {
										tempVO1.setDg_ind("N");
									} else {
										tempVO1.setDg_ind((String) temp);
									}
								} else if (key.startsWith("imoCls")) {
									tempVO1.setImo_class((String) temp);
								} else if (key.startsWith("undg")) {
									tempVO1.setUndg_nbr((String) temp);
								} else if (key.startsWith("flashPts")) {
									tempVO1.setFlashpoint((String) temp);
								} else if (key.startsWith("pkgGrp")) {
									tempVO1.setPacking_grp((String) temp);
								}
							} else if (temp instanceof Integer) {
								Integer temp1 = (Integer) temp;
								if (key.startsWith("itemNbr")) {
									tempVO1.setItem_no(String.valueOf(temp1));
								} else if (key.startsWith("pkgQty")) {
									tempVO1.setPackage_quantity(String.valueOf(temp1));
								} else if (key.startsWith("grossWt")) {
									tempVO1.setGross_wt(String.valueOf(temp1));
									tempVO1.setWeight(String.valueOf(temp1));
								} else if (key.startsWith("flashPts")) {
									tempVO1.setFlashpoint(String.valueOf(temp1));
								} else if (key.startsWith("pkgGrp")) {
									tempVO1.setPacking_grp(String.valueOf(temp1));
								}
							} else if (temp instanceof Double) {
								Double temp1 = (Double) temp;
								if (key.startsWith("pkgQty")) {
									tempVO1.setPackage_quantity(String.valueOf(temp1));
								} else if (key.startsWith("grossWt")) {
									tempVO1.setGross_wt(String.valueOf(temp1));
									tempVO1.setWeight(String.valueOf(temp1));
								} else if (key.startsWith("flashPts")) {
									tempVO1.setFlashpoint(String.valueOf(temp1));
								} else if (key.startsWith("pkgGrp")) {
									tempVO1.setPacking_grp(String.valueOf(temp1));
								}
							}
						}
						// check IMO Class exist
						if (CommonUtility.deNull(tempVO1.getImo_class()).trim().equalsIgnoreCase("")) {
							tempVO1.setDg_ind("N");
						} else {
							tempVO1.setDg_ind("Y");
						}
						customDetailsList.add(tempVO1);
						if (cntRpt > 1) { // to remove blnbr record if multiple instance within list to prevent wrong value assigned
							temporary.remove(tempVO);
						}
					} else {
						break;
					}
				}
				
				log.info("blList: " + blList);
				log.info("cntrList: " + cntrList);
				log.info("valG8List: " + valG8List);
				log.info("cntrInfoList: " + cntrInfo);
			}
			log.info("customDetailsList: " + customDetailsList);

			// validate
			PageDetails vesselCallDetails = customDetailsRepo.getVesselCallDetails(vvCd);
			List<String> portListDropdownList = manifestRepo.getPortListForExcelProcessing(false);
			List<CustomDetailsUploadConfig> header = customDetailsRepo.getTemplateHeader();
			List<String> packagingType = customDetailsRepo.getSelectionList(ConstantUtil.CUSTOM_PACKAGE_TYPE);
			List<String> imoClass = customDetailsRepo.getSelectionList(ConstantUtil.CUSTOM_IMO_CLASS);
			List<String> consigneeDropdownList = customDetailsRepo.getSelectionList(ConstantUtil.CUSTOM_CONSIGNEE);
			List<Comments> commentsList = new ArrayList<Comments>();
			Map<String,String> detailsMap = new HashMap<String,String>();
			boolean deleteflag = false;
			boolean cntrExistinJPOM = false;
			// validate
			for (int i = 0; i < customDetailsList.size(); i++) {
				detailsMap = new HashMap<String,String>();
				commentsList = new ArrayList<Comments>();
				CustomDetails customDetailsVO = customDetailsList.get(i);
				if (!CommonUtil.deNull(customDetailsVO.getCntr_nbr()).isEmpty() && (CommonUtil
						.deNull(customDetailsVO.getAction()).equalsIgnoreCase(ConstantUtil.action_delete))) {
					int exist = customDetailsRepo.customDetailIsExist(customDetailsVO.getCntr_nbr(), vvCd,
							customDetailsVO.getInstruction_type());
					if (exist == 0) {
						Comments comments = new Comments();
						comments.setKey(ConstantUtil.bills_of_landing_no);
						comments.setMessage(ConstantUtil.ErrorMsg_cntrNbrNotExist + "-" + vvCd);
						commentsList.add(comments);
						customDetailsVO.setMessage(ConstantUtil.error);
						customDetailsVO.setErrorInfo(commentsList);
						continue;
					} else {
						deleteflag = true;
					}
				}
				for (CustomDetailsUploadConfig customDetailsUploadConfig : header) {
					if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.action)) {
						String cellData = customDetailsVO.getAction();

						if (CommonUtil.deNull(cellData) == "") {
							if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
									.equalsIgnoreCase(ConstantUtil.yes)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(customDetailsUploadConfig.getAttr_name() + ConstantUtil.mandatory);
								commentsList.add(comments);
							}
						} else {
							// get whole details map based on isCntrDetailsMatch
							detailsMap = customDetailsRepo.getCntrdetailsMap(customDetailsVO.getCntr_nbr(),customDetailsVO.getInstruction_type(),vvCd);
							cntrExistinJPOM = customDetailsRepo.containerIsExist(customDetailsVO.getCntr_nbr(), customDetailsVO.getVv_cd()) > 0;
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.vessel_name)) {
						String cellData = customDetailsVO.getVessel_name();
						if (CommonUtil.deNull(cellData) == "") {
							if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
									.equalsIgnoreCase(ConstantUtil.yes)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG_EDI
										+ customDetailsUploadConfig.getAttr_desc());
								commentsList.add(comments);
							}
						} else if ((!customDetailsVO.getVessel_name()
								.equalsIgnoreCase(vesselCallDetails.getVesselName()))) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(ConstantUtil.ErrorMsg_Invalid_Voy);
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_VOYAGE_NO)) {
						String cellData = customDetailsVO.getVessel_name();
						if (CommonUtil.deNull(cellData) == "") {
							if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
									.equalsIgnoreCase(ConstantUtil.yes)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG_EDI
										+ customDetailsUploadConfig.getAttr_desc());
								commentsList.add(comments);
							}
						} else if (!customDetailsVO.getVoyage_no()
								.equalsIgnoreCase(vesselCallDetails.getInwardVoyNo()) && !customDetailsVO.getVoyage_no()
								.equalsIgnoreCase(vesselCallDetails.getOutVoyNo())) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(ConstantUtil.ErrorMsg_Invalid_Voy);
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_INSTRUCTION_TYPE)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getInstruction_type();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(cellData) == "") {
							if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
									.equalsIgnoreCase(ConstantUtil.yes)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG_EDI
										+ customDetailsUploadConfig.getAttr_desc());
								commentsList.add(comments);
							}
						} else if (!deleteflag && !ConstantUtil.CUSTOM_DETAILS_INSTRUCTION_TYPE_MAP.containsValue(upperCellData)) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc() + " " + ConstantUtil.invalid);
							commentsList.add(comments);
						} else if (!deleteflag && detailsMap.isEmpty()) {
							if (cntrExistinJPOM) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_WrongInstructionType);
								commentsList.add(comments);
							}
						} else if (!deleteflag && (!detailsMap.isEmpty()
								&& !detailsMap.get("PURP_CD").equalsIgnoreCase(upperCellData))
								&& !CommonUtil.deNull(customDetailsVO.getCntr_nbr()).isEmpty()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(ConstantUtil.ErrorMsg_WrongInstructionType);
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name()
							.equals(ConstantUtil.CUSTOM_HANDLING_INSTRUCTION)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getHandling_instruction();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(cellData) == "") {
							if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
									.equalsIgnoreCase(ConstantUtil.yes)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG_EDI
										+ customDetailsUploadConfig.getAttr_desc());
								commentsList.add(comments);
							}
						} else if (!deleteflag && !ConstantUtil.CUSTOM_DETAILS_HANDLING_INSTRUCTION_MAP.containsValue(upperCellData)) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc() + " " + ConstantUtil.invalid);
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_CONSIGNEE)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getConsignee_name();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						String item = upperCellData;
						if (CommonUtil.deNull(cellData) == "") {
							if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
									.equalsIgnoreCase(ConstantUtil.yes)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG_EDI
										+ customDetailsUploadConfig.getAttr_desc());
								commentsList.add(comments);
							}
						} else {
							if (!consigneeDropdownList.stream()
							        .anyMatch(entry -> entry.toUpperCase().matches(".*" + item + ".*"))) {
								customDetailsVO.setConsignee_cd(ConstantUtil.others.toUpperCase());
								customDetailsVO.setConsignee_name(upperCellData);
							} else {
								// check cd for consignee_name in db, else others
								String consCd = customDetailsRepo.getConsigneeShipperCd(upperCellData);
								customDetailsVO.setConsignee_cd(consCd);
								customDetailsVO.setConsignee_name(upperCellData);
							}
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_SHIPPER)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getShipper_name();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						String item = upperCellData;
						if (CommonUtil.deNull(cellData) == "") {
							if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
									.equalsIgnoreCase(ConstantUtil.yes)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG_EDI
										+ customDetailsUploadConfig.getAttr_desc());
								commentsList.add(comments);
							}
						} else {
							if (!deleteflag && !consigneeDropdownList.stream()
							        .anyMatch(entry -> entry.toUpperCase().matches(".*" + item + ".*"))) {
								customDetailsVO.setShipper_cd(ConstantUtil.others.toUpperCase());
								customDetailsVO.setShipper_name(upperCellData);
							} else {
								// check cd for shipper_name in db, else others
								String consCd = customDetailsRepo.getConsigneeShipperCd(upperCellData);
								customDetailsVO.setConsignee_cd(consCd);
								customDetailsVO.setShipper_name(upperCellData);
							}
						}

					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_CNTR_STATUS)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getCntr_status();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (!deleteflag && !CommonUtil.deNull(cellData).isEmpty()
								&& !ConstantUtil.CUSTOM_DETAILS_CNTR_STATUS_MAP.containsValue(upperCellData)) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc() + " " + ConstantUtil.invalid);
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_PACKAGE_TYPE)) {
						String cellData = customDetailsVO.getPackage_type();
						if (CommonUtil.deNull(cellData) == "") {
							if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
									.equalsIgnoreCase(ConstantUtil.yes)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG_EDI
										+ customDetailsUploadConfig.getAttr_desc());
								commentsList.add(comments);
							}
						} else if (!deleteflag && !packagingType.contains(cellData)) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc() + " " + ConstantUtil.invalid);
							commentsList.add(comments);

						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_VESSEL_DIS_PORT)
							|| customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_ORI_LOAD_PORT)
							|| customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_LOAD_PORT)
							|| customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_DIS_PORT)
							|| customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_DEST_PORT)) {
						String upperCellData = "", cellData = "";
						if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_VESSEL_DIS_PORT)) {
							cellData = customDetailsVO.getVessel_dis_port();
						} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_ORI_LOAD_PORT)) {
							cellData = customDetailsVO.getOri_load_port();
						} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_LOAD_PORT)) {
							cellData = customDetailsVO.getLoad_port();
						} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_DIS_PORT)) {
							cellData = customDetailsVO.getDis_port();
						} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_DEST_PORT)) {
							cellData = customDetailsVO.getDest_port();
						}
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(cellData) == "") {
							if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
									.equalsIgnoreCase(ConstantUtil.yes)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG_EDI
										+ customDetailsUploadConfig.getAttr_desc());
								commentsList.add(comments);
							}
						} else if (!deleteflag && !portListDropdownList.contains(upperCellData)) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(ConstantUtil.ErrorMsg_InvalidPort);
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_DG_IND)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getDg_ind();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (!deleteflag && !upperCellData.isEmpty() && (!detailsMap.isEmpty()
								&& !detailsMap.get("DG_IND").equalsIgnoreCase(upperCellData))
								&& !CommonUtil.deNull(customDetailsVO.getCntr_nbr()).isEmpty()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(ConstantUtil.ErrorMsg_WrongDangerousIndicator);
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_HSCODE)) {
						String cellData = customDetailsVO.getHscode();
						if (CommonUtil.deNull(cellData) == "") {
							if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
									.equalsIgnoreCase(ConstantUtil.yes)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG_EDI
										+ customDetailsUploadConfig.getAttr_desc());
								commentsList.add(comments);
							}
						} else if (!deleteflag && (!(cellData.length() == 0 || cellData.length() == 4
								|| cellData.length() == 6 || cellData.length() == 8))) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(ConstantUtil.ErrorMsg_InvalidCustomHSCode);
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_IMO_CLASS)) {
						String cellData = customDetailsVO.getImo_class();
						if (CommonUtil.deNull(cellData) == "") {
							if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
									.equalsIgnoreCase(ConstantUtil.yes)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG_EDI
										+ customDetailsUploadConfig.getAttr_desc());
								commentsList.add(comments);
							}
						} else if (!deleteflag && !imoClass.contains(cellData)) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc() + " " + ConstantUtil.invalid);
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_BL_NBR)) {
						String cellData = customDetailsVO.getBl_nbr();
						String upperCellData = "";
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase().trim();
						}
						if (CommonUtil.deNull(cellData) == "") {
							if (!deleteflag && customDetailsUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG_EDI
										+ customDetailsUploadConfig.getAttr_desc());
								commentsList.add(comments);
							}
						} else if (!deleteflag && detailsMap.isEmpty()) {
							if (cntrExistinJPOM) {
								continue; // instruction type error
							}
						} else if (!deleteflag && (!detailsMap.isEmpty()
								&& !detailsMap.get("BILL_LADING_NBR").equalsIgnoreCase(upperCellData))
								&& !CommonUtil.deNull(customDetailsVO.getCntr_nbr()).isEmpty()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(ConstantUtil.ErrorMsg_WrongBLNo);
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_CNTR_NBR)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getCntr_nbr();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase().trim();
						}
						if (CommonUtil.deNull(cellData).isEmpty()) {
							if (customDetailsUploadConfig.getMandatory_ind().equalsIgnoreCase(ConstantUtil.yes)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG_EDI
										+ customDetailsUploadConfig.getAttr_desc());
								commentsList.add(comments);
							}
						} else if (CommonUtil.deNull(customDetailsVO.getAction())
								.equalsIgnoreCase(ConstantUtil.action_add)) {
							if (!deleteflag && !cntrExistinJPOM) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_cntrNbrNotExistContainer);
								commentsList.add(comments);
							} else if (customDetailsRepo.customDetailIsExist(upperCellData, customDetailsVO.getVv_cd(),
									customDetailsVO.getInstruction_type()) > 0) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_cntrNbrAlreadyExist);
								commentsList.add(comments);
							} else if (!deleteflag && (!detailsMap.isEmpty()
									&& !customDetailsRepo.isShipmentStatusValid(detailsMap.get("CNTR_SEQ_NBR"), vvCd))) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_cntrNbrShipementStatusNotValid);
								commentsList.add(comments);
							} 
						} else if (CommonUtil.deNull(customDetailsVO.getAction())
								.equalsIgnoreCase(ConstantUtil.action_update)
								|| CommonUtil.deNull(customDetailsVO.getAction())
										.equalsIgnoreCase(ConstantUtil.action_delete)) {
							if (customDetailsRepo.customDetailIsExist(upperCellData, customDetailsVO.getVv_cd(),
									customDetailsVO.getInstruction_type()) == 0) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.ErrorMsg_recordsNotExist);
								commentsList.add(comments);
							}
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_MASTER_BL_NBR)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getMaster_bl_nbr();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_BL_NBR_REMARKS)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getBl_nbr_remarks();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name()
							.equals(ConstantUtil.CUSTOM_PLACE_OF_RECEIPT_NAME)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getPlace_of_receipt_name();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name()
							.equals(ConstantUtil.CUSTOM_PLACE_OF_DELIVERY_NAME)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getPlace_of_delivery_name();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_CONSIGNEE_UEN)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getConsignee_uen();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_CONSIGNEE_ADDRESS)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getConsignee_address();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_SHIPPER_UEN)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getShipper_uen();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_SHIPPER_ADDRESS)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getShipper_address();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(cellData) == "") {
							if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
									.equalsIgnoreCase(ConstantUtil.yes)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG_EDI
										+ customDetailsUploadConfig.getAttr_desc());
								commentsList.add(comments);
							}
						} else if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig
								.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_NOTIFY_PARTY_NAME)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getNotify_party_name();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_NOTIFY_PARTY_UEN)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getNotify_party_uen();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name()
							.equals(ConstantUtil.CUSTOM_NOTIFY_PARTY_CONTACT)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getNotify_party_contact();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name()
							.equals(ConstantUtil.CUSTOM_NOTIFY_PARTY_EMAIL)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getNotify_party_email();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}

					} else if (customDetailsUploadConfig.getAttr_name()
							.equals(ConstantUtil.CUSTOM_NOTIFY_PARTY_ADDRESS)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getNotify_party_address();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name()
							.equals(ConstantUtil.CUSTOM_FREIGHT_FOWARDER_NAME)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getFreight_fowarder_name();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name()
							.equals(ConstantUtil.CUSTOM_FREIGHT_FOWARDER_UEN)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getFreight_fowarder_uen();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name()
							.equals(ConstantUtil.CUSTOM_FREIGHT_FOWARDER_CONTACT)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getFreight_fowarder_contact();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name()
							.equals(ConstantUtil.CUSTOM_FREIGHT_FOWARDER_EMAIL)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getFreight_fowarder_email();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name()
							.equals(ConstantUtil.CUSTOM_FREIGHT_FOWARDER_ADDRESS)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getFreight_fowarder_address();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_STEVEDORE_NAME)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getStevedore_name();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_STEVEDORE_UEN)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getStevedore_uen();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_STEVEDORE_CONTACT)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getStevedore_contact();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_STEVEDORE_EMAIL)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getStevedore_email();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_STEVEDORE_ADDRESS)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getStevedore_address();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_CARGO_AGENT_NAME)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getCargo_agent_name();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_CARGO_AGENT_UEN)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getCargo_agent_uen();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name()
							.equals(ConstantUtil.CUSTOM_CARGO_AGENT_CONTACT)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getCargo_agent_contact();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_CARGO_AGENT_EMAIL)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getCargo_agent_email();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name()
							.equals(ConstantUtil.CUSTOM_CARGO_AGENT_ADDRESS)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getCargo_agent_address();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_ITEM_NO)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getItem_no();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(cellData).equalsIgnoreCase("") || CommonUtil.deNull(cellData).equalsIgnoreCase("0")) {
							if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
									.equalsIgnoreCase(ConstantUtil.yes)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG_EDI
										+ customDetailsUploadConfig.getAttr_desc());
								commentsList.add(comments);
							}
						} else if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig
								.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_PACKAGE_QUANTITY)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getPackage_quantity();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(cellData).isEmpty() || Integer.parseInt(CommonUtil.deNull(cellData)) <= 0) {
							if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
									.equalsIgnoreCase(ConstantUtil.yes)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG_EDI
										+ customDetailsUploadConfig.getAttr_desc());
								commentsList.add(comments);
							}
						} else if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig
								.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_WEIGHT)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getWeight();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(cellData).equalsIgnoreCase("") || CommonUtil.deNull(cellData).startsWith("-")
								|| CommonUtil.deNull(cellData).equalsIgnoreCase("0") || CommonUtil.deNull(cellData).equalsIgnoreCase("0.0")
								|| CommonUtil.deNull(cellData).equalsIgnoreCase("0.00")) { // add comments as unsuccessful for weight value negative, 0 or empty value
							if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
									.equalsIgnoreCase(ConstantUtil.yes)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG_EDI
										+ customDetailsUploadConfig.getAttr_desc());
								commentsList.add(comments);
							}
						} else if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig
								.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_MEASUREMENT)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getMeasurement();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_CARGO_DESCRIPTION)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getCargo_description();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(cellData) == "") {
							if (!deleteflag && customDetailsUploadConfig.getMandatory_ind()
									.equalsIgnoreCase(ConstantUtil.yes)) {
								Comments comments = new Comments();
								comments.setKey(customDetailsUploadConfig.getAttr_name());
								comments.setMessage(ConstantUtil.CUSTOM_MANDATORY_MSG_EDI
										+ customDetailsUploadConfig.getAttr_desc());
								commentsList.add(comments);
							}
						} else if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig
								.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_MARK_AND_NO)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getMark_and_no();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_UNDG_NBR)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getUndg_nbr();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_FLASHPOINT)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getFlashpoint();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_PACKING_GROUP)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getPacking_grp();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_ISO)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getIso();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_GROSS_WT)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getGross_wt();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					} else if (customDetailsUploadConfig.getAttr_name().equals(ConstantUtil.CUSTOM_SEAL_NBR_CARRIER)) {
						String upperCellData = "";
						String cellData = customDetailsVO.getSeal_nbr_carrier();
						if (cellData != null && cellData != "") {
							upperCellData = cellData.toUpperCase();
						}
						if (CommonUtil.deNull(upperCellData).length() > customDetailsUploadConfig.getMax_length()) {
							Comments comments = new Comments();
							comments.setKey(customDetailsUploadConfig.getAttr_name());
							comments.setMessage(customDetailsUploadConfig.getAttr_desc()
									+ ConstantUtil.CUSTOM_MAXLENGTH_MSG.replaceAll("~",
											String.valueOf(customDetailsUploadConfig.getMax_length())));
							commentsList.add(comments);
						}
					}
				}
				if (commentsList.size() > 0) {
					customDetailsVO.setMessage(ConstantUtil.error);
					customDetailsVO.setErrorInfo(commentsList);
				} else {
					customDetailsVO.setMessage(ConstantUtil.success);
				}
			}
			// insert/update/remove record
			List<CustomDetails> processResults = customDetailsRepo.insertCustomDetailsData(customDetailsList, vvCd,
					userId, companyCode);
			log.info("processResults: " + processResults);
			// check record
			Map<String, SummaryCuscar> vesselSummaryMap = new HashMap<>();
			int counter = 0;
			for (CustomDetails customDetails : processResults) {
				String vslNm = customDetails.getVessel_name();
				String voyNbr = customDetails.getVoyage_no();
				String vesselKey = vslNm + "_" + voyNbr;

				summ.setVslNm(vslNm);
				summ.setVslVoy(voyNbr);

				if (summ.getType() == null) {
					summ.setType(new ArrayList<>());
				}
				if (summ.getBlNbr() == null) {
					summ.setBlNbr(new ArrayList<>());
				}
				if (summ.getMessage() == null) {
					summ.setMessage(new HashMap<>());
				}
				int totalReceived = vesselSummaryMap.containsKey(vesselKey) ? Integer.parseInt(summ.getTotalRecordRcv())
						: 0;
				totalReceived++;
				summ.setTotalRecordRcv(String.valueOf(totalReceived));

				String type = customDetails.getAction();
				if (customDetails.getMessage().equalsIgnoreCase(ConstantUtil.success)) {
					int totalSuccess = summ.getTotalSuccess() == null ? 0 : Integer.parseInt(summ.getTotalSuccess());
					summ.setTotalSuccess(String.valueOf(totalSuccess + 1));

					switch (type) {
					case "Add":
						type = "A";
						summ.setTotalCreated(String.valueOf(
								(summ.getTotalCreated() == null ? 0 : Integer.parseInt(summ.getTotalCreated())) + 1));
						break;
					case "Update":
						type = "U";
						summ.setTotalUpdated(String.valueOf(
								(summ.getTotalUpdated() == null ? 0 : Integer.parseInt(summ.getTotalUpdated())) + 1));
						break;
					case "Delete":
						type = "D";
						summ.setTotalDeleted(String.valueOf(
								(summ.getTotalDeleted() == null ? 0 : Integer.parseInt(summ.getTotalDeleted())) + 1));
						break;
					}
				} else {
					int totalError = summ.getTotalError() == null ? 0 : Integer.parseInt(summ.getTotalError());
					summ.setTotalError(String.valueOf(totalError + 1));
					type = "F";

				}

				summ.getType().add(type);
				summ.getBlNbr().add(customDetails.getBl_nbr());
				summ.getMessage().put(customDetails.getBl_nbr(), CommonUtility.deNull(customDetails.getMessage()));
				if (type.equalsIgnoreCase("F")) {
					List<Comments> errInfo = customDetails.getErrorInfo();
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < errInfo.size(); i++) {
						Comments err = errInfo.get(i);
						String message = err.getMessage();
						
						if (message != null && message.toLowerCase().contains(" for this vessel")) {
					        message = message.substring(0, message.toLowerCase().indexOf(" for this vessel")).trim();
					    }
						if (i == 0) {
							sb.append(customDetails.getCntr_nbr()).append(": ");
							sb.append(message);
							if (errInfo.size() > 1) {
								sb.append(", ");
							}
						} else {
							sb.append(message);
							if (i < errInfo.size() - 1) {
								sb.append(", ");
							}
						}
					}
					summ.getMessageErr().put(customDetails.getBl_nbr() + "_" + counter, sb.toString());
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append(customDetails.getCntr_nbr()).append(": Success");
					summ.getMessageErr().put(customDetails.getBl_nbr() + "_" + counter, sb.toString());
				}

				vesselSummaryMap.put(vesselKey, summ);
				counter++;
			}

			Map.Entry<String, SummaryCuscar> entry = vesselSummaryMap.entrySet().iterator().next();
			SummaryCuscarObj = entry.getValue();

			String outputFileName = ConstantUtil.custom_details_filename + f.format(new Date())
					+ ConstantUtil.file_ext_status;
			boolean statusFile = generateStatusFile(summ, outputFileName, vvCd, "P");
			boolean row = false;
			if (statusFile) {
				// update output file name
				row = customDetailsRepo.updateCustomDetailsExcelDetails(seq_id, outputFileName);
			}

			String summaryUploaded = customDetailsRepo.getTotalCntrForVessel(vvCd);
			SummaryCuscarObj.setSummaryUploaded(summaryUploaded);
			SummaryCuscarObj.setTypeCd(ConstantUtil.cuscar_type_cd);

			log.info(row);

			// return value
			Template template = new Template();
			template.setRefId(seq_id.toString());
			template.setRefType("txt");
			template.setFileName(outputFileName);
			List<Template> templateList = new ArrayList<Template>();
			templateList.add(template);
			SummaryCuscarObj.setFileDetails(templateList);

		} catch (BusinessException e) {
			log.error("Exception processData: ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.error("Exception processData: ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END - processData. summaryList: " + SummaryCuscarObj);
		}
		return SummaryCuscarObj;
	}

	private boolean generateStatusFile(SummaryCuscar summary, String outputFileName, String varNbr, String loc)
			throws BusinessException {
		boolean fileCreated = false;
		String path = "";
		try {
			log.info("START - generateStatusFile. summary: " + summary.toString() + ", outputFileName: "
					+ outputFileName + ", varNbr: " + varNbr + ", loc: " + loc);
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
			path = folderLocation.resolve(outputFileName).toString();
			log.info("fileUpload folderLocation :" + path);
			try (FileWriter writer = new FileWriter(path)) {

				List<String> types = summary.getType();
				if (loc.equalsIgnoreCase("E")) {
					for (int i = 0; i < types.size(); i++) {
						writer.write(String.format("%n", summary.getFileError()));
					}
				} else {
					// header
					writer.write(String.format("H %-50s%-12s%-35s%-17s%-84s%n", "CUSCAR",
							LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")), summary.getVslNm(),
							summary.getVslVoy(), "" // Filler
					));

					// Write Message Details for Each BL
					List<String> blNbrs = summary.getBlNbr();
					Map<String, String> messages = summary.getMessageErr();

					log.info("Messages: " + messages);
					for (int i = 0; i < blNbrs.size(); i++) {
					    String key = blNbrs.get(i) + "_" + i;
					    String message = messages.getOrDefault(key, "");

					    if (message.trim().toLowerCase().contains(": success")) {
					        String content = String.format("%-2s%-75s%s%n", types.get(i), blNbrs.get(i), message);
					        writer.write(content);
					    }
					}

					for (int i = 0; i < blNbrs.size(); i++) {
					    String key = blNbrs.get(i) + "_" + i;
					    String message = messages.getOrDefault(key, "");

					    if (!message.trim().toLowerCase().contains(": success")) {
					        String content = String.format("%-2s%-75s%s%n", types.get(i), blNbrs.get(i), message);
					        writer.write(content);
					    }
					}

					// Write Summary Section
					writer.write(String.format("%-50s%-5s%-145s%n", ConstantUtil.total_rcrd_rcv_txt,
							summary.getTotalRecordRcv(), ""));
					writer.write(String.format("%-50s%-5s%-145s%n", ConstantUtil.total_err_rcd_txt,
							summary.getTotalError(), ""));
					writer.write(String.format("%-50s%-5s%-145s%n", ConstantUtil.total_rcrd_succ_txt,
							summary.getTotalSuccess(), ""));
					writer.write(String.format("%-50s%-5s%-145s%n", ConstantUtil.total_rcrd_crtd_txt,
							summary.getTotalCreated(), ""));
					writer.write(String.format("%-50s%-5s%-145s%n", ConstantUtil.total_rcrd_updt_txt,
							summary.getTotalUpdated(), ""));
					writer.write(String.format("%-50s%-5s%-145s%n", ConstantUtil.total_rcrd_del_txt,
							summary.getTotalDeleted(), ""));

					log.info("Status file created: " + path);
					fileCreated = true;
				}
			} catch (Exception e) {
				log.info("Exception generateStatusFile : ", e);
				throw new BusinessException("M4201");
			}
		} catch (Exception e) {
			log.info("Exception generateStatusFile : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END - generateStatusFile. fileCreated: " + fileCreated + ", filePath: " + path);
		}
		return fileCreated;
	}

	@Override
	public Resource downloadFile(String refId, String type) throws BusinessException {
		String fileName = null;
		Resource res = null;
		try {
			Path rootLocation = null;
			BookingReferenceFileUploadDetails fileDetails = customDetailsRepo.getCustomDetailFileUploadDetails(refId);
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
		} catch (Exception ex) {
			log.info("Exception downloadFile : ", ex);
			throw new FileSystemNotFoundException("File not found " + fileName);
		}
		return res;
	}

}
