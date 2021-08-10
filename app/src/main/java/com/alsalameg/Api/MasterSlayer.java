package com.alsalameg.Api;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.alsalameg.Constants.ImageURl;
import static com.alsalameg.Constants.NAMESPACE;
import static com.alsalameg.Constants.SOAP_ACTION;
import static com.alsalameg.Constants.URL;
import static com.alsalameg.Utils.substringAfter;

public class MasterSlayer {

    private String NoImg = "no-image.jpg";


    public String FunctionName = "";

    //send param
    private ArrayList<String> ParamSendName = new ArrayList<String>();
    private ArrayList<String> ParamSendValue = new ArrayList<String>();

    //request param
    private ArrayList<String> request_paramName = new ArrayList<String>();


    public MasterSlayer(String functionName) {
        FunctionName = functionName;
    }



    public void addsendparam(ArrayList<String> param, ArrayList<String> value) {
        if (param.size() != value.size()) {
            return;
        } else {
            ParamSendName.addAll(param);

            ParamSendValue.addAll(value);
        }
    }

    public void setRequest_paramName(ArrayList<String> request_paramName) {
        this.request_paramName = request_paramName;
    }

    public ArrayList<HashMap<String, String>> Call() {
        // Create request
        SoapObject request = new SoapObject(NAMESPACE, FunctionName);
        /**
         ParamSendName = ["x","y","z"]
         ParamSendValue = ["xvalue","yvalue","zvalue"]
         */
        for (int i = 0; i < ParamSendName.size(); i++) {
            //request.addProperty(getPI("x","xvalue"), String.class));
            request.addProperty(getPI(ParamSendName.get(i), ParamSendValue.get(i), String.class));
        }
//		request.addProperty(getPI("UserName",UserName, String.class));
        /** end send*/
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        ArrayList<HashMap<String, String>> ArrayListHash = null;
        try {
            // Invole web service
            androidHttpTransport.call(SOAP_ACTION + FunctionName, envelope);
            // Get the response

            SoapObject resultsString = (SoapObject) envelope.getResponse();
            //String response = resultsString.getProperty(0).toString();
            SoapObject diffgram = (SoapObject) resultsString.getProperty(1);
            SoapObject DocumentElement = (SoapObject) diffgram.getProperty(0);

            ArrayListHash = new ArrayList<>();
/**
 ==============================
 * */
            for (int i = 0; i < DocumentElement.getPropertyCount(); i++) {
                SoapObject Objecttable = (SoapObject) DocumentElement.getProperty(i);
                HashMap<String, String> map = new HashMap<String, String>();
                for (int name_num = 0; name_num < request_paramName.size(); name_num++) {

                    if (Objecttable.hasProperty(request_paramName.get(name_num))) {

                        /**map.put("ID", Objecttable.getProperty("ID").toString());*/
                        if (IsImage(request_paramName.get(name_num))) { /** Is Image );*/

                            if (Objecttable.getProperty(request_paramName.get(name_num)).toString().equals("anyType{}")) {
                                map.put(request_paramName.get(name_num), ImageURl + NoImg);
                            } else {
                                map.put(request_paramName.get(name_num), ImageURl + substringAfter(Objecttable.getProperty(request_paramName.get(name_num)).toString(), "/prdPic/"));
                            }
                        } else {
                            if (Objecttable.getProperty(request_paramName.get(name_num)).toString().equals("anyType{}")) {
                                map.put(request_paramName.get(name_num), "");
                            } else {
                                map.put(request_paramName.get(name_num), Objecttable.getProperty(request_paramName.get(name_num)).toString());
                            }
                        }

                    } else {
                        map.put(request_paramName.get(name_num), "");
                    }
                }
//				if(Objecttable.hasProperty("Code")) {
//					map.put("Code", Objecttable.getProperty("Code").toString());
//				}else{
//					map.put("Code", "");
//				}
                ArrayListHash.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("oioi", e.getMessage());
        }
        return ArrayListHash;
    }

    public ArrayList<HashMap<String, String>> Call_result() {
        // Create request
        SoapObject request = new SoapObject(NAMESPACE, FunctionName);
        /**
         ParamSendName = ["x","y","z"]
         ParamSendValue = ["xvalue","yvalue","zvalue"]
         */
        for (int i = 0; i < ParamSendName.size(); i++) {
            //request.addProperty(getPI("x","xvalue"), String.class));
            request.addProperty(getPI(ParamSendName.get(i), ParamSendValue.get(i), String.class));
        }
//		request.addProperty(getPI("UserName",UserName, String.class));
        /** end send*/
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        ArrayList<HashMap<String, String>> ArrayListHash = null;
        try {
            // Invole web service
            androidHttpTransport.call(SOAP_ACTION + FunctionName, envelope);
            // Get the response

            SoapPrimitive resultsString = (SoapPrimitive) envelope.getResponse();

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("result", String.valueOf(resultsString));

            ArrayListHash = new ArrayList<>();

            ArrayListHash.add(map);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("", "Call: ", e);
        }

        return ArrayListHash;
    }

    private boolean IsImage(String request_Name) {
        if (IsImage1.equals(request_Name) || IsImage2.equals(request_Name)) {
            return true;
        } else {
            return false;
        }
    }


    private static PropertyInfo getPI(String name, Object value, Object Type) {
        PropertyInfo celsiusPI = new PropertyInfo();
        celsiusPI.setName(name);
        celsiusPI.setValue(value);
        celsiusPI.setType(Type);
        return celsiusPI;
    }


    private String IsImage1 = "";
    private String IsImage2 = "";

    public void setIsImage1(String isImage1) {
        IsImage1 = isImage1;
    }

    public void setIsImage2(String isImage2) {
        IsImage2 = isImage2;
    }

    public static class MarshalDouble implements Marshal {
        public Object readInstance(XmlPullParser parser, String namespace, String name,
                                   PropertyInfo expected) throws IOException, XmlPullParserException {

            return Double.parseDouble(parser.nextText());
        }


        public void register(SoapSerializationEnvelope cm) {
            cm.addMapping(cm.xsd, "double", Double.class, this);

        }


        public void writeInstance(XmlSerializer writer, Object obj) throws IOException {
            writer.text(obj.toString());
        }
    }

    public static class MarshalShort implements Marshal {
        public Object readInstance(XmlPullParser parser, String namespace, String name,
                                   PropertyInfo expected) throws IOException, XmlPullParserException {

            return Short.parseShort(parser.nextText());
        }


        public void register(SoapSerializationEnvelope cm) {
            cm.addMapping(cm.xsd, "short", Short.class, this);

        }


        public void writeInstance(XmlSerializer writer, Object obj) throws IOException {
            writer.text(obj.toString());
        }
    }

}
