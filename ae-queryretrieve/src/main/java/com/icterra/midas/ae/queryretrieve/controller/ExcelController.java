package com.icterra.midas.ae.queryretrieve.controller;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ExcelController {

    private DateTimeFormatter excelFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private DateTimeFormatter dbFormat = DateTimeFormatter.ofPattern("yyyyMMdd");

    XSSFWorkbook workbook;
    XSSFSheet sheet;

    public ExcelController() {
        workbook = null;
        try {
            workbook = new XSSFWorkbook("D:\\Emre-Documents\\ODTU\\2021-2022 Spring\\Medical Informatics\\project\\ae-queryretrieve\\src\\main\\resources\\patientList.xlsx");
            sheet = workbook.getSheetAt(0);
        } catch (IOException e) {
            System.out.println("Excel file can not read.");
            e.printStackTrace();
        }
    }

    public List<String> getPatientInformation(int index) {
        if(index > sheet.getPhysicalNumberOfRows()) {
            return null;
        } else {
            XSSFRow row = sheet.getRow(index);

            String patientAccessionNumber = row.getCell(0).toString();
            if (patientAccessionNumber.contains(".")) {
                patientAccessionNumber = patientAccessionNumber.substring(0, patientAccessionNumber.indexOf("."));
            }

            String date = row.getCell(1) + "";
            date = date.substring(0, date.indexOf("E"));
            date = date.replace(".", "");

            ArrayList<String> informationList = new ArrayList<>();
            informationList.add("" + index);
            informationList.add(patientAccessionNumber);
            informationList.add(date);
            return informationList;
        }

    }
    public String excelReader(@RequestParam("file") MultipartFile excel) {

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(excel.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);

            for(int i=0; i<sheet.getPhysicalNumberOfRows();i++) {
                XSSFRow row = sheet.getRow(i);
                for(int j=0;j<row.getPhysicalNumberOfCells();j++) {
                    System.out.print(row.getCell(j) +" ");
                }
                System.out.println("");
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "Success";
    }
}
