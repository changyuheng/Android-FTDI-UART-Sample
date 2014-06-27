/*
 * Copyright (C) 2013
 * Licensed under the License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * This code is checked by Galaxy S II and FT232RL
 */
package com.tailyn.ftdiserialdemo;

import java.io.UnsupportedEncodingException;

import com.tailyn.ftdiserialdemo.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

public class MainActivity extends Activity {
    static final String TAG = "FTDISerialDemo";

    static D2xxManager mD2xxManager;
    FT_Device mFTDevice;

    Button mOpenButton;
    Button mCloseButton;
    Button mWriteButton;
    EditText mInputEditText;
    Spinner mPortSpinner;
    Spinner mBaudSpinner;
    Spinner mCodePageSpinner;

    private boolean mThreadStopped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mThreadStopped = true;
        mInputEditText = (EditText) findViewById(R.id.input);
        mOpenButton = (Button) findViewById(R.id.open);
        mCloseButton = (Button) findViewById(R.id.close);
        mWriteButton = (Button) findViewById(R.id.write);
        mPortSpinner = (Spinner) findViewById(R.id.port);
        mBaudSpinner = (Spinner) findViewById(R.id.baud);
        mCodePageSpinner = (Spinner) findViewById(R.id.code_page);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.port, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPortSpinner.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(
                this, R.array.baud, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBaudSpinner.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(
                this, R.array.code_page, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCodePageSpinner.setAdapter(adapter);

        updateOpenCloseButton(false);

        try {
            mD2xxManager = D2xxManager.getInstance(this);
        } catch (D2xxManager.D2xxException ex) {
            Log.e(TAG,ex.toString());
        }

        if (!mD2xxManager.setVIDPID(0x0403, 0xada1)) {
            Log.i(TAG, "setVIDPID Error");
        }
    }

    public void onOpenClicked(View v) {
        openDevice();
    }

    public void onCloseClicked(View v) {
        closeDevice();
    }

    public void onWriteClicked(View v) {
        write();
    }

    protected void write() {
        new Thread(new Runnable() {
            public void run() {
                handler.sendEmptyMessage(0);

                if (mFTDevice == null)
                    return;

                if (!mFTDevice.isOpen()) {
                    Log.e(TAG, "onClickWrite : Device not opened.");
                    return;
                }

                mFTDevice.setLatencyTimer((byte) 16);

                String inputString = mInputEditText.getText().toString();
                String currentSelectedCharset = mCodePageSpinner.getSelectedItem().toString();
                byte[] codePageCommand = null;
                byte[] inputBytes = null;

                if (currentSelectedCharset.equals("Chinese")) {
                    codePageCommand = new byte[] {
                        (byte) 0x1C, (byte) 0x26, (byte) 0x1B, (byte) 0x74, (byte) 0x00
                    };
                    try {
                        inputBytes = inputString.getBytes("Big5");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else if (currentSelectedCharset.equals("Japanese")) {
                    codePageCommand = new byte[] {
                        (byte) 0x1C, (byte) 0x2E, (byte) 0x1B, (byte) 0x74, (byte) 0x01
                    };
                    inputBytes = new byte[inputString.length()];
                    for (int i = 0; i < inputString.length(); i++) {
                        char c = inputString.charAt(i);
                        if (c <= 0x7F) {
                            inputBytes[i] = (byte) c;
                            continue;
                        }

                        String s = inputString.substring(i, i + 1);
                        if (CodeTable.JAPANESE.containsKey(s)) {
                            inputBytes[i] = CodeTable.JAPANESE.get(
                                    inputString.substring(i, i + 1)).byteValue();
                        } else {
                            inputBytes[i] = (byte) 0x3F;
                        }
                    }
                } else {
                    // TODO : Implement other charset support
                }

                if (inputBytes != null) {
                    // Set code page
                    mFTDevice.write(codePageCommand, codePageCommand.length);

                    mFTDevice.write(inputBytes, inputBytes.length);

                    // For debug
                    StringBuilder inputBytesStringBuilder = new StringBuilder();
                    for (byte b : inputBytes) {
                        inputBytesStringBuilder.append(String.format("%02X ", b));
                    }
                    Log.d(TAG, "Input bytes: " + inputBytesStringBuilder.toString());
                }

                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0)
                mWriteButton.setEnabled(false);
            else
                mWriteButton.setEnabled(true);
        }
    };

    @Override
    protected void onDestroy() {
        closeDevice();
        super.onDestroy();
    }

    protected void openDevice() {
        Integer port = Integer.valueOf(mPortSpinner.getSelectedItem().toString());
        Integer baud = Integer.valueOf(mBaudSpinner.getSelectedItem().toString());

        if (mFTDevice != null) {
            if (mFTDevice.isOpen()) {
                if (mThreadStopped) {
                    updateOpenCloseButton(true);
                    setSerialConfig(baud, (byte) 8, (byte) 1, (byte) 0, (byte) 0);
                    mFTDevice.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
                    mFTDevice.restartInTask();
                    new Thread(mLoop).start();
                }
                return;
            }
        }

        int devCount = 0;
        devCount = mD2xxManager.createDeviceInfoList(this);

        D2xxManager.FtDeviceInfoListNode[] deviceList = new D2xxManager.FtDeviceInfoListNode[devCount];
        mD2xxManager.getDeviceInfoList(devCount, deviceList);

        if (devCount <= 0)
            return;

        if (mFTDevice == null)
            mFTDevice = mD2xxManager.openByIndex(this, port);

        if (mFTDevice.isOpen()) {
            if (mThreadStopped) {
                updateOpenCloseButton(true);
                setSerialConfig(baud, (byte) 8, (byte) 1, (byte) 0, (byte) 0);
                mFTDevice.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
                mFTDevice.restartInTask();
                new Thread(mLoop).start();
            }
        }
    }

    private Runnable mLoop = new Runnable() {
        @Override
        public void run() {
            final int MAX_SIZE = 512;
            byte[] rbuf = new byte[MAX_SIZE];
            char[] rchar = new char[MAX_SIZE];

            mThreadStopped = false;
            while (true) {
                if (mThreadStopped)
                    break;
                synchronized (mFTDevice) {
                    int readSize = mFTDevice.getQueueStatus();
                    if (readSize > 0) {
                        int size = readSize;
                        if (size > MAX_SIZE) {
                            size = MAX_SIZE;
                        }
                        mFTDevice.read(rbuf, size);
                        // cannot use System.arraycopy
                        for (int i = 0; i < MAX_SIZE; i++) {
                            rchar[i] = (char) rbuf[i];
                        }
                    }
                }
            }
        }
    };

    void closeDevice() {
        mThreadStopped = true;
        updateOpenCloseButton(false);
        if (mFTDevice != null) {
            mFTDevice.close();
        }
    }

    private void updateOpenCloseButton(boolean on) {
        if (on) {
            mOpenButton.setEnabled(false);
            mWriteButton.setEnabled(true);
            mCloseButton.setEnabled(true);
        } else {
            mOpenButton.setEnabled(true);
            mWriteButton.setEnabled(false);
            mCloseButton.setEnabled(false);
        }
    }

    protected void setSerialConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl) {
        if (mFTDevice.isOpen() == false) {
            Log.e(TAG, "SetConfig: device not open");
            return;
        }

        // configure our port
        // reset to UART mode for 232 devices
        mFTDevice.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);

        mFTDevice.setBaudRate(baud);

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

        mFTDevice.setDataCharacteristics(dataBits, stopBits, parity);

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

        mFTDevice.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0d);
    }
}
