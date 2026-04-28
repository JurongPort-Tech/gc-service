package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ContainerValueObject  implements TopsIObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long cntr_seq_nbr;
    private String txn_status;
    private String cntr_nbr;
    private String Status;
    private String iso_size_type_cd;
    private String size_ft;
    private String ht_ft;
    private String width_ft;
    private String old_new_ind;
    private String type_cd;
    private String cat_cd;
    private long declr_wt;
    private long measure_wt;
    private String wt_class;
    private String Psrc;
    private String Pload;
    private String pdisc1;
    private String pdisc2;
    private String pdisc3;
    private String Pdest;
    private String oog_unit;
    private int oog_oh;
    private int oog_ol_front;
    private int oog_ol_back;
    private int oog_ow_right;
    private int oog_ow_left;
    private String refr_temp;
    private String refr_volt;
    private String imdg_cl_cd;
    private String cntr_opr_cd;
    private String tli_batch_nbr;
    private String imp_haul_cd;
    private String exp_haul_cd;
    private String lolo_party_ind;
    private String disc_slot_opr_cd;
    private String load_slot_opr_cd;
    private String disc_vv_cd;
    private String nom_disc_vv_cd;
    private String nom_load_vv_cd;
    private String load_vv_cd;
    private String doc_status;
    private String chas_prov_ind;
    private String purp_cd;
    private String prev_purp_cd;
    private String trpt_mode_cd;
    private String svc_type_cd;
    private String commodity_cd;
    private String seal_nbr;
    private String bill_lading_nbr;
    private String disc_os_ind;
    private String load_os_ind;
    private String dg_ind;
    private String refr_ind;
    private String uc_ind;
    private String over_sz_ind;
    private String intergateway_ind;
    private String disc_gateway;
    private String load_gateway;
    private String shipment_status;
    private String dir_hdlg_ind;
    private String uc_unit;
    private int uc_len;
    private int uc_width;
    private int uc_ht;
    private String ucr_nbr;
    private long ucr_bundle_nbr;
    private String imp_bay_nbr;
    private String imp_row_nbr;
    private String imp_tier_nbr;
    private String imp_deck_nbr;
    private String imp_bay_pos_cd;
    private String exp_bay_nbr;
    private String exp_row_nbr;
    private String exp_tier_nbr;
    private String exp_deck_nbr;
    private String exp_bay_pos_cd;
    private String create_user_id;
    private String create_org_cd;
    private Timestamp create_dttm;
    private String auth_slip_nbr;
    private Timestamp last_modify_dttm;
    private String special_details;
    private String cargo_desc;
    private long pscw_id_nbr;
    private String last_modify_user_id;
    private String discVesselName;
    private String discVoyageNumber;
    private String nomDiscVesselName;
    private String nomDiscVoyageNumber;
    private String loadVesselName;
    private String loadVoyageNumber;
    private String nomLoadVesselName;
    private String nomLoadVoyageNumber;
    private boolean declr_wt_isNull;
    private boolean measure_wt_isNull;
    private boolean ucr_bundle_nbr_isNull;
    private boolean pscw_id_nbr_isNull;
    private String cyIndicator;
    private String ctPlanDisc;
    private String ctPlanLoad;
    private String ith_exc_mode;
    private boolean mqLink;
    //START: V.Karthic CR-CIM-20050913-4  30-Dec-2005
    private String earlyEDOFlag;   //to store early  EDO flag in order to find out whether 
                                   //early EDO is created or updated or recreated 
    //END: V.Karthic CR-CIM-20050913-4   30-Dec-2005
    //START: FPT-ManhT Blocking Of Container  2-July-2010
    private String isBlocked;
    //END: FPT-ManhT Blocking Of Container  2-July-2010
    private String misc_app_nbr;
    public ContainerValueObject()
    {
        declr_wt_isNull = true;
        measure_wt_isNull = true;
        ucr_bundle_nbr_isNull = true;
        pscw_id_nbr_isNull = true;
        cyIndicator = null;
        ctPlanDisc = null;
        ctPlanLoad = null;
        ith_exc_mode = null;
        declr_wt = 0L;
        oog_oh = 0;
        oog_ol_front = 0;
        oog_ol_back = 0;
        oog_ow_right = 0;
        oog_ow_left = 0;
        uc_len = 0;
        uc_width = 0;
        uc_ht = 0;
    }

    public String getMisc_app_nbr() {
		return misc_app_nbr;
	}

	public void setMisc_app_nbr(String misc_app_nbr) {
		this.misc_app_nbr = misc_app_nbr;
	}

	public String getIsBlock() {
		return isBlocked;
	}

	public void setIsBlock(String isBlock) {
		this.isBlocked = isBlock;
	}

	public boolean declareWeightIsNull()
    {
        return declr_wt_isNull;
    }

    public String getAuthSlipNo()
    {
        return auth_slip_nbr;
    }

    public String getBillLadingNo()
    {
        return bill_lading_nbr;
    }

    public String getCTPlanDischarge()
    {
        return ctPlanDisc;
    }

    public String getCTPlanLoad()
    {
        return ctPlanLoad;
    }

    public String getCYIndicator()
    {
        return cyIndicator;
    }

    public String getCargoDesc()
    {
        return cargo_desc;
    }

    public String getCategoryCode()
    {
        return cat_cd;
    }

    public String getChasProvInd()
    {
        return chas_prov_ind;
    }

    public String getCommodityCode()
    {
        return commodity_cd;
    }

    public String getContainerNo()
    {
        return cntr_nbr;
    }

    public String getContainerOperator()
    {
        return cntr_opr_cd;
    }

    public long getContainerSeqNo()
    {
        return cntr_seq_nbr;
    }

    public Timestamp getCreateDttm()
    {
        return create_dttm;
    }

    public String getCreateOrgCode()
    {
        return create_org_cd;
    }

    public String getCreateUserId()
    {
        return create_user_id;
    }

    public String getDGInd()
    {
        return dg_ind;
    }

    public String getDirHandlingInd()
    {
        return dir_hdlg_ind;
    }

    public String getDiscGateway()
    {
        return disc_gateway;
    }

    public String getDiscOSInd()
    {
        return disc_os_ind;
    }

    public String getDiscSlotOperator()
    {
        return disc_slot_opr_cd;
    }

    public String getDiscVesselName()
    {
        return discVesselName;
    }

    public String getDiscVesselVoyage()
    {
        return disc_vv_cd;
    }

    public String getDiscVoyageNumber()
    {
        return discVoyageNumber;
    }

    public String getDocStatus()
    {
        return doc_status;
    }

    public String getExportBayNo()
    {
        return exp_bay_nbr;
    }

    public String getExportBayPosCode()
    {
        return exp_bay_pos_cd;
    }

    public String getExportDeckNo()
    {
        return exp_deck_nbr;
    }

    public String getExportHaulier()
    {
        return exp_haul_cd;
    }

    public String getExportRowNo()
    {
        return exp_row_nbr;
    }

    public String getExportTierNo()
    {
        return exp_tier_nbr;
    }

    public String getHeightFt()
    {
        return ht_ft;
    }

    public String getITHExcMode()
    {
        return ith_exc_mode;
    }

    public String getImdgClassCode()
    {
        return imdg_cl_cd;
    }

    public String getImportBayNo()
    {
        return imp_bay_nbr;
    }

    public String getImportBayPosCode()
    {
        return imp_bay_pos_cd;
    }

    public String getImportDeckNo()
    {
        return imp_deck_nbr;
    }

    public String getImportHaulier()
    {
        return imp_haul_cd;
    }

    public String getImportRowNo()
    {
        return imp_row_nbr;
    }

    public String getImportTierNo()
    {
        return imp_tier_nbr;
    }

    public String getIntergatewayInd()
    {
        return intergateway_ind;
    }

    public String getIsoCode()
    {
        return iso_size_type_cd;
    }

    public Timestamp getLastModifyDttm()
    {
        return last_modify_dttm;
    }

    public String getLastModifyUserId()
    {
        return last_modify_user_id;
    }

    public String getLoLoPartyInd()
    {
        return lolo_party_ind;
    }

    public String getLoadGateway()
    {
        return load_gateway;
    }

    public String getLoadOSInd()
    {
        return load_os_ind;
    }

    public String getLoadSlotOperator()
    {
        return load_slot_opr_cd;
    }

    public String getLoadVesselName()
    {
        return loadVesselName;
    }

    public String getLoadVesselVoyage()
    {
        return load_vv_cd;
    }

    public String getLoadVoyageNumber()
    {
        return loadVoyageNumber;
    }

    public long getMeasureWeight()
    {
        return measure_wt;
    }

    public String getNomDiscVesselName()
    {
        return nomDiscVesselName;
    }

    public String getNomDiscVesselVoyage()
    {
        return nom_disc_vv_cd;
    }

    public String getNomDiscVoyageNumber()
    {
        return nomDiscVoyageNumber;
    }

    public String getNomLoadVesselName()
    {
        return nomLoadVesselName;
    }

    public String getNomLoadVesselVoyage()
    {
        return nom_load_vv_cd;
    }

    public String getNomLoadVoyageNumber()
    {
        return nomLoadVoyageNumber;
    }

    public String getOldNewInd()
    {
        return old_new_ind;
    }

    public int getOogOH()
    {
        return oog_oh;
    }

    public int getOogOlBack()
    {
        return oog_ol_back;
    }

    public int getOogOlFront()
    {
        return oog_ol_front;
    }

    public int getOogOwLeft()
    {
        return oog_ow_left;
    }

    public int getOogOwRight()
    {
        return oog_ow_right;
    }

    public String getOogUnit()
    {
        return oog_unit;
    }

    public String getOverSizeInd()
    {
        return over_sz_ind;
    }

    public String getPdest()
    {
        return Pdest;
    }

    public String getPdisc1()
    {
        return pdisc1;
    }

    public String getPdisc2()
    {
        return pdisc2;
    }

    public String getPdisc3()
    {
        return pdisc3;
    }

    public String getPload()
    {
        return Pload;
    }

    public String getPreviousPurposeCode()
    {
        return prev_purp_cd;
    }

    public long getPscwIdNo()
    {
        return pscw_id_nbr;
    }

    public String getPsrc()
    {
        return Psrc;
    }

    public String getPurposeCode()
    {
        return purp_cd;
    }

    public String getReeferInd()
    {
        return refr_ind;
    }

    public String getReeferTemp()
    {
        return refr_temp;
    }

    public String getReeferVolt()
    {
        return refr_volt;
    }

    public String getSealNo()
    {
        return seal_nbr;
    }

    public String getShipmentStatus()
    {
        return shipment_status;
    }

    public String getSizeFt()
    {
        return size_ft;
    }

    public String getSpecialDetails()
    {
        return special_details;
    }

    public String getStatus()
    {
        return Status;
    }

    public String getSvcTypeCode()
    {
        return svc_type_cd;
    }

    public String getTLIBatchNo()
    {
        return tli_batch_nbr;
    }

    public String getTrptModeCode()
    {
        return trpt_mode_cd;
    }

    public String getTxnStatus()
    {
        return txn_status;
    }

    public String getTypeCode()
    {
        return type_cd;
    }

    public int getUCHeight()
    {
        return uc_ht;
    }

    public String getUCInd()
    {
        return uc_ind;
    }

    public int getUCLength()
    {
        return uc_len;
    }

    public long getUCRBundleNo()
    {
        return ucr_bundle_nbr;
    }

    public String getUCRNo()
    {
        return ucr_nbr;
    }

    public String getUCUnit()
    {
        return uc_unit;
    }

    public int getUCWidth()
    {
        return uc_width;
    }

    public long getWeight()
    {
        return declr_wt;
    }

    public String getWeightClass()
    {
        return wt_class;
    }

    public String getWidthFt()
    {
        return width_ft;
    }

    public boolean isMqLink()
    {
        return mqLink;
    }

    public boolean measureWeightIsNull()
    {
        return measure_wt_isNull;
    }

    public boolean pscwIdNoIsNull()
    {
        return pscw_id_nbr_isNull;
    }

    public void setAuthSlipNo(String s)
    {
        auth_slip_nbr = s;
    }

    public void setBillLadingNo(String s)
    {
        bill_lading_nbr = s;
    }

    public void setCTPlanDischarge(String s)
    {
        ctPlanDisc = s;
    }

    public void setCTPlanLoad(String s)
    {
        ctPlanLoad = s;
    }

    public void setCYIndicator(String s)
    {
        cyIndicator = s;
    }

    public void setCargoDesc(String s)
    {
        cargo_desc = s;
    }

    public void setCategoryCode(String s)
    {
        cat_cd = s;
    }

    public void setChasProvInd(String s)
    {
        chas_prov_ind = s;
    }

    public void setCommodityCode(String s)
    {
        commodity_cd = s;
    }

    public void setContainerNo(String s)
    {
        cntr_nbr = s;
    }

    public void setContainerOperator(String s)
    {
        cntr_opr_cd = s;
    }

    public void setContainerSeqNo(long l)
    {
        cntr_seq_nbr = l;
    }

    public void setCreateDttm(Timestamp timestamp)
    {
        create_dttm = timestamp;
    }

    public void setCreateOrgCode(String s)
    {
        create_org_cd = s;
    }

    public void setCreateUserId(String s)
    {
        create_user_id = s;
    }

    public void setDGInd(String s)
    {
        dg_ind = s;
    }

    public void setDirHandlingInd(String s)
    {
        dir_hdlg_ind = s;
    }

    public void setDiscGateway(String s)
    {
        disc_gateway = s;
    }

    public void setDiscOSInd(String s)
    {
        disc_os_ind = s;
    }

    public void setDiscSlotOperator(String s)
    {
        disc_slot_opr_cd = s;
    }

    public void setDiscVesselName(String s)
    {
        discVesselName = s;
    }

    public void setDiscVesselVoyage(String s)
    {
        disc_vv_cd = s;
    }

    public void setDiscVoyageNumber(String s)
    {
        discVoyageNumber = s;
    }

    public void setDocStatus(String s)
    {
        doc_status = s;
    }

    public void setExportBayNo(String s)
    {
        exp_bay_nbr = s;
    }

    public void setExportBayPosCode(String s)
    {
        exp_bay_pos_cd = s;
    }

    public void setExportDeckNo(String s)
    {
        exp_deck_nbr = s;
    }

    public void setExportHaulier(String s)
    {
        exp_haul_cd = s;
    }

    public void setExportRowNo(String s)
    {
        exp_row_nbr = s;
    }

    public void setExportTierNo(String s)
    {
        exp_tier_nbr = s;
    }

    public void setHeightFt(String s)
    {
        ht_ft = s;
    }

    public void setITHExcMode(String s)
    {
        ith_exc_mode = s;
        if(s != null && s.equalsIgnoreCase("M"))
            mqLink = true;
        else
            mqLink = false;
    }

    public void setImdgClassCode(String s)
    {
        imdg_cl_cd = s;
    }

    public void setImportBayNo(String s)
    {
        imp_bay_nbr = s;
    }

    public void setImportBayPosCode(String s)
    {
        imp_bay_pos_cd = s;
    }

    public void setImportDeckNo(String s)
    {
        imp_deck_nbr = s;
    }

    public void setImportHaulier(String s)
    {
        imp_haul_cd = s;
    }

    public void setImportRowNo(String s)
    {
        imp_row_nbr = s;
    }

    public void setImportTierNo(String s)
    {
        imp_tier_nbr = s;
    }

    public void setIntergatewayInd(String s)
    {
        intergateway_ind = s;
    }

    public void setIsoCode(String s)
    {
        iso_size_type_cd = s;
    }

    public void setLastModifyDttm(Timestamp timestamp)
    {
        last_modify_dttm = timestamp;
    }

    public void setLastModifyUserId(String s)
    {
        last_modify_user_id = s;
    }

    public void setLoLoPartyInd(String s)
    {
        lolo_party_ind = s;
    }

    public void setLoadGateway(String s)
    {
        load_gateway = s;
    }

    public void setLoadOSInd(String s)
    {
        load_os_ind = s;
    }

    public void setLoadSlotOperator(String s)
    {
        load_slot_opr_cd = s;
    }

    public void setLoadVesselName(String s)
    {
        loadVesselName = s;
    }

    public void setLoadVesselVoyage(String s)
    {
        load_vv_cd = s;
    }

    public void setLoadVoyageNumber(String s)
    {
        loadVoyageNumber = s;
    }

    public void setMeasureWeight(long l)
    {
        measure_wt_isNull = false;
        measure_wt = l;
    }

    public void setMqLink(boolean flag)
    {
        mqLink = flag;
        if(flag)
            ith_exc_mode = "M";
        else
            ith_exc_mode = "S";
    }

    public void setNomDiscVesselName(String s)
    {
        nomDiscVesselName = s;
    }

    public void setNomDiscVesselVoyage(String s)
    {
        nom_disc_vv_cd = s;
    }

    public void setNomDiscVoyageNumber(String s)
    {
        nomDiscVoyageNumber = s;
    }

    public void setNomLoadVesselName(String s)
    {
        nomLoadVesselName = s;
    }

    public void setNomLoadVesselVoyage(String s)
    {
        nom_load_vv_cd = s;
    }

    public void setNomLoadVoyageNumber(String s)
    {
        nomLoadVoyageNumber = s;
    }

    public void setOldNewInd(String s)
    {
        old_new_ind = s;
    }

    public void setOogOH(int i)
    {
        oog_oh = i;
    }

    public void setOogOlBack(int i)
    {
        oog_ol_back = i;
    }

    public void setOogOlFront(int i)
    {
        oog_ol_front = i;
    }

    public void setOogOwLeft(int i)
    {
        oog_ow_left = i;
    }

    public void setOogOwRight(int i)
    {
        oog_ow_right = i;
    }

    public void setOogUnit(String s)
    {
        oog_unit = s;
    }

    public void setOverSizeInd(String s)
    {
        over_sz_ind = s;
    }

    public void setPdest(String s)
    {
        Pdest = s;
    }

    public void setPdisc1(String s)
    {
        pdisc1 = s;
    }

    public void setPdisc2(String s)
    {
        pdisc2 = s;
    }

    public void setPdisc3(String s)
    {
        pdisc3 = s;
    }

    public void setPload(String s)
    {
        Pload = s;
    }

    public void setPreviousPurposeCode(String s)
    {
        prev_purp_cd = s;
    }

    public void setPscwIdNo(long l)
    {
        pscw_id_nbr_isNull = false;
        pscw_id_nbr = l;
    }

    public void setPsrc(String s)
    {
        Psrc = s;
    }

    public void setPurposeCode(String s)
    {
        purp_cd = s;
    }

    public void setReeferInd(String s)
    {
        refr_ind = s;
    }

    public void setReeferTemp(String s)
    {
        refr_temp = s;
    }

    public void setReeferVolt(String s)
    {
        refr_volt = s;
    }

    public void setSealNo(String s)
    {
        seal_nbr = s;
    }

    public void setShipmentStatus(String s)
    {
        shipment_status = s;
    }

    public void setSizeFt(String s)
    {
        size_ft = s;
    }

    public void setSpecialDetails(String s)
    {
        special_details = s;
    }

    public void setStatus(String s)
    {
        Status = s;
    }

    public void setSvcTypeCode(String s)
    {
        svc_type_cd = s;
    }

    public void setTLIBatchNo(String s)
    {
        tli_batch_nbr = s;
    }

    public void setTrptModeCode(String s)
    {
        trpt_mode_cd = s;
    }

    public void setTxnStatus(String s)
    {
        txn_status = s;
    }

    public void setTypeCode(String s)
    {
        type_cd = s;
    }

    public void setUCHeight(int i)
    {
        uc_ht = i;
    }

    public void setUCInd(String s)
    {
        uc_ind = s;
    }

    public void setUCLength(int i)
    {
        uc_len = i;
    }

    public void setUCRBundleNo(long l)
    {
        ucr_bundle_nbr_isNull = false;
        ucr_bundle_nbr = l;
    }

    public void setUCRNo(String s)
    {
        ucr_nbr = s;
    }

    public void setUCUnit(String s)
    {
        uc_unit = s;
    }

    public void setUCWidth(int i)
    {
        uc_width = i;
    }

    public void setWeight(long l)
    {
        declr_wt_isNull = false;
        declr_wt = l;
    }

    public void setWeightClass(String s)
    {
        wt_class = s;
    }

    public void setWidthFt(String s)
    {
        width_ft = s;
    }

    public boolean ucrBundleNoIsNull()
    {
        return ucr_bundle_nbr_isNull;
    }
   //START: V.Karthic CR-CIM-20050913-4  30-Dec-2005
	public String getEarlyEDOFlag() {
		return earlyEDOFlag;
	}

	public void setEarlyEDOFlag(String earlyEDOFlag) {
		this.earlyEDOFlag = earlyEDOFlag;
	}
   //END: V.Karthic CR-CIM-20050913-4  30-Dec-2005

	// Added by MC Consulting for VGM
	private String vgmInd;
	private String weighMethodCd;
	private String vgmSignPerson;
	private String vgmRefNbr;
	private String vgmAcqDttm;

	public String getVgmInd() {
		return vgmInd;
	}

	public void setVgmInd(String vgmInd) {
		this.vgmInd = vgmInd;
	}

	public String getWeighMethodCd() {
		return weighMethodCd;
	}

	public void setWeighMethodCd(String weighMethodCd) {
		this.weighMethodCd = weighMethodCd;
	}

	public String getVgmSignPerson() {
		return vgmSignPerson;
	}

	public void setVgmSignPerson(String vgmSignPerson) {
		this.vgmSignPerson = vgmSignPerson;
	}

	public String getVgmRefNbr() {
		return vgmRefNbr;
	}

	public void setVgmRefNbr(String vgmRefNbr) {
		this.vgmRefNbr = vgmRefNbr;
	}

	public String getVgmAcqDttm() {
		return vgmAcqDttm;
	}

	public void setVgmAcqDttm(String vgmAcqDttm) {
		this.vgmAcqDttm = vgmAcqDttm;
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
