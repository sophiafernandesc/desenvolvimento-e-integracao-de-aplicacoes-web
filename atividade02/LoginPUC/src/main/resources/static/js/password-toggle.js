
(function () {
    'use strict';

    var OLHO_ABERTO =
        '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" ' +
        'stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">' +
        '<path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>' +
        '<circle cx="12" cy="12" r="3"/></svg>';

    var OLHO_FECHADO =
        '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" ' +
        'stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">' +
        '<path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>' +
        '<path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/>' +
        '<path d="M14.12 14.12a3 3 0 1 1-4.24-4.24"/>' +
        '<line x1="1" y1="1" x2="23" y2="23"/></svg>';

    function adicionarBotao(input) {
        // Evita aplicar duas vezes no mesmo campo.
        if (input.dataset.toggleAplicado === 'true') {
            return;
        }
        input.dataset.toggleAplicado = 'true';

        // O input precisa de um pai posicionado para o botão ficar por cima.
        var wrapper = document.createElement('span');
        wrapper.className = 'field-control';
        input.parentNode.insertBefore(wrapper, input);
        wrapper.appendChild(input);

        var botao = document.createElement('button');
        // type="button" é essencial: sem isso o clique envia o formulário.
        botao.type = 'button';
        botao.className = 'password-toggle';
        botao.innerHTML = OLHO_ABERTO;
        botao.setAttribute('aria-label', 'Mostrar senha');
        botao.setAttribute('aria-pressed', 'false');
        // Fora da ordem de tabulação: quem usa teclado não quer tropeçar
        // nele entre um campo e outro.
        botao.tabIndex = -1;

        botao.addEventListener('click', function () {
            var visivel = input.type === 'text';

            input.type = visivel ? 'password' : 'text';
            botao.innerHTML = visivel ? OLHO_ABERTO : OLHO_FECHADO;
            botao.setAttribute('aria-label', visivel ? 'Mostrar senha' : 'Ocultar senha');
            botao.setAttribute('aria-pressed', visivel ? 'false' : 'true');

            // Devolve o cursor ao campo, no fim do texto já digitado.
            input.focus();
            var fim = input.value.length;
            if (input.setSelectionRange && input.type === 'text') {
                input.setSelectionRange(fim, fim);
            }
        });

        wrapper.appendChild(botao);
    }

    document.addEventListener('DOMContentLoaded', function () {
        document.querySelectorAll('input[type="password"]').forEach(adicionarBotao);
    });
})();
