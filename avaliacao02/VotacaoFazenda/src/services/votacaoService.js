import participantes from '../data/participantes';

const CHAVE_VOTOS = 'fazenda2026:votos';

export function listarParticipantes() {
  return participantes;
}

export function listarNaRoca() {
  return participantes.filter((p) => p.naRoca);
}

export function listarForaDaRoca() {
  return participantes.filter((p) => !p.naRoca);
}

export function obterVotos() {
  try {
    const salvo = localStorage.getItem(CHAVE_VOTOS);
    return salvo ? JSON.parse(salvo) : {};
  } catch {
    return {};
  }
}

export function registrarVoto(idParticipante) {
  const participante = participantes.find((p) => p.id === idParticipante);
  if (!participante || !participante.naRoca) {
    throw new Error('Só é possível votar em participantes que estão na roça.');
  }
  const votos = obterVotos();
  votos[idParticipante] = (votos[idParticipante] || 0) + 1;
  localStorage.setItem(CHAVE_VOTOS, JSON.stringify(votos));
  return votos;
}

export function calcularTotalVotos(votos) {
  return Object.values(votos).reduce((soma, qtd) => soma + qtd, 0);
}

export function calcularResultado(votos) {
  const total = calcularTotalVotos(votos);
  return listarNaRoca()
    .map((p) => {
      const qtd = votos[p.id] || 0;
      return {
        ...p,
        votos: qtd,
        percentual: total === 0 ? 0 : (qtd / total) * 100,
      };
    })
    .sort((a, b) => b.votos - a.votos);
}

export function zerarVotos() {
  localStorage.removeItem(CHAVE_VOTOS);
}
