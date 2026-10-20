package com.landim.catalogProductions.controller;

import com.landim.catalogProductions.entity.Production;
import com.landim.catalogProductions.entity.Type;
import com.landim.catalogProductions.service.ProductionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/production")
public class ProductionController {

    private final ProductionService productionService;

    public ProductionController(ProductionService productionService) {
        this.productionService = productionService;
    }

    @GetMapping
    public ResponseEntity<List<Production>> findAll(@RequestParam(required = false) Type type, @RequestParam(required = false) Double grade,
                                                    @RequestParam(required = false) String title){
        List<Production> list = productionService.findAll(type, grade, title);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Production> findById(@PathVariable String id){
        return ResponseEntity.ok().body(productionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Production> insert(@RequestBody Production obj){
        return ResponseEntity.status(HttpStatus.CREATED).body(productionService.insert(obj));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Production> update(@RequestBody Production obj, @PathVariable String id){
        return ResponseEntity.ok().body(productionService.update(obj, id));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id){
        productionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<Production> importFromTmdb(@RequestParam String title){
        Production imported = productionService.importAndSaveFromTmdb(title);
        return ResponseEntity.status(HttpStatus.CREATED).body(imported);
    }
}