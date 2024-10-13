function handleSearch(event) {
    event.preventDefault(); // Evita o envio do formulário
    const name = document.getElementById('name').value;
    const birthYear = document.getElementById('birth-year').value;
    const weight = document.getElementById('weight').value;
    const height = document.getElementById('height').value;
    const position = document.getElementById('position').value;

    let resultsHTML = `
        <p>Nome: ${name || 'Não especificado'}</p>
        <p>Ano de Nascimento: ${birthYear || 'Não especificado'}</p>
        <p>Peso: ${weight || 'Não especificado'} kg</p>
        <p>Altura: ${height || 'Não especificado'} cm</p>
        <p>Posição: ${position || 'Não especificado'}</p>
    `;

    document.getElementById('search-results').innerHTML = `
        <h3>Resultados da Pesquisa:</h3>
        ${resultsHTML}
    `;
    const userType = localStorage.getItem('userType');

        // Se o usuário não for um olheiro, redirecione para uma página de aviso
        if (userType !== 'olheiro') {
            alert('Acesso restrito! Apenas olheiros têm permissão para acessar esta página.');
            window.location.href = 'login.html'; // Redireciona para uma página de login ou outra página
        }
}