package app;

import app.model.Pergunta;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;


@RestController
public class Teste {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@GetMapping("/teste")
	public String teste(){
		return "Backend funcionando";
	}

	@GetMapping("/getPergunta")
public ResponseEntity<Pergunta> getPergunta() {
    String sql = "SELECT * FROM pergunta_prova WHERE id_perguntas = 1";

    Pergunta pergunta = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
        Pergunta p = new Pergunta();
        p.setId(rs.getInt("id_perguntas"));
        p.setTexto(rs.getString("texto"));
        p.setImagemUrl(rs.getString("imagemUrl"));
        p.setAudioUrl(rs.getString("audioUrl"));        
        int idAlternativas = rs.getInt("alternativas");
        p.setAlternativas(getAlternativas(idAlternativas));
        return p;
    });

    return ResponseEntity.ok(pergunta);
}

	private List<String> getAlternativas(int idAlternativas) {
		String sql = "SELECT opcao1, opcao2, opcao3, opcao4 FROM alternativas_prova WHERE id_alternativas = ?";
	
		return jdbcTemplate.queryForObject(sql, new Object[]{idAlternativas}, (rs, rowNum) -> {
			List<String> alternativas = new ArrayList<>();
			alternativas.add(rs.getString("opcao1"));
			alternativas.add(rs.getString("opcao2"));
			alternativas.add(rs.getString("opcao3"));
			alternativas.add(rs.getString("opcao4"));
			return alternativas;
		});
	}
}