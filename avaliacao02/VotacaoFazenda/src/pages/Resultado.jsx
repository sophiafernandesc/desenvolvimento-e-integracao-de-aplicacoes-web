import {useState} from 'react';
import {Link} from 'react-router-dom';
import {obterVotos, calcularTotalVotos, calcularResultado, zerarVotos} from '../services/votacaoService';

function Resultado() {
    const [votos, setVotos] = useState(() => obterVotos());

    const total = calcularTotalVotos(votos);
    const resultado = calcularResultado(votos);

    function handleZerar() {
        if (window.confirm('Apagar todos os votos?')) {
            zerarVotos();
            setVotos({});
        }
    }

    return (
        <main className="pagina">
            <header className="pagina-topo">
                <h1>Parcial da eliminação</h1>
                <p className="total-votos">
                    <strong>{total}</strong> votos computados
                </p>
            </header>

            {total === 0 ? (
                <div className="resultado-vazio">
                    <p>Nenhum voto ainda. Escolha quem sai da roça para ver a parcial.</p>
                    <Link to="/votacao" className="botao botao-principal">Ir para a votação</Link>
                </div>
            ) : (
                <>
                    <ol className="placar">
                        {resultado.map((p, indice) => (
                            <li key={p.id} className={indice === 0 ? 'placar-item placar-lider' : 'placar-item'}>
                                <img src={p.foto} alt={p.nome} className="placar-foto"/>

                                <div className="placar-info">
                                    <div className="placar-nome">
                                        <h3>{p.nome}</h3>
                                        {indice === 0 && <span className="selo-lider">Sairia hoje</span>}
                                    </div>

                                    <div className="barra">
                                        <div
                                            className="barra-preenchida"
                                            style={{width: `${p.percentual}%`}}
                                        />
                                    </div>

                                    <p className="placar-numeros">
                                        {p.percentual.toLocaleString('pt-BR', {maximumFractionDigits: 1})}%
                                        {' '}({p.votos} {p.votos === 1 ? 'voto' : 'votos'})
                                    </p>
                                </div>
                            </li>
                        ))}
                    </ol>

                    <button type="button" className="botao botao-zerar" onClick={handleZerar}>
                        Zerar votação
                    </button>
                </>
            )}
        </main>
    );
}

export default Resultado;
