package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.CashSalesRepository;
import sg.com.jp.generalcargo.domain.CashSalesValueObject;
import sg.com.jp.generalcargo.domain.EdoValueObjectOps;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
@Repository
public class CashSalesJdbcRepository implements CashSalesRepository {
	private static final Log log = LogFactory.getLog(CashSalesJdbcRepository.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public List<CashSalesValueObject> getCashSalesList()  throws BusinessException {
		List<CashSalesValueObject> arrayList = new ArrayList<CashSalesValueObject>();
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: getCashSalesList  DAO  Start Obj " );

			sb.append("SELECT C.CASH_RECEIPT_NBR, C.CUST_NM, C.TOTAL_AMT+C.GST_AMT+C.ROUND_UP_AMT+C.MIN_BILL_AMT, ");
			sb.append("C.TOTAL_CASH_AMT, C.TOTAL_CHEQ_AMT, C.STATUS, TO_CHAR(C.RECEIPT_DTTM,'DD/MM/YYYY'), C.LAST_MODIFY_DTTM, ");
			sb.append("N.TRANS_AMT, D.TRANS_AMT, ");
			sb.append(" C.TOTAL_CASH_NETS_AMT ");
			sb.append("FROM (CASH_SALES C LEFT JOIN CASH_SALES_EFTPOS_RECEIPT N ON C.CASH_RECEIPT_NBR = N.CASH_RECEIPT_NBR) ");
			sb.append("LEFT JOIN CASH_SALES_CASHCARD_RECEIPT D ON C.CASH_RECEIPT_NBR = D.CASH_RECEIPT_NBR ");
			sb.append("WHERE C.STATUS <> 'C' AND C.STATUS <> 'R' ORDER BY C.CASH_RECEIPT_NBR ");
			log.info("getCashSalesList SQL: " + sb.toString());
			log.info("paramMap: " + paramMap);
			log.info(" ***SQL *****" + sb.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			int ctr = 0;
			while (rs.next()) {
				CashSalesValueObject cashSalesValueObject = new CashSalesValueObject();

				cashSalesValueObject.setCash_receipt_nbr(CommonUtility.deNull(rs.getString(1)).trim());
				cashSalesValueObject.setCust_nm(CommonUtility.deNull(rs.getString(2)).trim());
				cashSalesValueObject.setTotal_amt(rs.getDouble(3));
				cashSalesValueObject.setTotal_cash_amt(rs.getDouble(4));
				cashSalesValueObject.setTotal_cash_amt(rs.getDouble(5));
				cashSalesValueObject.setStatus((CommonUtility.deNull(rs.getString(6)).trim()));
				cashSalesValueObject.setReceipt_dttm(CommonUtility.deNull(rs.getString(7)));
				cashSalesValueObject.setLast_modify_dttm(rs.getTimestamp(8).getTime());    
				cashSalesValueObject.setTotal_net_amt(rs.getDouble(9));
				cashSalesValueObject.setTotal_cash_card_amt(rs.getDouble(10));
				cashSalesValueObject.setTotal_cash_nets_amt(rs.getDouble("total_cash_nets_amt"));

				arrayList.add(ctr++, cashSalesValueObject);
			}

		} catch (NullPointerException e) { 
			log.info("Exception getCashSalesList : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCashSalesList : ", e);
			throw new BusinessException("M4201");
		} finally{
			log.info("END: getCashSalesList  DAO  END.  arrayList: " + arrayList);
		}

		return arrayList;
	}
	
	
	public List<CashSalesValueObject> getCashSales(List<EdoValueObjectOps> dnList) throws BusinessException {
		List<CashSalesValueObject> cashList = null;
        SqlRowSet rs = null;
        StringBuffer sql = new StringBuffer();
        Map<String,String> paramMap = new HashMap<String,String>();
        try {
        	log.info("START: getCashSales  DAO  Start Obj.  dnList: " + CommonUtility.deNull(String.valueOf(dnList)));
            sql.setLength(0);
            
            // NS amend sql to retrieve the payment receipt correctly (Jan 2023)
            sql.append(" SELECT dn_nbr,LISTAGG(cash_receipt_nbr, ', ') WITHIN GROUP ( ORDER BY cash_receipt_nbr) AS cash_receipt_nbr, receipt_dttm ");
            sql.append(" FROM ( SELECT dn.dn_nbr, (cs.cash_receipt_nbr || '-' || csi.tariff_main_cat_cd) cash_receipt_nbr,  ");
            sql.append(" to_char(cs.receipt_dttm, 'DD/MM/YYYY') receipt_dttm FROM dn_details dn LEFT JOIN cash_sales_item csi ON ");
            sql.append(" dn.dn_nbr = csi.ref_nbr OR csi.tariff_desc LIKE '%' || dn.dn_nbr || '%' LEFT JOIN cash_sales cs ON ");
            sql.append(" cs.cash_receipt_nbr = csi.cash_receipt_nbr WHERE dn.dn_nbr = :dnNbr  ");

            for (int i = 1; i < dnList.size(); i++) {
                sql.append(" OR DN.DN_NBR =:dnNbr"+i);
            }
            sql.append(" ORDER BY DN.DN_NBR) GROUP BY dn_nbr, receipt_dttm ");

            EdoValueObjectOps edoVO = (EdoValueObjectOps) dnList.get(0);
            log.info(edoVO.getDnNbr());
            paramMap.put("dnNbr", edoVO.getDnNbr());
            for (int i = 1; i < dnList.size(); i++) {
                edoVO = (EdoValueObjectOps) dnList.get(i);
                log.info(edoVO.getDnNbr());
                paramMap.put("dnNbr"+i, edoVO.getDnNbr());
            }
            log.info("getCashSales SQL: " + sql.toString());
            log.info("paramMap: " + paramMap);
            log.info(" ***SQL *****" + sql.toString());
			log.info(" ***paramMap *****" + paramMap.toString());
            rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

            List<List<String>> tmpCashList = new ArrayList<List<String>>();
            while (rs.next()) {
                List<String> cs = new ArrayList<String>();
                cs.add(CommonUtility.deNull(rs.getString(1)).trim());
                cs.add(CommonUtility.deNull(rs.getString(2)).trim());
                cs.add(CommonUtility.deNull(rs.getString(3)).trim());
                tmpCashList.add(cs);
            }

            cashList = new ArrayList<CashSalesValueObject>();
            for (int i=0; i<dnList.size(); i++) {
            	boolean added = false;
            	edoVO = (EdoValueObjectOps)dnList.get(i);
            	for (int j=0; j<tmpCashList.size(); j++) {
            		List<String> cs = tmpCashList.get(j);
            		if (edoVO.getDnNbr().trim().equalsIgnoreCase(cs.get(0).toString())) {
                        CashSalesValueObject csVO = new CashSalesValueObject();
                        csVO.setCash_receipt_nbr(cs.get(1).toString());
                        csVO.setReceipt_dttm(cs.get(1).toString());
                        cashList.add(csVO);
                        added = true;
                        break;
            		}
            	}
            	if (!added) {
            		CashSalesValueObject csVO = new CashSalesValueObject();
                    csVO.setCash_receipt_nbr("");
                    csVO.setCash_receipt_nbr("");
                    cashList.add(csVO);
            	}
            }

        } catch (NullPointerException e) { 
        	log.info("Exception getCashSales : ", e);
        	throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCashSales : ", e);
			throw new BusinessException("M4201");
		} finally{
			log.info("END: getCashSales  DAO  END.  cashList: " + cashList);
		}
        return cashList;
    }
	
	
	 public CashSalesValueObject getCashSales(String refNbr) throws BusinessException {
	        CashSalesValueObject csVO = new CashSalesValueObject();
	        SqlRowSet rs = null;
	        StringBuffer sql = new StringBuffer();
	        Map<String,String> paramMap = new HashMap<String,String>();
	        try {
	        	log.info("START:getCashSales DAO refNbr: " + CommonUtility.deNull(refNbr));
	            sql.append("SELECT cs.cash_receipt_nbr, cs.receipt_dttm, cs.total_amt, cs.total_cash_amt, cs.total_cheq_amt, cs.CASH_SALES_TYPE, ");
	            sql.append("n.trans_amt, d.trans_amt ");
	            sql.append("FROM ((cash_sales cs LEFT JOIN cash_sales_eftpos_receipt n ON cs.cash_receipt_nbr = n.cash_receipt_nbr) ");
	            sql.append("LEFT JOIN cash_sales_cashcard_receipt d ON cs.cash_receipt_nbr = d.cash_receipt_nbr) ");
	            sql.append("JOIN cash_sales_item csi ");
	            sql.append("ON cs.cash_receipt_nbr = csi.cash_receipt_nbr ");
	            sql.append("WHERE csi.ref_nbr =:refNbr  ");
	            // end change

	            paramMap.put("refNbr", refNbr);
	            log.info(" ***SQL *****" + sql.toString());
				log.info(" ***paramMap *****" + paramMap.toString());
	            rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
	            if (rs.next()) {
	                csVO.setCash_receipt_nbr(CommonUtility.deNull(rs.getString("cs.cash_receipt_nbr")).trim());
	                csVO.setReceipt_dttm(CommonUtility.deNull(rs.getString("cs.receipt_dttm")).trim());
	                csVO.setTotal_amt(rs.getDouble("cs.total_amt"));
	                csVO.setTotal_cash_amt(rs.getDouble("cs.total_cash_amt"));
	                csVO.setTotal_cheq_amt(rs.getDouble("cs.total_cheq_amt"));
	                csVO.setCsType(rs.getString("cs.CASH_SALES_TYPE"));
	                // added fields for cashless transaction
	                csVO.setTotal_cash_nets_amt(rs.getDouble("n.trans_amt"));
	                csVO.setTotal_cash_card_amt(rs.getDouble("d.trans_amt"));
	                // end add
	            } else {
	            	csVO.setCash_receipt_nbr("");
	                csVO.setReceipt_dttm("");
	            }
	        } catch (NullPointerException ex) {
	        	log.info("Exception getCashSales : ", ex);
	        	throw new BusinessException("M4201");
	        } catch (Exception e) {
	        	log.info("Exception getCashSales : ", e);
	        	throw new BusinessException("M4201");
	        } finally {
	        	log.info("END: getCashSales DAO csVO:"+csVO);
	        }
	        return csVO;
	    }

	    // added by Chua on 06 June 2008
	    public String getMachineID(String recNbr) throws BusinessException  {
	    	String machineId =  "";
			SqlRowSet rs = null;
	        StringBuffer sql = new StringBuffer();
	        Map<String,String> paramMap = new HashMap<String,String>();
	        try {
	        	log.info("START: DAO getMachineID recNbr: " + CommonUtility.deNull(recNbr));
	            sql.setLength(0);
	            sql.append("select MACHINE_ID from cash_sales_nets ");
	            sql.append("where cash_receipt_nbr =:cash_receipt_nbr ");

	            paramMap.put("cash_receipt_nbr", recNbr);
	            
	            log.info(" ***SQL *****" + sql.toString());
				log.info(" ***paramMap *****" + paramMap.toString());
	            rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
	            if (rs.next()) {
	            	machineId = CommonUtility.deNull(rs.getString("MACHINE_ID")).trim();
	            }
	           
	        } catch (Exception e) {
	        	log.info("Exception getMachineID : ", e);
	        	throw new BusinessException("M4201");
	        } finally {
	        	log.info("END: getMachineID machineId:"+machineId);
	        }
	        return machineId;
	    }
	    
	    
	    public String getCashSalesPaymentCode(String cashsalesType) throws BusinessException  {
	    	String payment_code = "";
			SqlRowSet rs = null;
	        StringBuffer sql = new StringBuffer();
	        Map<String,String> paramMap = new HashMap<String,String>();
	        try {
	        	log.info("START: getCashSalesPaymentCode DAO cashsalesType: " + CommonUtility.deNull(cashsalesType));
	            sql.setLength(0);
	            sql.append("SELECT MISC_TYPE_NM FROM MISC_TYPE_CODE WHERE CAT_CD = 'PAYMENT_CD' AND UPPER(MISC_TYPE_CD) = UPPER(:cashsalesType) ");
	            paramMap.put("cashsalesType", cashsalesType);
	            
	            log.info(" ***SQL *****" + sql.toString());
				log.info(" ***paramMap *****" + paramMap.toString());
	            rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
	            if (rs.next()) {
	            	payment_code = CommonUtility.deNull(rs.getString("MISC_TYPE_NM")).trim();
	            }
	           
	        } catch (Exception e) {
	        	log.info("Exception getCashSalesPaymentCode : ", e);
	        	throw new BusinessException("M4201");
	        } finally {
	        	log.info("END: getCashSalesPaymentCode payment_code:"+payment_code);
	        }
	        return payment_code;
	    }
	    
	    
	public String getNETSRefID(String receiptNo) throws BusinessException {
		String TransID = "";
		String CardNo = "";
		String NETSRefID = "";
		SqlRowSet rs = null;
		StringBuffer sql = new StringBuffer();
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START:getNETSRefID DAO receiptNo: " + CommonUtility.deNull(receiptNo));
			sql.setLength(0);
			sql.append(
					"SELECT NETS_TRANS_ID, NETS_CARD_NO FROM CASH_SALES_NETS WHERE UPPER(CASH_RECEIPT_NBR) =:receiptNo ");
			paramMap.put("receiptNo", receiptNo);
			
			log.info(" ***SQL *****" + sql.toString());
			log.info(" ***paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				TransID = CommonUtility.deNull(rs.getString("NETS_TRANS_ID")).trim();
				CardNo = CommonUtility.deNull(rs.getString("NETS_CARD_NO")).trim();
			}

			if (TransID.equalsIgnoreCase("NA")) {
				NETSRefID = CardNo;
			} else {
				NETSRefID = TransID;
			}

		} catch (Exception e) {
			log.info("Exception getNETSRefID : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:getNETSRefID NETSRefID:" + NETSRefID);
		}

		return NETSRefID;
	}
}
