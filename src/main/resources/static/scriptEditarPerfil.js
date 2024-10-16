function carregarDadosAtleta() {
    document.getElementById('name').value = localStorage.getItem('athleteName') || '';
    document.getElementById('telefone').value = localStorage.getItem('athletePhone') || '';
    document.getElementById('cpf').value = localStorage.getItem('athleteCpf') || '';
    document.getElementById('data-nascimento').value = localStorage.getItem('athleteDob') || '';
    document.getElementById('cep').value = localStorage.getItem('athleteCep') || '';
    document.getElementById('email').value = localStorage.getItem('athleteEmail') || '';
    document.getElementById('weight').value = localStorage.getItem('athleteWeight') || '';
    document.getElementById('height').value = localStorage.getItem('athleteHeight') || '';
    document.getElementById('position').value = localStorage.getItem('athletePosition') || '';
    document.getElementById('clubes').value = localStorage.getItem('athleteClubs') || '';
    document.getElementById('pe-dominante').value = localStorage.getItem('athleteDominantFoot') || '';
    document.getElementById('video').value = localStorage.getItem('athleteVideo') || '';
}

function salvarDadosAtleta(event){
    event.preventDefault();


const nome = document.getElementById('name').value;
const telefone = document.getElementById('telefone').value;
const cep = document.getElementById('cep').value;
const email = document.getElementById('email').value;
const senha = document.getElementById('senha').value;
const peso = document.getElementById('weight').value;
const altura = document.getElementById('height').value;
const posicao = document.getElementById('position').value;
const clubes = document.getElementById('clubes').value;
const peDominante = document.getElementById('pe-dominante').value;
const video = document.getElementById('video').value;
const foto = document.getElementById('perfil-foto').files[0];

localStorage.setItem('athleteName', nome);
localStorage.setItem('athletePhone', telefone);
localStorage.setItem('athleteCep', cep);
localStorage.setItem('athleteEmail', email);
localStorage.setItem('athletePassword', senha);
localStorage.setItem('athleteWeight', peso);
localStorage.setItem('athleteHeight', altura);
localStorage.setItem('athletePosition', posicao);
localStorage.setItem('athleteClubs', clubes);
localStorage.setItem('athleteDominantFoot', peDominante);
localStorage.setItem('athleteVideo', video);

if (foto) {
    const reader = new FileReader();
    reader.onload = function (e) {
        localStorage.setItem('athletePhoto', e.target.result);
    };
    reader.readAsDataURL(foto);
}

location.href = 'feedAtleta.html';
}

document.getElementById('editAthleteForm').addEventListener('submit', salvarDadosAtleta);

window.onload = carregarDadosAtleta;