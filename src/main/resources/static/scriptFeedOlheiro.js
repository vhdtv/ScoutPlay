function handleSearch(event) {
    event.preventDefault(); // Evita o envio do formulário
    const name = document.getElementById('name').value;
    const birthYear = document.getElementById('birth-year').value;
    const weight = document.getElementById('weight').value;
    const height = document.getElementById('height').value;
    const position = document.getElementById('position').value;

    const userType = localStorage.getItem('userType');

        // Se o usuário não for um olheiro, redirecione para uma página de aviso
        if (userType !== 'olheiro') {
            alert('Acesso restrito! Apenas olheiros tem permissão para acessar esta página.');
            window.location.href = 'login.html'; // Redireciona para uma página de login ou outra página
            return;
        }

    const searchFilters = {
        name : name,
        birthYear : birthYear,
        weight : weight,
        height : height,
        position : position,
};

    fetch('URL_DO_BACKEND/search', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body:JSON.stringify(searchFilters)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Erro na resposta do servidor.')
        }
        return response.json();
    })
    .then(data => {
        let resultsHTML = '';

        if (data.length > 0) {
            data.forEach(result => {
                resultsHTML +=`
                <p>Nome: ${result.name || 'Não especificado'}</p>
                <p>Ano de Nascimento: ${result.birthYear || 'Não especificado'}</p>
                <p>Peso: ${result.weight || 'Não especificado'} kg</p>
                <p>Altura: ${result.height || 'Não especificado'} cm</p>
                <p>Posição: ${result.position || 'Não especificado'}</p>
                <hr>
                `;
            });
        } else {
            resultsHTML = '<p> Nenhum resultado foi encontrado.</p>';
        }

        document.getElementById('search-results').innerHTML = `
        <h3>Resultados da Pesquisa: </h3>
        ${resultsHTML}
        `;
    })
    .catch(error =>{
        console.error('Erro ao buscar os resultados:', error);
        document.getElementById('search-results').innerHTML = `
        <p>OCorreu um erro ao buscar os resultados.</p>
        `;
    });
}