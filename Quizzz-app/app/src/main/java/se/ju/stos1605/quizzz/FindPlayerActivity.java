package se.ju.stos1605.quizzz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class FindPlayerActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;

    private static boolean mDiscovering = false;

    ArrayList<BluetoothDevice> mDiscoveredDevices;

    ArrayList<BluetoothDevice> mDiscoveredPhones;

    Boolean mIsListeningToBL = true;

    Boolean mHasSentName;

    int mAnswerCounter = 0;

    int DISCONNECTEDOPPONENTTIMER = 75;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_player);
        mDiscoveredDevices = new ArrayList<>();
        mDiscoveredPhones = new ArrayList<>();
        listenForBluetoothData();
        mIsListeningToBL = true;
        mAnswerCounter = 0;
        mHasSentName = false;

        Game game =  Game.getInstance();
        startBluetooth();
        Toast.makeText(getApplicationContext(), R.string.search, Toast.LENGTH_SHORT).show();
        game.mBluetoothHandler.connectAsServer();
        ListView listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View rowView, int position, long id) {
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            BluetoothDevice device;

            for (BluetoothDevice d : mDiscoveredPhones){
                if (adapterView.getItemAtPosition(position).toString().contains(d.getName())) {
                    //Connecting to device
                    device = d;
                    Game game = Game.getInstance();
                    game.setOpponentName(device.getName());
                    game.mIsGameInitiator = true;
                    game.mBluetoothHandler.connectAsClient(device);
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                    waitUntilConnectedAsClient();
                    mIsListeningToBL = false;
                    break;
                }
            }
        }
        });


    /**Pull list down to restart/refresh search of bluetooth devices*/
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.swipeRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Toast.makeText(getApplicationContext(), R.string.search, Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startBluetooth();
                        pullToRefresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });

    }

    /**After clicking to connect to player in list, waits for it to respond, otherwise cancel request after 15 seconds*/
    private void waitUntilConnectedAsClient(){
        Game game = Game.getInstance();
        if (!game.mIsPlayerConnected){
            mAnswerCounter += 1;
            if(mAnswerCounter >= DISCONNECTEDOPPONENTTIMER ){
                String string = this.getResources().getString(R.string.opponent_didnt_respond);
                game.resetGame();
                showAlert(string);
                return;
            }
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    waitUntilConnectedAsClient();
                }
            }, 200);
        }
        else{
            mAnswerCounter = 0;
            Intent myIntent = new Intent(this, SelectDifficultyActivity.class);
            startActivity(myIntent);
        }
    }
    /** Starts listening for data over bluetooth in lobby, recursive function that is called async
     * every 200ms.
     * */
    private void listenForBluetoothData(){
        if(!mIsListeningToBL) return;
        Game game = Game.getInstance();
        if(game.mErrorHasOccured){
            String errorString = this.getResources().getString(R.string.error_fetching_questions);
            game.resetGame();
            showAlert(errorString);
            return;
        }
        if ( ! game.mIsBluetoothDataReady){
            if (game.mIsPlayerConnected){
                if (!mHasSentName){
                    BluetoothCommunicator communicator = game.mBluetoothHandler.mBluetoothCommunicator;
                    communicator.sendNameToOpponent(communicator.buildStringFromName(game.mMyName));
                    mHasSentName = true;
                }
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            }
            //Call this func again in 200ms
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    listenForBluetoothData();
                }
            }, 200);
        }
        else {
            //Data ready
            Intent intent = new Intent(this, QuestionActivity.class);
            startActivity(intent);
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDiscoveredDevices = new ArrayList<>();
                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mDiscovering = false;
                //discovery finishes, dismiss progress dialog
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getName() != null){

                    if(! mDiscoveredDevices.contains(device))
                        mDiscoveredDevices.add(device);

                }
            }
        }
    };

    /***/
    @SuppressLint("NewApi")
    private void startBluetooth(){
        Game game = Game.getInstance();
        if (!game.mBluetoothHandler.isBluetoothAvailable()) {
            String errorString = this.getResources().getString(R.string.no_bluetooth);
            showAlert(errorString);
        }else if (!game.mBluetoothHandler.isBluetoothActivated()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
                @SuppressLint({"NewApi", "LocalSuppress"}) int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
                permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
                if(permissionCheck != 0){
                    this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
                }
            }
            BluetoothAdapter adapter = game.mBluetoothHandler.mBluetoothAdapter;
            IntentFilter filter = new IntentFilter();

            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

            Set<BluetoothDevice> all_devices = adapter.getBondedDevices();
            if (all_devices.size() > 0) {
                mDiscoveredDevices.addAll(all_devices);
            }
            String manufacturer = android.os.Build.MANUFACTURER;
            if (manufacturer.equals("samsung")){
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE),1);
                Toast.makeText(getApplicationContext(),R.string.discoverable,Toast.LENGTH_SHORT).show();
            }
            registerReceiver(mReceiver, filter);
            adapter.startDiscovery();
            mDiscovering = true;
            updateListOfPlayers();

        }

    }

    /**Updates list of discovered devices, only smartphones will be shown in list*/
    private void updateListOfPlayers(){
        ArrayList<String> listOfPlayers = new ArrayList<>();
        for (BluetoothDevice d : mDiscoveredDevices){
            if(d.getBluetoothClass().getDeviceClass()==(BluetoothClass.Device.PHONE_SMART)) {
                listOfPlayers.add(d.getName());
                mDiscoveredPhones.add(d);
            }
        }
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listOfPlayers));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mDiscovering)
                    updateListOfPlayers();
            }
        }, 200);

    }

    /**On activity result for bluetooth activation */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    startBluetooth();
                } else {
                    showAlert(this.getResources().getString(R.string.location_warning));
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }
    /** Alertdialog that takes string as parameter, string contains message*/
    private void showAlert(String string){
        AlertDialog alertDialog = new AlertDialog.Builder(FindPlayerActivity.this).create();
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(string);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        navigateUpTo(new Intent(getBaseContext(), MainActivity.class));
                    }
                });
        alertDialog.show();

    }
    /** Overrides the back-button so that the user will be going back to startpage
     * resets game */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        navigateUpTo(new Intent(getBaseContext(), MainActivity.class));
        Game game = Game.getInstance();
        game.resetGame();
        finish();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
