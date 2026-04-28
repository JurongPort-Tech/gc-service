package sg.com.jp.generalcargo.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.com.jp.generalcargo.util.CommonUtility;

/**
 * @author bhuvana
 *
 */
public class OscarJsonAdminWaiverVO {

    private String waiverAdviceNo;
    private String waiverCompany;
    private String waiveReason;
    private String vslVoy;
    private String vvcd;
    private String companyAddress;
    private String accountNbr;
    private String atbetbbtr;
    private String reqDate;
    private String reqBy;
    private String tariffDesc;
    private String unitNbr;
    private String uintRate;
    private String gst;


    private static final Log log = LogFactory.getLog(OscarJsonAdminWaiverVO.class);

    public String getWaiverAdviceNo() {
        return waiverAdviceNo;
    }

    public void setWaiverAdviceNo(String waiverAdviceNo) {
        this.waiverAdviceNo = waiverAdviceNo;
    }

    public String getWaiverCompany() {
        return waiverCompany;
    }

    public void setWaiverCompany(String waiverCompany) {
        this.waiverCompany = waiverCompany;
    }

    public String getVslVoy() {
        return vslVoy;
    }

    public void setVslVoy(String vslVoy) {
        this.vslVoy = vslVoy;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getVvcd() {
        return vvcd;
    }

    public void setVvcd(String vvcd) {
        this.vvcd = vvcd;
    }

    public String getAccountNbr() {
        return accountNbr;
    }

    public void setAccountNbr(String accountNbr) {
        this.accountNbr = accountNbr;
    }

    public String getAtbetbbtr() {
        return atbetbbtr;
    }

    public void setAtbetbbtr(String atbetbbtr) {
        this.atbetbbtr = atbetbbtr;
    }

    public String getReqDate() {
        return reqDate;
    }

    public void setReqDate(String reqDate) {
        this.reqDate = reqDate;
    }

    public String getTariffDesc() {
        return tariffDesc;
    }

    public void setTariffDesc(String tariffDesc) {
        this.tariffDesc = tariffDesc;
    }


    public String getUnitNbr() {
        return unitNbr;
    }

    public void setUnitNbr(String unitNbr) {
        this.unitNbr = unitNbr;
    }

    public String getUintRate() {
        return uintRate;
    }

    public void setUintRate(String uintRate) {
        this.uintRate = uintRate;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getWaiveReason() {
        return waiveReason;
    }

    public void setWaiveReason(String waiveReason) {
        this.waiveReason = waiveReason;
    }

    public String getReqBy() {
        return reqBy;
    }

    public void setReqBy(String reqBy) {
        this.reqBy = reqBy;
    }

    public OscarJsonAdminWaiverVO() {

    }

    public OscarJsonAdminWaiverVO(AdminFeeWaiverValueObject adminFeeWaiverVO) {
        log.info("Calling inside final constructor");
        try {
            String reqBy = adminFeeWaiverVO.getCreateUserId();
            log.info("reqBy=" + reqBy);
            this.setReqBy(reqBy);// H1

            String reqDate = adminFeeWaiverVO.getRequestedAt();
            log.info("reqDate=" + reqDate);
            this.setReqDate(CommonUtility.deNull(reqDate)); // H2

            String waiverAdviceNo = adminFeeWaiverVO.getWanAdviceNbr();
            log.info("waiverAdviceNo=" + waiverAdviceNo);
            this.setWaiverAdviceNo(CommonUtility.deNull(waiverAdviceNo)); // H3

            String waiverCompany = adminFeeWaiverVO.getWaiverCompany();
            log.info("waiverCompany=" + waiverCompany);
            this.setWaiverCompany(CommonUtility.deNull(waiverCompany));// H4

            String waiveReason = adminFeeWaiverVO.getWaiverReasons();
            log.info("waiveReason=" + waiveReason);
            this.setWaiveReason(CommonUtility.deNull(waiveReason)); // H5

            String vslVoy = adminFeeWaiverVO.getVesselVoy();
            log.info("vslVoy=" + vslVoy);
            this.setVslVoy(CommonUtility.deNull(vslVoy));// H6

            String vvCd = adminFeeWaiverVO.getVarCode();
            log.info("vvCd=" + vvCd);
            this.setVvcd(CommonUtility.deNull(vvCd));// H7

            String address = adminFeeWaiverVO.getCompanyAddress();
            log.info("address=" + address);
            this.setCompanyAddress(CommonUtility.deNull(address));// H8

            String accountNbr = adminFeeWaiverVO.getCompanyAccount();
            log.info("accountNbr=" + accountNbr);
            this.setAccountNbr(CommonUtility.deNull(accountNbr));// H9

            String atbetbbtr = adminFeeWaiverVO.getAtbEtbBtr();
            log.info("atbetbbtr=" + atbetbbtr);
            this.setAtbetbbtr(CommonUtility.deNull(atbetbbtr));// H10

            String tariffDesc = adminFeeWaiverVO.getTariffDesc();
            log.info("tariffDesc=" + tariffDesc);
            this.setTariffDesc(CommonUtility.deNull(tariffDesc));// H11

            String unitNbr = adminFeeWaiverVO.getUnitNbr();
            log.info("unitNbr=" + unitNbr);
            this.setUnitNbr(CommonUtility.deNull(unitNbr));// H12

            String unitRate = adminFeeWaiverVO.getUnitRate();
            log.info("unitRate=" + unitRate);
            this.setUintRate(CommonUtility.deNull(unitRate));// H13

            String gst = adminFeeWaiverVO.getGst();
            log.info("gst=" + gst);
            this.setGst(CommonUtility.deNull(gst));// H13

        } catch (Exception e) {
        	log.info("Exception OscarJsonAdminWaiverVO : ", e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(new ObjectMapper().writeValueAsString(this));
        } catch (JsonProcessingException j) {
            log.info("Printing j exception in toString" + j);
        	log.info("Exception toString JsonProcessingException: ", j);
            return "";
        } catch (Exception e) {
        	log.info("Exception toString : ", e);
        }
        return sb.toString();
    }
}
