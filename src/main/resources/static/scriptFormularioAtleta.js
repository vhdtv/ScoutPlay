function salvarDadosAtleta(event) {
    event.preventDefault(); // Impede o comportamento padrão do formulário

    // Cria um objeto FormData para enviar os dados
    const formData = new FormData();

    // Pega os valores do formulário e adiciona ao FormData
    formData.append('nome', document.getElementById('name').value);
    formData.append('telefone', document.getElementById('telefone').value);
    formData.append('cpf', document.getElementById('cpf').value);
    formData.append('dataNascimento', document.getElementById('data-nascimento').value);
    formData.append('cep', document.getElementById('cep').value);
    formData.append('email', document.getElementById('email').value);
    formData.append('senha', document.getElementById('senha').value);
    formData.append('peso', document.getElementById('weight').value);
    formData.append('altura', document.getElementById('height').value);
    formData.append('posicao', document.getElementById('position').value);
    formData.append('clubesAnteriores', document.getElementById('clubes').value);
    formData.append('peDominante', document.getElementById('pe-dominante').value);
    formData.append('video', document.getElementById('video').value);

    // Adiciona o arquivo da foto de perfil ao FormData
    const foto = document.getElementById('perfil-foto').files[0];
    if (foto) {
        formData.append('fotoPerfil', foto);
    }

    // Verifica se o checkbox foi marcado
    const maiorDe18 = document.getElementById('over18').checked;
    if (!maiorDe18) {
        alert("Você precisa confirmar que é maior de 18 anos.");
        return;
    }

    // Envia os dados via POST para o servidor
    fetch('/api/atletas', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (response.ok) {
                // Redireciona para a página de feed do atleta
                location.href = 'feedAtleta.html';
            } else {
                alert('Erro ao cadastrar atleta. Tente novamente.');
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            alert('Erro ao cadastrar atleta. Tente novamente.');
        });
}

// Adiciona o listener de submit ao formulário para salvar os dados e redirecionar
document.getElementById('athleteForm').addEventListener('submit', salvarDadosAtleta);