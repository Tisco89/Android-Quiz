package se.ju.stos1605.quizzz;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

public class BluetoothClientConnector extends Thread {

    private BluetoothSocket mSocket;

    private BluetoothDevice mDevice;

    public BluetoothClientConnector(BluetoothDevice device) {
        BluetoothSocket tmp = null;
        mDevice = device;

        try {
            tmp = device.createRfcommSocketToServiceRecord(BluetoothHandler.MY_UUID);
        } catch (IOException e) {
            Log.e("UH", "Socket's create() method failed", e);
        }
        mSocket = tmp;
    }

    /** Run the thread
     *  Initiate sockets */
    public void run() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.cancelDiscovery();

        try {
            mSocket.connect();
        } catch (IOException connectException) {
            try {
                mSocket.close();
            } catch (IOException closeException) {
                Log.e("UH", "Could not close the client socket", closeException);
            }
            return;
        }
        //Connection succeeded
        Game game = Game.getInstance();
        game.mBluetoothHandler.initiateSocket(mSocket);
        game.mIsPlayerConnected = true;
    }

    /** Discard of the socket */
    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            Log.e("UH", "Could not close the client socket", e);
        }
    }
}
