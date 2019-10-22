package startup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Class used for sending HTTP GET and read the response from the server.
 */
public class HTTPGet
{
    public static void main(String[] args) {
        HTTPGet example = new HTTPGet("datakomm.work", 80);
        example.doExampleGet();
    }

    private String BASE_URL; // Base URL (address) of the server

    /**
     * Create an HTTP GET object
     *
     * @param host Will send request to this host: IP address or domain
     * @param port Will use this port
     */
    public HTTPGet(String host, int port) {
        BASE_URL = "http://" + host + ":" + port + "/";
    }

    /**
     * Send an HTTP GET to a specific path on the web server
     */
    public void doExampleGet() {
        // TODO: change path to something correct
        sendGet("dkrest/test/get2");
    }

    /**
     * Send HTTP GET
     *
     * @param path     Relative path in the API.
     */
    private void sendGet(String path) {
        try {
            String url = BASE_URL + path;
            URL urlObj = new URL(url);
            System.out.println("Sending HTTP GET to " + url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Server reached");
                // Response was OK, read the body (data)
                InputStream stream = con.getInputStream();
                String responseBody = convertStreamToString(stream);
                stream.close();

                JSONObject jsonObject = new JSONObject(responseBody);
                int a = 0;
                int b = 0;
                int c = 0;
                if (jsonObject.has("a")) {
                    a = jsonObject.getInt("a");
                    System.out.println("The object contains field 'a' with value "
                            + a);
                }
                if (jsonObject.has("b")) {
                    b = jsonObject.getInt("b");
                    System.out.println("The object contains field 'b' with value "
                            + b);
                }
                if (jsonObject.has("c")) {
                    c = jsonObject.getInt("c");
                    System.out.println("The object contains field 'c' with value "
                            + c);
                }

                System.out.println("Response from the server:");
                System.out.println("a: " + a + "\n"
                                 + "b: " + b + "\n"
                                 + "c: " + c + "\n");
            } else {
                String responseDescription = con.getResponseMessage();
                System.out.println("Request failed, response code: " + responseCode + " (" + responseDescription + ")");
            }
        } catch (ProtocolException e) {
            System.out.println("Protocol not supported by the server");
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Read the whole content from an InputStream, return it as a string
     * @param is Inputstream to read the body from
     * @return The whole body as a string
     */
    private String convertStreamToString(InputStream is) {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                response.append('\n');
            }
        } catch (IOException ex) {
            System.out.println("Could not read the data from HTTP response: " + ex.getMessage());
        }
        return response.toString();
    }
}
