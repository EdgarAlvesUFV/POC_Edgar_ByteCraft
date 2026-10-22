package app.model;

public class Progresso {
    private Long id;
    /** FK para sessao_misterios(id) */
    private Long sessaoMisterioId;

    private int pontuacaoObtida;   // 0..5 (ou o range que você definir)
    private Long tempoResolucaoMs; // duração para resolver o mistério (ms)
    private boolean resolvido;     // true quando o aluno acertar

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSessaoMisterioId() { return sessaoMisterioId; }
    public void setSessaoMisterioId(Long sessaoMisterioId) { this.sessaoMisterioId = sessaoMisterioId; }

    public int getPontuacaoObtida() { return pontuacaoObtida; }
    public void setPontuacaoObtida(int pontuacaoObtida) { this.pontuacaoObtida = pontuacaoObtida; }

    public Long getTempoResolucaoMs() { return tempoResolucaoMs; }
    public void setTempoResolucaoMs(Long tempoResolucaoMs) { this.tempoResolucaoMs = tempoResolucaoMs; }

    public boolean isResolvido() { return resolvido; }
    public void setResolvido(boolean resolvido) { this.resolvido = resolvido; }
}
