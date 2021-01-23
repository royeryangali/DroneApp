package pe.edu.pucp.droneapp.Administrador;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.OutputStream;

import pe.edu.pucp.droneapp.Entity.Producto;
import pe.edu.pucp.droneapp.R;

public class AgregarItem extends AppCompatActivity {

    Producto producto = new Producto();
    Uri uri;
    EditText otro;
    boolean cond1, cond2, cond3, cond4 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_item);
        otro = findViewById(R.id.editTextTextOtro);
        String[] lista = {"Postre", "Bebida", "Tablet", "Laptop", "Otro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lista);
        Spinner spinner = findViewById(R.id.spinnerTipo);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("infoApp", "SELECCIONASTE ESTO : " + parent.getItemAtPosition(position).toString());
                if (position == 0) {
                    producto.setTipoProducto("Postre");
                    if (otro.getVisibility() == View.VISIBLE) {
                        otro.setVisibility(View.INVISIBLE);
                    }
                } else if (position == 1) {
                    producto.setTipoProducto("Bebida");
                    if (otro.getVisibility() == View.VISIBLE) {
                        otro.setVisibility(View.INVISIBLE);
                    }
                } else if (position == 2) {
                    producto.setTipoProducto("Tablet");
                    if (otro.getVisibility() == View.VISIBLE) {
                        otro.setVisibility(View.INVISIBLE);
                    }
                } else if (position == 3) {
                    producto.setTipoProducto("Laptop");
                    if (otro.getVisibility() == View.VISIBLE) {
                        otro.setVisibility(View.INVISIBLE);
                    }
                } else if (position == 4) {
                    producto.setTipoProducto("Otro");
                    otro.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }


    public void pickFile(View view) {
        ImageView foto = findViewById(R.id.imageViewFoto);
        if (foto.getVisibility() == View.VISIBLE) {
            foto.setVisibility(View.GONE);
        }
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Seleccione Foto para subir"), 10);

    }

    public void tomarFoto(View view) {
        TextView textViewFoto = findViewById(R.id.textViewFoto);
        if (textViewFoto.getVisibility() == View.VISIBLE) {
            textViewFoto.setVisibility(View.GONE);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {
                    uri = data.getData();
                    String fileName = getFileName(uri);
                    TextView textView = findViewById(R.id.textViewFoto);
                    textView.setText(fileName);
                    producto.setNombreFotoProducto(fileName);
                    textView.setVisibility(View.VISIBLE);
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    ImageView foto = findViewById(R.id.imageViewFoto);
                    foto.setVisibility(View.VISIBLE);
                    foto.setImageBitmap(bitmap);
                    guardarFotoTomada(bitmap);
                }
                break;
        }
    }

    public void guardarFotoTomada(Bitmap bitmap) {
        producto.setNombreFotoProducto("prueba.jpg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, producto.getNombreFotoProducto());
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void guardarDispositivo(View view) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        EditText editTextMarca = findViewById(R.id.editTextTextMarca);
        EditText editTextTextCaracteristicas = findViewById(R.id.editTextTextCaracteristicas);
        EditText editTextTextIncluye = findViewById(R.id.editTextTextIncluye);
        EditText editTextNumberStock = findViewById(R.id.editTextNumberStock);
        EditText editTextUbicacion = findViewById(R.id.editTextUbicacion);
        TextView textView = findViewById(R.id.textViewFoto);
        ImageView foto = findViewById(R.id.imageViewFoto);
        if (textView.getVisibility() == View.INVISIBLE && foto.getVisibility() == View.INVISIBLE) {
            Toast.makeText(AgregarItem.this, "Debe escoger o tomar una foto", Toast.LENGTH_SHORT).show();
        } else {
            if (editTextMarca.getText().toString().trim().isEmpty()) {
                editTextMarca.setError("Este campo no puede ser vacío");
            } else {
                if (editTextTextCaracteristicas.getText().toString().trim().isEmpty() || editTextTextCaracteristicas.getText().toString().trim().length() > 25) {
                    editTextTextCaracteristicas.setError("de 1 a 25 caracteres");
                } else {
                    if (editTextTextIncluye.getText().toString().trim().isEmpty() || editTextTextIncluye.getText().toString().trim().length() > 25) {
                        editTextTextIncluye.setError("de 1 a 25 caracteres");
                    } else {
                        if (editTextUbicacion.getText().toString().trim().isEmpty() || editTextUbicacion.getText().toString().trim().length() > 25) {
                            editTextUbicacion.setError("de 1 a 25 caracteres");

                        } else {
                            if (editTextNumberStock.getText().toString().trim().isEmpty() || editTextNumberStock.getText().toString().trim().length() > 9) {
                                editTextNumberStock.setError("de 1 a 9 dígitos");
                            } else {
                                if (producto.getTipoProducto().equalsIgnoreCase("Otro") && otro.getText().toString().trim().isEmpty()) {
                                    otro.setError("Este campo no puede ser vacío");
                                } else {
                                    producto.setStockProducto(Integer.parseInt(editTextNumberStock.getText().toString().trim()));
                                    producto.setMarcaProducto(editTextMarca.getText().toString().trim());
                                    producto.setCaracteristicaProducto(editTextTextCaracteristicas.getText().toString().trim());
                                    producto.setIncluyeProducto(editTextTextIncluye.getText().toString().trim());
                                    producto.setUbicacionProducto(editTextUbicacion.getText().toString().trim());
                                    final TextView textViewFoto = findViewById(R.id.textViewFoto);
                                    if (producto.getTipoProducto().equalsIgnoreCase("Otro")) {
                                        producto.setTipoProducto("Otro (" + otro.getText().toString().trim() + ")");
                                    }
                                    String mypk = databaseReference.push().getKey();
                                    producto.setPrimaryKeyProducto(mypk);
                                    databaseReference.child("Productos/" + producto.getPrimaryKeyProducto()).setValue(producto)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("JULIO", "GUARDADO EXITOSO EN TU DATABASE");

                                                    if (textViewFoto.getVisibility() == View.VISIBLE) {
                                                        subirArchivoConPutFile(textViewFoto.getText().toString());
                                                    } else {
                                                        subirArchivoConPutFile(producto.getNombreFotoProducto());
                                                    }

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    e.printStackTrace();
                                                }
                                            });
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    public void subirArchivoConPutFile(String fileName) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            //subir archivo a firebase storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            StorageMetadata storageMetadata = new StorageMetadata.Builder()
                    .setCustomMetadata("autor", firebaseUser.getDisplayName())
                    .setCustomMetadata("pk", producto.getPrimaryKeyProducto())
                    .build();

            UploadTask task = storageReference.child(producto.getPrimaryKeyProducto() + "/" + producto.getNombreFotoProducto()).putFile(uri, storageMetadata);


            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("infoApp", "subida exitosa");
                    Intent intent = new Intent(AgregarItem.this, PagPrincipalAdministrador.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(AgregarItem.this, "Producto agregado exitosamente", Toast.LENGTH_SHORT).show();
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("infoApp", "error en la subida");
                    e.printStackTrace();
                }
            });
            task.addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                }
            });
            task.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    long bytesTransferred = snapshot.getBytesTransferred();
                    long totalByteCount = snapshot.getTotalByteCount();
                    double progreso = (100.0 * bytesTransferred) / totalByteCount;
                    Log.d("infoApp", String.valueOf(progreso));
                }
            });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            Log.d("infoApp", "SIN PERMISOOOOOOOOOOOOOOOOOO");
        }
    }
}