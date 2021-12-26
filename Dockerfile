FROM theasp/clojurescript-nodejs:latest

WORKDIR /usr/src/app

COPY . .

RUN npm ci
RUN npm run build

EXPOSE 4000

CMD [ "node", "./resources/server/index.js" ]
