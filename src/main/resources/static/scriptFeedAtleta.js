// Função para calcular a idade a partir da data de nascimento
function calcularIdade(dataNascimento) {
    const hoje = new Date();
    const nascimento = new Date(dataNascimento);
    let idade = hoje.getFullYear() - nascimento.getFullYear();
    const mes = hoje.getMonth() - nascimento.getMonth();
    if (mes < 0 || (mes === 0 && hoje.getDate() < nascimento.getDate())) {
        idade--;
    }
    return idade;
}

// Função para carregar e exibir as informações do atleta
function carregarPerfilAtleta() {
    const id = localStorage.getItem("userId");
    fetch(`/api/atletas/${id}`)
        .then(response => response.json())
        .then(atleta => {
            console.table(atleta);

            // Preenchendo os dados no HTML
            document.getElementById('profilePic').src = `/api/atletas/fotos/${encodeURIComponent(atleta.fotoPerfil.split('/').pop())}`;
            document.getElementById('athleteName').textContent = atleta.nome;
            document.getElementById('athleteAge').textContent = calcularIdade(atleta.dataNascimento);
            document.getElementById('athleteWeight').textContent = atleta.peso;
            document.getElementById('athleteHeight').textContent = atleta.altura;
            document.getElementById('athletePosition').textContent = atleta.posicao;
            document.getElementById('athleteDominantFoot').textContent = atleta.peDominante;
            document.getElementById('athleteClubs').textContent = atleta.clubesAnteriores;

            // Exibir os links dos vídeos
            const videoContainer = document.getElementById('athleteVideos');
            videoContainer.innerHTML = ''; // Limpa o conteúdo anterior
            atleta.videos.forEach(video => {
                const videoLink = document.createElement('a');
                videoLink.href = video.urlVideo; // Usa o link correto do JSON
                videoLink.textContent = "Assistir vídeo";
                videoLink.target = '_blank'; // Abre o link em uma nova aba
                videoLink.className = 'video-link';
                videoContainer.appendChild(videoLink);
            });
        })
        .catch(error => console.error("Erro ao carregar perfil do atleta:", error));
}

// Adicionar funcionalidade ao botão de saída
document.getElementById("redirectButton").addEventListener("click", function () {
    // Redireciona para outra página
    window.location.href = "http://localhost:8080"; // Substitua pelo URL desejado
});

// Carregar o perfil do atleta quando a página for carregada
window.onload = carregarPerfilAtleta;
