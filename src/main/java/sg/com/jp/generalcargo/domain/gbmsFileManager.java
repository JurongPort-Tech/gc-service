package sg.com.jp.generalcargo.domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import sg.com.jp.generalcargo.dao.JPPsaRepository;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.Constant;

// Referenced classes of package filemanager.processor.gbms:
//            LogManager
@Component
public class gbmsFileManager {

	public gbmsFileManager() {
		writeByte = new byte[57610];
		sFilename = "";
		jppsavalueObj = new JPPSAValueObject();
 	}

	private static final Log log = LogFactory.getLog(gbmsFileManager.class);
	@Autowired
	private JPPsaRepository jppsaRepository;
	
	@Value("${IGD.OutGoingFilePath}")
	private String outGoingFilePath;
	
	@Value("${IGD.incomingArchival}")
	private String incomingArchival;
	
	@Value("${IGD.OutGoingArchival}")
	private String outGoingArchival;
	
	@Value("${JndiInitContext.java.naming.provider.url}")
	private String jndiInitContextUrl;
	

//	public JPPSAValueObject generateValueObject(String s) throws IOException {
//		FileOutputStream fileoutputstream = null;
//		String s1 = "";
//		try {
//			File file = new File(s);
//			sFilename = file.getName();
//			int i = (int) file.length();
//			byte abyte0[] = new byte[i];
//			ArrayList arraylist = loadData(s);
//			if (arraylist != null)
//				;
//			Iterator iterator = arraylist.iterator();
//			String s2 = "";
//			int j = 0;
//			while (iterator.hasNext()) {
//				String s3 = (String) iterator.next();
//				if (s3 != null) {
//					j++;
//					s2 = s2 + s3;
//				}
//			}
//			if (sFilename.trim().length() > 25) {
//				StringTokenizer stringtokenizer = new StringTokenizer(sFilename, "IGD");
//				String s4 = stringtokenizer.nextToken();
//				String s5 = stringtokenizer.nextToken();
//				sFilename = "IGD" + s5;
//			}
//			byte abyte1[] = s2.getBytes();
//			GregorianCalendar gregoriancalendar = new GregorianCalendar();
//			int k = gregoriancalendar.get(0);
//			int l = gregoriancalendar.get(1);
//			int i1 = gregoriancalendar.get(2) + 1;
//			String s6 = i1 + "";
//			if (s6.length() == 1)
//				s6 = "0" + s6;
//			int j1 = gregoriancalendar.get(5);
//			String s7 = j1 + "";
//			if (s7.length() == 1)
//				s7 = "0" + s7;
//			int k1 = gregoriancalendar.get(10);
//			String s8 = k1 + "";
//			if (s8.length() == 1)
//				s8 = "0" + s8;
//			int l1 = gregoriancalendar.get(12);
//			String s9 = l1 + "";
//			if (s9.length() == 1)
//				s9 = "0" + s9;
//			int i2 = gregoriancalendar.get(13);
//			String s10 = i2 + "";
//			if (s10.length() == 1)
//				s10 = "0" + s10;
//			int j2 = gregoriancalendar.get(14);
//			String s11 = j2 + "";
//			s11 = s11.substring(0, 3);
//			String s12 = "IGD" + l + s6 + s7 + s8 + s9 + s10 + s11 + ".txt";
//			String s13 = incomingArchival;
//			File file1 = new File(s13 + l + s6);
//			boolean flag = file1.mkdir();
//			fileoutputstream = new FileOutputStream(s13 + l + s6 + "/" + sFilename);
//			fileoutputstream.write(abyte1);
//			fileoutputstream.close();
//			log.info("No Of Line " + j);
//			if (j < 3)
//				throw new BusinessException("M30023");
//			int k2 = Integer.parseInt(getTotalRecSubmitted(s2));
//			log.info("No Of Records " + k2);
//			if (j - 2 != k2)
//				throw new BusinessException("M30024");
//			log.info("text file length" + s2.length());
//			if (s2.length() < 750)
//				throw new BusinessException("M30000");
//			fetchCargoHeader(s2);
//			fetchCargoDetails(s2, String.valueOf(j - 2));
//			fetchCargoSummary(s2);
//			String s14 = insertIGD(jppsavalueObj, null);
//		} catch (FileNotFoundException filenotfoundexception) {
//			log.info("file cant be created" + filenotfoundexception.getMessage());
//		} catch (IOException ioexception) {
//			log.info("Failed " + ioexception.getMessage());
//		} catch (BusinessException BusinessException) {
//			log.info("Exception generateValueObject", BusinessException );
//			log.info(BusinessException.getMessage());
//			log.info(BusinessException.getMessage());
//		} catch (Exception exception) {
//			log.info("Exception generateValueObject", exception );
//		} finally {
//			fileoutputstream.close();
//			log.info("GenerateValueObject END");
//		}
//		return jppsavalueObj;
//	}

	public void fetchCargoHeader(String s) throws BusinessException {
		String s1 = Constant.SysProperties.IntergatewayCargoHeader_RecordType;
		String s2 = fetchFieldDetails(s1, s);
		if (!s2.equals("H"))
			throw new BusinessException("M30001");
		String s3 = Constant.SysProperties.IntergatewayCargoHeader_CreationDate;
		String s4 = fetchFieldDetails(s3, s);
		if (s4.trim().length() == 0)
			throw new BusinessException("M30002");
		String s5 = Constant.SysProperties.IntergatewayCargoHeader_AbbVesselName;
		String s6 = fetchFieldDetails(s5, s);
		if (s6.trim().length() == 0)
			throw new BusinessException("M30003");
		String s7 = Constant.SysProperties.IntergatewayCargoHeader_AbbVoyNumber;
		String s8 = fetchFieldDetails(s7, s);
		disvoynbrForErr = s8;
		if (s8.trim().length() == 0) {
			throw new BusinessException("M30004");
		} else {
			String s9 = Constant.SysProperties.IntergatewayCargoHeader_Filler;
			String s10 = fetchFieldDetails(s9, s);
			jppsavalueObj.setICHRecType(s2);
			jppsavalueObj.setICHCreateDate(s4);
			jppsavalueObj.setICHAbbVslName(s6);
			jppsavalueObj.setICHDisAbbVslName(s8);
			incr = Integer.parseInt(Constant.SysProperties.IntergatewayCargoHeader_FileSize);
			return;
		}
	}

	public void fetchCargoDetails(String s, String s1) throws BusinessException {
//        try
//        {
//            int i = Integer.parseInt(s1.trim());
//            String as[] = new String[i];
//            String as1[] = new String[i];
//            String as2[] = new String[i];
//            String as3[] = new String[i];
//            String as4[] = new String[i];
//            String as5[] = new String[i];
//            String as6[] = new String[i];
//            String as7[] = new String[i];
//            String as8[] = new String[i];
//            String as9[] = new String[i];
//            String as10[] = new String[i];
//            String as11[] = new String[i];
//            String as12[] = new String[i];
//            String as13[] = new String[i];
//            String as14[] = new String[i];
//            String as15[] = new String[i];
//            String as16[] = new String[i];
//            String as17[] = new String[i];
//            String as18[] = new String[i];
//            String as19[] = new String[i];
//            String as20[] = new String[i];
//            for(int j = 0; j < i; j++)
//            {
//                String s2 = sysProp.getProperty("IntergatewayCargoDetails.RecordType");
//                as[j] = fetchFieldDetails(s2, s);
//                if(as[j].trim().length() == 0)
//                    throw new BusinessException("M30005");
//                String s3 = sysProp.getProperty("IntergatewayCargoDetails.Function");
//                as1[j] = fetchFieldDetails(s3, s);
//                if(as1[j].trim().length() == 0)
//                    throw new BusinessException("M30006");
//                String s4 = sysProp.getProperty("IntergatewayCargoDetails.BillofLading");
//                as2[j] = fetchFieldDetails(s4, s);
//                if(as2[j].trim().length() == 0)
//                    throw new BusinessException("M30007");
//                String s5 = sysProp.getProperty("IntergatewayCargoDetails.HSCode");
//                as3[j] = fetchFieldDetails(s5, s);
//                if(as3[j].trim().length() == 0)
//                    throw new BusinessException("M30008");
//                String s6 = sysProp.getProperty("IntergatewayCargoDetails.PackagingType");
//                as4[j] = fetchFieldDetails(s6, s);
//                if(as4[j].trim().length() == 0)
//                    throw new BusinessException("M30009");
//                String s7 = sysProp.getProperty("IntergatewayCargoDetails.NumberOfPackage");
//                as5[j] = fetchFieldDetails(s7, s);
//                if(as5[j].trim().length() == 0)
//                    throw new BusinessException("M30010");
//                String s8 = sysProp.getProperty("IntergatewayCargoDetails.weight");
//                as6[j] = fetchFieldDetails(s8, s);
//                if(as6[j].trim().length() == 0)
//                    throw new BusinessException("M30011");
//                String s9 = sysProp.getProperty("IntergatewayCargoDetails.volume");
//                as7[j] = fetchFieldDetails(s9, s);
//                if(as7[j].trim().length() == 0)
//                    throw new BusinessException("M30012");
//                String s10 = sysProp.getProperty("IntergatewayCargoDetails.DGIndicator");
//                as8[j] = fetchFieldDetails(s10, s);
//                String s11 = sysProp.getProperty("IntergatewayCargoDetails.ShipperName");
//                as9[j] = fetchFieldDetails(s11, s);
//                String s12 = sysProp.getProperty("IntergatewayCargoDetails.CargoType");
//                as10[j] = fetchFieldDetails(s12, s);
//                if(as10[j].trim().length() == 0)
//                    throw new BusinessException("M30013");
//                String s13 = sysProp.getProperty("IntergatewayCargoDetails.LoadingVessel");
//                as11[j] = fetchFieldDetails(s13, s);
//                if(as11[j].trim().length() == 0)
//                    throw new BusinessException("M30014");
//                String s14 = sysProp.getProperty("IntergatewayCargoDetails.LoadingVoyage");
//                as12[j] = fetchFieldDetails(s14, s);
//                if(as12[j].trim().length() == 0)
//                    throw new BusinessException("M30015");
//                String s15 = sysProp.getProperty("IntergatewayCargoDetails.PortsOfDischarge");
//                as13[j] = fetchFieldDetails(s15, s);
//                if(as13[j].trim().length() == 0)
//                    throw new BusinessException("M30016");
//                String s16 = sysProp.getProperty("IntergatewayCargoDetails.ContainerNumber");
//                as20[j] = fetchFieldDetails(s16, s);
//                String s17 = sysProp.getProperty("IntergatewayCargoDetails.DirectionOfIntergateway");
//                as14[j] = fetchFieldDetails(s17, s);
//                if(as14[j].trim().length() == 0)
//                    throw new BusinessException("M30017");
//                String s18 = sysProp.getProperty("IntergatewayCargoDetails.BookingReferenceOfLoadingVesel");
//                as15[j] = fetchFieldDetails(s18, s);
//                if(as15[j].trim().length() == 0)
//                {
//                    log.info("in");
//                    throw new BusinessException("M30018");
//                }
//                EJBHomeFactory ejbhomefactory = EJBHomeFactory.getInstance();
//                jppsaHome jppsahome = (jppsaHome)ejbhomefactory.lookUpHome("jppsa");
//                String s19 = sysProp.getProperty("IGD.OutGoingFilePath");
//                jppsaRemote jppsaremote = jppsahome.create();
//                log.info("Record : '" + as[j] + "'");
//                log.info("Function : '" + as1[j].trim() + "'");
//                log.info("Bill of Lading : '" + as2[j] + "'");
//                log.info("HS : " + as3[j].trim());
//                log.info("port of discharge : " + as13[j].trim());
//                log.info("booking ref :" + as15[j].trim());
//                String s20 = jppsaremote.CheckPackage(as4[j].trim());
//                String s21 = jppsaremote.CheckBkr(as15[j].trim());
//                String s22 = jppsaremote.LoadingVoyage(as12[j].trim(), as15[j].trim());
//                String s23 = jppsaremote.LoadingVessel(as12[j].trim(), as11[j].trim());
//                if(s20.equals("N"))
//                    throw new BusinessException("M30009");
//                if(s21.equals("N"))
//                    throw new BusinessException("M30018");
//                if(s22.equals("N"))
//                    throw new BusinessException("M30015");
//                if(s23.equals("N"))
//                    throw new BusinessException("M30014");
//                String s24 = sysProp.getProperty("IntergatewayCargoDetails.AccountNumber");
//                as19[j] = fetchFieldDetails(s24, s);
//                if(as19[j].trim().length() == 0)
//                    throw new BusinessException("M30019");
//                String s25 = sysProp.getProperty("IntergatewayCargoDetails.TDBNo");
//                as16[j] = fetchFieldDetails(s25, s);
//                if(as16[j].trim().length() == 0)
//                    throw new BusinessException("M30020");
//                String s26 = sysProp.getProperty("IntergatewayCargoDetails.CargoDescription");
//                as17[j] = fetchFieldDetails(s26, s);
//                if(as17[j].trim().length() == 0)
//                    throw new BusinessException("M30021");
//                String s27 = sysProp.getProperty("IntergatewayCargoDetails.Marking");
//                as18[j] = fetchFieldDetails(s27, s);
//                if(as18[j].trim().length() == 0)
//                    throw new BusinessException("M30022");
//                String s28 = sysProp.getProperty("IntergatewayCargoDetails.Filler");
//                String s29 = fetchFieldDetails(s28, s);
//                incr = incr + Integer.parseInt(sysProp.getProperty("IntergatewayCargoDetails.FileSize"));
//            }
//
//            Vector vector = new Vector();
//            for(int k = 0; k < i; k++)
//            {
//                JPPSAValueObject jppsavalueobject = new JPPSAValueObject();
//                jppsavalueobject.setICDRecordType(as[k]);
//                jppsavalueobject.setICDFunction(as1[k]);
//                jppsavalueobject.setICDBillofLading(as2[k]);
//                jppsavalueobject.setICDHScode(as3[k]);
//                jppsavalueobject.setICDPackageType(as4[k]);
//                jppsavalueobject.setICDNoofPackage(as5[k]);
//                jppsavalueobject.setICDWeight(as6[k]);
//                jppsavalueobject.setICDVolume(as7[k]);
//                jppsavalueobject.setICDDGIndicator(as8[k]);
//                jppsavalueobject.setICDShipperName(as9[k]);
//                jppsavalueobject.setICDCargoType(as10[k]);
//                jppsavalueobject.setICDLoadingVessel(as11[k]);
//                jppsavalueobject.setICDLoadingVoyage(as12[k]);
//                jppsavalueobject.setICDPortOfDischarge(as13[k]);
//                jppsavalueobject.setICDContainerNumber(as20[k]);
//                jppsavalueobject.setICDDirectInterGateWay(as14[k]);
//                jppsavalueobject.setICDBookingRef(as15[k]);
//                jppsavalueobject.setICDAccount(as19[k]);
//                jppsavalueobject.setICDTdbNo(as16[k]);
//                jppsavalueobject.setICDCargoDescription(as17[k]);
//                jppsavalueobject.setICDMarking(as18[k]);
//                vector.addElement(jppsavalueobject);
//            }
//
//            log.info("the size of the vector :" + vector.size());
//            jppsavalueObj.setCargoDetails(vector);
//        }
//        catch(BusinessException businessexception)
//        {
//            log.info("Writing from gbmsFileManager");
//            log.info("BusinessException: " + businessexception.getMessage());
//        }
//        catch(Exception remoteexception)
//        {
//            log.info("Writing from gbmsFileManager");
//           log.info("Exception: " + remoteexception.getMessage());
//        }

	}

	public void fetchCargoSummary(String s) throws BusinessException {
		String s1 = Constant.SysProperties.IntergatewayCargoSummary_RecordType;
		String s2 = fetchFieldDetails(s1, s);
		if (s2.trim().length() == 0)
			throw new BusinessException("Fetch Cargo Summary : The Create date is invalid");
		String s3 = Constant.SysProperties.IntergatewayCargoSummary_TotalRecordsSubmitted;
		String s4 = fetchFieldDetails(s3, s);
		if (s4.trim().length() == 0) {
			throw new BusinessException("Fetch Cargo Summary : The Create date is invalid");
		} else {
			jppsavalueObj.setICSRecordtype(s2);
			jppsavalueObj.setICSTotalRec(s4);
			jppsavalueObj.setFileName(sFilename);
			return;
		}
	}

	public String getTotalRecSubmitted(String s) throws BusinessException {
		int i = 0;
		i = s.length();
		int j = i - 49;
		int k = j + 8;
		log.info("Text : '" + s + "'");
		log.info("Text : " + s.substring(j, k));
		return s.substring(j, k);
	}

	public String fetchFieldDetails(String s, String s1) {
		StringTokenizer stringtokenizer = new StringTokenizer(s, "~");
		String s2 = stringtokenizer.nextToken();
		int i = Integer.parseInt(s2) + incr;
		String s3 = stringtokenizer.nextToken();
		int j = Integer.parseInt(s3) + incr;
		String s4 = stringtokenizer.nextToken();
		String s5 = stringtokenizer.nextToken();
		return s1.substring(i - 1, j);
	}

	public void insertText(String s, String s1) {
		int i = 0;
		boolean flag = false;
		s1 = CommonUtility.deNull(s1);
		String s2 = s;
		StringTokenizer stringtokenizer = new StringTokenizer(s2, "~");
		String s3 = stringtokenizer.nextToken();
		int k = Integer.parseInt(s3) + incr;
		String s4 = stringtokenizer.nextToken();
		int l = Integer.parseInt(s4) + incr;
		for (int j = k - 1; j < l; j++) {
			byte byte0;
			try {
				byte0 = (byte) s1.charAt(i);
			} catch (StringIndexOutOfBoundsException stringindexoutofboundsexception) {
				byte0 = 32;
			}
			writeByte[j] = byte0;
			i++;
		}

		if (s.equals("IntergatewayCargoHeader.Filler") || s.equals("IntergatewayCargoDetails.Filler")
				|| s.equals("IntergatewayCargoSummary.Filler")) {
			writeByte[l] = 10;
			incr++;
			log.info("this is in " + k + "*" + l);
		}
	}

	public void writeJPPSA(String s, String s1) throws BusinessException {
		try {
			GregorianCalendar gregoriancalendar = new GregorianCalendar();
			int i = gregoriancalendar.get(0);
			int j = gregoriancalendar.get(1);
			int k = gregoriancalendar.get(2) + 1;
			String s2 = k + "";
			if (s2.length() == 1)
				s2 = "0" + s2;
			int l = gregoriancalendar.get(5);
			String s3 = l + "";
			if (s3.length() == 1)
				s3 = "0" + s3;
			int i1 = gregoriancalendar.get(10);
			String s4 = i1 + "";
			if (s4.length() == 1)
				s4 = "0" + s4;
			int j1 = gregoriancalendar.get(12);
			String s5 = j1 + "";
			if (s5.length() == 1)
				s5 = "0" + s5;
			int k1 = gregoriancalendar.get(13);
			String s6 = k1 + "";
			if (s6.length() == 1)
				s6 = "0" + s6;
			int l1 = gregoriancalendar.get(14);
			String s7 = l1 + "";
			s7 = s7.substring(0, 3);
			List<JPPSAValueObject> vector = new Vector();

			String s8 = outGoingFilePath;

			vector = jppsaRepository.searchByVoyage(s, s1);
			String s9 = "IGD" + j + s2 + s3 + s4 + s5 + s6 + s7 + ".txt";
			int i2 = writeVoToText((JPPSAValueObject) vector.get(0), s8 + s9, s9);
			if (i2 == 1)
				jppsaRepository.updateIGD(s9, s, s1, "A");
			else
				jppsaRepository.updateIGD(s9, s, s1, "E");
		} catch (BusinessException businessexception) {
			log.info("Writing from gbmsFileManager");
			log.info("BusinessException: " + businessexception.getMessage());
		} catch (Exception exception) {
			log.info("Exception writeJPPSA", exception );
		}
	}

	public int writeVoToText(JPPSAValueObject jppsavalueobject, String s, String s1) throws IOException {
		int i = 0;
		FileOutputStream fileoutputstream = null,fileoutputstream1=null;
		try {
			log.info("Starting writeVoToText. File Name : " + s);
			incr = 0;
			insertText(Constant.SysProperties.IntergatewayCargoHeader_RecordType, "H");
			insertText(Constant.SysProperties.IntergatewayCargoHeader_CreationDate, jppsavalueobject.getICHCreateDate());
			insertText(Constant.SysProperties.IntergatewayCargoHeader_AbbVesselName, jppsavalueobject.getICHAbbVslName());
			insertText(Constant.SysProperties.IntergatewayCargoHeader_AbbVoyNumber, jppsavalueobject.getICHDisAbbVslName());
			insertText(Constant.SysProperties.IntergatewayCargoHeader_Filler, " ");
			incr = incr + Integer.parseInt(Constant.SysProperties.IntergatewayCargoHeader_FileSize);
			for (int j = 0; j < jppsavalueobject.getCargoDetails().size(); j++) {
				log.info("inside the writing loop of details");
				JPPSAValueObject jppsavalueobject1 = new JPPSAValueObject();
				List<JPPSAValueObject> vector = jppsavalueobject.getCargoDetails();
				jppsavalueobject1 = (JPPSAValueObject) vector.get(j);
				insertText(Constant.SysProperties.IntergatewayCargoDetails_RecordType, jppsavalueobject1.getICDRecordType());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_Function, jppsavalueobject1.getICDFunction());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_BillofLading, jppsavalueobject1.getICDBillofLading());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_HSCode, jppsavalueobject1.getICDHScode());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_PackagingType, jppsavalueobject1.getICDPackagingType());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_NumberOfPackage,
						formatValues(jppsavalueobject1.getICDNoofPackage()));
				insertText(Constant.SysProperties.IntergatewayCargoDetails_weight, jppsavalueobject1.getICDWeight());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_volume, jppsavalueobject1.getICDVolume());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_ShipperName, jppsavalueobject1.getICDShipperName());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_CargoType, jppsavalueobject1.getICDCargoType());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_LoadingVessel, jppsavalueobject1.getICDLoadingVessel());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_LoadingVoyage, jppsavalueobject1.getICDLoadingVoyage());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_PortsOfDischarge, jppsavalueobject1.getICDPortOfDischarge());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_ContainerNumber, jppsavalueobject1.getICDContainerNumber());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_DirectionOfIntergateway,
						jppsavalueobject1.getICDDirectInterGateWay());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_BookingReferenceOfLoadingVesel,
						jppsavalueobject1.getICDBookingRef());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_AccountNumber, jppsavalueobject1.getICDAccount());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_TDBNo, jppsavalueobject1.getICDTdbNo());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_CargoDescription, jppsavalueobject1.getICDCargoDescription());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_Marking, jppsavalueobject1.getICDMarking());
				insertText(Constant.SysProperties.IntergatewayCargoDetails_Filler, " ");
				incr = incr + Integer.parseInt(Constant.SysProperties.IntergatewayCargoDetails_FileSize);
			}

			insertText(Constant.SysProperties.IntergatewayCargoSummary_RecordType, "S");
			String s2 = "";
			String s3 = jppsavalueobject.getCargoDetails().size() + "";
			for (int k = 0; k < 8 - s3.length(); k++)
				s2 = s2 + "0";

			s3 = s2 + s3;
			insertText(Constant.SysProperties.IntergatewayCargoSummary_TotalRecordsSubmitted, "" + s3 + "");
			insertText(Constant.SysProperties.IntergatewayCargoSummary_Filler, "");
			String s4 = outGoingArchival;
			GregorianCalendar gregoriancalendar = new GregorianCalendar();
			int l = gregoriancalendar.get(0);
			int i1 = gregoriancalendar.get(1);
			int j1 = gregoriancalendar.get(2) + 1;
			String s5 = j1 + "";
			if (s5.length() == 1)
				s5 = "0" + s5;
			int k1 = gregoriancalendar.get(5);
			String s6 = k1 + "";
			if (s6.length() == 1)
				s6 = "0" + s6;
			int l1 = gregoriancalendar.get(10);
			String s7 = l1 + "";
			if (s7.length() == 1)
				s7 = "0" + s7;
			int i2 = gregoriancalendar.get(12);
			String s8 = i2 + "";
			if (s8.length() == 1)
				s8 = "0" + s8;
			int j2 = gregoriancalendar.get(13);
			String s9 = j2 + "";
			if (s9.length() == 1)
				s9 = "0" + s9;
			fileoutputstream = new FileOutputStream(s);
			fileoutputstream.write(writeByte);
			fileoutputstream.close();
			File file = new File(s4 + i1 + s5);
			boolean flag = file.mkdir();
			log.info(flag);
			fileoutputstream1 = new FileOutputStream(s4 + i1 + s5 + "/" + s1);
			fileoutputstream1.write(writeByte);
			fileoutputstream1.close();
			i = 1;
		} catch (FileNotFoundException filenotfoundexception) {
			log.info("file cant be created" + filenotfoundexception.getMessage());
			i = 0;
		} catch (IOException ioexception) {
			log.info("Failed :" + ioexception.getMessage());
			i = 0;
		} catch (Exception exception) {
			log.info("writeVoToText Unexpected Error");
			log.info("Exception writeVoToText", exception );
		} finally {
			if(i == 1) {
				fileoutputstream.close();
				fileoutputstream1.close();
			}
		}
		return i;
	}

	
	public String insertIGD(JPPSAValueObject jppsavalueobject, Criteria criteria) {
		String s = "";
		try {
			log.info("vect size " + jppsavalueobject.getCargoDetails().size());
			if (criteria != null) {
				String userAccount = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
				jppsavalueobject.setUser(userAccount);
			}

			s = jppsaRepository.insertIGD(jppsavalueobject);
		} catch (BusinessException businessexception) {
			log.info("Writing from gbmsFileManager");
			log.info("BusinessException: " + businessexception.getMessage());
		} catch (Exception exception) {
			log.info("Exception insertIGD", exception );
		}
		return s;
	}

	private Context getInitialContext() throws NamingException {
		String s = jndiInitContextUrl;
		Object obj = null;
		Object obj1 = null;
		Properties properties = null;
		try {
			properties = new Properties();
			properties.put("java.naming.factory.initial", "weblogic.jndi.WLInitialContextFactory");
			properties.put("java.naming.provider.url", s);
			if (obj != null) {
				properties.put("java.naming.security.principal", obj);
				properties.put("java.naming.security.credentials", obj1 != null ? ((Object) (obj1)) : "");
			}
		} catch (Exception exception) {
			log.info("Exception getInitialContext", exception );
		}
		return new InitialContext(properties);
	}

//	private ArrayList loadData(String s) throws FileNotFoundException, IOException {
//		Object obj = null;
//		Object obj2 = null;
//		Object obj4 = null;
//		Object obj6 = null;
//		ArrayList arraylist = new ArrayList();
//		try {
//			File file = new File(s);
//			if (!file.exists()) {
//				ArrayList arraylist1 = null;
//				return arraylist1;
//			}
//			FileReader filereader = new FileReader(file);
//			BufferedReader bufferedreader = new BufferedReader(filereader);
//			for (String s1 = bufferedreader.readLine(); s1 != null; s1 = bufferedreader.readLine())
//				if (s1.trim().length() != 0)
//					arraylist.add(s1);
//
//			ArrayList arraylist2 = arraylist;
//			bufferedreader.close();
//			return arraylist2;
//		} catch (Exception e) {
//			log.info("Exception Gbms FileManager", e);
//		} finally {
//			Object obj5 = null;
//			Object obj3 = null;
//			Object obj1 = null;
//		}
//		return null;
//	}

	public String formatValues(String s) {
		boolean flag = false;
		String as[] = new String[15];
		int i = 0;
		int j = 0;
		for (int k = 0; k < s.trim().length(); k++) {
			if (s.charAt(k) == '.')
				flag = true;
			if (flag)
				i++;
			else
				j++;
		}

		int l = 11 - j;
		int i1 = (4 - i) + 1;
		int j1 = i1;
		int k1 = 0;
		for (int l1 = 0; l1 < 11; l1++)
			if (l1 < l) {
				as[l1] = "0";
			} else {
				as[l1] = s.charAt(k1) + "";
				k1++;
			}

		k1++;
		int i2 = 0;
		for (int j2 = 11; j2 < 15; j2++)
			if (i1 > 0) {
				i1--;
				as[14 - i2] = "0";
				i2++;
			}

		for (int k2 = 11; k2 < 15 - j1; k2++) {
			as[k2] = s.charAt(k1) + "";
			k1++;
		}

		String s1 = "";
		for (int l2 = 0; l2 <= 14; l2++)
			s1 = s1 + as[l2];

		return s1;
	}

	public String textfile;
	public int incr;
	byte writeByte[];
	JPPSAValueObject jppsavalueObj;
	String disvoynbrForErr;
	String sFilename;
}
