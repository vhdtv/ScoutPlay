// Função para adicionar mais campos de link de vídeo dinamicamente
function addVideoLink() {
    const container = document.getElementById("videoLinksContainer");
    if (container) {  // Verifique se o container existe
        const input = document.createElement("input");
        input.type = "url";
        input.name = "videos[]";
        input.placeholder = "Coloque o link do seu DVD";
        container.appendChild(input);
    } else {
        console.error("O elemento videoLinksContainer não foi encontrado.");
    }
}

// Função para lidar com o envio do formulário do atleta
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
        videos: Array.from(document.querySelectorAll("input[name='videos[]']")).map(url => url.value).filter(url => url !== "")
    };

    const formData = new FormData();
    formData.append('atleta', new Blob([JSON.stringify(atleta)], { type: "application/json" }));

    const fotoPerfil = document.getElementById('perfil-foto').files[0];
    if (fotoPerfil) {
        formData.append('fotoPerfil', fotoPerfil);
    }

    // Envia o formulário
    try {
        const response = await fetch("http://localhost:8080/api/atletas", {
            method: "POST",
            body: formData
        });
        if (response.ok) {
            alert("Cadastro de atleta realizado com sucesso!");
            location.href = '/login.html';
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
