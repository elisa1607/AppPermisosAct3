package com.example.apppermisos;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    public static final int REQUEST_CODE = 1;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CODE_CAMERA = 100;


    //1. Declaracion de atributos(primitivos / objetos)
    private Button btnRequestPermission;
    private Button btnCheckPermission;
    private Button btnFingerPrint;
    private TextView tvCamera;
    private TextView tvBT;
    private TextView tvEws;
    private TextView tvRS;
    private TextView tvInternet;
    private TextView tvContac;
    private TextView tvFingerPrint;
    private TextView tvResponse;
    private BluetoothAdapter bluetoothAdapter;
    private View btnEnableBluetooth;
    private Button btnDisableBT;
    private Button btnListBTDevices;
    private TextView tvListDevices;
    private Button btnOpenCamera;
    private ImageView imgPreview;
    private TextView tvCameraStatus;
    private Uri imageUri;
    private EditText etNombreArchivo;
    private Button btnCrearArchivo;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        beginObjects();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btnCheckPermission.setOnClickListener(this::voidCheckPermission);
        btnRequestPermission.setOnClickListener(this::voidRequestPermission);
        btnEnableBluetooth.setOnClickListener(this::voidEnableBluetooth);
        btnDisableBT.setOnClickListener(this::voidDisableBluetooth);
        btnListBTDevices.setOnClickListener(this::voidListBTDevices);
        btnOpenCamera.setOnClickListener(this::voidOpenCamera);
        btnCrearArchivo.setOnClickListener(this::voidCrearArchivo);


    }


    //3. Verificar Permisos
    private void voidCheckPermission(View view) {
        int fingerPrint = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.USE_BIOMETRIC);
        int camera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int blueT = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH);
        int ews = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int internet = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET);
        int res = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int contacts = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS);
        tvFingerPrint.setText("Status Permission fingerprint: " + fingerPrint);
        tvCamera.setText("Status Permission camera: " + camera);
        tvBT.setText("Status Permission BT: " + blueT);
        tvEws.setText("Status Permission External Storage: " + ews);
        tvRS.setText("Status Permission Read Storage: " + res);
        tvInternet.setText("Status Permission Internet: " + internet);
        tvContac.setText("Status Permission Contacts: " + contacts);
        btnRequestPermission.setEnabled(true);

    }

    //5. Gestion de respuesta de solicitud de permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        tvResponse.setText(" "+grantResults[0]);;
        if (requestCode == REQUEST_CODE) {
            tvResponse.setText("  "+grantResults[0]);
            if(requestCode == REQUEST_CODE){
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    //perdir permiso nuevamente
                    new AlertDialog.Builder(this)
                            .setTitle("Alerta de Permisos")
                            .setMessage("No ha otorgado los permiso a la cámara.  Configure el permiso en ajuste")
                            .setPositiveButton("Ajustes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package",getPackageName(),null));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }).setNegativeButton("Salida", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    finish();
                                }
                            }).create().show();
                }else{

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ImageView imgPreview = findViewById(R.id.imgPreview);
            imgPreview.setImageBitmap(imageBitmap);
            imgPreview.setVisibility(View.VISIBLE); //Esto reduce el espacio sólo cuando hay imagen
        }
    }


    private void voidRequestPermission(View view) {
        //4. verificacion y solicitud  de permiso para la camara

        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
        }
    }
    //4. verificacion y solicitud  de permiso para el bluetooth
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void voidEnableBluetooth(View view) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    // Desactivar el blueooth
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void voidDisableBluetooth(View view) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
            Toast.makeText(this, "Bluetooth desactivado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Bluetooth ya está desactivado", Toast.LENGTH_SHORT).show();
        }
    }
    //Listar dispositivos bluetooth
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void voidListBTDevices(View view) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                StringBuilder builder = new StringBuilder();
                for (BluetoothDevice device : pairedDevices) {
                    builder.append("Nombre: ").append(device.getName()).append("\n");
                    builder.append("MAC: ").append(device.getAddress()).append("\n\n");
                }
                tvListDevices.setText(builder.toString());
            } else {
                tvListDevices.setText("No hay dispositivos emparejados.");
            }
        } else {
            tvListDevices.setText("Bluetooth no está activado.");
        }
    }
//abrir la camara
private void voidOpenCamera(View view) {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
    } else {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 100);
        }
    }
}



//crear archivo txt
private void voidCrearArchivo(View view) {
    String nombreArchivo = etNombreArchivo.getText().toString().trim();

    if (nombreArchivo.isEmpty()) {
        Toast.makeText(this, "Por favor ingresa un nombre para el archivo", Toast.LENGTH_SHORT).show();
        return;
    }

    // Obtener nivel de batería
    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    Intent batteryStatus = registerReceiver(null, ifilter);
    int nivel = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;

    // Obtener versión de Android
    String versionAndroid = Build.VERSION.RELEASE;

    // Texto a guardar
    String contenido = "Nombre del estudiante: " + nombreArchivo +
            "\nNivel de batería: " + nivel + "%" +
            "\nVersión de Android: " + versionAndroid;

    // Crear archivo en la carpeta Descargas
    File directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    File archivo = new File(directorio, nombreArchivo + ".txt");

    try {
        FileOutputStream fos = new FileOutputStream(archivo);
        fos.write(contenido.getBytes());
        fos.close();
        Toast.makeText(this, "Archivo creado en Descargas", Toast.LENGTH_LONG).show();
    } catch (IOException e) {
        e.printStackTrace();
        Toast.makeText(this, "Error al crear archivo", Toast.LENGTH_SHORT).show();
    }
}

    //2. Enlace de objets con la interfaz grafica e inicializacion de objetos
    private void beginObjects() {
        btnCheckPermission = findViewById(R.id.btnCheckPermission);
        btnRequestPermission = findViewById(R.id.btnRequestPermission);
        tvBT = findViewById(R.id.tvBT);
        tvCamera = findViewById(R.id.tvCamera);
        tvEws = findViewById(R.id.tvEws);
        tvRS = findViewById(R.id.tvRS);
        tvInternet = findViewById(R.id.tvInternet);
        tvResponse = findViewById(R.id.tvResponse);
        tvContac = findViewById(R.id.tvContactos);
        tvFingerPrint = findViewById(R.id.tvDactilar);
        btnRequestPermission.setEnabled(false);
        btnEnableBluetooth = findViewById(R.id.btnEnableBluetooth);
        btnDisableBT = findViewById(R.id.btnDisableBT);
        btnListBTDevices = findViewById(R.id.btnListBTDevices);
        tvListDevices = findViewById(R.id.tvListDevices);
        btnOpenCamera = findViewById(R.id.btnOpenCamera);
        imgPreview = findViewById(R.id.imgPreview);
        etNombreArchivo = findViewById(R.id.etNombreArchivo);
        btnCrearArchivo = findViewById(R.id.btnCrearArchivo);

    }

}





