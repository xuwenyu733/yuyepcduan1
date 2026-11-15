// Serve the compiled local build with the same API routes as the development server.
const path = require('node:path');
const fs = require('node:fs');
const http = require('node:http');
const express = require('../services/modules-api/node_modules/express');
const httpProxy = require('http-proxy');

const dist = path.resolve(__dirname, '../dist');
if (!fs.existsSync(path.join(dist, 'index.html'))) {
  throw new Error('Local build is missing. Run build-local.ps1 first.');
}
const routes = [
  { prefix: '/proxy-api', target: 'http://127.0.0.1:48181', strip: true },
  { prefix: '/modules-api', target: 'http://127.0.0.1:3021', strip: true },
  { prefix: '/llm', target: 'http://127.0.0.1:5190' },
  { prefix: '/api', target: 'http://127.0.0.1:8787' },
  { prefix: '/wiidu-datainf', target: 'http://127.0.0.1:8088/zx/datainf', strip: true },
  { prefix: '/wiidu', target: 'http://127.0.0.1:8088', strip: true },
  { prefix: '/PMIS', target: 'http://127.0.0.1:8088' },
  { prefix: '/list', target: 'http://127.0.0.1:8088/zx' },
  { prefix: '/zx', target: 'http://127.0.0.1:8088' }
];
function findRoute(url) {
  const pathname = url.split('?')[0];
  return routes.find(route => pathname === route.prefix || pathname.startsWith(route.prefix + '/'));
}
function rewrite(req, route) {
  if (route.strip) req.url = req.url.slice(route.prefix.length) || '/';
}
const proxy = httpProxy.createProxyServer({ changeOrigin: true, xfwd: true, proxyTimeout: 180000 });
proxy.on('error', (error, req, res) => {
  console.error('[local-proxy]', error.code || error.message, req.url);
  if (typeof res.writeHead === 'function' && !res.headersSent) {
    res.writeHead(502, { 'Content-Type': 'application/json; charset=utf-8' });
    res.end(JSON.stringify({ code: 502, message: 'Local upstream service is unavailable' }));
  } else if (typeof res.destroy === 'function') {
    res.destroy();
  }
});
const app = express();
app.disable('x-powered-by');
app.use((req, res, next) => {
  const route = findRoute(req.url);
  if (!route) return next();
  rewrite(req, route);
  proxy.web(req, res, { target: route.target });
});
app.get('/__local_run', (req, res) => res.json({
  version: '887361b', copy: path.basename(path.resolve(__dirname, '../..')),
  features: ['smart-entry-images', 'database-web-research'], mode: 'compiled-local', port: 4985, modulesPort: 3021
}));
app.use(express.static(dist, { setHeaders(res, filename) {
  if (filename.endsWith('.html')) res.setHeader('Cache-Control', 'no-store');
} }));
app.use((req, res) => res.status(404).send('Not found'));
const server = http.createServer(app);
server.on('upgrade', (req, socket, head) => {
  const route = findRoute(req.url);
  if (!route) return socket.destroy();
  rewrite(req, route);
  proxy.ws(req, socket, head, { target: route.target });
});
server.listen(4985, '127.0.0.1', () => console.log('New yuye copy: http://localhost:4985'));
