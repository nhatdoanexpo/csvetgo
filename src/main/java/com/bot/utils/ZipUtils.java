package com.bot.utils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.CopyUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    /**
     * Nén tất cả các tệp tin trong một thư mục vào một tệp tin ZIP.
     * @param sourceFolderPath đường dẫn tới thư mục chứa các tệp tin cần nén.
     * @param zipFilePath đường dẫn tới tệp tin ZIP sau khi đã nén.
     * @throws IOException
     */
    public static void zipDirectory(String sourceFolderPath, String zipFilePath) throws IOException {
        FileOutputStream fos = new FileOutputStream(zipFilePath);
        ZipOutputStream zos = new ZipOutputStream(fos);

        File directory = new File(sourceFolderPath);
        List<String> listNameFileZip = Arrays.asList("Affiliation Database"
                ,"Affiliation Database-journal"
                ,"Bookmarks"
                ,"Cookies"
                ,"Cookies-journal"
                ,"Favicons"
                ,"Favicons-journal"
                ,"heavy_ad_intervention_opt_out.db"
                ,"heavy_ad_intervention_opt_out.db-journal"
                ,"History"
                ,"History-journal"
                ,"LOCK"
                ,"LOG"
                ,"LOG.old"
                ,"Login Data"
                ,"Login Data For Account"
                ,"Login Data For Account-journal"
                ,"Login Data-journal"
                ,"Network Action Predictor"
                ,"Network Action Predictor-journal"
                ,"Network Persistent State"
                ,"Preferences"
                ,"PreferredApps"
                ,"README"
                ,"Reporting and NEL"
                ,"Reporting and NEL-journal"
                ,"Safe Browsing Cookies"
                ,"Safe Browsing Cookies-journal"
                ,"Secure Preferences"
                ,"Shortcuts"
                ,"Shortcuts-journal"
                ,"Top Sites"
                ,"Top Sites-journal"
                ,"Translate Ranker Model"
                ,"TransportSecurity"
                ,"Trusted Vault"
                ,"Visited Links"
                ,"Web Data"
                ,"Web Data-journal");
        
        zipSubDirectory("", directory, zos,listNameFileZip);

        zos.close();
        fos.close();
    }

    /**
     * Đệ quy nén tất cả các tệp tin trong một thư mục con vào tệp tin ZIP.
     * @param prefix đường dẫn tới thư mục cha.
     * @param directory thư mục con chứa các tệp tin cần nén.
     * @param zos đối tượng ZipOutputStream để ghi dữ liệu nén.
     * @throws IOException
     */
    private static void zipSubDirectory(String prefix, File directory, ZipOutputStream zos, List<String> listFileName) throws IOException {
        File[] files = directory.listFiles(); 
        for (File file : files) {
            if(listFileName.contains(file.getName())){
            if (file.isDirectory()) {
                zipSubDirectory(prefix + file.getName() + "/", file, zos,listFileName);
                continue;
            } 
            FileInputStream fis = new FileInputStream(file);
            ZipEntry entry = new ZipEntry(prefix + file.getName());
            zos.putNextEntry(entry); 
            CopyUtils.copy(fis,zos);
            zos.closeEntry();}
        }
    }

    /**
     * Giải nén tệp tin ZIP vào một thư mục.
     * @param zipFilePath đường dẫn tới tệp tin ZIP cần giải nén.
     * @param destinationFolderPath đường dẫn tới thư mục đích.
     * @throws IOException
     */
    public static void unzipFile(String zipFilePath, String destinationFolderPath) throws IOException {
        File destDir = new File(destinationFolderPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();

        while (entry != null) {
            String filePath = destinationFolderPath + File.separator + entry.getName();
            if (!entry.isDirectory()) { 
                extractFile(zipIn, filePath);
            } else {
                File dir = new File(filePath);
                dir.mkdir();
            }

            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }

        zipIn.close();
    } 
    /**
     * Trích xuất một tệp tin từ tệp tin ZIP.
     * @param zipIn đối tượng ZipInputStream để đọc dữ liệu nén.
     * @param filePath đường dẫn tới tệp tin đích.
     * @throws IOException
     */
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        CopyUtils.copy(zipIn,bos);
        bos.close();
    }




    public static void main(String[] args) throws IOException, ZipException {
// Nén các tệp tin trong thư mục sourceFolder vào tệp tin ZIP tại đường dẫn zipFilePath
//       ZipUtils.zipDirectory("C:\\New folder\\zalo-bot-v1\\src\\main\\resources\\zip\\nhat.doan.expo", "C:\\New folder\\zalo-bot-v1\\src\\main\\resources\\unzip\\nhat.doan.expo.zip");
      ZipUtils.zip4jDirectory("/headless/Desktop/src", "/headless/Desktop/srcbackup.zip","nhat123");

// Giải nén tệp tin ZIP tại đường dẫn zipFilePath vào thư mục destinationFolder
//     ZipUtils.unzipFile("C:\\New folder\\zalo-bot-v1\\src\\main\\resources\\unzip\\nhat.doan.expo.zip", "C:\\New folder\\zalo-bot-v1\\src\\main\\resources\\zip\\nhat1");
    // ZipUtils.unzip4jFile("/headless/?/git/vetgocsnew/file_laodai117647.zip", "./browser-profile/user_98","nhat123");
//
    }
    public static boolean copyFolder(String srcFolder, String desFolder)  {
        try { 
                // Tạo đối tượng File cho thư mục nguồn và đích
                File sourceDir = new File(srcFolder);
                File destDir = new File(desFolder);

                // Sao chép tất cả các tệp trong thư mục nguồn sang thư mục đích
                FileUtils.copyDirectory(sourceDir, destDir);
                         
            return true;
 		} catch (Exception e) {
 			 System.out.println("Exception!!!"+e);
 			// TODO: handle exception
 		}
            return false;
            
     }

    public static boolean unzip4jFile(String pathZipFile, String desFolder, String pw)  {
           try {
        	   ZipFile zipFile = new ZipFile(pathZipFile);
               if (zipFile.isEncrypted()) {
                   zipFile.setPassword(pw);
               }
               zipFile.extractAll(desFolder );
               System.out.println("Done!!!");
               return true;
		} catch (Exception e) {
			 System.out.println("Exception!!!"+e);
			// TODO: handle exception
		}
           return false;
           
    }

    public static void zip4jDirectory(String srcFolder, String outPutFile, String pw) throws IOException, ZipException {
//         outPutFile = "C:/demo/zip4jFolderExample.zip";
//          srcFolder = "C:/demo/data";
//          pw = "yourPassword";
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            if (pw.length() > 0) {
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
                parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
                parameters.setPassword(pw);
            }

            // Zip files inside a folder
            // An exception will be thrown if the output file already exists
            File outputFile = new File(outPutFile);
            if(!outputFile.getParentFile().exists()){
                outputFile.getParentFile().mkdirs();
            }
            net.lingala.zip4j.core.ZipFile zipFile = new net.lingala.zip4j.core.ZipFile(outputFile);

            File sourceFolder = new File(srcFolder);
            // false - indicates archive should not be split and 0 is split length
            zipFile.createZipFileFromFolder(sourceFolder, parameters, false, 0);

            // Zip a single file
            // An exception will be thrown if the output file already exists
//            outputFile = new File("C:/demo/zip4jFileExample.zip");
//            zipFile = new ZipFile(outputFile);
//            File sourceFile = new File("C:/demo/data/sql.log");
//            zipFile.addFile(sourceFile, parameters);
//            System.out.println("Done!!!");
        }



}
