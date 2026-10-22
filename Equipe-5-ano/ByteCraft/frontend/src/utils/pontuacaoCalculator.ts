import type { NivelDificuldade } from "../types";

/**
 * Define a pontuação base por peça
 * Montagem Externa: 4 peças × 100 = 400 pontos
 * Montagem Interna: 5 peças × 100 = 500 pontos
 * Total máximo possível: 900 pontos base
 */
const PONTUACAO_BASE_POR_PECA = 100;

/**
 * Tabela de penalidades por tentativa conforme RN10
 */
const PENALIDADES: Record<NivelDificuldade, Record<number, number>> = {
  facil: {
    1: 0,      // 100%
    2: 0,      // aviso, sem desconto
    3: 20,     // dica, -20%
    4: 50,     // ilumina, -50%
  },
  medio: {
    1: 0,      // 100%
    2: 5,      // -5%
    3: 10,     // -10%
    4: 20,     // aviso, -20%
    5: 30,     // dica, -30%
  },
  dificil: {
    1: 0,      // 100%
    2: 10,     // -10%
    3: 15,     // -15%
    4: 25,     // aviso, -25%
    5: 40,     // aviso, -40%
  },
};

/**
 * Feedback/ajuda para cada tentativa conforme RN10
 */
const FEEDBACK_TENTATIVA: Record<NivelDificuldade, Record<number, string>> = {
  facil: {
    1: "Parabéns! Primeira tentativa!",
    2: "⚠️ Aviso: Cuidado com a posição",
    3: "💡 Dica: Procure uma área com destaque especial",
    4: "✨ Área iluminada: É aqui!",
  },
  medio: {
    1: "Perfeito! Primeira tentativa!",
    2: "😊 Quase lá, tente de novo",
    3: "💡 Procure observar melhor a posição",
    4: "⚠️ Cuidado! Está ficando mais difícil",
    5: "💡 Dica: Observe o contorno no computador",
  },
  dificil: {
    1: "Excelente! Primeira tentativa!",
    2: "😊 Continue tentando",
    3: "💡 Observe a estrutura com atenção",
    4: "⚠️ Cuidado! Mais uma e você perde mais pontos",
    5: "⚠️ Última chance! Escolha com cuidado",
  },
};

/**
 * ✅ CORREÇÃO: Calcula a pontuação de uma peça conforme RN10
 * 
 * @param numeroTentativas - Número da tentativa (1, 2, 3, etc)
 * @param nivel - Nível de dificuldade
 * @param pontosBase - Pontos base da peça (padrão: 100)
 * @returns Objeto com pontos e feedback
 */
export function calcularPontuacaoPeca(
  numeroTentativas: number,
  nivel: NivelDificuldade,
  pontosBase: number = PONTUACAO_BASE_POR_PECA
): { pontos: number; feedback: string; desconto: number } {
  // Pegar a penalidade da tabela
  const penalidade = PENALIDADES[nivel][numeroTentativas] ?? 
                     PENALIDADES[nivel][Object.keys(PENALIDADES[nivel]).length] ?? 
                     50;
  
  // Calcular desconto (não pode ultrapassar 100%)
  const desconto = Math.min(penalidade, 100);
  
  // Calcular pontos finais (nunca negativo)
  const pontos = Math.max(0, Math.round(pontosBase * (1 - desconto / 100)));
  
  // Obter feedback
  const feedback = FEEDBACK_TENTATIVA[nivel][numeroTentativas] ?? 
                   FEEDBACK_TENTATIVA[nivel][Object.keys(FEEDBACK_TENTATIVA[nivel]).length] ?? 
                   "Tente novamente!";
  
  console.log(`📊 Cálculo peça - Tentativas: ${numeroTentativas}, Nível: ${nivel}, Desconto: ${desconto}%, Pontos: ${pontos}`);
  
  return {
    pontos,
    feedback,
    desconto
  };
}

/**
 * ✅ CORREÇÃO: Calcula a pontuação final com bônus de tempo conforme RN22
 * 
 * Fórmula: X × max(1, (2 - T/600))
 * Onde:
 *   X = pontuação total acumulada (sem bônus)
 *   T = tempo TOTAL em segundos (montagem externa + interna)
 * 
 * Ganha bônus se completar em menos de 10 minutos (600 segundos)
 * Não perde pontos se demorar mais (mínimo é 1x)
 * 
 * @param pontuacaoTotal - Pontuação total acumulada sem bônus
 * @param tempoSegundos - Tempo TOTAL em segundos
 * @returns Pontuação final com bônus aplicado
 */
export function calcularPontuacaoFinal(
  pontuacaoTotal: number,
  tempoSegundos: number
): number {
  // ✅ Aplicar fórmula RN22: max(1, (2 - T/600))
  const multiplicador = Math.max(1, 2 - tempoSegundos / 600);
  const pontuacaoFinal = Math.round(pontuacaoTotal * multiplicador);
  
  const tempoMinutos = Math.floor(tempoSegundos / 60);
  const tempoSegundosRestantes = tempoSegundos % 60;
  
  console.log(`
╔════════════════════════════════════════════════════════════╗
║          📊 CÁLCULO DE PONTUAÇÃO FINAL (RN22)             ║
╠════════════════════════════════════════════════════════════╣
║  Pontuação Base (acumulada): ${String(pontuacaoTotal).padStart(24)} pts  ║
║  Tempo Total: ${String(`${tempoMinutos}:${String(tempoSegundosRestantes).padStart(2, '0')}`).padStart(36)} (${tempoSegundos}s)  ║
║  Multiplicador de Tempo: ${String(multiplicador.toFixed(3)).padStart(27)}x  ║
║  ───────────────────────────────────────────────────────  ║
║  🎯 PONTUAÇÃO FINAL: ${String(pontuacaoFinal).padStart(32)} pts  ║
╚════════════════════════════════════════════════════════════╝
  `);
  
  return pontuacaoFinal;
}

/**
 * ✅ NOVO: Valida e exibe o resumo da pontuação
 */
export function validarPontuacao(
  pontuacaoExibida: number,
  pontuacaoCalculada: number,
  pontuacaoBackend: number
): boolean {
  const valido = pontuacaoExibida === pontuacaoCalculada && 
                 pontuacaoCalculada === pontuacaoBackend;
  
  if (!valido) {
    console.error(`
⚠️ INCONSISTÊNCIA DETECTADA:
   Pontuação Exibida: ${pontuacaoExibida}
   Pontuação Calculada: ${pontuacaoCalculada}
   Pontuação Backend: ${pontuacaoBackend}
    `);
  }
  
  return valido;
}

/**
 * Simula o cálculo de pontuação para múltiplas tentativas
 * Útil para testes e entendimento
 */
export function simularPontuacoes() {
  console.log("=== SIMULAÇÃO RN10 ===\n");
  
  (['facil', 'medio', 'dificil'] as NivelDificuldade[]).forEach(nivel => {
    console.log(`\n${nivel.toUpperCase()}:`);
    console.log("Tentativa | Pontos | Desconto | Feedback");
    console.log("----------|--------|----------|------------------");
    
    for (let i = 1; i <= 5; i++) {
      const { pontos, feedback, desconto } = calcularPontuacaoPeca(i, nivel);
      console.log(
        `${i}        | ${String(pontos).padStart(6)} | ${String(desconto).padStart(7)}% | ${feedback}`
      );
    }
  });
  
  console.log("\n=== SIMULAÇÃO RN22 (Bônus de Tempo) ===\n");
  console.log("Tempo     | Multiplicador | Pontuação Base | Pontuação Final");
  console.log("----------|---------------|----------------|----------------");
  
  const pontuacaoBase = 900; // Máximo possível
  const tempos = [300, 450, 600, 750, 900, 1200]; // 5min, 7.5min, 10min, 12.5min, 15min, 20min
  
  tempos.forEach(tempo => {
    const multiplicador = Math.max(1, 2 - tempo / 600);
    const pontuacaoFinal = Math.round(pontuacaoBase * multiplicador);
    const minutos = Math.floor(tempo / 60);
    console.log(
      `${String(minutos).padStart(2)}min     | ${multiplicador.toFixed(3).padStart(13)} | ${String(pontuacaoBase).padStart(14)} | ${String(pontuacaoFinal).padStart(15)}`
    );
  });
}