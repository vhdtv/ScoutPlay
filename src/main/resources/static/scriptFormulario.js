function toggleCpfField(show) {
    const cpfField = document.getElementById('cpfField');
    if (show) {
        cpfField.style.display = 'block';
    } else {
        cpfField.style.display = 'none';
        document.getElementById('cpf').value = ''; // Limpa o campo CPF caso seja ocultado
    }
}

function redirecionarFormularioAtleta() {
    const ageSelection = document.querySelector('input[name="age"]:checked').value;
    const cpfInput = document.getElementById('cpf').value;

    if (ageSelection === "maior") {
        // Redireciona para o formulário do atleta maior de idade
        location.href = window.location.origin + '/formularioAtletaMaiorDeIdade.html';
    } else {
        // Se for menor de idade, valida o CPF antes de prosseguir
        if (cpfInput.match(/^\d{3}\.\d{3}\.\d{3}-\d{2}$/)) {
            // Salva o CPF do responsável no backend e redireciona para o formulário de menor de idade
            salvarResponsavel(cpfInput);
        } else {
            alert("Por favor, insira um CPF válido.");
        }
    }
}

function salvarResponsavel(cpf) {
    fetch('/api/responsaveis', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ cpf: cpf }),
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text); });
            }
            return response.json();
        })
        .then(data => {
            console.log('Responsável salvo:', data);
            location.href = window.location.origin + '/formularioAtletaMenorDeIdade.html';
        })
        .catch((error) => {
            console.error('Erro ao salvar o responsável:', error);
            alert('Erro ao salvar o responsável: ' + error.message);
        });
}
