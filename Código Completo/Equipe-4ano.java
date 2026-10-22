package com.ufv.BitTInt.controller;

import com.ufv.BitTInt.model.Aluno;
import com.ufv.BitTInt.model.Desenho;
import com.ufv.BitTInt.repository.AlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// --- IMPORTAÇÃO ADICIONADA ---
import org.springframework.web.bind.annotation.CrossOrigin;

// --- ANOTAÇÃO ADICIONADA ---
@CrossOrigin(origins = "http://localhost:3000") // Permite requisições do seu App React
@RestController
@RequestMapping("/alunos")
public class AlunoController {

    @Autowired
    private AlunoRepository alunoRepository;

    @PostMapping
    public Aluno cadastrarAluno(@RequestBody Aluno aluno) {
        return alunoRepository.save(aluno);
    }

    @GetMapping
    public List<Aluno> buscarTodosAlunos() {
        return alunoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aluno> buscarAlunoPorId(@PathVariable Long id) {
        return alunoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/login")
    public ResponseEntity<String> validarLogin(@RequestBody Aluno dadosLogin) {
        Optional<Aluno> alunoOptional = alunoRepository.findByNickname(dadosLogin.getNickname());
        if (alunoOptional.isPresent() && alunoOptional.get().getSenha().equals(dadosLogin.getSenha())) {
            return ResponseEntity.ok("Login bem-sucedido!");
        }
        return ResponseEntity.status(401).body("Nickname ou senha inválidos.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarAluno(@PathVariable Long id) {
        if (!alunoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        alunoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{alunoId}/desenhos")
    public ResponseEntity<Aluno> adicionarDesenho(@PathVariable Long alunoId) {
        Optional<Aluno> alunoOptional = alunoRepository.findById(alunoId);
        if (alunoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Aluno aluno = alunoOptional.get();
        aluno.inserirDesenho(new Desenho());
        Aluno alunoAtualizado = alunoRepository.save(aluno);

        return ResponseEntity.ok(alunoAtualizado);
    }
}package com.ufv.BitTInt.controller;

import com.ufv.BitTInt.model.Aluno;
import com.ufv.BitTInt.model.Turma;
import com.ufv.BitTInt.repository.AlunoRepository;
import com.ufv.BitTInt.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// --- IMPORTAÇÃO ADICIONADA ---
import org.springframework.web.bind.annotation.CrossOrigin;

// --- ANOTAÇÃO ADICIONADA ---
@CrossOrigin(origins = "http://localhost:3000") // Permite requisições do seu App React
@RestController
@RequestMapping("/turmas")
public class TurmaController {

    @Autowired
    private TurmaRepository turmaRepository;
    @Autowired
    private AlunoRepository alunoRepository;

    @PostMapping
    public Turma cadastrarTurma(@RequestBody Turma turma) {
        return turmaRepository.save(turma);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Turma> buscarTurmaPorId(@PathVariable Long id) {
        return turmaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{turmaId}/alunos/{alunoId}")
    public ResponseEntity<Turma> inserirAlunoNaTuma(@PathVariable Long turmaId, @PathVariable Long alunoId) {
        Optional<Turma> turmaOpt = turmaRepository.findById(turmaId);
        Optional<Aluno> alunoOpt = alunoRepository.findById(alunoId);

        if (turmaOpt.isPresent() && alunoOpt.isPresent()) {
            Turma turma = turmaOpt.get();
            Aluno aluno = alunoOpt.get();
            turma.inserirAluno(aluno);
            turmaRepository.save(turma);
            return ResponseEntity.ok(turma);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/porNome/{nomeTurma}")
    public ResponseEntity<Turma> buscarTurmaPorNome(@PathVariable String nomeTurma) {
        return turmaRepository.findByNomeTurma(nomeTurma)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}package com.ufv.BitTInt.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String senha;

    private int pontuacaoTotal;
    private int faseAtual;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "aluno_pontuacao_fases", joinColumns = @JoinColumn(name = "aluno_id"))
    @Column(name = "pontuacao")
    private List<Integer> pontuacaoFases;

    // --- ATUALIZAÇÃO: Relação com a entidade Desenho ---
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "aluno_id")
    private List<Desenho> desenhos;

    public Aluno() {
    }

    public Aluno(String nickname, String senha) {
        this.nickname = nickname;
        this.senha = senha;
        this.pontuacaoTotal = 0;
        this.faseAtual = 1;
        this.pontuacaoFases = new ArrayList<>();
        this.desenhos = new ArrayList<>();
    }

    // --- MÉTODO ATUALIZADO ---
    public void inserirDesenho(Desenho novoDesenho) {
        if (this.desenhos == null) {
            this.desenhos = new ArrayList<>();
        }
        this.desenhos.add(novoDesenho);
    }

    public void inserirPontuacao(int numeroFase, int pontos) {
        int indice = numeroFase - 1;
        while (this.pontuacaoFases.size() < numeroFase) {
            this.pontuacaoFases.add(0);
        }
        int pontuacaoAntiga = this.pontuacaoFases.get(indice);
        if (pontos > pontuacaoAntiga) {
            this.pontuacaoFases.set(indice, pontos);
            atualizarPontuacaoTotal();
        }
    }

    private void atualizarPontuacaoTotal() {
        this.pontuacaoTotal = this.pontuacaoFases.stream().mapToInt(Integer::intValue).sum();
    }

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public int getPontuacaoTotal() { return pontuacaoTotal; }
    public void setPontuacaoTotal(int pontuacaoTotal) { this.pontuacaoTotal = pontuacaoTotal; }
    public int getFaseAtual() { return faseAtual; }
    public void setFaseAtual(int faseAtual) { this.faseAtual = faseAtual; }
    public List<Integer> getPontuacaoFases() { return pontuacaoFases; }
    public void setPontuacaoFases(List<Integer> pontuacaoFases) { this.pontuacaoFases = pontuacaoFases; }
    public List<Desenho> getDesenhos() { return desenhos; }
    public void setDesenhos(List<Desenho> desenhos) { this.desenhos = desenhos; }
}package com.ufv.BitTInt.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Desenho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Construtor vazio para o JPA
    public Desenho() {
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}package com.ufv.BitTInt.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nomeTurma;

    // --- NOVO CAMPO ADICIONADO ---
    private String senhaAdmin;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "turma_id")
    @OrderBy("pontuacaoTotal DESC")
    private List<Aluno> rankingAlunos;

    public Turma() {
    }

    public Turma(String nomeTurma) {
        this.nomeTurma = nomeTurma;
        this.rankingAlunos = new ArrayList<>();
    }

    public void inserirAluno(Aluno novoAluno) {
        if (this.rankingAlunos == null) {
            this.rankingAlunos = new ArrayList<>();
        }
        this.rankingAlunos.add(novoAluno);
        this.rankingAlunos.sort(Comparator.comparingInt(Aluno::getPontuacaoTotal).reversed());
    }

    public void removerAluno(Aluno aluno) {
        this.rankingAlunos.remove(aluno);
    }

    public void exibirDesenhos() {
        if (rankingAlunos == null || rankingAlunos.isEmpty()) {
            System.out.println("Ainda não há alunos nesta turma.");
            return;
        }

        for (Aluno aluno : this.rankingAlunos) {
            System.out.println("Desenhos do aluno: " + aluno.getNickname());

            List<Desenho> listaDeDesenhos = aluno.getDesenhos();

            if (listaDeDesenhos == null || listaDeDesenhos.isEmpty()) {
                System.out.println("  - Não possui desenhos registrados.");
            } else {
                System.out.print("  - IDs dos Desenhos: ");
                for (Desenho desenho : listaDeDesenhos) {
                    System.out.print(desenho.getId() + " ");
                }
                System.out.println();
            }
        }
    }

    // --- Getters e Setters (com os novos para senhaAdmin) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeTurma() { return nomeTurma; }
    public void setNomeTurma(String nomeTurma) { this.nomeTurma = nomeTurma; }
    public List<Aluno> getRankingAlunos() { return rankingAlunos; }
    public void setRankingAlunos(List<Aluno> rankingAlunos) { this.rankingAlunos = rankingAlunos; }

    // --- GETTER E SETTER PARA O NOVO CAMPO ---
    public String getSenhaAdmin() { return senhaAdmin; }
    public void setSenhaAdmin(String senhaAdmin) { this.senhaAdmin = senhaAdmin; }
}package com.ufv.BitTInt.repository;

import com.ufv.BitTInt.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Anotação que indica ao Spring que esta é uma interface de repositório
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    // JpaRepository<Aluno, Long> significa:
    // "Este repositório vai gerenciar a entidade 'Aluno',
    // e o tipo da chave primária (o @Id) do Aluno é 'Long'."

    // O Spring Data JPA vai entender o nome deste método e criar a consulta SQL sozinho!
    Optional<Aluno> findByNickname(String nickname);
}package com.ufv.BitTInt.repository;

import com.ufv.BitTInt.model.Desenho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesenhoRepository extends JpaRepository<Desenho, Long> {
}package com.ufv.BitTInt.repository;

import com.ufv.BitTInt.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // <-- ADICIONE OU VERIFIQUE ESTA IMPORTAÇÃO

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {

    // --- NOVO MÉTODO ADICIONADO ABAIXO ---
    // Permite buscar uma turma pelo seu nome exato.
    // Retorna um Optional, que pode conter a Turma se ela for encontrada, ou vazio caso contrário.
    Optional<Turma> findByNomeTurma(String nomeTurma);
}package com.ufv.BitTInt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BitTintApplication {

	public static void main(String[] args) {
		SpringApplication.run(BitTintApplication.class, args);
	}

}
