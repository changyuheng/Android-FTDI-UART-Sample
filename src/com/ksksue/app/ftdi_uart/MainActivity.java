/*
 * Copyright (C) 2013
 * Licensed under the License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * This code is checked by Galaxy S II and FT232RL
 */
package com.ksksue.app.ftdi_uart;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.CheckBox;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.ksksue.app.fpga_fifo.R;

public class MainActivity extends Activity {
    private final static String TAG = "FTDIDemo";

    private static D2xxManager ftD2xx = null;
    private FT_Device ftDev;

    static final int READBUF_SIZE  = 512;
    byte[] rbuf  = new byte[READBUF_SIZE];
    char[] rchar = new char[READBUF_SIZE];
    int mReadSize=0;

    TextView tvRead;
    EditText etWrite;
    Button btOpen;
    Button btWrite;
    Button btClose;
    CheckBox cFT4232;
    CheckBox cFT2232;
    Spinner spBaudrate;
    Spinner spDevPort;

    Handler mHandler = new Handler();
    Thread mThread;
    boolean mThreadIsStopped;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mThreadIsStopped = true;
        tvRead = (TextView) findViewById(R.id.tvRead);
        etWrite = (EditText) findViewById(R.id.etWrite);
        btOpen = (Button) findViewById(R.id.btOpen);
        btWrite = (Button) findViewById(R.id.btWrite);
        btClose = (Button) findViewById(R.id.btClose);
        cFT4232 = (CheckBox) findViewById(R.id.cFT4232);
        cFT2232 = (CheckBox) findViewById(R.id.cFT2232);
        spBaudrate = (Spinner) findViewById(R.id.sp_Brate);
        spDevPort = (Spinner) findViewById(R.id.spDevPort);
        updateView(false);

        List<String> BrateList = new ArrayList<String>();
        BrateList.add("9600");
        BrateList.add("38400");
        BrateList.add("115200");

        List<String> DportList = new ArrayList<String>();
        DportList.add("0");
        DportList.add("1");
        DportList.add("2");
        DportList.add("3");
        DportList.add("4");
        DportList.add("5");

        ArrayAdapter<String> BrateAdapter = new ArrayAdapter<String>
        (this, android.R.layout.simple_spinner_item,BrateList);

        ArrayAdapter<String> DporAdapter = new ArrayAdapter<String>
        (this, android.R.layout.simple_spinner_item,DportList);

        BrateAdapter.setDropDownViewResource
        (android.R.layout.simple_spinner_dropdown_item);

        DporAdapter.setDropDownViewResource
        (android.R.layout.simple_spinner_dropdown_item);

        spBaudrate.setAdapter(BrateAdapter);
        spDevPort.setAdapter(DporAdapter);

        try {
            ftD2xx = D2xxManager.getInstance(this);
        } catch (D2xxManager.D2xxException ex) {
            Log.e(TAG,ex.toString());
        }

        if(!ftD2xx.setVIDPID(0x0403, 0xada1)) {
            Log.i(TAG,"setVIDPID Error");
        }
    }

    public void onClickOpen(View v) {
        openDevice();
    }

    public void onClickWrite(View v) {

        btWrite.setEnabled(false);
        new Thread(new Runnable() {
            public void run() {
                if(ftDev == null) {
                    return;
                }
                if(ftDev.isOpen() == false) {
                    Log.e(TAG, "onClickWrite : Device is not open");
                    return;
                }
                ftDev.setLatencyTimer((byte)16);

                String writeString = etWrite.getText().toString();
                byte[] writeByte = writeString.getBytes();
                ftDev.write(writeByte, writeString.length());
                handler.sendEmptyMessage(0);
            }
          }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            btWrite.setEnabled(true);
        }
    };

    public void onClickClose(View v) {
        closeDevice();
    }

    @Override
    public void onDestroy() {
        closeDevice();
        super.onDestroy();
    }

    private void openDevice() {
        String writeBR = String.valueOf(spBaudrate.getSelectedItem());
        String dev_port= String.valueOf(spDevPort.getSelectedItem());

        if(ftDev != null) {
            if(ftDev.isOpen()) {
                if(mThreadIsStopped) {
                    updateView(true);
                    SetConfig(Integer.valueOf(writeBR), (byte)8, (byte)1, (byte)0, (byte)0);
                    ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
                    ftDev.restartInTask();
                    new Thread(mLoop).start();
                }
                return;
            }
        }

        int devCount = 0;
        devCount = ftD2xx.createDeviceInfoList(this);

        D2xxManager.FtDeviceInfoListNode[] deviceList = new D2xxManager.FtDeviceInfoListNode[devCount];
        ftD2xx.getDeviceInfoList(devCount, deviceList);

        if(devCount <= 0) {
            return;
        }

        if(ftDev == null) {
            if(cFT2232.isChecked()==true && cFT4232.isChecked()==false){
                ftDev = ftD2xx.openByIndex(this, Integer.valueOf(dev_port));
            }
            else if(cFT2232.isChecked()==false && cFT4232.isChecked()==true){
                ftDev = ftD2xx.openByIndex(this, Integer.valueOf(dev_port));
            }
            else{
                return;
            }

        } else {
            synchronized (ftDev) {
                // 4232: 0~3, 2232:4~5
                if(cFT2232.isChecked()==true && cFT4232.isChecked()==false){
                    ftDev = ftD2xx.openByIndex(this, Integer.valueOf(dev_port));
                }
                else if(cFT2232.isChecked()==false && cFT4232.isChecked()==true){
                    ftDev = ftD2xx.openByIndex(this, Integer.valueOf(dev_port));
                }
                else{
                    return;
                }
            }
        }

        if(ftDev.isOpen()) {
            if(mThreadIsStopped) {
                updateView(true);
                SetConfig(Integer.valueOf(writeBR), (byte)8, (byte)1, (byte)0, (byte)0);
                ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
                ftDev.restartInTask();
                new Thread(mLoop).start();
            }
        }
    }

    private Runnable mLoop = new Runnable() {
        @Override
        public void run() {
            int i;
            int readSize;
            mThreadIsStopped = false;
            while(true) {
                if(mThreadIsStopped) {
                    break;
                }

                synchronized (ftDev) {
                    readSize = ftDev.getQueueStatus();
                    if(readSize>0) {
                        mReadSize = readSize;
                        if(mReadSize > READBUF_SIZE) {
                            mReadSize = READBUF_SIZE;
                        }
                        ftDev.read(rbuf,mReadSize);

                        // cannot use System.arraycopy
                        for(i=0; i<mReadSize; i++) {
                            rchar[i] = (char)rbuf[i];
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvRead.append(String.copyValueOf(rchar,0,mReadSize));
                            }
                        });

                    } // end of if(readSize>0)
                } // end of synchronized
            }
        }
    };

    private void closeDevice() {
        mThreadIsStopped = true;
        updateView(false);
        if(ftDev != null) {
            ftDev.close();
        }
    }

    private void updateView(boolean on) {
        if(on) {
            btOpen.setEnabled(false);
            btWrite.setEnabled(true);
            btClose.setEnabled(true);
        } else {
            btOpen.setEnabled(true);
            btWrite.setEnabled(false);
            btClose.setEnabled(false);
        }
    }

    public void SetConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl) {
        if (ftDev.isOpen() == false) {
            Log.e(TAG, "SetConfig: device not open");
            return;
        }

        // configure our port
        // reset to UART mode for 232 devices
        ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);

        ftDev.setBaudRate(baud);

        switch (dataBits) {
        case 7:
            dataBits = D2xxManager.FT_DATA_BITS_7;
            break;
        case 8:
            dataBits = D2xxManager.FT_DATA_BITS_8;
            break;
        default:
            dataBits = D2xxManager.FT_DATA_BITS_8;
            break;
        }

        switch (stopBits) {
        case 1:
            stopBits = D2xxManager.FT_STOP_BITS_1;
            break;
        case 2:
            stopBits = D2xxManager.FT_STOP_BITS_2;
            break;
        default:
            stopBits = D2xxManager.FT_STOP_BITS_1;
            break;
        }

        switch (parity) {
        case 0:
            parity = D2xxManager.FT_PARITY_NONE;
            break;
        case 1:
            parity = D2xxManager.FT_PARITY_ODD;
            break;
        case 2:
            parity = D2xxManager.FT_PARITY_EVEN;
            break;
        case 3:
            parity = D2xxManager.FT_PARITY_MARK;
            break;
        case 4:
            parity = D2xxManager.FT_PARITY_SPACE;
            break;
        default:
            parity = D2xxManager.FT_PARITY_NONE;
            break;
        }

        ftDev.setDataCharacteristics(dataBits, stopBits, parity);

        short flowCtrlSetting;
        switch (flowControl) {
        case 0:
            flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
            break;
        case 1:
            flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
            break;
        case 2:
            flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
            break;
        case 3:
            flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
            break;
        default:
            flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
            break;
        }

        // TODO : flow ctrl: XOFF/XOM
        // TODO : flow ctrl: XOFF/XOM
        ftDev.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0d);
    }
}
