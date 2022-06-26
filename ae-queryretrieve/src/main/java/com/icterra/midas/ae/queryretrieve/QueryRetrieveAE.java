/**
 * Developed by ICterra Bilgi ve İletişim Teknolojileri, Inc. 2017
 */
package com.icterra.midas.ae.queryretrieve;

import com.icterra.midas.ae.queryretrieve.model.NetworkSettings;
import com.icterra.midas.ae.queryretrieve.model.QueryRetrieveFindResult;
import com.icterra.midas.ae.queryretrieve.model.ServiceStatusResult;
import com.icterra.midas.ae.queryretrieve.service.NetworkSettingsService;
import com.icterra.midas.ae.queryretrieve.util.DicomAnonymizer;
import com.icterra.midas.ae.queryretrieve.util.ServiceStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.deident.DeIdentifier;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.net.*;
import org.dcm4che3.net.pdu.PresentationContext;
import org.dcm4che3.net.service.AbstractDicomService;
import org.dcm4che3.net.service.DicomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class QueryRetrieveAE {

    @Autowired
    private NetworkSettingsService networkSettingsService;

    @Autowired
    private DicomAnonymizer dicomAnonymizer;

    private Object lockObjectOperation;
    private Object lockObjectDcm4che;
    private Object lockObjectRetrieveRSP;
    private com.icterra.midas.ae.queryretrieve.QueryRetrieveSCU queryRetrieveSCU;
    private static String outputFileDir = "";
    private volatile boolean cfindFailed = false;
    private ArrayList<QueryRetrieveFindResult> resultList;

    public final static DicomService cstoreResultHandler =
            new AbstractDicomService(UID.DigitalXRayImageStorageForPresentation, UID.DigitalXRayImageStorageForProcessing) {
                private Object lockObjectCStore = new Object();

                @Override
                protected void onDimseRQ(Association as, PresentationContext pc, Dimse dimse, Attributes cmd,
                                         Attributes data) throws IOException {

                    // TODO Auto-generated method stub
                    // synch lock object
                    synchronized (lockObjectCStore) {
                        if (dimse == Dimse.C_STORE_RQ) {
                            try {
                                log.info("C_STORE_RQ Received");
                                //onWorkReceived(data);
                                DicomOutputStream dcmo;

                                try {
                                    // TODO Create File with Unique Name and Create Folders
                                    String patientFolderDir = "D:\\DATAS\\" + data.getString(Tag.PatientID) + "\\";
                                    log.info("Patient File Dir :: " + patientFolderDir);
                                    File file = new File(patientFolderDir);
                                    if (!file.exists()) {
                                        try {
                                            log.info("Patient Folder is Creating :: " + data.getString(Tag.PatientID));
                                            file.mkdir();
                                        } catch (Exception e) {
                                            log.error("C_STORE_RQ Creating Patient Folder is failed.");
                                            log.error(e.getMessage());
                                        }
                                    }

                                    String patientID = data.getString(Tag.PatientID);
                                    String studyDate = data.getString(Tag.StudyDate);
                                    String imageLeterality = data.getString(Tag.ImageLaterality);
                                    String viewPosition = data.getString(Tag.ViewPosition);
                                    String siu = data.getString(Tag.SOPInstanceUID);



                                    DeIdentifier deIdentifier = new DeIdentifier();
                                    deIdentifier.deidentify(data);

                                    // Fill meta
                                    Attributes meta = new Attributes();
                                    String SOPClassUIDForProcessing = "1.2.840.10008.5.1.4.1.1.1.2.1";
                                    String TransferSyntaxUID = "1.2.840.10008.1.2.1";
                                    LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault());
                                    LocalDateTime now = date.now();
                                    String current = now.getYear() + "." + now.getMonthValue() + "." + now.getDayOfMonth() + "." + now.getHour() + "." + now.getMinute() + "." + now.getSecond() + "." +
                                            System.currentTimeMillis();
                                    String SOPInstanceUID = "1.2." + current;

                                    meta.setInt(Tag.FileMetaInformationVersion, VR.OB, 0, 1);
                                    meta.setString(Tag.MediaStorageSOPClassUID, VR.UI, SOPClassUIDForProcessing);
                                    meta.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, SOPInstanceUID);
                                    meta.setString(Tag.TransferSyntaxUID, VR.UI, TransferSyntaxUID);
                                    meta.setString(Tag.ImplementationClassUID, VR.UI, "1.2.276.0.7230010.3.0.3.6.0");
                                    meta.setString(Tag.ImplementationVersionName, VR.SH, "1.0.0");

                                    String patientFilePath = patientFolderDir + patientID + "_"
                                            + studyDate + "_"
                                            + imageLeterality + viewPosition + "_"
                                            + siu + ".dcm";

                                    dcmo = new DicomOutputStream(new File(patientFilePath));
                                    dcmo.writeFileMetaInformation(meta);



                                    data.writeTo(dcmo);
                                    dcmo.close();

                                    log.info("C_STORE_RQ - " + patientFilePath + " CREATED");


                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    log.error("C_STORE_RQ Image Saving Operation is failed.");
                                    log.error(e.getMessage());
                                    e.printStackTrace();
                                }

                                Attributes rsp = Commands.mkCStoreRSP(cmd, Status.Success);
                                as.tryWriteDimseRSP(pc, rsp);
                                //as.writeDimseRSP(pc, rsp, null);
                            } catch (Exception e) {

                                Attributes rsp = Commands.mkCStoreRSP(cmd, Status.UnableToProcess);
                                as.tryWriteDimseRSP(pc, rsp);
                                log.error("Association exception occurred. " + e.getMessage());
                            }
                        } else if (dimse == Dimse.C_MOVE_RQ) {
                            try {
                                Attributes rsp = Commands.mkCMoveRSP(cmd, Status.Success);
                                as.writeDimseRSP(pc, rsp, null);

                            } catch (AssociationStateException e) {
                                log.error("Association exception occurred. " + e.getMessage());
                            }
                        }
                    }
                }
            };


    public List<QueryRetrieveFindResult> query(String startDate, String patientId) {
        synchronized (lockObjectOperation) {
            List<QueryRetrieveFindResult> queryResultSet = new ArrayList<>();
            resultList.clear();
            cfindFailed = false;
            // Perform C-FIND operation
            Association association = null;

            try {

                association = queryRetrieveSCU.establishAssociation();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IncompatibleConnectionException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }

            if (association != null) {
                // DimseRSPHandler
                DimseRSPHandler rspHandler = new DimseRSPHandler(association.nextMessageID()) {
                    @Override
                    public void onDimseRSP(Association as, Attributes cmd, Attributes data) {
                        synchronized (lockObjectDcm4che) {
                            super.onDimseRSP(as, cmd, data);
                            int status = cmd.getInt(Tag.Status, -1);
                            QueryRetrieveFindResult queryRetrieveFindResult = new QueryRetrieveFindResult();
                            queryRetrieveFindResult.setServiceStatusEnum(ServiceStatusEnum.convertToServiceStatus(status));
                            // DVTK does not send failure status for CFIND.
                            if (Status.isPending(status)) {
                                log.info("QUERY RSP HANDLER - Pending");
                                queryRetrieveFindResult = onQueryResultReceived(data);
                                queryRetrieveFindResult.setServiceStatusEnum(ServiceStatusEnum.convertToServiceStatus(status));
                            } else if (status == Status.Success) {
                                log.info("QUERY RSP HANDLER - Success");
                                onQueryFinished();
                            } else {
                                // Status can be other than "Failed"
                                log.info("QUERY RSP HANDLER - Failed");
                                onQueryFailed();
                            }
                            resultList.add(queryRetrieveFindResult);
                            queryResultSet.add(queryRetrieveFindResult);
                        }
                    }
                };
                queryRetrieveSCU.query(rspHandler, startDate, patientId);
            } else {
                //callback.inform(ICallback.ACTION_QUERY, queryID, ICallback.STATUS_ERROR, 0);
            }
            queryRetrieveSCU.releaseAssociation();
            return queryResultSet;
        }
    }

    private QueryRetrieveFindResult onQueryResultReceived(Attributes data) {
        log.info("onQueryResultReceived");

        QueryRetrieveFindResult queryRetrieveFindResult = new QueryRetrieveFindResult();
        queryRetrieveFindResult.setAccessionNumber(data.getString(Tag.AccessionNumber));
        queryRetrieveFindResult.setNumberOfStudyRelatedInstances(data.getInt(Tag.NumberOfStudyRelatedInstances, 0));
        queryRetrieveFindResult.setNumberOfStudyRelatedSeries(data.getInt(Tag.NumberOfStudyRelatedSeries, 0));
        queryRetrieveFindResult.setPatientId(data.getString(Tag.PatientID));
        queryRetrieveFindResult.setStudyDate(data.getString(Tag.StudyDate));
        queryRetrieveFindResult.setStudyTime(data.getString(Tag.StudyTime));
        queryRetrieveFindResult.setStudyId(data.getString(Tag.StudyID));
        queryRetrieveFindResult.setStudyInstanceUID(data.getString(Tag.StudyInstanceUID));
        queryRetrieveFindResult.setPatientName(data.getString(Tag.PatientName));
        //queryRetrieveFindResult.setQueryId(queryID);
        log.info("Patient Id - " + queryRetrieveFindResult.getPatientId()
                + " Patient Name - " + queryRetrieveFindResult.getPatientName()
                + " Study Instance UID - " + queryRetrieveFindResult.getStudyInstanceUID());

        return queryRetrieveFindResult;
    }

    private void onQueryFailed() {
        log.info("onFailed");
        if (!cfindFailed) {
            cfindFailed = true;
        }
    }

    private void onQueryFinished() {
        log.info("onFinished");
        if (cfindFailed) {
            resultList.clear();
            log.info("cFind Failed");
        }
    }

    public void initialize() {
        outputFileDir = "E:\\DATAS\\";
        lockObjectOperation = new Object();
        lockObjectDcm4che = new Object();
        lockObjectRetrieveRSP = new Object();
        resultList = new ArrayList<>();

//        NetworkSettings scu = new NetworkSettings();
//        NetworkSettings scp = new NetworkSettings();
//
//        scu.setAeTitle("MOBILXRY_QR");
//        scu.setDeviceName("query/retrieve server");
//        scu.setHostName("127.0.0.1");
//        scu.setPort(4005);
//        scu.setServer(false);
//
//        scp.setAeTitle("DVTK_QR_SCP");
//        scp.setDeviceName("query/retrieve");
//        scp.setHostName("127.0.0.1");
//        scp.setPort(106);
//        scp.setServer(true);

//        networkSettingsService.saveNetworkSettings(scu);
//        networkSettingsService.saveNetworkSettings(scp);

        List<NetworkSettings> networkSettingsList = networkSettingsService.findAllNetworkSettings();
        NetworkSettings scu = networkSettingsList.stream().filter(
                networkSetting -> !networkSetting.isServer()).findFirst().orElse(null);
        NetworkSettings scp = networkSettingsList.stream().filter(
                networkSetting -> networkSetting.isServer()).findFirst().orElse(null);

        queryRetrieveSCU = new com.icterra.midas.ae.queryretrieve.QueryRetrieveSCU(scu, scp);

        String startDate = "20211011";//"20181107";
        String endDate = "";
        String patientId = "HN1234";//""HN989";
        String accessionNumber = "00000335";//"00000334";

        //query(startDate, endDate, accessionNumber, patientId);
    }

    public List<ServiceStatusResult> retrieve(List<QueryRetrieveFindResult> queryResult) {
        synchronized (lockObjectOperation) {
            List<ServiceStatusResult> serviceStatusResultList = new ArrayList<>();
            if (queryResult.size() > 0) {
                // TODO Check for any failures that can cause for missing data, and if result list have any error stop the operation.
                QueryRetrieveFindResult result = queryResult.stream().filter(queryRetrieveFindResult ->
                        queryRetrieveFindResult.getServiceStatusEnum() != ServiceStatusEnum.Pending
                                & queryRetrieveFindResult.getServiceStatusEnum() != ServiceStatusEnum.Success & queryRetrieveFindResult.getServiceStatusEnum() != ServiceStatusEnum.MissingAttribute &
                                queryRetrieveFindResult.getServiceStatusEnum() != ServiceStatusEnum.ProcessingFailure ).findFirst().orElse(null);
                if (result == null) {

                    for (QueryRetrieveFindResult queryRetrieveFindResult : queryResult) {
                        Association association = null;

                        try {
                            association = queryRetrieveSCU.establishAssociation();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IncompatibleConnectionException e) {
                            e.printStackTrace();
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }
//					// Read filter parameters from DB
                        // Perform C-FIND operation

                        if (association != null) {
                            // DimseRSPHandler
                            DimseRSPHandler rspHandler = new DimseRSPHandler(association.nextMessageID()) {
                                @Override
                                public void onDimseRSP(Association as, Attributes cmd, Attributes data) {
                                    synchronized (lockObjectRetrieveRSP) {
                                        super.onDimseRSP(as, cmd, data);
                                        int status = cmd.getInt(Tag.Status, -1);
                                        ServiceStatusResult serviceStatusResult = new ServiceStatusResult();
                                        serviceStatusResult.setServiceStatusEnum(ServiceStatusEnum.convertToServiceStatus(status));
                                        serviceStatusResultList.add(serviceStatusResult);

                                        if (Status.isPending(status)) {
                                            log.info("RETRIEVE RSP HANDLER - Pending");
                                            //onWorkReceived(data);
                                        } else if (status == Status.CoercionOfDataElements) {
                                            log.info("RETRIEVE RSP HANDLER - CoercionOfDataElements");

                                            //QueryRetrieveObject object = queryRetrieveDAO.getQueryRetrieveObject(dbid);

                                            //queryID = -1;
                                        } else if (status == Status.Success) {
                                            log.info("RETRIEVE RSP HANDLER - Success");

                                            //QueryRetrieveObject object = queryRetrieveDAO.getQueryRetrieveObject(dbid);

                                            //onCompletedSuccessfully();
                                            //queryID = -1;
                                        } else {
                                            // Status can be other than "Failed"
                                            log.info("RETRIEVE RSP HANDLER - Failed Status : "+status);

                                            //QueryRetrieveObject object = queryRetrieveDAO.getQueryRetrieveObject(dbid);

                                            //onCompletedSuccessfully();
                                            //queryID = -1;
                                        }
                                    }
                                }
                            };
                            //QueryRetrieveObject object = queryRetrieveDAO.getQueryRetrieveObject(dbid);
                            queryRetrieveSCU.retrieve(rspHandler, queryRetrieveFindResult);
                        }

                        queryRetrieveSCU.releaseAssociation();
                    }
                } else {
                    log.info("There are other results than Pending or Success in the List !");
                    ServiceStatusResult serviceStatusResult = new ServiceStatusResult();
                    serviceStatusResult.setServiceStatusEnum(ServiceStatusEnum.ErrorInResults);
                    serviceStatusResultList.add(serviceStatusResult);
                }
            } else {
                log.info("Result List is Empty !");
                ServiceStatusResult serviceStatusResult = new ServiceStatusResult();
                serviceStatusResult.setServiceStatusEnum(ServiceStatusEnum.NoFindQueryResults);
                serviceStatusResultList.add(serviceStatusResult);
            }
            return serviceStatusResultList;
        }
    }
}
