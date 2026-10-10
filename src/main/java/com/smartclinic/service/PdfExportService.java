package com.smartclinic.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.smartclinic.entity.Prescription;
import com.smartclinic.entity.PrescriptionItem;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Service
public class PdfExportService {

    public byte[] generatePrescriptionPdf(Prescription prescription) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 40, 40);
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            Font clinicTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(15, 76, 129));
            Font clinicSubtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            Font sectionHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new Color(30, 41, 59));
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
            Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            Font tableCellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
            Font italicFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, Color.DARK_GRAY);

            // 1. Clinic Header
            Paragraph clinicName = new Paragraph(prescription.getClinic().getName(), clinicTitleFont);
            clinicName.setAlignment(Element.ALIGN_CENTER);
            document.add(clinicName);

            String clinicContact = (prescription.getClinic().getAddress() != null ? prescription.getClinic().getAddress() : "")
                    + " | Phone: " + (prescription.getClinic().getPhone() != null ? prescription.getClinic().getPhone() : "N/A")
                    + " | Email: " + (prescription.getClinic().getEmail() != null ? prescription.getClinic().getEmail() : "N/A");
            Paragraph contactPara = new Paragraph(clinicContact, clinicSubtitleFont);
            contactPara.setAlignment(Element.ALIGN_CENTER);
            contactPara.setSpacingAfter(10f);
            document.add(contactPara);

            // Separator Line
            LineSeparator separator = new LineSeparator(1.5f, 100, new Color(15, 76, 129), Element.ALIGN_CENTER, -2);
            document.add(separator);
            document.add(new Paragraph(" "));

            // 2. Doctor & Patient Info Table
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1, 1});

            // Doctor details (Left)
            PdfPCell docCell = new PdfPCell();
            docCell.setBorder(Rectangle.NO_BORDER);
            docCell.addElement(new Paragraph("DOCTOR DETAILS", sectionHeaderFont));
            docCell.addElement(new Paragraph(prescription.getDoctor().getUser().getFullName(), boldFont));
            docCell.addElement(new Paragraph(prescription.getDoctor().getSpecialization() + " - " + prescription.getDoctor().getQualification(), regularFont));
            docCell.addElement(new Paragraph("License No: " + prescription.getDoctor().getLicenseNumber(), italicFont));
            infoTable.addCell(docCell);

            // Patient details (Right)
            PdfPCell patCell = new PdfPCell();
            patCell.setBorder(Rectangle.NO_BORDER);
            patCell.addElement(new Paragraph("PATIENT DETAILS", sectionHeaderFont));
            patCell.addElement(new Paragraph(prescription.getPatient().getFullName() + " (" + prescription.getPatient().getGender() + ")", boldFont));
            
            Integer age = null;
            if (prescription.getPatient().getDateOfBirth() != null) {
                age = Period.between(prescription.getPatient().getDateOfBirth(), LocalDate.now()).getYears();
            }
            patCell.addElement(new Paragraph("ID: " + prescription.getPatient().getPatientCode() + (age != null ? " | Age: " + age + " yrs" : ""), regularFont));
            patCell.addElement(new Paragraph("Phone: " + prescription.getPatient().getPhone(), regularFont));
            infoTable.addCell(patCell);

            document.add(infoTable);
            document.add(new Paragraph(" "));

            // Prescription Meta Info Bar
            PdfPTable metaTable = new PdfPTable(3);
            metaTable.setWidthPercentage(100);
            metaTable.setWidths(new float[]{1.5f, 1.5f, 1.5f});

            PdfPCell c1 = new PdfPCell(new Phrase("Prescription #: " + prescription.getPrescriptionNumber(), boldFont));
            PdfPCell c2 = new PdfPCell(new Phrase("Date: " + prescription.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")), regularFont));
            PdfPCell c3 = new PdfPCell(new Phrase("Next Visit: " + (prescription.getNextVisitDate() != null ? prescription.getNextVisitDate().toString() : "As needed"), regularFont));

            Color metaBg = new Color(241, 245, 249);
            c1.setBackgroundColor(metaBg);
            c2.setBackgroundColor(metaBg);
            c3.setBackgroundColor(metaBg);
            c1.setPadding(6f);
            c2.setPadding(6f);
            c3.setPadding(6f);

            metaTable.addCell(c1);
            metaTable.addCell(c2);
            metaTable.addCell(c3);
            document.add(metaTable);

            // Diagnosis & Vitals
            if (prescription.getConsultation() != null) {
                document.add(new Paragraph(" "));
                Paragraph diag = new Paragraph("Diagnosis: " + prescription.getConsultation().getDiagnosis(), boldFont);
                document.add(diag);

                String vitals = "Vitals: BP: " + (prescription.getConsultation().getVitalBp() != null ? prescription.getConsultation().getVitalBp() : "N/A")
                        + " | Pulse: " + (prescription.getConsultation().getVitalPulse() != null ? prescription.getConsultation().getVitalPulse() + " bpm" : "N/A")
                        + " | SpO2: " + (prescription.getConsultation().getVitalSpo2() != null ? prescription.getConsultation().getVitalSpo2() + "%" : "N/A")
                        + " | Temp: " + (prescription.getConsultation().getVitalTemp() != null ? prescription.getConsultation().getVitalTemp() : "N/A");
                Paragraph vitalsPara = new Paragraph(vitals, italicFont);
                vitalsPara.setSpacingAfter(10f);
                document.add(vitalsPara);
            }

            // Rx Header
            Paragraph rxSymbol = new Paragraph("℞ Medications & Regimen", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(15, 76, 129)));
            rxSymbol.setSpacingAfter(8f);
            document.add(rxSymbol);

            // 3. Medications Table
            PdfPTable medTable = new PdfPTable(5);
            medTable.setWidthPercentage(100);
            medTable.setWidths(new float[]{3f, 1.5f, 2f, 1.5f, 3f});

            Color headerColor = new Color(30, 41, 59);
            String[] headers = {"Medicine Name", "Dosage", "Frequency", "Duration", "Instructions"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, tableHeaderFont));
                cell.setBackgroundColor(headerColor);
                cell.setPadding(6f);
                medTable.addCell(cell);
            }

            for (PrescriptionItem item : prescription.getItems()) {
                PdfPCell cName = new PdfPCell(new Phrase(item.getMedicineName(), boldFont));
                PdfPCell cDosage = new PdfPCell(new Phrase(item.getDosage(), tableCellFont));
                PdfPCell cFreq = new PdfPCell(new Phrase(item.getFrequency(), tableCellFont));
                PdfPCell cDur = new PdfPCell(new Phrase(item.getDuration(), tableCellFont));
                PdfPCell cInst = new PdfPCell(new Phrase(item.getInstructions() != null ? item.getInstructions() : "-", italicFont));

                cName.setPadding(5f);
                cDosage.setPadding(5f);
                cFreq.setPadding(5f);
                cDur.setPadding(5f);
                cInst.setPadding(5f);

                medTable.addCell(cName);
                medTable.addCell(cDosage);
                medTable.addCell(cFreq);
                medTable.addCell(cDur);
                medTable.addCell(cInst);
            }
            document.add(medTable);

            // 4. Advice & Instructions
            document.add(new Paragraph(" "));
            if (prescription.getGeneralAdvice() != null && !prescription.getGeneralAdvice().trim().isEmpty()) {
                Paragraph advicePara = new Paragraph("General Advice: ", boldFont);
                advicePara.add(new Phrase(prescription.getGeneralAdvice(), regularFont));
                document.add(advicePara);
            }

            if (prescription.getDietInstructions() != null && !prescription.getDietInstructions().trim().isEmpty()) {
                Paragraph dietPara = new Paragraph("Dietary Instructions: ", boldFont);
                dietPara.add(new Phrase(prescription.getDietInstructions(), regularFont));
                document.add(dietPara);
            }

            // Doctor Signature block at bottom
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            PdfPTable sigTable = new PdfPTable(2);
            sigTable.setWidthPercentage(100);
            sigTable.setWidths(new float[]{2f, 1f});

            PdfPCell emptyCell = new PdfPCell(new Phrase(""));
            emptyCell.setBorder(Rectangle.NO_BORDER);

            PdfPCell sigCell = new PdfPCell();
            sigCell.setBorder(Rectangle.NO_BORDER);
            sigCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sigCell.addElement(new Paragraph("__________________________", regularFont));
            sigCell.addElement(new Paragraph(prescription.getDoctor().getUser().getFullName(), boldFont));
            sigCell.addElement(new Paragraph("Authorized Medical Practitioner", italicFont));

            sigTable.addCell(emptyCell);
            sigTable.addCell(sigCell);
            document.add(sigTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate prescription PDF: " + e.getMessage(), e);
        }
    }
}
