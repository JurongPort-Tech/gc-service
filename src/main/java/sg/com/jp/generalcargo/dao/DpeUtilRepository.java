package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.DPEUtil;
import sg.com.jp.generalcargo.util.BusinessException;

public interface DpeUtilRepository {

	public List<DPEUtil> listVesselByName(Integer start, Integer limit, String name, String coCd) throws BusinessException;

	public int countVesselByName(String name, String coCd) throws BusinessException;

	public List<DPEUtil> getInVoyageList(String name, String coCd, String voyNbr, String ind) throws BusinessException;

	public List<DPEUtil> getOutVoyageList(String name, String coCd, String voyNbr, String ind) throws BusinessException;

	public DPEUtil getVesselDetail(String name) throws BusinessException;

	public List<DPEUtil> listCompanyByName(Integer start, Integer limit, String name) throws BusinessException;

	public int countCompanyByName(String name) throws BusinessException;

	public int countHaulierCompanyByName(String name) throws BusinessException;

	public List<DPEUtil> listHaulierCompanyByName(Integer start, Integer limit, String name) throws BusinessException;

	public List<DPEUtil> listVesselByNameForMonitoring(String name, String coCd) throws BusinessException;

	public int countVesselByNameForMonitoring(String name, String coCd) throws BusinessException;

	public List<DPEUtil> listVesselForAddTransferCargo(Integer start, Integer limit, String name, String coCd) throws BusinessException;

	public List<DPEUtil> getOutVoyageList4Transfer(String name, String coCd, String voyNbr, String ind) throws BusinessException;
}
