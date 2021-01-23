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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import pe.edu.pucp.droneapp.Entity.GlideApp;
import pe.edu.pucp.droneapp.Entity.Producto;
import pe.edu.pucp.droneapp.R;

public class EditarProductoAdmin extends AppCompatActivity {
    Producto producto = new Producto();
    Uri uri = null;
    StorageReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_producto_admin);

        Intent intent = getIntent();
        producto = (Producto) intent.getSerializableExtra("producto");
        ImageView imagen = findViewById(R.id.imageViewFotoEdit);
        reference = FirebaseStorage.getInstance().getReference().child(producto.getPrimaryKeyProducto() + "/" + producto.getNombreFotoProducto());
        GlideApp.with(this).load(reference).into(imagen);
        TextView textView = findViewById(R.id.textViewTipoEdit);
        textView.setText("Tipo: " + producto.getTipoProducto());
        EditText editCaracteristica = findViewById(R.id.editTextCaracteristicasEdit);
        editCaracteristica.setText(producto.getCaracteristicaProducto());
        EditText editTextMarcaEdit = findViewById(R.id.editTextMarcaEdit);
        editTextMarcaEdit.setText(producto.getMarcaProducto());
        EditText editTextInluyeEdit = findViewById(R.id.editTextInluyeEdit);
        editTextInluyeEdit.setText(producto.getIncluyeProducto());
        EditText editTextStockEdit = findViewById(R.id.editTextStockEdit);
        editTextStockEdit.setText(String.valueOf(producto.getStockProducto()));
        EditText editTextUbicacion = findViewById(R.id.editTextUbicacionEdit);
        editTextUbicacion.setText(String.valueOf(producto.getUbicacionProducto()));
    }

    public void limpiarImagen(View view) {

        TextView textViewFoto = findViewById(R.id.textViewNombreFotoEdit);
        if (textViewFoto.getVisibility() == View.VISIBLE) {
            textViewFoto.setVisibility(View.INVISIBLE);
        }
        ImageView imagen = findViewById(R.id.imageViewFotoEdit);
        if (imagen.getVisibility() != View.VISIBLE) {
            imagen.setVisibility(View.VISIBLE);
        }
        if (uri != null) {
            Log.d("infoApp", "PK: " + producto.getPrimaryKeyProducto());
            Glide.with(this).load(reference).into(imagen);
        }
        uri = null;
    }

    public void pickFileEdit(View view) {
        ImageView foto = findViewById(R.id.imageViewFotoEdit);
        if (foto.getVisibility() == View.VISIBLE) {
            foto.setVisibility(View.INVISIBLE);
        }
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Seleccione Foto para subir"), 10);

    }


    public void tomarFotoEdit(View view) {
        TextView textViewFoto = findViewById(R.id.textViewNombreFotoEdit);
        if (textViewFoto.getVisibility() == View.VISIBLE) {
            textViewFoto.setVisibility(View.INVISIBLE);
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
                    ImageView foto = findViewById(R.id.imageViewFotoEdit);
                    foto.setVisibility(View.INVISIBLE);
                    uri = data.getData();
                    String fileName = getFileNameEdit(uri);
                    TextView textView = findViewById(R.id.textViewNombreFotoEdit);
                    textView.setText(fileName);
                    producto.setNombreFotoProducto(fileName);
                    textView.setVisibility(View.VISIBLE);
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    TextView textView = findViewById(R.id.textViewNombreFotoEdit);
                    textView.setVisibility(View.INVISIBLE);
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    ImageView foto = findViewById(R.id.imageViewFotoEdit);
                    foto.setVisibility(View.VISIBLE);
                    foto.setImageBitmap(bitmap);
                    guardarFotoTomadaEdit(bitmap);
                }
                break;
        }
    }

    public void guardarFotoTomadaEdit(Bitmap bitmap) {
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

    public String getFileNameEdit(Uri uri) {
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

    public void guardarDispositivoEdit(View view) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        EditText editTextMarca = findViewById(R.id.editTextMarcaEdit);
        EditText editTextTextCaracteristicas = findViewById(R.id.editTextCaracteristicasEdit);
        EditText editTextTextIncluye = findViewById(R.id.editTextInluyeEdit);
        EditText editTextNumberStock = findViewById(R.id.editTextStockEdit);
        EditText editTextUbicacion = findViewById(R.id.editTextUbicacionEdit);
        final TextView textViewFoto = findViewById(R.id.textViewFoto);

        if (editTextMarca.getText().toString().trim().isEmpty()) {
            editTextMarca.setError("Este campo no puede ser vacío");
        } else {
            if (editTextTextCaracteristicas.getText().toString().trim().isEmpty() || editTextTextCaracteristicas.getText().toString().trim().length() > 25) {
                editTextTextCaracteristicas.setError("de 1 a 25 caracteres");
            } else {
                if (editTextTextIncluye.getText().toString().trim().isEmpty() || editTextTextIncluye.getText().toString().trim().length() > 25) {
                    editTextTextIncluye.setError("de 1 a 25 caracteres");
                } else {
                    if (editTextNumberStock.getText().toString().trim().isEmpty() || editTextNumberStock.getText().toString().trim().length() > 9) {
                        editTextNumberStock.setError("de 1 a 9 dígitos");
                    } else {
                        producto.setStockProducto(Integer.parseInt(editTextNumberStock.getText().toString().trim()));
                        producto.setIncluyeProducto(editTextTextIncluye.getText().toString().trim());
                        producto.setCaracteristicaProducto(editTextTextCaracteristicas.getText().toString().trim());
                        producto.setMarcaProducto(editTextMarca.getText().toString().trim());
                        producto.setUbicacionProducto(editTextUbicacion.getText().toString().trim());
                        databaseReference.child("Productos/" + producto.getPrimaryKeyProducto()).setValue(producto)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("infoApp", "GUARDADO EXITOSO EN TU DATABASE");
                                        if (uri != null) {
                                            if (textViewFoto.getVisibility() == View.VISIBLE) {
                                                subirArchivoConPutFileEdit(textViewFoto.getText().toString());
                                            } else {
                                                subirArchivoConPutFileEdit(producto.getNombreFotoProducto());
                                            }
                                        } else {
                                            Intent intent = new Intent(EditarProductoAdmin.this, PagPrincipalAdministrador.class);
                                            startActivity(intent);
                                            finish();
                                            Toast.makeText(EditarProductoAdmin.this, "Producto editado exitosamente", Toast.LENGTH_SHORT).show();
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

    public void subirArchivoConPutFileEdit(String fileName) {
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
                    Intent intent = new Intent(EditarProductoAdmin.this, PagPrincipalAdministrador.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(EditarProductoAdmin.this, "Producto editado exitosamente", Toast.LENGTH_SHORT).show();
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("JULIO", "error en la subida");
                    Toast.makeText(EditarProductoAdmin.this, "Error Al subir, vuelva a intentar", Toast.LENGTH_SHORT).show();
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