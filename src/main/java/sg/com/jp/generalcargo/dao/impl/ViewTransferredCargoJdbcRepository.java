package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.ViewTransferredCargoRepository;
import sg.com.jp.generalcargo.domain.ViewTransferredCargo;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("viewTransferredCargoRepo")
public class ViewTransferredCargoJdbcRepository implements  ViewTransferredCargoRepository {

	private static final Log log = LogFactory.getLog(ViewTransferredCargoJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	//StartRegion  getTransferredCargoDetails

	public List<ViewTransferredCargo> getTransferredCargoDetails(String vslIndicator, String vslName, String outVoyNo,String custCode) throws BusinessException {
		log.info("START: getTransferredCargoDetails DAO START  vslIndicator: " + CommonUtility.deNull(vslIndicator)
				+ " , vslName: " + CommonUtility.deNull(vslName) + " , outVoyNo: " + CommonUtility.deNull(outVoyNo)
				+ " , custCode: " + CommonUtility.deNull(custCode));
		StringBuffer sqlStr1 = new StringBuffer();
		StringBuffer sqlStr2 = new StringBuffer();
		StringBuffer sqlStr3 = new StringBuffer();
		List<ViewTransferredCargo> transferredCargoDetailsList = new ArrayList<>();
		try {
			if (StringUtils.isNotBlank(outVoyNo)) {
				outVoyNo = outVoyNo.trim();
			}
			if (StringUtils.isNotBlank(vslName)) {
				vslName = vslName.trim();
			}
			if (vslIndicator.equalsIgnoreCase("FROM")) {
				sqlStr1.append("select COUNT(*) from BK_DETAILS BK where BK_STATUS ='A' and BK.TRANS_CRG ='Y' ");
				sqlStr1.append("and BK.BK_ORIGINAL_VAR_NBR = (select VV_CD from VESSEL_CALL where VSL_NM =:vslName ");
				sqlStr1.append(" and OUT_VOY_NBR =:outVoyNo)");
			} else {
				sqlStr1.append("select COUNT(*) from BK_DETAILS BK where BK_STATUS ='A' and BK.TRANS_CRG ='Y' ");
				sqlStr1.append("and BK.VAR_NBR = (select VV_CD from VESSEL_CALL where VSL_NM =:vslName ");
				sqlStr1.append("and OUT_VOY_NBR =:outVoyNo)");
			}
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("vslName", vslName);
			paramMap.put("outVoyNo", outVoyNo);
			log.info("vslIndicator From List SQL : " + sqlStr1.toString() + paramMap.toString() );
			int count = namedParameterJdbcTemplate.queryForObject(sqlStr1.toString(), paramMap, Integer.class);
			log.info("List count : " + count);
			if (count <= 0) {
				if (vslIndicator.equalsIgnoreCase("FROM"))
					throw new BusinessException("T20201");
				else
					throw new BusinessException("T20202");
			} else {
				
					log.info("START:Checking Count List: ");
					if(vslIndicator.equalsIgnoreCase("FROM")) {
						sqlStr2.append("SELECT COUNT(*) FROM BK_DETAILS BK INNER JOIN VESSEL_CALL vc ON BK.BK_ORIGINAL_VAR_NBR = VC.VV_CD  ");
						sqlStr2.append(" INNER JOIN ESN E ON E.BK_REF_NBR =BK.BK_REF_NBR  AND E.ESN_STATUS ='A' LEFT JOIN VESSEL_DECLARANT VD ON E.OUT_VOY_VAR_NBR = VD.VV_CD ");
						sqlStr2.append(" LEFT JOIN COMPANY_CODE CC ON VC.VSL_OPR_CD = CC.CO_CD LEFT JOIN ESN_DETAILS ED ON ED.ESN_ASN_NBR = E.ESN_ASN_NBR LEFT JOIN TESN_JP_JP T1 ");
						sqlStr2.append(" ON T1.ESN_ASN_NBR = E.ESN_ASN_NBR LEFT JOIN TESN_PSA_JP T2 ON T2.ESN_ASN_NBR     = E.ESN_ASN_NBR WHERE BK.BK_STATUS ='A' ");
						sqlStr2.append(" AND BK.TRANS_CRG ='Y' AND E.ESN_STATUS  ='A' AND ( 'JP'  = :custCode OR E.ESN_CREATE_CD = :custCode OR T2.TRUCKER_IC IN ");
						sqlStr2.append(" (SELECT TDB_CR_NBR FROM CUSTOMER WHERE CUST_CD = :custCode UNION SELECT UEN_NBR FROM CUSTOMER WHERE CUST_CD = :custCode ) OR ED.TRUCKER_IC IN ");
						sqlStr2.append(" (SELECT TDB_CR_NBR FROM CUSTOMER WHERE CUST_CD = :custCode UNION SELECT UEN_NBR FROM CUSTOMER WHERE CUST_CD = :custCode ) OR T2.TRUCKER_IC IN " );
						sqlStr2.append(" (SELECT TDB_CR_NBR FROM CUSTOMER WHERE CUST_CD = :custCode UNION SELECT UEN_NBR FROM CUSTOMER WHERE CUST_CD = :custCode ) ");
						sqlStr2.append(" OR ED.MIXED_SCHEME_ACCT_NBR IN(SELECT ACCT_NBR FROM CUST_ACCT WHERE CUST_CD = :custCode ) ");
						sqlStr2.append(" OR VD.CUST_CD = :custCode OR (VD.CUST_CD    IS NULL AND VC.VSL_OPR_CD = :custCode ) ");
						sqlStr2.append(" OR (VD.CUST_CD IS NULL AND E.OUT_VOY_VAR_NBR IN ");
						sqlStr2.append(" (SELECT VV_CD FROM NOMINATED_SCHEME NS LEFT JOIN VESSEL_SCHEME VS ON NS.SCHEME_CD = VS.SCHEME_CD WHERE VS.AB_CD  = :custCode  AND VS.REC_STATUS = 'A'  )) ) ");
						sqlStr2.append(" AND VC.VSL_NM = :vslName AND VC.OUT_VOY_NBR =:outVoyNo");

					}else {
						sqlStr2.append("SELECT COUNT(*) FROM BK_DETAILS BK INNER JOIN VESSEL_CALL vc ON BK.VAR_NBR = VC.VV_CD  ");
						sqlStr2.append(" INNER JOIN ESN E ON E.BK_REF_NBR =BK.BK_REF_NBR  AND E.ESN_STATUS ='A' LEFT JOIN VESSEL_DECLARANT VD ON E.OUT_VOY_VAR_NBR = VD.VV_CD" );
						sqlStr2.append(" LEFT JOIN COMPANY_CODE CC ON VC.VSL_OPR_CD = CC.CO_CD LEFT JOIN ESN_DETAILS ED ON ED.ESN_ASN_NBR = E.ESN_ASN_NBR LEFT JOIN TESN_JP_JP T1 ");
						sqlStr2.append(" ON T1.ESN_ASN_NBR = E.ESN_ASN_NBR LEFT JOIN TESN_PSA_JP T2 ON T2.ESN_ASN_NBR     = E.ESN_ASN_NBR WHERE BK.BK_STATUS ='A' ");
						sqlStr2.append(" AND BK.TRANS_CRG ='Y' AND E.ESN_STATUS  ='A' AND ( 'JP'  = :custCode OR E.ESN_CREATE_CD = :custCode OR T2.TRUCKER_IC IN " );
						sqlStr2.append(" (SELECT TDB_CR_NBR FROM CUSTOMER WHERE CUST_CD = :custCode UNION SELECT UEN_NBR FROM CUSTOMER WHERE CUST_CD = :custCode ) OR ED.TRUCKER_IC IN " );
						sqlStr2.append(" (SELECT TDB_CR_NBR FROM CUSTOMER WHERE CUST_CD = :custCode UNION SELECT UEN_NBR FROM CUSTOMER WHERE CUST_CD = :custCode ) OR T2.TRUCKER_IC IN " );
						sqlStr2.append(" (SELECT TDB_CR_NBR FROM CUSTOMER WHERE CUST_CD = :custCode UNION SELECT UEN_NBR FROM CUSTOMER WHERE CUST_CD = :custCode ) " );
						sqlStr2.append(" OR ED.MIXED_SCHEME_ACCT_NBR IN(SELECT ACCT_NBR FROM CUST_ACCT WHERE CUST_CD = :custCode )");
						sqlStr2.append(" OR VD.CUST_CD = :custCode OR (VD.CUST_CD    IS NULL AND VC.VSL_OPR_CD = :custCode ) ");
						sqlStr2.append(" OR (VD.CUST_CD IS NULL AND E.OUT_VOY_VAR_NBR IN");
						sqlStr2.append(" (SELECT VV_CD FROM NOMINATED_SCHEME NS LEFT JOIN VESSEL_SCHEME VS ON NS.SCHEME_CD = VS.SCHEME_CD WHERE VS.AB_CD  = :custCode  AND VS.REC_STATUS = 'A'  )) )");
						sqlStr2.append(" AND VC.VSL_NM = :vslName AND VC.OUT_VOY_NBR =:outVoyNo");
					}
					Map<String, String> paramMap1 = new HashMap<String, String>();
					paramMap1.put("vslName", vslName);
					paramMap1.put("outVoyNo", outVoyNo);
					paramMap1.put("custCode", custCode);
					log.info("List SQL : " + sqlStr2.toString() +  " paramMap: " + paramMap1.toString() );
					int count1 = namedParameterJdbcTemplate.queryForObject(sqlStr2.toString(), paramMap1, Integer.class);
					log.info("Count SQL Info : " + count1);
					if(count1 <= 0) {
						throw new BusinessException("T20203");
					}
					if(vslIndicator.equalsIgnoreCase("FROM")) {
						sqlStr3.append("SELECT (SELECT V2.VSL_NM FROM VESSEL_CALL V2 WHERE V2.VV_CD =BK.VAR_NBR) as vslName, ");
						sqlStr3.append(" (SELECT V2.TERMINAL FROM VESSEL_CALL V2 WHERE V2.VV_CD =BK.VAR_NBR) as toTerminal, ");
						sqlStr3.append(" (SELECT E.ESN_ASN_NBR FROM ESN E WHERE E.BK_REF_NBR =BK.BK_REF_NBR  AND E.ESN_STATUS ='A') AS toASN, ");
						sqlStr3.append(" (SELECT DECODE(E2.TRANS_TYPE,'E','L','T') FROM ESN E2 ");
						sqlStr3.append(" WHERE E2.BK_REF_NBR =BK.BK_REF_NBR AND E2.ESN_STATUS ='A') AS cargoStatus, ");
						sqlStr3.append(" (SELECT NVL(ed1.nbr_pkgs,0) + NVL(tjj.nbr_pkgs,0) + NVL(tpj.nbr_pkgs,0) FROM esn e1, esn_details ed1, tesn_jp_jp tjj, tesn_psa_jp tpj WHERE e1.esn_asn_nbr = ed1.esn_asn_nbr (+)  and e1.esn_asn_nbr = tjj.esn_asn_nbr (+) and e1.esn_asn_nbr = tpj.esn_asn_nbr (+) AND e1.esn_asn_nbr = ");
						sqlStr3.append(" (SELECT E.ESN_ASN_NBR FROM ESN E WHERE E.BK_REF_NBR =BK.BK_REF_NBR AND E.ESN_STATUS ='A')) as transferredPkgs, ");
						sqlStr3.append(" DECODE(BK.OLD_BK_REF,'','FULL LOT','PARTIAL LOT') as transferType, ");
						sqlStr3.append(" (SELECT to_char(ED.FIRST_TRANS_DTTM, 'dd/mm/yyyy hh24:mi') FROM ESN_DETAILS ED WHERE ED.ESN_ASN_NBR = ");
						sqlStr3.append(" (SELECT ESN_ASN_NBR FROM ESN E3 WHERE E3.BK_REF_NBR =BK.BK_REF_NBR AND E3.ESN_STATUS ='A')) as esnFirstTrans, ");
						sqlStr3.append(" (SELECT to_char(T1.FIRST_TRANS_DTTM, 'dd/mm/yyyy hh24:mi') FROM TESN_JP_JP T1 WHERE T1.ESN_ASN_NBR = ");
						sqlStr3.append(" (SELECT ESN_ASN_NBR FROM ESN E3 WHERE E3.BK_REF_NBR =BK.BK_REF_NBR AND E3.ESN_STATUS ='A')) as tesnjpjpFirstTrans, ");
						sqlStr3.append(" (SELECT to_char(T3.FIRST_TRANS_DTTM, 'dd/mm/yyyy hh24:mi') FROM TESN_PSA_JP T3 WHERE T3.ESN_ASN_NBR = ");
						sqlStr3.append(" (SELECT ESN_ASN_NBR FROM ESN E3 WHERE E3.BK_REF_NBR =BK.BK_REF_NBR AND E3.ESN_STATUS ='A')) as tesnpsajpFirstTrans, ");
						sqlStr3.append(" (SELECT V2.VSL_NM FROM VESSEL_CALL V2 WHERE V2.VV_CD =BK.BK_ORIGINAL_VAR_NBR) as fromVslNum, ");
						sqlStr3.append(" (SELECT V2.TERMINAL FROM VESSEL_CALL V2 WHERE V2.VV_CD =BK.BK_ORIGINAL_VAR_NBR) as fromTerminal, ");
						sqlStr3.append(" DECODE((SELECT E.ESN_ASN_NBR FROM ESN E WHERE E.BK_REF_NBR =BK.OLD_BK_REF AND E.ESN_STATUS ='A'),'','NA', ");
						sqlStr3.append(" (SELECT E.ESN_ASN_NBR FROM ESN E WHERE E.BK_REF_NBR =BK.OLD_BK_REF AND E.ESN_STATUS ='A')) AS fromASN, ");
						sqlStr3.append(" NVL((SELECT ed1.nbr_pkgs||tjj.nbr_pkgs||tpj.nbr_pkgs FROM esn e1, esn_details ed1, tesn_jp_jp tjj, tesn_psa_jp tpj WHERE e1.esn_asn_nbr = ed1.esn_asn_nbr (+)  and e1.esn_asn_nbr = tjj.esn_asn_nbr (+) and e1.esn_asn_nbr = tpj.esn_asn_nbr (+) AND e1.esn_asn_nbr =  ");
						sqlStr3.append(" (SELECT E.ESN_ASN_NBR FROM ESN E WHERE E.BK_REF_NBR =BK.OLD_BK_REF AND E.ESN_STATUS ='A')),'NA') AS fromVslNumPkgs, ");
						//PDE add
						sqlStr3.append(" (SELECT E.LAST_MODIFY_USER_ID FROM ESN E WHERE E.BK_REF_NBR = BK.BK_REF_NBR AND E.ESN_STATUS ='A') as userId, ");
						sqlStr3.append(" (SELECT  to_char(E.LAST_MODIFY_DTTM, 'dd/mm/yyyy hh24:mi')  FROM ESN E WHERE E.BK_REF_NBR = BK.BK_REF_NBR AND E.ESN_STATUS ='A') as modifyDttm, ");
						sqlStr3.append(" BK.SHUTOUT_DELIVERY_PKGS AS noOfShutOutPkgs, ");
						sqlStr3.append(" (SELECT V2.OUT_VOY_NBR FROM VESSEL_CALL V2 WHERE V2.VV_CD =BK.BK_ORIGINAL_VAR_NBR) as fromVoyageOut, ");
						sqlStr3.append(" (SELECT V2.OUT_VOY_NBR FROM VESSEL_CALL V2 WHERE V2.VV_CD =BK.VAR_NBR) as toVoyageOut ");
						//PDE END
						sqlStr3.append(" FROM BK_DETAILS BK WHERE BK_STATUS ='A' AND BK.TRANS_CRG ='Y' AND BK.BK_ORIGINAL_VAR_NBR = ");
						sqlStr3.append(" (SELECT VV_CD AS vvCd FROM VESSEL_CALL WHERE VSL_NM = :vslName AND OUT_VOY_NBR =:outVoyNo) ");
					}else {
						sqlStr3.append(" SELECT (SELECT V2.VSL_NM FROM VESSEL_CALL V2 WHERE V2.VV_CD =BK.BK_ORIGINAL_VAR_NBR) as vslName, ");
                    	sqlStr3.append(" (SELECT V2.TERMINAL FROM VESSEL_CALL V2 WHERE V2.VV_CD =BK.BK_ORIGINAL_VAR_NBR) as fromTerminal, ");
                        sqlStr3.append(" DECODE((SELECT E.ESN_ASN_NBR FROM ESN E WHERE E.BK_REF_NBR =BK.OLD_BK_REF AND E.ESN_STATUS ='A'),'','NA', ");
                        sqlStr3.append(" (SELECT E.ESN_ASN_NBR FROM ESN E WHERE E.BK_REF_NBR =BK.OLD_BK_REF AND E.ESN_STATUS ='A')) AS fromASN, ");
                        sqlStr3.append(" NVL((SELECT DECODE(E2.TRANS_TYPE,'E','L','T') FROM ESN E2 ");
                        sqlStr3.append(" WHERE E2.BK_REF_NBR =BK.BK_REF_NBR AND E2.ESN_STATUS ='A'),'NA') AS cargoStatus, ");
                        sqlStr3.append(" NVL((SELECT ed1.nbr_pkgs||tjj.nbr_pkgs||tpj.nbr_pkgs FROM esn e1, esn_details ed1, tesn_jp_jp tjj, tesn_psa_jp tpj WHERE e1.esn_asn_nbr = ed1.esn_asn_nbr (+)  and e1.esn_asn_nbr = tjj.esn_asn_nbr (+) and e1.esn_asn_nbr = tpj.esn_asn_nbr (+) AND e1.esn_asn_nbr = ");
                        sqlStr3.append(" (SELECT E.ESN_ASN_NBR FROM ESN E WHERE E.BK_REF_NBR =BK.OLD_BK_REF AND E.ESN_STATUS ='A')),'NA') as transferredPkgs, ");
                        sqlStr3.append(" DECODE(BK.OLD_BK_REF,'','FULL LOT','PARTIAL LOT') as transferType, ");
                        sqlStr3.append(" (SELECT to_char(ED.FIRST_TRANS_DTTM, 'dd/mm/yyyy hh24:mi') FROM ESN_DETAILS ED WHERE ED.ESN_ASN_NBR = ");
                        sqlStr3.append(" (SELECT ESN_ASN_NBR FROM ESN E3 WHERE E3.BK_REF_NBR =BK.BK_REF_NBR AND E3.ESN_STATUS ='A')) as esnFirstTrans, ");
                        sqlStr3.append(" (SELECT to_char(T1.FIRST_TRANS_DTTM, 'dd/mm/yyyy hh24:mi') FROM TESN_JP_JP T1 WHERE T1.ESN_ASN_NBR = ");
                        sqlStr3.append(" (SELECT ESN_ASN_NBR FROM ESN E3 WHERE E3.BK_REF_NBR =BK.BK_REF_NBR AND E3.ESN_STATUS ='A')) as tesnjpjpFirstTrans, ");
                        sqlStr3.append(" (SELECT to_char(T3.FIRST_TRANS_DTTM, 'dd/mm/yyyy hh24:mi') FROM TESN_PSA_JP T3 WHERE T3.ESN_ASN_NBR = ");
                        sqlStr3.append(" (SELECT ESN_ASN_NBR FROM ESN E3 WHERE E3.BK_REF_NBR =BK.BK_REF_NBR AND E3.ESN_STATUS ='A')) as tesnpsajpFirstTrans, ");
                        sqlStr3.append(" (SELECT V2.VSL_NM FROM VESSEL_CALL V2 WHERE V2.VV_CD =BK.VAR_NBR) toVslNm, ");
                        sqlStr3.append(" (SELECT V2.TERMINAL FROM VESSEL_CALL V2 WHERE V2.VV_CD =BK.VAR_NBR) toTerminal, ");
                        sqlStr3.append(" (SELECT E.ESN_ASN_NBR FROM ESN E WHERE E.BK_REF_NBR =BK.BK_REF_NBR AND E.ESN_STATUS='A') AS toASN, ");
                        sqlStr3.append(" (SELECT NVL(ed1.nbr_pkgs,0) + NVL(tjj.nbr_pkgs,0) + NVL(tpj.nbr_pkgs,0) FROM esn e1, esn_details ed1, tesn_jp_jp tjj, tesn_psa_jp tpj WHERE e1.esn_asn_nbr = ed1.esn_asn_nbr (+)  and e1.esn_asn_nbr = tjj.esn_asn_nbr (+) and e1.esn_asn_nbr = tpj.esn_asn_nbr (+) AND e1.esn_asn_nbr = ");
                        sqlStr3.append(" (SELECT E.ESN_ASN_NBR FROM ESN E WHERE E.BK_REF_NBR =BK.BK_REF_NBR AND E.ESN_STATUS='A')) AS transferredPkgs ");
                        //PDE add
                        sqlStr3.append(",(SELECT E.LAST_MODIFY_USER_ID FROM ESN E WHERE E.BK_REF_NBR = BK.BK_REF_NBR AND E.ESN_STATUS ='A') as userId, ");
                        sqlStr3.append(" (SELECT to_char(E.LAST_MODIFY_DTTM, 'dd/mm/yyyy hh24:mi') FROM ESN E WHERE E.BK_REF_NBR = BK.BK_REF_NBR AND E.ESN_STATUS ='A') as modifyDttm, ");
                        sqlStr3.append(" BK.SHUTOUT_DELIVERY_PKGS, ");
                        sqlStr3.append(" (SELECT V2.OUT_VOY_NBR FROM VESSEL_CALL V2 WHERE V2.VV_CD =BK.BK_ORIGINAL_VAR_NBR) as fromVoyageOut, ");
                        sqlStr3.append(" (SELECT V2.OUT_VOY_NBR FROM VESSEL_CALL V2 WHERE V2.VV_CD =BK.VAR_NBR) as toVoyageOut ");
                        //PDE END
                        sqlStr3.append(" FROM BK_DETAILS BK WHERE BK_STATUS ='A' AND BK.TRANS_CRG ='Y' AND BK.VAR_NBR = ");
                        sqlStr3.append(" (SELECT VV_CD FROM VESSEL_CALL WHERE VSL_NM =:vslName AND OUT_VOY_NBR =:outVoyNo)");
					}

					Map<String, String> paramMap2 = new HashMap<String, String>();
					paramMap2.put("vslName", vslName);
					paramMap2.put("outVoyNo", outVoyNo);
					log.info("sqlStr3 SQL: " + sqlStr3 + " paramMap: " + paramMap2);
					transferredCargoDetailsList = namedParameterJdbcTemplate.query(sqlStr3.toString(),paramMap2,
							new BeanPropertyRowMapper<ViewTransferredCargo>(ViewTransferredCargo.class));
					log.info("**** getTransferredCargoDetails End ****" + transferredCargoDetailsList.toString());

				 
			}
		} catch (NullPointerException e) {
			log.info("Exception getTransferredCargoDetails : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception getTransferredCargoDetails : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getTransferredCargoDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("**** DAO getTransferredCargoDetails End ****");
		}

		return transferredCargoDetailsList;
	}

	//EndRegion getTransferredCargoDetails

}
