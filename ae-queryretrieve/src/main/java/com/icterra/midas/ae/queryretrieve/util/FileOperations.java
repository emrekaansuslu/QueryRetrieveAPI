package com.icterra.midas.ae.queryretrieve.util;

import org.springframework.stereotype.Component;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FileOperations {
    private String destinationPath = "D:\\DATAS";

    public int getDownloadImageCount(String patientId, String date) {
        int downloadImageCount = 0;

        String patientFolderPath = destinationPath+"\\"+patientId;
        Pattern pattern = Pattern.compile(patientId+"_"+date);

        File patientFolder = new File(patientFolderPath);
        File[] listOfFiles = patientFolder.listFiles();

        if(patientFolder.exists()) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    if(fileName.contains(patientId) && fileName.contains(date))
                        downloadImageCount += 1;

                }
            }
        }
        return downloadImageCount;
    }

    public synchronized String getExcelIndex() {
        String index = "";
        try {
            File tempFile = new File("D:\\Emre-Documents\\ODTU\\2021-2022 Spring\\Medical Informatics\\project\\ae-queryretrieve\\src\\main\\resources\\index.txt");
            BufferedReader bf = new BufferedReader(new FileReader(tempFile));
            index = bf.readLine();
            bf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  index;
    }

    public synchronized void setExcelIndex(String index) {
        try {
            FileWriter writer = null;
            writer = new FileWriter("D:\\Emre-Documents\\ODTU\\2021-2022 Spring\\Medical Informatics\\project\\ae-queryretrieve\\src\\main\\resources\\index.txt");
            writer.write(index);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
