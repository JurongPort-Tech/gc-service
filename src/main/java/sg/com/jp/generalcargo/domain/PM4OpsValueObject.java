package sg.com.jp.generalcargo.domain;

import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * System Name: TOPs (Terminal Operation and Planning System)
 * Component ID: PM4OpsValueObject.java 
 *
 * Component Description: Value Object for PM4
 *
 * @author      Alvin Chia  
 * @version     11 September 2003
 */

/*
 * Revision History
 * ----------------
 * Author       Request Number  Description of Change   Version     Date Released
 * Alvin Chia   CTCIM20030013   Creation                1.0         11/09/2003
 * SBala        CTCIM20030013   Add methods for newly
 added calss variables.  1.3         03/10/2003
 * Alvin Chia	CTCIM20030013	Add new field 			1.4			09/03/2004
 *								cntr_dg_wt                   
 * Alvin Chia	CTCIM20030013	Add new field 			1.5			10/03/2004
 *								cntr_teu
 *
 * Suba							New static variable added.			24-Mar-2005
 * 
 * Sudha Kamble				New methods added for the
 * 								new filters CO, Inward So, 
 * 								Outward SO							11/04/2005
 * NgocNN1						add new attribute contactNbr		29/05/2008 
 * Sripriya                     add new attribute ith_mode          06/07/2012
 */

public class PM4OpsValueObject implements TopsIObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String DG_IND_YES = "Y";

	public static final String DG_IND_NO = "N";

	public static final String IMO_CL_NA = "NA";

	public static final String STORAGE_CD_NA = "NA";

	// Vessel Info Variables
	private String pm4_id; // pm4_id

	private String inward_pm4_id; // inward_pm4_id

	private String record_type; // record_type

	private String version_no; // version_nbr

	private String user_id; // user_id *

	private String user_nm; // user_nm *

	private String system_date; // submit_dttm

	private String org_crn; // org_crn *

	private String org_nm; // org_nm *

	private String vsl_full_nm; // vsl_full_nm *

	private String vessel_name; // vsl_abbr_nm

	private String mpa_vsl_id; // mpa_vsl_id *

	// System.out.println here
	private String voyage; // voy_nbr

	private String txn_dt; // txn_dttm *

	private String gateway; // gateway

	private String eta; // eta_dttm

	private String arrival_dt; // arr_dttm

	private String ucr_no; // ucr_nbr

	private String container_no; // cntr_nbr

	private String prev_cntr_nbr; // prev_cntr_nbr *

	private String cntr_seq_nbr; // cntr_seq_nbr *

	private String voyage_code; // vv_cd

	private String container_status; // cntr_status

	private String tank_container; // tank_ind

	private String stowage; // stowage

	private String quantity; // nbr_of_pkg

	private String weight; // weight

	private String type_of_package; // packing_type

	private String un_no; // un_nbr

	private String imo_class; // imo_cl

	private String substance; // substance

	private String flashpoint; // flash_point

	private String wt_limit_cd; // wt_limit_cd *

	private String chemist_vet_ind; // chemist_vet_ind *

	private String chemist_reason; // chemist_reason *

	private String mpa_grp; // mpa_group

	private String jp_grp; // jp_group

	private String mpa_approval_st; // mpa_appv_status

	private String jp_approval_st; // jp_appv_status

	private String mpa_approval_dttm; // mpa_appv_dttm

	private String jp_approval_dttm; // jp_appv_dttm

	private String mpa_remarks; // mpa_remarks

	private String jp_remarks; // jp_remarks

	private String mpa_error; // mpa_errpr

	private String cntr_grp; // cntr_group

	private String dg_storage_cd; // dg_storage_cd

	private String cntr_storage_cd; // cntr_storage_cd

	private String close_ind; // close_ind *

	private String last_modify_user_id; // last_modify_user_id *

	private String last_modify_dttm; // last_modify_dttm *

	private boolean objMod; // Value for DGMS to set if obj has been modified

	private String cntr_dg_wt;

	private String cntr_teu;

	private String cntr_imo_class;

	private boolean onlineStatus;

	// Misc
	private String voyage_in;

	private String voyage_out;

	// sudha
	private String cntrOperator;

	private String inwardSO;

	private String outwardSO;

	// sudha end

	private String operation; // opr_type

	private String new_container_no;

	private String bl_nbr;

	private String ucr_nbr;

	private Date txn_dttm;

	private String cntr_rec_created;

	private ArrayList imo_class_list;

	private String msg_type;

	private String file_nm;

	private String source_of_appl;

	private int process_ind;

	private String msg_id;

	// For CheckPM4
	private String load_vv_cd;

	private String disc_vv_cd;

	private String nom_load_vv_cd;

	private String nom_disc_vv_cd;

	private String cntr_status; // The Container status in the container table not in the PM4 table

	private String shipment_status;

	private String func_id;

	private String loadGateway;

	private String discGateway;

	private String newLoadGateway;
	// NgocNN1 add on 29-May-2008
	private String contactNbr;
	// NgocNN1 end on 29-May-2008

	// sripriya 4 Jan 2012
	private String terminal;

	private String ct_planned_disc;
	private String ith_mode;

	private String residueInd;
	private String technicalName; // technical name
	private String controlTemp;
	private String sadt;
	private String emergencyTemp;
	private String wasteInd;
	private String contactEmail;
	private String subsidiary1;
	private String subsidiary2;
	private String subsidiary3;
	private String subsidiary4;
	private String varNbr;
	private String uen_nbr;
	private Date berth;
	private String autoVetInd; // auto vet indicator

	/** Creates new PM4OpsValueObject */
	public PM4OpsValueObject() {
		objMod = false;
		onlineStatus = false;
	}

	/***************************************************************************
	 * Codes below are functions that set the values
	 ***************************************************************************/

	public void setCntrTeu(String cntrTeu) {
		cntr_teu = cntrTeu;
	}

	public void setCntrDgWt(String cntrDgWt) {
		cntr_dg_wt = cntrDgWt;
	}

	public void setObjMod(boolean ObjMod) {
		objMod = ObjMod;
	}

	public void setCntrImoClass(String CntrImoClass) {
		cntr_imo_class = CntrImoClass;
	}

	public void setLoadVvCd(String LoadVvCd) {
		load_vv_cd = LoadVvCd;
	}

	public void setDiscVvCd(String DiscVvCd) {
		disc_vv_cd = DiscVvCd;
	}

	public void setNomLoadVvCd(String NomLoadVvCd) {
		nom_load_vv_cd = NomLoadVvCd;
	}

	public void setNomDiscVvCd(String NomDiscVvCd) {
		nom_disc_vv_cd = NomDiscVvCd;
	}

	public void setCntrStatus(String CntrStatus) {
		cntr_status = CntrStatus;
	}

	public void setShipmentStatus(String ShipmentStatus) {
		shipment_status = ShipmentStatus;
	}

	public void setFuncId(String FuncId) {
		func_id = FuncId;
	}

	public void setLoadGateway(String LoadGateway) {
		loadGateway = LoadGateway;
	}

	public void setDiscGateway(String DiscGateway) {
		discGateway = DiscGateway;
	}

	public void setNewLoadGateway(String NewLoadGateway) {
		newLoadGateway = NewLoadGateway;
	}

	public void setVesselName(String VesselName) {
		vessel_name = VesselName;
	}

	public void setVoyage(String Voyage) {
		voyage = Voyage;
	}

	public void setVoyageIn(String VoyageIn) {
		voyage_in = VoyageIn;
	}

	public void setVoyageOut(String VoyageOut) {
		voyage_out = VoyageOut;
	}

	public void setGateway(String Gateway) {
		gateway = Gateway;
	}

	public void setArrivalDt(String ArrivalDt) {
		arrival_dt = ArrivalDt;
	}

	public void setETA(String ETA) {
		eta = ETA;
	}

	public void setContainerNo(String ContainerNo) {
		container_no = ContainerNo;
	}

	public void setNewContainerNo(String NewContainerNo) {
		new_container_no = NewContainerNo;
	}

	public void setUCRNo(String UCRNo) {
		ucr_no = UCRNo;
	}

	public void setSystemDate(String SystemDate) {
		system_date = SystemDate;
	}

	public void setPM4Id(String PM4Id) {
		pm4_id = PM4Id;
	}

	public void setInwardPM4Id(String inwardPM4Id) {
		inward_pm4_id = inwardPM4Id;
	}

	public void setVersionNo(String VersionNo) {
		version_no = VersionNo;
	}

	public void setStowage(String Stowage) {
		stowage = Stowage;
	}

	public void setOperation(String Operation) {
		operation = Operation;
	}

	public void setContainerStatus(String ContainerStatus) {
		container_status = ContainerStatus;
	}

	public void setRecordType(String RecordType) {
		record_type = RecordType;
	}

	public void setMPAApprovalSt(String MPAApprovalSt) {
		mpa_approval_st = MPAApprovalSt;
	}

	public void setJPApprovalSt(String JPApprovalSt) {
		jp_approval_st = JPApprovalSt;
	}

	public void setMPAApprovalDttm(String MPAApprovalDttm) {
		mpa_approval_dttm = MPAApprovalDttm;
	}

	public void setJPApprovalDttm(String JPApprovalDttm) {
		jp_approval_dttm = JPApprovalDttm;
	}

	public void setMPAGrp(String MPAGrp) {
		mpa_grp = MPAGrp;
	}

	public void setJPGrp(String JPGrp) {
		jp_grp = JPGrp;
	}

	public void setMPARemarks(String MPARemarks) {
		mpa_remarks = MPARemarks;
	}

	public void setJPRemarks(String JPRemarks) {
		jp_remarks = JPRemarks;
	}

	public void setMPAError(String MPAError) {
		mpa_error = MPAError;
	}

	public void setIMOClass(String IMOClass) {
		imo_class = IMOClass;
	}

	public void setUNNo(String UNNo) {
		un_no = UNNo;
	}

	public void setQuantity(String Quantity) {
		quantity = Quantity;
	}

	public void setWeight(String Weight) {
		weight = Weight;
	}

	public void setTypeOfPackage(String TypeOfPackage) {
		type_of_package = TypeOfPackage;
	}

	public void setFlashPoint(String FlashPoint) {
		flashpoint = FlashPoint;
	}

	public void setSubstance(String Substance) {
		substance = Substance;
	}

	public void setIMOClassList(ArrayList IMOClassList) {
		imo_class_list = IMOClassList;
	}

	public void setTankContainer(String TankContainer) {
		tank_container = TankContainer;
	}

	public void setChemistVetInd(String chemistVetInd) {
		chemist_vet_ind = chemistVetInd;
	}

	public void setChemistReason(String ChemistReason) {
		chemist_reason = ChemistReason;
	}

	public void setVoyageCode(String VoyageCode) {
		voyage_code = VoyageCode;
	}

	public void setCntrGrp(String CntrGrp) {
		cntr_grp = CntrGrp;
	}

	public void setDGStorageCd(String DGStorageCd) {
		dg_storage_cd = DGStorageCd;
	}

	public void setCntrStorageCd(String CntrStorageCd) {
		cntr_storage_cd = CntrStorageCd;
	}

	public void setCntrRecCreated(String CntrRecCreated) {
		cntr_rec_created = CntrRecCreated;
	}

	public void setUserId(String userId) {
		this.user_id = userId;
	}

	public void setUserName(String userName) {
		this.user_nm = userName;
	}

	public void setOrgCrn(String orgCrn) {
		this.org_crn = orgCrn;
	}

	public void setOrgName(String orgName) {
		this.org_nm = orgName;
	}

	public void setVslFullName(String vslFullName) {
		this.vsl_full_nm = vslFullName;
	}

	public void setMpaVslId(String mpaVslId) {
		this.mpa_vsl_id = mpaVslId;
	}

	public void setTxnDate(String txnDt) {
		txn_dt = txnDt;
	}

	public void setTxnDateTime(java.util.Date txnDttm) {
		this.txn_dttm = txnDttm;
	}

	public void setBlNbr(String blNbr) {
		this.bl_nbr = blNbr;
	}

	public void setUcrNbr(String ucrNbr) {
		this.ucr_nbr = ucrNbr;
	}

	public void setPreCntrNbr(String preCntrNbr) {
		this.prev_cntr_nbr = preCntrNbr;
	}

	public void setCntrSeqNbr(String cntrSeqNbr) {
		this.cntr_seq_nbr = cntrSeqNbr;
	}

	public void setWtLimitCd(String WtLimitCd) {
		this.wt_limit_cd = WtLimitCd;
	}

	public void setLastModifyUserId(String lastModifyUserId) {
		this.last_modify_user_id = lastModifyUserId;
	}

	public void setLastModifyDttm(String lastModifyDttm) {
		this.last_modify_dttm = lastModifyDttm;
	}

	public void setCloseInd(String CloseInd) {
		close_ind = CloseInd;
	}

	public void setMsgType(String msg_type) {
		this.msg_type = msg_type;
	}

	public void setFileName(String file_name) {
		this.file_nm = file_name;
	}

	public void setSourceOfApproval(String sourceOfAppl) {
		this.source_of_appl = sourceOfAppl;
	}

	public void setProcessInd(int process_ind) {
		this.process_ind = process_ind;
	}

	public void setMsgId(String msg_id) {
		this.msg_id = msg_id;
	}

	public void setOnlineStatus(boolean OnlineStatus) {
		onlineStatus = OnlineStatus;
	}

	/***************************************************************************
	 * Codes below are functions that returns the values
	 ***************************************************************************/

	public boolean getObjMod() {
		return objMod;
	}

	public String getCntrImoClass() {
		return cntr_imo_class;
	}

	public String getVesselName() {
		return vessel_name;
	}

	public String getVoyage() {
		return voyage;
	}

	public String getVoyageOut() {
		return voyage_out;
	}

	public String getVoyageIn() {
		return voyage_in;
	}

	public String getGateway() {
		return gateway;
	}

	public String getArrivalDt() {
		return arrival_dt;
	}

	public String getETA() {
		return eta;
	}

	public String getSystemDate() {
		return system_date;
	}

	public String getContainerNo() {
		return container_no;
	}

	public String getNewContainerNo() {
		return new_container_no;
	}

	public String getUCRNo() {
		return ucr_no;
	}

	public String getPM4Id() {
		return pm4_id;
	}

	public String getInwardPM4Id() {
		return inward_pm4_id;
	}

	public String getVersionNo() {
		return version_no;
	}

	public String getStowage() {
		return stowage;
	}

	public String getOperation() {
		return operation;
	}

	public String getContainerStatus() {
		return container_status;
	}

	public String getRecordType() {
		return record_type;
	}

	public String getQuantity() {
		return quantity;
	}

	public String getWeight() {
		return weight;
	}

	public String getTypeOfPackage() {
		return type_of_package;
	}

	public String getMPAApprovalSt() {
		return mpa_approval_st;
	}

	public String getJPApprovalSt() {
		return jp_approval_st;
	}

	public String getMPAApprovalDttm() {
		return mpa_approval_dttm;
	}

	public String getJPApprovalDttm() {
		return jp_approval_dttm;
	}

	public String getMPAGrp() {
		return mpa_grp;
	}

	public String getJPGrp() {
		return jp_grp;
	}

	public String getMPARemarks() {
		return mpa_remarks;
	}

	public String getJPRemarks() {
		return jp_remarks;
	}

	public String getMPAError() {
		return mpa_error;
	}

	public String getIMOClass() {
		return imo_class;
	}

	public String getUNNo() {
		return un_no;
	}

	public String getFlashPoint() {
		return flashpoint;
	}

	public String getSubstance() {
		return substance;
	}

	public ArrayList getIMOClassList() {
		return imo_class_list;
	}

	public String getTankContainer() {
		return tank_container;
	}

	public String getChemistVetInd() {
		return chemist_vet_ind;
	}

	public String getChemistReason() {
		return chemist_reason;
	}

	public String getVoyageCode() {
		return voyage_code;
	}

	public String getCntrGrp() {
		return cntr_grp;
	}

	public String getDGStorageCd() {
		return dg_storage_cd;
	}

	public String getCntrStorageCd() {
		return cntr_storage_cd;
	}

	public String getCntrRecCreated() {
		return cntr_rec_created;
	}

	public String getUserId() {
		return this.user_id;
	}

	public String getUserName() {
		return this.user_nm;
	}

	public String getOrgCrn() {
		return this.org_crn;
	}

	public String getOrgName() {
		return this.org_nm;
	}

	public String getVslFullName() {
		return this.vsl_full_nm;
	}

	public String getMpaVslId() {
		return this.mpa_vsl_id;
	}

	public java.util.Date getTxnDateTime() {
		return this.txn_dttm;
	}

	public String getTxnDate() {
		return txn_dt;
	}

	public String getBlNbr() {
		return this.bl_nbr;
	}

	public String getUcrNbr() {
		return this.ucr_nbr;
	}

	public String getPreCntrNbr() {
		return this.prev_cntr_nbr;
	}

	public String getCntrSeqNbr() {
		return this.cntr_seq_nbr;
	}

	public String getWtLimitCd() {
		return this.wt_limit_cd;
	}

	public String getLastModifyUserId() {
		return this.last_modify_user_id;
	}

	public String getLastModifyDttm() {
		return this.last_modify_dttm;
	}

	public String getCloseInd() {
		return close_ind;
	}

	public String getMsgType() {
		return this.msg_type;
	}

	public String getFileName() {
		return this.file_nm;
	}

	public String getSourceOfApproval() {
		return this.source_of_appl;
	}

	public int getProcessInd() {
		return this.process_ind;
	}

	public String getMsgId() {
		return this.msg_id;
	}

	public String getLoadVvCd() {
		return load_vv_cd;
	}

	public String getDiscVvCd() {
		return disc_vv_cd;
	}

	public String getNomLoadVvCd() {
		return nom_load_vv_cd;
	}

	public String getNomDiscVvCd() {
		return nom_disc_vv_cd;
	}

	public String getCntrStatus() {
		return cntr_status;
	}

	public String getShipmentStatus() {
		return shipment_status;
	}

	public String getFuncId() {
		return func_id;
	}

	public String getLoadGateway() {
		return loadGateway;
	}

	public String getDiscGateway() {
		return discGateway;
	}

	public String getNewLoadGateway() {
		return newLoadGateway;
	}

	public boolean getOnlineStatus() {
		return onlineStatus;
	}

	public String getCntrDgWt() {
		return cntr_dg_wt;
	}

	public String getCntrTeu() {
		return cntr_teu;
	}

	// START: Sudha

	public String getCntrOperator() {
		return cntrOperator;
	}

	public void setCntrOperator(String cntrOperator) {
		this.cntrOperator = cntrOperator;
	}

	public String getInwardSO() {
		return inwardSO;
	}

	public void setInwardSO(String inwardSO) {
		this.inwardSO = inwardSO;
	}

	public String getOutwardSO() {
		return outwardSO;
	}

	public void setOutwardSO(String outwardSO) {
		this.outwardSO = outwardSO;
	}
	// END: Sudha

	// NgocNN1 added on 29-May-2008
	/**
	 * @return the contactNbr
	 */
	public String getContactNbr() {
		return contactNbr;
	}

	/**
	 * @param contactNbr the contactNbr to set
	 */
	public void setContactNbr(String contactNbr) {
		this.contactNbr = contactNbr;
	}
	// END: NgocNN1

	/**
	 * @return the terminal
	 */
	public String getTerminal() {
		return terminal;
	}

	/**
	 * @param terminal the terminal to set
	 */
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	/**
	 * @return the ct_planned_disc
	 */
	public String getCtplanDisc() {
		return ct_planned_disc;
	}

	/**
	 * @param ctPlannedDisc the ct_planned_disc to set
	 */
	public void setCtplanDisc(String ctPlannedDisc) {
		ct_planned_disc = ctPlannedDisc;
	}

	/**
	 * @return the ith_mode
	 */
	public String getIthMode() {
		return ith_mode;
	}

	/**
	 * @param ithMode the ith_mode to set
	 */
	public void setIthMode(String ithMode) {
		ith_mode = ithMode;
	}

	public String getResidueInd() {
		return residueInd;
	}

	public void setResidueInd(String residueInd) {
		this.residueInd = residueInd;
	}

	public String getTechnicalName() {
		return technicalName;
	}

	public void setTechnicalName(String technicalName) {
		this.technicalName = technicalName;
	}

	public String getControlTemp() {
		return controlTemp;
	}

	public void setControlTemp(String controlTemp) {
		this.controlTemp = controlTemp;
	}

	public String getSadt() {
		return sadt;
	}

	public void setSadt(String sadt) {
		this.sadt = sadt;
	}

	public String getEmergencyTemp() {
		return emergencyTemp;
	}

	public void setEmergencyTemp(String emergencyTemp) {
		this.emergencyTemp = emergencyTemp;
	}

	public String getWasteInd() {
		return wasteInd;
	}

	public void setWasteInd(String wasteInd) {
		this.wasteInd = wasteInd;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getSubsidiary1() {
		return subsidiary1;
	}

	public void setSubsidiary1(String subsidiary1) {
		this.subsidiary1 = subsidiary1;
	}

	public String getSubsidiary2() {
		return subsidiary2;
	}

	public void setSubsidiary2(String subsidiary2) {
		this.subsidiary2 = subsidiary2;
	}

	public String getSubsidiary3() {
		return subsidiary3;
	}

	public void setSubsidiary3(String subsidiary3) {
		this.subsidiary3 = subsidiary3;
	}

	public String getSubsidiary4() {
		return subsidiary4;
	}

	public void setSubsidiary4(String subsidiary4) {
		this.subsidiary4 = subsidiary4;
	}

	public String getVarNbr() {
		return varNbr;
	}

	public void setVarNbr(String varNbr) {
		this.varNbr = varNbr;
	}

	public String getUenNbr() {
		return uen_nbr;
	}

	public void setUenNbr(String uen_nbr) {
		this.uen_nbr = uen_nbr;
	}

	public Date getBerth() {
		return berth;
	}

	public void setBerth(Date berth) {
		this.berth = berth;
	}

	public String getAutoVetInd() {
		return autoVetInd;
	}

	public void setAutoVetInd(String autoVetInd) {
		this.autoVetInd = autoVetInd;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

}
