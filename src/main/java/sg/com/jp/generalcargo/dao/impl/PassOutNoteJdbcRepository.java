package sg.com.jp.generalcargo.dao.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import sg.com.jp.generalcargo.dao.PassOutNoteRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.PassOutNoteFormValueObject;
import sg.com.jp.generalcargo.domain.PassOutNoteValueObject;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("PassOutNoteRepository")
public class PassOutNoteJdbcRepository implements PassOutNoteRepository {

	private static final Log log = LogFactory.getLog(PassOutNoteJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// jp.src.ejb.sessionBeans.gbms.ops.tenant.passOutNote-->PassOutNoteEJB-->createPassOutNote()
	/**
	 * Create new PassOutNote
	 * 
	 * @param passOutNoteFormVO
	 * @return
	 */
	@Override
	public boolean createPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();    
		StringBuilder sb = new StringBuilder();
		String passOutNoteSeq = null;
		String tmpPassOutNoteSeq = null;
		String standCompanyCode = null;
		String passOutNoteId = null;

		try {
			log.info("START: createPassOutNote  DAO  Start passOutNoteFormVO:" + passOutNoteFormVO.toString());

			//				//Try to get max seq uat value
			//				try{
			//					String sqlGetPassOutNoteId = "select PASSOUT_NOTE_SEQ.NEXTVAL seqno from dual";
			//					pstmt = conn.prepareStatement(sqlGetPassOutNoteId);
			//		            rs = pstmt.executeQuery();
			//		            if (rs.next()){
			//		            	tmpPassOutNoteSeq = rs.getString("seqno");
			//		            }
			//		            //System.out.println("=== passOutNoteSeq: "+passOutNoteSeq);
			//		            tmpPassOutNoteSeq = "000000"+tmpPassOutNoteSeq;
			//		            int length = tmpPassOutNoteSeq.length();
			//		            passOutNoteSeq = tmpPassOutNoteSeq.substring(length-6,length);
			//				}catch (Exception e) {
			//					e.printStackTrace();
			//					return false;
			//		        }

			// Try to get Standard Company Code
			try {
				sb.append(" SELECT misc_type_nm FROM misc_type_code ");
				sb.append(" WHERE cat_cd = 'TENANT_REF' and MISC_TYPE_CD =:miscTypeCd ");
				sb.append(" AND REC_STATUS = 'A' ");


				paramMap.put("miscTypeCd", passOutNoteFormVO.getPassOutNoteValueObject().getCompanyCode());
				log.info(" *** createPassOutNote SQL *****" + sb.toString() +" paramMap "+paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

				if (rs.next()) {
					standCompanyCode = rs.getString("misc_type_nm");
				}
			} catch (Exception e) {
				log.info("Exception: createPassOutNote ", e);
				return false;
			}

			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int yearYY = year % 100;

			try {
				sb = new StringBuilder();
				paramMap = new HashMap<String, Object>();
				sb.append(" SELECT pon_id FROM passout_note WHERE pon_id ");
				sb.append(" LIKE :standCompanyCode ORDER BY pon_id DESC ");


				paramMap.put("standCompanyCode", "P" + standCompanyCode + "%");

				log.info(" *** createPassOutNote SQL *****" + sb.toString() +" paramMap "+paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				if (rs.next()) {
					tmpPassOutNoteSeq = rs.getString("pon_id");
					if (tmpPassOutNoteSeq != null && tmpPassOutNoteSeq.length() == 13) {
						tmpPassOutNoteSeq = tmpPassOutNoteSeq.substring(7);
					} else {
						tmpPassOutNoteSeq = null;
					}
				}

				if (tmpPassOutNoteSeq == null) {
					tmpPassOutNoteSeq = "000000";
				}

				tmpPassOutNoteSeq = "" + (Integer.parseInt(tmpPassOutNoteSeq) + 1);
				tmpPassOutNoteSeq = "000000" + tmpPassOutNoteSeq;
				int length = tmpPassOutNoteSeq.length();
				passOutNoteSeq = tmpPassOutNoteSeq.substring(length - 6, length);
			} catch (Exception e) {
				log.info("Exception: createPassOutNote ", e);
				return false;
			}

			if (passOutNoteSeq != null && standCompanyCode != null) {
				passOutNoteId = "P" + standCompanyCode + yearYY + passOutNoteSeq;
			} else {
				return false;
			}

			sb = new StringBuilder();
			paramMap = new HashMap<String, Object>();
			sb.append(" INSERT INTO passout_note (pon_id, truck_nbr, driver_pass_id, ");
			sb.append(" tenant_co_cd, pkgs_nbr, crg_marks, crg_desc, create_remarks, ");
			sb.append(" create_user_id, create_dttm, last_modify_user_id, ");
			sb.append(" last_modify_dttm, pon_status, consignee_nm, declr_person, ");
			sb.append(" declr_co_nm, nric_no ) VALUES (:ponId,:truckNbr, ");
			sb.append(" :driverPassId,:tenantCoCd,:pkgsNbr,:crgMarks, ");
			sb.append(" :crgDesc,:createRemarks,:userId,SYSDATE, ");
			sb.append(" :userId,SYSDATE,:ponStatus,:consigneeNm, ");
			sb.append(" :declrPerson,:declrCoNm,:nricNo) ");


			// pon_id
			paramMap.put("ponId", passOutNoteId);
			// truck_nbr
			paramMap.put("truckNbr", passOutNoteFormVO.getPassOutNoteValueObject().getTruckPlaceNo());
			// driver_pass_id
			paramMap.put("driverPassId", passOutNoteFormVO.getPassOutNoteValueObject().getDriverPassNo());
			// tenant_co_cd
			paramMap.put("tenantCoCd", passOutNoteFormVO.getPassOutNoteValueObject().getCompanyCode());
			// pkgs_nbr
			paramMap.put("pkgsNbr", Integer.parseInt(passOutNoteFormVO.getPassOutNoteValueObject().getNoOfPkgs()));
			
			// 53711_PB-86 Creation of PON in JPOM - when user used special character '&', system display distorted details &amp; - start
			/* // crg_marks
			paramMap.put("crgMarks", passOutNoteFormVO.getPassOutNoteValueObject().getCargoMarks());
			// crg_desc
			paramMap.put("crgDesc", passOutNoteFormVO.getPassOutNoteValueObject().getCargoDesc());
			// create_remarks
			paramMap.put("createRemarks", passOutNoteFormVO.getPassOutNoteValueObject().getRemarks());*/
			// crg_marks
			paramMap.put("crgMarks", passOutNoteFormVO.getPassOutNoteValueObject().getCargoMarks().replaceAll("&amp;","&"));
			// crg_desc
			paramMap.put("crgDesc", passOutNoteFormVO.getPassOutNoteValueObject().getCargoDesc().replaceAll("&amp;","&"));
			// create_remarks
			String remarks = passOutNoteFormVO.getPassOutNoteValueObject().getRemarks();
			log.info(" ***Remarks in ParamMap: ***" +remarks);
			if (remarks == null || remarks.trim().isEmpty()) {
				   paramMap.put("createRemarks", passOutNoteFormVO.getPassOutNoteValueObject().getRemarks());
				} else {
				   paramMap.put("createRemarks", passOutNoteFormVO.getPassOutNoteValueObject().getRemarks().replaceAll("&amp;","&"));
				}
			// 53711_PB-86 Creation of PON in JPOM - when user used special character '&', system display distorted details &amp; - end */
			
			// create_user_id
			paramMap.put("userId", passOutNoteFormVO.getPassOutNoteValueObject().getLoginUser());
			// create_dttm
			// paramMap.put(10, uatFormVO.getUatValueObject().getDateTime());
			// last_modify_user_id
			// last_modify_dttm
			// paramMap.put(12, uatFormVO.getUatValueObject().getDateTime());
			// pon_status
			paramMap.put("ponStatus", passOutNoteFormVO.getPassOutNoteValueObject().getStatus());
			
			//53711_PB-86 Creation of PON in JPOM - when user used special character '&', system display distorted details &amp; - start
			/*// consignee_nm
			paramMap.put("consigneeNm", passOutNoteFormVO.getPassOutNoteValueObject().getConsignee());
			// declr_person
			paramMap.put("declrPerson", passOutNoteFormVO.getPassOutNoteValueObject().getNameDecl());
			// declr_co_nm
			paramMap.put("declrCoNm", passOutNoteFormVO.getPassOutNoteValueObject().getCompanyDecl());*/
			// consignee_nm
			paramMap.put("consigneeNm", passOutNoteFormVO.getPassOutNoteValueObject().getConsignee().replaceAll("&amp;","&"));
			// declr_person
			paramMap.put("declrPerson", passOutNoteFormVO.getPassOutNoteValueObject().getNameDecl().replaceAll("&amp;","&"));
			// declr_co_nm
			paramMap.put("declrCoNm", passOutNoteFormVO.getPassOutNoteValueObject().getCompanyDecl().replaceAll("&amp;","&"));
			//53711_PB-86 Creation of PON in JPOM - when user used special character '&', system display distorted details &amp; - end 
			
			// nric_no
			paramMap.put("nricNo", passOutNoteFormVO.getPassOutNoteValueObject().getNricNo());

			log.info(" *** createPassOutNote SQL *****" + sb.toString() +" paramMap "+paramMap.toString());
			int i = namedParameterJdbcTemplate.update(sb.toString(), paramMap);


			if (i == 0) {
				return false;
			} else {
				sb = new StringBuilder();
				paramMap = new HashMap<String, Object>();
				sb.append(" SELECT TO_CHAR(create_dttm, 'DDMMYYYY HH24MI') create_dttm, ");
				sb.append(" truck_nbr,gate_out_dttm,pon_status,pkgs_nbr,crg_Desc ");
				sb.append(" FROM passout_note WHERE pon_id =:passOutNoteId ");


				paramMap.put("passOutNoteId", passOutNoteId);
				log.info(" *** createPassOutNote SQL *****" + sb.toString() +" paramMap "+paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				if (rs.next()) {
					String createDttm = rs.getString("create_dttm");
					String truckNbr = rs.getString("truck_nbr");
					String gateOutDttm = rs.getString("gate_out_dttm");
					String passOutNoteStatus = rs.getString("pon_status");
					int pkgsNbr = rs.getInt("pkgs_nbr");
					String crgDesc = rs.getString("crg_Desc");
					// String deleteRemark = rs.getString("delete_remarks");
					passOutNoteFormVO.getPassOutNoteValueObject().setDateTime(createDttm);
					passOutNoteFormVO.getPassOutNoteValueObject().setPassOutNoteId(passOutNoteId);
					passOutNoteFormVO.getPassOutNoteValueObject().setTruckPlaceNo(truckNbr);
					passOutNoteFormVO.getPassOutNoteValueObject().setGateOutDttm(gateOutDttm);
					passOutNoteFormVO.getPassOutNoteValueObject().setStatus(passOutNoteStatus);
					passOutNoteFormVO.getPassOutNoteValueObject().setNoOfPkgs("" + pkgsNbr);
					passOutNoteFormVO.getPassOutNoteValueObject().setCargoDesc(crgDesc);
					// uatFormVO.getUatValueObject().setDeleteRemark(deleteRemark);
				}
				return true;
			}
		} catch (NullPointerException e) {
			log.info("Exception: createPassOutNote ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: createPassOutNote ", e);
			throw new BusinessException("M4201");
		}  finally {

			log.info("END: createPassOutNote  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.ops.tenant.passOutNote-->PassOutNoteEJB-->searchPassOutNote()
	/**
	 * Search PassOutNote
	 * 
	 * @param passOutNoteFormVO
	 */
	@Override
	public TableResult searchPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO, Criteria criteria)
			throws BusinessException {
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		TableResult tableResult = new TableResult();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		PassOutNoteValueObject rsPassOutNoteVO = null;

		try {
			log.info("START: searchPassOutNote  DAO  Start passOutNoteFormVO:" + passOutNoteFormVO.toString() +" criteria:"+criteria.toString() );

			String srchPassOutNoteNo = passOutNoteFormVO.getPassOutNoteValueObject().getSearchPassOutNoteNo();
			String srchDateFrom = passOutNoteFormVO.getPassOutNoteValueObject().getSearchDateFrom();
			String srchDateTo = passOutNoteFormVO.getPassOutNoteValueObject().getSearchDateTo();
			String srchCompanyCode = passOutNoteFormVO.getPassOutNoteValueObject().getSearchCompanyCode();
			boolean srchActive = passOutNoteFormVO.getPassOutNoteValueObject().isSearchActive();
			boolean flagCompany = false;
			if (srchPassOutNoteNo == null || srchPassOutNoteNo.equals("")) {
				boolean flagDate = false;

				sb.append(" SELECT TO_CHAR(create_dttm, 'DDMMYYYY HH24MI') create_dttm, ");
				sb.append(" create_dttm create_dttm1, pon_id, truck_nbr, ");
				sb.append(" TO_CHAR(gate_out_dttm, 'DDMMYYYY HH24MI') gate_out_dttm, ");
				sb.append(" pon_status, pkgs_nbr, ");
				sb.append(" crg_desc FROM passout_note");
				if (srchCompanyCode != null && !srchCompanyCode.equals("")) {
					sb.append(" WHERE tenant_co_cd =:srchCompanyCode ");
					flagCompany = true;
				}

				if (srchDateFrom != null && !srchDateFrom.equals("") && srchDateFrom != null
						&& !srchDateFrom.equals("")) {
					if(!sb.toString().contains("WHERE")) {
						sb.append(" WHERE");
					}
					if (flagCompany) {
						sb.append(" and ");
					}
					sb.append(" TO_DATE(TO_CHAR(create_dttm,'ddmmyyyy'),'ddmmyyyy') ");
					sb.append(" >= TO_DATE(:srchDateFrom,'DDMMYYYY') and TO_DATE(TO_CHAR(create_dttm, ");
					sb.append(" 'ddmmyyyy'),'ddmmyyyy') <= TO_DATE(:srchDateTo,'DDMMYYYY') ");
					flagDate = true;
				}

				if (srchActive) {
					sb.append(" AND pon_status = 'A' and gate_out_dttm is null ");
				}

				sb.append(" ORDER BY create_dttm1 DESC ");



				if (flagCompany) {
					paramMap.put("srchCompanyCode", srchCompanyCode);
					if (flagDate) {

						if (srchDateFrom != null && !srchDateFrom.equals("") && srchDateFrom != null
								&& !srchDateFrom.equals("")) {
							paramMap.put("srchDateFrom", srchDateFrom);
							paramMap.put("srchDateTo", srchDateTo);
						}
					}
				} else {

					if (srchDateFrom != null && !srchDateFrom.equals("") && srchDateFrom != null
							&& !srchDateFrom.equals("")) {
						paramMap.put("srchDateFrom", srchDateFrom);
						paramMap.put("srchDateTo", srchDateTo);
					}
				}
			}

			else {
				sb.append(" SELECT TO_CHAR(create_dttm, 'DDMMYYYY HH24MI') create_dttm, ");
				sb.append(" create_dttm create_dttm1, pon_id, truck_nbr, ");
				sb.append(" TO_CHAR(gate_out_dttm, 'DDMMYYYY HH24MI') gate_out_dttm, ");
				sb.append(" pon_status, pkgs_nbr, crg_desc FROM ");
				sb.append(" passout_note WHERE pon_id =:srchPassOutNoteNo ");

				if (srchActive) {
					sb.append(" AND pon_status = 'A' ");
				}

				if (srchCompanyCode != null && !srchCompanyCode.equals("")) {
					sb.append(" AND tenant_co_cd =:srchCompanyCode ");
					flagCompany = true;
				}

				sb.append("ORDER BY create_dttm1 DESC");



				paramMap.put("srchPassOutNoteNo", srchPassOutNoteNo);
				if (flagCompany) {
					paramMap.put("srchCompanyCode", srchCompanyCode);
				}
			}
			log.info(" *** searchPassOutNote SQL *****" + sb.toString() +" paramMap "+paramMap.toString());
			String sql = sb.toString();
			if(criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
						paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
				
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());
			}
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			List<PassOutNoteValueObject> tmpPassOutNoteVOList = new ArrayList<PassOutNoteValueObject>();
			while (rs.next()) {
				rsPassOutNoteVO = new PassOutNoteValueObject();

				String createDttm = rs.getString("create_dttm");
				String passOutNoteId = rs.getString("pon_id");
				String truckNbr = rs.getString("truck_nbr");
				String gateOutDttm = rs.getString("gate_out_dttm");
				String passOutNoteStatus = rs.getString("pon_status");
				String pkgsNbr = rs.getString("pkgs_nbr");
				String crgDesc = rs.getString("crg_desc");

				rsPassOutNoteVO.setDateTime(createDttm);
				rsPassOutNoteVO.setPassOutNoteId(passOutNoteId);
				rsPassOutNoteVO.setTruckPlaceNo(truckNbr);
				rsPassOutNoteVO.setGateOutDttm(gateOutDttm);
				rsPassOutNoteVO.setStatus(passOutNoteStatus);
				rsPassOutNoteVO.setNoOfPkgs(pkgsNbr);
				rsPassOutNoteVO.setCargoDesc(crgDesc);

				tmpPassOutNoteVOList.add(rsPassOutNoteVO);
			}
			passOutNoteFormVO.setPassOutNoteValueObjectList(tmpPassOutNoteVOList);

			topsModel.put(passOutNoteFormVO);
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			
			log.info("END: *** searchPassOutNote Result *****" + topsModel.getSize());

		} catch (NullPointerException e) {
			log.info("Exception: searchPassOutNote ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: searchPassOutNote ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: searchPassOutNote  DAO  END");
		}
		return tableResult;
	}

	// jp.src.ejb.sessionBeans.gbms.ops.tenant.passOutNote-->PassOutNoteEJB-->checkDeletedPassOutNote()
	/**
	 * Check a Pass Out Note is deleted or not Return false if cannot delete
	 * 
	 * @param passOutNoteFormVO
	 * @return
	 */
	@Override
	public boolean checkDeletedPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();   
		StringBuilder sb = new StringBuilder();
		boolean result = true;
		try {
			log.info("START: checkDeletedPassOutNote  DAO  Start passOutNoteFormVO:" + passOutNoteFormVO.toString());

			String[] selectedPassOutNote = passOutNoteFormVO.getSelectedPassOutNote();
			StringBuffer deletedPassOutNote = new StringBuffer();
			if (selectedPassOutNote == null || selectedPassOutNote.length == 0) {
				result = false;
			} else {
				deletedPassOutNote.append("('" + selectedPassOutNote[0] + "'");

				for (int i = 1; i < selectedPassOutNote.length; i++) {
					deletedPassOutNote.append(",'" + selectedPassOutNote[i] + "'");
				}

				deletedPassOutNote.append(")");

				sb.append(" SELECT pon_status FROM passout_note WHERE pon_id IN :deletedPassOutNote ");


				paramMap.put("deletedPassOutNote", deletedPassOutNote.toString());

				log.info(" *** checkDeletedPassOutNote SQL *****" + sb.toString() +" paramMap "+paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

				while (rs.next()) {
					String status = rs.getString("pon_status");
					if (!"A".equals(status)) {
						result = false;
						break;
					}
				}
			}
		} catch (NullPointerException e) {
			log.info("Exception: checkDeletedPassOutNote ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: checkDeletedPassOutNote ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkDeletedPassOutNote  DAO  END");

		}
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.ops.tenant.passOutNote-->PassOutNoteEJB-->deletePassOutNote()
	/**
	 * Delete PassOutNote
	 * 
	 * @param passOutNoteFormVO
	 * @return
	 */
	@Override
	public boolean deletePassOutNote(PassOutNoteFormValueObject passOutNoteFormVO) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();   
		StringBuilder sb = new StringBuilder();
		boolean result = false;
		try {
			log.info("START: deletePassOutNote  DAO  Start passOutNoteFormVO:" + passOutNoteFormVO.toString());

			String[] selectedPassOutNote = passOutNoteFormVO.getSelectedPassOutNote();
			StringBuffer deletedPassOutNote = new StringBuffer();
			if (selectedPassOutNote == null || selectedPassOutNote.length == 0) {
				result = false;
			} else {
				deletedPassOutNote.append("('" + selectedPassOutNote[0] + "'");

				for (int i = 1; i < selectedPassOutNote.length; i++) {
					deletedPassOutNote.append(",'" + selectedPassOutNote[i] + "'");
				}

				deletedPassOutNote.append(")");

				sb.append(" UPDATE passout_note SET pon_status='X', last_modify_user_id = :userId, ");
				sb.append(" last_modify_dttm = SYSDATE, delete_remarks=:deleteRemarks WHERE ");
				sb.append("  pon_id IN ");
				sb.append(deletedPassOutNote.toString());

				paramMap.put("userId", passOutNoteFormVO.getPassOutNoteValueObject().getCreatedBy());
				paramMap.put("deleteRemarks", passOutNoteFormVO.getPassOutNoteValueObject().getDeleteRemark());

				log.info(" *** deletePassOutNote SQL *****" + sb.toString() +" paramMap "+paramMap.toString());
				int i = namedParameterJdbcTemplate.update(sb.toString(), paramMap);

				if (i == 0) {
					result = false;
				} else {
					result = true;
				}
			}
		} catch (NullPointerException e) {
			log.info("Exception: deletePassOutNote ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: deletePassOutNote ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: deletePassOutNote  DAO  END");
		}
		passOutNoteFormVO.setResult(result);
		log.info("passOutNoteFormVO " + passOutNoteFormVO.toString());
		return result;

	}

	// jp.src.ejb.sessionBeans.gbms.ops.tenant.passOutNote-->PassOutNoteEJB-->viewPassOutNote()
	/**
	 * View PassOutNote by PassOutNoteId
	 * 
	 * @param passOutNoteFormVO
	 * @return
	 */
	@Override
	public PassOutNoteFormValueObject viewPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO)
			throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();   
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: viewPassOutNote  DAO  Start passOutNoteFormVO:" + passOutNoteFormVO.toString());

			sb.append(" SELECT TO_CHAR(passout_note.create_dttm, 'DDMMYYYY HH24MI') create_dttm, ");
			sb.append(" pon_id, truck_nbr, gate_out_dttm, pon_status, pkgs_nbr, user_nm, ");
			sb.append(" tenant_co_cd, misc_type_nm,driver_pass_id,cardhldr_name,crg_marks, ");
			sb.append(" crg_Desc,create_remarks,delete_remarks, misc_type_nm,driver_pass_id, ");
			sb.append(" crg_marks,crg_Desc,create_remarks,delete_remarks, ");
			sb.append(" consignee_nm,declr_person,declr_co_nm,passout_note.nric_no ");
			sb.append(" FROM passout_note JOIN logon_acct ON ");
			sb.append(" passout_note.create_user_id = logon_acct.login_id ");
			sb.append(" JOIN misc_type_code ON passout_note.tenant_co_cd = ");
			sb.append(" misc_type_code.misc_type_cd LEFT JOIN jc_carddtl on ");
			sb.append(" passout_note.driver_pass_id = jc_carddtl.id_no ");
			sb.append(" WHERE pon_id =:passOutNoteId AND misc_type_code.cat_cd = 'TENANT' ");


			paramMap.put("passOutNoteId", passOutNoteFormVO.getPassOutNoteValueObject().getPassOutNoteId());
			log.info(" *** viewPassOutNote SQL *****" + sb.toString() +" paramMap "+paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			// PassOutNoteValueObject passOutNoteVO = new PassOutNoteValueObject();

			if (rs.next()) {
				String passOutNoteId = rs.getString("pon_id");
				String createDttm = rs.getString("create_dttm");
				String truckNbr = rs.getString("truck_nbr");
				String gateOutDttm = rs.getString("gate_out_dttm");
				String passOutNoteStatus = rs.getString("pon_status");
				int pkgsNbr = rs.getInt("pkgs_nbr");
				// String createdBy = rs.getString("create_user_id");
				String createdBy = rs.getString("user_nm");
				String companyCode = rs.getString("tenant_co_cd");
				String companyName = rs.getString("misc_type_nm");
				String driverPassNo = rs.getString("driver_pass_id");
				String driverName = rs.getString("cardhldr_name");
				String crgMark = rs.getString("crg_marks");
				String crgDesc = rs.getString("crg_Desc");
				String createRemark = rs.getString("create_remarks");
				String deleteRemark = rs.getString("delete_remarks");
				String consignee = rs.getString("consignee_nm");
				String declrPerson = rs.getString("declr_person");
				String declrCoNm = rs.getString("declr_co_nm");
				String nricNo = rs.getString("nric_no");

				passOutNoteFormVO.getPassOutNoteValueObject().setDateTime(createDttm);
				passOutNoteFormVO.getPassOutNoteValueObject().setPassOutNoteId(passOutNoteId);
				passOutNoteFormVO.getPassOutNoteValueObject().setTruckPlaceNo(truckNbr);
				passOutNoteFormVO.getPassOutNoteValueObject().setGateOutDttm(gateOutDttm);
				passOutNoteFormVO.getPassOutNoteValueObject().setStatus(passOutNoteStatus);
				passOutNoteFormVO.getPassOutNoteValueObject().setNoOfPkgs("" + pkgsNbr);
				passOutNoteFormVO.getPassOutNoteValueObject().setCargoDesc(crgDesc);
				passOutNoteFormVO.getPassOutNoteValueObject().setDeleteRemark(deleteRemark);
				passOutNoteFormVO.getPassOutNoteValueObject().setCreatedBy(createdBy);
				passOutNoteFormVO.getPassOutNoteValueObject().setCompanyCode(companyCode);
				passOutNoteFormVO.getPassOutNoteValueObject().setCompanyName(companyName);
				passOutNoteFormVO.getPassOutNoteValueObject().setDriverPassNo(driverPassNo);
				passOutNoteFormVO.getPassOutNoteValueObject().setDriverName(driverName);
				passOutNoteFormVO.getPassOutNoteValueObject().setCargoMarks(crgMark);
				passOutNoteFormVO.getPassOutNoteValueObject().setDeleteRemark(deleteRemark);
				passOutNoteFormVO.getPassOutNoteValueObject().setRemarks(createRemark);
				passOutNoteFormVO.getPassOutNoteValueObject().setNameDecl(declrPerson);
				passOutNoteFormVO.getPassOutNoteValueObject().setCompanyDecl(declrCoNm);
				passOutNoteFormVO.getPassOutNoteValueObject().setConsignee(consignee);
				passOutNoteFormVO.getPassOutNoteValueObject().setNricNo(nricNo);
			}
			
			log.info("END: *** viewPassOutNote Result *****" + passOutNoteFormVO.toString());
		} catch (NullPointerException e) {
			log.info("Exception: viewPassOutNote ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: viewPassOutNote ", e);
			throw new BusinessException("M4201");
		}  finally {

			log.info("END: viewPassOutNote  DAO  END");
		}
		return passOutNoteFormVO;
	}

	@Override
	public List<Map<String, Object>> printPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO)
			throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			log.info("START: printPassOutNote  DAO  Start passOutNoteFormVO:" + passOutNoteFormVO.toString());
			
			sb.append(" SELECT TO_CHAR(passout_note.create_dttm, 'DDMMYYYY HH24MI') create_dttm, ");
			sb.append(" pon_id, truck_nbr, gate_out_dttm, pon_status, pkgs_nbr, user_nm, ");
			sb.append(" tenant_co_cd, misc_type_nm,'XXXXXXX' || SUBSTR(passout_note.driver_pass_id, -4) AS driver_pass_id ,cardhldr_name,crg_marks, ");
			sb.append(" crg_Desc,create_remarks,delete_remarks, misc_type_nm,'XXXXXXX' || SUBSTR(passout_note.driver_pass_id, -4) AS driver_pass_id, ");
			sb.append(" crg_marks,crg_Desc,create_remarks,delete_remarks, ");
			sb.append(" consignee_nm,declr_person,declr_co_nm,'XXXXXXX' || SUBSTR(passout_note.nric_no, -4) AS nric_no ");
			sb.append(" FROM passout_note JOIN logon_acct ON ");
			sb.append(" passout_note.create_user_id = logon_acct.login_id ");
			sb.append(" JOIN misc_type_code ON passout_note.tenant_co_cd = ");
			sb.append(" misc_type_code.misc_type_cd LEFT JOIN jc_carddtl on ");
			sb.append(" passout_note.driver_pass_id = jc_carddtl.id_no ");
			sb.append(" WHERE pon_id =:passOutNoteId AND misc_type_code.cat_cd = 'TENANT' ");

			paramMap.put("passOutNoteId", passOutNoteFormVO.getPassOutNoteValueObject().getPassOutNoteId());
			log.info(" ***printPassOutNote SQL *****" + sb.toString() +" paramMap "+paramMap.toString());
			
			list = namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap);
		

			log.info("END: *** printPassOutNote Result *****" + passOutNoteFormVO.toString());
			log.info("END: *** printPassOutNote Result *****" + list.size());
		} catch (NullPointerException e) {
			log.info("Exception printPassOutNote :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception printPassOutNote :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: printPassOutNote  DAO  END");
		}
		return list;
	}
	
	@Override
	public JasperPrint fillReports(InputStream is, Map<String, Object> parameters) {

		JasperPrint jasperPrint = null;
		try {
			log.info("START: fillReports  DAO  Start parameters:" + parameters +" is:"+ is.toString());

			jasperPrint = JasperFillManager.fillReport(is, parameters);
			log.info("END: *** fillReports Result *****" + jasperPrint.toString());
			
		} catch (Exception e) {
			log.info("Exception fillReports :" , e);

		} finally {

			log.info("END: fillReports  DAO  END");
		}
		return jasperPrint;
	}
	
	@Override
	public String getCompanyName(String coCd) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		String companyName = "";
		try {
			log.info("START: getCompanyName coCd:" + CommonUtility.deNull(coCd));
			sb.append(" SELECT CO_NM AS coNm FROM tops.COMPANY_CODE WHERE CO_CD LIKE :coCd ");
			paramMap.put("coCd", coCd);
			companyName = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, String.class);
			log.info("SQL" + sb.toString());
		} catch (Exception e) {
			log.info("Exception getCompanyName : ", e);
		} finally {
			log.info("END: DAO getCompanyName");
		}
		return companyName;
	}
	
	@Override
	public String getCompanyCode(String companyName) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		String companyCode = "";
		try {
			log.info("START: getCompanyCode companyName:" + CommonUtility.deNull(companyName));
			sb.append(" select MISC_TYPE_CD from MISC_TYPE_CODE where CAT_CD = 'TENANT' ");
			sb.append(" and rec_status = 'A' and misc_type_nm =:companyName ");
			paramMap.put("companyName", companyName);
			companyCode = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, String.class);
			log.info("SQL" + sb.toString());
		} catch (Exception e) {
			log.info("Exception getCompanyCode : ", e);
		} finally {
			log.info("END: DAO getCompanyName");
		}
		return companyCode;
	}

}
