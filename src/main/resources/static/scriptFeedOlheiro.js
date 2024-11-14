function handleExit() {
    window.location.href = 'http://localhost:8080';
}

function handleSearch(event) {
    event.preventDefault(); // Evita o envio do formulário
    const name = document.getElementById('name').value;
    const birthYear = document.getElementById('birth-year').value;
    const weight = document.getElementById('weight').value;
    const height = document.getElementById('height').value;
    const position = document.getElementById('position').value;

    const searchFilters = {
        name: name,
        birthYear: birthYear,
        weight: weight,
        height: height,
        position: position
    };

    // Realiza a busca via API
    fetch(`/api/atletas?name=${name}&birthYear=${birthYear}&weight=${weight}&height=${height}&position=${position}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        const resultsContainer = document.getElementById('search-results');
        resultsContainer.innerHTML = ''; // Limpa resultados anteriores

        if (data.length > 0) {
            data.forEach(atleta => {
                const resultItem = document.createElement('div');
                resultItem.classList.add('result-item');

                // Converte a data de nascimento para o formato brasileiro
                const dataNascimentoFormatada = atleta.dataNascimento ?
                    new Date(atleta.dataNascimento).toLocaleDateString('pt-BR') :
                    'Não especificado';

                resultItem.innerHTML = `
                    <img class="result-img" src="/api/atletas/fotos/${atleta.fotoPerfil.split('/').pop()}" alt="Foto do atleta">
                    <h4>${atleta.nome || 'Não especificado'}</h4>
                    <p>Data de Nascimento: ${dataNascimentoFormatada}</p>
                    <p>Peso: ${atleta.peso || 'Não especificado'} kg</p>
                    <p>Altura: ${atleta.altura || 'Não especificado'} cm</p>
                    <p>Posição: ${atleta.posicao || 'Não especificado'}</p>
                    <p>Pé Dominante: ${atleta.peDominante || 'Não especificado'}</p>
                    <p>Telefone: ${atleta.telefone || 'Não especificado'}
                    <p>Email: ${atleta.email || 'Não especificado'}</p>
                `;

                // Verificar se há vídeos e criar botões
                if (atleta.videos && atleta.videos.length > 0) {
                    const videoContainer = document.createElement('div');
                    videoContainer.classList.add('video-container');
                    atleta.videos.forEach(video => {
                        const videoButton = document.createElement('a');
                        videoButton.href = video.urlVideo;
                        videoButton.innerText = "Assistir no YouTube";
                        videoButton.target = "_blank"; // Abre o link em uma nova aba
                        videoButton.classList.add('video-link');
                        videoContainer.appendChild(videoButton);
                    });
                    resultItem.appendChild(videoContainer);
                }

                resultsContainer.appendChild(resultItem);
            });
        } else {
            resultsContainer.innerHTML = `<p>Nenhum resultado encontrado.</p>`;
        }
    })
    .catch(error => {
        console.error('Erro ao buscar atletas:', error);
        document.getElementById('search-results').innerHTML = `<p>Erro ao buscar os resultados.</p>`;
    });
}