package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.util.BusinessException;

public interface GCARepository {

	public List<String> getEdoDetails(String edoNo) throws BusinessException;

	public List<String> getEsnDetails(String esnNo) throws BusinessException;

	public List<String> getEdoDetails(String edoNo, String hsCode, String hsCodeFrom, String hsCodeTo,
			String hsSubCodeDesc) throws BusinessException;

	public List<String> getEsnDetails(String esnNo, String hsCode, String hsCodeFrom, String hsCodeTo,
			String hsSubCodeDesc) throws BusinessException;

	public boolean updateManifestGCAHsCode(String mftSeqNbr, String hsCode, String hsCodeFrom, String hsCodeTo)
			throws BusinessException;

	public boolean updateEsnGCAHsCode(String esnNbr, String hsCode, String hsCodeFrom, String hsCodeTo)
			throws BusinessException;

}
