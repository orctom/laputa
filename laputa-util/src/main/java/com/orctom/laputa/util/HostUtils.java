package com.orctom.laputa.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * Host utils
 * Created by hao on 7/11/16.
 */
public abstract class HostUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(HostUtils.class);

  private static final String LOCALHOST = "127.0.0.1";
  private static final String ANY_HOST = "0.0.0.0";
  private static final Pattern PATTERN_IP = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");
  private static final String ETH0 = "eth0";
  private static final String PREFIX_DOCKER = "docker";
  private static volatile InetAddress LOCAL_ADDRESS = null;
  private static volatile String MAC_ADDRESS = null;

  public static String getHostIdentity() {
    InetAddress localhost = getLocalHostAddress();
    if (null != localhost) {
      return localhost.getHostName() + "" + getHostId();
    }
    return String.valueOf(getHostId());
  }

  public static Long getHostId() {
    String mac = getMacAddress();
    if (null == mac) {
      return null;
    }
    try {
      long id = Long.valueOf(mac.replaceAll("\\W", ""), 16);
      return id > 0 ? id : -id;
    } catch (NumberFormatException e) {
      return null;
    }
  }

  public static String getMacAddress() {
    if (null == MAC_ADDRESS) {
      MAC_ADDRESS = getMacAddress0();
    }
    return MAC_ADDRESS;
  }

  private static String getMacAddress0() {
    InetAddress address = getLocalAddress();
    try {
      NetworkInterface network = NetworkInterface.getByInetAddress(address);
      byte[] mac = network.getHardwareAddress();

      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < mac.length; i++) {
        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
      }
      return sb.toString();
    } catch (SocketException e) {
      LOGGER.warn(e.getMessage(), e);
      return null;
    }
  }

  public static String getIP() {
    return getLocalAddress().getHostAddress();
  }

  private static boolean isValidAddress(InetAddress address) {
    if (address == null || address.isLoopbackAddress())
      return false;
    String name = address.getHostAddress();
    return (name != null
        && !ANY_HOST.equals(name)
        && !LOCALHOST.equals(name)
        && PATTERN_IP.matcher(name).matches());
  }

  public static InetAddress getLocalAddress() {
    if (null == LOCAL_ADDRESS) {
      LOCAL_ADDRESS = getLocalAddress0();
    }
    return LOCAL_ADDRESS;
  }

  private static InetAddress getLocalAddress0() {
    InetAddress eth0 = getValidEth0AddressOrNull();
    if (null != eth0) {
      return eth0;
    }

    InetAddress localhostAddress = getLocalHostAddress();
    if (null != localhostAddress && isValidAddress(localhostAddress)) {
      return localhostAddress;
    }

    try {
      Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
      if (networkInterfaces != null) {
        while (networkInterfaces.hasMoreElements()) {
          try {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            if (networkInterface.isLoopback() ||
                networkInterface.isPointToPoint() ||
                networkInterface.isVirtual() ||
                !networkInterface.isUp() ||
                networkInterface.getName().startsWith(PREFIX_DOCKER)) {
              continue;
            }

            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
              try {
                InetAddress address = addresses.nextElement();
                if (isValidAddress(address)) {
                  return address;
                }
              } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
              }
            }
          } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
          }
        }
      }
    } catch (SocketException e) {
      LOGGER.warn(e.getMessage(), e);
    }

    LOGGER.error("Could not get local host ip address, using 127.0.0.1");
    return localhostAddress;
  }

  private static InetAddress getValidEth0AddressOrNull() {
    try {
      NetworkInterface networkInterface = NetworkInterface.getByName(ETH0);
      if (null != networkInterface) {
        Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          InetAddress address = addresses.nextElement();
          if (isValidAddress(address)) {
            return address;
          }
        }
      }
    } catch (SocketException e) {
      LOGGER.warn(e.getMessage(), e);
    }

    return null;
  }

  private static InetAddress getLocalHostAddress() {
    try {
      return InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      LOGGER.warn(e.getMessage(), e);
      return null;
    }
  }
}
