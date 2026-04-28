package sg.com.jp.generalcargo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.jp.generalcargo.dao.EsnRepository;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.OutwardCargoTransferOfCargoService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service
public class OutwardCargoTransferOfCargoServiceImpl implements OutwardCargoTransferOfCargoService {

	@Autowired
	private EsnRepository esnRepo;

	@Override
	public List<VesselVoyValueObject> getTransferVslCrgList_T(String vslnm, String ovoynbr) throws BusinessException {
		return esnRepo.getTransferVslCrgList_T(vslnm, ovoynbr);
	}

	@Override
	public List<VesselVoyValueObject> getTransferVslCrgList_F(String vslnm, String ovoynbr) throws BusinessException {
		return esnRepo.getTransferVslCrgList_F(vslnm, ovoynbr);
	}

	@Override
	public String getTransferVarno(String vslnm, String voynbr, String ind, String cust_cd) throws BusinessException {
		return esnRepo.getTransferVarno(vslnm, voynbr, ind, cust_cd);
	}

	@Override
	public List<EsnListValueObject> getTransferDetails(String vv_cd, String cust_cd) throws BusinessException {
		return esnRepo.getTransferDetails(vv_cd, cust_cd);
	}

	@Override
	public List<EsnListValueObject> TransferCrgUpdateForDPE(String[] bk_ref_nbr, String[] newbkrefnbr, String[] esnarr,
			String[] transtypearr, String[] transNbr, String[] shutoutqty, String[] actnbrshped, String[] uanbrpkgs,
			String[] uaftdttm, String Toutvoynbr, String varnoF, String varnoT, String UserID)
			throws BusinessException {
		return esnRepo.TransferCrgUpdateForDPE(bk_ref_nbr, newbkrefnbr, esnarr, transtypearr, transNbr, shutoutqty,
				actnbrshped, uanbrpkgs, uaftdttm, Toutvoynbr, varnoF, varnoT, UserID);
	}

	@Override
	public String getSysdate() throws BusinessException {
		return esnRepo.getSysdate();
	}

}
