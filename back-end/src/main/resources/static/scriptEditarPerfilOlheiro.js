let olheiroAtual = null;

function getAuthHeaders() {
    const token = localStorage.getItem('authToken');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

function carregarDadosOlheiro() {
    const olheiroId = localStorage.getItem('userId');
    fetch(`/api/olheiros/${olheiroId}`, { headers: getAuthHeaders() })
        .then(response => {
            if (response.status === 401) {
                window.location.href = '/login.html?type=olheiro';
                throw new Error('Sessão expirada');
            }
            return response.json().then(payload => ({ ok: response.ok, payload }));
        })
        .then(({ ok, payload }) => {
            if (!ok || !payload.success) {
                throw new Error(payload.message || 'Erro ao carregar olheiro');
            }

            olheiroAtual = payload.data;
            document.getElementById('name').value = olheiroAtual.nome || '';
            document.getElementById('telefone').value = olheiroAtual.telefone || '';
            document.getElementById('cep').value = olheiroAtual.cep || '';
            document.getElementById('email').value = olheiroAtual.email || '';
            document.getElementById('clubes').value = olheiroAtual.clube || '';
        })
        .catch(error => alert(error.message));
}

function salvarDadosOlheiro(event) {
    event.preventDefault();

    if (!olheiroAtual) {
        alert('Perfil do olheiro ainda não foi carregado.');
        return;
    }

    const payload = {
        id: olheiroAtual.id,
        nome: document.getElementById('name').value,
        telefone: document.getElementById('telefone').value,
        cpf: olheiroAtual.cpf,
        dataNascimento: olheiroAtual.dataNascimento,
        cep: document.getElementById('cep').value,
        email: document.getElementById('email').value,
        senha: '',
        clube: document.getElementById('clubes').value,
        local: olheiroAtual.local || 'Não informado'
    };

    fetch(`/api/olheiros/${olheiroAtual.id}`, {
        method: 'PUT',
        headers: getAuthHeaders(),
        body: JSON.stringify(payload)
    })
        .then(response => response.json().then(data => ({ ok: response.ok, data })))
        .then(({ ok, data }) => {
            if (!ok || !data.success) {
                throw new Error(data.message || 'Erro ao atualizar olheiro');
            }

            localStorage.setItem('scoutName', payload.nome);
            localStorage.setItem('scoutPhone', payload.telefone);
            localStorage.setItem('scoutCep', payload.cep);
            localStorage.setItem('scoutEmail', payload.email);
            localStorage.setItem('scoutClub', payload.clube);
            alert('Perfil do Olheiro atualizado com sucesso!!!');
        })
        .catch(error => alert(error.message));
}

document.getElementById('editAthleteForm').addEventListener('submit', salvarDadosOlheiro);
window.onload = carregarDadosOlheiro;

document.querySelector('.soccer-ball').addEventListener('click', function() {
    window.location.href = 'feedOlheiro.html'; 
});