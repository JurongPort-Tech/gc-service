package sg.com.jp.generalcargo.service;

import java.util.List;

import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface OutwardCargoTransferOfCargoService {

	public String getSysdate() throws BusinessException;

	public List<EsnListValueObject> getTransferDetails(String vv_cd, String cust_cd) throws BusinessException;

	public String getTransferVarno(String vslnm, String voynbr, String ind, String cust_cd) throws BusinessException;

	public List<VesselVoyValueObject> getTransferVslCrgList_T(String vslnm, String ovoynbr) throws BusinessException;

	public List<VesselVoyValueObject> getTransferVslCrgList_F(String vslnm, String ovoynbr) throws BusinessException;

	public List<EsnListValueObject> TransferCrgUpdateForDPE(String bk_ref_nbr[], String newbkrefnbr[], String esnarr[],
			String transtypearr[], String transNbr[], String shutoutqty[], String actnbrshped[], String uanbrpkgs[],
			String uaftdttm[], String Toutvoynbr, String varnoF, String varnoT, String UserID) throws BusinessException;

}
