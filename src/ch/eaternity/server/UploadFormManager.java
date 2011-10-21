// Copyright 2011 Google Inc. All Rights Reserved.

package ch.eaternity.server;

import com.google.appengine.api.conversion.Asset;

// Make sure that the Apache Commons FileUpload JAR file has been copied
// to your war/WEB-INF/lib directory and added to your build path.
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.*;

/**
 * Class that manages the uploaded form processing.
 *
 * @author laihaiming@google.com (Haiming Lai)
 */
class UploadFormManager {
  private final List<Asset> assets = new ArrayList<Asset>();
  private final HttpServletRequest req;
  /* @Nullable */ private String outputType;

  public UploadFormManager(HttpServletRequest req) {
    this.req = req;
  }

  /**
   * Read the uploaded files and other form fields. Following this doc:
   * http://code.google.com/appengine/kb/java.html#fileforms
   * @throws FileUploadException thrown from file upload servlet.
   * @throws IOException thrown from IO operations.
   */
  void processForm() throws FileUploadException, IOException {
    int assetsNumber = 0;
    List<String> assetMimeTypeList = new ArrayList<String>();
    List<byte[]> assetDataList = new ArrayList<byte[]>();
    List<String> assetNameList = new ArrayList<String>();

    ServletFileUpload upload = new ServletFileUpload();
    FileItemIterator iterator = upload.getItemIterator(req);
    while (iterator.hasNext()) {
      FileItemStream item = iterator.next();
      InputStream stream = item.openStream();
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      String fieldName = item.getFieldName();
      try {
        if (item.isFormField()) {
          // Handle form fields
          if (fieldName.equalsIgnoreCase("input_type")) {
            // Added as the mime type of first asset.
            assetMimeTypeList.add(0, Streams.asString(stream).trim().toLowerCase());
          } else if (fieldName.equalsIgnoreCase("output_type")) {
            outputType = Streams.asString(stream).trim().toLowerCase();
          } else if (fieldName.equalsIgnoreCase("assets_number")) {
            assetsNumber = Integer.parseInt(Streams.asString(stream));
          } else if (fieldName.startsWith("asset_mime_type_")) {
            int index = Integer.parseInt(
                fieldName.substring("asset_mime_type_".length()));
            assetMimeTypeList.add(index, Streams.asString(stream));
          } else if (fieldName.startsWith("asset_name_")) {
            int index = Integer.parseInt(
                fieldName.substring("asset_name_".length()));
            assetNameList.add(index, Streams.asString(stream));
          }
        } else {
          // Handle uploaded files.
          if (fieldName.equalsIgnoreCase("upload_file")) {
            // Added as the name of first asset.
            assetNameList.add(0, item.getName());
            Streams.copy(stream, byteStream, true);
            // Added as the data of first asset.
            assetDataList.add(0, byteStream.toByteArray());
          } else if (fieldName.startsWith("asset_data_")) {
            int index = Integer.parseInt(
                fieldName.substring("asset_data_".length()));
            Streams.copy(stream, byteStream, true);
            assetDataList.add(index, byteStream.toByteArray());
          }
        }
      } catch (IOException ex) {
        throw new FileUploadException("Failed to read from the upload stream.");
      } finally {
        stream.close();
        byteStream.reset();
      }
      
    }
    
    if (assetMimeTypeList.get(0) == null) {
      throw new FileUploadException("Input type must be selected.");
    }
    
    if (outputType == null) {
      throw new FileUploadException("Output type must be selected.");
    }
    
    if (assetNameList.get(0) == null) {
      throw new FileUploadException("Upload file must be selected.");
    }
    
    if (assetDataList.get(0) == null) {
      throw new FileUploadException("Upload file can not be found.");
    }
    
    // Add all the assets. Primary inputs are added into first asset.
    // Additional assets may also be included, e.g. images in HTML.
    for (int i = 0; i < assetsNumber; i++) {
      assets.add(new Asset(
          assetMimeTypeList.get(i), assetDataList.get(i), assetNameList.get(i)));
    }
  }

  /**
   * @return The uploaded file name, or null if not present.
   */
  String getFileName() {
    return assets.get(0).getName();
  }

  /**
   * @return The input mime type, or null if not present.
   */
  String getInputType() {
    return assets.get(0).getMimeType();
  }

  /**
   * @return The output mime type, or null if not present.
   */
  String getOutputType() {
    return outputType;
  }

  /**
   * @return All assets associated with the document to be converted.
   */
  List<Asset> getAssets() {
    return Collections.unmodifiableList(assets);
  }
}
