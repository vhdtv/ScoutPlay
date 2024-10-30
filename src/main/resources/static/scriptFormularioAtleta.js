async function handleAtletaForm(event) {
    event.preventDefault();

    // Criação do objeto JSON com os dados do atleta
    const atleta = {
        nome: document.getElementById('name').value,
        telefone: document.getElementById('telefone').value,
        cpf: document.getElementById('cpf').value,
        dataNascimento: document.getElementById('data-nascimento').value,
        cep: document.getElementById('cep').value,
        email: document.getElementById('email').value,
        senha: document.getElementById('senha').value,
        peso: document.getElementById('weight').value,
        altura: document.getElementById('height').value,
        posicao: document.getElementById('position').value,
        clubesAnteriores: document.getElementById('clubes').value,
        peDominante: document.getElementById('pe-dominante').value,
        video: document.getElementById('video').value,
    };

    // Criação do FormData e adição do JSON como 'atleta'
    const formData = new FormData();
    formData.append('atleta', new Blob([JSON.stringify(atleta)], { type: "application/json" }));

    // Adiciona a foto de perfil ao FormData
    const fotoPerfil = document.getElementById('perfil-foto').files[0]; // Corrigido o ID aqui
    formData.append('fotoPerfil', fotoPerfil);

    try {
        const response = await fetch("http://localhost:8080/api/atletas", {
            method: "POST",
            body: formData
        });

        // Verifica se a resposta foi bem-sucedida e exibe alertas
        if (response.ok) {
            alert("Cadastro de atleta realizado com sucesso!");
            location.href = '/feedAtleta.html';
        } else {
            const errorText = await response.text();
            console.error("Erro na resposta:", errorText);
            alert("Erro no cadastro do atleta.");
        }
    } catch (error) {
        console.error("Erro na requisição:", error);
        alert("Erro na requisição.");
    }
}

// Adiciona o listener para o evento de envio do formulário
document.getElementById('athleteForm').addEventListener('submit', handleAtletaForm);
