// Copyright 2011 Google Inc. All Rights Reserved.

package ch.eaternity.server;

import ch.eaternity.shared.Recipe;

import com.google.appengine.api.conversion.Asset;
import com.google.appengine.api.conversion.Conversion;
import com.google.appengine.api.conversion.ConversionResult;
import com.google.appengine.api.conversion.ConversionService;
import com.google.appengine.api.conversion.ConversionServiceFactory;
import com.google.appengine.api.conversion.Document;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.util.Streams;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

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
	 private final List<Asset> assets = new ArrayList<Asset>();
	private static final long serialVersionUID = 2708668642046127395L;
private static final Logger log = Logger.getLogger(ConvertServlet.class.getName());
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse resp)
      throws ServletException, IOException, MalformedURLException { 
	  // TODO get the key, check the user, urlfetch the date, send the pdf
	  String tempIds = request.getParameter("ids");
		String permanentId = request.getParameter("pid");
		
		String outputType = "application/pdf";
		Boolean DoItAll = true;
	  
//	    UserService userService = UserServiceFactory.getUserService();
		
	    ConversionService service = ConversionServiceFactory.getConversionService();
//	    DAO dao = new DAO();
//	    
//	    User user = userService.getCurrentUser();
//	    
//	    
//		
//		
//		List<Recipe> adminRecipes = new ArrayList<Recipe>();
//		List<Recipe> rezeptePersonal = new ArrayList<Recipe>();
//		List<Recipe> kitchenRecipes = new ArrayList<Recipe>();
//		
//		if (user != null) {
//			rezeptePersonal = dao.getYourRecipe(user);
//			kitchenRecipes = dao.getKitchenRecipes(user);
//			
//			adminRecipes = dao.adminGetRecipe(user);
//			
//			// remove double entries for admin
//			if(rezeptePersonal != null){
//				for(Recipe recipe: rezeptePersonal){
//					int removeIndex = -1;
//					for(Recipe rezept2:adminRecipes){
//						if(rezept2.getId().equals(recipe.getId())){
//							removeIndex = adminRecipes.indexOf(rezept2);
//						}
//					}
//					if(removeIndex != -1){
//						adminRecipes.remove(removeIndex);
//					}
//				}
//			}
//		} else {
//			 if(tempIds != null){
//				rezeptePersonal = dao.getRecipeByIds(tempIds,true);
//			 } else {
//				 if(permanentId != null){
//					rezeptePersonal = dao.getRecipeByIds(permanentId,false);
//					DoItAll = false;
//				 } 
//			 }
//			}
//		
		String RAWURL = request.getRequestURL().toString();
		String BASEURL = RAWURL.substring(0, RAWURL.length()-7);
		
	    int assetsNumber = 1;
	    List<String> assetMimeTypeList = new ArrayList<String>();
	    List<byte[]> assetDataList = new ArrayList<byte[]>();
	    List<String> assetNameList = new ArrayList<String>();
	    
	    URL url = new URL(BASEURL + "view.jsp?ids=" + tempIds);
        InputStream stream = url.openStream();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

		try {
            
//            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
         
			assetMimeTypeList.add(0, "text/html");
            assetNameList.add(0, "MKB_zert.jsp");
            Streams.copy(stream, byteStream, true);
            // Added as the data of first asset.
            assetDataList.add(0, byteStream.toByteArray());
            
//            
//            String line;
//      	  resp.setContentType("text/plain");
//    	  
//            while ((line = reader.readLine()) != null) {
//                // ...
//            	resp.getWriter().println(line);
//            }
//            reader.close();

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
          
          if (assetMimeTypeList.get(0) == null) {
              throw new IOException("Input type must be selected.");
            }
            
 
            
            if (assetNameList.get(0) == null) {
              throw new IOException("Upload file must be selected.");
            }
            
            if (assetDataList.get(0) == null) {
              throw new IOException("Upload file can not be found.");
            }
            
		
          // Add all the assets. Primary inputs are added into first asset.
          // Additional assets may also be included, e.g. images in HTML.
          for (int i = 0; i < assetsNumber; i++) {
            assets.add(new Asset(
                assetMimeTypeList.get(i), assetDataList.get(i), assetNameList.get(i)));
          }
          
		
	    Document document = new Document(Collections.unmodifiableList(assets));
	    Conversion conversion = new Conversion(document, outputType);

	  
	   ConversionResult result = service.convert(conversion);
		
	   if (!result.success()) {
		      // Conversion failed! Print out the error code.
		      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.format(
		          "Failed to convert %s from %s to %s: %s",
		          assets.get(0).getName(), assets.get(0).getMimeType(),
		          outputType, result.getErrorCode().toString()));
		    }
		    
		    int assetsNum = result.getOutputDoc().getAssets().size();
		    if (assetsNum == 0) {
		      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No result was returned.");
		    }
		    
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
