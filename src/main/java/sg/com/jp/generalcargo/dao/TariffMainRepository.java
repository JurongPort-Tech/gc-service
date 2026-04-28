package sg.com.jp.generalcargo.dao;

import java.sql.SQLException;
import java.sql.Timestamp;

import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.domain.TariffMainVO;
import sg.com.jp.generalcargo.util.BusinessException;

public interface TariffMainRepository {

	//StartRegion  TariffMainRepository
	
	public TariffMainVO retrieveTariffByCdTierSeqNbr(int versionNbr, String custCd, String acctNbr, String contractNbr,
			int contractualYr, String tariffMainCat, String tariffSubCat, String tariffCd, int tierSeqNbr,
			Timestamp varDate) throws SQLException, BusinessException;
	
	public TariffMainVO retrieveTariffMainTierGenChar(int versionNbr, String custCd, String acctNbr, String contractNbr,
			int contractualYr, String tariffMainCat, String tariffSubCat, String businessType, String schemeCd,
			String mvmt, String type, String cntrCat, String cntrSize, Timestamp varDate)
			throws SQLException, BusinessException;

	public int getGeneralCargoCustFspDays(GeneralEventLogValueObject generalEventLogValueObject)
			throws BusinessException;
	
	public boolean isJNL2JNL(GeneralEventLogValueObject vo) throws BusinessException;
	
	public TariffMainVO retrieveTariffMainTierAdm(int versionNbr, String custCd, String acctNbr, String contractNbr,
			int contractualYr, String tariffMainCat, String tariffSubCat, String schemeCd, String businessType,
			Timestamp varDate) throws SQLException, BusinessException;

	public boolean isCustFSPbySRAcct(GeneralEventLogValueObject generalEventLogValueObject) throws BusinessException;

	public int getGBCustFspDays(String discVvCd, String mvmt, String vvInd, String stgType, String gdsType, String hsCd,
			String acctNbr, Timestamp varDttm) throws BusinessException;

	public int getGBCustFspDays(String discVvCd, String mvmt, String vvInd, String stgType, String gdsType,
			String hsCd) throws BusinessException;

	public String retrieveBillPartyByTariffCd(int versionNbr, String tariffCd) throws BusinessException;

	public TariffMainVO retrieveTariffMainTier(int versionNbr, String custCd, String acctNbr, String contractNbr,
			// amended by hujun on 7/6/2011 for add tariff item effective date parameter
			// String tariffMainCat, String tariffSubCat, String schemeCd, String
			// businessType) throws SQLException,
			String tariffMainCat, String tariffSubCat, String schemeCd, String businessType, Timestamp varDate) throws
	// amended end
	BusinessException;	
	//EndRegion TariffMainRepository

}
