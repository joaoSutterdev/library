package com.biblioteca.biblioteca.controller;

import com.biblioteca.biblioteca.controller.dto.AutorDTO;
import com.biblioteca.biblioteca.controller.dto.ErroResposta;
import com.biblioteca.biblioteca.exceptions.OperacaoNaoPermitida;
import com.biblioteca.biblioteca.exceptions.RegistroDuplicadoException;
import com.biblioteca.biblioteca.models.Autor;
import com.biblioteca.biblioteca.services.AutorService;

import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



//http://localhost:8080/autores
@RestController
@RequestMapping("autores")
@RequiredArgsConstructor
public class AutorController {

private final AutorService service;

    // public AutorController(AutorService service){
    //     this.service = service;
    // }

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestBody AutorDTO autor){
    try {
        Autor autorEntidade = autor.mapearParaAutor();
        service.salvar(autorEntidade);

    URI location = ServletUriComponentsBuilder
    .fromCurrentRequest()
    .path("/{id}")
    .buildAndExpand(autorEntidade.getId())
    .toUri();

        return ResponseEntity.created(location).build();
        
    } catch (RegistroDuplicadoException e){
        var erroDTO = ErroResposta.conflito(e.getMessage());
        return ResponseEntity.status(erroDTO.status()).body(erroDTO);

    }
    }
    
    @GetMapping("{id}") 
    public ResponseEntity<AutorDTO> obterDetalhes(@PathVariable("id") String id){
        var idAutor = UUID.fromString(id);
        Optional<Autor> autorOptional = service.obterPorId(idAutor);
        if (autorOptional.isPresent()) {
            Autor autor = autorOptional.get();
            AutorDTO dto = new AutorDTO(autor.getId(),
            autor.getNome(),
            autor.getDataNascimento(),
            autor.getNacionalidade());

            return ResponseEntity.ok(dto);
            
        }
        return ResponseEntity.notFound().build();

    }
    

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable("id") String id){
        try{


        var idAutor = UUID.fromString(id);
        Optional<Autor> autorOptional = service.obterPorId(idAutor);

        if(autorOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        service.deletar(autorOptional.get());
        return ResponseEntity.noContent().build();
    }catch(OperacaoNaoPermitida e){

        var erroResposta = ErroResposta.respostaPadrao(e.getMessage());
        return ResponseEntity.status(erroResposta.status()).body(erroResposta);
    }
    }

    @GetMapping
    public ResponseEntity<List<AutorDTO>> pesquisar(@RequestParam(value = "nome", required = false)String nome, 
    @RequestParam( value = "nacionalidade", required = false) String nacionalidade){
        List<Autor> resultado = service.pesquisa(nome, nacionalidade);
        List<AutorDTO> lista = resultado
        .stream()
        .map(autor -> new AutorDTO(
            autor.getId(), 
            autor.getNome(), 
            autor.getDataNascimento(),
            autor.getNacionalidade())
            ).collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> atualizar (@PathVariable("id") String id, @RequestBody AutorDTO dto){

        try {

        var idAutor = UUID.fromString(id);
        Optional<Autor> autorOptional = service.obterPorId(idAutor);

        if (autorOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
            
        }

        var autor = autorOptional.get();
        autor.setNome(dto.nome());
        autor.setNacionalidade(dto.nacionalidade());
        autor.setDataNascimento(dto.dataNascimento());

        service.atualizar(autor);

        return ResponseEntity.noContent().build();
    }catch (RegistroDuplicadoException e){
        var erroDTO = ErroResposta.conflito(e.getMessage());
        return ResponseEntity.status(erroDTO.status()).body(erroDTO);
    }

    }

    }
    

}
