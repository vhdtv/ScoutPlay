function getAuthHeaders() {
    const token = localStorage.getItem('authToken');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

function salvarDadosLocais(atleta) {
    localStorage.setItem('athleteName', atleta.nome || '');
    localStorage.setItem('athletePhone', atleta.telefone || '');
    localStorage.setItem('athleteCep', atleta.cep || '');
    localStorage.setItem('athleteEmail', atleta.email || '');
    localStorage.setItem('athleteWeight', atleta.peso || '');
    localStorage.setItem('athleteHeight', atleta.altura || '');
    localStorage.setItem('athletePosition', atleta.posicao || '');
    localStorage.setItem('athleteClubs', atleta.clubesAnteriores || '');
    localStorage.setItem('athleteDominantFoot', atleta.peDominante || '');
    localStorage.setItem('athleteVideo', atleta.videos && atleta.videos[0] ? atleta.videos[0].urlVideo : '');
}

// Função para exibir o menu ao clicar na engrenagem
document.querySelector('.edit-button').addEventListener('click', function(e) {
    e.preventDefault();
    const gearMenu = document.getElementById('gearMenu');
    gearMenu.style.display = (gearMenu.style.display === 'block') ? 'none' : 'block';
});

// Fecha o menu ao clicar fora dele
document.addEventListener('click', function(event) {
    const gearMenu = document.getElementById('gearMenu');
    const editButton = document.querySelector('.edit-button');
    if (!gearMenu.contains(event.target) && !editButton.contains(event.target)) {
        gearMenu.style.display = 'none';
    }
});

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
    fetch(`/api/atletas/${id}`, { headers: getAuthHeaders() })
        .then(response => {
            if (response.status === 401) {
                window.location.href = '/login.html?type=atleta';
                throw new Error('Sessão expirada');
            }
            return response.json().then(payload => ({ ok: response.ok, payload }));
        })
        .then(({ ok, payload }) => {
            if (!ok || !payload.success) {
                throw new Error(payload.message || 'Erro ao carregar perfil');
            }

            const atleta = payload.data;
            salvarDadosLocais(atleta);
            console.table(atleta);

            // Preenchendo os dados no HTML
            if (atleta.fotoPerfil) {
                document.getElementById('profilePic').src = `/api/atletas/fotos/${encodeURIComponent(atleta.fotoPerfil.split('/').pop())}`;
            }
            document.getElementById('athleteName').textContent = atleta.nome;
            document.getElementById('athleteAge').textContent = atleta.idade || calcularIdade(atleta.dataNascimento);
            document.getElementById('athleteWeight').textContent = atleta.peso;
            document.getElementById('athleteHeight').textContent = atleta.altura;
            document.getElementById('athletePosition').textContent = atleta.posicao;
            document.getElementById('athleteDominantFoot').textContent = atleta.peDominante;
            document.getElementById('athleteClubs').textContent = atleta.clubesAnteriores;

            // Exibir os links dos vídeos
            const videoContainer = document.getElementById('athleteVideos');
            videoContainer.innerHTML = ''; // Limpa o conteúdo anterior
            (atleta.videos || []).forEach(video => {
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

// Carregar o perfil do atleta quando a página for carregada
window.onload = carregarPerfilAtleta;


