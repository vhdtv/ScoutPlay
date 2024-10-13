
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
    window.location.href = 'index.html'; // Altere conforme necessário
}
