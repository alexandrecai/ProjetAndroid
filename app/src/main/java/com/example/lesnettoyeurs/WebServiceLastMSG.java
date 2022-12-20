package com.example.lesnettoyeurs;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class WebServiceLastMSG {

    public static final String TAG = "WbSrvcLastMSG";
    private String session;
    private String signature;

    public WebServiceLastMSG(String session, String signature) {
        this.session = session;
        this.signature = signature;
    }

    public ArrayList<Message> callWebService() {
        String status = "Probleme lors de l'appel au webservice";
        ArrayList<Message> aAjouter = new ArrayList<Message>();
        try {
            URL url = new URL("http://51.68.124.144/nettoyeurs_srv/last_msgs.php?session=" + this.session + "&signature=" + this.signature);
            Log.d(TAG, "url = " + url);
            URLConnection cnx = url.openConnection();
            InputStream in = cnx.getInputStream();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xml = db.parse(in);

            NodeList nl = xml.getElementsByTagName("STATUS");
            Node nodeStatus = nl.item(0);
            status = nodeStatus.getTextContent();

            Log.d(TAG, "status = " + status);

            if (!status.startsWith("OK"))
                return null;
            nl = xml.getElementsByTagName("CONTENT");
            Node nodeContent = nl.item(0);
            NodeList messagesXML = nodeContent.getChildNodes();

            for (int i = 0; i < messagesXML.getLength(); i++) {
                Node message = messagesXML.item(i);
                aAjouter.add(parseMessage(message));
            }
        } catch (
                MalformedURLException e) {
            e.printStackTrace();
        } catch (
                IOException e) {
            e.printStackTrace();
        } catch (
                ParserConfigurationException e) {
            e.printStackTrace();
        } catch (
                SAXException e) {
            e.printStackTrace();
        }
        return aAjouter;
    }


    private Message parseMessage(Node msgNode)
    {
        int id = -1;
        String auteur = null;
        String contenu = null;
        String stringDate = null;
        NodeList messageFields = msgNode.getChildNodes();
        for (int j = 0; j < messageFields.getLength(); j++) {
            Node field = messageFields.item(j);
            if (field.getNodeName().equalsIgnoreCase("ID"))
                id = Integer.parseInt(field.getTextContent());
            else if (field.getNodeName().equalsIgnoreCase("DATESENT"))
                stringDate = field.getTextContent();
            else if (field.getNodeName().equalsIgnoreCase("AUTHOR"))
                auteur = field.getTextContent();
            else if (field.getNodeName().equalsIgnoreCase("MSG"))
                contenu = field.getTextContent();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        assert stringDate != null;
        Date date = null;
        try {
            date = formatter.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        assert auteur != null;
        assert contenu != null;
        assert date != null;
        return new Message(id, auteur, contenu, date);
    }
}
