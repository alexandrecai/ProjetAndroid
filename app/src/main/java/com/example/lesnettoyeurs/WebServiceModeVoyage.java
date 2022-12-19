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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class WebServiceModeVoyage {

    public static final String TAG = "WbSrvcModeVoyage";
    private String session;
    private String signature;

    public WebServiceModeVoyage(String session, String signature) {
        this.session = session;
        this.signature = signature;
    }

    public String callWebService(){
        String status = "Probleme lors de l'appel au webservice";
        try {
            URL url = new URL("http://51.68.124.144/nettoyeurs_srv/mode_voyage.php?session=" + this.session + "&signature=" + this.signature);
            Log.d(TAG,"url = " + url);
            URLConnection cnx = url.openConnection();
            InputStream in = cnx.getInputStream();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xml = db.parse(in);

            NodeList nl = xml.getElementsByTagName("STATUS");
            Node nodeStatus = nl.item(0);
            status = nodeStatus.getTextContent();

            Log.d(TAG,"status = " + status);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return status;
    }


}
