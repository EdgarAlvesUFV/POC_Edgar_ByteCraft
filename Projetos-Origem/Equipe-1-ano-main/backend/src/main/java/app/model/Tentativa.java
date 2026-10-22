package app.model;

import java.time.LocalDateTime;

public class Tentativa {
    private Long id;
    private Long progressoId;
    private Long misterioId;
    private String alternativaEscolhida;
    private boolean correta;
    private Long tempoRespostaMs;
    private LocalDateTime dataHora;

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public Long getProgressoId() {return progressoId;}
    public void setProgressoId(Long progressoId) {this.progressoId = progressoId;}

    public Long getMisterioId() {return misterioId;}
    public void setMisterioId(Long misterioId) {this.misterioId = misterioId;}

    public String getAlternativaEscolhida() {return alternativaEscolhida;}
    public void setAlternativaEscolhida(String alternativaEscolhida) {this.alternativaEscolhida = alternativaEscolhida;}

    public boolean isCorreta() {return correta;}
    public void setCorreta(boolean correta) {this.correta = correta;}

    public Long getTempoRespostaMs() {return tempoRespostaMs;}
    public void setTempoRespostaMs(Long tempoRespostaMs) {this.tempoRespostaMs = tempoRespostaMs;}

    public LocalDateTime getDataHora() {return dataHora;}
    public void setDataHora(LocalDateTime dataHora) {this.dataHora = dataHora;}
}
