const postgres = require('pg');

const connectionString = 'postgres://lnivaupb:xvg82wOC6s-Tg7rouTqXxDuvyBTVbXxH@castor.db.elephantsql.com/lnivaupb';
let connectionData = {
    connectionString: connectionString
}
const client = new postgres.Client(connectionData);
client.connect();

const q = `SELECT * FROM "public"."company"`;

client.query(q, (err, res) => {
    if (err) {
        console.log(err);
        return;
    }
    for (let row of res.rows) {
        console.log(row);
    }
    client.end();
});
