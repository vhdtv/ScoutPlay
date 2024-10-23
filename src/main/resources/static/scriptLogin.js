// scriptLogin.js
const urlParams = new URLSearchParams(window.location.search);
const userType = urlParams.get('type');

// Atualiza o título e o link dependendo do tipo de usuário
if (userType === 'atleta') {
    document.getElementById('login-title').innerText = 'Login - Atleta';
    document.getElementById('register-link').setAttribute('href', 'formularioMaior.html'); // Página do atleta
} else if (userType === 'olheiro') {
    document.getElementById('login-title').innerText = 'Login - Olheiro';
    document.getElementById('register-link').setAttribute('href', 'formularioOlheiro.html'); // Página do olheiro
}

// Manipula o envio do formulário de login
function handleLogin(event) {
<<<<<<< HEAD
    event.preventDefault(); // Impede o envio padrão do formulário

    // Lógica de login aqui
    alert('Login realizado com sucesso!');

    // Aqui você pode redirecionar o usuário após o login
    if (userType === 'atleta') {
        window.location.href = 'formularioMaior.html'; // Redireciona para a página do atleta
    } else if (userType === 'olheiro') {
        window.location.href = 'formularioOlheiro.html'; // Redireciona para a página do olheiro
    }
=======
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
>>>>>>> 2b439817ac9b1113590f372c1019d90395bfabf1
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
