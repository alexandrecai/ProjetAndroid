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

public class WebServiceStatsEquipe {

    public static final String TAG = "WbSrvcStatsEquipe";
    private String session;
    private String signature;

    public WebServiceStatsEquipe(String session, String signature) {
        this.session = session;
        this.signature = signature;
    }

    public StatsEquipe callWebService(){
        String status = "Probleme lors de l'appel au webservice";
        StatsEquipe statsEquipe = new StatsEquipe(0,0,0);
        try {
            URL url = new URL("http://51.68.124.144/nettoyeurs_srv/stats_equipe.php?session=" + this.session + "&signature=" + this.signature);
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

            if(status.equals("OK")){

                NodeList nlPARAM = xml.getElementsByTagName("PARAMS");
                Node nodeParam = nlPARAM.item(0);

                NodeList nlStats = nodeParam.getChildNodes();

                int value = Integer.parseInt(nlStats.item(0).getTextContent());
                int adv_value = Integer.parseInt(nlStats.item(1).getTextContent());
                int active_members = Integer.parseInt(nlStats.item(2).getTextContent());

                statsEquipe = new StatsEquipe(value,adv_value,active_members);

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return statsEquipe;
    }
}
