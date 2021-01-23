package pe.edu.pucp.droneapp.Administrador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pe.edu.pucp.droneapp.Entity.Producto;
import pe.edu.pucp.droneapp.MainActivity;
import pe.edu.pucp.droneapp.R;
import pe.edu.pucp.droneapp.RecyclerAdapters.ProductoAdapter;

public class PagPrincipalAdministrador extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pag_principal_administrador);
        listarProductos();
    }

    ////para relacionar el layout de menú con esta vista

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_administrador, menu);
        return true;
    }

    ///para linkear las opciones del menú con una acción en particular de forma centralizada ///también puede realizarse desde el primer método onCreate pero de otra manera, revisar min 01:18:43 del video zoom

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abrirMenuTI:
                View view = findViewById(R.id.abrirMenuTI);
                PopupMenu popupMenu = new PopupMenu(this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_popup_administrador, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.verSolicitudAdmin:
                                Intent intent = new Intent(PagPrincipalAdministrador.this, SolicitudesPendientesAdmin.class);
                                startActivity(intent);

                                return true;
                            case R.id.verPedidosAdmin:
                                Intent intent1 = new Intent(PagPrincipalAdministrador.this, HistorialPrestamosAdmin.class);
                                startActivity(intent1);

                                return true;
                            case R.id.cerrarSesionAdmin:
                                logOut();
                                return true;
                            case R.id.agregarProductoAdmin:
                                Intent intent2 = new Intent(PagPrincipalAdministrador.this, AgregarItem.class);
                                startActivity(intent2);
                                return true;
                            case R.id.gestionarProductosAdmin:
                                return true;
                            default:
                                return false;

                        }
                    }
                });
                popupMenu.show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    public void logOut() {
        AuthUI instance = AuthUI.getInstance();
        instance.signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Lógica de cerrao de sesión lo pongo aquí porque luego lo ecesitaremos cuando acabemos el menú de cliente y TI
                Intent intent = new Intent(PagPrincipalAdministrador.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void listarProductos() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Productos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Producto> productoArrayList = new ArrayList<>();
                for (DataSnapshot children : snapshot.getChildren()) {
                    Producto producto = children.getValue(Producto.class);
                    productoArrayList.add(producto);
                }
                if (!productoArrayList.isEmpty()) {
                    TextView message = findViewById(R.id.textViewMessageDispo);
                    if (message.getVisibility() == View.VISIBLE) {
                        message.setVisibility(View.INVISIBLE);
                    }
                    ProductoAdapter adapter = new ProductoAdapter(productoArrayList, PagPrincipalAdministrador.this);
                    RecyclerView recyclerView = findViewById(R.id.devicesRv);
                    if (recyclerView.getVisibility() == View.INVISIBLE) {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(PagPrincipalAdministrador.this));
                } else {
                    TextView message = findViewById(R.id.textViewMessageDispo);
                    message.setVisibility(View.VISIBLE);
                    RecyclerView recyclerView = findViewById(R.id.devicesRv);
                    if (recyclerView.getVisibility() == View.VISIBLE) {
                        recyclerView.setVisibility(View.INVISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}