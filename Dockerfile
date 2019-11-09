FROM circleci/clojure:lein-2.7.1

RUN curl -sL https://deb.nodesource.com/setup_12.x | sudo -E bash -
RUN sudo apt-get install nodejs

COPY . .

RUN npm ci
RUN sudo npm run build

EXPOSE 4000

CMD [ "node", "resources/server/index.js" ]
