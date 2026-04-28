/*
 * RecordPaging.java
 * Copyright &#169 2005 Jurong Port Pte Ltd. 
 * All rights reserved, use is subject to license terms.
 * 
 * Revision History
 *   
 * Date        Author    SR#/CS/PM#/OTHERS   Desctiption
 * -----------------------------------------------------------------------------
 * 17-Mar-2003 Qida      /                   Remove the hashtable, and keep only 
 *                                           last requested resultset.
 * 15-Aug-2005 guoqiao   /                   Added javadoc.
 */
package sg.com.jp.generalcargo.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sg.com.jp.generalcargo.domain.TopsModel;

/**
 * The <code>RecordPaging</code> class is to handle pagination related
 * operation.
 * 
 * @author Rudy Sutjiato
 * @version 1.0 (Aug 15, 2005)
 */
public class RecordPaging implements Serializable {

	// private Hashtable cachedResultSet;

	private static final Log log = LogFactory.getLog(RecordPaging.class);

	private String lastCommand;

	private RecordPagingSet lastRecordSet;

	/**
	 * Default constructor.
	 */
	public RecordPaging() {
		// cachedResultSet = new Hashtable();
	}

	/**
	 * Create pagination record cache.
	 * 
	 * @param httpCommand key used to store and retrieve the record cache
	 * @param records     the record to be cached.
	 * @param recPerPage  number of record per page.
	 * @return numOfPage number of pages.
	 * @throws SysException if any error while creating the record cache
	 */
	public int createRecordPagingCache(String httpCommand, List records, int recPerPage) {
		int numOfPages = 0;
		if ((records != null) && (records.size() > 0)) {
			numOfPages = (records.size() % recPerPage) > 0 ? (records.size() / recPerPage) + 1
					: Math.round(records.size() / recPerPage);
			// cachedResultSet.put(httpCommand, new RecordPagingSet(records,
			// recPerPage, numOfPages));
			lastCommand = httpCommand;
			lastRecordSet = new RecordPagingSet(records, recPerPage, numOfPages);
		} else {
			log.info("Records is empty");
		}

		return numOfPages;
	}

	/**
	 * Create pagination record cache.
	 * 
	 * @param httpCommand key used to store and retrieve the record cache
	 * @param model       the TopsModel to be cached.
	 * @param recPerPage  number of record per page.
	 * @return numOfPage number of pages.
	 * @throws SysException if any error while creating the record cache
	 */
	public int createRecordPagingCache(String httpCommand, TopsModel model, int recPerPage) {
		List records = null;
		int numOfPages = 0;
		if ((model != null) && (model.getSize() > 0)) {
			records = new ArrayList(model.getSize());

			for (int i = 0; i < model.getSize(); i++)
				records.add(model.get(i));

			numOfPages = (model.getSize() % recPerPage) > 0 ? (model.getSize() / recPerPage) + 1
					: Math.round(model.getSize() / recPerPage);
			// cachedResultSet.put(httpCommand, new RecordPagingSet(records,
			// recPerPage, numOfPages));
			lastCommand = httpCommand;
			lastRecordSet = new RecordPagingSet(records, recPerPage, numOfPages);
		} else {
			log.info("Records is empty");
		}

		return numOfPages;
	}

	// START : RAJAGOPAL CR-CIM-20050913-15 06-Jan-2006
	// To capture the show number of records per page selection in the list screen
	public boolean setRecordPerPage(String httpCommand, int recPerPage) {
		List retObj = null;

		if (lastCommand == null || lastRecordSet == null || !lastCommand.equals(httpCommand))
			return false;

		RecordPagingSet recPgSet = lastRecordSet;
		List records = recPgSet.getRecords();
		if (recPerPage == 0)
			recPerPage = records.size();
		else if (recPerPage > records.size())
			recPerPage = records.size();
		createRecordPagingCache(httpCommand, records, recPerPage);
		return true;
	}

	// END : RAJAGOPAL CR-CIM-20050913-15 06-Jan-2006

	/**
	 * Get one page of records from the records cache.
	 * 
	 * @param httpCommand Key to retrieve back the records from records cache
	 * @param pageNum     Page number of the records page to retrieve
	 * @return records Records from cache as Collection object
	 * @throws SysException if any error while retrieving the records from cache
	 */
	public List getRecordsPage(String httpCommand, int pageNum) {
		List<?> retObj = null;

		// if (!cachedResultSet.containsKey(httpCommand))
		if (lastCommand == null || lastRecordSet == null || !lastCommand.equals(httpCommand))
			return null;

		// RecordPagingSet recPgSet = (RecordPagingSet)
		// cachedResultSet.get(httpCommand);
		RecordPagingSet recPgSet = lastRecordSet;
		List<?> records = recPgSet.getRecords();
		int numOfRec = recPgSet.getNumOfRec();
		int recPerPage = recPgSet.getRecPerPage();
		int numOfPage = recPgSet.getNumOfPages();

		if (pageNum > numOfPage)
			log.info("Data Not Available.");
		else {
			int startRecPos = (pageNum - 1) * recPerPage;
			int endRecPos = pageNum * recPerPage;
			if (endRecPos > numOfRec)
				endRecPos = numOfRec;

			int numOfRecRet = endRecPos - startRecPos;

			retObj = records.subList(startRecPos, endRecPos);
		}

		return retObj;
	}

	/**
	 * Get one page of records from the model cache.
	 * 
	 * @param httpCommand the key to retrieve the records from records cache
	 * @param pageNum     Page number of the records page to retrieve
	 * @return the TopsModel retrieved from cache.
	 * @throws SysException if any error while retrieving the records from cache
	 */
	public TopsModel getTopsModelPage(String httpCommand, int pageNum) {
		List records = null;
		TopsModel model = null;

		records = getRecordsPage(httpCommand, pageNum);
		if (records == null)
			return null;
		else {
			// return
			// ((RecordPagingSet)cachedResultSet.get(httpCommand)).getTopsModel();
			model = new TopsModel();
			for (int i = 0; i < records.size(); i++)
				model.put((TopsModel) records.get(i));
		}
		return model;
	}

	/**
	 * Get number of pages for the data.
	 * 
	 * @param httpCommand the key to retrieve the records from records cache
	 * @return number of pages as int value.
	 */
	public int getNumOfPages(String httpCommand) {
		// if (!cachedResultSet.containsKey(httpCommand))
		if (lastCommand == null || lastRecordSet == null || !lastCommand.equals(httpCommand))
			return 0;
		else
			// return
			// ((RecordPagingSet)cachedResultSet.get(httpCommand)).getNumOfPages();
			return lastRecordSet.getNumOfPages();
	}

	/**
	 * The <code>RecordPagingSet</code> class is a value object for storing
	 * pagination related parameters.
	 * 
	 * @author Administrator
	 * @version 1.0 (Aug 15, 2005)
	 */
	class RecordPagingSet implements Serializable {

		private List records = null;

		private int recPerPage;

		private int numOfRec;

		private int numOfPages;

		/**
		 * Constructor.
		 * 
		 * @param recordObj  list of record.
		 * @param recPerPage number of record per page.
		 * @param numOfPages number of pages.
		 */
		RecordPagingSet(List recordObj, int recPerPage, int numOfPages) {
			this.records = recordObj;
			this.recPerPage = recPerPage;
			this.numOfRec = recordObj.size();
			this.numOfPages = numOfPages;
		}

		/**
		 * Get all records as List.
		 * 
		 * @return all records as a List.
		 */
		List getRecords() {
			return records;
		}

		/**
		 * Get number of record per page.
		 * 
		 * @return number of record per page.
		 */
		int getRecPerPage() {
			return recPerPage;
		}

		/**
		 * Get number of records.
		 * 
		 * @return number of records as int value.
		 */
		int getNumOfRec() {
			return numOfRec;
		}

		/**
		 * Get number of pages.
		 * 
		 * @return number of pages as int value.
		 */
		int getNumOfPages() {
			return numOfPages;
		}
	}

}
