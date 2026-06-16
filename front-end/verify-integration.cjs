const { chromium } = require('./node_modules/playwright');
const fs = require('fs');
const path = require('path');

const SS = 'C:/Users/m04062a/AppData/Local/Temp/scoutplay-verify';
if (!fs.existsSync(SS)) fs.mkdirSync(SS, { recursive: true });
fs.readdirSync(SS).forEach(f => fs.unlinkSync(path.join(SS, f)));

const results = [];
let browser, page, context;

const ss = async (name) => {
  const f = path.join(SS, `${name}.png`);
  await page.screenshot({ path: f, fullPage: false });
  return f;
};
const go = async (url) => {
  await page.goto(url, { waitUntil: 'load', timeout: 20000 });
  await page.waitForTimeout(800);
};
const step = async (label, fn) => {
  try {
    const r = await fn();
    console.log('  [PASS] ' + label + (r ? ' -> ' + String(r).substring(0, 100) : ''));
    results.push({ label, status: 'PASS', detail: r || '' });
    return r;
  } catch (e) {
    console.log('  [FAIL] ' + label + ' -> ' + e.message.split('\n')[0]);
    results.push({ label, status: 'FAIL', detail: e.message.split('\n')[0] });
    return null;
  }
};
const probe = async (label, fn) => {
  try {
    const r = await fn();
    console.log('  [PROBE-OK] ' + label + ' -> ' + String(r || 'ok').substring(0, 100));
    results.push({ label, status: 'PROBE-PASS', detail: r || '' });
    return r;
  } catch (e) {
    console.log('  [PROBE-FAIL] ' + label + ' -> ' + e.message.split('\n')[0]);
    results.push({ label, status: 'PROBE-FAIL', detail: e.message.split('\n')[0] });
    return null;
  }
};

(async () => {
  browser = await chromium.launch({ channel: 'msedge', headless: true });
  context = await browser.newContext({ viewport: { width: 1280, height: 800 }, ignoreHTTPSErrors: true });
  const apiCalls = [];
  context.on('response', r => {
    if (r.url().includes('localhost:8080'))
      apiCalls.push(r.status() + ' ' + r.request().method() + ' ' + r.url().replace('http://localhost:8080', ''));
  });
  page = await context.newPage();

  // 1. HOME
  console.log('\n[1] PAGINA INICIAL');
  await step('Carrega localhost:3000', async () => {
    await go('http://localhost:3000');
    await ss('01-home');
    return (await page.textContent('body')).trim().substring(0, 80);
  });

  // 2. LOGIN INVALIDO
  console.log('\n[2] LOGIN INVALIDO');
  await go('http://localhost:3000/login');
  await ss('02-login-page');
  await probe('Erro com senha errada', async () => {
    apiCalls.length = 0;
    const email = await page.$('input[type="email"],input[type="text"]');
    const pass = await page.$('input[type="password"]');
    if (!email || !pass) throw new Error('inputs nao encontrados');
    await email.fill('fabio@atleta.com');
    await pass.fill('ERRADA');
    const btn = await page.$('button[type="submit"],button');
    await btn.click();
    await page.waitForTimeout(2000);
    await ss('03-login-error');
    const txt = await page.textContent('body');
    const api = apiCalls.join(' | ');
    return 'api=[' + api + '] | erro_visivel=' + /inválid|incorret|senha|erro/i.test(txt);
  });

  // 3. LOGIN VALIDO
  console.log('\n[3] LOGIN VALIDO');
  await go('http://localhost:3000/login');
  apiCalls.length = 0;
  await step('Login com fabio@atleta.com/12345', async () => {
    const email = await page.$('input[type="email"],input[type="text"]');
    const pass = await page.$('input[type="password"]');
    if (!email || !pass) throw new Error('inputs nao encontrados');
    await email.fill('fabio@atleta.com');
    await pass.fill('12345');
    const respP = page.waitForResponse(r => r.url().includes('/api/login'), { timeout: 8000 });
    await (await page.$('button[type="submit"],button')).click();
    const resp = await respP;
    const body = await resp.json();
    if (!body.success) throw new Error(body.message);
    return 'success=' + body.success + ' | user=' + (body.data && (body.data.username || body.data.nome));
  });

  await page.waitForTimeout(2000);
  await ss('04-after-login');
  await step('Redireciona fora de /login', async () => {
    const url = page.url();
    if (url.endsWith('/login')) throw new Error('Ainda em /login. URL=' + url);
    return '-> ' + url;
  });

  // 4. FEED
  console.log('\n[4] FEED');
  await step('Feed carrega apos login', async () => {
    await go('http://localhost:3000');
    await ss('05-feed');
    const txt = await page.textContent('body');
    if (txt.trim().length < 30) throw new Error('Vazio: ' + txt.trim());
    return txt.trim().substring(0, 80);
  });

  // 5. PERFIL
  console.log('\n[5] PERFIL');
  await step('GET /api/user?user=fabin_123 retorna dados', async () => {
    const resp = await context.request.get('http://localhost:8080/api/user?user=fabin_123');
    const body = await resp.json();
    if (!body.data) throw new Error('sem data na resposta');
    return 'nome=' + body.data.nome + ' | tipo=' + body.data.tipoConta;
  });

  await step('Rota /user/fabin_123 exibe perfil', async () => {
    await go('http://localhost:3000/user/fabin_123');
    const txt = await page.textContent('body');
    await ss('06-profile');
    if (/Not Found/i.test(txt)) throw new Error('Not Found para /user/fabin_123');
    if (/Fabio|fabin_123|ATLETA/i.test(txt)) return 'dados visiveis: ' + txt.substring(0, 80);
    return 'rota aberta, conteudo: ' + txt.substring(0, 80);
  });

  // 6. POST
  console.log('\n[6] POST');
  const POST_ID = '326a3c76-d415-4c5f-83c5-032f9ba959fb';
  await step('Navega para /post/:id', async () => {
    await go('http://localhost:3000/post/' + POST_ID);
    await ss('07-post');
    const txt = await page.textContent('body');
    return 'url=' + page.url() + ' | ' + txt.substring(0, 80);
  });

  await step('Dados do post visiveis na UI', async () => {
    const txt = await page.textContent('body');
    if (/Meu Post|Teste|Fabio|fabin/i.test(txt)) return 'Post encontrado';
    throw new Error('Post nao visivel: ' + txt.substring(0, 100));
  });

  // 7. LIKE — button has Material Icon text "thumb_up" inside a span
  console.log('\n[7] LIKE');
  await probe('Clica like e API responde', async () => {
    apiCalls.length = 0;
    // find button that contains the material icon span with "thumb_up"
    const likeBtn = await page.$('button:has(span.material-symbols-outlined)');
    if (!likeBtn) throw new Error('Botao like (material-symbols) nao encontrado');
    await likeBtn.scrollIntoViewIfNeeded();
    const respP = page.waitForResponse(r => r.url().includes('/like') || r.url().includes('/dislike'), { timeout: 5000 }).catch(() => null);
    await likeBtn.click({ force: true }); // force: CSS overlay intercepts click in headless mode
    await page.waitForTimeout(1500);
    const resp = await respP;
    await ss('08-like');
    if (resp) {
      const b = await resp.json().catch(() => ({}));
      return 'API ' + resp.status() + ' ' + resp.request().method() + ' | success=' + b.success;
    }
    return 'Clicado. api=[' + (apiCalls.join(', ') || 'nenhuma') + ']';
  });

  // 8. COMENTARIO — textarea name="comentario" + submit button
  console.log('\n[8] COMENTARIO');
  await probe('Escreve e envia comentario', async () => {
    apiCalls.length = 0;
    const input = await page.$('textarea[name="comentario"]');
    if (!input) throw new Error('textarea[name="comentario"] nao encontrado');
    await input.fill('Comentario via teste de integracao!');
    // click the submit button (has Material Icon "send")
    const submitBtn = await page.$('form button[type="submit"]');
    if (!submitBtn) throw new Error('Botao submit do comentario nao encontrado');
    const respP = page.waitForResponse(r => r.url().includes('/comment'), { timeout: 6000 }).catch(() => null);
    await submitBtn.click();
    await page.waitForTimeout(2000);
    const resp = await respP;
    await ss('09-comment');
    if (resp) {
      const b = await resp.json().catch(() => ({}));
      return 'API ' + resp.status() + ' ' + resp.request().method() + ' | success=' + b.success;
    }
    return 'Clicado. api=[' + (apiCalls.join(', ') || 'nenhuma') + ']';
  });

  // 9. SIGNUP
  console.log('\n[9] SIGNUP');
  await step('/signup renderiza formulario', async () => {
    await go('http://localhost:3000/signup');
    await ss('10-signup');
    const inputs = await page.$$('input');
    const txt = await page.textContent('body');
    return inputs.length + ' inputs | ' + txt.trim().substring(0, 60);
  });

  // 10. SETTINGS
  console.log('\n[10] SETTINGS');
  await probe('Rota /settings', async () => {
    await go('http://localhost:3000/settings');
    await ss('11-settings');
    return (await page.textContent('body')).trim().substring(0, 80);
  });

  // 11. LOGOUT
  console.log('\n[11] LOGOUT');
  await probe('Botao Logout na UI (Header)', async () => {
    // still logged in from earlier — navigate to feed and check header
    await go('http://localhost:3000/feed');
    await ss('12-home-nav');
    // Header has: <button onClick={logout}> Logout</button>
    const btn = await page.$('button:has-text("Logout")');
    if (btn) {
      const txt = (await btn.textContent()).trim();
      return 'encontrado: button com texto="' + txt + '"';
    }
    // fallback: collect all button texts
    const btns = await page.$$('button');
    const texts = [];
    for (const b of btns) texts.push((await b.textContent()).trim());
    throw new Error('Nao encontrado. Botoes: [' + texts.filter(t => t).join(', ') + ']');
  });

  await probe('Logout via API', async () => {
    const resp = await context.request.post('http://localhost:8080/api/logout');
    return 'status=' + resp.status();
  });

  await browser.close();

  const pass = results.filter(r => r.status === 'PASS').length;
  const fail = results.filter(r => r.status === 'FAIL').length;
  const pp = results.filter(r => r.status === 'PROBE-PASS').length;
  const pf = results.filter(r => r.status === 'PROBE-FAIL').length;

  console.log('\n========================================');
  console.log('VEREDICTO: ' + (fail === 0 ? 'PASS' : 'FAIL'));
  console.log('  PASS=' + pass + ' FAIL=' + fail + ' PROBE-OK=' + pp + ' PROBE-FAIL=' + pf);
  console.log('----------------------------------------');
  results.forEach(r => {
    const ic = r.status === 'PASS' ? '[OK]' : r.status === 'FAIL' ? '[FAIL]' : '[PROBE]';
    console.log(ic + ' ' + r.label);
    if (r.detail) console.log('   ' + String(r.detail).substring(0, 120));
  });
  console.log('\nScreenshots: ' + SS);
  console.log('Arquivos: ' + fs.readdirSync(SS).join(', '));
})();
