package projet.td.controllers;

import projet.td.models.dao.Account;
import projet.td.models.dto.AccountDto;
import projet.td.models.mappers.AccountMapper;
import projet.td.services.AccountService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountMapper mapper;
    private final AccountService service;

    @PostMapping("/accounts")
    public ResponseEntity<Account> saveAccount(@RequestBody @Valid AccountDto request){
        return ResponseEntity.ok(service.saveAccount(mapper.toEntity(request)));
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Integer id){
        return ResponseEntity.ok(service.getAccountById(id));
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccount(){
        return ResponseEntity.ok(service.getAllAccount());
    }

    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id){
        service.deleteAccoundById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/hello")
    public String accountSayHello(@RequestBody AccountDto request){
        return service.sayHello(mapper.toEntity(request));
    }

    @GetMapping("/hello")
    public String sayHello(){
        return "HELLO WORLD";
    }
}