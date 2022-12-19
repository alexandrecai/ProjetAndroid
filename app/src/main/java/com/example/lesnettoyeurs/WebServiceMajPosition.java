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
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class WebServiceMajPosition {

    public static final String TAG = "WbSrvcMajPosition";
    private String session;
    private String signature;
    private double lon;
    private double lat;


    public WebServiceMajPosition(String session, String signature) {
        this.session = session;
        this.signature = signature;
    }

    public String callWebService(Double lon, Double lat, ArrayList<Contrat> availableContractList, ArrayList<NettoyeurEnnemi> ennemisList) {
        this.lon = lon;
        this.lat = lat;
        String status = "Probleme lors de l'appel au webservice";
        try {
            URL url = new URL("http://51.68.124.144/nettoyeurs_srv/deplace.php?session=" + this.session + "&signature=" + this.signature + "&lon=" + this.lon + "&lat=" + this.lat);
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

                availableContractList.clear();
                ennemisList.clear();

                NodeList nlPARAM = xml.getElementsByTagName("PARAMS");
                Node nodeParam = nlPARAM.item(0);

                NodeList nlDetected = nodeParam.getChildNodes();

                Node nodeCTR = nlDetected.item(0);
                NodeList nlCTR = nodeCTR.getChildNodes();
                for(int i = 0; i<nlCTR.getLength(); i++){
                    Node nodeContrat = nlCTR.item(i);
                    NodeList nlCurrentContract = nodeContrat.getChildNodes();
                    int cible_id = Integer.parseInt(nlCurrentContract.item(0).getTextContent());
                    int value = Integer.parseInt(nlCurrentContract.item(1).getTextContent());
                    Double longitude = Double.parseDouble(nlCurrentContract.item(2).getTextContent());
                    Double latitude = Double.parseDouble(nlCurrentContract.item(3).getTextContent());

                    Contrat contrat = new Contrat(cible_id,value,longitude,latitude);

                    availableContractList.add(contrat);
                }


                Node nodeNET = nlDetected.item(1);
                NodeList nlEnnemi = nodeNET.getChildNodes();
                for(int i = 0; i<nlEnnemi.getLength(); i++){
                    Node nodeEnnemi = nlEnnemi.item(i);
                    NodeList nlCurrentEnnemi = nodeEnnemi.getChildNodes();
                    int net_id = Integer.parseInt(nlCurrentEnnemi.item(0).getTextContent());
                    int value = Integer.parseInt(nlCurrentEnnemi.item(1).getTextContent());
                    Double longitude = Double.parseDouble(nlCurrentEnnemi.item(2).getTextContent());
                    Double latitude = Double.parseDouble(nlCurrentEnnemi.item(3).getTextContent());
                    int lifespan = Integer.parseInt(nlCurrentEnnemi.item(4).getTextContent());

                    NettoyeurEnnemi ennemi = new NettoyeurEnnemi(net_id,value,longitude,latitude,lifespan);

                    ennemisList.add(ennemi);
                }
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
        return status;
    }
}
