package com.pilog.mdm.service.impl;

import com.pilog.mdm.exception.ResourceNotFoundException;
import com.pilog.mdm.model.DalMailConfig;
import com.pilog.mdm.repository.DalMailConfigRepository;
import com.pilog.mdm.service.EmailNotificationService;
import com.pilog.mdm.service.IOtpGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements EmailNotificationService {


    private final DalMailConfigRepository mailRepo;
    private final IOtpGenerator otpGenerator;

    @Override
    public String sendEmail(String email) {
        String result = null;
        Integer OTP = (otpGenerator.generateOTP(email));

        DalMailConfig mailConfig = getMailConfig();
        Properties prop = configureMailProperties(mailConfig);
        Session session = createMailSession(mailConfig, prop);
        try {
            Message message = createEmailMessage(mailConfig, session, email, OTP);
            sendEmail(message);
            System.out.println("Email sent successfully.");
            result = "Thank you for registering! To complete the registration process, please Validate Your email address";
        } catch (MessagingException e) {
            e.printStackTrace();
            try {
                throw new MessagingException("Unable to send email");
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            }
        }
        return result;
    }

    private DalMailConfig getMailConfig() {
        return mailRepo.findByOrgnId("C1F5CFB03F2E444DAE78ECCEAD80D27D")
                .orElseThrow(() -> new ResourceNotFoundException("OrgId not found"));
    }

    private Properties configureMailProperties(DalMailConfig mailConfig) {
        Properties prop = new Properties();
        prop.setProperty("mail.smtp.host", mailConfig.getSmtpHost());
        prop.put("mail.transport.protocol", mailConfig.getTransportProtocol());
        prop.put("mail.smtp.starttls.enable", mailConfig.getSmtpStarttlsEnable());
        prop.put("mail.smtp.auth", mailConfig.getSmtpAuth());
        prop.put("mail.smtp.port", mailConfig.getSmtpPort());

        return prop;
    }

    private Session createMailSession(DalMailConfig mailConfig, Properties prop) {
        return Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailConfig.getUserName(), mailConfig.getPasword());
            }
        });

    }

    private Message createEmailMessage(DalMailConfig mailConfig, Session session, String email, Integer OTP) {
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(mailConfig.getUserName()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Pilog Email Verification");


            String htmlContent = generateHtmlContent(OTP);

            Multipart multipart = new MimeMultipart("related");
            BodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(htmlContent, "text/html; charset=utf-8");
            multipart.addBodyPart(mimeBodyPart);

                mimeBodyPart = new MimeBodyPart();
                InputStream imageStream = this.getClass().getResourceAsStream("/images/IMPULSIVE-LOGO-04.png");
                DataSource fds = new ByteArrayDataSource(IOUtils.toByteArray(imageStream), "image/png");
                mimeBodyPart.setDataHandler(new DataHandler(fds));
                mimeBodyPart.setHeader("Content-ID", "<image>");
                multipart.addBodyPart(mimeBodyPart);
                mimeBodyPart.setFileName("logo.png");
                message.setContent(multipart);
                System.out.println("Done");
        } catch (javax.mail.MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }



    private void sendEmail(Message message) throws MessagingException {
        try {
            Transport.send(message);
        } catch (javax.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateHtmlContent(Integer OTP) {
        String htmlContent = emailHeader();
        htmlContent += emailBody(OTP);
        htmlContent += emailFooter();
        return htmlContent;
    }

    private String emailHeader() {
        String htmlContent = "<html>\n" +
                "<body style=\"margin: 0; padding: 0; font-family: Arial, Helvetica, sans-serif;\">\n" +
                "    <div class=\"container\" style=\"max-width: 535px; margin: 0 auto; padding: 20px; background-color: #ffffff;\">\n" +
                "        <div class=\"logo\" style=\"padding: 20px 0;\">\n" +
                "            <img src=\"cid:image\" alt=\"Your Logo\" style=\"max-width: 20%; height: auto;\">\n" +
                "<div>\n";   //Logo end
        return htmlContent;
    }

    private String emailBody(Integer OTP) {
        String htmlContent = "    <p style=\"font-size: 36px; font-weight: bold; color: #000000; line-height: 40px;\">A secret code<br>to your favorite place</p>\n" +
                "    <div style=\"font-size: 17px; color: #000000; line-height: 22px;\">\n" +
                "        <p>Hello,</p>\n" +
                "    <div style=\"height: 5px;\"></div>\n" +
                "        <p>Thank you for choosing Intellisense Solutions, the bridge that connects political candidates and parties to parliamentary bodies, assembly constituencies, and municipal corporations." +
                "    Your  <strong >OTP </strong>for registration is: " +
                "<div style=\"text-align: center;\" ><strong style=\"font-size: 30px;\">" + OTP + "</strong></div></p>\n" +
                "<strong >Note: OTP is valid for 5 minutes only . Please do not share your OTP with anyone.</strong>" +
                "        <p>Embrace the power of informed decisions and effective governance through our specialized political consulting services. We're excited to have you onboard for facilitating the Election Management and Good Governance Process.</p>\n" +
                "    <div style=\"height: 10px;\"></div>\n" +
                "        <p>Best regards,<br>Team Intellisense Solutions</p>\n" +
                "    </div>\n" +
                "    <div style=\"height: 15px;\"></div>\n" +
                "</div>";
        return htmlContent;
    }

    private String emailFooter() {
        String htmlContent = "<table style=\"width: 100%; background-color: #f0f0f0;\">\n" +
                "    <tr>\n" +
                "    <td align=\"center\" style=\"padding:10px 0px 0px 0px\">\n" +
                "        <div style=\"display:inline-block;width:100%;max-width:100px;padding:0 0 10px 0;text-align:center\">\n" +
                "            <a href=\"\" target=\"_blank\" >\n" +
                "                <img alt=\"Download Android App\" src=\"https://ci5.googleusercontent.com/proxy/UtYlCOBitcS6-sVwupSN3yAqwIAOTmTuncQPrcl5OpPf3_hLnbrHRNPGg4onXi68aOymo5BBwoQCxq6Qsfan7vYbclnhyaWucdEBofB4IfcmREV9CGQ11EtbTkUmWJ5YF4VcsIhFyvvQaKR_oV1mp9VVtcAcNBhqhNMkfb8=s0-d-e1-ft#https://d13ir53smqqeyp.cloudfront.net/contain/newsletter/template-04-03-16/android-button-black@2x-new.png\" style=\"width:100px;outline:0\" >\n" +
                "            </a>\n" +
                "        </div>\n" +
                "        <div style=\"display:inline-block;width:100%;max-width:100px;padding:0 0 10px 0\">\n" +
                "            <a href=\"\" target=\"_blank\" >\n" +
                "                <img alt=\"Download iOS App\" src=\"https://ci4.googleusercontent.com/proxy/Akyu3QtfpQ85qHz4CfB_VTJZQDiySdlezfrjKTUVV3lC0L53FEoKtKHanRKbdkmqPoN_hJXSuYuHG6AzTIWlMcbkB6Eu05be1W_9QIK7ynm0EltjvNVHf-GWBWn23MWx6ZmURsOPl7GGT4p0OKWAn_a-HId155xLHAE=s0-d-e1-ft#https://d13ir53smqqeyp.cloudfront.net/contain/newsletter/template-04-03-16/appstore-button-black@2x.png\" style=\"width:100px;outline:0\" >\n" +
                "            </a>\n" +
                "        </div>\n" +
                "    </td>\n" +
                "        <td style=\"text-align: right; padding: 20px;\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "    <tbody>\n" +
                "    <tr>\n" +
                "        <td ><a href=\"https://www.facebook.com/intellisensesolutions\" target=\"_blank\" \">\n" +
                "            <img alt=\"\" height=\"auto\" src=\"https://ci4.googleusercontent.com/proxy/SEnZ289C3uktUAQbHZpdPDA6P_Ve78P3jFw5_r9TExr71bOwOSbDni-x8fZbgl08Sfv27rlAGJQf_Jj7LF3hp6UMn_BKY_ebNVi7Idm6mYnHPCxWS8pcc2aCG1gclwTdISuOBGBK09VxQGXXj_4LYg9VSM6kfz_dmhhaKUnfiaAwwWCMewJzQj6ttZRmzxexFMk=s0-d-e1-ft#https://tatadigital-prod.adobecqms.net/content/dam/tcp/email-template-assets/product-emailers/images/GN-MAILER-FBbtn-151221.png\" style=\"width: 30px;\"></a></td>\n" +
                "        <td width=\"30px\" ><a href=\"https://www.youtube.com/@intellisensesolutions8456\" target=\"_blank\" \">\n" +
                "            <img alt=\"\" height=\"auto\" src=\"https://ci6.googleusercontent.com/proxy/xY-hM9tReS2SnZpKDitYb63O7Icx2M5risUpCibSbp_mTbo9e9PGIggYUNpckj_Z-aJXRI66uc-ciIk7gvW4BCvrQqmMgAW3UskJ_FjcPITP8ahpPhAcf7jIax8j6k3ZPiSyefvpvQYsFOp3RidEi6G7YPmcZ5TDEWkZJBkgHyKToMHEDsO3ykErDVLmGNPL_xEpfg=s0-d-e1-ft#https://tatadigital-prod.adobecqms.net/content/dam/tcp/email-template-assets/product-emailers/images/GN-MAILER-Youbtn-151221.png\" style=\"width: 30px;\"></a></td>\n" +
                "        <td width=\"30px\"><a href=\"https://twitter.com/i/flow/login?redirect_after_login=%2FIntellisenseSo1\" target=\"_blank\" \">\n" +
                "            <img alt=\"\" height=\"auto\" src=\"https://ci6.googleusercontent.com/proxy/pCfxiImt76LEHmQAPzdYv4fCbJBb6Dblq-5pag7aSlRxHVvCYnuLaZso75Wx4UI6P5VVMmg9xAU_jvVhgkv7aQybLkjknZfU0kJMfWR8sT2ZmINGKNtj1u5GN2FGy8SfoEC16HedYICEEzAoiE57l0N1Cdqty1_J8hV3v1IxrlEmJf2Eja6TzYP26QhjmuPkJkE=s0-d-e1-ft#https://tatadigital-prod.adobecqms.net/content/dam/tcp/email-template-assets/product-emailers/images/GN-MAILER-Twbtn-151221.png\" style=\"width: 30px;\"></a></td>\n" +
                "        <td width=\"30px\"><a href=\"https://www.instagram.com/intellisensesolutions/\" target=\"_blank\" \">\n" +
                "            <img alt=\"\" height=\"auto\" src=\"https://ci3.googleusercontent.com/proxy/nH8SUxXQ4aen9bVVJGg6Iswl7FcuICyoFyAGIUh3dU2FgZ0pDH5-2vieU-VOQcg9dxw4xSGgLS2f4rfcNgLA_bs_x5N59p-F023wIU4y4tWqQIoBKmDiF0RcISxI2B6sKJ3HMIICN1oOsJRI4cCSx7SE38dhh65Ul5BDt824mSGFDxA_SJUg872TDQZZ1INaJav4w6Qq=s0-d-e1-ft#https://tatadigital-prod.adobecqms.net/content/dam/tcp/email-template-assets/product-emailers/images/GN-MAILER-Instabtn-151221.png\" style=\"width: 30px;\"></a></td>\n" +
                "        <td width=\"3%\">&nbsp;</td>\n" +
                "    </tr>\n" +
                "    </tbody>\n" +
                "</table>" +
                "        </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td style=\"display: flex; justify-content: space-between; align-items: center; padding: 20px;\">\n" +
                "        </td>\n" +
                "        <td style=\"text-align: right; padding: 20px;\">\n" +
                "            <a href=\"https://intellisensesolutions.com/contact-us.php\" style=\"text-decoration: none;\">Contact us</a> |\n" +
                "            <a href=\"https://intellisensesolutions.com/privacy-policy.php\" style=\"text-decoration: none;\">Privacy Policy</a>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "</body>\n" +
                "</html>"; // Footer
        return htmlContent;
    }



    public void sendEmailVerification(String email) {
        DalMailConfig mailConfig = getMailConfig();
        Properties prop = configureMailProperties(mailConfig);
        Session session = createMailSession(mailConfig, prop);
        try {
            Message message = createEmailVerificationMessage(mailConfig, session, email);
            sendEmail(message);
            System.out.println("Email verification sent successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
            try {
                throw new MessagingException("Unable to send email verification");
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private Message createEmailVerificationMessage(DalMailConfig mailConfig, Session session, String email) {
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(mailConfig.getUserName()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Email Verified Successfully"); // Update the subject
            String htmlContent = generateEmailVerificationContent(email); // Update the content
            Multipart multipart = new MimeMultipart("related");
            BodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(htmlContent, "text/html; charset=utf-8");
            multipart.addBodyPart(mimeBodyPart);
            try {
                mimeBodyPart = new MimeBodyPart();
                InputStream imageStream = this.getClass().getResourceAsStream("/images/IMPULSIVE-LOGO-04.png");
                DataSource fds = new ByteArrayDataSource(IOUtils.toByteArray(imageStream), "image/png");
                mimeBodyPart.setDataHandler(new DataHandler(fds));
                mimeBodyPart.setHeader("Content-ID", "<image>");
                multipart.addBodyPart(mimeBodyPart);
                mimeBodyPart.setFileName("logo.png");
                message.setContent(multipart);
                System.out.println("Done");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (javax.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    private String generateEmailVerificationContent(String email) {
        String htmlContent = emailHeader();
        htmlContent += emailVerifiedBody(email);
        htmlContent += emailFooter();
        return htmlContent;
    }

    private String emailVerifiedBody(String mail) {
        String htmlContent = "    <p style=\"font-size: 36px; font-weight: bold;text-align:center; color: #000000; line-height: 10px;\">Account Activated </p>\n" +
                "    <div style=\"font-size: 17px; color: #000000; line-height: 22px;\">\n" +
                "    <div style=\"height: 2px;\"></div>\n" +
                "<div style=\"text-align: center;\">\n" +
                "                <img  src=\"https://icons-for-free.com/iconfiles/png/512/connection+contact+email+envelope+mail+message+send-1320196165770653012.png\" style=\"border-radius:50%; max-width: 100px;\" >\n" +
                "        </div>\n" +
                "    <div style=\"height: 3px;\"></div>\n" +
                "        <p>Hello,</p>\n" +
                "    <div style=\"height: 3px;\"></div>\n" +
                "        <p>Great news! Your email "+ mail +" has been verified, and your account is now active. To get started with the Election Management and Good Governance Process, please complete your account setup. " +
                "    <div style=\"height: 7px;\"></div>\n";
        htmlContent += "<div style=\"text-align: center;\">\n" +
//                "    <a href=\"https://example.com\" style=\"text-decoration: none; \">\n" +
//                "        <button style=\"background-color: #a855f7; color: #fff;cursor:pointer; padding: 10px 20px; border: none; border-radius: 5px;  font-size: 16px;\">\n" +
//                "            LOGIN TO YOUR ACCOUNT\n" +
//                "        </button>\n" +
//                "    </a>\n" +
                "</div>" +
                "    <div style=\"height: 7px;\"></div>\n" +
                "       <p>We appreciate your commitment to Intellisense Solutions and are here to assist you every step of the way. Thank you for choosing us. We're eager to work together in enhancing the electoral and governance experience.</p>\n" +
                "       <p>If you encounter any issues or have questions during the setup process, please don't hesitate to reach out to our support team.</p>\n" +
                "    <div style=\"height: 10px;\"></div>\n" +

                "        <p>Best regards,<br>Team Intellisense Solutions</p>\n" +
                "    </div>\n" +
                "    <div style=\"height: 15px;\"></div>\n" +
                "</div>";
        return htmlContent;
    }


}