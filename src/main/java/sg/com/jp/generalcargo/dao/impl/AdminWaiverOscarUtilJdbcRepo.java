package sg.com.jp.generalcargo.dao.impl;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import sg.com.jp.generalcargo.dao.AdminWaiverOscarUtilRepo;
import sg.com.jp.generalcargo.domain.AdminFeeWaiverValueObject;
import sg.com.jp.generalcargo.domain.OscarJsonAdminWaiverVO;
import sg.com.jp.generalcargo.domain.OscarJsonResponseVO;
import sg.com.jp.generalcargo.util.BusinessException;

@Repository("adminWaiverOscarUtilRepo")
public class AdminWaiverOscarUtilJdbcRepo implements AdminWaiverOscarUtilRepo {

	private static final Log log = LogFactory.getLog(AdminWaiverOscarUtilJdbcRepo.class);

	@Value("${OSCARADMINWAIVER.URI}")
	private String waiverUrl;

	@Override
	public boolean sendAdminWaiverRequestToOscar(AdminFeeWaiverValueObject adminFeeWaiverVO) throws BusinessException {
		boolean adminWaiveJsonSent = false;
		String adminWaiverJsonString = new String();
		try {
			log.info("START:sendAdminWaiverRequestToOscar adminFeeWaiverVO:" + adminFeeWaiverVO.toString());
			String svrUrl = waiverUrl;
			log.info("To OSCAR:Micro.Service.OSCARADMINWAIVER.URI=" + svrUrl);
			if (StringUtils.isNotBlank(svrUrl)) {
				adminFeeWaiverVO.setCreateUserId(adminFeeWaiverVO.getCreateUserId());
			} else {
				log.info(
						"requestCredentialWebServiceUrl or adminFeeApprovalWebServiceUrl is not defined in sys.properties file, please check!");
			}
			if (adminFeeWaiverVO != null) {
				StringBuffer sb = new StringBuffer();
				sb.setLength(0);
				sb.append("waiverAdviceNo:" + adminFeeWaiverVO.getWanAdviceNbr());
				sb.append(";waiverCompany:" + adminFeeWaiverVO.getWaiverCompany());
				sb.append(";vslVoy:" + adminFeeWaiverVO.getVesselVoy());
				sb.append(";address:" + adminFeeWaiverVO.getCompanyAddress());
				sb.append(";vvcd:" + adminFeeWaiverVO.getVarCode());
				sb.append(";accountNbr:" + adminFeeWaiverVO.getCompanyAccount());
				sb.append(";atbetbbtr:" + adminFeeWaiverVO.getAtbEtbBtr());
				sb.append(";reqDate:" + adminFeeWaiverVO.getRequestedAt());
				sb.append(";reqBy :" + adminFeeWaiverVO.getCreateUserId());
				sb.append(";tariffDesc:" + adminFeeWaiverVO.getTariffDesc());
				sb.append(";unit:" + adminFeeWaiverVO.getUnitNbr());
				sb.append(";uintRate:" + adminFeeWaiverVO.getUnitRate());
				sb.append(";gst:" + adminFeeWaiverVO.getGst());
				sb.append(";waiveReason:" + adminFeeWaiverVO.getWaiverReasons());
				sb.append("\n");
				log.info("Admin waive Data to Jason=" + sb.toString());
				OscarJsonAdminWaiverVO oscarObj = new OscarJsonAdminWaiverVO(adminFeeWaiverVO);
				adminWaiverJsonString = oscarObj.toString();
				log.info("Final AdminWaiverJsonString=" + adminWaiverJsonString + "=END");
			}
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<String>(adminWaiverJsonString, headers);
			RestTemplate restTemplate = new RestTemplate();
			log.info("INPUT TO REST CALL:svrUrl" + svrUrl + "BODY" + entity.toString());
			ResponseEntity<Object> result = null;
			try {
				result = restTemplate.exchange(svrUrl, HttpMethod.POST, entity, Object.class);
			} catch (HttpClientErrorException e) {
				log.info("Exception sendAdminWaiverRequestToOscar : ", e);
			}
			log.info("**POST Admin Waive** request Url: " + svrUrl + ";Result=" + result);
			int output = 1;
			if (HttpStatus.OK == result.getStatusCode()) { // 200
				output = 0;
			} else if (HttpStatus.NOT_FOUND == result.getStatusCode()) { // 404
				log.info("Admin Waiver OSCAR Service is unavailable=" + result);
			}
			log.info("Admin Waiver Jason output=" + output);
			// Read the response body.
			if (output == 0) {
				ObjectMapper objectMapper = new ObjectMapper();
				OscarJsonResponseVO oscarResponse = objectMapper.readValue(result.getBody().toString(),
						OscarJsonResponseVO.class);
				String errorCode = oscarResponse.getError();
				String errorMessage = oscarResponse.getErrorMessage();
				String id = oscarResponse.getId();
				String url = oscarResponse.getUrl();
				log.info("ResponseError=" + errorCode + "; ResponseErrMsg=" + errorMessage + ";ResponseId =" + id);
				if (errorCode != null && !errorCode.equals("") && Integer.parseInt(errorCode) != -1) {
					adminWaiveJsonSent = true;
				}
			}
		} catch (Exception ex) {
			log.info("Exception sendAdminWaiverRequestToOscar : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: sendAdminWaiverRequestToOscar");
		}
		return adminWaiveJsonSent;
	}

}
