package ch.eaternity.server;


import gwtupload.server.exceptions.UploadActionException; 
import gwtupload.server.gae.AppEngineUploadAction; 
import java.io.InputStream; 
import java.util.List; 
import javax.jdo.PersistenceManager; 
import javax.jdo.Transaction; 
import javax.servlet.http.HttpServletRequest; 
import org.apache.commons.fileupload.FileItem; 
import org.apache.commons.io.IOUtils; 
import com.google.appengine.api.datastore.Blob; 


public class UploadAction extends AppEngineUploadAction { 
        /**
	 * 
	 */
	private static final long serialVersionUID = 2494954341562694754L;

		@Override 
        public String executeAction(HttpServletRequest request, 
List<FileItem> sessionFiles) throws UploadActionException { 
                
                for(FileItem imgItem : sessionFiles) { 
                        PersistenceManager pm = DataServiceImpl.getPersistenceManager(); 
                        Transaction tx = pm.currentTransaction(); 
                        try { 
                            // Start the transaction 
                            tx.begin(); 
                            InputStream imgStream = imgItem.getInputStream(); 
                                Blob blob = new Blob(IOUtils.toByteArray(imgStream)); 
                                ImageBlob imageBlob = new ImageBlob(imgItem.getName(), blob); 
                            pm.makePersistent(imageBlob); 
                            // Commit the transaction, flushing the object to the datastore 
                            tx.commit(); 
                        } 
                        catch(Exception e) { 
                                e.printStackTrace(); 
                        } 
                        finally { 
                            if(tx.isActive()) { 
                                tx.rollback(); 
                            } 
                            pm.close(); 
                        } 
                } 
//                removeSessionFileItems(request);
                String out = super.executeAction(request, sessionFiles); 
            return out; 
        } 
} 