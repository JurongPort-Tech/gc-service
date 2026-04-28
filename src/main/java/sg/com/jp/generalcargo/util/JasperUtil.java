package sg.com.jp.generalcargo.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;


@Component
public class JasperUtil {

	private static final Log log = LogFactory.getLog(JasperUtil.class);

	private static String pdfDir = "";
	private static String setJasperDir = "";
	
	@Value("${reportJasperFiles.local.path}")
	public void setJasperDir(String path) {
		setJasperDir = path;
	}

	@Value("${pdfDir.downloadfile.path}")
	public void setPdfDir(String path) {
		pdfDir = path;
	}
	
	public static JasperPrint jasperPrint(Map<String, Object> parameters, String fileName, String nbr,
			List<?> records) throws BusinessException {
		JasperPrint jasperPrint = null;
		File file = null;
		try {
			log.info("START:jasperPrint parameters:" + parameters.toString() + "fileName:" + fileName + "nbr:" + nbr);
			String filepath = setJasperDir +"/"+ fileName;
			log.info("file path : " + filepath);
			if (filepath.contains("/") == false  ||  filepath.indexOf("\\") >= 0 ) {
				log.info("filepath validation failed!");
				throw new BusinessException("filepath validation failed!");
			}
			
			Path path = Paths.get(filepath);	
			log.info("path = "+ path);
			
			if (Files.exists(path)) {
				file = ResourceUtils.getFile(filepath);
			}
						
			if (Files.notExists(path)) {
				log.error("Jasper File not Found !");
				throw new BusinessException("Jasper File not Found !");
			}
			
			String reportDirFolder = setJasperDir+"/";;
			String pdfDirFolder = pdfDir + '/';
			
			Path directoryPath = Paths.get(pdfDirFolder);	
			if (Files.notExists(directoryPath)) {
				Files.createDirectories(Paths.get(pdfDirFolder));
				log.info("Directory created !");
			}
						
			String ret = nbr + "_" + getCurrentTimeStamp("yyMMHHmmssS");
			if (parameters.containsKey("report_type")) {
				parameters.put("trans_ref", nbr);
				if (parameters.get("report_type").toString().length() > 0) {
					String barcode = pdfDirFolder + ret + ".png";
					log.info("barcode = " + barcode);
					generateCode39Writer(barcode, nbr);
					parameters.put("barcodeImage", barcode);
				}
			}
			if (parameters.containsKey("sub_report_dir")) {
				parameters.put("sub_report_dir", reportDirFolder);
			}
			// Pass Data through JRBeanCollectionDataSource to report
			if (records != null && records.size() != 0) {
				JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(records);
				JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
			} else {
				JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
			}

		} catch (BusinessException e) {
			log.error("Exception jasperPrint : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.error("Exception jasperPrint : ", e);
		} finally {
			log.info("END: jasperPrint ");
		}
		return jasperPrint;

	}

	public static String getCurrentTimeStamp(String format) {
		SimpleDateFormat s = new SimpleDateFormat(format);
		return s.format(Calendar.getInstance().getTime());
	}

	public static void generateCode39Writer(String barcodeText, String nbr) throws Exception {
		try {
			File outputfile = new File(barcodeText);
			BitMatrix bitMatrix = new MultiFormatWriter().encode(nbr, BarcodeFormat.CODE_128, 0, 0);
			BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
			ImageIO.write(bufferedImage, "png", outputfile);
		} catch (Exception e) {
			log.error("Exception generateCode39Writer : ", e);
		}

	}
	
}
