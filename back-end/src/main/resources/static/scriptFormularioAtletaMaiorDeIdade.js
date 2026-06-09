function salvarSessao(sessao) {
    localStorage.setItem('authToken', sessao.token);
    localStorage.setItem('userId', sessao.userId);
    localStorage.setItem('userType', sessao.userType);
    localStorage.setItem('userName', sessao.nome || '');
    localStorage.setItem('userEmail', sessao.email || '');
}

// Função para adicionar mais campos de link de vídeo dinamicamente
function addVideoLink() {
    const container = document.getElementById("videoLinksContainer");
    if (container) {  // Verifique se o container existe
        const input = document.createElement("input");
        input.type = "url";
        input.name = "videos[]";
        input.placeholder = "Coloque o link do seu DVD";
        container.appendChild(input);
    } else {
        console.error("O elemento videoLinksContainer não foi encontrado.");
    }
}

// Função para lidar com o envio do formulário do atleta
async function handleAtletaForm(event) {
    event.preventDefault();

    // Criação do objeto JSON com os dados do atleta
    const atleta = {
        nome: document.getElementById('name').value,
        telefone: document.getElementById('telefone').value,
        cpf: document.getElementById('cpf').value,
        dataNascimento: document.getElementById('data-nascimento').value,
        cep: document.getElementById('cep').value,
        email: document.getElementById('email').value,
        senha: document.getElementById('senha').value,
        peso: document.getElementById('weight').value,
        altura: document.getElementById('height').value,
        posicao: document.getElementById('position').value,
        clubesAnteriores: document.getElementById('clubes').value,
        peDominante: document.getElementById('pe-dominante').value,
        videos: Array.from(document.querySelectorAll("input[name='videos[]']")).map(url => url.value).filter(url => url !== "")
    };

    const fotoPerfil = document.getElementById('perfil-foto').files[0];

    // Envia o formulário
    try {
        const response = await fetch('/api/atletas/registro', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(atleta)
        });

        const payload = await response.json();
        if (!response.ok || !payload.success) {
            throw new Error(payload.message || 'Erro no cadastro do atleta.');
        }

        salvarSessao(payload.data);

        if (fotoPerfil) {
            const fotoData = new FormData();
            fotoData.append('fotoPerfil', fotoPerfil);
            await fetch(`/api/atletas/${payload.data.userId}/foto`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${payload.data.token}`
                },
                body: fotoData
            });
        }

        for (const urlVideo of atleta.videos) {
            await fetch(`/api/atletas/${payload.data.userId}/videos`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${payload.data.token}`
                },
                body: JSON.stringify({ titulo: 'Video do atleta', urlVideo })
            });
        }

        alert('Cadastro de atleta realizado com sucesso!');
        location.href = '/feedAtleta.html';
    } catch (error) {
        console.error("Erro na requisição:", error);
        alert(error.message || 'Erro na requisição.');
    }
}

// Adiciona o listener para o evento de envio do formulário
document.getElementById('athleteForm').addEventListener('submit', handleAtletaForm);
