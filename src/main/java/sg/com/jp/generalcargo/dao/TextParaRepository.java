package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.domain.TextParaVO;
import sg.com.jp.generalcargo.util.BusinessException;

public interface TextParaRepository {

	// ejb.sessionBeans.codes.textPara--TextParaEJB
	public TextParaVO getParaCodeInfo(TextParaVO tpvo) throws BusinessException;

}
