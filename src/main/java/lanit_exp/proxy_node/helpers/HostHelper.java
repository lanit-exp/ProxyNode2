package lanit_exp.proxy_node.helpers;

import java.net.InetAddress;

public class HostHelper {

    public static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }

}
