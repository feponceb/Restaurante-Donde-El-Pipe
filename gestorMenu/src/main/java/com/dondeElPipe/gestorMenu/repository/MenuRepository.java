package com.dondeElPipe.gestorMenu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dondeElPipe.gestorMenu.model.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer>{

    //Optional<Menu> findByNameIgnoreCase(String nombrePlato);

    //Boolean existsByName(String nombrePlato);

}
