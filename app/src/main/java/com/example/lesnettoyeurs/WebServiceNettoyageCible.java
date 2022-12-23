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

public class WebServiceNettoyageCible {

    public static final String TAG = "WbSrvcNettoyageCible";
    private String session;
    private String signature;

    public WebServiceNettoyageCible(String session, String signature) {
        this.session = session;
        this.signature = signature;
    }

    public String callWebService(String cible_id) {
        String status = "Probleme lors de l'appel au webservice";
        try {
            URL url = new URL("http://51.68.124.144/nettoyeurs_srv/frappe_cible.php?session=" + this.session + "&signature=" + this.signature + "&cible_id=" + cible_id);
            //Log.d(TAG, "url = " + url);
            URLConnection cnx = url.openConnection();
            InputStream in = cnx.getInputStream();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xml = db.parse(in);

            NodeList nl = xml.getElementsByTagName("STATUS");
            Node nodeStatus = nl.item(0);
            status = nodeStatus.getTextContent();

            Log.d(TAG, "status = " + status);

            if(status.equals("OK")) {

                NodeList nlPARAM = xml.getElementsByTagName("PARAMS");
                Node nodeParam = nlPARAM.item(0);

                NodeList nlOutcome = nodeParam.getChildNodes();

                Node nodeOutcome = nlOutcome.item(0);
                Node nodeDeteced = nlOutcome.item(1);


                if(nodeOutcome.getTextContent().equals("1") && nodeDeteced.getTextContent().equals("1")){
                    return "Succès mais vous avez été détecté (La cible va bientot disparaître)";
                }
                else if(nodeOutcome.getTextContent().equals("1") && nodeDeteced.getTextContent().equals("0")){
                    return "Succès (La cible va bientot disparaître)";
                }
                else if(nodeOutcome.getTextContent().equals("0") && nodeDeteced.getTextContent().equals("1")){
                    return "Raté et vous avez été détecté";
                }
                else {
                    return "Raté mais vous n'avez pas été détecté";
                }
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
        return status;
    }
}
