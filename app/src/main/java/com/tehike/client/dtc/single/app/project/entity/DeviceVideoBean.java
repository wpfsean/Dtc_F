package com.tehike.client.dtc.single.app.project.entity;

import com.tehike.client.dtc.single.app.project.onvif.MediaProfile;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 描述：$desc$
 * ===============================
 *
 * @author $user$ wpfsean@126.com
 * @version V1.0
 * @Create at:$date$ $time$
 */

public class DeviceVideoBean  implements Serializable {


    /**
     * 哨位Guid(根据此guid关联哨位视频，面部视频，弹箱视频)
     */
    private String guid;

    //面部视频
    class FaceVideoBean implements  Serializable{
        //通道（流媒体）
        private String channel;
        //视频类型
        private String devicetype;
        //面部视频唯一id
        private String id;
        //Ip地址
        private String ipaddress;
        //名称
        private String name;

        //坐标
        private String location;
        //密码
        private String password;
        //端口
        private int port;
        //用户名
        private String username;

        //解析时要返回的参数(详见onvif协议)
        private String serviceUrl;//用于请求onvif时的url
        private String mediaUrl;
        private String shotPicUrl;
        private String ptzUrl;
        private String rtsp;
        private String token;

        public FaceVideoBean() {
            profiles = new ArrayList<>();

        }

        private ArrayList<MediaProfile> profiles;
        public ArrayList<MediaProfile> getProfiles() {
            return profiles;
        }

        public void addProfile(MediaProfile profile) {
            this.profiles.add(profile);
        }

        public void addProfiles(ArrayList<MediaProfile> profiles) {
            this.profiles.addAll(profiles);
        }


        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getDevicetype() {
            return devicetype;
        }

        public void setDevicetype(String devicetype) {
            this.devicetype = devicetype;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIpaddress() {
            return ipaddress;
        }

        public void setIpaddress(String ipaddress) {
            this.ipaddress = ipaddress;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getMediaUrl() {
            return mediaUrl;
        }

        public void setMediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
        }

        public String getShotPicUrl() {
            return shotPicUrl;
        }

        public void setShotPicUrl(String shotPicUrl) {
            this.shotPicUrl = shotPicUrl;
        }

        public String getPtzUrl() {
            return ptzUrl;
        }

        public void setPtzUrl(String ptzUrl) {
            this.ptzUrl = ptzUrl;
        }

        public String getRtsp() {
            return rtsp;
        }

        public void setRtsp(String rtsp) {
            this.rtsp = rtsp;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }


        public FaceVideoBean(String channel, String devicetype, String id, String ipaddress, String name, String location, String password, int port, String username, String serviceUrl, String mediaUrl, String shotPicUrl, String ptzUrl, String rtsp, String token) {
            this.channel = channel;
            this.devicetype = devicetype;
            this.id = id;
            this.ipaddress = ipaddress;
            this.name = name;
            this.location = location;
            this.password = password;
            this.port = port;
            this.username = username;
            this.serviceUrl = serviceUrl;
            this.mediaUrl = mediaUrl;
            this.shotPicUrl = shotPicUrl;
            this.ptzUrl = ptzUrl;
            this.rtsp = rtsp;
            this.token = token;

            profiles = new ArrayList<>();
        }

        @Override
        public String toString() {
            return "VideoBean{" +
                    "channel='" + channel + '\'' +
                    ", devicetype='" + devicetype + '\'' +
                    ", id='" + id + '\'' +
                    ", ipaddress='" + ipaddress + '\'' +
                    ", name='" + name + '\'' +
                    ", location='" + location + '\'' +
                    ", password='" + password + '\'' +
                    ", port=" + port +
                    ", username='" + username + '\'' +
                    ", serviceUrl='" + serviceUrl + '\'' +
                    ", mediaUrl='" + mediaUrl + '\'' +
                    ", shotPicUrl='" + shotPicUrl + '\'' +
                    ", ptzUrl='" + ptzUrl + '\'' +
                    ", rtsp='" + rtsp + '\'' +
                    ", token='" + token + '\'' +
                    ", profiles=" + profiles +
                    '}';
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getServiceUrl() {
            return serviceUrl;
        }

        public void setServiceUrl(String serviceUrl) {
            this.serviceUrl = serviceUrl;
        }
    }

    //哨位视频
    class SentryVideoBean implements  Serializable{
        //通道（流媒体）
        private String channel;
        //视频类型
        private String devicetype;
        //面部视频唯一id
        private String id;
        //Ip地址
        private String ipaddress;
        //名称
        private String name;

        //坐标
        private String location;
        //密码
        private String password;
        //端口
        private int port;
        //用户名
        private String username;

        //解析时要返回的参数(详见onvif协议)
        private String serviceUrl;//用于请求onvif时的url
        private String mediaUrl;
        private String shotPicUrl;
        private String ptzUrl;
        private String rtsp;
        private String token;

        public SentryVideoBean() {
            profiles = new ArrayList<>();

        }

        private ArrayList<MediaProfile> profiles;
        public ArrayList<MediaProfile> getProfiles() {
            return profiles;
        }

        public void addProfile(MediaProfile profile) {
            this.profiles.add(profile);
        }

        public void addProfiles(ArrayList<MediaProfile> profiles) {
            this.profiles.addAll(profiles);
        }


        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getDevicetype() {
            return devicetype;
        }

        public void setDevicetype(String devicetype) {
            this.devicetype = devicetype;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIpaddress() {
            return ipaddress;
        }

        public void setIpaddress(String ipaddress) {
            this.ipaddress = ipaddress;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getMediaUrl() {
            return mediaUrl;
        }

        public void setMediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
        }

        public String getShotPicUrl() {
            return shotPicUrl;
        }

        public void setShotPicUrl(String shotPicUrl) {
            this.shotPicUrl = shotPicUrl;
        }

        public String getPtzUrl() {
            return ptzUrl;
        }

        public void setPtzUrl(String ptzUrl) {
            this.ptzUrl = ptzUrl;
        }

        public String getRtsp() {
            return rtsp;
        }

        public void setRtsp(String rtsp) {
            this.rtsp = rtsp;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }


        public SentryVideoBean(String channel, String devicetype, String id, String ipaddress, String name, String location, String password, int port, String username, String serviceUrl, String mediaUrl, String shotPicUrl, String ptzUrl, String rtsp, String token) {
            this.channel = channel;
            this.devicetype = devicetype;
            this.id = id;
            this.ipaddress = ipaddress;
            this.name = name;
            this.location = location;
            this.password = password;
            this.port = port;
            this.username = username;
            this.serviceUrl = serviceUrl;
            this.mediaUrl = mediaUrl;
            this.shotPicUrl = shotPicUrl;
            this.ptzUrl = ptzUrl;
            this.rtsp = rtsp;
            this.token = token;

            profiles = new ArrayList<>();
        }

        @Override
        public String toString() {
            return "VideoBean{" +
                    "channel='" + channel + '\'' +
                    ", devicetype='" + devicetype + '\'' +
                    ", id='" + id + '\'' +
                    ", ipaddress='" + ipaddress + '\'' +
                    ", name='" + name + '\'' +
                    ", location='" + location + '\'' +
                    ", password='" + password + '\'' +
                    ", port=" + port +
                    ", username='" + username + '\'' +
                    ", serviceUrl='" + serviceUrl + '\'' +
                    ", mediaUrl='" + mediaUrl + '\'' +
                    ", shotPicUrl='" + shotPicUrl + '\'' +
                    ", ptzUrl='" + ptzUrl + '\'' +
                    ", rtsp='" + rtsp + '\'' +
                    ", token='" + token + '\'' +
                    ", profiles=" + profiles +
                    '}';
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getServiceUrl() {
            return serviceUrl;
        }

        public void setServiceUrl(String serviceUrl) {
            this.serviceUrl = serviceUrl;
        }
    }

    //弹箱视频
    class AmmoVideoBean implements  Serializable{
        //通道（流媒体）
        private String channel;
        //视频类型
        private String devicetype;
        //面部视频唯一id
        private String id;
        //Ip地址
        private String ipaddress;
        //名称
        private String name;

        //坐标
        private String location;
        //密码
        private String password;
        //端口
        private int port;
        //用户名
        private String username;

        //解析时要返回的参数(详见onvif协议)
        private String serviceUrl;//用于请求onvif时的url
        private String mediaUrl;
        private String shotPicUrl;
        private String ptzUrl;
        private String rtsp;
        private String token;

        public AmmoVideoBean() {
            profiles = new ArrayList<>();

        }

        private ArrayList<MediaProfile> profiles;
        public ArrayList<MediaProfile> getProfiles() {
            return profiles;
        }

        public void addProfile(MediaProfile profile) {
            this.profiles.add(profile);
        }

        public void addProfiles(ArrayList<MediaProfile> profiles) {
            this.profiles.addAll(profiles);
        }


        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getDevicetype() {
            return devicetype;
        }

        public void setDevicetype(String devicetype) {
            this.devicetype = devicetype;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIpaddress() {
            return ipaddress;
        }

        public void setIpaddress(String ipaddress) {
            this.ipaddress = ipaddress;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getMediaUrl() {
            return mediaUrl;
        }

        public void setMediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
        }

        public String getShotPicUrl() {
            return shotPicUrl;
        }

        public void setShotPicUrl(String shotPicUrl) {
            this.shotPicUrl = shotPicUrl;
        }

        public String getPtzUrl() {
            return ptzUrl;
        }

        public void setPtzUrl(String ptzUrl) {
            this.ptzUrl = ptzUrl;
        }

        public String getRtsp() {
            return rtsp;
        }

        public void setRtsp(String rtsp) {
            this.rtsp = rtsp;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }


        public AmmoVideoBean(String channel, String devicetype, String id, String ipaddress, String name, String location, String password, int port, String username, String serviceUrl, String mediaUrl, String shotPicUrl, String ptzUrl, String rtsp, String token) {
            this.channel = channel;
            this.devicetype = devicetype;
            this.id = id;
            this.ipaddress = ipaddress;
            this.name = name;
            this.location = location;
            this.password = password;
            this.port = port;
            this.username = username;
            this.serviceUrl = serviceUrl;
            this.mediaUrl = mediaUrl;
            this.shotPicUrl = shotPicUrl;
            this.ptzUrl = ptzUrl;
            this.rtsp = rtsp;
            this.token = token;

            profiles = new ArrayList<>();
        }

        @Override
        public String toString() {
            return "VideoBean{" +
                    "channel='" + channel + '\'' +
                    ", devicetype='" + devicetype + '\'' +
                    ", id='" + id + '\'' +
                    ", ipaddress='" + ipaddress + '\'' +
                    ", name='" + name + '\'' +
                    ", location='" + location + '\'' +
                    ", password='" + password + '\'' +
                    ", port=" + port +
                    ", username='" + username + '\'' +
                    ", serviceUrl='" + serviceUrl + '\'' +
                    ", mediaUrl='" + mediaUrl + '\'' +
                    ", shotPicUrl='" + shotPicUrl + '\'' +
                    ", ptzUrl='" + ptzUrl + '\'' +
                    ", rtsp='" + rtsp + '\'' +
                    ", token='" + token + '\'' +
                    ", profiles=" + profiles +
                    '}';
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getServiceUrl() {
            return serviceUrl;
        }

        public void setServiceUrl(String serviceUrl) {
            this.serviceUrl = serviceUrl;
        }
    }


}
