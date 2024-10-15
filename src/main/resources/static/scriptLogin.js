
const urlParams = new URLSearchParams(window.location.search);
const userType = urlParams.get('type');

// Atualiza o link dependendo do tipo de usuário
if (userType === 'atleta') {
    document.getElementById('login-title').innerText = 'Login - Atleta';
    document.getElementById('register-link').setAttribute('href', 'formularioMaior.html');
    
} else if (userType === 'olheiro') {
    document.getElementById('login-title').innerText = 'Login - Olheiro';
    document.getElementById('register-link').setAttribute('href', 'formularioOlheiro.html');
}

function handleLogin(event) {
    event.preventDefault(); // Previne o envio do formulário

    const email = document.querySelector('input[type="text"]').value;
    const senha = document.querySelector('input[type="password"]').value;

    fetch('/api/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
            email: email,
            senha: senha
        })
    })
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                throw new Error('Login falhou');
            }
        })
        .then(data => {
            if (data === 'Atleta') {
                window.location.href = 'feedAtleta.html'; // Redireciona para o painel de atleta
            } else if (data === 'Olheiro') {
                window.location.href = 'feedOlheiro.html'; // Redireciona para o painel de olheiro
            }
        })
        .catch(error => {
            alert('Erro de login: ' + error.message);
        });
}
