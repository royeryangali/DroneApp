package pe.edu.pucp.droneapp.RecyclerAdapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import pe.edu.pucp.droneapp.Administrador.EditarProductoAdmin;
import pe.edu.pucp.droneapp.Entity.GlideApp;
import pe.edu.pucp.droneapp.Entity.Producto;
import pe.edu.pucp.droneapp.Entity.ProductoUser;
import pe.edu.pucp.droneapp.R;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {
    private ArrayList<Producto> listadeProductos;
    private Context context;

    public ProductoAdapter(ArrayList<Producto> listadeProductos, Context context) {
        this.listadeProductos = listadeProductos;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.producto_rv,parent,false);
        ProductoViewHolder productoViewHolder = new ProductoViewHolder(itemView);
        return productoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        final Producto producto = listadeProductos.get(position);
        StorageReference reference =
                FirebaseStorage.getInstance().getReference().child(producto.getPrimaryKeyProducto()+"/"+producto.getNombreFotoProducto());
        GlideApp.with(context).load(reference).into(holder.imagen);
        holder.tipo.setText("Tipo: "+producto.getTipoProducto()+" - Marca: "+ producto.getMarcaProducto());
        holder.caracteristica.setText("Características: "+ producto.getCaracteristicaProducto());
        holder.incluye.setText("Incluye: "+ producto.getIncluyeProducto());
        holder.ubicacion.setText("Ubicación: " + producto.getUbicacionProducto());
        holder.stock.setText("Stock: "+ String.valueOf(producto.getStockProducto()));
        holder.editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditarProductoAdmin.class);
                intent.putExtra("producto", producto);
                context.startActivity(intent);
            }
        });
        holder.borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Solicitudes/").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<ProductoUser> productoUserArrayList = new ArrayList<>();
                        for (DataSnapshot children : snapshot.getChildren()) {
                            ProductoUser productoUser = children.getValue(ProductoUser.class);
                            if (productoUser.getProducto().getPrimaryKeyProducto().equalsIgnoreCase(producto.getPrimaryKeyProducto())) {
                                if (productoUser.getEstado().equalsIgnoreCase("Pendiente")) {
                                    productoUserArrayList.add(productoUser);
                                }

                            }

                        }
                        if (!productoUserArrayList.isEmpty()) {
                            Toast.makeText(context, "Se tienen Solicitudes Pendientes", Toast.LENGTH_SHORT).show();
                        }else{
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("Productos/"+producto.getPrimaryKeyProducto()).setValue(null)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("infoApp","BORRADO EXITOSO EN TU DATABASE");
                                            Toast.makeText(context, "Producto editado exitosamente", Toast.LENGTH_SHORT).show();

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
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return listadeProductos.size();
    }

    public static  class ProductoViewHolder extends RecyclerView.ViewHolder{
        TextView tipo;
        TextView caracteristica;
        TextView stock;
        TextView incluye;
        TextView ubicacion;
        ImageView imagen;
        Button borrar;
        Button editar;
        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tipo = itemView.findViewById(R.id.textViewTipoMode);
            caracteristica = itemView.findViewById(R.id.textViewCaracteristica);
            stock = itemView.findViewById(R.id.textViewStock);
            incluye = itemView.findViewById(R.id.textViewIncluye);
            imagen = itemView.findViewById(R.id.imageViewDevice);
            borrar = itemView.findViewById(R.id.buttonborrar);
            editar = itemView.findViewById(R.id.buttonEditar);
            ubicacion = itemView.findViewById(R.id.textViewUbicacionProducto);
        }
    }
}
