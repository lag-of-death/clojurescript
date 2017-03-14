To run:

1. `npm i`
2. `lein cljsbuild auto client`
3. `lein cljsbuild auto server`
4. `npm run start`

Go to `http://localhost:4000`.

Tests can be run with:
`lein doo phantom test once` or `npm test`