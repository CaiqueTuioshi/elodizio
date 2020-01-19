package com.example.demo.Resource;

import com.example.demo.DTO.DuplaDTO;
import com.example.demo.DTO.MembroDTO;
import com.example.demo.Service.RodizioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/rodizio-api")
public class RodizioResource {

    private final RodizioService rodizioService;

    public RodizioResource(RodizioService testeService) {
        this.rodizioService = testeService;
    }

    @GetMapping("/buscar-membros")
    public ResponseEntity<List<MembroDTO>> buscarMembrosDTO() throws IOException {
        return ResponseEntity.ok(this.rodizioService.buscarMembrosDTO());
    }

    @PostMapping("/iniciar-duplas")
    public ResponseEntity<List<String>> iniciarDuplas(@RequestBody DuplaDTO dupla) throws IOException {
        return ResponseEntity.ok(this.rodizioService.iniciarDuplas(dupla));
    }

    @GetMapping("/duplas")
    public ResponseEntity<List<String>> construirDuplas() throws IOException {
        return ResponseEntity.ok(this.rodizioService.construirDuplas());
    }

    @PostMapping("/save")
    public ResponseEntity<List<String>> salvar(@RequestBody String membro) throws IOException {
        return ResponseEntity.ok(this.rodizioService.salvar(membro));
    }

    @DeleteMapping("/remover/{idMembro}")
    public ResponseEntity<List<String>> remover(@PathVariable int idMembro) throws IOException {
        return ResponseEntity.ok(this.rodizioService.remover(idMembro));
    }
}
