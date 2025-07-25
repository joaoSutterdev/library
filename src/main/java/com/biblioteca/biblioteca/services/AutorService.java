package com.biblioteca.biblioteca.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.biblioteca.biblioteca.exceptions.OperacaoNaoPermitida;
import com.biblioteca.biblioteca.models.Autor;
import com.biblioteca.biblioteca.models.Livro;
import com.biblioteca.biblioteca.repository.AutorRepository;
import com.biblioteca.biblioteca.repository.LivroRepository;
import com.biblioteca.biblioteca.validator.AutorValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutorService {

    private final AutorRepository repository;
    private final AutorValidator validator;
    private final LivroRepository livroRepository;
    

    // public AutorService(AutorRepository repository, AutorValidator validator, LivroRepository livroRepository) {
    //     this.repository = repository;
    //     this.validator = validator;
    //     this.livroRepository = livroRepository;
    // }
    
    public Autor salvar(Autor autor){
        validator.validar(autor);
        return repository.save(autor);
    }

    public Optional<Autor> obterPorId(UUID id){
        return repository.findById(id);
    }

    public void deletar (Autor autor){
        if (possuiLivro(autor)) {
            throw new OperacaoNaoPermitida("Autor possui livros cadastrados");
            
        }
        repository.delete(autor);
    }

    public void atualizar (Autor autor){
        if (autor.getId() == null) {
            throw new IllegalArgumentException("Para atualizar é necessário que o autor já esteja salvo na base");
            
        }
        validator.validar(autor);
        repository.save(autor);
    }

    public List<Autor> pesquisa (String nome, String nacionalidade ){

        if (nome != null && nacionalidade != null) {
            return repository.findByNomeAndNacionalidade(nome, nacionalidade);

        }
        if (nome != null) {
            return repository.findByNome(nome);
            
        }
        if (nacionalidade != null) {
            return repository.findByNacionalidade(nacionalidade);
            
        }

        return repository.findAll();
    }

    public Boolean possuiLivro(Autor autor){
        return livroRepository.existsByAutor(autor);
    
    }
}
