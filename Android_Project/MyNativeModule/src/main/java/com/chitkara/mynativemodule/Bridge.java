package com.chitkara.mynativemodule;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;

import com.unity3d.player.UnityPlayer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class Bridge
{
    private static final String TAG = "AndroidPlugin";
    private static final int message = 1;

    private static Handler h;

    private static BluetoothAdapter bluetoothAdapter = null;
    private static BluetoothSocket btSocket = null;

    private static ConnectedThread mConnectedThread;

    private static String address = "null";
    private static StringBuilder sb = new StringBuilder();

    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private  static  boolean connected = false;

    private static int dataCount = 35;

    public static void Initialize()
    {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter==null)
            showWarning("Bluetooth Device Not Available.");
        else if(!bluetoothAdapter.isEnabled())
            showWarning("Bluetooth Device Not Available.");

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                String name = bt.getName();

                if(name.contains("HC-05"))
                {
                    address = bt.getAddress();

                    UnityPlayer.UnitySendMessage("AndroidNativeController", "onCallBackBluetoothInfo", "Found Bluetooth "+ name +"\n"+address);
                }
            }
        }
        else
        {
            showWarning("No Paired Bluetooth Devices Found.");
        }
    }

    public static void Connect()
    {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        try
        {
            btSocket = createBluetoothSocket(device);
        }
        catch (IOException e)
        {
            showWarning("Socket create failed "+ e.getMessage());
        }

        if(address.contains("null") || btSocket == null)
        {
            showWarning("Connection Failed.Try again. Address:- " + address +" Socket:- "+ btSocket);
        }
        else
        {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        bluetoothAdapter.cancelDiscovery();

        try
        {
            btSocket.connect();
            connected = true;
        }
        catch (IOException e)
        {
            showWarning("Could not connect to bluetooth.");

            connected = false;

            try
            {
                btSocket.close();
            }
            catch (IOException e2)
            {
                showWarning("unable to close socket during connection failure" + e2.getMessage());
            }
        }

        UnityPlayer.UnitySendMessage("AndroidNativeController", "onCallBackConnecting", "Check Connection");
    }

    public static void isConnected()
    {
        if(connected)
        {
            UnityPlayer.UnitySendMessage("AndroidNativeController", "onCallBackConnected", "Connected");
        }
        else
        {
            UnityPlayer.UnitySendMessage("AndroidNativeController", "onCallBackConnected", "Connecting...");
        }
    }

    public static void startThread()
    {
        startHandler();

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        UnityPlayer.UnitySendMessage("AndroidNativeController", "onCallBackThread", "");
    }

    @SuppressLint("HandlerLeak")
    private  static  void startHandler()
    {
        h = new Handler()
        {
            public void handleMessage(android.os.Message msg)
            {
                switch (msg.what)
                {
                    case message:
                        byte[] readBuf = (byte[]) msg.obj;

                        String strIncom = new String(readBuf, 0, msg.arg1);
                        //Log.d(TAG, "data: "+strIncom);

                        UnityPlayer.UnitySendMessage("AndroidNativeController", "CallBackValues", strIncom);

                        break;
                }
            }
        };
    }

    public static void WriteValues(String value)
    {
        mConnectedThread.write(value);
    }

    private static BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        return  device.createRfcommSocketToServiceRecord(myUUID);
    }

    private static class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        byte[] buffer = new byte[256];

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            int bytes;

            while (true)
            {
                try
                {
                    bytes = mmInStream.read(buffer);
                    h.obtainMessage(message, bytes, -1, buffer).sendToTarget();
                }
                catch (IOException e)
                {
                    Log.d(TAG, "...Error reading data from arduino");
                    break;
                }
            }
        }

        public void write(String message)
        {
            byte[] msgBuffer = message.getBytes();
            try
            {
                mmOutStream.write(msgBuffer);
            }
            catch (IOException e)
            {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }

    private static void showWarning(String msg)
    {
        AlertDialog.Builder messagePopup = new AlertDialog.Builder(new ContextThemeWrapper(UnityPlayer.currentActivity, GetTheme()));
        messagePopup.setTitle("!Warning");
        messagePopup.setMessage(msg);
        messagePopup.setPositiveButton("ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        messagePopup.setCancelable(false);
        messagePopup.show();
    }

    @SuppressLint("InlinedApi")
    private static int GetTheme()
    {
        int theme = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            theme = android.R.style.Theme_Material_Light_Dialog;
        else
            theme = android.R.style.Theme_Holo_Dialog;
        return theme;
    }
}