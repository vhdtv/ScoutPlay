// scriptLogin.js
const urlParams = new URLSearchParams(window.location.search);
const userType = urlParams.get('type');

function salvarSessao(sessao) {
    localStorage.setItem('authToken', sessao.token);
    localStorage.setItem('userId', sessao.userId);
    localStorage.setItem('userType', sessao.userType);
    localStorage.setItem('userName', sessao.nome || '');
    localStorage.setItem('userEmail', sessao.email || '');
}

// Atualiza o título e o link dependendo do tipo de usuário
if (userType === 'atleta') {
    document.getElementById('login-title').innerText = 'Login - Atleta';
    document.getElementById('register-link').setAttribute('href', 'formularioMaior.html'); // Página do atleta
} else if (userType === 'olheiro') {
    document.getElementById('login-title').innerText = 'Login - Olheiro';
    document.getElementById('register-link').setAttribute('href', 'formularioOlheiro.html'); // Página do olheiro
}
//Salvar as informações em algum local persistente ex: LocalStorage
// Manipula o envio do formulário de login
function handleLogin(event) {
    event.preventDefault(); // Impede o envio padrão do formulário
    fetch('/api/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            email: event.target.elements[0].value,
            senha: event.target.elements[1].value
        })
    })
        .then(response => {
            return response.json().then(payload => ({ ok: response.ok, payload }));
        })
        .then(({ ok, payload }) => {
            if (!ok || !payload.success) {
                throw new Error(payload.message || 'Login falhou');
            }

            const data = payload.data;
            salvarSessao(data);
            alert('Login realizado com sucesso!');
            if (data.userType === 'ATLETA') {
                window.location.href = 'feedAtleta.html'; // Redireciona para o painel de atleta
            } else {
                window.location.href = 'feedOlheiro.html'; // Redireciona para o painel de olheiro
            }
        })
        .catch(error => {
            alert('Erro de login: ' + error.message);
        });
}

// Adiciona um evento de clique ao link de registro
document.getElementById('register-link').addEventListener('click', function(event) {
    event.preventDefault(); // Impede o comportamento padrão do link
    if (userType === 'atleta') {
        window.location.href = 'formularioMaior.html'; // Redireciona para a página do atleta
    } else if (userType === 'olheiro') {
        window.location.href = 'formularioOlheiro.html'; // Redireciona para a página do olheiro
    }
});
