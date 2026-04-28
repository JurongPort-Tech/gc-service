package sg.com.jp.generalcargo.service;

import java.nio.file.Path;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.CustomDetailsFileUploadDetails;
import sg.com.jp.generalcargo.domain.FTZInterchangeVO;
import sg.com.jp.generalcargo.domain.SummaryCuscar;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CustomDetailsCuscarService {

	public List<FTZInterchangeVO> parseData(Path path, String varNbr) throws BusinessException;

	public Path uploadFile(Criteria criteria, MultipartFile uploadFile) throws BusinessException;

	public Resource downloadFile(String refId, String type) throws BusinessException;

	public SummaryCuscar processData(List<FTZInterchangeVO> voList,
			CustomDetailsFileUploadDetails fileUploadDetails, String vvCd, String userId, String companyCode)
			throws BusinessException;
}
