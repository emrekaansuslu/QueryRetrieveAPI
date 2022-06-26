package com.icterra.midas.ae.queryretrieve;

import com.icterra.midas.ae.queryretrieve.model.NetworkSettings;
import com.icterra.midas.ae.queryretrieve.model.QueryRetrieveFindResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.net.*;
import org.dcm4che3.net.pdu.AAssociateRQ;
import org.dcm4che3.net.pdu.PresentationContext;
import org.dcm4che3.net.service.BasicCEchoSCP;
import org.dcm4che3.net.service.DicomServiceRegistry;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QueryRetrieveSCU {

    private static final Logger LOGGER = LogManager.getLogger("QueryRetrieve");
    private static final String UNIVERSAL_MATCHING_SYMBOL = "";

    private final NetworkSettings scu;
    private final NetworkSettings scp;
    private Device device;
    private ApplicationEntity ae;
    private Connection conn;
    private Connection remote;
    private ExecutorService executorService;
    private ScheduledExecutorService scheduledExecutorService;
    private Association association;
    private AAssociateRQ rq;
    private Attributes keys = new Attributes();
    private int queryPriority;

    public static final String[] IVR_LE_FIRST = {
            UID.ImplicitVRLittleEndian,
            UID.ExplicitVRLittleEndian,
            UID.ExplicitVRBigEndianRetired
    };

    public QueryRetrieveSCU(NetworkSettings scu, NetworkSettings scp) {
        this.scu = scu;
        this.scp = scp;

        // initialize device for new connection
        rq = new AAssociateRQ();

        device = new Device(scu.getDeviceName());
        //if (connectionSettings.isTlsEnabled()) {
        //TLSConnectionHelper.getInstance().initialize(LOGGER);
        //TLSConnectionHelper.getInstance().setTLSConnection(device);
        //}
        conn = new Connection();
        device.addConnection(conn);
        ae = new ApplicationEntity(scu.getAeTitle());
        ae.setAssociationAcceptor(true);
        device.addApplicationEntity(ae);
        ae.addConnection(conn);

        remote = new Connection();
        DicomServiceRegistry serviceRegistry = new DicomServiceRegistry();
        serviceRegistry.addDicomService(new BasicCEchoSCP());
        serviceRegistry.addDicomService(QueryRetrieveAE.cstoreResultHandler);
        ae.setDimseRQHandler(serviceRegistry);

        rq.setCalledAET(scp.getAeTitle());
        remote.setHostname(this.scp.getHostName());
        remote.setPort(this.scp.getPort());
        remote.setHttpProxy(null);

        ae.setAETitle(scu.getAeTitle());
        conn.setPort(scu.getPort());

        conn.setReceivePDULength(Connection.DEF_MAX_PDU_LENGTH);
        conn.setSendPDULength(Connection.DEF_MAX_PDU_LENGTH);
        conn.setMaxOpsInvoked(0);
        conn.setMaxOpsPerformed(0);
        conn.setPackPDV(true);
        conn.setConnectTimeout(0);
        conn.setRequestTimeout(0);
        conn.setAcceptTimeout(0);
        conn.setReleaseTimeout(0);
        conn.setResponseTimeout(0);
        conn.setRetrieveTimeout(0);
        conn.setIdleTimeout(0);
        conn.setSocketCloseDelay(Connection.DEF_SOCKETDELAY);
        conn.setSendBufferSize(0);
        conn.setReceiveBufferSize(0);
        conn.setTcpNoDelay(true);
        //if (connectionSettings.isTlsEnabled()) {
        //conn.setTlsCipherSuites(Connection.TLS_RSA_WITH_NULL_SHA, Connection.TLS_RSA_WITH_3DES_EDE_CBC_SHA,
        //        Connection.TLS_RSA_WITH_AES_128_CBC_SHA);
        //conn.setTlsProtocols("TLSv1");
        //}

        remote.setTlsProtocols(conn.getTlsProtocols());
        remote.setTlsCipherSuites(conn.getTlsCipherSuites());

        rq.setCalledAET(scp.getAeTitle());
        rq.addPresentationContext(
                new PresentationContext(1, UID.PatientRootQueryRetrieveInformationModelFIND, UID.ImplicitVRLittleEndian, UID.ExplicitVRLittleEndian,
                        UID.ExplicitVRBigEndianRetired));
        rq.addPresentationContext(
                new PresentationContext(3, UID.StudyRootQueryRetrieveInformationModelMOVE, UID.ImplicitVRLittleEndian, UID.ExplicitVRLittleEndian,
                        UID.ExplicitVRBigEndianRetired));
        rq.addPresentationContext(
                new PresentationContext(5, UID.DigitalXRayImageStorageForPresentation, UID.ImplicitVRLittleEndian, UID.ExplicitVRLittleEndian,
                        UID.ExplicitVRBigEndianRetired));
        rq.addPresentationContext(
                new PresentationContext(7, UID.DigitalXRayImageStorageForProcessing, UID.ImplicitVRLittleEndian, UID.ExplicitVRLittleEndian,
                        UID.ExplicitVRBigEndianRetired));
        rq.addPresentationContext(new PresentationContext(9, UID.VerificationSOPClass, UID.ImplicitVRLittleEndian, UID.ExplicitVRLittleEndian,
                UID.ExplicitVRBigEndianRetired));

        ae.addTransferCapability(
                new TransferCapability(null,
                        UID.DigitalXRayImageStorageForPresentation,
                        TransferCapability.Role.SCP,
                        IVR_LE_FIRST));
        ae.addTransferCapability(
                new TransferCapability(null,
                        UID.DigitalXRayImageStorageForProcessing,
                        TransferCapability.Role.SCP,
                        IVR_LE_FIRST));
        ae.addTransferCapability(
                new TransferCapability(null,
                        UID.VerificationSOPClass,
                        TransferCapability.Role.SCP,
                        IVR_LE_FIRST));
        ae.addTransferCapability(
                new TransferCapability(null,
                        UID.PatientRootQueryRetrieveInformationModelFIND,
                        TransferCapability.Role.SCU,
                        IVR_LE_FIRST));
        ae.addTransferCapability(
                new TransferCapability(null,
                        UID.StudyRootQueryRetrieveInformationModelMOVE,
                        TransferCapability.Role.SCU,
                        IVR_LE_FIRST));

        executorService = Executors.newCachedThreadPool();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        device.setExecutor(executorService);
        device.setScheduledExecutor(scheduledExecutorService);

        try {
            device.bindConnections();
        } catch (IOException | GeneralSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public Association establishAssociation() throws IOException, InterruptedException, IncompatibleConnectionException, GeneralSecurityException {

        association = null;

        Association associationAsResult;
//		try {
        // Establish association between SCU and SCP
        association = ae.connect(remote, rq);
        associationAsResult = association;
//		} catch (IOException| InterruptedException| org.dcm4che3.net.IncompatibleConnectionException| java.security.GeneralSecurityException e) {
//			associationAsResult = null;
//			LOGGER.error("Error occured during establishing association between QR SCU and SCP" + e.getMessage());
//		}
        return associationAsResult;
    }

    public boolean query(DimseRSPHandler rspHandler, String startDate, String patientId) {
        boolean result = true;

        try {
            //  Configure keys and call cfind method on Association
            queryPriority = Priority.NORMAL;
            keys = new Attributes();

//            String studyDate = "";
//            if (!startDate.equals("")) {
//                studyDate += startDate;
//            }
//
//            studyDate += "-";
            //image level
            keys.setString(Tag.StudyDate, VR.DA, "20200301-20200330");
            keys.setString(Tag.PatientID, VR.LO, "12345");
            keys.setString(Tag.PatientName, VR.PN, UNIVERSAL_MATCHING_SYMBOL);
            keys.setString(Tag.StudyInstanceUID, VR.UI, "");
            keys.setString(Tag.QueryRetrieveLevel, VR.CS, "STUDY");
            keys.setString(Tag.SOPInstanceUID, VR.UI, UNIVERSAL_MATCHING_SYMBOL);
            keys.setString(Tag.AccessionNumber, VR.SH, "");
            keys.setString(Tag.StudyTime, VR.TM, UNIVERSAL_MATCHING_SYMBOL);
            keys.setString(Tag.Modality, VR.CS, UNIVERSAL_MATCHING_SYMBOL);
            keys.setString(Tag.StudyID, VR.SH, UNIVERSAL_MATCHING_SYMBOL);
            keys.setString(Tag.SeriesInstanceUID, VR.SH, "");
            keys.setString(Tag.NumberOfStudyRelatedSeries, VR.IS, UNIVERSAL_MATCHING_SYMBOL);
            keys.setString(Tag.NumberOfStudyRelatedInstances, VR.IS, UNIVERSAL_MATCHING_SYMBOL);
            keys.setString(Tag.ReferringPhysicianName, VR.PN, UNIVERSAL_MATCHING_SYMBOL);
            keys.setString(Tag.ModalitiesInStudy, VR.CS, UNIVERSAL_MATCHING_SYMBOL);
            keys.setString(Tag.PatientBirthDate, VR.DA, UNIVERSAL_MATCHING_SYMBOL);
            keys.setString(Tag.PatientSex, VR.CS, UNIVERSAL_MATCHING_SYMBOL);
            keys.setString(Tag.SeriesNumber, VR.IS, UNIVERSAL_MATCHING_SYMBOL);
            keys.setString(Tag.InstanceNumber, VR.IS, UNIVERSAL_MATCHING_SYMBOL);
            keys.setString(Tag.Rows, VR.US, UNIVERSAL_MATCHING_SYMBOL);
            keys.setString(Tag.Columns, VR.US, UNIVERSAL_MATCHING_SYMBOL);

            association.cfind(UID.PatientRootQueryRetrieveInformationModelFIND, queryPriority, keys, null, rspHandler);
        } catch (IOException | InterruptedException e) {
            result = false;
            LOGGER.error("Error occured during cfind request from QR SCU to SCP" + e.getMessage());
        }
        return result;
    }

    public boolean retrieve(DimseRSPHandler rspHandler, QueryRetrieveFindResult queryRetrieveFindResult) {
        boolean result = true;

        try {
            //  Configure keys and call cfind method on Association
            queryPriority = Priority.NORMAL;
            keys = new Attributes();

            //image level
            keys.setString(Tag.QueryRetrieveLevel, VR.CS, "STUDY");
            keys.setString(Tag.PatientID, VR.LO, queryRetrieveFindResult.getPatientId());
            keys.setString(Tag.AccessionNumber, VR.SH, queryRetrieveFindResult.getAccessionNumber());
            keys.setString(Tag.StudyInstanceUID, VR.UI, queryRetrieveFindResult.getStudyInstanceUID());
            keys.setString(Tag.SeriesInstanceUID, VR.UI, "");
            //keys.setString(Tag.SOPInstanceUID, VR.UI, "1.2.840.113619.2.308.4.2147483647.1523258799.548942");

            association.cmove(UID.StudyRootQueryRetrieveInformationModelMOVE, queryPriority, keys, null, scu.getAeTitle(), rspHandler);
        } catch (IOException | InterruptedException e) {
            result = false;
            LOGGER.error("Error occured during cfind request from QR SCU to SCP" + e.getMessage());
        }
        return result;
    }

    public boolean releaseAssociation() {
        boolean result = true;
        // Release association between SCU and SCP
        try {
            if (association != null) {
                if (association.isReadyForDataTransfer()) {
                    association.waitForOutstandingRSP();
                    association.release();
                }
                association.waitForSocketClose();
            }

            //			if (conn.isListening()) {
            //				device.waitForNoOpenConnections();
            //				device.unbindConnections();
            //			}

            //			ae.removeConnection(conn);
            //			device.removeConnection(conn);
        } catch (IOException | InterruptedException e) {
            result = false;
            LOGGER.error("Error occured during releasing association between QR SCU and SCP" + e.getMessage());
        }
        return result;
    }

    public void stopQRSCU() {
        // Shutdown Executors for SCU
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException exc) {
            LOGGER.error(exc.getMessage());
            executorService.shutdownNow();
        }
        scheduledExecutorService.shutdown();
        try {
            if (!scheduledExecutorService.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                scheduledExecutorService.shutdownNow();
            }
        } catch (InterruptedException exc) {
            LOGGER.error(exc.getMessage());
            scheduledExecutorService.shutdownNow();
        }
    }

}
