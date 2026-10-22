package app.model;

import java.time.LocalDateTime;

public class PistaUso {
    private Long id;
    private Long progressoId;
    private Long sessaoMisterioId;
    private Long pistaId;
    private Integer ordem;
    private LocalDateTime dataHora;
    private String tipoPista;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProgressoId() { return progressoId; }
    public void setProgressoId(Long progressoId) { this.progressoId = progressoId; }

    public Long getSessaoMisterioId() { return sessaoMisterioId; }
    public void setSessaoMisterioId(Long sessaoMisterioId) { this.sessaoMisterioId = sessaoMisterioId; }

    public Long getPistaId() { return pistaId; }
    public void setPistaId(Long pistaId) { this.pistaId = pistaId; }

    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getTipoPista() { return tipoPista; }
    public void setTipoPista(String tipoPista) { this.tipoPista = tipoPista; }
}
