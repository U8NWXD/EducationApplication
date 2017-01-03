/*
 * Copyright (C) 2016 U8N WXD.
 * This file is part of EducationApplication.
 *
 * EducationApplication is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EducationApplication is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EducationApplication.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.icloud.cs_temporary.EducationApplication;

import com.sun.mail.smtp.SMTPTransport;
import java.security.Security;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Object for sending email to teachers
 */
public class Mailer {
    private String username;
    private String password;
    private String recipient;

    /**
     * Constructor that initializes instance fields from parameters
     * @param inUsername Username of account (Gmail) to send emails from
     * @param inPassword Password of account (Gmail) to send emails from
     * @param inRecipient Email address to send email to
     */
    public Mailer(String inUsername, String inPassword, String inRecipient) {
        username = inUsername;
        password = inPassword;
        recipient = inRecipient;
    }

    /**
     * Send Student progress report to the teacher email address used to create object
     * @param report Text of report to send
     * @param student Student whose information is being reported
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendReport (String report, Student student) {
        try {
            String subject = "EducationApplication Progress Report for: " + student.getName();
            Send(username, password, recipient, "", subject, report);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Source: https://stackoverflow.com/questions/3649014/send-email-using-java
    // Author: Cheok Yan Cheng

    /*
    COPYRIGHT AND LICENSING NOTICE: Because this method came from StackOverflow per the attribution and citation
    information above, it has different copyright and licensing attributes than those for the rest of the file and
    program (Namely GNU GPL 3 and Copyright by U8N WXD). Those attributes are:
        Copyright (C) Cheok Yan Cheng 2010
        License: CC BY-SA 3.0 (https://creativecommons.org/licenses/by-sa/3.0/)
        The method can be found at: https://stackoverflow.com/questions/3649014/send-email-using-java
        The method has been copied verbatim, with the exception of formatting changes like adding newlines
     */
    /**
     * Send email using GMail SMTP server.
     *
     * @param username GMail username
     * @param password GMail password
     * @param recipientEmail TO recipient
     * @param ccEmail CC recipient. Can be empty if there is no CC recipient
     * @param title title of the message
     * @param message message to be sent
     * @throws AddressException if the email address parse failed
     * @throws MessagingException if the connection is dead or not in the connected state or if the message is not a
     * MimeMessage
     */
    public static void Send(final String username, final String password, String recipientEmail, String ccEmail,
                            String title, String message) throws AddressException, MessagingException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.smtps.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtps.auth", "true");

        /*
        If set to false, the QUIT command is sent and the connection is immediately closed. If set
        to true (the default), causes the transport to wait for the response to the QUIT command.

        ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
                http://forum.java.sun.com/thread.jspa?threadID=5205249
                smtpsend.java - demo program from javamail
        */
        props.put("mail.smtps.quitwait", "false");

        Session session = Session.getInstance(props, null);

        // -- Create a new message --
        final MimeMessage msg = new MimeMessage(session);

        // -- Set the FROM and TO fields --
        msg.setFrom(new InternetAddress(username + "@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));

        if (ccEmail.length() > 0) {
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
        }

        msg.setSubject(title);
        msg.setText(message, "utf-8");
        msg.setSentDate(new Date());

        SMTPTransport t = (SMTPTransport)session.getTransport("smtps");

        t.connect("smtp.gmail.com", username, password);
        t.sendMessage(msg, msg.getAllRecipients());
        t.close();
    }
}
