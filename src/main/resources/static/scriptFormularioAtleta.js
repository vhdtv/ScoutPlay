
function redirecionarFormularioAtleta() {
    location.href = window.location.origin + '/src/main/resources/static/formularioAtleta.html';
}
// Função para salvar os dados do atleta no localStorage e redirecionar para a página do feed
function salvarDadosAtleta(event) {
    event.preventDefault(); // Impede o comportamento padrão do formulário

    // Pega os valores do formulário
    const nome = document.getElementById('name').value;
    const telefone = document.getElementById('telefone').value;
    const cpf = document.getElementById('cpf').value;
    const dataNascimento = document.getElementById('data-nascimento').value;
    const cep = document.getElementById('cep').value;
    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;
    const peso = document.getElementById('weight').value;
    const altura = document.getElementById('height').value;
    const posicao = document.getElementById('position').value;
    const clubes = document.getElementById('clubes').value;
    const peDominante = document.getElementById('pe-dominante').value;
    const video = document.getElementById('video').value;
    const foto = document.getElementById('perfil-foto').files[0]; // Arquivo de foto

    // Validação simples para confirmar que o checkbox foi marcado
    const maiorDe18 = document.getElementById('over18').checked;
    if (!maiorDe18) {
        alert("Você precisa confirmar que é maior de 18 anos.");
        return;
    }

    // Salva as informações no localStorage
    localStorage.setItem('athleteName', nome);
    localStorage.setItem('athletePhone', telefone);
    localStorage.setItem('athleteCpf', cpf);
    localStorage.setItem('athleteDob', dataNascimento);
    localStorage.setItem('athleteCep', cep);
    localStorage.setItem('athleteEmail', email);
    localStorage.setItem('athletePassword', senha);
    localStorage.setItem('athleteWeight', peso);
    localStorage.setItem('athleteHeight', altura);
    localStorage.setItem('athletePosition', posicao);
    localStorage.setItem('athleteClubs', clubes);
    localStorage.setItem('athleteDominantFoot', peDominante);
    localStorage.setItem('athleteVideo', video);

    // Salva a foto no localStorage, convertendo-a para uma string Base64
    if (foto) {
        const reader = new FileReader();
        reader.onload = function (e) {
            localStorage.setItem('athletePhoto', e.target.result); // Salva a foto como string Base64
        };
        reader.readAsDataURL(foto);
    }

    // Redireciona para a página de feed do atleta
    location.href = 'feedAtleta.html';
}

// Adiciona o listener de submit ao formulário para salvar os dados e redirecionar
document.getElementById('athleteForm').addEventListener('submit', salvarDadosAtleta);
