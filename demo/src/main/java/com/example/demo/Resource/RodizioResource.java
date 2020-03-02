package com.example.demo.Resource;

import com.example.demo.DTO.DuplaDTO;
import com.example.demo.DTO.MembroDTO;
import com.example.demo.DTO.NovoMembroDTO;
import com.example.demo.Service.RodizioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/rodizio-api")
public class RodizioResource {

    private final RodizioService rodizioService;

    public RodizioResource(RodizioService rodizioService) {
        this.rodizioService = rodizioService;
    }

    @GetMapping("/buscar-membros")
    public ResponseEntity<List<MembroDTO>> buscarMembrosDTO() throws IOException {
        return ResponseEntity.ok(this.rodizioService.buscarMembrosDTO());
    }

    @GetMapping("/buscar-duplas")
    public ResponseEntity<List<DuplaDTO>> buscarDuplasDTO() throws IOException {
        return ResponseEntity.ok(this.rodizioService.buscarDuplasDTO());
    }

    @PostMapping("/iniciar-duplas")
    public ResponseEntity<List<DuplaDTO>> iniciarDuplas(@RequestBody DuplaDTO dupla) throws IOException {
        return ResponseEntity.ok(this.rodizioService.iniciarDuplas(dupla));
    }

    @PostMapping("/lockar-deslockar/{id}")
    public ResponseEntity<List<MembroDTO>> lockarDeslockarMembro(@PathVariable String id) throws IOException {
        return ResponseEntity.ok(this.rodizioService.lockarDeslockarMembro(id));
    }

    @GetMapping("/duplas")
    public ResponseEntity<List<DuplaDTO>> construirDuplas() throws IOException {
        return ResponseEntity.ok(this.rodizioService.construirDuplas());
    }

    @PostMapping("/save")
    public ResponseEntity<List<MembroDTO>> salvar(@RequestBody NovoMembroDTO membro) throws IOException {
        return ResponseEntity.ok(this.rodizioService.salvar(membro));
    }

    @PostMapping("/remover/{id}")
    public ResponseEntity<List<DuplaDTO>> removerMembro(@PathVariable String id) throws IOException {
        return ResponseEntity.ok(this.rodizioService.removerMembro(id));
    }

    @PostMapping("/zerar-membros")
    public ResponseEntity<Void> zerarMembros() throws IOException {
        this.rodizioService.zerarMembros();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/zerar-duplas")
    public ResponseEntity<Void> zerarDuplas() throws IOException {
        this.rodizioService.zerarDuplas();
        return ResponseEntity.ok().build();
    }
}
