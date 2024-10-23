
//Função para exibir o menu ao clicar na engrenagem
document.querySelector('.edit-button').addEventListener('click', function(e) {
    e.preventDefault();
    const gearMenu = document.getElementById('gearMenu');
    gearMenu.style.display = (gearMenu.style.display == 'block') ? 'none' : 'block';
});
//Fecha o menu ao clicar fora dele
document.addEventListener('click', function(event){
    const gearMenu = document.getElementById('gearMenu');
    const editButton = document.querySelector('.edit-button');
    //Verifica se o clique foi fora do menu ou engrenagem
    if (!gearMenu.contains(event.target) && !editButton.contains(event.target)) {
        gearMenu.style.display = 'none';
    }
});

// Função para calcular a idade a partir da data de nascimento
function calcularIdade(dataNascimento) {
    // Cria um objeto Date com a data atual (o dia em que o código está sendo executado)
    const hoje = new Date();
    
    // Cria um objeto Date a partir da data de nascimento fornecida
    const nascimento = new Date(dataNascimento);
    
    // Calcula a idade subtraindo o ano de nascimento do ano atual
    let idade = hoje.getFullYear() - nascimento.getFullYear();
    
    // Calcula a diferença de meses entre a data atual e a data de nascimento
    const mes = hoje.getMonth() - nascimento.getMonth();
    
    // Verifica se o mês atual é anterior ao mês de nascimento ou se é o mesmo mês,
    // mas o dia atual ainda não chegou ao dia de aniversário. Se for, subtrai um ano da idade.
    if (mes < 0 || (mes === 0 && hoje.getDate() < nascimento.getDate())) {
        idade--;
    }
    
    // Retorna a idade final calculada
    return idade;
}

// Função para carregar e exibir as informações do atleta
function carregarPerfilAtleta() {
    // Simulando a recuperação de dados de localStorage ou de uma fonte externa
    const atleta = {
        nome: localStorage.getItem('athleteName') || 'João da Silva',                // Nome do atleta
        dataNascimento: localStorage.getItem('athleteDob') || '2005-04-15',           // Data de nascimento
        peso: localStorage.getItem('athleteWeight') || 70,                            // Peso em kg
        altura: localStorage.getItem('athleteHeight') || 180,                         // Altura em cm
        posicao: localStorage.getItem('athletePosition') || 'Atacante',               // Posição em campo
        foto: localStorage.getItem('athletePhoto') || 'https://via.placeholder.com/180',          // Foto de perfil padrão ou inserida
        peDominante: localStorage.getItem('athleteDominantFoot') || 'Destro',         // Pé dominante
        clubesAnteriores: localStorage.getItem('athleteClubs') || 'Sem clubes',       // Clubes anteriores
        linkVideo: localStorage.getItem('athleteVideoLink') || 'Assistir ao DVD' // Link do vídeo (DVD)
    };
 

    // Preenchendo os dados no HTML
    document.getElementById('profilePic').src = atleta.foto;                            // Exibe a foto de perfil
    document.getElementById('athleteName').textContent = atleta.nome;                   // Exibe o nome do atleta
    document.getElementById('athleteAge').textContent = calcularIdade(atleta.dataNascimento);  // Calcula e exibe a idade
    document.getElementById('athleteWeight').textContent = atleta.peso + " kg";         // Exibe o peso
    document.getElementById('athleteHeight').textContent = atleta.altura + " cm";       // Exibe a altura
    document.getElementById('athletePosition').textContent = atleta.posicao;            // Exibe a posição em campo
    document.getElementById('athleteDominantFoot').textContent = atleta.peDominante;    // Exibe o pé dominante
    document.getElementById('athleteClubs').textContent = atleta.clubesAnteriores;      // Exibe os clubes anteriores
    document.getElementById('athleteVideoLink').textContent = atleta.linkVideo;         // Exibe o link do vídeo
    document.getElementById('athleteVideoLink').href = atleta.linkVideo;                // Adiciona o link clicável ao vídeo
}

// Carregar o perfil do atleta quando a página for carregada
window.onload = carregarPerfilAtleta;
