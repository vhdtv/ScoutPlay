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
    event.preventDefault(); // Impede o envio padrão do formulário

    // Lógica de login aqui
    alert('Login realizado com sucesso!');

    // Aqui você pode redirecionar o usuário após o login
    if (userType === 'atleta') {
        window.location.href = 'formularioMaior.html'; // Redireciona para a página do atleta
    } else if (userType === 'olheiro') {
        window.location.href = 'formularioOlheiro.html'; // Redireciona para a página do olheiro
    }
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
