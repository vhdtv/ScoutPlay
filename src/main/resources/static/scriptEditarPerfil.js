let atletaAtual = null;

function getAuthHeaders() {
    const token = localStorage.getItem('authToken');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

function carregarDadosAtleta() {
    const atletaId = localStorage.getItem('userId');

    fetch(`/api/atletas/${atletaId}`, { headers: getAuthHeaders() })
        .then(response => {
            if (response.status === 401) {
                window.location.href = '/login.html?type=atleta';
                throw new Error('Sessão expirada');
            }
            return response.json().then(payload => ({ ok: response.ok, payload }));
        })
        .then(({ ok, payload }) => {
            if (!ok || !payload.success) {
                throw new Error(payload.message || 'Erro ao carregar atleta');
            }

            atletaAtual = payload.data;
            document.getElementById('name').value = atletaAtual.nome || '';
            document.getElementById('telefone').value = atletaAtual.telefone || '';
            document.getElementById('cep').value = atletaAtual.cep || '';
            document.getElementById('email').value = atletaAtual.email || '';
            document.getElementById('weight').value = atletaAtual.peso || '';
            document.getElementById('height').value = atletaAtual.altura || '';
            document.getElementById('position').value = atletaAtual.posicao || '';
            document.getElementById('clubes').value = atletaAtual.clubesAnteriores || '';
            document.getElementById('pe-dominante').value = atletaAtual.peDominante || '';
            document.getElementById('video').value = atletaAtual.videos && atletaAtual.videos[0] ? atletaAtual.videos[0].urlVideo : '';
        })
        .catch(error => alert(error.message));
}

function salvarDadosAtleta(event){
    event.preventDefault();

    if (!atletaAtual) {
        alert('Perfil do atleta ainda não foi carregado.');
        return;
    }

    const payload = {
        id: atletaAtual.id,
        nome: document.getElementById('name').value,
        telefone: document.getElementById('telefone').value,
        cpf: atletaAtual.cpf,
        dataNascimento: atletaAtual.dataNascimento,
        cep: document.getElementById('cep').value,
        email: document.getElementById('email').value,
        senha: document.getElementById('senha').value || '',
        peso: Number(document.getElementById('weight').value),
        altura: Number(document.getElementById('height').value),
        posicao: document.getElementById('position').value,
        clubesAnteriores: document.getElementById('clubes').value,
        peDominante: document.getElementById('pe-dominante').value,
        videos: (atletaAtual.videos || []).map(videoAtual => videoAtual.urlVideo)
    };

    const novoVideo = document.getElementById('video').value;
    const foto = document.getElementById('perfil-foto').files[0];

    fetch(`/api/atletas/${atletaAtual.id}`, {
        method: 'PUT',
        headers: getAuthHeaders(),
        body: JSON.stringify(payload)
    })
        .then(response => response.json().then(data => ({ ok: response.ok, data })))
        .then(({ ok, data }) => {
            if (!ok || !data.success) {
                throw new Error(data.message || 'Erro ao atualizar atleta');
            }

            const promises = [];

            if (foto) {
                const fotoData = new FormData();
                fotoData.append('fotoPerfil', foto);
                promises.push(fetch(`/api/atletas/${atletaAtual.id}/foto`, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('authToken')}`
                    },
                    body: fotoData
                }));
            }

            if (novoVideo) {
                const body = JSON.stringify({ titulo: 'Video do atleta', urlVideo: novoVideo });
                if (atletaAtual.videos && atletaAtual.videos[0]) {
                    promises.push(fetch(`/api/videos/${atletaAtual.videos[0].idVideo}`, {
                        method: 'PUT',
                        headers: getAuthHeaders(),
                        body
                    }));
                } else {
                    promises.push(fetch(`/api/atletas/${atletaAtual.id}/videos`, {
                        method: 'POST',
                        headers: getAuthHeaders(),
                        body
                    }));
                }
            }

            return Promise.all(promises);
        })
        .then(() => {
            alert('Perfil atualizado com sucesso!');
            location.href = 'feedAtleta.html';
        })
        .catch(error => alert(error.message));
}

document.getElementById('editAthleteForm').addEventListener('submit', salvarDadosAtleta);

window.onload = carregarDadosAtleta;