package sg.com.jp.generalcargo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import io.swagger.annotations.ApiOperation;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.StoreRentCrReport;
import sg.com.jp.generalcargo.service.InterTerminalStoreRentService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = InterTerminalStoreRentController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InterTerminalStoreRentController {

	public static final String ENDPOINT = "gc";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(InterTerminalStoreRentController.class);

	@Autowired
	private InterTerminalStoreRentService interTerminalStoreRentService;

	@Value("${pdfDir.downloadfile.path}")
	private String printingBeanPdf;

	// delegate.helper.gbms.reports.ops --> StoreRentCrReportHandler
	@ApiOperation(value = "/storeRentCrReport", response = String.class)
	@PostMapping(value = "/storeRentCrReport")
	public ResponseEntity<?> StoreRentCrReport(HttpServletRequest request, HttpServletResponse response)
			throws BusinessException, IOException {

		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		FileInputStream fileInputStream = null;
		try {

			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** StoreRentCrReport Start criteria :" + criteria.toString());

			String billmonth = CommonUtility.deNull(criteria.getPredicates().get("month"));
			String tsdirection = CommonUtility.deNull(criteria.getPredicates().get("tsdirection"));

			File file = null;
			List<File> pdfs = new ArrayList<File>();
			String id = "Tops";

			if (billmonth != null && billmonth.trim().length() > 0) {
				
				if (tsdirection.equals("both")) {

					file = getPdfFile(billmonth, "jptopsa", "StoreRentCrReportJP.jrxml");
					if (file != null && file.exists())
						pdfs.add(file);

					file = getPdfFile(billmonth, "psatojp", "StoreRentCrReportPJ.jrxml");
					if (file != null && file.exists())
						pdfs.add(file);

				} else {
					if (tsdirection.equals("jptopsa")) {
						file = getPdfFile(billmonth, tsdirection, "StoreRentCrReportJP.jrxml");
						if (file != null && file.exists())
							pdfs.add(file);
					}
					if (tsdirection.equals("psatojp")) {
						file = getPdfFile(billmonth, tsdirection, "StoreRentCrReportPJ.jrxml");
						if (file != null && file.exists())
							pdfs.add(file);
					}
				}
				

				try {
					String pdfDir = printingBeanPdf + '/';
					SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
					Date date = new Date();
					file = new File(pdfDir + "uat_" + dateTimeFormat.format(date) + ".pdf");
					FileOutputStream output = new FileOutputStream(file);
					concatPDFs(pdfs, output, true);
					
				} catch (BusinessException e) {
					throw new BusinessException(e.getMessage());
				} catch (Exception e) {
					throw new ServletException(e);
				}
			}
			
			fileInputStream = new FileInputStream(file);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/pdf"))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + id)
					.body((new InputStreamResource(fileInputStream)));
			
		} catch (BusinessException be) {
			log.info("Exception StoreRentCrReport : ", be);
			errorMessage = ConstantUtil.STORERENT_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception StoreRentCrReport : ", e);
			if(e.getMessage().contains("print") || e.getMessage().contains("Jasper")) {
				errorMessage = e.getMessage();			
			} else {
				errorMessage = ConstantUtil.CONTAINERISED_ERROR_CONSTANT_MAP.get("M4201");
			}
			
		} finally {
			if (errorMessage != null && errorMessage.contains("Jasper")) {
				return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/html"))
						.body(null);
			} else if (errorMessage != null && errorMessage.contains("print")) {
				return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/xml"))
						.body(null);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: StoreRentCrReport result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}


	private File getPdfFile(String billmonth, String tsdirection, String jasperName)
			throws FileNotFoundException, BusinessException {
		String fileName = "";
		try {
			log.info("START: getPdfFile "+" billmonth:"+CommonUtility.deNull(billmonth) +" tsdirection:"+ CommonUtility.deNull(tsdirection)
			+" jasperName:"+ CommonUtility.deNull(jasperName) );
			String pdfDir = printingBeanPdf + '/';

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("bill_month", billmonth);
			log.info("StoreRentCrReport serach params: " + parameters);
			// JasperPrint jasperPrint = JasperFillManager.fillReport(is, parameters, conn);

			String fileNameJasper = jasperName;
			List<StoreRentCrReport> records = interTerminalStoreRentService.getStoreRentReports(billmonth, tsdirection);
			JasperPrint jasperPrint = interTerminalStoreRentService.getJasperPrint(fileNameJasper, parameters,
					billmonth, records);

			SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
			Date date = new Date();
			fileName = FilenameUtils.normalize(pdfDir + "uat_" + dateTimeFormat.format(date) + new Random().nextInt() + ".pdf");
			JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);

			SimpleOutputStreamExporterOutput c = new SimpleOutputStreamExporterOutput(fileName);

			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(c);
			exporter.exportReport();
			FileInputStream st = null;
			
			if (!new File(fileName).exists()) {
				log.info("filepath validation failed! - " + fileName);
				throw new BusinessException("filepath validation failed!");
			}
			
			st = new FileInputStream(fileName);
			st.close();
			
		} catch (BusinessException ex) {
			log.info("Exception getPdfFile : ", ex);
			throw new BusinessException(ex.getMessage());
		} catch (Exception ex) {
			log.info("Exception getPdfFile : ", ex);
			throw new BusinessException(ex.getMessage());
		}
		return new File(fileName);
	}

	public static void concatPDFs(List<File> streamOfPDFFiles, FileOutputStream output, boolean paginate) throws BusinessException {
		log.info("START: concatPDFs "+" streamOfPDFFiles:"+streamOfPDFFiles.size() +" output:"+ output
		+" paginate:"+ paginate );
		Document document = new Document();
		FileInputStream fis = null;
		try {
			List<File> pdfs = streamOfPDFFiles;
			int totalPages = 0;

			// Create a writer for the outputstream
			PdfWriter writer = PdfWriter.getInstance(document, output);

			document.open();
			BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
			PdfContentByte cb = writer.getDirectContent(); // Holds the PDF
			// data

			PdfImportedPage page;
			int currentPageNumber = 0;
			int pageOfCurrentReaderPDF = 0;

			// Loop through the PDF files and add to the output.
			for (File pdf : pdfs) {
				
				try {
					fis = new FileInputStream(pdf);
					PdfReader pdfReader = new PdfReader(fis);
					totalPages = 0;
					pageOfCurrentReaderPDF = 0;
					currentPageNumber = 0;
					totalPages += pdfReader.getNumberOfPages();

					// Create a new page in the target for each source page.
					while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {
						document.newPage();
						pageOfCurrentReaderPDF++;
						currentPageNumber++;
						page = writer.getImportedPage(pdfReader, pageOfCurrentReaderPDF);
						cb.addTemplate(page, 0, 0);

						// Code for pagination.
						if (paginate) {
							cb.beginText();
							cb.setFontAndSize(bf, 9);
							cb.showTextAligned(PdfContentByte.ALIGN_CENTER,
									"" + currentPageNumber + " of " + totalPages, 520, 5, 0);
							cb.endText();
						}
					}
					pageOfCurrentReaderPDF = 0;
					
				} finally {
					if (fis != null)
						fis.close();

				}
			}
			output.flush();
			document.close();
			output.close();
		} catch (Exception e) {
			log.info("Exception concatPDFs : ", e);
			throw new BusinessException("M4201");
		} finally {
			if (document.isOpen())
				document.close();
			try {
				if (output != null)
					output.close();
			} catch (IOException e) {
				log.info("Exception concatPDFs : ", e);
			}
		}


	}

}
