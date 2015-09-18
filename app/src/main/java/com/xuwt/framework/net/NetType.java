package com.xuwt.framework.net;

/**
 * 网络连接类型
 * @author lijunma
 *
 */
public enum NetType {
	Unknown(0),
	Wifi(1),
	CMNet(2),
	CMWap(3),
	UNNet(4),
	UNWap(5),
	CTNet(6),
	CTWap(7),
	G3Wap(8),
	G3Net(9);
	
	private int value = 0;
    private NetType(int value) {
        this.value = value;
    }
    public static NetType valueOf(int value) {
        switch (value) {
        case 0:
            return Unknown;
        case 1:
            return Wifi;
        case 2:
            return CMNet;
        case 3:
            return CMWap;
        case 4:
            return UNNet;
        case 5:
            return UNWap;
        case 6:
            return CTNet;
        case 7:
            return CTWap;
        case 8:
            return G3Wap;
        case 9:
            return G3Net;
        default:
            return Unknown;
        }
    }

    public int value() {
        return this.value;
    }
}
