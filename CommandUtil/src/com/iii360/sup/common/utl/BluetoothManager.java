package com.iii360.sup.common.utl;

import android.bluetooth.BluetoothAdapter;
/**
 * 
 * @author Jerome.Hu
 * <pre>
 * 蓝牙相关的工具类
 * </pre>
 *
 */
public class BluetoothManager {
	/**
	 * 打开蓝牙
	 */
    public static void  openBluetooth() {
            final BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                    .getDefaultAdapter();
            final boolean flag = bluetoothAdapter.isEnabled();
            if (!flag) {
                bluetoothAdapter.enable();
            }
    }
    /**
     * 关闭蓝牙
     */
    public static void closeBluetooth() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        final boolean flag = bluetoothAdapter.isEnabled();
        if (flag) {
            bluetoothAdapter.disable();
        }
    }
    /**
     * 
     * @return 蓝牙是否可用
     */
    public static boolean isBluetoothEnable() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        final boolean flag = bluetoothAdapter.isEnabled();
        return flag; 
    }
}
