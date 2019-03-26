package se.ju.stos1605.quizzz;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothServerConnector extends Thread{

    private final BluetoothServerSocket mServerSocket;

    private static final UUID MY_UUID = UUID.fromString("f0dd43dd-f017-43c0-86b7-a5106a1555fc");

    public BluetoothServerConnector(BluetoothAdapter adapter){
        BluetoothServerSocket tmp = null;
        try {
            tmp = adapter.listenUsingInsecureRfcommWithServiceRecord("Quizzz", MY_UUID);
        } catch (IOException e) {
            Log.e("UH", "Socket's listen method failed: ", e);
        }
        mServerSocket = tmp;
    }

    /** Starts thread and waits for an opponent to connect as client from the lobby
     * Sends the returned socket to handler and closes the server socket when a connection is initiated*/
    public void run(){
        BluetoothSocket socket = null;
        while (true) {
            try {
                socket = mServerSocket.accept();
            } catch (IOException e){
                Log.e("UH", "Socket's accept method failed: ", e);
            }

            if (socket != null) {
                sendSocketToHandler(socket);
                try {
                    mServerSocket.close();
                } catch (IOException e) {
                    Log.e("UH", "Could not close the connect socket: ", e);
                }
                break;
            }

        }
    }

    /** Sends the socket received from the server socket to the bluetooth handler */
    public void sendSocketToHandler(BluetoothSocket socket){
        Game game = Game.getInstance();
        game.mBluetoothHandler.initiateSocket(socket);
    }

    /** Close the server socket */
    public void cancel() {
        try {
            mServerSocket.close();
        } catch (IOException e) {
            Log.e("UH", "Could not cancel the connect socket: ", e);
        }
    }


}
