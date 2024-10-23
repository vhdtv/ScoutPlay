function redirecionarFormularioAtleta() {
    const ageSelection = document.querySelector('input[name="age"]:checked').value;
    const cpfInput = document.getElementById('cpf').value;

    if (ageSelection === "maior") {
        // Redireciona para o formulário do atleta se for maior de idade
        location.href = window.location.origin + '/src/main/resources/static/formularioAtleta.html';
    } else {
        // Se for menor de idade, valida o CPF antes de prosseguir
        if (cpfInput.match(/^\d{3}\.\d{3}\.\d{3}-\d{2}$/)) {
            // Salva o CPF do responsável no backend e redireciona
            salvarResponsavel(cpfInput);
        } else {
            alert("Por favor, insira um CPF válido.");
        }
    }
}

function salvarResponsavel(cpf) {
    // Envia o CPF para o backend via requisição POST
    fetch('/api/responsaveis', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ cpf: cpf }),
    })
        .then(response => response.json())
        .then(data => {
            console.log('Responsável salvo:', data);
            // Após salvar o responsável, redireciona para o formulário do atleta
            location.href = window.location.origin + '/src/main/resources/static/formularioAtleta.html';
        })
        .catch((error) => {
            console.error('Erro ao salvar o responsável:', error);
        });
}