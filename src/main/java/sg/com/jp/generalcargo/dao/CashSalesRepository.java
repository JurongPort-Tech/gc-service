package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.CashSalesValueObject;
import sg.com.jp.generalcargo.domain.EdoValueObjectOps;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CashSalesRepository {

	public List<CashSalesValueObject> getCashSalesList() throws BusinessException;

	public List<CashSalesValueObject> getCashSales(List<EdoValueObjectOps> dnList) throws BusinessException;

	public CashSalesValueObject getCashSales(String refNbr) throws BusinessException;

	public String getMachineID(String recNbr) throws BusinessException;

	public String getCashSalesPaymentCode(String cashsalesType) throws BusinessException;

	public String getNETSRefID(String receiptNo) throws BusinessException;

}
