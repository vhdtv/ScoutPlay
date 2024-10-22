function handleSearch(event) {
    event.preventDefault(); // Evita o envio do formulário
    const name = document.getElementById('name').value;
    const birthYear = document.getElementById('birth-year').value;
    const weight = document.getElementById('weight').value;
    const height = document.getElementById('height').value;
    const position = document.getElementById('position').value;

    const userType = localStorage.getItem('userType');

        // Simula alguns resultados aleatórios, para exibir os resultados se acordo com o banco de dados, substituir o código da linha 11 a linha 55 pela parte comentada após essa linha.
        const simulatedData = [
            {
                name: 'Atleta 1',
                birthYear: '1995',
                weight: '75',
                height: '180',
                position: 'Atacante',
                imageUrl: 'https://via.placeholder.com/180'
            },
            {
                name: 'Atleta 2',
                birthYear: '2000',
                weight: '70',
                height: '175',
                position: 'Meio-Campo',
                imageUrl: 'https://via.placeholder.com/180'
            },
            {
                name: 'Atleta 3',
                birthYear: '1998',
                weight: '80',
                height: '185',
                position: 'Defensor',
                imageUrl: 'https://via.placeholder.com/180'
            }
        ];
        const resultsContainer = document.getElementById('search-results');
        resultsContainer.innerHTML = '<h3>Resultados da pesquisa (Simulados):</h3>';

    
        // Exibe os resultados simulados

    
        simulatedData.forEach(result => {
            const resultItem = document.createElement('div');
            resultItem.classList.add('result-item');
            resultItem.innerHTML += `
            <img class ="result-img" src="${result.imageUrl}" alt="Foto do atleta">
            <h4>${result.name || 'Não especificado'}</h4>
            <p>Ano de Nascimento: ${result.birthYear || 'Não especificado'}</p>
            <p>Peso: ${result.weight || 'Não especificado'} kg</p>
            <p>Altura: ${result.height || 'Não especificado'} cm</p>
            <p>Posição: ${result.position || 'Não especificado'}</p>
            <a href="#">Ver Perfil</a>
            `;

    
            resultsContainer.appendChild(resultItem);    
            });
    }

        // Se o usuário não for um olheiro, redirecione para uma página de aviso
        //if (userType !== 'olheiro') {
          //  alert('Acesso restrito! Apenas olheiros tem permissão para acessar esta página.');
            //window.location.href = 'login.html'; // Redireciona para uma página de login ou outra página
            //return;
        //}

    //const searchFilters = {
      //  name : name,
        //birthYear : birthYear,
        //weight : weight,
        //height : height,
        //position : position,
//};

    //fetch('URL_DO_BACKEND/search', {
        //method: 'GET',
        //headers: {
        //    'Content-Type': 'application/json'
        //},
        //body:JSON.stringify(searchFilters)
    //})
    //.then(response => {
    //    if (!response.ok) {
    //        throw new Error('Erro na resposta do servidor.')
    //    }
    //    return response.json();
    //})
    //.then(data => {
    //    let resultsHTML = '';

    //    if (Array.isArray(data) && data.length > 0) {
    //        data.forEach(result => {
    //            resultsHTML +=`
    //        <div class="result-item">
    //        <img src="${result.imageUrl}" alt="Foto do atleta">
    //        <h4>${result.name || 'Não especificado'}</h4>
    //        <p>Ano de Nascimento: ${result.birthYear || 'Não especificado'}</p>
    //        <p>Peso: ${result.weight || 'Não especificado'} kg</p>
    //        <p>Altura: ${result.height || 'Não especificado'} cm</p>
    //        <p>Posição: ${result.position || 'Não especificado'}</p>
    //        <a href="#">Ver Perfil</a>
    //        </div>
    //            `;
    //        });
    //    } else {
    //        resultsHTML = '<p> Nenhum resultado foi encontrado.</p>';
    //    }

    //    document.getElementById('search-results').innerHTML = `
    //    <h3>Resultados da Pesquisa: </h3>
    //    ${resultsHTML}
    //    `;
    //})
    //.catch(error =>{
    //    console.error('Erro ao buscar os resultados:', error);
    //    document.getElementById('search-results').innerHTML = `
    //    <p>Ocorreu um erro ao buscar os resultados.</p>
    //    `;
    //});
//}