package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DPEUtil;
import sg.com.jp.generalcargo.domain.HSCode;
import sg.com.jp.generalcargo.util.BusinessException;

public interface DpeGeneralCargoAmendRepository {
	public DPEUtil getDiscargingCargo(String asnNbr) throws BusinessException;

	public List<DPEUtil> listAcount(String vvCd) throws BusinessException;

	public DPEUtil getEsnVessel(String asnNbr) throws BusinessException;

	public DPEUtil getHsSubCodeDesc(String hsCode, String hsSubCode);

	public List<DPEUtil> listCrgType(String vslType) throws BusinessException;

	public List<DPEUtil> getShipper(String name, String shipperCode, Criteria criteria) throws BusinessException;

	public List<DPEUtil> currentListCargoCategory(String cargoTypeCode, String companyCode) throws BusinessException;

	public List<HSCode> listHsCode(String status) throws BusinessException;

	public List<DPEUtil> listHsSubCode(String status, String hsCode) throws BusinessException;

	public List<DPEUtil> listAuthorizedParty(String name, String vvCd, Criteria criteria) throws BusinessException;

	public List<DPEUtil> listPort(String name, Criteria criteria) throws BusinessException;

	public List<DPEUtil> listPackaging(String name, Criteria criteria) throws BusinessException;

	public List<DPEUtil> listCompany(String name, Criteria criteria) throws BusinessException;

	public DPEUtil getShipperInformation(String name);

	public int updateBkDetails(String bk_ref_nbr, String shipperNbr, String shipperAddress, String shipperNm,
			String strUserID, String conNm, String conAddr, String shipperAddr, String notifyParty,
			String notifyPartyAddr, String placeofDelivery, String placeofReceipt, String blNbr);
}
