function carregarDadosOlheiro() {
    document.getElementById('name').value = localStorage.getItem('scoutName') || '';
    document.getElementById('telefone').value = localStorage.getItem('scoutPhone') || '';
    document.getElementById('cep').value = localStorage.getItem('scoutCep') || '';
    document.getElementById('email').value = localStorage.getItem('scoutEmail') || '';
    document.getElementById('clubes').value = localStorage.getItem('scoutClub') || '';   
    //nome, telefone, cep, email clubes
}

function salvarDadosOlheiro(event) {
    event.preventDefault();


    const nome = document.getElementById('name').value;
    const telefone = document.getElementById('telefone').value;
    const cep = document.getElementById('cep').value;
    const email = document.getElementById('email').value;
    const clube = document.getElementById('clubes').value;

    localStorage.setItem('scoutName', nome);
    localStorage.setItem('scoutPhone', telefone);
    localStorage.setItem('scoutCep', cep);
    localStorage.setItem('scoutEmail', email);
    localStorage.setItem('scoutClub', clube);

    alert("Perfil do Olheiro atualizado com sucesso!!!");
}

document.getElementById('editAthleteForm').addEventListener('submit', salvarDadosOlheiro);
window.onload = carregarDadosOlheiro;

document.querySelector('.soccer-ball').addEventListener('click', function() {
    window.location.href = 'feedOlheiro.html'; 
});