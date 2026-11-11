FROM node:18-alpine

WORKDIR /app

COPY package.json ./
COPY backends/fish/package.json ./backends/fish/
COPY backends/flavor/package.json ./backends/flavor/
COPY backends/threshold/package.json ./backends/threshold/

RUN npm install --omit=dev --no-audit --no-fund \
 && npm install --prefix backends/fish --omit=dev --no-audit --no-fund \
 && npm install --prefix backends/flavor --omit=dev --no-audit --no-fund \
 && npm install --prefix backends/threshold --omit=dev --no-audit --no-fund

COPY . .

ENV PORT=3020
EXPOSE 3020

CMD ["node", "src/index.js"]
