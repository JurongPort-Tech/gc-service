package sg.com.jp.generalcargo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.jp.generalcargo.dao.TesnPsaJpRepository;
import sg.com.jp.generalcargo.dao.TesnRepository;
import sg.com.jp.generalcargo.dao.TesnSearchRepository;
import sg.com.jp.generalcargo.dao.TesnjpjpRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TesnJpJpValueObject;
import sg.com.jp.generalcargo.domain.TesnPsaJpEsnListValueObject;
import sg.com.jp.generalcargo.domain.TesnValueObject;
import sg.com.jp.generalcargo.domain.TesnVesselVoyValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.TesnSearchService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service
public class TesnSearchServiceImpl implements TesnSearchService {

	@Autowired
	private TesnSearchRepository TesnSearchRepo;

	@Autowired
	private TesnjpjpRepository tesnjpjp;

	@Autowired
	private TesnRepository tesnRepo;

	@Autowired
	private TesnPsaJpRepository tesnPsaJp;

	@Override
	public String tesnSearch(String tesnNo) throws BusinessException {

		return TesnSearchRepo.tesnSearch(tesnNo);
	}

	@Override
	public TesnJpJpValueObject tesnjpjpView(String vesselvoyageno, String tesn_nbr, String edoAsn) throws BusinessException {

		return tesnjpjp.tesnjpjpView(vesselvoyageno, tesn_nbr, edoAsn);
	}

	@Override
	public int tesnjpjpValidCheck_DN(String tesn_nbr, String out_voy_var_nbr) throws BusinessException {

		return tesnjpjp.tesnjpjpValidCheck_DN(tesn_nbr, out_voy_var_nbr);
	}

	@Override
	public int tesnjpjpValidCheck_status(String tesn_nbr, String out_voy_var_nbr) throws BusinessException {

		return tesnjpjp.tesnjpjpValidCheck_status(tesn_nbr, out_voy_var_nbr);
	}

	@Override
	public int tesnjpjpValidCheck(String tesn_nbr, String out_voy_var_nbr) throws BusinessException {

		return tesnjpjp.tesnjpjpValidCheck(tesn_nbr, out_voy_var_nbr);
	}

	@Override
	public List<TesnVesselVoyValueObject> getVslList(String coCd) throws BusinessException {

		return tesnjpjp.getVslList(coCd);
	}

	@Override
	public TableResult getTesnJpJpList(String vvcode, Criteria criteria) throws BusinessException {

		return tesnjpjp.getTesnJpJpList(vvcode, criteria);
	}

	@Override
	public List<TesnValueObject> getTesnSearchJpPsaList(String tesnNo, String coCode) throws BusinessException {

		return tesnRepo.getTesnSearchJpPsaList(tesnNo, coCode);
	}

	@Override
	public List<TesnValueObject> getDisplayCargoDetails(String asnNo, String vvCd, String secCarNm)
			throws BusinessException {

		return tesnRepo.getDisplayCargoDetails(asnNo, vvCd, secCarNm);
	}

	@Override
	public List<VesselVoyValueObject> getVesselVoy(String coCode) throws BusinessException {

		return tesnRepo.getVesselVoy(coCode);
	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getCntrDetails(String esnNbr) throws BusinessException {

		return tesnPsaJp.getCntrDetails(esnNbr);
	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getEsnDetails(String esnNbr, String custId) throws BusinessException {

		return tesnPsaJp.getEsnDetails(esnNbr, custId);
	}

	@Override
	public List<VesselVoyValueObject> getVesselList(String custId) throws BusinessException {

		return tesnPsaJp.getVesselList(custId);
	}

}
