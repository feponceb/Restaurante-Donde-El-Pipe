package com.dondeElPipe.gestorInventario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorInventario.DTO.InventarioDTO;
import com.dondeElPipe.gestorInventario.model.CategoriaInsumo;
import com.dondeElPipe.gestorInventario.model.Inventario;
import com.dondeElPipe.gestorInventario.repository.CategoriaInsumoRepository;
import com.dondeElPipe.gestorInventario.repository.InventarioRepository;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository repo;

    @Autowired
    private CategoriaInsumoRepository catRepo;

    //--+----+----+----+----+----+----+----+----+----+----+--
    //--+----+----+----+--Crud básico--+----+----+----+----+--
    //--+----+----+----+----+----+----+----+----+----+----+--

    //ver lista completa inventario
    public List<Inventario> listar(){
        return repo.findAll();
    }

    //crear insumo
    public Inventario crearInsumo(Inventario insumo){

        // 1. Validar que la categoría seleccionada por ID exista físicamente en la BD
        // Ahora validamos directamente usando el número Integer que viene en el atributo categoriaId
        if (insumo.getCategoria() == null || !catRepo.existsById(insumo.getCategoria())) {
            return null; // Categoría inválida o no encontrada
        }
    
        // EXTRA: Normalizamos el nombre inmediatamente al entrar a la función
        String nombreLimpio = insumo.getNombreInsumo().trim().replaceAll("\\s+", " ");
    
        // 2. Ahora validamos en la BD usando el nombre ya limpio de espacios basura
        if (repo.existsByNombreInsumoIgnoreCase(nombreLimpio)) {
            return null; // Duplicado detectado correctamente
        }
    
        // 3. Pasadas las validaciones, creamos y asignamos de forma segura
        Inventario nuevoInsumo = new Inventario();
        nuevoInsumo.setNombreInsumo(nombreLimpio); // Usamos la variable limpia
        nuevoInsumo.setStockActual(insumo.getStockActual());
        nuevoInsumo.setUnidadMedida(insumo.getUnidadMedida());
        
        // OJO: Aquí asignamos el ID numérico directamente al nuevo objeto
        nuevoInsumo.setCategoria(insumo.getCategoria()); 
    
        // Al retornar el save, la base de datos devolverá el ID del insumo de forma normal
        return repo.save(nuevoInsumo);
    }
    
    //delete insumo
    public void eliminarInsumo(Integer id){
        repo.deleteById(id);
    }

    //modificar insumo
    public Inventario actualizarInsumo(Integer id, Inventario insumo){
        insumo.setId(id);
        return repo.save(insumo);
    }

    //--+----+----+----+----+----+----+----+----+----+----+--
    //--+----+----+--Funciones especiales--+----+----+----+--
    //--+----+----+----+----+----+----+----+----+----+----+--

    //buscar por id
    public Optional<Inventario> buscarId(Integer id){
        return repo.findById(id);
    }

    //buscar por id con DTO
    public InventarioDTO obtenerInsumoPorId(Integer id) {
        // 1. Buscar el insumo en la BD
        Inventario insumo = repo.findById(id).orElse(null);
        if (insumo == null) return null;

        // 2. Buscar la categoría real usando el categoriaId guardado en el insumo
        CategoriaInsumo cat = catRepo.findById(insumo.getCategoria()).orElse(null);
        String nombreCat = (cat != null) ? cat.getNombre() : "SIN CATEGORÍA";

        // 3. Armar y retornar el DTO armado con los datos mezclados
        return new InventarioDTO(
            insumo.getNombreInsumo(),
            insumo.getStockActual(),
            insumo.getUnidadMedida(),
            nombreCat // Aquí inyectamos el nombre real
        );
    }

    // ver lista completa inventario en formato DTO
    public List<InventarioDTO> listarDTO() {
        // 1. Buscamos todas las entidades base en la BD
        List<Inventario> inventarioCompleto = repo.findAll();

        // 2. Transformamos la lista original en una lista de DTOs
        return inventarioCompleto.stream().map(insumo -> {

            // Buscamos el nombre real de la categoría usando el id guardado en el insumo
            String nombreCat = catRepo.findById(insumo.getCategoria())
                                            .map(cat -> cat.getNombre())
                                            .orElse("SIN CATEGORÍA");

            // Construimos el DTO con el constructor AllArgsConstructor
            return new InventarioDTO(
                insumo.getNombreInsumo(),
                insumo.getStockActual(),
                insumo.getUnidadMedida(),
                nombreCat // Asignamos el texto real de la categoría
            );
        }).toList(); // Convertimos el flujo de vuelta a una lista
    }

}
