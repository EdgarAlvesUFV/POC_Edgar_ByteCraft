import { DIALOGOS_FACIL } from './dialogos/facil';
import { DIALOGOS_MEDIO } from './dialogos/medio';
import { DIALOGOS_DIFICIL } from './dialogos/dificil';
import type { DialogosPorNivel, NivelDificuldade } from '../types';

export const DIALOGOS_HISTORIA: DialogosPorNivel = {
  facil: DIALOGOS_FACIL,
  medio: DIALOGOS_MEDIO,
  dificil: DIALOGOS_DIFICIL
};

// Mensagens personalizadas para os modais de sucesso e erro por nível
export const MENSAGENS_MODAIS = {
  facil: {
    sucesso: [
      "🎉 Parabéns! Você acertou!",
      "👏 Muito bem! Continue assim!",
      "✅ Excelente! Peça encaixada corretamente!",
      "🌟 Você é um expert em montagem!",
      "😊 Que legal! Você conseguiu!",
      "👍 Perfeito! Mais uma peça no lugar certo!"
    ],
    erro: [
      "🤔 Quase lá! Tente novamente.",
      "💡 Dica: Observe com atenção o formato da peça.",
      "🔄 Não desista! Você consegue!",
      "📐 Lembre-se: cada peça tem seu lugar específico.",
      "😅 Opa! Vamos tentar de novo?",
      "🔍 Preste atenção onde as outras peças se encaixam."
    ]
  },
  medio: {
    sucesso: [
      "💻 Ótimo trabalho! Conexão estabelecida.",
      "🔌 Perfeito! Componente instalado corretamente.",
      "⚡ Funcionamento ideal confirmado!",
      "🖥️ Sistema reconheceu o hardware!",
      "🔧 Montagem precisa! Continue assim.",
      "📊 Interface conectada com sucesso!"
    ],
    erro: [
      "❌ Conexão inadequada. Verifique os encaixes.",
      "🔧 Ajuste necessário - tente outra posição.",
      "⚠️ Compatibilidade não detectada.",
      "📟 Refaça a conexão cuidadosamente.",
      "🔌 Verifique a orientação do componente.",
      "🔄 Reposicione e tente novamente."
    ]
  },
  dificil: {
    sucesso: [
      "🎯 Precisão técnica exemplar!",
      "🔬 Configuração otimizada alcançada!",
      "💾 Interface periférica sincronizada!",
      "🚀 Performance máxima estabelecida!",
      "⚙️ Subsistema integrado com eficiência!",
      "🔋 Fluxo de dados estabilizado!"
    ],
    erro: [
      "⚙️ Falha na interface de comunicação.",
      "🔍 Análise: Verifique protocolos de conexão.",
      "📊 Erro de compatibilidade de barramento.",
      "🛠️ Requer recalibração do subsistema.",
      "🔌 Interferência no sinal de dados detectada.",
      "⚠️ Configuração não atende aos parâmetros técnicos."
    ]
  }
};

// Função para obter mensagem aleatória
export const obterMensagemAleatoria = (nivel: NivelDificuldade, tipo: 'sucesso' | 'erro'): string => {
  const mensagens = MENSAGENS_MODAIS[nivel]?.[tipo] || MENSAGENS_MODAIS.medio[tipo];
  return mensagens[Math.floor(Math.random() * mensagens.length)];
};