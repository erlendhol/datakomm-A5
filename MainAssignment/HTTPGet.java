package MainAssignment;

public class HTTPGet
{
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


}
