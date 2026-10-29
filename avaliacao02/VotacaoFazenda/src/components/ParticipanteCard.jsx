function ParticipanteCard({ participante, onVotar }) { //props desestruturado na assinatura
  const { nome, foto, naRoca } = participante;

  return (
    <article className={naRoca ? 'card card-roca' : 'card'}>
      {naRoca && <span className="selo-roca">Na roça</span>}
      <img src={foto} alt={nome} className="card-foto" />
      <h3 className="card-nome">{nome}</h3>

      {onVotar && (
        <button
          type="button"
          className="botao botao-principal card-botao"
          onClick={() => onVotar(participante)}
        >
          Votar para eliminar
        </button>
      )}
    </article>
  );
}

export default ParticipanteCard;
