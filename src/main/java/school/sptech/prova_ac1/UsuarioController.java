package school.sptech.prova_ac1;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        if (usuarios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }

    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {

        String email = usuario.getEmail().trim().toLowerCase();
        String cpf = usuario.getCpf().trim();

        if (usuarioRepository.existsByEmail(email)){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (usuarioRepository.existsByCpf(cpf)){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Usuario usuarioCriado = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {

        return usuarioRepository.findById(id)
                .map(usuario -> ResponseEntity.status(HttpStatus.OK).body(usuario))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/filtro-data")
    public ResponseEntity<List<Usuario>> buscarPorDataNascimento(@RequestParam LocalDate nascimento) {
        List<Usuario> usuarios = usuarioRepository.findByDataNascimentoAfter(nascimento);

        if (usuarios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Integer id, @RequestBody Usuario usuario) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }

        Optional<Usuario> usuarioPorEmail = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioPorEmail.isPresent() && !usuarioPorEmail.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Optional<Usuario> usuarioPorCpf = usuarioRepository.findByCpf(usuario.getCpf());
        if (usuarioPorCpf.isPresent() && !usuarioPorCpf.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        usuario.setId(id);
        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.OK).body(usuarioAtualizado);
    }
}