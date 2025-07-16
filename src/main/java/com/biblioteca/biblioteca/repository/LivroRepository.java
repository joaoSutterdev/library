package com.biblioteca.biblioteca.repository;

import com.biblioteca.biblioteca.models.Autor;

public interface LivroRepository {

    boolean existsByAutor(Autor autor);
}
