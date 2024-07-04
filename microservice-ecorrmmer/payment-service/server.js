const express = require('express');
const app = express()
require('dotenv').config()

const bodyParser = require('body-parser');
const cors = require('cors');
const connect = require('./database/database.js');
const eurekaHelper = require('./discovery/eureka-helper.js');

const paymentRouter = require('./routers/Payment.js');
const orderRouter = require('./routers/Vnpay.js');

const paymentService = require('./services/Payment.js');    

const consumer = require('./kafka/consumer.js')

app.use(bodyParser.json());
const port = process.env.PORT || 5000
app.use(cors({ origin: '*' }));
app.use('/api/v1/payments', paymentRouter);
app.use('/api/v1/payments', orderRouter);

app.listen(port, async() => {
    await connect()
    console.log(`listening on portt : ${port}`)
})

eurekaHelper.registerWithEureka('payment-service', port);

// const topic = process.env.KAFKA_TOPIC
// console.log('topic', topic)
async function main(){
    try {
        await consumer.connect()
        await consumer.subscribe({
            topic :  'payment-cod',
            fromBeginning: true,
        })
        console.log('Connect kafka consumer successfully')

        await consumer.run({
            eachMessage: async ({ topic, partition, message }) => {
                console.log('Received message', {
                    topic,
                    partition,
                    offset: message.offset,
                    value: message.value.toString()
                })
                let data = message.value.toString()
                const payment = await paymentService.createPayment( data, 'COD', 'PENDING', '','');
                console.log('Payment created :', payment)
            }
        })
    } catch (error) {
        throw new Error(error)
    }

}

main().catch(console.error);    // Call the main function and log any errors

