package app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MetricasProgressoDto {
    private long totalResolvidos;
    private double mediaEstrelas;
    private double mediaTentativas;
}
