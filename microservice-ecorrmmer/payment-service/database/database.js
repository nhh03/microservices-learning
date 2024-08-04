const mongoose = require('mongoose');
mongoose.set('strictQuery', true);
require('dotenv').config();

async function connect() {
    console.log(process.env.MONGO_URI);
    try {
        let connection = await mongoose.connect(process.env.MONGO_URI, {
            useNewUrlParser: true,
            useUnifiedTopology: true,
        });
        console.log('Connect mongoose successfully');
        return connection;
    } catch (error) {
        console.log('Connect mongoose f1');
        const { code } = error;
        if (error.code == 8000) {
            console.log('Connect mongoose f2');
        } else if (code == 'ENOTFOUND') {
            console.log('Connect mongoose f3');
        }
    }
}

module.exports = connect;