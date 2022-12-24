package com.example.lesnettoyeurs;

import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class WebServiceConnexion {

    public static final String TAG = "WebServiceConnexion";
    private String username;
    private String password;

    public WebServiceConnexion(String login, String password) {
        try {

            this.username=URLEncoder.encode(login,"UTF-8");
            this.password=password;
            Log.d(TAG,"pswd : "+this.password);
            try {
                this.password = String.format("%0" + (MessageDigest.getInstance("SHA-256").digest(this.password.getBytes(StandardCharsets.UTF_8)).length << 1) + "x",new BigInteger(1,MessageDigest.getInstance("SHA-256").digest(this.password.getBytes(StandardCharsets.UTF_8))));
                Log.d(TAG,"pswd sha256: "+this.password);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public Map<String,String> connectToWebService(){
        Map<String,String> result = new HashMap<>();
        try {
            URL url = new URL("http://51.68.124.144/nettoyeurs_srv/connexion.php?login="+username+"&passwd="+password);
            URLConnection cnx = url.openConnection();
            InputStream in = cnx.getInputStream();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xml = db.parse(in);

            NodeList nl = xml.getElementsByTagName("STATUS");
            Node nodeStatus = nl.item(0);
            String status = nodeStatus.getTextContent();

            result.put("status",status);
            Log.d(TAG, "Status : " + status);
            if (Objects.equals(status, "OK")){
                Log.d(TAG, "Session : " + xml.getElementsByTagName("SESSION").item(0).getTextContent());
                Log.d(TAG, "Signature : " + xml.getElementsByTagName("SIGNATURE").item(0).getTextContent());
                result.put("session",xml.getElementsByTagName("SESSION").item(0).getTextContent());
                result.put("signature",xml.getElementsByTagName("SIGNATURE").item(0).getTextContent());
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
        return result;
    }
}
