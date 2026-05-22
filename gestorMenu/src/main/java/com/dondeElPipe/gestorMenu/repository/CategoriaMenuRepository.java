package com.dondeElPipe.gestorMenu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dondeElPipe.gestorMenu.model.CategoriaMenu;

public interface CategoriaMenuRepository extends JpaRepository<CategoriaMenu, Integer>{

    boolean existsByNombreIgnoreCase(String nombre);
}
