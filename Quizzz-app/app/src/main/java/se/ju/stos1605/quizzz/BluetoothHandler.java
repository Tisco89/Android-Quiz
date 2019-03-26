package se.ju.stos1605.quizzz;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class BluetoothHandler {

    public BluetoothCommunicator mBluetoothCommunicator;

    public BluetoothClientConnector mBluetoothClientConnector;

    public BluetoothAdapter mBluetoothAdapter;

    private BroadcastReceiver mReceiver;

    public BluetoothServerConnector mServerConnector;

    private BluetoothSocket mSocket;

    public BluetoothClientConnector mClientBluetoothConnector;

    private BluetoothDevice mConnectedDevice;

    public static final UUID MY_UUID = UUID.fromString("f0dd43dd-f017-43c0-86b7-a5106a1555fc");


    public BluetoothHandler(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /** Initiate socket and communicator */
    public void initiateSocket(BluetoothSocket socket){
        mSocket = socket;
        initiateConnector();
    }

    /** Connects as client after challenging an opponent from the lobby */
    public void connectAsClient(BluetoothDevice device) {
        this.mConnectedDevice = device;
        mClientBluetoothConnector = new BluetoothClientConnector(device);
        mClientBluetoothConnector.start();
        Game game = Game.getInstance();
        game.mIsGameInitiator = true;
    }

    /** Remove the pairing connection between two devices */
    public void unPairDevice() {
        Integer a = 2;
        if(this.mConnectedDevice == null) return;
        try {
            Method m = this.mConnectedDevice.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(this.mConnectedDevice, (Object[]) null);
        } catch (Exception e) {
            Log.e("UH", e.getMessage());
        }
    }

    /** Connects as server after being sent a game challenge from an opponent */
    public void connectAsServer(){
        mServerConnector = new BluetoothServerConnector(mBluetoothAdapter);
        mServerConnector.start();
        Game game = Game.getInstance();
        game.mIsGameInitiator = false;
    }

    /** Starts the communicator class to handle all messages */
    public void initiateConnector() {
        mBluetoothCommunicator = new BluetoothCommunicator(mSocket);
        mBluetoothCommunicator.start();
    }

    /** Determine if the device supports bluetooth */
    public boolean isBluetoothAvailable(){
        if (mBluetoothAdapter == null) {
            Log.e("UH", "No BlueTooth!");
            // Device doesn't support Bluetooth
            return false;
        }
        else return true;
    }

    /** Determine if bluetooth is activated on the device */
    public boolean isBluetoothActivated(){
        if (mBluetoothAdapter == null) {
            Log.e("UH", "No BlueTooth!");
            // Device doesn't support Bluetooth
            return false;
        }
        return mBluetoothAdapter.isEnabled();
    }

    /** Starts looking for devices */
    public void startDiscovery(Context context){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        if(mBluetoothAdapter.startDiscovery()) {
            IntentFilter iFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            context.registerReceiver(mReceiver, iFilter);
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            for(BluetoothDevice device : pairedDevices){
                String deviceName = device.getName();
        }
    }

    }
}

