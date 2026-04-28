package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.util.BusinessException;

public interface ContainerCommonFunctionRepo {
	
	public String getCntrCatCd(String iso, String imdgClsCd, int oh, int olFront, int olBack, int owRight, int owLeft,
			String refrInd, String ucInd, String oogInd, String specDetail, String status, String dgInd)
			throws BusinessException;
	
	public boolean isTSContainerExitToIM(int cntrSeqNbr) throws BusinessException;
	
	public String getCntrCatCd(String iso, String imdgClsCd, int oh, int olFront, int olBack, int owRight, int owLeft,
            String refrInd, String ucInd, String oogInd, String specDetail, String status) throws BusinessException ;

}
