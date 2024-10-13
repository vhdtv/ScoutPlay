// Função que lida com a submissão do formulário do olheiro
function handleOlheiroForm(event) {
    event.preventDefault(); // Evita o comportamento padrão de recarregar a página

    // Salvar no localStorage que o usuário é um olheiro
    localStorage.setItem('userType', 'olheiro');

    // Exemplo: Aqui você pode coletar os dados do formulário, se necessário
    const nome = document.getElementById('name').value;
    const dataNascimento = document.getElementById('dob').value;
    const clube = document.getElementById('club').value;
    const local = document.getElementById('location').value;

    // (Opcional) Validação dos dados antes de prosseguir
    if (nome && dataNascimento && clube && local) {
        // Todos os campos preenchidos corretamente

        // Aqui você pode realizar qualquer outra ação, como enviar dados para o servidor, etc.

        // Redireciona para a página de feed de olheiro
        location.href = window.location.origin + '/src/main/resources/static/feedOlheiro.html';
    } else {
        alert('Por favor, preencha todos os campos.');
    }
}

// Adiciona o listener ao formulário para chamar a função quando for submetido
document.getElementById('scoutForm').addEventListener('submit', handleOlheiroForm);
