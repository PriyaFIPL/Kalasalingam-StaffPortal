package webservice;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by fipl on 09-12-2016.
 */

public class WebService {

    //Namespace of the Webservice - can be found in WSDL
    private static String NAMESPACE = "http://ws.fipl.com/";
    //Webservice URL - WSDL File location
    //For Loyola server database
    //private static String URL = "http://erp.loyolacollege.edu/evarsitywebserviceLoyola/EmployeeAndroid?wsdl";

    //For Shasun live server database
    private static String URL = "https://erp.shasuncollege.edu.in/evarsitywebservice/EmployeeAndroid?wsdl";//Make sure you changed IP address
    //private static String URL = "http://192.168.0.106:8086/evarsitywebservice/EmployeeAndroid?wsdl";//Make sure you changed IP address
    //For cvs server shasun database
    //private static String URL = "http://firstlineinfotech.com/EmployeeAndroidShasun/EmployeeAndroid?wsdl";//Make sure you changed IP address

    //SOAP Action URI again Namespace + Web method name
    private static String SOAP_ACTION = "http://ws.fipl.com/";
    private static String ResultString = "";

    private static byte[] image;
    public static String METHOD_NAME = "";
    public static String strParameters[];

//    public static String invokeWS(){
//        //Object result;
//        //Initialize soap request + add parameters
//        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
//        //Use this to add parameters
//        for (int i=0; i<=strParameters.length-1; i=i+3){
//            PropertyInfo piInfo = new PropertyInfo();
//            if (strParameters[i]=="String"){
//                piInfo.setType(String.class);
//                piInfo.setName(strParameters[i + 1]);
//                piInfo.setValue(strParameters[i + 2]);
//            } else if (strParameters[i]=="int"){
//                piInfo.setType(int.class);
//                piInfo.setName(strParameters[i + 1]);
//                piInfo.setValue(strParameters[i + 2]);
//            } else if (strParameters[i]=="Long"){
//                piInfo.setType(Long.class);
//                piInfo.setName(strParameters[i + 1]);
//                piInfo.setValue(Long.parseLong(strParameters[i + 2]));
//            }
//            request.addProperty(piInfo);
//        }
//        //Declare the version of the SOAP request
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//
//
////        JSONObject json = new JSONObject(str);
////        String xml = XML.toString(json);
//
//        envelope.setOutputSoapObject(request);
//        envelope.dotNet = false;
//        EncryptDecrypt ED = new EncryptDecrypt();
//        try {
//            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,100000); //,100000
//            System.setProperty("http.keepAlive", "false");
//            //this is the actual part that will call the webservice
//            //androidHttpTransport.setXmlVersionTag("?xml version=\"1.0\" encoding=\"utf-8\"?>");
//            System.out.println("Method Name:"+METHOD_NAME);
//            androidHttpTransport.call("\""+SOAP_ACTION+METHOD_NAME+"\"", envelope);
//            if (envelope.bodyIn instanceof SoapFault) {
//                //this is the actual part that will call the webservice
//                String str= ((SoapFault) envelope.bodyIn).faultstring;
//                Log.i("", str);
//            }else {
//                // Get the SoapResult from the envelope body.
//                SoapObject result = (SoapObject) envelope.bodyIn;
//                ResultString = result.getProperty(0).toString();
//                ResultString = ED.getDecryptedData(ResultString);
//            }
//            // Get the SoapResult from the envelope body.
//        } catch (Exception e) {
//            Log.e(TAG, "Error: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return ResultString;
//    }

    public static String invokeWS(){
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        String strBody="";
        //Log.e("TEST",strParameters.toString());
       // Log.e("TEST",METHOD_NAME);
        if(strParameters != null) {
            for (int i = 0; i <= strParameters.length - 1; i = i + 3) {
                //Log.e("TEST",strParameters[i]);

                strBody += "<" + strParameters[i + 1] + ">" + strParameters[i + 2] + "</" + strParameters[i + 1] + ">";
                //Log.e("TEST",strBody);

            }
        }
        EncryptDecrypt ED = new EncryptDecrypt();
        String strEncryptedData =  ED.getEncryptedData(strBody);
        PropertyInfo piInfo = new PropertyInfo();
            piInfo.setType(String.class);
            piInfo.setName("EncryptedData");
            piInfo.setValue(strEncryptedData);
        request.addProperty(piInfo);

        //Declare the version of the SOAP request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        envelope.dotNet = false;
        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,100000); //,100000
            System.setProperty("http.keepAlive", "false");
            androidHttpTransport.call("\""+SOAP_ACTION+METHOD_NAME+"\"", envelope);
            if (envelope.bodyIn instanceof SoapFault) {
                //this is the actual part that will call the webservice
                String str= ((SoapFault) envelope.bodyIn).faultstring;
                Log.i("", str);
            }else {
                // Get the SoapResult from the envelope body.
                SoapObject result = (SoapObject) envelope.bodyIn;
                ResultString = result.getProperty(0).toString();
                ResultString = ED.getDecryptedData(ResultString);
            }
            // Get the SoapResult from the envelope body.
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return ResultString;
    }

//    public static byte[] invokeWSforImage(){
//        //Object result;
//        //Initialize soap request + add parameters
//        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
//        //Use this to add parameters
//        for (int i=0; i<=strParameters.length-1; i=i+3){
//            PropertyInfo piInfo = new PropertyInfo();
//            if (strParameters[i]=="int"){
//                piInfo.setType(int.class);
//                piInfo.setName(strParameters[i + 1]);
//                piInfo.setValue(strParameters[i + 2]);
//            } else if (strParameters[i]=="Long"){
//                piInfo.setType(Long.class);
//                piInfo.setName(strParameters[i + 1]);
//                piInfo.setValue(Long.parseLong(strParameters[i + 2]));
//            }
//            request.addProperty(piInfo);
//        }
//        //Declare the version of the SOAP request
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        envelope.setOutputSoapObject(request);
//        envelope.dotNet = false;
//        try {
//            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,100000); //,100000
//            System.setProperty("http.keepAlive", "false");
//            //this is the actual part that will call the webservice
//            //androidHttpTransport.setXmlVersionTag("?xml version=\"1.0\" encoding=\"utf-8\"?>");
//            System.out.println("Method Name:"+METHOD_NAME);
//            androidHttpTransport.call("\""+SOAP_ACTION+METHOD_NAME+"\"", envelope);
//            if (envelope.bodyIn instanceof SoapFault) {
//                //this is the actual part that will call the webservice
//                String str= ((SoapFault) envelope.bodyIn).faultstring;
//                Log.i("", str);
//            }else {
//                // Get the SoapResult from the envelope body.
//                SoapObject result = (SoapObject) envelope.bodyIn;
//                //ResultString = result.getProperty(0).toString();
//                byte[] b = result.getProperty(0).getBytes();
////                byte[] b = result.getBytes();
//            }
//            // Get the SoapResult from the envelope body.
//        } catch (Exception e) {
//            Log.e(TAG, "Error: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return ResultString;
//    }

    public static ArrayList invokeWSArray(){
        //Object result;
        //Initialize soap request + add parameters
        ArrayList<String> arrlist = new ArrayList<String>();
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        //Use this to add parameters
        for (int i=0; i<=strParameters.length-1; i=i+3){
            PropertyInfo piInfo = new PropertyInfo();
            if (strParameters[i]=="String"){
                piInfo.setType(String.class);
                piInfo.setName(strParameters[i + 1]);
                piInfo.setValue(strParameters[i + 2]);
            }
            else{
                piInfo.setType(Long.class);
                piInfo.setName(strParameters[i + 1]);
                piInfo.setValue(Long.parseLong(strParameters[i + 2]));
            }
            request.addProperty(piInfo);
        }
        //Declare the version of the SOAP request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = false;
        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,100000); //,100000
            System.out.println("Method Name:"+METHOD_NAME);

            androidHttpTransport.call("\""+SOAP_ACTION+METHOD_NAME+"\"", envelope);
            if (envelope.bodyIn instanceof SoapFault) {
                //this is the actual part that will call the webservice
                String str= ((SoapFault) envelope.bodyIn).faultstring;
                Log.i("", str);
            }else {
                // Get the SoapResult from the envelope body.
                SoapObject result = (SoapObject) envelope.bodyIn;
                ResultString = result.getProperty(0).toString();
                java.util.StringTokenizer st = new java.util.StringTokenizer(ResultString,",");
                while (st.hasMoreTokens()){
                    arrlist.add(st.nextToken());
                }
            }
            // Get the SoapResult from the envelope body.
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return arrlist;
    }

    public static ArrayList invokeWSArrayInner(){
        //Object result;
        //Initialize soap request + add parameters
        ArrayList<String> arrlist = new ArrayList<String>();
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        //Use this to add parameters
        for (int i=0; i<=strParameters.length-1; i=i+3){
            PropertyInfo piInfo = new PropertyInfo();
            if (strParameters[i]=="String"){
                piInfo.setType(String.class);
                piInfo.setName(strParameters[i + 1]);
                piInfo.setValue(strParameters[i + 2]);
            }
            else{
                piInfo.setType(Long.class);
                piInfo.setName(strParameters[i + 1]);
                piInfo.setValue(Long.parseLong(strParameters[i + 2]));
            }
            request.addProperty(piInfo);
        }
        //Declare the version of the SOAP request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = false;
        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,100000); //,100000
            System.out.println("Method Name:"+METHOD_NAME);

            androidHttpTransport.call("\""+SOAP_ACTION+METHOD_NAME+"\"", envelope);
            if (envelope.bodyIn instanceof SoapFault) {
                //this is the actual part that will call the webservice
                String str= ((SoapFault) envelope.bodyIn).faultstring;
                Log.i("", str);
            }else {
                // Get the SoapResult from the envelope body.
                SoapObject result = (SoapObject) envelope.bodyIn;
                ResultString = result.getProperty(0).toString();
                java.util.StringTokenizer st = new java.util.StringTokenizer(ResultString,"#");
                while (st.hasMoreTokens()){
                    arrlist.add(st.nextToken());
                }
            }
            // Get the SoapResult from the envelope body.
        } catch (Exception e) {
//            System.out.println(e);
            Log.e(TAG, "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return arrlist;
    }
}

