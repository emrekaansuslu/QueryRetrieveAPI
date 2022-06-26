package com.icterra.midas.ae.queryretrieve.util;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.deident.DeIdentifier;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DicomAnonymizer {

    public void anonymize(String file) {


        try {

            File sourceDicomFile = new File(file);
            File anonymousDicomFile = null;

            DicomInputStream dis = new DicomInputStream(sourceDicomFile);
            Attributes meta = dis.readFileMetaInformation();
            Attributes attributes = dis.readDataset(-1, Tag.CoefficientsSDDN);
            dis.close();

            DeIdentifier deIdentifier = new DeIdentifier();
            deIdentifier.deidentify(attributes);


            anonymousDicomFile = new File(file);
            DicomOutputStream dcmo = new DicomOutputStream(anonymousDicomFile);
            dcmo.writeFileMetaInformation(meta);
            attributes.writeTo(dcmo);
            dcmo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
