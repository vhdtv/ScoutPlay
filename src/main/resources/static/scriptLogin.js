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
//Salvar as informações em algum local persistente ex: LocalStorage
// Manipula o envio do formulário de login
function handleLogin(event) {
    event.preventDefault(); // Impede o envio padrão do formulário
    fetch('/api/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
            email: event.target.elements[0].value,
            senha: event.target.elements[1].value
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
        localStorage.setItem("userId", data)
            alert('Login realizado com sucesso!');
            if ( data.substring(0,3) === 'ATL') {
                window.location.href = 'feedAtleta.html'; // Redireciona para o painel de atleta
            } else{
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
