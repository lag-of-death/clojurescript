FROM theasp/clojurescript-nodejs:latest

COPY . .

RUN npm ci --production
RUN npm run build

EXPOSE 4000

CMD [ "node", "resources/server/index.js" ]
