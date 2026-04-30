function salvarSessao(sessao) {
    localStorage.setItem('authToken', sessao.token);
    localStorage.setItem('userId', sessao.userId);
    localStorage.setItem('userType', sessao.userType);
    localStorage.setItem('userName', sessao.nome || '');
    localStorage.setItem('userEmail', sessao.email || '');
}

// Função que lida com a submissão do formulário do olheiro
function handleOlheiroForm(event) {
    event.preventDefault(); // Evita o comportamento padrão de recarregar a página

    // Coletar os dados do formulário
    const nome = document.getElementById('name').value;
    const telefone = document.getElementById('telefone').value;
    const cpf = document.getElementById('cpf').value;
    const dataNascimento = document.getElementById('data-nascimento').value;
    const cep = document.getElementById('cep').value;
    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;
    const clube = document.getElementById('club').value;
    const local = document.getElementById('location').value;

    // Validação simples para garantir que todos os campos estejam preenchidos
    if (nome && telefone && cpf && dataNascimento && cep && email && senha && clube && local) {
        // Criar um objeto com os dados do formulário
        const olheiroData = {
            nome: nome,
            telefone: telefone,
            cpf: cpf,
            dataNascimento: dataNascimento,
            cep: cep,
            email: email,
            senha: senha,
            clube: clube,
            local: local
        };

        // Fazer uma requisição POST para enviar os dados ao servidor
        fetch('/api/olheiros/registro', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(olheiroData),
        })
        .then(response => {
            return response.json().then(payload => ({ ok: response.ok, payload }));
        })
        .then(({ ok, payload }) => {
            if (!ok || !payload.success) {
                throw new Error(payload.message || 'Erro ao cadastrar o olheiro.');
            }

            salvarSessao(payload.data);
            // Sucesso no cadastro, redirecionar para a página feedOlheiro.html
            alert('Cadastro realizado com sucesso!');
            location.href = '/feedOlheiro.html'; // Redirecionar para a página de feed
        })
        .catch(error => {
            console.error('Erro:', error);
            alert('Erro ao cadastrar olheiro. Tente novamente.');
        });
    } else {
        alert('Por favor, preencha todos os campos.');
    }
}

// Adiciona o listener ao formulário para chamar a função quando for submetido
document.getElementById('scoutForm').addEventListener('submit', handleOlheiroForm);
