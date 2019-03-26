package se.ju.stos1605.quizzz;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class ChooseNameActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_name);
        checkPermission();
        enableBluetooth();
    }

    /**Try to enable bluetooth*/
    private void enableBluetooth(){
        Game game = Game.getInstance();
        if (!game.mBluetoothHandler.isBluetoothAvailable()) {
            showAlert(this.getResources().getString(R.string.no_bluetooth));
        }else if (!game.mBluetoothHandler.isBluetoothActivated()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 0){
            showAlert(this.getResources().getString(R.string.no_bluetooth));
        }

    }

    /*Checks that the app has all the permissions that is needed*/
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(ChooseNameActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ChooseNameActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }
    }

    /**Checks what response the ask for permission popup got, if permission got denied a warning will be showed
     * for the user and will be sent back to the home screen*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) { } else {
                    showAlert(this.getResources().getString(R.string.location_warning));
                }
            }
        }
    }

    /** Shows an alertdialog as popup with a String as message
     * Message is sent with including string.xml line from the function that is using it */
    private void showAlert(String string){
        AlertDialog alertDialog = new AlertDialog.Builder(ChooseNameActivity.this).create();
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

    /** "Go to Lobby" button is pressed
     * hides keyboard
     * starts new Game
     * sets name
     * shows loading screen before going to next activity*/
    public void findPlayerButtonClicked(View view) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        findViewById(R.id.loadingPanel1).setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, FindPlayerActivity.class);

        EditText editableText = findViewById(R.id.enteredName);
        String enteredName = editableText.getText().toString();
        Game game = new Game();
        Game activeGame = Game.getInstance();
        activeGame.setMyName(enteredName);
        startActivity(intent);
    }
}
