package com.hualala.mobilebox;


import com.hualala.data.executor.UIThread;
import com.hualala.data.repository.TerminalDefaultBusinessContractor;
import com.hualala.domain.config.CloudServerInfo;
import com.hualala.domain.config.GeneralConfig;
import com.hualala.domain.interactor.BusinessContractor;
import com.hualala.libutils.MBContext;
import com.hualala.libutils.wifi.WifiUtils;

public class MBBusinessContractor {

    private static BusinessContractor businessContractor;

    public static BusinessContractor getBusinessContractor() {
        return businessContractor;
    }

    public static void initBusinessContractor() {
        GeneralConfig generalConfig = new GeneralConfig();
        CloudServerInfo cloudServerInfo = new CloudServerInfo();
        cloudServerInfo.setBaseApiUrl("http://0.0.0.0:8888/");
        generalConfig.setCloudServerInfo(cloudServerInfo);
        businessContractor = new TerminalDefaultBusinessContractor(generalConfig, UIThread.getInstance(), null);
    }

    public static String getFileBaseUrl() {
        if (getBusinessContractor().getGeneralConfig().getCloudServerInfo().getBaseApiUrl().equals(WifiUtils.getWifiIp(MBContext.getContext()))) {
            return MBBusinessContractor.getBusinessContractor()
                    .getGeneralConfig().getCloudServerInfo().getBaseApiUrl() + "files/";
        }
        return "file://";
    }

}
