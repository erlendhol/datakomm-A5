package MainAssignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.lang.Math;

public class WebServiceClient
{
    public static void main(String[] args)
    {
        WebServiceClient client = new WebServiceClient("datakomm.work", 80);
        client.authorize("erlehols@stud.ntnu.no", "90154117");
        client.getTask(1);
        client.solveTask1();
        client.getTask(2);
        client.solveTask2();
        client.getTask(3);
        client.solveTask3();
        client.getTask(4);
        client.solveTask4();
        client.getTask((int)Math.sqrt(4064256));
        client.solveHiddenTask();
    }

    private String BASE_URL; // Base URL (address) of the server
    private int sessionID;
    private String answerTask2;
    private int answerTask3 = 1;
    private int answerTask4;
    private String answerHiddenTask;

    /**
     * Create a WebServiceClient object
     *
     * @param host Will send requests to this host: IP address or domain
     * @param port Will use this port
     */
    public WebServiceClient(String host, int port)
    {
        BASE_URL = "http://" + host + ":" + port + "/";
    }

    private void authorize(String email, String phoneNmbr)
    {
        try
        {
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("phone", phoneNmbr);

            String url = BASE_URL + "dkrest/auth";
            URL urlObj = new URL(url);
            System.out.println("Sending HTTP POST to " + url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            os.write(json.toString().getBytes());
            os.flush();

            int responseCode = con.getResponseCode();
            if (responseCode == 200)
            {
                System.out.println("Server reached");

                //Response was OK, read the body (data)
                InputStream stream = con.getInputStream();
                String responseBody = convertStreamToString(stream);
                stream.close();
                System.out.println("Response from the server:");
                JSONObject jsonObject = new JSONObject(responseBody);
                if (jsonObject.has("sessionId"))
                {
                    sessionID = jsonObject.getInt("sessionId");
                }
                System.out.println(responseBody);
            }
            else
            {
                String responseDescription = con.getResponseMessage();
                System.out.println("Request failed, response code: " + responseCode + " (" + responseDescription + ")");
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        } catch (ProtocolException e)
        {
            e.printStackTrace();
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void getTask(int taskNumber)
    {
        try
        {
            String taskRequest = "dkrest/gettask/" + taskNumber + "?sessionId=" + sessionID;
            JSONObject json = sendGet(taskRequest);
            if (json.has("taskNr"))
            {
                if (json.getInt("taskNr") == 2)
                {
                    JSONArray arguments = json.getJSONArray("arguments");
                    answerTask2 = arguments.getString(0);
                }
                if (json.getInt("taskNr") == 3)
                {
                    JSONArray arguments = json.getJSONArray("arguments");
                    for (int i = 0; i < arguments.length(); i++)
                    {
                        answerTask3 = answerTask3 * Integer.parseInt(arguments.getString(i));
                    }
                }
                if (json.getInt("taskNr") == 4)
                {
                    JSONArray arguments = json.getJSONArray("arguments");
                    String md5Hash = arguments.getString(0);
                    for (int i = 0000; i < 9999; i ++)
                    {
                        String password = "" + i;
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        byte[] hashInBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

                        StringBuilder sb = new StringBuilder();
                        for (byte b : hashInBytes)
                        {
                            sb.append(String.format("%02x", b));
                        }
                        if (sb.toString().equals(md5Hash))
                        {
                            answerTask4 = i;
                            break;
                        }
                    }
                }
                if (json.getInt("taskNr") == Math.sqrt(4064256))
                {
                    JSONArray arguments = json.getJSONArray("arguments");
                    String ipAddress = arguments.getString(0);
                    String subMask = arguments.getString(1);
                    String[] ipParts = ipAddress.split(".", 4);
                    String[] subMaskParts = subMask.split(".", 4);

                }
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    private void solveTask1()
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sessionId", sessionID);
            jsonObject.put("msg", "Hello");
            sendPost("dkrest/solve", jsonObject);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void solveTask2()
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sessionId", sessionID);
            jsonObject.put("msg", answerTask2);
            sendPost("dkrest/solve", jsonObject);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void solveTask3()
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sessionId", sessionID);
            jsonObject.put("result", answerTask3);
            sendPost("dkrest/solve", jsonObject);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void solveTask4()
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sessionId", sessionID);
            jsonObject.put("pin", answerTask4);
            sendPost("dkrest/solve", jsonObject);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void solveHiddenTask()
    {
        try
        {
            answerHiddenTask = "192.244.53.4";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sessionId", sessionID);
            jsonObject.put("ip", answerHiddenTask);
            sendPost("dkrest/solve", jsonObject);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Send HTTP GET
     *
     * @param path     Relative path in the API.
     */
    private JSONObject sendGet(String path) {
        JSONObject jsonObject = null;
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

                System.out.println(responseBody);
                jsonObject = new JSONObject(responseBody);
            } else {
                String responseDescription = con.getResponseMessage();
                System.out.println("Request failed, response code: " + responseCode + " (" + responseDescription + ")");
                jsonObject = new JSONObject(responseDescription);
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
        return jsonObject;
    }

    private void sendPost(String path, JSONObject jsonData)
    {
        try
        {
            String url = BASE_URL + path;
            URL urlObj = new URL(url);
            System.out.println("Sending HTTP POST to " + url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            os.write(jsonData.toString().getBytes());
            os.flush();

            System.out.println("Sent " + jsonData.toString() + " to the server.");

            int responseCode = con.getResponseCode();
            if (responseCode == 200)
            {
                System.out.println("Server reached");

                //Response was OK, read the body (data)
                InputStream stream = con.getInputStream();
                String responseBody = convertStreamToString(stream);
                stream.close();
                System.out.println("Response from the server:");
                //JSONObject jsonObject = new JSONObject(responseBody);
                System.out.println(responseBody);
            }
            else
            {
                String responseDescription = con.getResponseMessage();
                System.out.println("Request failed, response code: " + responseCode + " (" + responseDescription + ")");
            }

        } catch (ProtocolException e)
        {
            System.out.println("Protocol not supported by the server");
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            System.out.println("Something went wrong: " + e.getMessage());
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
