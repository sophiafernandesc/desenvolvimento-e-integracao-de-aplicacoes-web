// Fecha a mensagem quando o usuario clica no "x".
// Um unico listener no documento cobre os alertas de todas as telas.
document.addEventListener('click', function (event) {
    const button = event.target.closest('.alert-close');
    if (button) {
        button.closest('.alert').remove();
    }
});
