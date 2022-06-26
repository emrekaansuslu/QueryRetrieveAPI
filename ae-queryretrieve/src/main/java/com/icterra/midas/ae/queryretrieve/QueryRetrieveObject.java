/**
 * Developed by ICterra Bilgi ve Iletisim Teknolojileri, Inc. 2017
 */
package com.icterra.midas.ae.queryretrieve;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class is used as model for Dicom Transmission Result Object.
 *
 * @author hande.saglam
 * @version 1.3.0 6 Nis 2018 11:36:11
 */
@JsonIgnoreProperties(value = { "dbid", "studyDate", "studyTime", "studyInstanceId", "studyId", "patientName" })
public class QueryRetrieveObject {

    private int dbid;
    private String studyDate;
    private String studyTime;
    private String accessionNumber;
    private String patientId;
    private String studyInstanceId;
    private String studyId;
    private String patientName;
    private int numberOfStudyRelatedSeries;
    private int numberOfStudyRelatedInstances;
    private int queryId;

    public QueryRetrieveObject() {
    }

    public int getDbid() {
        return dbid;
    }


    public void setDbid(int dbid) {
        this.dbid = dbid;
    }


    public String getStudyDate() {
        return studyDate;
    }


    public void setStudyDate(String studyDate) {
        this.studyDate = studyDate;
    }


    public String getStudyTime() {
        return studyTime;
    }


    public void setStudyTime(String studyTime) {
        this.studyTime = studyTime;
    }


    public String getAccessionNumber() {
        return accessionNumber;
    }


    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }


    public String getPatientId() {
        return patientId;
    }


    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }


    public String getStudyInstanceId() {
        return studyInstanceId;
    }


    public void setStudyInstanceId(String studyInstanceId) {
        this.studyInstanceId = studyInstanceId;
    }


    public String getStudyId() {
        return studyId;
    }


    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }


    public int getNumberOfStudyRelatedSeries() {
        return numberOfStudyRelatedSeries;
    }


    public void setNumberOfStudyRelatedSeries(int numberOfStudyRelatedSeries) {
        this.numberOfStudyRelatedSeries = numberOfStudyRelatedSeries;
    }


    public int getNumberOfStudyRelatedInstances() {
        return numberOfStudyRelatedInstances;
    }


    public void setNumberOfStudyRelatedInstances(int numberOfStudyRelatedInstances) {
        this.numberOfStudyRelatedInstances = numberOfStudyRelatedInstances;
    }

    public int getQueryId() {
        return queryId;
    }

    public void setQueryId(int queryId) {
        this.queryId = queryId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

}
