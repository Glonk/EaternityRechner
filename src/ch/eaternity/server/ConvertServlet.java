// Copyright 2011 Google Inc. All Rights Reserved.

package ch.eaternity.server;

import ch.eaternity.shared.Recipe;

import com.google.appengine.api.conversion.Asset;
import com.google.appengine.api.conversion.Conversion;
import com.google.appengine.api.conversion.ConversionResult;
import com.google.appengine.api.conversion.ConversionService;
import com.google.appengine.api.conversion.ConversionServiceFactory;
import com.google.appengine.api.conversion.Document;


import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.util.Streams;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 * Servlet that handles the requests to perform conversions.
 *
 * @author laihaiming@google.com (Haiming Lai)
 */
public class ConvertServlet extends HttpServlet {
	/**
	 * 
	 */
	private List<Asset> assets = new ArrayList<Asset>();
	private static final long serialVersionUID = 2708668642046127395L;
	private static final Logger log = Logger.getLogger(ConvertServlet.class.getName());

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse resp)
	throws ServletException, IOException, MalformedURLException { 
		// TODO get the key, check the user, urlfetch the date, send the pdf
		String tempIds = request.getParameter("ids");
		String permId = request.getParameter("pid");


		String outputType = "application/pdf";

		ConversionService service = ConversionServiceFactory.getConversionService();

		//		
		String RAWURL = request.getRequestURL().toString();
		String BASEURL = RAWURL.substring(0, RAWURL.length()-7);


		// this is stuped to set the number manually
		// the resources should be added anyway by parsing the html file, in my opinion

		List<String> assetMimeTypeList = new ArrayList<String>();
		List<byte[]> assetDataList = new ArrayList<byte[]>();
		List<String> assetNameList = new ArrayList<String>();
		
		URL url = null;
		String name = "";
		if(tempIds != null){
			url = new URL(BASEURL + "pdf.jsp?ids=" + tempIds + "&pdf=1");
			name=  "Menü_Klimabilanz_Zertifikat.pdf";
			
		} else if(permId != null) {
			url = new URL(BASEURL + "view.jsp?pid=" + permId +"&pdf=1");
			name=  "Menü_Klimabilanz.pdf";
		}
		
		InputStream stream = null;
		try {
			
	        URLConnection yc = url.openConnection();
	        yc.setReadTimeout(10000);
	        yc.setConnectTimeout(10000);
	        stream = yc.getInputStream();
	        
		} catch (IOException e) {
			// just try again
			resp.setContentType("text/plain");
			resp.getWriter().println("Sorry. Das Programm musste neu geladen werden, starten Sie die Anfrage einfach ein zweites mal. Danke.");
		}
		
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

		try {
			assetMimeTypeList.add(0, "text/html");
			assetNameList.add(0,name);
			Streams.copy(stream, byteStream, true);

			// Added as the data of first asset.
			assetDataList.add(0, byteStream.toByteArray());

		} catch (MalformedURLException e) {
			// ...
			resp.setContentType("text/plain");
			resp.getWriter().println(e.toString());

		} catch (IOException e) {
			// ...
			resp.setContentType("text/plain");
			resp.getWriter().println(e.toString());

		} finally {

			stream.close();
			byteStream.reset();
		}


	
		
		// parse images, and get them from local filespace (if they are there)
/*
		// images can be inside a <img tag or in the css...
		String[] pngImagesReport = {"green.png","light-gray.png", "logo-eaternity-huge_04-11-2010.png", "gray.png", "smiley8.png", "orange.png","karotte.png"};
//		String[] pngImagesReport = {"green.png"};
		
		
		ServletContext context = getServletContext();
		
		InputStream imageStream = null;
		
		
		for (int i = 0; i < pngImagesReport.length; i++)
		{

			URL imageUrl = new URL("http://test.eaternityrechner.appspot.com/report/"+pngImagesReport[i]);

			imageStream = imageUrl.openStream();
//			imageStream = context.getResourceAsStream("/report/"+pngImagesReport[i]);

			try {
				// reading over input stream
				assetMimeTypeList.add("image/png");
				assetNameList.add(pngImagesReport[i]);
				
				Streams.copy(imageStream, byteStream, true);

				// Added as the data of first asset.
				assetDataList.add(byteStream.toByteArray());
//
			} catch (IOException e) {
				// ...
				resp.setContentType("text/plain");
				resp.getWriter().println(e.toString());

			} finally {

				imageStream.close();
				byteStream.reset();
			}


		}
		*/
			/*
		
		// find each appearence of: src="QR-xxx" in the html file
		// take this code: xxx and fetch the corresponding carts
		// load those charts into the Asset()
		byte[] htmlFile = assetDataList.get(0);
		String element = new String(htmlFile);
	
		
		String[] tokens = element.split("QR-");
		
	
		for (String codeRaw : tokens ){
			int test = codeRaw.indexOf("-CODE");
			if(test != -1){
			String code = codeRaw.substring(0, codeRaw.indexOf("-CODE"));
			URL qrCodeUrl =new URL("http://chart.apis.google.com/chart?cht=qr&chs=84x84&chl="+code+"&chld=M%7C0");
//			URL qrCodeUrl =new URL("http://chart.apis.google.com/chart?cht=qr&amp;chs=84x84&amp;chld=M|0&amp;chl="+code);
			
			// --> this works
//			URL qrCodeUrl = new URL("http://test.eaternityrechner.appspot.com/report/green.png");	
			
			
			imageStream = qrCodeUrl.openStream();
			
				try {
					
					
					
					assetMimeTypeList.add("image/png");
					assetNameList.add("QR-"+code+"-CODE");
					Streams.copy(imageStream, byteStream, true);

					// Added as the data of first asset.
					assetDataList.add(byteStream.toByteArray());
					
				} catch (MalformedURLException e) {
					// ...
					resp.setContentType("text/plain");
					resp.getWriter().println(e.toString());

				} catch (IOException e) {
					// ...
					resp.setContentType("text/plain");
					resp.getWriter().println(e.toString());

				} finally {

					imageStream.close();
					byteStream.reset();
				}
			}
		}
		
		*/



		// get the rest of the images from external sources

/*
		// font test
	
		InputStream reader2 = context.getResourceAsStream("/opensans300.woff");

		try {
			// reading over input stream
			assetMimeTypeList.add(2, "application/x-woff");
			assetNameList.add(2, "opensans300.woff");

			Streams.copy(reader2, byteStream, true);
			assetDataList.add(2, byteStream.toByteArray());

		} catch (IOException e) {
			// ...
			resp.setContentType("text/plain");
			resp.getWriter().println(e.toString());

		} finally {

			reader2.close();
			byteStream.reset();
		}
*/



		if (assetMimeTypeList.get(0) == null) {
			throw new IOException("Input type must be selected.");
		}

		if (assetNameList.get(0) == null) {
			throw new IOException("Upload file must be selected.");
		}

		if (assetDataList.get(0) == null) {
			throw new IOException("Upload file can not be found.");
		}

		int assetsNumber = assetDataList.size();
		// Add all the assets. Primary inputs are added into first asset.
		// Additional assets may also be included, e.g. images in HTML.
		assets.clear();
		for (int i = 0; i < assetsNumber; i++) {
			assets.add(new Asset(
					assetMimeTypeList.get(i), assetDataList.get(i), assetNameList.get(i)));
		}

// okay let's try this
//		Document document = new Document(Collections.unmodifiableList(assets));
		Document document = new Document(assets);
		Conversion conversion = new Conversion(document, outputType);


		ConversionResult result = service.convert(conversion);

		if (!result.success()) {
			// Conversion failed! Print out the error code.
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.format(
					"Failed to convert %s from %s to %s: %s - %s.%s.%s",
					assets.get(0).getName(), assets.get(0).getMimeType(),
					outputType, result.getErrorCode().toString(),Integer.toString(assetMimeTypeList.size()),Integer.toString(assetNameList.size()) ,Integer.toString(assetDataList.size())  ));
		} else {

			int assetsNum = result.getOutputDoc().getAssets().size();
			
			if (assetsNum == 0) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No result was returned.");
			} else {

				// Conversion succeeded! Send the converted result as an attachment.
				resp.setContentType(outputType);
				resp.setHeader(
						"Content-Disposition",
						"attachment; filename=" + assets.get(0).getName().split("\\.")[0]);
				List<Asset> assets = result.getOutputDoc().getAssets();
				// Print the first/primary asset to servlet output steam.
				resp.getOutputStream().write(assets.get(0).getData());
				// Additional assets may be returned, e.g. images in HTML.
				// Here we simply write them into log, you may want to handle them
				// in your own way.
				for (int i = 1; i < assetsNum; i++) {
					log.info(String.format(
							"Additional asset: name %s, type %s, data length %d", assets.get(i).getName(),
							assets.get(i).getMimeType(), assets.get(i).getData().length));
				}
				
			}
		}
	}


	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		UploadFormManager uploadFormManager = new UploadFormManager(req);
		try {
			uploadFormManager.processForm();
		} catch (FileUploadException ex) {
			throw new ServletException(ex);
		}

		Document document = new Document(uploadFormManager.getAssets());
		Conversion conversion = new Conversion(document, uploadFormManager.getOutputType());

		// Perform the conversion and retrieve the result.
		ConversionService service = ConversionServiceFactory.getConversionService();
		ConversionResult result = service.convert(conversion);

		if (!result.success()) {
			// Conversion failed! Print out the error code.
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.format(
					"Failed to convert %s from %s to %s: %s",
					uploadFormManager.getFileName(), uploadFormManager.getInputType(),
					uploadFormManager.getOutputType(), result.getErrorCode().toString()));
		}

		int assetsNum = result.getOutputDoc().getAssets().size();
		if (assetsNum == 0) {
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No result was returned.");
		}

		// Conversion succeeded! Send the converted result as an attachment.
		res.setContentType(uploadFormManager.getOutputType());
		res.setHeader(
				"Content-Disposition",
				"attachment; filename=" + uploadFormManager.getFileName().split("\\.")[0]);
		List<Asset> assets = result.getOutputDoc().getAssets();
		// Print the first/primary asset to servlet output steam.
		res.getOutputStream().write(assets.get(0).getData());
		// Additional assets may be returned, e.g. images in HTML.
		// Here we simply write them into log, you may want to handle them
		// in your own way.
		for (int i = 1; i < assetsNum; i++) {
			log.info(String.format(
					"Additional asset: name %s, type %s, data length %d", assets.get(i).getName(),
					assets.get(i).getMimeType(), assets.get(i).getData().length));
		}
	}
}
