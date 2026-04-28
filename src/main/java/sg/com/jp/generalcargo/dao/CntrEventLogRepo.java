package sg.com.jp.generalcargo.dao;

import java.sql.Timestamp;

import sg.com.jp.generalcargo.domain.CntrEventLogKey;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CntrEventLogRepo {

	public CntrEventLogKey create(Integer cntrSeqNbr, String cntrNbr, String status, String prevPurpCd,
			Timestamp txnDttm, String txnCd, String userId, String craneNm, String craneOprId, String wtaId,
			String pmType, String pmNm, String pmOprId, String isoSizeTypeCd, String sizeFt, String catCd,
			Integer declrWt, Integer measureWt, String purpCd, String cntrOprCd, String haulCd, String loloPartyInd,
			String discSlotOprCd, String loadSlotOprCd, String renomSlotOprCd, String discVvCd, String ldVvCd,
			String renomVvCd, String psrc, String pload, String pdisc, String pdest, String dgInd, String imdgClCd,
			String refrInd, String ucInd, String overSzInd, String intergatewayInd, Timestamp discDttm,
			Timestamp loadDttm, Timestamp offloadDttm, Timestamp mountDttm, Timestamp arrDttm, Timestamp exitDttm,
			Timestamp changePurpDttm, String dirHdlgInd, String chasProvInd, String gearUsed, String pluginTemp,
			Timestamp pluginDttm, Timestamp unplugDttm, Integer ucHandlingDur, String athwartshipInd, String billVslInd,
			String billYdInd, String procQcIncentive, String procYcIncentive, String procPmIncentive,
			String lastModifyUserId, Timestamp lastModifyDttm) throws BusinessException;

}
