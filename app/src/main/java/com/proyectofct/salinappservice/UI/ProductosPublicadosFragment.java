package com.proyectofct.salinappservice.UI;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.proyectofct.salinappservice.Clases.Empresa.EmpresaViewHolder;
import com.proyectofct.salinappservice.Clases.Empresa.InfoEmpresa;
import com.proyectofct.salinappservice.Clases.Productos.ListaProductosPublicadosAdapter;
import com.proyectofct.salinappservice.Clases.Productos.ProductosPublicados;
import com.proyectofct.salinappservice.Modelos.ConfiguraciónDB.ConfiguracionesGeneralesDB;
import com.proyectofct.salinappservice.Modelos.ProductosPublicadosDB;
import com.proyectofct.salinappservice.R;
import com.proyectofct.salinappservice.Utilidades.PaginationListener;

import java.util.ArrayList;

public class ProductosPublicadosFragment extends Fragment {
    private Button btAtras = null;
    private RecyclerView rvProductosPublicados;
    private ArrayList<ProductosPublicados> productosPublicados;
    private ListaProductosPublicadosAdapter listaProductosPublicadosAdapter = null;
    private int páginaActual;
    private int totalRegistros;
    private int totalPáginas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_productos_publicados, container, false);

        /*
        //BOTÓN IR ATRÁS
        btAtras = (Button) vista.findViewById(R.id.btn_Atras);
        btAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = NavHostFragment.findNavController(fragment_producto_publicado.this);
                navController.popBackStack();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };

        //requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        */

        //RECYCLER VIEW CON LOS PRODUCTOS
        totalRegistros = ProductosPublicadosDB.obtenerCantidadProductosPublicados();
        totalPáginas = (totalRegistros / ConfiguracionesGeneralesDB.ELEMENTOS_POR_PAGINA) + 1;

        Log.i("SQL", "Total de registros -> " + String.valueOf(totalRegistros));
        Log.i("SQL", "Total de páginas -> " + String.valueOf(totalPáginas));

        InfoEmpresa infoEmpresa = (InfoEmpresa)getArguments().getSerializable(EmpresaViewHolder.EXTRA_OBJETO_EMPRESA);
        String codEmpresa = infoEmpresa.getCod_empresa();

        páginaActual = 0;
        productosPublicados = ProductosPublicadosDB.obtenerProductosPublicadosPorEmpresa(páginaActual, codEmpresa);
        páginaActual = páginaActual + 1;
        if(productosPublicados != null) {
            Log.i("SQL", "Página actual -> " + String.valueOf(páginaActual));
            Log.i("SQL", "Productos publicados leídos -> " + String.valueOf(this.productosPublicados.size()));
            rvProductosPublicados = vista.findViewById(R.id.rvProductosPublicados);
            listaProductosPublicadosAdapter = new ListaProductosPublicadosAdapter(getActivity(), productosPublicados);
            rvProductosPublicados.setAdapter(listaProductosPublicadosAdapter);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                rvProductosPublicados.setLayoutManager(new LinearLayoutManager(getActivity()));
            } else {
                rvProductosPublicados.setLayoutManager(new GridLayoutManager(getActivity(), ConfiguracionesGeneralesDB.LANDSCAPE_NUM_COLUMNAS));
            }

            //PAGINACIÓN
            rvProductosPublicados.addOnScrollListener(new PaginationListener((LinearLayoutManager) rvProductosPublicados.getLayoutManager()) {
                private int productosPublicadosLeídos = 0;

                @Override
                protected void loadMoreItems() {
                    int totalRegistrosLeídos = rvProductosPublicados.getLayoutManager().getItemCount();
                    if (totalRegistrosLeídos < totalRegistros) {
                        ArrayList<ProductosPublicados> nuevosProductosPublicados = ProductosPublicadosDB.obtenerProductosPublicadosPorEmpresa(páginaActual, codEmpresa);
                        productosPublicadosLeídos = nuevosProductosPublicados.size();
                        páginaActual++;

                        Boolean resultado = rvProductosPublicados.post(new Runnable() {
                            @Override
                            public void run() {
                                ListaProductosPublicadosAdapter listaProductosPublicadosAdapter1 = (ListaProductosPublicadosAdapter) rvProductosPublicados.getAdapter();
                                ArrayList<ProductosPublicados> productosPublicadosRv = listaProductosPublicadosAdapter1.getListaProductosPublicados();
                                productosPublicadosRv.addAll(nuevosProductosPublicados);
                                rvProductosPublicados.getAdapter().notifyDataSetChanged();
                            }});
                        Log.i("SQL", "Siguiente página -> " + String.valueOf(páginaActual));
                        Log.i("SQL", "Total registros -> " + String.valueOf(totalRegistros));
                        Log.i("SQL", "Total registros leídos -> " + String.valueOf(totalRegistrosLeídos));
                    }
                    else{
                        productosPublicadosLeídos = 0;
                    }
                }
                @Override
                public boolean isLastPage() {
                    if(páginaActual > totalPáginas - 1) {
                        return true;
                    }
                    else{
                        return false;
                    }
                }
            });
        }else{
            Log.i("ERROR", "El producto publicado no se ha recibido del fragment anterior correctamente");
        }

        return vista;
    }
}